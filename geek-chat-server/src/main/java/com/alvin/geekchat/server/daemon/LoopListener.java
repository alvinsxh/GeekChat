package com.alvin.geekchat.server.daemon;

import com.alvin.geekchat.server.component.RequestHandler;
import com.alvin.geekchat.server.component.TaskComponent;
import com.alvin.geekchat.server.model.Constants;
import com.alvin.geekchat.server.util.GlobalUtil;
import com.alvin.geekchat.server.model.OnlineUser;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

import static com.alvin.geekchat.model.RequestHeader.*;
import static com.alvin.geekchat.model.RequestHeader.MESSAGE;

public class LoopListener implements Runnable {
    private RequestHandler requestHandler;
    private TaskComponent taskComponent;

    public LoopListener(ApplicationContext context) {
        this.requestHandler = context.getBean(RequestHandler.class);
        this.taskComponent = context.getBean(TaskComponent.class);
    }

    @Override
    public void run() {
        OnlineUser onlineUser;
        Socket s;
        int head;
        Object[] userIDs = new Object[0];
        int userID, i;
        while (true) {
            GlobalUtil.lock();
            if (!GlobalUtil.ONLINE_USER_COLLECTION.isEmpty()) {
                userIDs = GlobalUtil.ONLINE_USER_COLLECTION.keySet().toArray();
            }
            GlobalUtil.unlock();

            for (i = 0; i < userIDs.length; i++) {
                head = -2;
                userID = (int) userIDs[i];
                if ((onlineUser = GlobalUtil.ONLINE_USER_COLLECTION.get(userID)) == null) {
                    continue;
                }
                if (!onlineUser.isTaskFinish()) {
                    continue;
                }
                s = onlineUser.getS();
                try {
                    s.setSoTimeout(Constants.READ_WAIT);
                    head = s.getInputStream().read();
                    s.setSoTimeout(Integer.MAX_VALUE);
                } catch (IOException e) {
                    if (!(e instanceof SocketTimeoutException)) {
                        head = 0;
                        e.printStackTrace();
                    }
                }
                if (head == 0 || head == -1) {
                    GlobalUtil.onlineStatusRemove(userID);
                } else if (head == -2) {
                    continue;
                } else {
                    switch ((char) head) {
                        case CON_SERVER: {
                            onlineUser.setTaskFinish(false);
                            taskComponent.submitRequestHandleTask(currrentUser -> {
                                requestHandler.handleCon(currrentUser);
                            }, onlineUser);
                            break;
                        }
                        case DISCON_SERVER: {
                            onlineUser.setTaskFinish(false);
                            taskComponent.submitRequestHandleTask(currrentUser -> {
                                requestHandler.handleDiscon(currrentUser);
                            }, onlineUser);
                            break;
                        }
                        case CLIENT_ONLINE: {
                            onlineUser.setTaskFinish(false);
                            taskComponent.submitRequestHandleTask(currrentUser -> {
                                requestHandler.handleReady(currrentUser);
                            }, onlineUser);
                            break;
                        }
                        case MESSAGE: {
                            onlineUser.setTaskFinish(false);
                            taskComponent.submitRequestHandleTask(currrentUser -> {
                                requestHandler.handleSend(currrentUser);
                            }, onlineUser);
                            break;
                        }
                    }
                }
            }
            try {
                Thread.sleep(Constants.LISTEN_INTERVAL);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}

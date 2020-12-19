package com.alvin.geekchat.server.daemon;

import com.alvin.geekchat.server.component.RequestHandler;
import com.alvin.geekchat.server.component.TaskComponent;
import com.alvin.geekchat.server.model.Constants;
import com.alvin.geekchat.server.model.OnlineUser;
import org.springframework.context.ApplicationContext;

import java.net.ServerSocket;
import java.net.Socket;

import static com.alvin.geekchat.model.RequestHeader.CON_SERVER;

public class MainListener implements Runnable {
    private RequestHandler requestHandler;
    private TaskComponent taskComponent;

    public MainListener(ApplicationContext context) {
        this.requestHandler = context.getBean(RequestHandler.class);
        this.taskComponent = context.getBean(TaskComponent.class);
    }

    @Override
    public void run() {
        try(ServerSocket serverGet = new ServerSocket(Constants.SERVER_PORT);) {
            while (true) {
                Socket s = serverGet.accept();
                char c = (char) s.getInputStream().read();
                switch (c) {
                    case CON_SERVER: {
                        taskComponent.submitRequestHandleTask(currentUser -> {
                            requestHandler.handleCon(currentUser);
                        }, new OnlineUser(s));
                        break;
                    } default: {
                        s.close();
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

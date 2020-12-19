package com.alvin.geekchat.server.component;

import com.alvin.geekchat.model.EncryptedPack;
import com.alvin.geekchat.model.User;
import com.alvin.geekchat.server.model.OnlineUser;
import com.alvin.geekchat.server.util.GlobalUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.*;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

import static com.alvin.geekchat.model.RequestHeader.*;

@Component
public class RequestHandler {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    @Resource
    UserComponent userComponent;

    public void handleCon(OnlineUser currentUser) {
        User user = null;
        int userID = 0;
        try {
            char[] IDChar = new char[ID_LEN];
            char[] passwordChar = new char[32];
            Reader r = new InputStreamReader(currentUser.getS().getInputStream());
            r.read(IDChar);
            r.read(passwordChar);
            userID = Integer.valueOf(new String(IDChar));
            user = userComponent.getUser(userID, new String(passwordChar));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            PrintStream ps = new PrintStream(currentUser.getS().getOutputStream());
            ps.print(USER_INFO);
            if (user == null) {
                ps.print(USER_INFO_ERROR);
            } else {
                if (GlobalUtil.ONLINE_USER_COLLECTION.containsKey(userID)) {
                    ps.print(USER_INFO_REPEAT);
                    return;
                }
                ps.print(USER_INFO_SUCCESS);

                List<User> friends = userComponent.queryRelatedUsers(userID);
                ObjectOutputStream oos = new ObjectOutputStream(currentUser.getS().getOutputStream());
                oos.writeObject(user);
                oos.writeObject(friends.toArray(new User[]{}));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        currentUser.setTaskFinish(true);
        if (user != null) {
            GlobalUtil.lock();
            currentUser.setLastReportTime(System.currentTimeMillis());
            GlobalUtil.ONLINE_USER_COLLECTION.put(userID, currentUser);
            GlobalUtil.unlock();
            logger.info("New client log in, userId={}", userID);
        }
    }

    public void handleDiscon(OnlineUser currentUser) {
        try {
            char[] IDChar = new char[ID_LEN];
            Reader r = new InputStreamReader(currentUser.getS().getInputStream());
            r.read(IDChar);
            int userID = Integer.valueOf(new String(IDChar));
            GlobalUtil.onlineStatusRemove(userID);
            logger.info("Client offline, userId={}", userID);
        } catch (IOException e) {
            e.printStackTrace();
        }
        currentUser.setTaskFinish(true);
    }

    public void handleReady(OnlineUser currentUser) {
        int userID = 0;
        try {
            char[] IDChar = new char[ID_LEN];
            Reader r = new InputStreamReader(currentUser.getS().getInputStream());
            r.read(IDChar);
            userID = Integer.valueOf(new String(IDChar));
            onlineStatusUpdate(userID);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (userID != 0 && GlobalUtil.UNSEND_PACK.containsKey(userID)) {
            EncryptedPack pack;
            LinkedList<EncryptedPack> packList = GlobalUtil.UNSEND_PACK.get(userID);
            while ((pack = packList.peek()) != null) {
                if (sendPack(pack)) {
                    packList.poll();
                } else {
                    break;
                }
            }
            if (packList.size() == 0) {
                GlobalUtil.UNSEND_PACK.remove(userID);
            }
        }
        currentUser.setTaskFinish(true);
    }

    public void handleSend(OnlineUser currentUser) {
        EncryptedPack pack = null;
        try {
            ObjectInputStream ois = new ObjectInputStream(currentUser.getS().getInputStream());
            pack = (EncryptedPack) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (pack != null) {
            if (!sendPack(pack)) {
                addUnSend(pack);
                GlobalUtil.onlineStatusRemove(pack.to.getId());
            }
        }
        currentUser.setTaskFinish(true);
    }

    private void onlineStatusUpdate(int userID) {
        GlobalUtil.lock();
        long now = System.currentTimeMillis();
        OnlineUser onlineUser = GlobalUtil.ONLINE_USER_COLLECTION.get(userID);
        if (onlineUser != null && onlineUser.getLastReportTime() < now) {
            onlineUser.setLastReportTime(now);
        }
        GlobalUtil.unlock();
    }

    private void addUnSend(EncryptedPack pack) {
        if (!GlobalUtil.UNSEND_PACK.containsKey(pack.to.getId())) {
            GlobalUtil.UNSEND_PACK.put(pack.to.getId(), new LinkedList<EncryptedPack>());
        }
        GlobalUtil.UNSEND_PACK.get(pack.to.getId()).add(pack);
    }

    private boolean sendPack(EncryptedPack pack) {
        boolean sent = false;
        if (GlobalUtil.ONLINE_USER_COLLECTION.containsKey(pack.to.getId())) {
            try {
                Socket s = GlobalUtil.ONLINE_USER_COLLECTION.get(pack.to.getId()).getS();
                s.getOutputStream().write(MESSAGE);
                ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
                oos.writeObject(pack);
                sent = true;
            } catch (IOException e) {
            }
        }
        return sent;
    }
}

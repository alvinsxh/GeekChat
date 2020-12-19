package com.alvin.geekchat.server.util;

import com.alvin.geekchat.model.EncryptedPack;
import com.alvin.geekchat.server.model.OnlineUser;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class GlobalUtil {
    private static ReentrantLock GLOBAL_LOCK = new ReentrantLock(true);
    public static Map<Integer, OnlineUser> ONLINE_USER_COLLECTION = new HashMap<>();
    public static Map<Integer, LinkedList<EncryptedPack>> UNSEND_PACK = new Hashtable<>();
    public static ApplicationContext context;

    public static void lock() {
        GLOBAL_LOCK.lock();
    }

    public static void unlock() {
        GLOBAL_LOCK.unlock();
    }

    public static void onlineStatusRemove(int userID) {
        GLOBAL_LOCK.lock();
        if (ONLINE_USER_COLLECTION.containsKey(userID)) {
            try {
                ONLINE_USER_COLLECTION.get(userID).getS().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ONLINE_USER_COLLECTION.remove(userID);
        GLOBAL_LOCK.unlock();
    }
}

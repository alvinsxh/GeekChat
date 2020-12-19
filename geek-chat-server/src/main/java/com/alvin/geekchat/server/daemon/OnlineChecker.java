package com.alvin.geekchat.server.daemon;

import com.alvin.geekchat.server.model.Constants;
import com.alvin.geekchat.server.util.GlobalUtil;
import com.alvin.geekchat.server.model.OnlineUser;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

public class OnlineChecker implements Runnable {
    @Override
    public void run() {
        while (true) {
            GlobalUtil.lock();
            long now = System.currentTimeMillis();

            Iterator<HashMap.Entry<Integer, OnlineUser>> it = GlobalUtil.ONLINE_USER_COLLECTION.entrySet().iterator();
            HashMap.Entry<Integer, OnlineUser> entry;
            while(it.hasNext()) {
                entry = it.next();
                if (now - entry.getValue().getLastReportTime() > Constants.MAX_NO_RESPONSE_MILLIS) {
                    try {
                        GlobalUtil.ONLINE_USER_COLLECTION.get(entry.getKey()).getS().close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    it.remove();
                }
            }

				/*
				LinkedList<Integer> removeList = new LinkedList<Integer>();
				for (int userID : onlineUserTime.keySet()) {
					if (now - onlineUserTime.get(userID) > maxNoResponseMillis) {
						removeList.add(userID);
					}
				}
				for (int userID : removeList) {
					onlineStatusRemove(userID);
				}
				*/

            GlobalUtil.unlock();
            try {
                Thread.sleep(Constants.CHECK_ONLINE_INTERVAL);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

package com.alvin.geekchat.server.model;

import java.net.Socket;

public class OnlineUser {
    private Socket s;
    private long lastReportTime;
    private volatile boolean taskFinish = true;

    public OnlineUser(Socket s) {
        this.s = s;
    }

    public Socket getS() {
        return s;
    }

    public void setS(Socket s) {
        this.s = s;
    }

    public long getLastReportTime() {
        return lastReportTime;
    }

    public void setLastReportTime(long lastReportTime) {
        this.lastReportTime = lastReportTime;
    }

    public boolean isTaskFinish() {
        return taskFinish;
    }

    public void setTaskFinish(boolean taskFinish) {
        this.taskFinish = taskFinish;
    }
}

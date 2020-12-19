package com.alvin.geekchat.server.util;

public class ContextUtil {
    private static ThreadLocal<String> traceId = new ThreadLocal<>();
    private static ThreadLocal<Integer> entryCount = new ThreadLocal<>();

    public static void setTraceId(String traceId) {
        ContextUtil.traceId.set(traceId);
    }

    public static String getTraceId() {
        if (ContextUtil.traceId.get() == null) {
            ContextUtil.traceId.set(UuidUtil.generateRandomTraceId());
        }
        return ContextUtil.traceId.get();
    }

    public static void entry() {
        if (ContextUtil.entryCount.get() == null) {
            ContextUtil.entryCount.set(0);
        }
        ContextUtil.entryCount.set(ContextUtil.entryCount.get() + 1);
    }

    public static void exit() {
        ContextUtil.entryCount.set(ContextUtil.entryCount.get() - 1);
        if (ContextUtil.entryCount.get().equals(0)) {
            traceId.remove();
        }
    }
}

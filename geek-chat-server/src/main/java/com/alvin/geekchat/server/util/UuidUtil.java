package com.alvin.geekchat.server.util;

import org.apache.commons.lang3.RandomStringUtils;

public class UuidUtil {
    public static String generateRandomTraceId() {
        return RandomStringUtils.random(20, "0123456789abcdefghigklmnopqrstuvwxyz");
    }
}

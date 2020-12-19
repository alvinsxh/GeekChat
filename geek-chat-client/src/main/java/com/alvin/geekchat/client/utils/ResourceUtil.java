package com.alvin.geekchat.client.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.File;

public class ResourceUtil {
    private static final String parentPath = ResourceUtil.class.getResource("/").getPath();

    public static String concatResourcesPath(String subPath) {
        if (StringUtils.isBlank(subPath)) {
            return parentPath;
        } else {
            return parentPath + subPath;
        }
    }
}

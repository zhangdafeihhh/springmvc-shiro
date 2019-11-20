package com.zhuanche.util;

import org.apache.commons.lang3.StringUtils;

/**
 * @author admin
 */
public class MobileOverlayUtil {
    private static final String OVERLAY = "****";
    private static final int START = 3;
    private static final int END = 7;

    public static String doOverlayPhone(String mobile) {
        if (StringUtils.isNotBlank(mobile)) {
            return StringUtils.overlay(mobile, OVERLAY, START, END);
        }
        return "";
    }
}

package com.bootdo.meituan.banma.util;

import java.util.Date;

/**
 * 时间戳处理类
 */
public class DateUtil {

    /**
     * 返回当前时间的秒数
     *
     * @return
     */
    public static int unixTime() {
        return (int) (System.currentTimeMillis() / 1000);
    }

    /**
     * 把表转换为Date
     *
     * @param seconds
     * @return
     */
    public static Date fromUnixTime(Integer seconds) {
        return new Date(seconds * 1000L);
    }

    public static Date fromUnixTime(Long seconds) {
        return new Date(seconds * 1000L);
    }
}

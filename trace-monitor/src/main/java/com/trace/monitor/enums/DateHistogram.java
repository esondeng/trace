package com.trace.monitor.enums;

import java.util.Arrays;

import com.eson.common.core.enums.EnumBase;
import com.eson.common.core.util.Assert;
import com.eson.common.core.util.TimeUtils;
import com.trace.monitor.constants.EsConstants;

import lombok.Getter;

/**
 * ES时间统计类型
 *
 * @author dengxiaolin
 * @since 2021/03/04
 */
public enum DateHistogram implements EnumBase {
    /**
     * 时间间隔类型
     */
    INTERVAL_DAY(0, 10 * 24 * 3600, Long.MAX_VALUE, EsConstants.INTERVAL_DAY, TimeUtils.DATE, "每天"),
    INTERVAL_12_HOUR(1, 5 * 24 * 3600, 10 * 24 * 3600, "12h", TimeUtils.DATE_HOUR, "每12小时"),
    INTERVAL_6_HOUR(2, 2 * 24 * 3600, 5 * 24 * 3600, "6h", TimeUtils.DATE_HOUR, "每6小时"),
    INTERVAL_2_HOUR(3, 24 * 3600, 2 * 24 * 3600, "2h", TimeUtils.DATE_HOUR, "每2小时"),
    INTERVAL_HOUR(4, 10 * 3600, 24 * 3600, EsConstants.INTERVAL_HOUR, TimeUtils.DATE_HOUR, "每小时"),
    INTERVAL_30_MINUTE(5, 5 * 3600, 10 * 3600, "30m", TimeUtils.DATE_MINUTE, "每30分钟"),
    INTERVAL_15_MINUTE(6, 2 * 3600, 5 * 3600, "15m", TimeUtils.DATE_MINUTE, "每15分钟"),
    INTERVAL_10_MINUTE(7, 3600, 2 * 3600, "10m", TimeUtils.DATE_MINUTE, "每10分钟"),
    INTERVAL_5_MINUTE(8, 1800, 3600, "5m", TimeUtils.DATE_MINUTE, "每5分钟"),
    INTERVAL_2_MINUTE(9, 900, 1800, "2m", TimeUtils.DATE_MINUTE, "每2分钟"),
    INTERVAL_MINUTE(10, 600, 900, EsConstants.INTERVAL_MINUTE, TimeUtils.DATE_MINUTE, "每分钟"),
    INTERVAL_30_SECOND(11, 300, 600, "30s", TimeUtils.DATE_TIME, "每30秒"),
    INTERVAL_15_SECOND(12, 150, 300, "15s", TimeUtils.DATE_TIME, "每15秒"),
    INTERVAL_10_SECOND(13, 100, 150, "10s", TimeUtils.DATE_TIME, "每10秒"),
    INTERVAL_5_SECOND(14, 50, 100, "5s", TimeUtils.DATE_TIME, "每5秒"),
    INTERVAL_2_SECOND(15, 20, 50, "2s", TimeUtils.DATE_TIME, "每2秒"),
    INTERVAL_SECOND(16, 0, 20, EsConstants.INTERVAL_SECOND, TimeUtils.DATE_TIME, "每秒");

    private int id;
    private String message;

    @Getter
    private long minSeconds;
    @Getter
    private long maxSeconds;

    @Getter
    private String interval;
    @Getter
    private String format;


    DateHistogram(int id, long minSeconds, long maxSeconds, String interval, String format, String message) {
        this.id = id;
        this.minSeconds = minSeconds;
        this.maxSeconds = maxSeconds;
        this.interval = interval;
        this.format = format;
        this.message = message;
    }

    @Override
    public int id() {
        return this.id;
    }

    @Override
    public String message() {
        return this.message;
    }

    public static DateHistogram of(long interval) {
        DateHistogram dateHistogram = Arrays.stream(DateHistogram.values())
                .filter(t -> t.getMinSeconds() <= interval && interval < t.getMaxSeconds())
                .findFirst()
                .orElse(null);
        Assert.throwIfNull(dateHistogram, "{}没有DateHistogram区间", interval);

        return dateHistogram;
    }
}

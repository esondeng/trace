package com.trace.monitor.enums;

import java.util.Arrays;

import com.eson.common.core.enums.EnumBase;
import com.eson.common.core.util.Assert;
import com.eson.common.core.util.TimeUtils;
import com.trace.monitor.constants.EsConstants;

import lombok.Getter;

/**
 * @author dengxiaolin
 * @since 2021/03/04
 */
public enum DateHistogram implements EnumBase {
    /**
     * 时间间隔类型
     */
    INTERVAL_DAY(0, 24 * 3600, Long.MAX_VALUE, EsConstants.INTERVAL_DAY, TimeUtils.DATE, "每天"),
    INTERVAL_12_HOUR(1, 12 * 3600, 24 * 3600, "12h", TimeUtils.DATE_HOUR, "每12小时"),
    INTERVAL_6_HOUR(2, 6 * 3600, 12 * 3600, "6h", TimeUtils.DATE_HOUR, "每6小时"),
    INTERVAL_2_HOUR(3, 2 * 3600, 6 * 3600, "2h", TimeUtils.DATE_HOUR, "每2小时"),
    INTERVAL_HOUR(4, 3600, 2 * 3600, EsConstants.INTERVAL_HOUR, TimeUtils.DATE_HOUR, "每小时"),
    INTERVAL_30_MINUTE(5, 1800, 3600, "30m", TimeUtils.DATE_MINUTE, "每30分钟"),
    INTERVAL_15_MINUTE(6, 900, 1800, "15m", TimeUtils.DATE_MINUTE, "每15分钟"),
    INTERVAL_10_MINUTE(7, 600, 900, "10m", TimeUtils.DATE_MINUTE, "每10分钟"),
    INTERVAL_5_MINUTE(8, 300, 600, "5m", TimeUtils.DATE_MINUTE, "每5分钟"),
    INTERVAL_2_MINUTE(9, 120, 300, "2m", TimeUtils.DATE_MINUTE, "每2分钟"),
    INTERVAL_MINUTE(10, 60, 120, EsConstants.INTERVAL_MINUTE, TimeUtils.DATE_MINUTE, "每分钟"),
    INTERVAL_30_SECOND(11, 30, 60, "30s", TimeUtils.DATE_TIME, "每30秒"),
    INTERVAL_15_SECOND(12, 15, 30, "15s", TimeUtils.DATE_TIME, "每15秒"),
    INTERVAL_10_SECOND(13, 10, 15, "10s", TimeUtils.DATE_TIME, "每10秒"),
    INTERVAL_5_SECOND(14, 5, 10, "5s", TimeUtils.DATE_TIME, "每5秒"),
    INTERVAL_2_SECOND(15, 2, 5, "2s", TimeUtils.DATE_TIME, "每2秒"),
    INTERVAL_SECOND(16, 0, 2, EsConstants.INTERVAL_SECOND, TimeUtils.DATE_TIME, "每秒");

    private int id;
    private String message;

    @Getter
    private long min;
    @Getter
    private long max;

    @Getter
    private String interval;
    @Getter
    private String format;


    DateHistogram(int id, long min, long max, String interval, String format, String message) {
        this.id = id;
        this.min = min;
        this.max = max;
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
                .filter(t -> t.getMin() <= interval && interval <= t.getMax())
                .findFirst()
                .orElse(null);
        Assert.throwIfNull(dateHistogram, "{}没有DateHistogram区间", interval);

        return dateHistogram;
    }
}

package com.trace.monitor.query;

import java.time.Instant;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.eson.common.core.enums.EnumBase;
import com.eson.common.core.util.TimeUtils;
import com.eson.common.web.query.PageQuery;
import com.trace.monitor.constants.EsConstants;
import com.trace.monitor.enums.DateType;

import lombok.Getter;
import lombok.Setter;

/**
 * @author dengxiaolin
 * @since 2021/02/19
 */
@Getter
@Setter
public class LogQuery extends PageQuery {
    private String dateType;
    private String startTime;
    private String endTime;

    private String condition;

    /**
     * 用于es统计参数
     */
    private String interval;
    private String format;
    private String minBounds;
    private String maxBounds;

    @Override
    public void validate() {
        super.validate();
        fillDataTypeInfo();
        // ES语法
        condition = condition.replaceAll("\"", "\\\\\"");
    }

    private void fillDataTypeInfo() {
        if (StringUtils.isBlank(dateType)) {
            fillStatisticsInfo(startTime, endTime);
        }
        else {
            DateType dateTypeEnum = EnumBase.ofMessage(DateType.class, dateType);

            Date now = new Date();
            endTime = TimeUtils.formatAsDateTime(now);

            switch (dateTypeEnum) {
                case LAST_3_HOUR:
                    Instant start = TimeUtils.ofDate(now).minusHours(3L).toInstant();
                    fillHourInfo(start, now);
                    break;
                case LAST_6_HOUR:
                    start = TimeUtils.ofDate(now).minusHours(6L).toInstant();
                    fillHourInfo(start, now);
                    break;
                case LAST_24_HOUR:
                    start = TimeUtils.ofDate(now).minusHours(24L).toInstant();
                    fillHourInfo(start, now);
                    break;
                case TODAY:
                    start = TimeUtils.ofDate(now).with(TimeUtils.START_OF_DAY).toInstant();
                    fillHourInfo(start, now);
                    break;
                case CURRENT_MONTH:
                    start = TimeUtils.getMonthStartDate(now).toInstant();
                    fillDayInfo(start, now);
                    break;
                default:
                    break;
            }
        }
    }


    private void fillHourInfo(Instant start, Date now) {
        startTime = TimeUtils.formatAsDateTime(start);
        interval = EsConstants.INTERVAL_HOUR;
        format = TimeUtils.DATE_HOUR;
        minBounds = TimeUtils.formatAsHour(start);
        maxBounds = TimeUtils.formatAsHour(now);
    }

    private void fillDayInfo(Instant start, Date now) {
        startTime = TimeUtils.formatAsDateTime(start);
        interval = EsConstants.INTERVAL_DAY;
        format = TimeUtils.DATE;
        minBounds = TimeUtils.formatAsDate(start);
        maxBounds = TimeUtils.formatAsDate(now);
    }

    private void fillStatisticsInfo(String startTime, String endTime) {
        interval = EsConstants.INTERVAL_DAY;
        format = TimeUtils.DATE;

        minBounds = TimeUtils.formatAsDate(TimeUtils.parseAsDate(startTime));
        maxBounds = TimeUtils.formatAsDate(TimeUtils.parseAsDate(endTime));
    }
}

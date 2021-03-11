package com.trace.monitor.query;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.eson.common.core.enums.EnumBase;
import com.eson.common.core.util.TimeUtils;
import com.eson.common.web.query.PageQuery;
import com.trace.monitor.enums.DateHistogram;
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
        if (StringUtils.isNotBlank(dateType)) {
            DateType dateTypeEnum = EnumBase.ofMessage(DateType.class, dateType);

            Date now = new Date();
            endTime = TimeUtils.formatAsDateTime(now);

            switch (dateTypeEnum) {
                case LAST_3_HOUR:
                    startTime = TimeUtils.formatAsDateTime(TimeUtils.ofDate(now).minusHours(3L).toInstant());
                    break;
                case LAST_6_HOUR:
                    startTime = TimeUtils.formatAsDateTime(TimeUtils.ofDate(now).minusHours(6L).toInstant());
                    break;
                case LAST_24_HOUR:
                    startTime = TimeUtils.formatAsDateTime(TimeUtils.ofDate(now).minusHours(24L).toInstant());
                    break;
                case TODAY:
                    startTime = TimeUtils.formatAsDateTime(TimeUtils.ofDate(now).with(TimeUtils.START_OF_DAY).toInstant());
                    break;
                case CURRENT_MONTH:
                    startTime = TimeUtils.formatAsDateTime(TimeUtils.getMonthStartDate(now).toInstant());
                    break;
                default:
                    break;
            }
        }

        fillStatisticsInfo();
    }

    private void fillStatisticsInfo() {
        Date startDate = TimeUtils.parseAsDate(startTime);
        Date endDate = TimeUtils.parseAsDate(endTime);

        long seconds = (endDate.getTime() - startDate.getTime()) / 1000;
        DateHistogram dateHistogram = DateHistogram.of(seconds);

        interval = dateHistogram.getInterval();
        format = dateHistogram.getFormat();
        minBounds = TimeUtils.format(startDate, format);
        maxBounds = TimeUtils.format(endDate, format);
    }
}

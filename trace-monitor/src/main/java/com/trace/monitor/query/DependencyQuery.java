package com.trace.monitor.query;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.eson.common.core.enums.EnumBase;
import com.eson.common.core.util.TimeUtils;
import com.trace.monitor.enums.DateType;

import lombok.Getter;
import lombok.Setter;

/**
 * @author dengxiaolin
 * @since 2021/02/04
 */
@Getter
@Setter
public class DependencyQuery {
    private String dateType;
    private String startTime;
    private String endTime;

    public void validate() {
        if (StringUtils.isNotBlank(dateType)) {
            DateType dateTypeEnum = EnumBase.ofMessage(DateType.class, dateType);

            Date now = new Date();
            endTime = TimeUtils.formatAsDateTime(now);

            switch (dateTypeEnum) {
                case LAST_1_HOUR:
                    startTime = TimeUtils.formatAsDateTime(TimeUtils.ofDate(now).minusHours(1L).toInstant());
                    break;
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
                default:
                    break;
            }
        }
    }
}

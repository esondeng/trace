package com.trace.collect.constants;

import java.util.Arrays;
import java.util.List;

import org.slf4j.MDC;

import com.trace.core.TraceContext;
import com.trace.core.constants.TraceConstants;

/**
 * @author dengxiaolin
 * @since 2021/01/26
 */
public class MdcTraceConstants {

    public static List<Runnable> MDC_RUNNABLE_LIST = Arrays.asList(
            () -> MDC.put(TraceConstants.TRACE_ID, TraceContext.peek().getTraceId()),
            () -> MDC.remove(TraceConstants.TRACE_ID));
}

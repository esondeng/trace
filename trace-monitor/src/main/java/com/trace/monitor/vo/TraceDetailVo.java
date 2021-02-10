package com.trace.monitor.vo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;

import com.eson.common.core.constants.Constants;
import com.eson.common.core.util.Funs;
import com.trace.common.domain.IndexSpan;

import lombok.Getter;
import lombok.Setter;

/**
 * @author dengxiaolin
 * @since 2021/01/28
 */
@Getter
@Setter
public class TraceDetailVo {
    private static final int COST_STEP_NUM = 6;

    private String traceId;
    private long start;
    private long end;
    private long cost;

    /**
     * 页面展示时间步长
     */
    private List<String> costSteps;
    private int appKeyCount;
    private int depth;
    private int totalSpanCount;


    private List<AppKeyCountVo> appKeyCountVos;
    private List<SpanVo> spanVos;


    public static TraceDetailVo of(List<IndexSpan> indexSpans) {
        if (CollectionUtils.isEmpty(indexSpans)) {
            return new TraceDetailVo();
        }
        else {
            indexSpans.sort(Comparator.comparingInt(IndexSpan::getDepth)
                    .thenComparingInt(indexSpan -> {
                        String id = indexSpan.getId();
                        int lastPointIndex = id.lastIndexOf(".");
                        return Integer.valueOf(id.substring(lastPointIndex + 1));
                    }));

            IndexSpan rootSpan = indexSpans.get(0);
            List<SpanVo> spanVos = Funs.map(indexSpans, t -> SpanVo.of(t, rootSpan));
            TraceDetailVo vo = new TraceDetailVo();

            vo.setTraceId(rootSpan.getTraceId());
            vo.setStart(rootSpan.getStart());
            vo.setEnd(rootSpan.getEnd());

            fillCostInfo(vo, rootSpan);
            fillAppCountInfo(vo, spanVos);

            vo.setSpanVos(spanVos);
            vo.setTotalSpanCount(spanVos.size());
            vo.setDepth(spanVos.stream()
                    .mapToInt(SpanVo::getDepth)
                    .max()
                    .orElse(0));


            fillChildrenInfo(vo);

            return vo;
        }
    }

    private static void fillAppCountInfo(TraceDetailVo vo, List<SpanVo> spanVos) {
        Map<String, Integer> appKeyCountMap = new LinkedHashMap<>(16);
        spanVos.forEach(t -> appKeyCountMap.put(t.getAppKey(), appKeyCountMap.getOrDefault(t.getAppKey(), 0) + 1));

        vo.setAppKeyCount(appKeyCountMap.size());
        vo.setAppKeyCountVos(Funs.map(appKeyCountMap.entrySet(), entry -> AppKeyCountVo.of(entry.getKey(), entry.getValue())));
    }


    private static void fillCostInfo(TraceDetailVo vo, IndexSpan rootSpan) {
        vo.setCost(rootSpan.getCost());

        long costStep = rootSpan.getCost() / COST_STEP_NUM;

        List<String> costSteps = new ArrayList<>();
        costSteps.add("");

        for (int i = 1; i < COST_STEP_NUM - 1; i++) {
            costSteps.add(i * costStep + "ms");
        }
        costSteps.add(rootSpan.getCost() + "ms");
        vo.setCostSteps(costSteps);
    }

    private static void fillChildrenInfo(TraceDetailVo vo) {
        List<SpanVo> spanVos = vo.getSpanVos();
        Map<String, SpanVo> spanVoMap = Funs.toMapQuietly(spanVos, SpanVo::getId, t -> t);
        spanVoMap.forEach((k, v) -> {
            String id = v.getId();
            int lastIndex = id.lastIndexOf(Constants.POINT);
            if (lastIndex > 0) {
                String parentId = id.substring(0, lastIndex);
                spanVoMap.get(parentId).addChildId(id);
            }
        });
    }
}

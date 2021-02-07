package com.trace.monitor.vo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;

import com.eson.common.core.constants.Constants;
import com.eson.common.core.util.Funs;
import com.eson.common.core.util.JsonUtils;
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
            List<SpanVo> spanVos = JsonUtils.convertList(indexSpans, SpanVo.class);
            TraceDetailVo vo = new TraceDetailVo();
            spanVos.sort(Comparator.comparing(SpanVo::getId));

            SpanVo rootSpanVo = spanVos.get(0);
            vo.setTraceId(rootSpanVo.getTraceId());

            fillCostInfo(vo, rootSpanVo);

            vo.setSpanVos(spanVos);
            vo.setTotalSpanCount(spanVos.size());
            vo.setDepth(spanVos.stream()
                    .mapToInt(SpanVo::getDepth)
                    .max()
                    .orElse(0));

            Map<String, Integer> appKeyCountMap = new LinkedHashMap<>(16);
            spanVos.forEach(t -> appKeyCountMap.put(t.getAppKey(), appKeyCountMap.getOrDefault(t.getAppKey(), 0) + 1));

            vo.setAppKeyCount(appKeyCountMap.size());
            vo.setAppKeyCountVos(Funs.map(appKeyCountMap.entrySet(), entry -> AppKeyCountVo.of(entry.getKey(), entry.getValue())));

            fillChildrenInfo(vo);

            return vo;
        }

    }

    private static void fillCostInfo(TraceDetailVo vo, SpanVo rootSpanVo) {
        vo.setCost(rootSpanVo.getCost());

        long costStep = rootSpanVo.getCost() / COST_STEP_NUM;

        List<String> costSteps = new ArrayList<>();
        costSteps.add("");

        for (int i = 1; i < COST_STEP_NUM - 1; i++) {
            costSteps.add(i * costStep + "ms");
        }
        costSteps.add(rootSpanVo.getCost() + "ms");
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

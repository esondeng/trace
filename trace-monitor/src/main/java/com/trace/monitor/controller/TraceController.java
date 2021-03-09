package com.trace.monitor.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.trace.monitor.manager.TraceManager;
import com.trace.monitor.query.TraceQuery;

/**
 * @author dengxiaolin
 * @since 2021/01/26
 */
@Controller
public class TraceController {

    @Autowired
    private TraceManager traceManager;

    @GetMapping(value = "/trace/search.html")
    public String traceSearch(Model model, TraceQuery traceQuery) {
        traceQuery.validate();
        model.addAttribute("data", traceManager.getTraceVosByQuery(traceQuery));
        return "trace/ajax/trace-list";
    }

    @GetMapping(value = "/trace/detail.html")
    public String getTraceDetail(Model model, String traceId) {
        model.addAttribute("data", traceManager.getDetailByTraceId(traceId));
        return "trace/trace-detail";
    }
}

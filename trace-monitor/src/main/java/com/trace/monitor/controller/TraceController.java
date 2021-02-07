package com.trace.monitor.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

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

    @RequestMapping(value = "/trace/search.html")
    public String traceSearch(Model model, TraceQuery traceQuery) {
        traceQuery.validate();
        model.addAttribute("data", traceManager.search(traceQuery));
        return "trace/ajax/trace-list";
    }

    @RequestMapping(value = "/trace/detail.html")
    public String getTraceDetail(Model model, String traceId) {
        model.addAttribute("data", traceManager.getTraceDetailByTraceId(traceId));
        return "trace/trace-detail";
    }
}

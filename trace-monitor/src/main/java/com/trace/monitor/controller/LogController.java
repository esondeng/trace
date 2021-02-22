package com.trace.monitor.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.trace.monitor.manager.LogManager;
import com.trace.monitor.query.LogQuery;
import com.trace.monitor.vo.LogAggregationsVo;

/**
 * @author dengxiaolin
 * @since 2021/02/19
 */
@Controller
public class LogController {
    @Autowired
    private LogManager logManager;

    @GetMapping(value = "/log/search.html")
    public String logSearch(Model model, LogQuery logQuery) {
        logQuery.validate();
        model.addAttribute("data", logManager.getLogVosByQuery(logQuery));
        return "log/ajax/log-list";
    }


    @GetMapping(value = "/log/aggs.html")
    @ResponseBody
    public LogAggregationsVo logAggs(LogQuery logQuery) {
        logQuery.validate();
        return logManager.getLogAggsByQuery(logQuery);
    }
}

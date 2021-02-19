package com.trace.monitor.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.trace.monitor.manager.LogManager;
import com.trace.monitor.query.LogQuery;

/**
 * @author dengxiaolin
 * @since 2021/02/19
 */
@Controller
public class LogController {
    @Autowired
    private LogManager logManager;

    @RequestMapping(value = "/log/search.html")
    public String traceSearch(Model model, LogQuery logQuery) {
        logQuery.validate();
        model.addAttribute("data", logManager.getLogVosByQuery(logQuery));
        return "log/ajax/log-list";
    }
}

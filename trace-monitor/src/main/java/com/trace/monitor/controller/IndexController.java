package com.trace.monitor.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.trace.monitor.manager.TraceManager;

/**
 * @author dengxiaolin
 * @since 2021/01/23
 */
@Controller
public class IndexController {

    @Autowired
    private TraceManager traceManager;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/trace/trace.html")
    public String callChain(Model model) {
        model.addAttribute("applicationNames", traceManager.getAppKeys());
        return "/trace/trace";
    }


    @GetMapping("/dependency/dependency.html")
    public String dependency(Model model) {
        model.addAttribute("applicationNames", traceManager.getAppKeys());
        return "/dependency/dependency";
    }

    @GetMapping("/log/log.html")
    public String log(Model model) {
        model.addAttribute("applicationNames", traceManager.getAppKeys());
        return "/log/log";
    }

}

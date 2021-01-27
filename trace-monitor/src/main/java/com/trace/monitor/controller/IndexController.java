package com.trace.monitor.controller;

import java.util.Arrays;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author dengxiaolin
 * @since 2021/01/23
 */
@Controller
public class IndexController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @RequestMapping("/trace/trace.html")
    public String callChain(Model model) {
        model.addAttribute("applicationNames", Arrays.asList("product-dubbo-consumer", "product-dubbo-provider"));
        return "/trace/trace";
    }


    @RequestMapping("/dependency/dependency.html")
    public String dependency(Model model) {
        model.addAttribute("applicationNames", Arrays.asList("product-dubbo-consumer", "product-dubbo-provider"));
        return "/dependency/dependency";
    }


}

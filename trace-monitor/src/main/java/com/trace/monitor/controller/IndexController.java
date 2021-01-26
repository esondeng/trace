package com.trace.monitor.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

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

}

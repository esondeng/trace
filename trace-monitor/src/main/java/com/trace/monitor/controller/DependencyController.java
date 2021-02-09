package com.trace.monitor.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eson.common.web.WebResponse;
import com.trace.monitor.manager.TraceManager;
import com.trace.monitor.query.DependencyQuery;
import com.trace.monitor.vo.DependencyVo;

/**
 * @author dengxiaolin
 * @since 2021/02/01
 */
@RestController
public class DependencyController {

    @Autowired
    private TraceManager traceManager;

    @GetMapping("/dependencies.html")
    public WebResponse<List<DependencyVo>> analyze(DependencyQuery dependencyQuery) {
        dependencyQuery.validate();
        return WebResponse.success(traceManager.getDependencyVos(dependencyQuery));
    }
}

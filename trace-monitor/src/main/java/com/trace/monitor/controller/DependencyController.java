package com.trace.monitor.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eson.common.web.WebResponse;
import com.trace.monitor.vo.DependencyVo;

/**
 * @author dengxiaolin
 * @since 2021/02/01
 */
@RestController
public class DependencyController {
    @GetMapping("/dependencies")
    public WebResponse<List<DependencyVo>> index() {
        List<DependencyVo> list = new ArrayList<>();
        DependencyVo vo = new DependencyVo();
        vo.setParent("product-dubbo-consumer");
        vo.setChild("product-dubbo-provider");
        vo.setCallCount(3);

        list.add(vo);

        vo = new DependencyVo();
        vo.setParent("test-consumer");
        vo.setChild("elephant-provider");
        vo.setCallCount(3);
        list.add(vo);

        vo = new DependencyVo();
        vo.setParent("product-dubbo-consumer");
        vo.setChild("elephant-provider");
        vo.setCallCount(3);

        list.add(vo);

        vo = new DependencyVo();
        vo.setParent("elephant-provider");
        vo.setChild("rider-provider");
        vo.setCallCount(3);

        list.add(vo);

        vo = new DependencyVo();
        vo.setParent("product-dubbo-consumer");
        vo.setChild("money-provider");
        vo.setCallCount(3);

        list.add(vo);

        vo = new DependencyVo();
        vo.setParent("product-dubbo-consumer");
        vo.setChild("org-provider");
        vo.setCallCount(3);

        list.add(vo);

        vo = new DependencyVo();
        vo.setParent("product-dubbo-consumer");
        vo.setChild("user-provider");
        vo.setCallCount(3);

        list.add(vo);

        vo = new DependencyVo();
        vo.setParent("product-dubbo-consumer");
        vo.setChild("token-provider");
        vo.setCallCount(3);

        list.add(vo);



        return WebResponse.success(list);
    }
}

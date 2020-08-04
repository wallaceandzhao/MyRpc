package com.myrpc.client;

import ServiceInterface.TestService;
import com.myrpc.discovery.ServiceDiscovery;
import com.myrpc.rpc.proxy.ServiceProxy;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ControllerExample {
    @RequestMapping(value="/numadd")
    @ResponseBody
    public int way(int num){
        TestService testService = ServiceProxy.getInstance(TestService.class, "TestService", new ServiceDiscovery());

        return testService.myadd(num);
    }

}

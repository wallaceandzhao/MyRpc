package com.myrpc.rpcserver.serviceImp;

import com.myrpc.rpcserver.annotation.RpcService;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@RpcService(ServiceClass = TestService.class,serviceName = "TestService")
//@Component
public class TestService {
    public Integer myadd(Integer a){
        return a+3;
    };
}

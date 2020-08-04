package com.myrpc.rpcserver.register;

import org.springframework.stereotype.Component;

public interface IRegisterCenter {
    /**
     * 注册服务
     * @param serviceName 服务名称
     * @param serviceIp 服务IP
     * @param port 端口号
     */
    void register(String serviceName,String serviceIp,int port);
}

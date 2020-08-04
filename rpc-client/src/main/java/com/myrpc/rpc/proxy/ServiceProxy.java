package com.myrpc.rpc.proxy;

import com.myrpc.discovery.IDiscovery;
import com.myrpc.discovery.ServiceDiscovery;

import java.lang.reflect.Proxy;
@SuppressWarnings("unchecked")
public class ServiceProxy<T> {
    public static <T> T getInstance(Class<T> classInterface, String serviceName, ServiceDiscovery serviceDiscovery) {
        return (T) Proxy.newProxyInstance(classInterface.getClassLoader(),
                new Class[]{classInterface},
                new RpcInvocationHandler(serviceDiscovery, serviceName));
    }
}

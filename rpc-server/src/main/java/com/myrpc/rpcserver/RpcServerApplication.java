package com.myrpc.rpcserver;

import com.myrpc.rpcserver.server.ZookeeperServer;
import org.apache.commons.logging.Log;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.support.ClassPathXmlApplicationContext;

@SpringBootApplication
public class RpcServerApplication {
    @Autowired
    ZookeeperServer zookeeperServer;

    public static void main(String[] args) {
        SpringApplication.run(RpcServerApplication.class, args);
    }

}

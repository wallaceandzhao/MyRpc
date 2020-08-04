package com.myrpc.rpcserver.register;

import com.myrpc.rpcserver.config.ZookeeperConfig;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.springframework.stereotype.Component;


@Component
public class RegisterCenter implements IRegisterCenter{
    private CuratorFramework curatorFramework;

    {   // 通过curator连接zk
        curatorFramework = CuratorFrameworkFactory.builder().
                //定义连接串
                        connectString(ZookeeperConfig.ZK_CONNECTION).
                // 定义session超时时间
                        sessionTimeoutMs(ZookeeperConfig.ZK_TIMEOUT).
                //定义重试策略
                        retryPolicy(new ExponentialBackoffRetry(1000, 10)).build();
        //启动
        curatorFramework.start();
        System.out.println("服务中心创建成功");
    }

    @Override
    public void register(String serviceName, String serviceIp, int port) {
        String path = ZookeeperConfig.REGISTER_NAMESPACE + "/" + serviceName;
        String zkNode = "have exist";
        try {
            //判断 /${registerPath}/${serviceName}节点是否存在，不存在则创建对应的持久节点
            if (curatorFramework.checkExists().forPath(path) == null) {
                curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path, "0".getBytes());
            }
            //设置节点的value为对应的服务地址信息(临时节点)
            String serviceAddress = path + "/" + serviceIp + ":" + port;
            if(curatorFramework.checkExists().forPath(serviceAddress) == null) {
                zkNode = curatorFramework.create().withMode(CreateMode.EPHEMERAL).forPath(serviceAddress, "0".getBytes());
            }
            System.out.println(serviceName + "服务,地址:" + serviceAddress + " 注册成功：" + zkNode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package com.myrpc.discovery;

import com.myrpc.client.Config.ZookeeperConfig;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceDiscovery implements IDiscovery{
    private List<String> ServiceAddresses;
    private Map<String,String> serviceMap = new HashMap<String,String>();
    private CuratorFramework curatorFramework;
    {
        curatorFramework = CuratorFrameworkFactory.builder().connectString(ZookeeperConfig.ZK_CONNECTION)
                .sessionTimeoutMs(ZookeeperConfig.ZK_TIMEOUT)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
        curatorFramework.start();
    }
    @Override
    public String DiscoveryByServiceName(String servicename) {
        String ServicePath = ZookeeperConfig.REGISTER_NAMESPACE + "/" + servicename;
        try {
            ServiceAddresses = curatorFramework.getChildren().forPath(ServicePath);
            addServiceAddress(ServiceAddresses,servicename);
//            System.out.println(ServiceAddresses.get(0));
            registerWatcher(ServicePath,servicename);
//            System.out.println(servicename+"就是他");
        }catch (Exception e){
            e.printStackTrace();
        }
        return serviceMap.get(servicename);
    }
    private void registerWatcher(final String path,final String serviceName) {
        PathChildrenCache pathChildrenCache = new PathChildrenCache(curatorFramework, path, true);

        pathChildrenCache.getListenable().addListener(new PathChildrenCacheListener() {
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
                ServiceAddresses = curatorFramework.getChildren().forPath(path);
                addServiceAddress(ServiceAddresses,serviceName);
                System.out.println("监听到节点:" + path + "变化为:" + ServiceAddresses + "....");
            }
        });
        try {
            pathChildrenCache.start();
        } catch (Exception e) {
            throw new RuntimeException("监听节点变化异常！", e);
        }
    }


    private void addServiceAddress(List<String> serviceAddresses,String serviceName){
        if (!CollectionUtils.isEmpty(serviceAddresses)) {
            String serviceAddress = serviceAddresses.get(0);
            serviceMap.put(serviceName,serviceAddress);
        }
    }
}

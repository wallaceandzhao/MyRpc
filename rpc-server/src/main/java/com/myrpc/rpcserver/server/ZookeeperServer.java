package com.myrpc.rpcserver.server;

import com.myrpc.rpcserver.NettyPacketProcess.NewEncoder;
import com.myrpc.rpcserver.NettyPacketProcess.RequestDecoder;
import com.myrpc.rpcserver.NettyPacketProcess.ResponseDecoder;
import com.myrpc.rpcserver.NettyPacketProcess.ServerRpcHandler;
import com.myrpc.rpcserver.annotation.RpcService;
import com.myrpc.rpcserver.config.ZookeeperConfig;
import com.myrpc.rpcserver.register.IRegisterCenter;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
@Component
public class ZookeeperServer implements InitializingBean, ApplicationContextAware {
    private static final Executor executor = new ThreadPoolExecutor(15,15,0L, TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>(30));

    @Autowired
    IRegisterCenter registerCenter;
    Object a;
    private Map<String, Object> BeanMaps = new HashMap<>();

    @Override
    public void afterPropertiesSet(){
        HandlerByNetty();
    }
    private void HandlerByNetty(){
        EventLoopGroup bossLoopGroup = null;
        EventLoopGroup workerLoopGroup = null;
        try{
            bossLoopGroup = new NioEventLoopGroup();
            workerLoopGroup  = new NioEventLoopGroup();
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossLoopGroup,workerLoopGroup).channel(NioServerSocketChannel.class);
            serverBootstrap.childHandler(
                            new ChannelInitializer<SocketChannel>() {
                                @Override
                                protected void initChannel(SocketChannel socketChannel) throws Exception {
                                    socketChannel.pipeline()
                                            .addLast("encoder", new RequestDecoder())
                                            .addLast("decoder", new NewEncoder())
                                            .addLast("handler", new ServerRpcHandler(BeanMaps));
                                }
                            }
                    )
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            //绑定端口启动netty客户端
            ChannelFuture future = serverBootstrap.bind(ZookeeperConfig.ZK_PORT).sync();
            System.out.println("netty启动,端口为:" + ZookeeperConfig.ZK_PORT + "....");
            future.channel().closeFuture().sync();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        String[] beanNames = applicationContext.getBeanNamesForAnnotation(RpcService.class);
        for(String beanName : beanNames){
            Object bean = applicationContext.getBean(beanName);
            RpcService annotation = bean.getClass().getAnnotation(RpcService.class);
            Class interfaceClass = annotation.ServiceClass();
            String serviceName = annotation.serviceName();
            //将接口的类名和对应的实例bean的映射关系保存起来
            String[] classnames = interfaceClass.getName().split("\\.");
            System.out.println(classnames[classnames.length-1]+"sdsadsa");
            BeanMaps.put(classnames[classnames.length-1], bean);
            //注册实例到zk
            registerCenter.register(serviceName, "127.0.0.1", ZookeeperConfig.ZK_PORT);
        }
    }
}

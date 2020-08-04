package com.myrpc.rpc.proxy;

import NettyPacketProcess.ClientRpcHandler;
import NettyPacketProcess.NewEncoder;
import NettyPacketProcess.ResponseDecoder;
import com.myrpc.discovery.IDiscovery;
import com.myrpc.discovery.ServiceDiscovery;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import NettyPacketProcess.RpcRequest;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
public class RpcInvocationHandler implements InvocationHandler {
    @Autowired
    private IDiscovery serviceDiscovery;

    private String servicename;

    final ClientRpcHandler handler = new ClientRpcHandler();
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest rpcRequest = new RpcRequest(UUID.randomUUID().toString(),
                method.getDeclaringClass().getName(), method.getName() ,args
        );
        return HandlerByNetty(rpcRequest);
    }

    private Object HandlerByNetty(RpcRequest rpcRequest){
        EventLoopGroup eventLoopGroup = null;
        try{
            eventLoopGroup = new NioEventLoopGroup();
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.TCP_NODELAY ,true)
                    .handler(
                    new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    .addLast("encoder", new NewEncoder())
                                    .addLast("decoder", new ResponseDecoder())
                                    .addLast("handler", handler);
                        }
                    }
            );
        //通过service从zk获取服务端地址
        String address = serviceDiscovery.DiscoveryByServiceName(servicename);
        //绑定端口启动netty客户端
//            System.out.println(address+"就是他");
        String[] add = address.split(":");
        System.out.println(add[1]);
        ChannelFuture future = bootstrap.connect(add[0], Integer.parseInt(add[1])).sync();
        //通过Netty发送  RPCRequest给服务端
        future.channel().writeAndFlush(rpcRequest).sync();
        future.channel().closeFuture().sync();

        }catch(Exception e){
            e.printStackTrace();
        }finally {
            eventLoopGroup.shutdownGracefully();
        }
        return handler.getResponse();
    }
}

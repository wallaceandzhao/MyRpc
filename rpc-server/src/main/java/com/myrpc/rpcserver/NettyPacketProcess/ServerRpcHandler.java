package com.myrpc.rpcserver.NettyPacketProcess;

import com.myrpc.rpcserver.config.ZookeeperConfig;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.lang.reflect.Method;
import java.util.*;

public class ServerRpcHandler extends ChannelInboundHandlerAdapter {
    private RpcResponse response;
    public ServerRpcHandler(Map<String,Object> serviceMap){
        this.serviceMap = serviceMap;
    }
    private Map<String,Object> serviceMap;
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RpcRequest rpcRequest = (RpcRequest)msg;
        String className = rpcRequest.getClassName();
        Object result = null;
        ChannelFuture future = null;
        try{
            Class<?> clazz = Class.forName("com.myrpc.rpcserver.serviceImp."+className.split("\\.")[1]);
            Object[] parameters = rpcRequest.getArgs();
            for(Map.Entry<String,Object> en:serviceMap.entrySet())
                System.out.println(en.getKey()+"112");
            Object ServiceInstance = serviceMap.get(className.split("\\.")[1]);
            //无参直接调用
            if (parameters == null) {

                Method method = clazz.getMethod(rpcRequest.getMethodName());
                 result = method.invoke(ServiceInstance);

            }else{
                Class[] types = new Class[parameters.length];
                //类型数组
                StringBuilder sb = new StringBuilder();
                sb.toString();
                for (int i = 0; i < parameters.length; i++) {
                    types[i] = parameters[i].getClass();
                }
                Method method = clazz.getMethod(rpcRequest.getMethodName(), types);
                result = method.invoke(ServiceInstance, parameters);
            }
            response.setId(((RpcRequest) msg).getId());
            response.setResult(result);
            if (result == null) {
                // 如果方法结果为空，将一个默认的OK结果给客户端
                future = ctx.writeAndFlush(ZookeeperConfig.DEFAULT_MSG);
            } else {
                // 将返回值写给客户端写给客户端结果
                future = ctx.writeAndFlush(response);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }

    public Object getResponse(){
        return  response;
    }
    public Object getResponse(int a){
        return  response;
    }
}

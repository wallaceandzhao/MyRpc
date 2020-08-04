package com.myrpc.rpcserver.NettyPacketProcess;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @program:
 *
 * @description: 自定义Response编码器
 *
 * @author: Mr.Wang
 **/
public class NewEncoder extends MessageToByteEncoder<RpcRequest> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RpcRequest rpcRequest, ByteBuf byteBuf) throws Exception {
        byte[] RequestToByte = SerialUtils.serializeToByte(rpcRequest);
        byteBuf.writeInt(RequestToByte.length);
        byteBuf.writeBytes(RequestToByte);
    }
}

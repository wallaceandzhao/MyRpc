package com.myrpc.rpcserver.NettyPacketProcess;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @program:
 *
 * @description: 自定义解码器
 *
 * @author: Mr.Wang
 **/
public class RequestDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if(byteBuf.readableBytes()<4)
            return;
        //返回当前读索引
        int index = byteBuf.readerIndex();
        //返回当前索引的整型，读索引加4
        int readInt = byteBuf.readInt();
        //若可读长度<应有长度
        if (byteBuf.readableBytes() < readInt) {
            byteBuf.readerIndex(index);
            return;
        }
        byte[] bytes = new byte[readInt];
        byteBuf.readBytes(bytes);
        RpcRequest request = SerialUtils.deserializeFromByte(bytes,RpcRequest.class);
        list.add(request);
    }
}

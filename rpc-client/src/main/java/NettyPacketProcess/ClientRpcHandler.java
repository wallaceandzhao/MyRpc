package NettyPacketProcess;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ClientRpcHandler extends ChannelInboundHandlerAdapter {
    private Object response;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        response = msg;
    }

    public Object getResponse(){
        return  response;
    }
}

package com.myrpc.rpcserver.NettyPacketProcess;
import lombok.Data;

@Data
public class RpcResponse {
    private String id;
    private Object result;
}

package NettyPacketProcess;

import lombok.AllArgsConstructor;
import lombok.Data;
@AllArgsConstructor
@Data
public class RpcRequest {
    private String id;
    private String className;
    private String methodName;
    private Object[] args;
}

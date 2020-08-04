package NettyPacketProcess;
import lombok.Data;

@Data
public class RpcResponse {
    private String id;
    private String result;
}

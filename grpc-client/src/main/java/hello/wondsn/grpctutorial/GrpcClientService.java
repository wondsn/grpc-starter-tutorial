package hello.wondsn.grpctutorial;

import hello.wondsn.tutorial.HelloReply;
import hello.wondsn.tutorial.HelloRequest;
import hello.wondsn.tutorial.SimpleGrpc;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import static hello.wondsn.tutorial.SimpleGrpc.*;

@Service
public class GrpcClientService {

    @GrpcClient("grpc-server")
    private SimpleBlockingStub simpleStub;

    public String sendMessage(String name) {
        try {
            HelloReply response = this.simpleStub.sayHello(
                    HelloRequest.newBuilder().setName(name).build());
            return response.getMessage();
        } catch (StatusRuntimeException e) {
            return "FAILED with " + e.getStatus().getCode().name();
        }
    }
}

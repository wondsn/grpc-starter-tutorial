package hello.wondsn.grpctutorial;

import hello.wondsn.tutorial.HelloReply;
import hello.wondsn.tutorial.HelloRequest;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static hello.wondsn.tutorial.SimpleGrpc.SimpleBlockingStub;

@Service
public class GrpcClientService {

    @GrpcClient("grpc-server")
    private SimpleBlockingStub simpleStub;

    @Autowired
    private GrpcHttpClient httpClient;

    public String sendMessageUsingGrpc(String name) {
        try {
            HelloReply response = this.simpleStub.sayHello(
                    HelloRequest.newBuilder().setName(name).build());
            return response.getMessage();
        } catch (StatusRuntimeException e) {
            return "FAILED with " + e.getStatus().getCode().name();
        }
    }

    public String sendMessageUsingRest(String name) {
        return httpClient.sayHello(name);
    }
}

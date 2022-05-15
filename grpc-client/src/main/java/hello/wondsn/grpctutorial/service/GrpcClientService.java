package hello.wondsn.grpctutorial.service;

import hello.wondsn.grpctutorial.client.RestHttpClient;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static hello.wondsn.tutorial.HelloWorldProto.HelloReply;
import static hello.wondsn.tutorial.HelloWorldProto.HelloRequest;
import static hello.wondsn.tutorial.SimpleGrpc.SimpleBlockingStub;

@Service
public class GrpcClientService {

    @GrpcClient("grpc-server")
    private SimpleBlockingStub simpleBlockingStub;

    @Autowired
    private RestHttpClient httpClient;


    public String sendMessageUsingGrpc(String name) {
        try {
            HelloReply response = this.simpleBlockingStub.sayHello(
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

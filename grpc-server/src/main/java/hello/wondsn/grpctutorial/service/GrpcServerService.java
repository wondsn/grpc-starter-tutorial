package hello.wondsn.grpctutorial.service;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static hello.wondsn.tutorial.HelloWorldProto.HelloReply;
import static hello.wondsn.tutorial.HelloWorldProto.HelloRequest;
import static hello.wondsn.tutorial.SimpleGrpc.SimpleImplBase;

@GrpcService
public class GrpcServerService extends SimpleImplBase {

    private final Logger logger = LoggerFactory.getLogger(GrpcServerService.class);

    @Override
    public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
        logger.info("name = " + request.getName());
        HelloReply reply = HelloReply.newBuilder().setMessage("Hello ==> " + request.getName()).build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

}

package hello.wondsn.grpctutorial;

import hello.wondsn.tutorial.HelloReply;
import hello.wondsn.tutorial.HelloRequest;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import static hello.wondsn.tutorial.SimpleGrpc.SimpleImplBase;

@GrpcService
public class GrpcServerService extends SimpleImplBase {

    @Override
    public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
        HelloReply reply = HelloReply.newBuilder().setMessage("Hello ==> " + request.getName()).build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }
}

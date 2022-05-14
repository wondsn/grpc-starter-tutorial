package hello.wondsn.grpctutorial;

import hello.wondsn.tutorial.*;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static hello.wondsn.tutorial.SimpleGrpc.*;
import static hello.wondsn.tutorial.SimpleGrpc.SimpleStub;

@Service
public class GrpcClientService {

    private Logger log = LoggerFactory.getLogger(GrpcClientService.class);

    @GrpcClient("grpc-server")
    private SimpleBlockingStub simpleBlockingStub;

    @GrpcClient("grpc-server")
    private SimpleStub simpleNonBlockingStub;

    @Autowired
    private GrpcHttpClient httpClient;

    private Integer sum = 0;

    public String sendMessageUsingGrpc(String name) {
        try {
            HelloReply response = this.simpleBlockingStub.sayHello(
                    HelloRequest.newBuilder().setName(name).build());
            return response.getMessage();
        } catch (StatusRuntimeException e) {
            return "FAILED with " + e.getStatus().getCode().name();
        }
    }

    public Integer sendBigDataUsingGrpc() throws InterruptedException {
        final CountDownLatch finishLatch = new CountDownLatch(1);
        StreamObserver<SumReply> responseObserver = new StreamObserver<SumReply>() {
            @Override
            public void onNext(SumReply value) {
                sum += value.getSum();
                finishLatch.countDown();
            }

            @Override
            public void onError(Throwable t) {
                log.warn("error: " + t.getMessage());
                finishLatch.countDown();
            }

            @Override
            public void onCompleted() {
                log.info("Stream end");
                finishLatch.countDown();
            }
        };

        StreamObserver<IntegerRequest> requestObserver = simpleNonBlockingStub.sumStreamInteger(responseObserver);
        try {
            for (int i = 0; i < 10; i++) {
                Random random = new Random(System.currentTimeMillis());
                int num = random.nextInt(100);
                requestObserver.onNext(IntegerRequest.newBuilder().setNum(num).build());
                log.info("stream num = " + num);
                Thread.sleep(1000);
            }
            requestObserver.onCompleted();
        } catch (RuntimeException e) {
            requestObserver.onError(e);
            throw e;
        }

        if (!finishLatch.await(1, TimeUnit.MINUTES)) {
            log.warn("clientSideStream이 1분안에 끝나지 않음");
        }
        return sum;
    }

    public String sendMessageUsingRest(String name) {
        return httpClient.sayHello(name);
    }


}

package hello.wondsn.grpctutorial;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "grpc-server-api", url = "http://localhost:8081/rest")
public interface GrpcHttpClient {

    @GetMapping
    String sayHello(@RequestParam("name") String name);
}

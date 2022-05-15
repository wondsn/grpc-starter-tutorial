package hello.wondsn.grpctutorial.client;

import feign.Headers;
import feign.Param;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "grpc-server-api", url = "http://localhost:8081/rest")
public interface RestHttpClient {

    @GetMapping
    String sayHello(@RequestParam("name") String name);

    @PostMapping(value = "/upload-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Headers("Content-Type: multipart/form-data")
    String fileUpload(@Param(value = "file") MultipartFile file);
}

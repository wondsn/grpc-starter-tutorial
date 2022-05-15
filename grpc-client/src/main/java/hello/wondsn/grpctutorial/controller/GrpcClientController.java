package hello.wondsn.grpctutorial.controller;

import hello.wondsn.grpctutorial.service.GrpcClientService;
import hello.wondsn.grpctutorial.service.FileUploadClientService;
import hello.wondsn.tutorial.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class GrpcClientController {

    @Autowired
    private GrpcClientService grpcClientService;

    @Autowired
    private FileUploadClientService fileService;

    @GetMapping("/grpc")
    public String printMessageUsingGrpc(@RequestParam(defaultValue = "kim") String name) {
        return grpcClientService.sendMessageUsingGrpc(name);
    }

    @GetMapping("/rest")
    public String printMessageUsingRest(@RequestParam(defaultValue = "kim") String name) {
        return grpcClientService.sendMessageUsingRest(name);
    }

    @GetMapping("/grpc-file-upload")
    public String uploadFileDataUsingGrpc(@RequestParam String file) throws IOException {
        Status status = fileService.uploadFileUsingGrpc(file);
        return status.name();
    }

    @GetMapping("/rest-file-upload")
    public String uploadFileDataUsingRest(@RequestParam String file) throws IOException {
        return fileService.uploadFileUsingRest(file);
    }

}

package hello.wondsn.grpctutorial.controller;

import hello.wondsn.tutorial.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@RestController
@RequestMapping("/rest")
public class GrpcServerController {

    private static final Path SERVER_BASE_PATH = Paths.get("grpc-server/src/test/resources/output");

    private final Logger logger = LoggerFactory.getLogger(GrpcServerController.class);

    @GetMapping
    public String sendMessageUsingRest(@RequestParam String name) {
        logger.info("name = "+ name);
        return "Hello ==> " + name;
    }

    @PostMapping("/upload-file")
    public String uploadFile(@RequestPart(value = "file") MultipartFile file) throws IOException {
        String filename = file.getOriginalFilename();
        Path path = SERVER_BASE_PATH.resolve(filename);
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
        }
        return Status.SUCCESS.name();
    }
}

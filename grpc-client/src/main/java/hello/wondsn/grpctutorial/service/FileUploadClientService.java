package hello.wondsn.grpctutorial.service;

import com.google.protobuf.ByteString;
import hello.wondsn.grpctutorial.client.RestHttpClient;
import hello.wondsn.tutorial.*;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.FilenameUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static hello.wondsn.tutorial.FileServiceGrpc.FileServiceStub;

@Service
public class FileUploadClientService {

    private final Logger log = LoggerFactory.getLogger(FileUploadClientService.class);

    @GrpcClient("grpc-server")
    private FileServiceStub fileServiceStub;

    @Autowired
    private RestHttpClient restHttpClient;

    private static final Path UPLOAD_FILE_PATH = Paths.get("grpc-client/src/test/resources/input");

    public Status uploadFileUsingGrpc(String file) throws IOException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        StreamObserver<FileUploadRequest> streamObserver = this.fileServiceStub.uploadFile(new StreamObserver<FileUploadResponse>() {
            @Override
            public void onNext(FileUploadResponse value) {
                log.info("File Upload status :: " + value.getStatus());
            }

            @Override
            public void onError(Throwable t) {}

            @Override
            public void onCompleted() {}
        });
        String name = FilenameUtils.getBaseName(file);
        String extension = FilenameUtils.getExtension(file);
        FileUploadRequest metadata = FileUploadRequest.newBuilder()
                .setMetadata(MetaData.newBuilder()
                        .setName(name)
                        .setType(extension)
                        .build()
                ).build();
        streamObserver.onNext(metadata);

        // upload file as chunk
        InputStream inputStream = Files.newInputStream(UPLOAD_FILE_PATH.resolve(file));
        byte[] bytes = new byte[4096];
        int size;
        while ((size = inputStream.read(bytes)) > 0) {
            FileUploadRequest uploadRequest = FileUploadRequest
                    .newBuilder()
                    .setFile(
                            File.newBuilder()
                                    .setContent(ByteString.copyFrom(bytes, 0, size))
                                    .setSize(size)
                                    .build())
                    .build();
            streamObserver.onNext(uploadRequest);
        }
        inputStream.close();
        streamObserver.onCompleted();
        stopWatch.stop();
        log.info("grpc file upload => " + stopWatch.prettyPrint());
        return Status.SUCCESS;
    }

    public String uploadFileUsingRest(String file) throws IOException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        java.io.File f = UPLOAD_FILE_PATH.resolve(file).toFile();
        DiskFileItem fileItem = new DiskFileItem("file", Files.probeContentType(f.toPath()), false, f.getName(), (int) f.length(), f.getParentFile());

        FileInputStream inputStream = new FileInputStream(f);
        OutputStream outputStream = fileItem.getOutputStream();
        IOUtils.copy(inputStream, outputStream);

        CommonsMultipartFile multipartFile = new CommonsMultipartFile(fileItem);
        stopWatch.stop();
        log.info("rest file upload => " + stopWatch.prettyPrint());
        return restHttpClient.fileUpload(multipartFile);
    }
}

package hello.wondsn.grpctutorial.service;

import com.google.protobuf.ByteString;
import hello.wondsn.tutorial.FileServiceGrpc;
import hello.wondsn.tutorial.FileUploadRequest;
import hello.wondsn.tutorial.FileUploadResponse;
import hello.wondsn.tutorial.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@GrpcService
public class FileUploadServerService extends FileServiceGrpc.FileServiceImplBase {

    private static final Path SERVER_BASE_PATH = Paths.get("grpc-server/src/test/resources/output");

    private final Logger log = LoggerFactory.getLogger(FileUploadServerService.class);

    @Override
    public StreamObserver<FileUploadRequest> uploadFile(StreamObserver<FileUploadResponse> responseObserver) {
        log.info("uploadFile Server");
        return new StreamObserver<FileUploadRequest>() {
            private OutputStream writer;
            Status status = Status.IN_PROGRESS;

            @Override
            public void onNext(FileUploadRequest fileUplaodRequest) {
                try {
                    if (fileUplaodRequest.hasMetadata()) {
                        writer = getFilePath(fileUplaodRequest);
                        return;
                    }
                    writeFile(writer, fileUplaodRequest.getFile().getContent());
                } catch (IOException e) {
                    this.onError(e);
                }
            }

            @Override
            public void onError(Throwable t) {
                status = Status.FAILED;
                this.onCompleted();
            }

            @Override
            public void onCompleted() {
                closeFile(writer);
                status = Status.IN_PROGRESS.equals(status) ? Status.SUCCESS : status;
                FileUploadResponse response = FileUploadResponse.newBuilder().setStatus(status).build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            }
        };
    }

    private OutputStream getFilePath(FileUploadRequest request) throws IOException {
        var fileName = request.getMetadata().getName() + "." + request.getMetadata().getType();
        return Files.newOutputStream(SERVER_BASE_PATH.resolve(fileName), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    private void writeFile(OutputStream writer, ByteString content) throws IOException {
        writer.write(content.toByteArray());
        writer.flush();
    }

    private void closeFile(OutputStream writer){
        try {
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

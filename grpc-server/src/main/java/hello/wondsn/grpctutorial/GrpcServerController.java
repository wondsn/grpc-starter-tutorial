package hello.wondsn.grpctutorial;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GrpcServerController {

    private final Logger logger = LoggerFactory.getLogger(GrpcServerController.class);

    @GetMapping("/rest")
    public String sendMessageUsingRest(@RequestParam String name) {
        logger.info("name = "+ name);
        return "Hello ==> " + name;
    }
}

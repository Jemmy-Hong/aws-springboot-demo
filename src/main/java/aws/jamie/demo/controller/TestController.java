package aws.jamie.demo.controller;

import aws.jamie.demo.service.PodService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Resource
    private PodService podService;

    @GetMapping("/hostname")
    public String getHostName() {
        return "Current Spring Boot is running on the pod : " +  this.podService.getHostname();
    }

}

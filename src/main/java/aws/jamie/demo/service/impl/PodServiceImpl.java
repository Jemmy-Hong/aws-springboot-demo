package aws.jamie.demo.service.impl;

import aws.jamie.demo.service.PodService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PodServiceImpl implements PodService {

    private static final String HOST_NAME = "HOSTNAME";

    private static final String INSTANCE_GUID = "LOCAL";

    @Value("${" + HOST_NAME + ":" + INSTANCE_GUID + "}")
    private String hostName;

    @Override
    public String getHostname() {
        return this.hostName;
    }
}

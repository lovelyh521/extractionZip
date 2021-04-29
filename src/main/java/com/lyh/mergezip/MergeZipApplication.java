package com.lyh.mergezip;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class MergeZipApplication {

    public static void main(String[] args) {
        SpringApplication.run(MergeZipApplication.class, args);
    }

}

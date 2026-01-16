package com.sesjob;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SesJobCrawlerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SesJobCrawlerApplication.class, args);
    }
}

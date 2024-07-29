package com.hh.consertreservation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableRetry
@SpringBootApplication
@ServletComponentScan
public class ConsertreservationApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ConsertreservationApplication.class, args);
    }

}

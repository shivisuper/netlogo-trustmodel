package com.netlogo.trustmodel;

import org.nlogo.headless.HeadlessWorkspace;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public HeadlessWorkspace headlessWorkspace() {
        return HeadlessWorkspace.newInstance();
    }
}

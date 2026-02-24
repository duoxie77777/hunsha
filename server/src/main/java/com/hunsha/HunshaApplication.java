package com.hunsha;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.hunsha.mapper")
public class HunshaApplication {

    public static void main(String[] args) {
        SpringApplication.run(HunshaApplication.class, args);
    }
}

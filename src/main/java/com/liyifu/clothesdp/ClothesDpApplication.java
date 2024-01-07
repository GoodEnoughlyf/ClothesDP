package com.liyifu.clothesdp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "com.liyifu.clothesdp.mapper")
public class ClothesDpApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClothesDpApplication.class, args);
    }

}

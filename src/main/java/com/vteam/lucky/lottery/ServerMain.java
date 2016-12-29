package com.vteam.lucky.lottery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author li.cheng
 * @version 1.0.0 2016年12月17日
 * @since soter 1.0.0
 */
@Configuration
@ComponentScan
@EnableAutoConfiguration
public class ServerMain {

    public static void main(String[] args) {
        SpringApplication.run(ServerMain.class);
    }

}

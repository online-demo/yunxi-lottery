package com.yunxi.lucky.lottery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @author 无双老师【云析学院】
 * @version 1.0.0 2019年12月17日
 * @since  1.0.0
 */
@Configuration
@ComponentScan
@EnableAutoConfiguration
public class ServerMain {

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

    public static void main(String[] args) {
        SpringApplication.run(ServerMain.class);
    }

}

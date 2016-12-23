package com.vteam.lucky.lottery.setting;

import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;

public class ApplicationListenerStarted implements ApplicationListener<ApplicationStartedEvent> {
    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {

        //init data

        //init person

        //init process

        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~");
    }

}
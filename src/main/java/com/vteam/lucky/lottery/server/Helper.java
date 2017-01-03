package com.vteam.lucky.lottery.server;

import org.springframework.stereotype.Component;

import javax.websocket.Session;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author li.cheng
 * @version 1.0.0 2017年01月03日
 * @since soter 1.0.0
 */
public class Helper {



    private static Map<String,Session> shows = new ConcurrentHashMap<>();

    private static Map<String,Session> operations = new ConcurrentHashMap<>();

    private static Map<String,Session> getSessionMap(ClientType type){
        switch (type) {
            case show:
                return shows;
            case operation:
                return operations;
        }
        throw new RuntimeException("");
    }

    public static void addSession(ClientType type,Session session){
        getSessionMap(type).put(session.getId(),session);
    }

    public static void removeSession(ClientType type,Session session){
        getSessionMap(type).remove(session.getId());
    }

    public static void sendMessage(Command command) throws IOException {
        getSessionMap(ClientType.show).values().forEach(session -> {
            try {
                session.getBasicRemote().sendText(command.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }


}

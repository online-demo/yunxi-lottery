package com.yunxi.lucky.lottery.server;

import javax.websocket.Session;
import java.io.IOException;

/**
 * @author li.cheng
 * @version 1.0.0 2017年01月03日
 * @since soter 1.0.0
 */
public class Helper {



    private static Session processSession;

    private static Session awardSession;

    public static void addSession(ClientType type,Session session){
        switch (type) {
            case process:
                processSession = session;
                break;
            case award:
                awardSession = session;
                break;
        }

    }

    public static void removeSession(ClientType type){
        switch (type) {
            case process:
                processSession = null;
                break;
            case award:
                awardSession = null;
                break;
        }
    }

    public static void sendMessage(Command command) throws IOException {
        Session session = null == awardSession ? processSession :awardSession;
        if(null != session){
            session.getBasicRemote().sendText(command.toString());
        }
    }


}

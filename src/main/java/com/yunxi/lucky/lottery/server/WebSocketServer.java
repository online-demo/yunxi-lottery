package com.yunxi.lucky.lottery.server;

import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

/**
 * @author 无双老师【云析学院】
 * @version 1.0.0 2017年01月03日
 * @since  1.0.0
 */
@ServerEndpoint(value = "/websocket/{type}")
@Component
public class WebSocketServer {
    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;
    private ClientType clientType;

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(
            @PathParam("type") String type,
            Session session) {
        try {
            clientType = ClientType.valueOf(type);
            this.session = session;
            session.getBasicRemote().sendText(Command.CONNECTED.toString());
            Helper.addSession(clientType, session);
        } catch (Exception e) {
            try {
                session.close();
            } catch (IOException ignored) {
            }
        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        Helper.removeSession(clientType);
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
    }

    /**
     * 发生错误时调用
     */
    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println("发生错误");
        error.printStackTrace();
    }

}
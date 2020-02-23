package com.example.rtspDemo;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WsHandler extends BinaryWebSocketHandler {

    /**
     * 存放所有在线的客户端
     */
    private static Map<String, WebSocketSession> clients = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("新的加入");
        clients.put(session.getId(), session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        clients.remove(session.getId());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        clients.remove(session.getId());
    }

    public void sendVideo(byte[] data) {
        BinaryMessage binaryMessage = new BinaryMessage(data);
        for (Map.Entry<String, WebSocketSession> sessionEntry : clients.entrySet()) {
            try {
                WebSocketSession session = sessionEntry.getValue();
                if (session.isOpen()) {
                    session.sendMessage(binaryMessage);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

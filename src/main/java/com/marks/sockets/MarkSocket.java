package com.marks.sockets;

import org.apache.log4j.Logger;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by David Jobe on 5/11/16.
 */

/**
 * This websocket is responsible for updating the feed tab for all connected users
 * when a new Mark is created by someone.
 */

@WebSocket(maxTextMessageSize = 90000)
public class MarkSocket {
    
    private static final Queue<Session> sessions = new ConcurrentLinkedQueue<>();
    final static Logger logger = Logger.getLogger(MarkSocket.class);

    @OnWebSocketConnect
    public void connected(Session session) {
        logger.info(sessions.size() + " connected users");
        sessions.add(session);
    }

    @OnWebSocketClose
    public void closed(Session session, int statusCode, String reason) {
        logger.info("closed()");
        sessions.remove(session);
    }

    @OnWebSocketMessage
    public void message(Session session, String object) throws IOException {
        logger.info("Got: " + object);
        logger.info(sessions.size() + " connected users");
        sessions.stream().filter(Session::isOpen).forEach(s -> {
            try {
                s.getRemote().sendString(object);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }
}

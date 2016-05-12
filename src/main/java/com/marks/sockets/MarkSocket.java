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
@WebSocket(maxTextMessageSize = 90000)
public class MarkSocket {

    // Store sessions if you want to, for example, broadcast a message to all users
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

    //TODO send MarkDTO with promotes and demotes
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

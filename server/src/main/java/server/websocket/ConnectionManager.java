package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<Integer, ArrayList<Session>> gameSessions = new ConcurrentHashMap<>();

    public void add(String visitorName, Session session) {
        var connection = new Connection(visitorName, session);
        connections.put(visitorName, connection);
    }

    public void remove(String visitorName) {
        connections.remove(visitorName);
    }

    public void broadcast(String excludeVisitorName, ServerMessage notification) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (!c.visitorName.equals(excludeVisitorName)) {
                    c.send(notification.toString());
                }
            } else {
                removeList.add(c);
            }
        }

        // Clean up any connections that were left open.
        for (var c : removeList) {
            connections.remove(c.visitorName);
        }
    }

    public ArrayList<Session> getSessions(Integer gameID) {
        ArrayList<Session> sessions = gameSessions.get(gameID);
        if (sessions == null) {
            return new ArrayList<>();
        }
        return sessions;
    }

    public static void saveSession(Integer gameID, Session session) {
        ArrayList<Session> sessions = gameSessions.get(gameID);
        if (sessions == null) {
            sessions = new ArrayList<>();
            gameSessions.put(gameID, sessions);
        }
        synchronized (sessions) {
            sessions.add(session);
        }
    }

}
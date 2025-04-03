package server.websocket;

import com.google.gson.Gson;
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

    //change broadcast since this broadcasts to everyone
    //need to broadcast just to people in the game
    //also need to change it so the notification is a gson object
    public void broadcast(String excludeVisitorName, int gameID, ServerMessage notification) throws IOException {
        var removeList = new ArrayList<Session>();

        ArrayList<Session> sessions = gameSessions.get(gameID);
        if (sessions == null) return;

        for (Session session : sessions) {
            if (session.isOpen()) {
                Connection connection = null;
                for (Connection conn : connections.values()) {
                    if (conn.session.equals(session)) {
                        connection = conn;
                        break;
                    }
                }
                if (connection != null && !connection.visitorName.equals(excludeVisitorName)) {
                    connection.send(new Gson().toJson(notification));
                }
            } else {
                removeList.add(session);
            }
        }

        sessions.removeAll(removeList);
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
            gameSessions.put(gameID, sessions);
        }
    }

    public Connection getConnection(String visitorName) {
        return connections.get(visitorName);
    }

}
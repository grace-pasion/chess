package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer,
            ConcurrentHashMap<String, Connection>> gameConnections;

    public ConnectionManager() {
        gameConnections = new ConcurrentHashMap<>();
    }

    public void add(int gameID, String visitorName, Session session) {
        ConcurrentHashMap<String, Connection> connections = gameConnections.get(gameID);
        if (connections == null) {
            connections = new ConcurrentHashMap<>();
            gameConnections.put(gameID, connections);
        }
        Connection connection = new Connection(visitorName, session);
        connections.put(visitorName, connection);
        gameConnections.put(gameID, connections);
    }

    public void remove(int gameID, String visitorName) {
        System.out.println("Attempting to remove player "
                + visitorName + " from game " + gameID);
        ConcurrentHashMap<String, Connection> connections = gameConnections.get(gameID);
        if (connections != null) {
            System.out.println("Connections found, removing player " + visitorName);
            connections.remove(visitorName);
            if (connections.isEmpty()) {
                gameConnections.remove(gameID);
                System.out.println("Game " + gameID + " removed from gameConnections.");
            }
        }
    }

    public void broadcast(String excludeVisitorName, int gameID, ServerMessage notification) throws IOException {
        ConcurrentHashMap<String, Connection> connections = gameConnections.get(gameID);
        if (connections == null) return;

        ArrayList<String> toRemove = new ArrayList<>();

        for (var entry : connections.entrySet()) {
            String visitorName = entry.getKey();
            Connection connection = entry.getValue();
            Session session = connection.session;

            if (!session.isOpen()) {
                toRemove.add(visitorName);
            } else if (!visitorName.equals(excludeVisitorName)) {
                connection.send(new Gson().toJson(notification));
            }
        }

        for (String visitor : toRemove) {
            connections.remove(visitor);
        }

        if (connections.isEmpty()) {
            gameConnections.remove(gameID);
        }
    }

        public Connection getConnection(int gameID, String visitorName) {
            ConcurrentHashMap<String, Connection> connections = gameConnections.get(gameID);
            return connections != null ? connections.get(visitorName) : null;
        }
    }

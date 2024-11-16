package com.project;

import java.net.http.WebSocket;

public class Connection {
    private static WebSocket connection;

    public static WebSocket getConnection() {
        return connection;
    }
}

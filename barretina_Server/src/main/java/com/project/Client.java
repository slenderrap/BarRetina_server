package com.project;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

public class Client extends WebSocketClient {
    private String location;
    public Client(String location, Draft draft) throws URISyntaxException {
        super(new URI(location),draft);
        this.location = location;

    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {

    }

    @Override
    public void onMessage(String s) {
        System.out.println(s);
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        System.out.println("El cliente se ha desconectado");
    }

    @Override
    public void onError(Exception e) {

    }
}

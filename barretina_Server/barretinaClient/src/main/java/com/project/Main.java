package com.project;

import java.net.URISyntaxException;
import org.java_websocket.drafts.Draft_6455;


public class Main {

    public static void main(String[] args) {

        Client client = null;
        try {
            client = new Client("ws://localhost:3000", new Draft_6455());
        } catch (
                URISyntaxException e) {
            throw new RuntimeException(e);
        }
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        client.connect();
        client.send("{'type':'ping'}");

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        client.close();
        System.out.println("Fin programa");


    }
}

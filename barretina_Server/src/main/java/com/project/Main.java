package com.project;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.json.*;
import org.java_websocket.*;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;



public class Main{
    public static void main(String[] args) {

        // WebSockets server
        Server server = new Server(new InetSocketAddress("0.0.0.0",3000));
        server.start();

//        LineReader reader = LineReaderBuilder.builder().build();
//        System.out.println("Server running. Type 'exit' to gracefully stop it.");




//        Client client = null;
//        try {
//            client = new Client("ws://localhost:3000",new Draft_6455());
//        } catch (URISyntaxException e) {
//            throw new RuntimeException(e);
//        }
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//        client.connect();
//        client.send("ping");
//        client.close();
//        System.out.println("Fin programa");

//        server.onClose();
//        server.onOpen();


    }
}
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
        Server server = new Server(new InetSocketAddress(3000));
        server.start();
        String systemName = Server.askSystemName();

        LineReader reader = LineReaderBuilder.builder().build();
        System.out.println("Server running. Type 'exit' to gracefully stop it.");

        // Add objects
        String name0 = "O0";
        JSONObject obj0 = new JSONObject();
        obj0.put("objectId", name0);
        obj0.put("x", 300);
        obj0.put("y", 50);
        obj0.put("cols", 4);
        obj0.put("rows", 1);
        Server.selectableObjects.put(name0, obj0);

        String name1 = "O1";
        JSONObject obj1 = new JSONObject();
        obj1.put("objectId", name1);
        obj1.put("x", 300);
        obj1.put("y", 100);
        obj1.put("cols", 1);
        obj1.put("rows", 3);
        Server.selectableObjects.put(name1, obj1);

        try {
            while (true) {
                String line = null;
                try {
                    line = reader.readLine("> ");
                } catch (UserInterruptException e) {
                    continue;
                } catch (EndOfFileException e) {
                    break;
                }

                line = line.trim();

                if (line.equalsIgnoreCase("exit")) {
                    System.out.println("Stopping server...");
                    try {
                        server.stop(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                } else {
                    System.out.println("Unknown command. Type 'exit' to stop server gracefully.");
                }
            }
        } finally {
            System.out.println("Server stopped.");
        }


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
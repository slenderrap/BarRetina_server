package com.project;

import java.net.URISyntaxException;
import java.util.Scanner;

import org.java_websocket.drafts.Draft_6455;
import org.jline.reader.EndOfFileException;
import org.jline.reader.UserInterruptException;


public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        System.out.println("Server running. Type 'exit' to close connection.");
        Client client = null;
        try {
            client = new Client("ws://localhost:3000", new Draft_6455());
            client.connect();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        //client.connect();
        try {
            while (true) {
                String line = null;
                try {
                    Thread.sleep(500);
                    System.out.print("> ");
                    line = sc.nextLine();
                } catch (UserInterruptException e) {
                    continue;
                } catch (EndOfFileException e) {
                    break;
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                line = line.trim();

                if (line.equalsIgnoreCase("exit")) {
                    System.out.println("Stopping server...");
                    client.close();
                    break;
                } else {
                    client.send(line);
                }
            }
        } finally {
            System.out.println("Server stopped.");
        }
//        client.send("{'type':'ping'}");

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Fin programa");


    }
}
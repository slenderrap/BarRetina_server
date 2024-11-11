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


//        server.onClose();
//        server.onOpen();


    }
}
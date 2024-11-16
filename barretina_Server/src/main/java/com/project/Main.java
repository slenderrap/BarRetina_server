package com.project;

import com.project.Utils.UtilsDB;

import java.net.InetSocketAddress;

public class Main{
    public static void main(String[] args) {
        try {
            UtilsDB.getInstance();

            Server server = new Server(new InetSocketAddress("0.0.0.0", 3000));
            server.start();
            
            System.out.println("Server started successfully on port 3000");
        } catch (Exception e) {
            System.err.println("Failed to start server: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
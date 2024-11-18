package com.project;

import org.java_websocket.WebSocket;
import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.project.Utils.UtilsDB;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Server extends WebSocketServer {

    private static final List<String> PLAYER_NAMES = Arrays.asList("A", "B");

    private Map<WebSocket, String> clients;
    private List<String> availableNames;
    private Map<String, JSONObject> missatgeBounce = new HashMap<>();

    static Map<String, JSONObject> missatgePing = new HashMap<>();

    public Server(InetSocketAddress address) {
        super(address);
        clients = new ConcurrentHashMap<>();
//        resetAvailableNames();
    }

    private void resetAvailableNames() {
        availableNames = new ArrayList<>(PLAYER_NAMES);
        Collections.shuffle(availableNames);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        String clientName = getNextAvailableName();
        clients.put(conn, clientName);
        System.out.println("WebSocket client connected: " + clientName);
//        sendClientsList();
//        sendCowntdown();
    }

    private String getNextAvailableName() {
        if (availableNames == null || availableNames.isEmpty()) {
            resetAvailableNames();
        }
        return availableNames.remove(0);
    }

    public Map<WebSocket, String> getClients() {
        return clients;
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        String clientName = clients.get(conn);
        clients.remove(conn);
        availableNames.add(clientName);
        System.out.println("WebSocket client disconnected: " + clientName);
//        sendClientsList();
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println("aqui");
        try {
            JSONObject obj = new JSONObject(message);

            if (obj.has("type")) {
                String type = obj.getString("type");

                switch (type) {
                    case "bounce":
                        // Obtenim el clientId del missatge
                        String missatge = obj.getString("message");
                        missatgeBounce.put(missatge, obj);

                        // Prepara el missatge de tipus 'serverMouseMoving' amb les posicions de tots els clients
                        JSONObject rst0 = new JSONObject();
                        rst0.put("type", "bounce");
                        rst0.put("message", missatgeBounce);

                        // Envia el missatge a tots els clients connectats
                        conn.send(rst0.toString());
                        break;
                    case "ping":
                        JSONObject rst1 = new JSONObject();
                        rst1.put("type", "pong");

                        conn.send(rst1.toString());

//                    sendServerSelectableObjects();
                        break;
                    case "getProducts":
                        JSONObject rst2 = new JSONObject();
                        rst2.put("type", "ack");
                        rst2.put("responseType", "getProducts");
                        ArrayList<Product> products = ProductLoader.loadProducts();
                        JSONArray jsonProducts = new JSONArray();
                        for (Product product : products) {
                            jsonProducts.put(product.toJsonObject());
                        }
                        rst2.put("products", jsonProducts);
                        conn.send(rst2.toString());
                        break;
                    case "getTags":
                        JSONObject rst3 = new JSONObject();
                        rst3.put("type", "ack");
                        rst3.put("responseType", "getTags");
                        JSONArray jsonTags = new JSONArray(ProductLoader.getTags());
                        rst3.put("tags", jsonTags);
                        conn.send(rst3.toString());
                        break;
                    case "getTables":
                        /*JSONObject rst4 = new JSONObject();
                        rst4.put("type", "ack");
                        rst4.put("responseType", "getTables");
                        JSONArray jsonTables = UtilsDB.getInstance().queryToJsonArray(
                            "SELECT taula.id_taula as tableNumber, cambrer.nom as waiter,"
                            +"taula.ocupada as occupied, comanda.pagat as paid "
                            +"FROM taula JOIN comanda ON taula.id_comanda = comanda.id_comanda"
                            +"JOIN cambrer ON comanda.id_cambrer = cambrer.id_cambrer"
                        );*
                        rst4.put("tables", jsonTables);
                        conn.send(rst4.toString());
                        break;*/
                        //TEST
                        JSONObject rst4 = new JSONObject();
                        rst4.put("type", "ack");
                        rst4.put("responseType", "getTables");
                        JSONArray jsonTables = new JSONArray();
                        jsonTables.put(new JSONObject("{tableNumber: 1, waiter: 'Marc', occupied: false, paid: false}"));
                        jsonTables.put(new JSONObject("{tableNumber: 2, waiter: 'Maria', occupied: true, paid: false}"));
                        jsonTables.put(new JSONObject("{tableNumber: 3, waiter: 'Pere', occupied: true, paid: true}"));
                        rst4.put("tables", jsonTables);
                        conn.send(rst4.toString());
                        break;
                    case "setCommand":
                        int tableNumber = obj.getInt("tableNumber");
                        JSONArray commandProducts = obj.getJSONArray("products");
                        
                        // Check if table has an existing command
                        String checkCommandQuery = "SELECT id_comanda FROM taula WHERE id_taula = ?";
                        
                        ResultSet rs = UtilsDB.getInstance().queryResultSet(checkCommandQuery, tableNumber);
                        
                        int commandId = -1;
                        boolean commandExists = false;
                        try {
                            if (rs.next()) {
                                // Update existing command
                                Object commandIdObj = rs.getObject("id_comanda");
                                if (commandIdObj != null) {
                                    commandId = rs.getInt("id_comanda");
                                    commandExists = true;
                                }
                            } else {
                                conn.send("{type: 'error', message: 'Error table does not exists, tableNumber: " + tableNumber + "'}");
                                return;
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if (rs != null) {
                                    rs.close();
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                        if (commandExists) {
                            // Delete existing command-products
                            UtilsDB.getInstance().executeUpdate(
                                false,
                                "DELETE FROM comanda_producte WHERE id_comanda = ?",
                                commandId
                            );
                        } else {
                            String insertCommandQuery = "INSERT INTO comanda (pagat, data) VALUES (false, ?)";
                            commandId = UtilsDB.getInstance().executeInsert(
                                false,
                                insertCommandQuery,
                                new Timestamp(System.currentTimeMillis())
                            );
                            String updateTableQuery = "UPDATE taula SET id_comanda = ? WHERE id_taula = ?";
                            UtilsDB.getInstance().executeUpdate(
                                false,
                                updateTableQuery,
                                commandId,
                                tableNumber
                            );
                        }

                        // Insert new command-products
                        String insertProductQuery = "INSERT INTO comanda-producte (id_comanda, id_producte, quantitat) VALUES (?, ?, ?)";
                        for (int i = 0; i < commandProducts.length(); i++) {
                            JSONObject product = commandProducts.getJSONObject(i);
                            UtilsDB.getInstance().executeUpdate(
                                false,
                                insertProductQuery,
                                commandId,
                                product.getInt("id"),
                                product.getInt("quantity")
                            );
                        }

                        // Send confirmation response
                        JSONObject response = new JSONObject();
                        response.put("type", "ack");
                        response.put("responseType", "setCommand");
                        response.put("commandId", commandId);
                        conn.send(response.toString());
                        UtilsDB.getInstance().commit();
                        break;
                    default:
                        conn.send("{type: 'error', message: 'Unknow command'}");
                        break;
                }
            } else {
                conn.send("{type: 'error', message: 'Malformed JSON, missing type'}");

            }
        } catch (JSONException e) {
            conn.send("{type: 'error', message: 'Malformed JSON,required parameters not found for request of type ' + type'}");
        }
    }

//    private void broadcastMessage(String message, WebSocket sender) {
//        for (Map.Entry<WebSocket, String> entry : clients.entrySet()) {
//            WebSocket conn = entry.getKey();
//            if (conn != sender) {
//                try {
//                    conn.send(message);
//                } catch (WebsocketNotConnectedException e) {
//                    System.out.println("Client " + entry.getValue() + " not connected.");
//                    clients.remove(conn);
//                    availableNames.add(entry.getValue());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }

//    private void sendPrivateMessage(String destination, String message, WebSocket senderConn) {
//        boolean found = false;
//
//        for (Map.Entry<WebSocket, String> entry : clients.entrySet()) {
//            if (entry.getValue().equals(destination)) {
//                found = true;
//                try {
//                    entry.getKey().send(message);
//                    JSONObject confirmation = new JSONObject();
//                    confirmation.put("type", "confirmation");
//                    confirmation.put("message", "Message sent to " + destination);
//                    senderConn.send(confirmation.toString());
//                } catch (WebsocketNotConnectedException e) {
//                    System.out.println("Client " + destination + " not connected.");
//                    clients.remove(entry.getKey());
//                    availableNames.add(destination);
//                    notifySenderClientUnavailable(senderConn, destination);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                break;
//            }
//        }

//        if (!found) {
//            System.out.println("Client " + destination + " not found.");
//            notifySenderClientUnavailable(senderConn, destination);
//        }
//    }

    private void notifySenderClientUnavailable(WebSocket sender, String destination) {
        JSONObject rst = new JSONObject();
        rst.put("type", "error");
        rst.put("message", "Client " + destination + " not available.");

        try {
            sender.send(rst.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private void sendClientsList() {
//        JSONArray clientList = new JSONArray();
//        for (String clientName : clients.values()) {
//            clientList.put(clientName);
//        }
//
//        Iterator<Map.Entry<WebSocket, String>> iterator = clients.entrySet().iterator();
//        while (iterator.hasNext()) {
//            Map.Entry<WebSocket, String> entry = iterator.next();
//            WebSocket conn = entry.getKey();
//            String clientName = entry.getValue();
//
//            JSONObject rst = new JSONObject();
//            rst.put("type", "clients");
//            rst.put("id", clientName);
//            rst.put("list", clientList);
//
//            try {
//                conn.send(rst.toString());
//            } catch (WebsocketNotConnectedException e) {
//                System.out.println("Client " + clientName + " not connected.");
//                iterator.remove();
//                availableNames.add(clientName);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }

//    public void sendCowntdown() {
//        int requiredNumberOfClients = 2;
//        if (clients.size() == requiredNumberOfClients) {
//            for (int i = 5; i >= 0; i--) {
//                JSONObject msg = new JSONObject();
//                msg.put("type", "countdown");
//                msg.put("value", i);
//                broadcastMessage(msg.toString(), null);
//                if (i == 0) {
//                    sendServerSelectableObjects();
//                } else {
//                    try {
//                        Thread.sleep(750);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
//    }

//    public void sendServerSelectableObjects() {
//
//        // Prepara el missatge de tipus 'serverObjects' amb les posicions de tots els clients
//        JSONObject rst1 = new JSONObject();
//        rst1.put("type", "serverSelectableObjects");
//        rst1.put("selectableObjects", selectableObjects);
//
//        // Envia el missatge a tots els clients connectats
//        broadcastMessage(rst1.toString(), null);
//    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        System.out.println("WebSocket server started on port: " + getPort());
        setConnectionLostTimeout(0);
        setConnectionLostTimeout(100);
    }

//    public static String askSystemName() {
//        StringBuilder resultat = new StringBuilder();
//        try {
//            ProcessBuilder processBuilder = new ProcessBuilder("uname", "-r");
//            Process process = processBuilder.start();
//
//            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
//            String line;
//            while ((line = reader.readLine()) != null) {
//                resultat.append(line).append("\n");
//            }
//
//            int exitCode = process.waitFor();
//            if (exitCode != 0) {
//                return "Error: El procés ha finalitzat amb codi " + exitCode;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "Error: " + e.getMessage();
//        }
//        return resultat.toString().trim();
//    }

}
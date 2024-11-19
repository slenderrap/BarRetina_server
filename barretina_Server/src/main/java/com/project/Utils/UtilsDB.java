package com.project.Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLRecoverableException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.ResultSetMetaData;

public class UtilsDB {

    public static UtilsDB instance;
    private String HostName = "localhost";
    private String Port = "3306";
    private String DatabaseName = "barretina";
    private String Username = "barretina4";
    private String Password = "barretina4";
    private static Connection conn;

    private UtilsDB() {
        System.out.println("Connecting to the database...");
        connect();
        try {
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Connected");
    }

    public void connect() {
        String url = "jdbc:mysql://" + HostName + ":" + Port + "/" + DatabaseName;
        try {
            conn = DriverManager.getConnection(url, Username, Password);
            conn.setAutoCommit(false); // Desactiva l'autocommit per permetre control manual de transaccions
            
        } catch (SQLException e) {
            System.out.println("Error conecting to the database");
            e.printStackTrace();
            System.exit(0);
        }
    }

    public static UtilsDB getInstance() {
        if (instance == null) {
            instance = new UtilsDB();
        }
        return instance;
    }

    public static Connection getConnection() {
        return instance.conn;
    }

    public PreparedStatement getPreparedStatement(String sql) throws SQLException {
        return conn.prepareStatement(sql);
    }

    public void close() {
        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void update(String sql) {
        try (Statement stmt = conn.createStatement()) {
             stmt.executeUpdate(sql);
             conn.commit(); // Confirma els canvis
        }catch(SQLRecoverableException exc){
            connect();
            update(sql);
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
            try {
                conn.rollback(); // Reverteix els canvis en cas d'error
            } catch (SQLException ex) {
                System.out.println("Error en fer rollback.");
                ex.printStackTrace();
            }
        }
    }

    public int insertAndGetId(String sql) {
        int generatedId = -1;
        try (Statement stmt = conn.createStatement()) {
            // Execute the update
            stmt.executeUpdate(sql);
            conn.commit();  // Make sure to commit the transaction if auto-commit is disabled
    
            // Query the last inserted row ID
            try (ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
                if (rs.next()) {
                    generatedId = rs.getInt(1); // Retrieve the last inserted ID
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            try {
                conn.rollback(); // Rollback the transaction in case of error
            } catch (SQLException ex) {
                System.out.println("Error during rollback.");
                ex.printStackTrace();
            }
        }
        return generatedId;
    }
   
    public List<Map<String, Object>> query(String sql) {
        List<Map<String, Object>> resultList = new ArrayList<>();

        try (Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(metaData.getColumnLabel(i).toLowerCase(), rs.getObject(i));
                }
                resultList.add(row);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return resultList;
    }

    public JSONArray queryToJsonArray(String sql) {
        List<Map<String, Object>> resultList = query(sql);
        JSONArray jsonArray = new JSONArray();
        for (Map<String, Object> row : resultList) {
            JSONObject jsonObject = new JSONObject();
            for (Map.Entry<String, Object> entry : row.entrySet()) {
                if (entry.getValue() != null) {
                    jsonObject.put(entry.getKey(), entry.getValue());
                }
                else{
                    jsonObject.put(entry.getKey(), JSONObject.NULL);
                }
            }
            jsonArray.put(jsonObject);
        }
        return jsonArray;
    }

    public ResultSet queryResultSet(String sql, Object... params) {
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            return stmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void executeUpdate(String sql, Object... params) {
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            stmt.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                System.out.println("Error during rollback: " + ex.getMessage());
            }
            e.printStackTrace();
        }
    }

    public void executeUpdate(boolean commit, String sql, Object... params) {
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            stmt.executeUpdate();
            if (commit) {
                conn.commit();
            }
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                System.out.println("Error during rollback: " + ex.getMessage());
            }
            e.printStackTrace();
        }
    }

    public int executeInsert(String sql, Object... params) {
        int generatedId = -1;
        
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedId = rs.getInt(1);
                }
            }
            conn.commit();
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                System.out.println("Error during rollback: " + ex.getMessage());
            }
            e.printStackTrace();
        }
        
        return generatedId;
    }

    public int executeInsert(boolean commit, String sql, Object... params) {
        int generatedId = -1;
        
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedId = rs.getInt(1);
                }
            }
            if (commit) {
                conn.commit();
            }
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                System.out.println("Error during rollback: " + ex.getMessage());
            }
            e.printStackTrace();
        }
        
        return generatedId;
    }

    public void commit() {
        try {
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
}

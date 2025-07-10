package net.devvoxel.ServerCreate;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class DatabaseManager {
    private Connection connection;

    public void init(File dataFolder) throws SQLException {
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        File dbFile = new File(dataFolder, "servers.db");
        String url = "jdbc:sqlite:" + dbFile.getAbsolutePath();
        connection = DriverManager.getConnection(url);
        try (Statement st = connection.createStatement()) {
            st.executeUpdate("CREATE TABLE IF NOT EXISTS servers (" +
                    "name TEXT PRIMARY KEY," +
                    "mode TEXT," +
                    "server_id INTEGER," +
                    "allocation INTEGER," +
                    "expiration INTEGER"
                    + ")");
        }
    }

    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException ignored) {
            }
        }
    }

    public void addServer(ServerInfo info) throws SQLException {
        String sql = "INSERT OR REPLACE INTO servers (name, mode, server_id, allocation, expiration) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, info.getName());
            ps.setString(2, info.getMode());
            ps.setInt(3, info.getServerId());
            ps.setInt(4, info.getAllocation());
            ps.setLong(5, info.getExpiration());
            ps.executeUpdate();
        }
    }

    public Map<String, ServerInfo> loadServers() throws SQLException {
        Map<String, ServerInfo> map = new HashMap<>();
        String sql = "SELECT name, mode, server_id, allocation, expiration FROM servers";
        try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                String name = rs.getString(1);
                String mode = rs.getString(2);
                int serverId = rs.getInt(3);
                int allocation = rs.getInt(4);
                long exp = rs.getLong(5);
                ServerInfo info = new ServerInfo(name, mode, serverId, allocation);
                info.setExpiration(exp);
                map.put(name, info);
            }
        }
        return map;
    }
}

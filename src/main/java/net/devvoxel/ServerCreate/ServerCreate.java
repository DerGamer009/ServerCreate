package net.devvoxel.ServerCreate;

import org.bukkit.plugin.java.JavaPlugin;
import java.sql.SQLException;

public final class ServerCreate extends JavaPlugin {
    private final java.util.Map<String, ServerInfo> servers = new java.util.HashMap<>();
    private final DatabaseManager db = new DatabaseManager();
    private final TitanNodeApi api = new TitanNodeApi();

    @Override
    public void onEnable() {
        try {
            db.init(getDataFolder());
            servers.putAll(db.loadServers());
        } catch (SQLException e) {
            getLogger().severe("Failed to init database: " + e.getMessage());
        }
        getCommand("server").setExecutor(new ServerCommand(this));
    }

    @Override
    public void onDisable() {
        db.close();
    }

    public java.util.Map<String, ServerInfo> getServers() {
        return servers;
    }

    public DatabaseManager getDatabase() {
        return db;
    }

    public TitanNodeApi getApi() {
        return api;
    }
}

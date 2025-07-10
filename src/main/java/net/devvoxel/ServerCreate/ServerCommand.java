package net.devvoxel.ServerCreate;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import java.io.IOException;
import java.sql.SQLException;

public class ServerCommand implements CommandExecutor {

    private final ServerCreate plugin;

    public ServerCommand(ServerCreate plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("/server <create|join|addtime|settime> ...");
            return true;
        }
        String sub = args[0].toLowerCase();
        switch (sub) {
            case "create":
                handleCreate(sender, args);
                break;
            case "join":
                handleJoin(sender, args);
                break;
            case "addtime":
                handleAddTime(sender, args);
                break;
            case "settime":
                handleSetTime(sender, args);
                break;
            default:
                sender.sendMessage("Unknown subcommand.");
                break;
        }
        return true;
    }

    private void handleCreate(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage("Usage: /server create <mode> <name>");
            return;
        }
        String mode = args[1];
        String name = args[2];
        if (plugin.getServers().containsKey(name)) {
            sender.sendMessage("Server with that name already exists.");
            return;
        }
        try {
            ServerInfo info = plugin.getApi().createServer(name, mode);
            plugin.getServers().put(name, info);
            plugin.getDatabase().addServer(info);
            sender.sendMessage("Created server " + name + " (ID " + info.getServerId() + ") with mode " + mode + ". Starting now...");

            try {
                plugin.getApi().startServer(info.getServerId());
            } catch (IOException | InterruptedException startEx) {
                sender.sendMessage("Failed to start server: " + startEx.getMessage());
            }
        } catch (IOException | InterruptedException | SQLException e) {
            sender.sendMessage("Failed to create server: " + e.getMessage());
        }
    }

    private void handleJoin(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("Usage: /server join <name>");
            return;
        }
        String name = args[1];
        ServerInfo info = plugin.getServers().get(name);
        if (info == null) {
            sender.sendMessage("Server not found.");
            return;
        }
        sender.sendMessage("Joining server " + name + " (IP automatisch). Mode: " + info.getMode());
    }

    private void handleAddTime(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage("Usage: /server addtime <name> <duration>");
            return;
        }
        String name = args[1];
        String duration = combine(args, 2);
        ServerInfo info = plugin.getServers().get(name);
        if (info == null) {
            sender.sendMessage("Server not found.");
            return;
        }
        try {
            long millis = TimeParser.parseDuration(duration);
            info.addTime(millis);
            sender.sendMessage("Added time to server " + name + ".");
        } catch (IllegalArgumentException ex) {
            sender.sendMessage("Invalid duration: " + duration);
        }
    }

    private void handleSetTime(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage("Usage: /server settime <name> <duration|0>");
            return;
        }
        String name = args[1];
        String duration = combine(args, 2);
        ServerInfo info = plugin.getServers().get(name);
        if (info == null) {
            sender.sendMessage("Server not found.");
            return;
        }
        if (duration.equalsIgnoreCase("0") || duration.equalsIgnoreCase("unlimited")) {
            info.setExpiration(0L);
            sender.sendMessage("Server " + name + " set to unlimited time.");
            return;
        }
        try {
            long millis = TimeParser.parseDuration(duration);
            info.setExpiration(System.currentTimeMillis() + millis);
            sender.sendMessage("Set time for server " + name + ".");
        } catch (IllegalArgumentException ex) {
            sender.sendMessage("Invalid duration: " + duration);
        }
    }

    private String combine(String[] args, int start) {
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < args.length; i++) {
            if (i > start) sb.append(' ');
            sb.append(args[i]);
        }
        return sb.toString();
    }
}

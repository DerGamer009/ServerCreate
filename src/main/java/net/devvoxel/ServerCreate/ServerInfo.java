package net.devvoxel.ServerCreate;

public class ServerInfo {
    private final String name;
    private final String mode;
    private final int serverId;
    private final int allocation;
    private long expiration; // 0 = unlimited (no expiration)

    public ServerInfo(String name, String mode, int serverId, int allocation) {
        this.name = name;
        this.mode = mode;
        this.serverId = serverId;
        this.allocation = allocation;
        this.expiration = 0L;
    }

    public String getName() {
        return name;
    }

    public String getMode() {
        return mode;
    }

    public int getServerId() {
        return serverId;
    }

    public int getAllocation() {
        return allocation;
    }

    public long getExpiration() {
        return expiration;
    }

    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }

    public void addTime(long millis) {
        if (expiration == 0L) {
            expiration = System.currentTimeMillis() + millis;
        } else {
            expiration += millis;
        }
    }
}

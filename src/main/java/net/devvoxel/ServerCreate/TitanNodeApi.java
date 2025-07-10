package net.devvoxel.ServerCreate;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TitanNodeApi {
    private static final String API_URL = "https://cp.titannode.de/api/application";
    private static final String API_KEY = "ptla_rwkxfDT9LWbvYLGvXz3sdMcqORSSv1JREsGvnsgkazv";

    private final HttpClient http = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    public ServerInfo createServer(String name, String mode) throws IOException, InterruptedException {
        JsonObject limits = new JsonObject();
        limits.addProperty("memory", 4096);
        limits.addProperty("swap", 0);
        limits.addProperty("disk", 16384);
        limits.addProperty("io", 500);
        limits.addProperty("cpu", 0);

        JsonObject features = new JsonObject();
        features.addProperty("databases", 1);
        features.addProperty("allocations", 1);
        features.addProperty("backups", 0);

        JsonObject allocation = new JsonObject();
        allocation.addProperty("default", 1);

        JsonObject env = new JsonObject();

        JsonObject payload = new JsonObject();
        payload.addProperty("name", name);
        payload.addProperty("user", 1); // default user id
        payload.addProperty("egg", 16);
        payload.addProperty("docker_image", "ghcr.io/parkervcp/yolks:java_17");
        payload.addProperty("startup", "java -Xms128M -Xmx{{SERVER_MEMORY}}M -jar server.jar nogui");
        payload.add("environment", env);
        payload.add("limits", limits);
        payload.add("feature_limits", features);
        payload.add("allocation", allocation);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + "/servers"))
                .header("Authorization", "Bearer " + API_KEY)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(payload)))
                .build();

        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 201) {
            throw new IOException("Create server failed: " + response.body());
        }
        JsonObject obj = gson.fromJson(response.body(), JsonObject.class);
        JsonObject attrs = obj.getAsJsonObject("attributes");
        int serverId = attrs.get("id").getAsInt();
        JsonObject allocationObj = attrs.getAsJsonObject("allocation");
        int allocationId = allocationObj.get("id").getAsInt();
        return new ServerInfo(name, mode, serverId, allocationId);
    }
}

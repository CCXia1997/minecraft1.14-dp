package net.minecraft.server;

import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonParseException;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.UUID;
import net.minecraft.util.JsonHelper;
import com.google.gson.JsonDeserializationContext;
import java.lang.reflect.Type;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonDeserializer;
import com.mojang.authlib.GameProfile;
import net.minecraft.text.TextComponent;

public class ServerMetadata
{
    private TextComponent description;
    private Players players;
    private Version version;
    private String favicon;
    
    public TextComponent getDescription() {
        return this.description;
    }
    
    public void setDescription(final TextComponent textComponent) {
        this.description = textComponent;
    }
    
    public Players getPlayers() {
        return this.players;
    }
    
    public void setPlayers(final Players players) {
        this.players = players;
    }
    
    public Version getVersion() {
        return this.version;
    }
    
    public void setVersion(final Version version) {
        this.version = version;
    }
    
    public void setFavicon(final String string) {
        this.favicon = string;
    }
    
    public String getFavicon() {
        return this.favicon;
    }
    
    public static class Players
    {
        private final int max;
        private final int online;
        private GameProfile[] sample;
        
        public Players(final int max, final int integer2) {
            this.max = max;
            this.online = integer2;
        }
        
        public int getPlayerLimit() {
            return this.max;
        }
        
        public int getOnlinePlayerCount() {
            return this.online;
        }
        
        public GameProfile[] getSample() {
            return this.sample;
        }
        
        public void setSample(final GameProfile[] arr) {
            this.sample = arr;
        }
        
        public static class Deserializer implements JsonDeserializer<Players>, JsonSerializer<Players>
        {
            public Players a(final JsonElement jsonElement, final Type type, final JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                final JsonObject jsonObject4 = JsonHelper.asObject(jsonElement, "players");
                final Players players5 = new Players(JsonHelper.getInt(jsonObject4, "max"), JsonHelper.getInt(jsonObject4, "online"));
                if (JsonHelper.hasArray(jsonObject4, "sample")) {
                    final JsonArray jsonArray6 = JsonHelper.getArray(jsonObject4, "sample");
                    if (jsonArray6.size() > 0) {
                        final GameProfile[] arr7 = new GameProfile[jsonArray6.size()];
                        for (int integer8 = 0; integer8 < arr7.length; ++integer8) {
                            final JsonObject jsonObject5 = JsonHelper.asObject(jsonArray6.get(integer8), "player[" + integer8 + "]");
                            final String string10 = JsonHelper.getString(jsonObject5, "id");
                            arr7[integer8] = new GameProfile(UUID.fromString(string10), JsonHelper.getString(jsonObject5, "name"));
                        }
                        players5.setSample(arr7);
                    }
                }
                return players5;
            }
            
            public JsonElement a(final Players entry, final Type unused, final JsonSerializationContext context) {
                final JsonObject jsonObject4 = new JsonObject();
                jsonObject4.addProperty("max", entry.getPlayerLimit());
                jsonObject4.addProperty("online", entry.getOnlinePlayerCount());
                if (entry.getSample() != null && entry.getSample().length > 0) {
                    final JsonArray jsonArray5 = new JsonArray();
                    for (int integer6 = 0; integer6 < entry.getSample().length; ++integer6) {
                        final JsonObject jsonObject5 = new JsonObject();
                        final UUID uUID8 = entry.getSample()[integer6].getId();
                        jsonObject5.addProperty("id", (uUID8 == null) ? "" : uUID8.toString());
                        jsonObject5.addProperty("name", entry.getSample()[integer6].getName());
                        jsonArray5.add(jsonObject5);
                    }
                    jsonObject4.add("sample", jsonArray5);
                }
                return jsonObject4;
            }
        }
    }
    
    public static class Version
    {
        private final String gameVersion;
        private final int protocolVersion;
        
        public Version(final String string, final int integer) {
            this.gameVersion = string;
            this.protocolVersion = integer;
        }
        
        public String getGameVersion() {
            return this.gameVersion;
        }
        
        public int getProtocolVersion() {
            return this.protocolVersion;
        }
        
        public static class Serializer implements JsonDeserializer<Version>, JsonSerializer<Version>
        {
            public Version a(final JsonElement jsonElement, final Type type, final JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                final JsonObject jsonObject4 = JsonHelper.asObject(jsonElement, "version");
                return new Version(JsonHelper.getString(jsonObject4, "name"), JsonHelper.getInt(jsonObject4, "protocol"));
            }
            
            public JsonElement a(final Version entry, final Type unused, final JsonSerializationContext context) {
                final JsonObject jsonObject4 = new JsonObject();
                jsonObject4.addProperty("name", entry.getGameVersion());
                jsonObject4.addProperty("protocol", entry.getProtocolVersion());
                return jsonObject4;
            }
        }
    }
    
    public static class Deserializer implements JsonDeserializer<ServerMetadata>, JsonSerializer<ServerMetadata>
    {
        public ServerMetadata a(final JsonElement functionJson, final Type unused, final JsonDeserializationContext context) throws JsonParseException {
            final JsonObject jsonObject4 = JsonHelper.asObject(functionJson, "status");
            final ServerMetadata serverMetadata5 = new ServerMetadata();
            if (jsonObject4.has("description")) {
                serverMetadata5.setDescription(context.<TextComponent>deserialize(jsonObject4.get("description"), TextComponent.class));
            }
            if (jsonObject4.has("players")) {
                serverMetadata5.setPlayers(context.<Players>deserialize(jsonObject4.get("players"), Players.class));
            }
            if (jsonObject4.has("version")) {
                serverMetadata5.setVersion(context.<Version>deserialize(jsonObject4.get("version"), Version.class));
            }
            if (jsonObject4.has("favicon")) {
                serverMetadata5.setFavicon(JsonHelper.getString(jsonObject4, "favicon"));
            }
            return serverMetadata5;
        }
        
        public JsonElement a(final ServerMetadata serverMetadata, final Type type, final JsonSerializationContext jsonSerializationContext) {
            final JsonObject jsonObject4 = new JsonObject();
            if (serverMetadata.getDescription() != null) {
                jsonObject4.add("description", jsonSerializationContext.serialize(serverMetadata.getDescription()));
            }
            if (serverMetadata.getPlayers() != null) {
                jsonObject4.add("players", jsonSerializationContext.serialize(serverMetadata.getPlayers()));
            }
            if (serverMetadata.getVersion() != null) {
                jsonObject4.add("version", jsonSerializationContext.serialize(serverMetadata.getVersion()));
            }
            if (serverMetadata.getFavicon() != null) {
                jsonObject4.addProperty("favicon", serverMetadata.getFavicon());
            }
            return jsonObject4;
        }
    }
}

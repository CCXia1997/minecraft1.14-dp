package net.minecraft;

import org.apache.logging.log4j.LogManager;
import java.io.InputStream;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.io.Reader;
import java.io.InputStreamReader;
import java.time.ZonedDateTime;
import net.minecraft.util.JsonHelper;
import com.google.gson.JsonObject;
import java.util.UUID;
import java.util.Date;
import org.apache.logging.log4j.Logger;
import com.mojang.bridge.game.GameVersion;

public class MinecraftVersion implements GameVersion
{
    private static final Logger LOGGER;
    private final String id;
    private final String name;
    private final boolean stable;
    private final int worldVersion;
    private final int protocolVersion;
    private final int packVersion;
    private final Date buildTime;
    private final String releaseTarget;
    
    public MinecraftVersion() {
        this.id = UUID.randomUUID().toString().replaceAll("-", "");
        this.name = "1.14";
        this.stable = true;
        this.worldVersion = 1952;
        this.protocolVersion = 477;
        this.packVersion = 4;
        this.buildTime = new Date();
        this.releaseTarget = "1.14";
    }
    
    protected MinecraftVersion(final JsonObject jsonObject) {
        this.id = JsonHelper.getString(jsonObject, "id");
        this.name = JsonHelper.getString(jsonObject, "name");
        this.releaseTarget = JsonHelper.getString(jsonObject, "release_target");
        this.stable = JsonHelper.getBoolean(jsonObject, "stable");
        this.worldVersion = JsonHelper.getInt(jsonObject, "world_version");
        this.protocolVersion = JsonHelper.getInt(jsonObject, "protocol_version");
        this.packVersion = JsonHelper.getInt(jsonObject, "pack_version");
        this.buildTime = Date.from(ZonedDateTime.parse(JsonHelper.getString(jsonObject, "build_time")).toInstant());
    }
    
    public static GameVersion create() {
        try (final InputStream inputStream1 = MinecraftVersion.class.getResourceAsStream("/version.json")) {
            if (inputStream1 == null) {
                MinecraftVersion.LOGGER.warn("Missing version information!");
                return (GameVersion)new MinecraftVersion();
            }
            try (final InputStreamReader inputStreamReader3 = new InputStreamReader(inputStream1)) {
                return (GameVersion)new MinecraftVersion(JsonHelper.deserialize(inputStreamReader3));
            }
        }
        catch (IOException | JsonParseException ex2) {
            final Exception ex;
            final Exception exception1 = ex;
            throw new IllegalStateException("Game version information is corrupt", exception1);
        }
    }
    
    public String getId() {
        return this.id;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getReleaseTarget() {
        return this.releaseTarget;
    }
    
    public int getWorldVersion() {
        return this.worldVersion;
    }
    
    public int getProtocolVersion() {
        return this.protocolVersion;
    }
    
    public int getPackVersion() {
        return this.packVersion;
    }
    
    public Date getBuildTime() {
        return this.buildTime;
    }
    
    public boolean isStable() {
        return this.stable;
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}

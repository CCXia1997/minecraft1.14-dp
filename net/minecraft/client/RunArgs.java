package net.minecraft.client;

import net.minecraft.client.resource.DirectResourceIndex;
import net.minecraft.client.resource.ResourceIndex;
import javax.annotation.Nullable;
import java.io.File;
import java.net.Proxy;
import com.mojang.authlib.properties.PropertyMap;
import net.minecraft.client.util.Session;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class RunArgs
{
    public final Network network;
    public final WindowSettings windowSettings;
    public final Directories directories;
    public final Game game;
    public final AutoConnect autoConnect;
    
    public RunArgs(final Network network, final WindowSettings windowSettings, final Directories dirs, final Game game, final AutoConnect autoConnect) {
        this.network = network;
        this.windowSettings = windowSettings;
        this.directories = dirs;
        this.game = game;
        this.autoConnect = autoConnect;
    }
    
    @Environment(EnvType.CLIENT)
    public static class Game
    {
        public final boolean demo;
        public final String version;
        public final String versionType;
        
        public Game(final boolean demo, final String version, final String versionType) {
            this.demo = demo;
            this.version = version;
            this.versionType = versionType;
        }
    }
    
    @Environment(EnvType.CLIENT)
    public static class Network
    {
        public final Session session;
        public final PropertyMap b;
        public final PropertyMap profileProperties;
        public final Proxy netProxy;
        
        public Network(final Session session, final PropertyMap propertyMap2, final PropertyMap propertyMap3, final Proxy proxy) {
            this.session = session;
            this.b = propertyMap2;
            this.profileProperties = propertyMap3;
            this.netProxy = proxy;
        }
    }
    
    @Environment(EnvType.CLIENT)
    public static class Directories
    {
        public final File runDir;
        public final File resourcePackDir;
        public final File assetDir;
        public final String assetIndex;
        
        public Directories(final File runDir, final File resPackDir, final File assetDir, @Nullable final String assetIndex) {
            this.runDir = runDir;
            this.resourcePackDir = resPackDir;
            this.assetDir = assetDir;
            this.assetIndex = assetIndex;
        }
        
        public ResourceIndex getResourceIndex() {
            return (this.assetIndex == null) ? new DirectResourceIndex(this.assetDir) : new ResourceIndex(this.assetDir, this.assetIndex);
        }
    }
    
    @Environment(EnvType.CLIENT)
    public static class AutoConnect
    {
        public final String serverIP;
        public final int serverPort;
        
        public AutoConnect(final String serverIP, final int serverPort) {
            this.serverIP = serverIP;
            this.serverPort = serverPort;
        }
    }
}

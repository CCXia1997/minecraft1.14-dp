package net.minecraft.server.dedicated;

import org.apache.logging.log4j.LogManager;
import java.io.IOException;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.Logger;
import net.minecraft.server.PlayerManager;

public class DedicatedPlayerManager extends PlayerManager
{
    private static final Logger LOGGER;
    
    public DedicatedPlayerManager(final MinecraftDedicatedServer minecraftDedicatedServer) {
        super(minecraftDedicatedServer, minecraftDedicatedServer.getProperties().maxPlayers);
        final ServerPropertiesHandler serverPropertiesHandler2 = minecraftDedicatedServer.getProperties();
        this.setViewDistance(serverPropertiesHandler2.viewDistance, serverPropertiesHandler2.viewDistance - 2);
        super.setWhitelistEnabled(serverPropertiesHandler2.whiteList.get());
        if (!minecraftDedicatedServer.isSinglePlayer()) {
            this.getUserBanList().setEnabled(true);
            this.getIpBanList().setEnabled(true);
        }
        this.loadUserBanList();
        this.saveUserBanList();
        this.loadIpBanList();
        this.saveIpBanList();
        this.loadOpList();
        this.loadWhitelist();
        this.saveOpList();
        if (!this.getWhitelist().getFile().exists()) {
            this.saveWhitelist();
        }
    }
    
    @Override
    public void setWhitelistEnabled(final boolean boolean1) {
        super.setWhitelistEnabled(boolean1);
        this.getServer().setUseWhitelist(boolean1);
    }
    
    @Override
    public void addToOperators(final GameProfile gameProfile) {
        super.addToOperators(gameProfile);
        this.saveOpList();
    }
    
    @Override
    public void removeFromOperators(final GameProfile gameProfile) {
        super.removeFromOperators(gameProfile);
        this.saveOpList();
    }
    
    @Override
    public void reloadWhitelist() {
        this.loadWhitelist();
    }
    
    private void saveIpBanList() {
        try {
            this.getIpBanList().save();
        }
        catch (IOException iOException1) {
            DedicatedPlayerManager.LOGGER.warn("Failed to save ip banlist: ", (Throwable)iOException1);
        }
    }
    
    private void saveUserBanList() {
        try {
            this.getUserBanList().save();
        }
        catch (IOException iOException1) {
            DedicatedPlayerManager.LOGGER.warn("Failed to save user banlist: ", (Throwable)iOException1);
        }
    }
    
    private void loadIpBanList() {
        try {
            this.getIpBanList().load();
        }
        catch (IOException iOException1) {
            DedicatedPlayerManager.LOGGER.warn("Failed to load ip banlist: ", (Throwable)iOException1);
        }
    }
    
    private void loadUserBanList() {
        try {
            this.getUserBanList().load();
        }
        catch (IOException iOException1) {
            DedicatedPlayerManager.LOGGER.warn("Failed to load user banlist: ", (Throwable)iOException1);
        }
    }
    
    private void loadOpList() {
        try {
            this.getOpList().load();
        }
        catch (Exception exception1) {
            DedicatedPlayerManager.LOGGER.warn("Failed to load operators list: ", (Throwable)exception1);
        }
    }
    
    private void saveOpList() {
        try {
            this.getOpList().save();
        }
        catch (Exception exception1) {
            DedicatedPlayerManager.LOGGER.warn("Failed to save operators list: ", (Throwable)exception1);
        }
    }
    
    private void loadWhitelist() {
        try {
            this.getWhitelist().load();
        }
        catch (Exception exception1) {
            DedicatedPlayerManager.LOGGER.warn("Failed to load white-list: ", (Throwable)exception1);
        }
    }
    
    private void saveWhitelist() {
        try {
            this.getWhitelist().save();
        }
        catch (Exception exception1) {
            DedicatedPlayerManager.LOGGER.warn("Failed to save white-list: ", (Throwable)exception1);
        }
    }
    
    @Override
    public boolean isWhitelisted(final GameProfile gameProfile) {
        return !this.isWhitelistEnabled() || this.isOperator(gameProfile) || this.getWhitelist().isAllowed(gameProfile);
    }
    
    @Override
    public MinecraftDedicatedServer getServer() {
        return (MinecraftDedicatedServer)super.getServer();
    }
    
    @Override
    public boolean canBypassPlayerLimit(final GameProfile gameProfile) {
        return this.getOpList().isOp(gameProfile);
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}

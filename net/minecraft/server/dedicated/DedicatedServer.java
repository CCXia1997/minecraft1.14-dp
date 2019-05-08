package net.minecraft.server.dedicated;

public interface DedicatedServer
{
    ServerPropertiesHandler getProperties();
    
    String getHostname();
    
    int getPort();
    
    String getMotd();
    
    String getVersion();
    
    int getCurrentPlayerCount();
    
    int getMaxPlayerCount();
    
    String[] getPlayerNames();
    
    String getLevelName();
    
    String getPlugins();
    
    String executeRconCommand(final String arg1);
    
    boolean isDebuggingEnabled();
    
    void info(final String arg1);
    
    void warn(final String arg1);
    
    void logError(final String arg1);
    
    void log(final String arg1);
}

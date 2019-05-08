package net.minecraft.server.dedicated;

import java.util.function.UnaryOperator;
import java.nio.file.Path;

public class ServerPropertiesLoader
{
    private final Path path;
    private ServerPropertiesHandler propertiesHandler;
    
    public ServerPropertiesLoader(final Path path) {
        this.path = path;
        this.propertiesHandler = ServerPropertiesHandler.load(path);
    }
    
    public ServerPropertiesHandler getPropertiesHandler() {
        return this.propertiesHandler;
    }
    
    public void store() {
        this.propertiesHandler.store(this.path);
    }
    
    public ServerPropertiesLoader apply(final UnaryOperator<ServerPropertiesHandler> unaryOperator) {
        (this.propertiesHandler = unaryOperator.apply(this.propertiesHandler)).store(this.path);
        return this;
    }
}

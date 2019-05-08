package net.minecraft.util;

import org.apache.logging.log4j.LogManager;
import javax.annotation.Nullable;
import java.io.OutputStream;
import org.apache.logging.log4j.Logger;
import java.io.PrintStream;

public class PrintStreamLogger extends PrintStream
{
    protected static final Logger LOGGER;
    protected final String name;
    
    public PrintStreamLogger(final String name, final OutputStream outputStream) {
        super(outputStream);
        this.name = name;
    }
    
    @Override
    public void println(@Nullable final String string) {
        this.log(string);
    }
    
    @Override
    public void println(final Object object) {
        this.log(String.valueOf(object));
    }
    
    protected void log(@Nullable final String string) {
        PrintStreamLogger.LOGGER.info("[{}]: {}", this.name, string);
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}

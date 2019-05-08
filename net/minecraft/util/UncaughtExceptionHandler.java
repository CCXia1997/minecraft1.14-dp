package net.minecraft.util;

import org.apache.logging.log4j.Logger;

public class UncaughtExceptionHandler implements Thread.UncaughtExceptionHandler
{
    private final Logger logger;
    
    public UncaughtExceptionHandler(final Logger logger) {
        this.logger = logger;
    }
    
    @Override
    public void uncaughtException(final Thread thread, final Throwable throwable) {
        this.logger.error("Caught previously unhandled exception :");
        this.logger.error(thread.getName(), throwable);
    }
}

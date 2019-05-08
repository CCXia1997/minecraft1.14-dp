package net.minecraft.util;

import org.apache.logging.log4j.Logger;

public class UncaughtExceptionLogger implements Thread.UncaughtExceptionHandler
{
    private final Logger logger;
    
    public UncaughtExceptionLogger(final Logger logger) {
        this.logger = logger;
    }
    
    @Override
    public void uncaughtException(final Thread thread, final Throwable throwable) {
        this.logger.error("Caught previously unhandled exception :", throwable);
    }
}

package net.minecraft.util;

import java.io.OutputStream;

public class DebugPrintStreamLogger extends PrintStreamLogger
{
    public DebugPrintStreamLogger(final String name, final OutputStream outputStream) {
        super(name, outputStream);
    }
    
    @Override
    protected void log(final String string) {
        final StackTraceElement[] arr2 = Thread.currentThread().getStackTrace();
        final StackTraceElement stackTraceElement3 = arr2[Math.min(3, arr2.length)];
        DebugPrintStreamLogger.LOGGER.info("[{}]@.({}:{}): {}", this.name, stackTraceElement3.getFileName(), stackTraceElement3.getLineNumber(), string);
    }
}

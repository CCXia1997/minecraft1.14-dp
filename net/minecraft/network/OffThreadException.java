package net.minecraft.network;

public final class OffThreadException extends RuntimeException
{
    public static final OffThreadException INSTANCE;
    
    private OffThreadException() {
        this.setStackTrace(new StackTraceElement[0]);
    }
    
    @Override
    public synchronized Throwable fillInStackTrace() {
        this.setStackTrace(new StackTraceElement[0]);
        return this;
    }
    
    static {
        INSTANCE = new OffThreadException();
    }
}

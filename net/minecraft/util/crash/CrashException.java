package net.minecraft.util.crash;

public class CrashException extends RuntimeException
{
    private final CrashReport report;
    
    public CrashException(final CrashReport crashReport) {
        this.report = crashReport;
    }
    
    public CrashReport getReport() {
        return this.report;
    }
    
    @Override
    public Throwable getCause() {
        return this.report.getCause();
    }
    
    @Override
    public String getMessage() {
        return this.report.getMessage();
    }
}

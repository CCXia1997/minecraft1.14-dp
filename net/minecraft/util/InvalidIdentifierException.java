package net.minecraft.util;

public class InvalidIdentifierException extends RuntimeException
{
    public InvalidIdentifierException(final String string) {
        super(string);
    }
    
    public InvalidIdentifierException(final String string, final Throwable throwable) {
        super(string, throwable);
    }
}

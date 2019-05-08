package net.minecraft.util;

import java.util.function.Supplier;

public class Lazy<T>
{
    private Supplier<T> supplier;
    private T value;
    
    public Lazy(final Supplier<T> delegate) {
        this.supplier = delegate;
    }
    
    public T get() {
        final Supplier<T> supplier1 = this.supplier;
        if (supplier1 != null) {
            this.value = supplier1.get();
            this.supplier = null;
        }
        return this.value;
    }
}

package net.minecraft.util;

import javax.annotation.Nullable;

public interface Clearable
{
    void clear();
    
    default void clear(@Nullable final Object object) {
        if (object instanceof Clearable) {
            ((Clearable)object).clear();
        }
    }
}

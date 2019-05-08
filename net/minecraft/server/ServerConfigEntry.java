package net.minecraft.server;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;

public class ServerConfigEntry<T>
{
    @Nullable
    private final T object;
    
    public ServerConfigEntry(final T object) {
        this.object = object;
    }
    
    protected ServerConfigEntry(@Nullable final T object, final JsonObject jsonObject) {
        this.object = object;
    }
    
    @Nullable
    T getKey() {
        return this.object;
    }
    
    boolean isInvalid() {
        return false;
    }
    
    protected void serialize(final JsonObject jsonObject) {
    }
}

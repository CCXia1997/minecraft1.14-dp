package net.minecraft.util.registry;

import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.util.Identifier;

public class DefaultedRegistry<T> extends SimpleRegistry<T>
{
    private final Identifier defaultId;
    private T defaultValue;
    
    public DefaultedRegistry(final String defaultId) {
        this.defaultId = new Identifier(defaultId);
    }
    
    @Override
    public <V extends T> V set(final int rawId, final Identifier id, final V entry) {
        if (this.defaultId.equals(id)) {
            this.defaultValue = entry;
        }
        return super.<V>set(rawId, id, entry);
    }
    
    @Override
    public int getRawId(@Nullable final T entry) {
        final int integer2 = super.getRawId(entry);
        return (integer2 == -1) ? super.getRawId(this.defaultValue) : integer2;
    }
    
    @Nonnull
    @Override
    public Identifier getId(final T entry) {
        final Identifier identifier2 = super.getId(entry);
        return (identifier2 == null) ? this.defaultId : identifier2;
    }
    
    @Nonnull
    @Override
    public T get(@Nullable final Identifier id) {
        final T object2 = super.get(id);
        return (object2 == null) ? this.defaultValue : object2;
    }
    
    @Nonnull
    @Override
    public T get(final int index) {
        final T object2 = super.get(index);
        return (object2 == null) ? this.defaultValue : object2;
    }
    
    @Nonnull
    @Override
    public T getRandom(final Random rand) {
        final T object2 = super.getRandom(rand);
        return (object2 == null) ? this.defaultValue : object2;
    }
    
    public Identifier getDefaultId() {
        return this.defaultId;
    }
}

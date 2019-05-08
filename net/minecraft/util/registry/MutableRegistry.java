package net.minecraft.util.registry;

import net.minecraft.util.Identifier;

public abstract class MutableRegistry<T> extends Registry<T>
{
    public abstract <V extends T> V set(final int arg1, final Identifier arg2, final V arg3);
    
    public abstract <V extends T> V add(final Identifier arg1, final V arg2);
    
    public abstract boolean isEmpty();
}

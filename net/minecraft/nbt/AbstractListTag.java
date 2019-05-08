package net.minecraft.nbt;

import java.util.AbstractList;

public abstract class AbstractListTag<T extends Tag> extends AbstractList<T> implements Tag
{
    public abstract T d(final int arg1, final T arg2);
    
    public abstract void c(final int arg1, final T arg2);
    
    public abstract T c(final int arg1);
    
    public abstract boolean setTag(final int arg1, final Tag arg2);
    
    public abstract boolean addTag(final int arg1, final Tag arg2);
}

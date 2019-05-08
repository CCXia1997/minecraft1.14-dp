package net.minecraft.container;

public interface PropertyDelegate
{
    int get(final int arg1);
    
    void set(final int arg1, final int arg2);
    
    int size();
}

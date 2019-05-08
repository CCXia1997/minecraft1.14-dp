package net.minecraft.world.chunk;

interface PaletteResizeListener<T>
{
    int onResize(final int arg1, final T arg2);
}

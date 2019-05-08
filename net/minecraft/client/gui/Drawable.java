package net.minecraft.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface Drawable
{
    void render(final int arg1, final int arg2, final float arg3);
}

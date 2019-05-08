package net.minecraft.client.render.item;

import net.minecraft.item.ItemStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface ItemColorMapper
{
    int getColor(final ItemStack arg1, final int arg2);
}

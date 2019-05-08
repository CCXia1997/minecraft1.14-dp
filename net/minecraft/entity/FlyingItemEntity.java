package net.minecraft.entity;

import net.minecraft.item.ItemStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface FlyingItemEntity
{
    ItemStack getStack();
}

package net.minecraft.container;

import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;

@FunctionalInterface
public interface ContainerProvider
{
    @Nullable
    Container createMenu(final int arg1, final PlayerInventory arg2, final PlayerEntity arg3);
}

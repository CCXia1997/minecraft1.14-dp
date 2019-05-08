package net.minecraft.world;

import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.entity.player.PlayerEntity;

public interface PlayerSaveHandler
{
    void savePlayerData(final PlayerEntity arg1);
    
    @Nullable
    CompoundTag loadPlayerData(final PlayerEntity arg1);
}

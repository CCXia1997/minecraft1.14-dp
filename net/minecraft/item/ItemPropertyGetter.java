package net.minecraft.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.LivingEntity;
import javax.annotation.Nullable;
import net.minecraft.world.World;

public interface ItemPropertyGetter
{
    @Environment(EnvType.CLIENT)
    float call(final ItemStack arg1, @Nullable final World arg2, @Nullable final LivingEntity arg3);
}

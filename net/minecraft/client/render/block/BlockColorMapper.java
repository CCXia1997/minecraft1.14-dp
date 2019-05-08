package net.minecraft.client.render.block;

import net.minecraft.util.math.BlockPos;
import javax.annotation.Nullable;
import net.minecraft.world.ExtendedBlockView;
import net.minecraft.block.BlockState;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface BlockColorMapper
{
    int getColor(final BlockState arg1, @Nullable final ExtendedBlockView arg2, @Nullable final BlockPos arg3, final int arg4);
}

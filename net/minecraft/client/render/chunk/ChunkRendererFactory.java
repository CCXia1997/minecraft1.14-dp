package net.minecraft.client.render.chunk;

import net.minecraft.client.render.WorldRenderer;
import net.minecraft.world.World;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface ChunkRendererFactory
{
    ChunkRenderer create(final World arg1, final WorldRenderer arg2);
}

package net.minecraft.client.render.debug;

import net.minecraft.fluid.FluidState;
import java.util.Iterator;
import net.minecraft.world.ViewableWorld;
import net.minecraft.client.render.Camera;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.world.BlockView;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class WaterDebugRenderer implements DebugRenderer.Renderer
{
    private final MinecraftClient client;
    
    public WaterDebugRenderer(final MinecraftClient minecraftClient) {
        this.client = minecraftClient;
    }
    
    @Override
    public void render(final long long1) {
        final Camera camera3 = this.client.gameRenderer.getCamera();
        final double double4 = camera3.getPos().x;
        final double double5 = camera3.getPos().y;
        final double double6 = camera3.getPos().z;
        final BlockPos blockPos10 = this.client.player.getBlockPos();
        final ViewableWorld viewableWorld11 = this.client.player.world;
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color4f(0.0f, 1.0f, 0.0f, 0.75f);
        GlStateManager.disableTexture();
        GlStateManager.lineWidth(6.0f);
        for (final BlockPos blockPos11 : BlockPos.iterate(blockPos10.add(-10, -10, -10), blockPos10.add(10, 10, 10))) {
            final FluidState fluidState14 = viewableWorld11.getFluidState(blockPos11);
            if (fluidState14.matches(FluidTags.a)) {
                final double double7 = blockPos11.getY() + fluidState14.getHeight(viewableWorld11, blockPos11);
                DebugRenderer.a(new BoundingBox(blockPos11.getX() + 0.01f, blockPos11.getY() + 0.01f, blockPos11.getZ() + 0.01f, blockPos11.getX() + 0.99f, double7, blockPos11.getZ() + 0.99f).offset(-double4, -double5, -double6), 1.0f, 1.0f, 1.0f, 0.2f);
            }
        }
        for (final BlockPos blockPos11 : BlockPos.iterate(blockPos10.add(-10, -10, -10), blockPos10.add(10, 10, 10))) {
            final FluidState fluidState14 = viewableWorld11.getFluidState(blockPos11);
            if (fluidState14.matches(FluidTags.a)) {
                DebugRenderer.a(String.valueOf(fluidState14.getLevel()), blockPos11.getX() + 0.5, blockPos11.getY() + fluidState14.getHeight(viewableWorld11, blockPos11), blockPos11.getZ() + 0.5, -16777216);
            }
        }
        GlStateManager.enableTexture();
        GlStateManager.disableBlend();
    }
}

package net.minecraft.client.render.block.entity;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.ExtendedBlockView;
import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.enums.PistonType;
import net.minecraft.state.property.Property;
import net.minecraft.block.PistonHeadBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.VertexFormats;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.BlockRenderManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.PistonBlockEntity;

@Environment(EnvType.CLIENT)
public class PistonBlockEntityRenderer extends BlockEntityRenderer<PistonBlockEntity>
{
    private final BlockRenderManager manager;
    
    public PistonBlockEntityRenderer() {
        this.manager = MinecraftClient.getInstance().getBlockRenderManager();
    }
    
    @Override
    public void render(final PistonBlockEntity entity, final double xOffset, final double yOffset, final double zOffset, final float tickDelta, final int blockBreakStage) {
        final BlockPos blockPos10 = entity.getPos().offset(entity.r().getOpposite());
        BlockState blockState11 = entity.getPushedBlock();
        if (blockState11.isAir() || entity.getProgress(tickDelta) >= 1.0f) {
            return;
        }
        final Tessellator tessellator12 = Tessellator.getInstance();
        final BufferBuilder bufferBuilder13 = tessellator12.getBufferBuilder();
        this.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
        GuiLighting.disable();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableBlend();
        GlStateManager.disableCull();
        if (MinecraftClient.isAmbientOcclusionEnabled()) {
            GlStateManager.shadeModel(7425);
        }
        else {
            GlStateManager.shadeModel(7424);
        }
        bufferBuilder13.begin(7, VertexFormats.POSITION_COLOR_UV_LMAP);
        bufferBuilder13.setOffset(xOffset - blockPos10.getX() + entity.getRenderOffsetX(tickDelta), yOffset - blockPos10.getY() + entity.getRenderOffsetY(tickDelta), zOffset - blockPos10.getZ() + entity.getRenderOffsetZ(tickDelta));
        final World world14 = this.getWorld();
        if (blockState11.getBlock() == Blocks.aW && entity.getProgress(tickDelta) <= 4.0f) {
            blockState11 = ((AbstractPropertyContainer<O, BlockState>)blockState11).<Comparable, Boolean>with((Property<Comparable>)PistonHeadBlock.SHORT, true);
            this.a(blockPos10, blockState11, bufferBuilder13, world14, false);
        }
        else if (entity.isSource() && !entity.isExtending()) {
            final PistonType pistonType15 = (blockState11.getBlock() == Blocks.aO) ? PistonType.b : PistonType.a;
            BlockState blockState12 = (((AbstractPropertyContainer<O, BlockState>)Blocks.aW.getDefaultState()).with(PistonHeadBlock.TYPE, pistonType15)).<Comparable, Comparable>with((Property<Comparable>)PistonHeadBlock.FACING, (Comparable)blockState11.<V>get((Property<V>)PistonBlock.FACING));
            blockState12 = ((AbstractPropertyContainer<O, BlockState>)blockState12).<Comparable, Boolean>with((Property<Comparable>)PistonHeadBlock.SHORT, entity.getProgress(tickDelta) >= 0.5f);
            this.a(blockPos10, blockState12, bufferBuilder13, world14, false);
            final BlockPos blockPos11 = blockPos10.offset(entity.r());
            bufferBuilder13.setOffset(xOffset - blockPos11.getX(), yOffset - blockPos11.getY(), zOffset - blockPos11.getZ());
            blockState11 = ((AbstractPropertyContainer<O, BlockState>)blockState11).<Comparable, Boolean>with((Property<Comparable>)PistonBlock.EXTENDED, true);
            this.a(blockPos11, blockState11, bufferBuilder13, world14, true);
        }
        else {
            this.a(blockPos10, blockState11, bufferBuilder13, world14, false);
        }
        bufferBuilder13.setOffset(0.0, 0.0, 0.0);
        tessellator12.draw();
        GuiLighting.enable();
    }
    
    private boolean a(final BlockPos blockPos, final BlockState blockState, final BufferBuilder bufferBuilder, final World world, final boolean boolean5) {
        return this.manager.getModelRenderer().tesselate(world, this.manager.getModel(blockState), blockState, blockPos, bufferBuilder, boolean5, new Random(), blockState.getRenderingSeed(blockPos));
    }
}

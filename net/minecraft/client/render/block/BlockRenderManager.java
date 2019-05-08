package net.minecraft.client.render.block;

import net.minecraft.resource.ResourceManager;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.model.BasicBakedModel;
import net.minecraft.block.BlockRenderType;
import net.minecraft.world.ExtendedBlockView;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.BlockState;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.SynchronousResourceReloadListener;

@Environment(EnvType.CLIENT)
public class BlockRenderManager implements SynchronousResourceReloadListener
{
    private final BlockModels models;
    private final BlockModelRenderer renderer;
    private final DynamicBlockRenderer dynamicRenderer;
    private final FluidRenderer fluidRenderer;
    private final Random random;
    
    public BlockRenderManager(final BlockModels models, final BlockColorMap blockColorMap) {
        this.dynamicRenderer = new DynamicBlockRenderer();
        this.random = new Random();
        this.models = models;
        this.renderer = new BlockModelRenderer(blockColorMap);
        this.fluidRenderer = new FluidRenderer();
    }
    
    public BlockModels getModels() {
        return this.models;
    }
    
    public void tesselateDamage(final BlockState state, final BlockPos pos, final Sprite sprite, final ExtendedBlockView extendedBlockView) {
        if (state.getRenderType() != BlockRenderType.c) {
            return;
        }
        final BakedModel bakedModel5 = this.models.getModel(state);
        final long long6 = state.getRenderingSeed(pos);
        final BakedModel bakedModel6 = new BasicBakedModel.Builder(state, bakedModel5, sprite, this.random, long6).build();
        this.renderer.tesselate(extendedBlockView, bakedModel6, state, pos, Tessellator.getInstance().getBufferBuilder(), true, this.random, long6);
    }
    
    public boolean tesselateBlock(final BlockState blockState, final BlockPos blockPos, final ExtendedBlockView extendedBlockView, final BufferBuilder bufferBuilder, final Random random) {
        try {
            final BlockRenderType blockRenderType6 = blockState.getRenderType();
            if (blockRenderType6 == BlockRenderType.a) {
                return false;
            }
            switch (blockRenderType6) {
                case c: {
                    return this.renderer.tesselate(extendedBlockView, this.getModel(blockState), blockState, blockPos, bufferBuilder, true, random, blockState.getRenderingSeed(blockPos));
                }
                case b: {
                    return false;
                }
            }
        }
        catch (Throwable throwable6) {
            final CrashReport crashReport7 = CrashReport.create(throwable6, "Tesselating block in world");
            final CrashReportSection crashReportSection8 = crashReport7.addElement("Block being tesselated");
            CrashReportSection.addBlockInfo(crashReportSection8, blockPos, blockState);
            throw new CrashException(crashReport7);
        }
        return false;
    }
    
    public boolean tesselateFluid(final BlockPos blockPos, final ExtendedBlockView extendedBlockView, final BufferBuilder bufferBuilder, final FluidState fluidState) {
        try {
            return this.fluidRenderer.tesselate(extendedBlockView, blockPos, bufferBuilder, fluidState);
        }
        catch (Throwable throwable5) {
            final CrashReport crashReport6 = CrashReport.create(throwable5, "Tesselating liquid in world");
            final CrashReportSection crashReportSection7 = crashReport6.addElement("Block being tesselated");
            CrashReportSection.addBlockInfo(crashReportSection7, blockPos, null);
            throw new CrashException(crashReport6);
        }
    }
    
    public BlockModelRenderer getModelRenderer() {
        return this.renderer;
    }
    
    public BakedModel getModel(final BlockState state) {
        return this.models.getModel(state);
    }
    
    public void renderDynamic(final BlockState state, final float delta) {
        final BlockRenderType blockRenderType3 = state.getRenderType();
        if (blockRenderType3 == BlockRenderType.a) {
            return;
        }
        switch (blockRenderType3) {
            case c: {
                final BakedModel bakedModel4 = this.getModel(state);
                this.renderer.render(bakedModel4, state, delta, true);
                break;
            }
            case b: {
                this.dynamicRenderer.render(state.getBlock(), delta);
                break;
            }
        }
    }
    
    @Override
    public void apply(final ResourceManager manager) {
        this.fluidRenderer.onResourceReload();
    }
}

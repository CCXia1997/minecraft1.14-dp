package net.minecraft.client.render.block.entity;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.client.block.ChestAnimationProgress;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.block.entity.TrappedChestBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.Direction;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.BlockState;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Calendar;
import net.minecraft.client.render.entity.model.ChestDoubleEntityModel;
import net.minecraft.client.render.entity.model.ChestEntityModel;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ChestBlockEntityRenderer<T extends BlockEntity> extends BlockEntityRenderer<T>
{
    private static final Identifier TRAPPED_DOUBLE_TEX;
    private static final Identifier CHRISTMAS_DOUBLE_TEX;
    private static final Identifier NORMAL_DOUBLE_TEX;
    private static final Identifier TRAPPED_TEX;
    private static final Identifier CHRISTMAS_TEX;
    private static final Identifier NORMAL_TEX;
    private static final Identifier ENDER_TEX;
    private final ChestEntityModel modelSingleChest;
    private final ChestEntityModel modelDoubleChest;
    private boolean isChristmas;
    
    public ChestBlockEntityRenderer() {
        this.modelSingleChest = new ChestEntityModel();
        this.modelDoubleChest = new ChestDoubleEntityModel();
        final Calendar calendar1 = Calendar.getInstance();
        if (calendar1.get(2) + 1 == 12 && calendar1.get(5) >= 24 && calendar1.get(5) <= 26) {
            this.isChristmas = true;
        }
    }
    
    @Override
    public void render(final T entity, final double xOffset, final double yOffset, final double zOffset, final float tickDelta, final int blockBreakStage) {
        GlStateManager.enableDepthTest();
        GlStateManager.depthFunc(515);
        GlStateManager.depthMask(true);
        final BlockState blockState10 = ((BlockEntity)entity).hasWorld() ? ((BlockEntity)entity).getCachedState() : ((AbstractPropertyContainer<O, BlockState>)Blocks.bP.getDefaultState()).<Comparable, Direction>with((Property<Comparable>)ChestBlock.FACING, Direction.SOUTH);
        final ChestType chestType11 = blockState10.<ChestType>contains(ChestBlock.CHEST_TYPE) ? blockState10.<ChestType>get(ChestBlock.CHEST_TYPE) : ChestType.a;
        if (chestType11 == ChestType.b) {
            return;
        }
        final boolean boolean12 = chestType11 != ChestType.a;
        final ChestEntityModel chestEntityModel13 = this.a(entity, blockBreakStage, boolean12);
        if (blockBreakStage >= 0) {
            GlStateManager.matrixMode(5890);
            GlStateManager.pushMatrix();
            GlStateManager.scalef(boolean12 ? 8.0f : 4.0f, 4.0f, 1.0f);
            GlStateManager.translatef(0.0625f, 0.0625f, 0.0625f);
            GlStateManager.matrixMode(5888);
        }
        else {
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        }
        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();
        GlStateManager.translatef((float)xOffset, (float)yOffset + 1.0f, (float)zOffset + 1.0f);
        GlStateManager.scalef(1.0f, -1.0f, -1.0f);
        final float float14 = blockState10.<Direction>get((Property<Direction>)ChestBlock.FACING).asRotation();
        if (Math.abs(float14) > 1.0E-5) {
            GlStateManager.translatef(0.5f, 0.5f, 0.5f);
            GlStateManager.rotatef(float14, 0.0f, 1.0f, 0.0f);
            GlStateManager.translatef(-0.5f, -0.5f, -0.5f);
        }
        this.a(entity, tickDelta, chestEntityModel13);
        chestEntityModel13.a();
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        if (blockBreakStage >= 0) {
            GlStateManager.matrixMode(5890);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5888);
        }
    }
    
    private ChestEntityModel a(final T blockEntity, final int integer, final boolean boolean3) {
        Identifier identifier4;
        if (integer >= 0) {
            identifier4 = ChestBlockEntityRenderer.DESTROY_STAGE_TEXTURES[integer];
        }
        else if (this.isChristmas) {
            identifier4 = (boolean3 ? ChestBlockEntityRenderer.CHRISTMAS_DOUBLE_TEX : ChestBlockEntityRenderer.CHRISTMAS_TEX);
        }
        else if (blockEntity instanceof TrappedChestBlockEntity) {
            identifier4 = (boolean3 ? ChestBlockEntityRenderer.TRAPPED_DOUBLE_TEX : ChestBlockEntityRenderer.TRAPPED_TEX);
        }
        else if (blockEntity instanceof EnderChestBlockEntity) {
            identifier4 = ChestBlockEntityRenderer.ENDER_TEX;
        }
        else {
            identifier4 = (boolean3 ? ChestBlockEntityRenderer.NORMAL_DOUBLE_TEX : ChestBlockEntityRenderer.NORMAL_TEX);
        }
        this.bindTexture(identifier4);
        return boolean3 ? this.modelDoubleChest : this.modelSingleChest;
    }
    
    private void a(final T blockEntity, final float float2, final ChestEntityModel chestEntityModel) {
        float float3 = ((ChestAnimationProgress)blockEntity).getAnimationProgress(float2);
        float3 = 1.0f - float3;
        float3 = 1.0f - float3 * float3 * float3;
        chestEntityModel.b().pitch = -(float3 * 1.5707964f);
    }
    
    static {
        TRAPPED_DOUBLE_TEX = new Identifier("textures/entity/chest/trapped_double.png");
        CHRISTMAS_DOUBLE_TEX = new Identifier("textures/entity/chest/christmas_double.png");
        NORMAL_DOUBLE_TEX = new Identifier("textures/entity/chest/normal_double.png");
        TRAPPED_TEX = new Identifier("textures/entity/chest/trapped.png");
        CHRISTMAS_TEX = new Identifier("textures/entity/chest/christmas.png");
        NORMAL_TEX = new Identifier("textures/entity/chest/normal.png");
        ENDER_TEX = new Identifier("textures/entity/chest/ender.png");
    }
}

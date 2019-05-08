package net.minecraft.client.render.block.entity;

import java.util.Comparator;
import java.util.Arrays;
import net.minecraft.util.DyeColor;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.Direction;
import net.minecraft.state.property.Property;
import net.minecraft.block.enums.BedPart;
import net.minecraft.block.BedBlock;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.model.BedEntityModel;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BedBlockEntity;

@Environment(EnvType.CLIENT)
public class BedBlockEntityRenderer extends BlockEntityRenderer<BedBlockEntity>
{
    private static final Identifier[] TEXTURES;
    private final BedEntityModel model;
    
    public BedBlockEntityRenderer() {
        this.model = new BedEntityModel();
    }
    
    @Override
    public void render(final BedBlockEntity entity, final double xOffset, final double yOffset, final double zOffset, final float tickDelta, final int blockBreakStage) {
        if (blockBreakStage >= 0) {
            this.bindTexture(BedBlockEntityRenderer.DESTROY_STAGE_TEXTURES[blockBreakStage]);
            GlStateManager.matrixMode(5890);
            GlStateManager.pushMatrix();
            GlStateManager.scalef(4.0f, 4.0f, 1.0f);
            GlStateManager.translatef(0.0625f, 0.0625f, 0.0625f);
            GlStateManager.matrixMode(5888);
        }
        else {
            final Identifier identifier10 = BedBlockEntityRenderer.TEXTURES[entity.getColor().getId()];
            if (identifier10 != null) {
                this.bindTexture(identifier10);
            }
        }
        if (entity.hasWorld()) {
            final BlockState blockState10 = entity.getCachedState();
            this.a(blockState10.<BedPart>get(BedBlock.PART) == BedPart.a, xOffset, yOffset, zOffset, blockState10.<Direction>get((Property<Direction>)BedBlock.FACING));
        }
        else {
            this.a(true, xOffset, yOffset, zOffset, Direction.SOUTH);
            this.a(false, xOffset, yOffset, zOffset - 1.0, Direction.SOUTH);
        }
        if (blockBreakStage >= 0) {
            GlStateManager.matrixMode(5890);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5888);
        }
    }
    
    private void a(final boolean boolean1, final double double2, final double double4, final double double6, final Direction direction8) {
        this.model.setVisible(boolean1);
        GlStateManager.pushMatrix();
        GlStateManager.translatef((float)double2, (float)double4 + 0.5625f, (float)double6);
        GlStateManager.rotatef(90.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.translatef(0.5f, 0.5f, 0.5f);
        GlStateManager.rotatef(180.0f + direction8.asRotation(), 0.0f, 0.0f, 1.0f);
        GlStateManager.translatef(-0.5f, -0.5f, -0.5f);
        GlStateManager.enableRescaleNormal();
        this.model.render();
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.popMatrix();
    }
    
    static {
        final Identifier identifier;
        TEXTURES = Arrays.<DyeColor>stream(DyeColor.values()).sorted(Comparator.comparingInt(DyeColor::getId)).map(dyeColor -> {
            new Identifier("textures/entity/bed/" + dyeColor.getName() + ".png");
            return identifier;
        }).<Identifier>toArray(Identifier[]::new);
    }
}

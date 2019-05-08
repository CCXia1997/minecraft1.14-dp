package net.minecraft.client.render.block.entity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BellBlockEntity;

@Environment(EnvType.CLIENT)
public class BellBlockEntityRenderer extends BlockEntityRenderer<BellBlockEntity>
{
    private static final Identifier BELL_BODY_TEXTURE;
    private final BellModel model;
    
    public BellBlockEntityRenderer() {
        this.model = new BellModel();
    }
    
    @Override
    public void render(final BellBlockEntity entity, final double xOffset, final double yOffset, final double zOffset, final float tickDelta, final int blockBreakStage) {
        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();
        this.bindTexture(BellBlockEntityRenderer.BELL_BODY_TEXTURE);
        GlStateManager.translatef((float)xOffset, (float)yOffset, (float)zOffset);
        final float float10 = entity.ringTicks + tickDelta;
        float float11 = 0.0f;
        float float12 = 0.0f;
        if (entity.isRinging) {
            final float float13 = MathHelper.sin(float10 / 3.1415927f) / (4.0f + float10 / 3.0f);
            if (entity.lastSideHit == Direction.NORTH) {
                float11 = -float13;
            }
            else if (entity.lastSideHit == Direction.SOUTH) {
                float11 = float13;
            }
            else if (entity.lastSideHit == Direction.EAST) {
                float12 = -float13;
            }
            else if (entity.lastSideHit == Direction.WEST) {
                float12 = float13;
            }
        }
        this.model.a(float11, float12, 0.0625f);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.popMatrix();
    }
    
    static {
        BELL_BODY_TEXTURE = new Identifier("textures/entity/bell/bell_body.png");
    }
}

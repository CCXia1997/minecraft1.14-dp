package net.minecraft.client.render.block.entity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.MathHelper;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.entity.EndPortalBlockEntity;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class EndGatewayBlockEntityRenderer extends EndPortalBlockEntityRenderer
{
    private static final Identifier BEAM_TEXTURE;
    
    @Override
    public void render(final EndPortalBlockEntity entity, final double xOffset, final double yOffset, final double zOffset, final float tickDelta, final int blockBreakStage) {
        GlStateManager.disableFog();
        final EndGatewayBlockEntity endGatewayBlockEntity10 = (EndGatewayBlockEntity)entity;
        if (endGatewayBlockEntity10.isRecentlyGenerated() || endGatewayBlockEntity10.needsCooldownBeforeTeleporting()) {
            GlStateManager.alphaFunc(516, 0.1f);
            this.bindTexture(EndGatewayBlockEntityRenderer.BEAM_TEXTURE);
            float float11 = endGatewayBlockEntity10.isRecentlyGenerated() ? endGatewayBlockEntity10.getRecentlyGeneratedBeamHeight(tickDelta) : endGatewayBlockEntity10.getCooldownBeamHeight(tickDelta);
            final double double12 = endGatewayBlockEntity10.isRecentlyGenerated() ? (256.0 - yOffset) : 50.0;
            float11 = MathHelper.sin(float11 * 3.1415927f);
            final int integer14 = MathHelper.floor(float11 * double12);
            final float[] arr15 = endGatewayBlockEntity10.isRecentlyGenerated() ? DyeColor.c.getColorComponents() : DyeColor.k.getColorComponents();
            BeaconBlockEntityRenderer.renderLightBeam(xOffset, yOffset, zOffset, tickDelta, float11, endGatewayBlockEntity10.getWorld().getTime(), 0, integer14, arr15, 0.15, 0.175);
            BeaconBlockEntityRenderer.renderLightBeam(xOffset, yOffset, zOffset, tickDelta, float11, endGatewayBlockEntity10.getWorld().getTime(), 0, -integer14, arr15, 0.15, 0.175);
        }
        super.render(entity, xOffset, yOffset, zOffset, tickDelta, blockBreakStage);
        GlStateManager.enableFog();
    }
    
    @Override
    protected int a(final double double1) {
        return super.a(double1) + 1;
    }
    
    @Override
    protected float c() {
        return 1.0f;
    }
    
    static {
        BEAM_TEXTURE = new Identifier("textures/entity/end_gateway_beam.png");
    }
}

package net.minecraft.client.render.block.entity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.render.Tessellator;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BeaconBlockEntity;

@Environment(EnvType.CLIENT)
public class BeaconBlockEntityRenderer extends BlockEntityRenderer<BeaconBlockEntity>
{
    private static final Identifier BEAM_TEX;
    
    @Override
    public void render(final BeaconBlockEntity entity, final double xOffset, final double yOffset, final double zOffset, final float tickDelta, final int blockBreakStage) {
        this.render(xOffset, yOffset, zOffset, tickDelta, entity.getBeamSegments(), entity.getWorld().getTime());
    }
    
    private void render(final double double1, final double double3, final double double5, final double double7, final List<BeaconBlockEntity.BeamSegment> list9, final long long10) {
        GlStateManager.alphaFunc(516, 0.1f);
        this.bindTexture(BeaconBlockEntityRenderer.BEAM_TEX);
        GlStateManager.disableFog();
        int integer12 = 0;
        for (int integer13 = 0; integer13 < list9.size(); ++integer13) {
            final BeaconBlockEntity.BeamSegment beamSegment14 = list9.get(integer13);
            renderBeaconLightBeam(double1, double3, double5, double7, long10, integer12, (integer13 == list9.size() - 1) ? 1024 : beamSegment14.getHeight(), beamSegment14.getColor());
            integer12 += beamSegment14.getHeight();
        }
        GlStateManager.enableFog();
    }
    
    private static void renderBeaconLightBeam(final double x, final double y, final double z, final double tickDelta, final long long9, final int integer11, final int integer12, final float[] arr13) {
        renderLightBeam(x, y, z, tickDelta, 1.0, long9, integer11, integer12, arr13, 0.2, 0.25);
    }
    
    public static void renderLightBeam(final double x, final double y, final double z, final double tickDelta, final double textureOffset, final long worldTime, final int startHeight, final int height, final float[] beamColor, final double innerRadius, final double outerRadius) {
        final int integer20 = startHeight + height;
        GlStateManager.texParameter(3553, 10242, 10497);
        GlStateManager.texParameter(3553, 10243, 10497);
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.pushMatrix();
        GlStateManager.translated(x + 0.5, y, z + 0.5);
        final Tessellator tessellator21 = Tessellator.getInstance();
        final BufferBuilder bufferBuilder22 = tessellator21.getBufferBuilder();
        final double double23 = Math.floorMod(worldTime, 40L) + tickDelta;
        final double double24 = (height < 0) ? double23 : (-double23);
        final double double25 = MathHelper.fractionalPart(double24 * 0.2 - MathHelper.floor(double24 * 0.1));
        final float float29 = beamColor[0];
        final float float30 = beamColor[1];
        final float float31 = beamColor[2];
        GlStateManager.pushMatrix();
        GlStateManager.rotated(double23 * 2.25 - 45.0, 0.0, 1.0, 0.0);
        double double26 = 0.0;
        double double27 = innerRadius;
        double double28 = innerRadius;
        double double29 = 0.0;
        double double30 = -innerRadius;
        double double31 = 0.0;
        double double32 = 0.0;
        double double33 = -innerRadius;
        double double34 = 0.0;
        double double35 = 1.0;
        double double36 = -1.0 + double25;
        double double37 = height * textureOffset * (0.5 / innerRadius) + double36;
        bufferBuilder22.begin(7, VertexFormats.POSITION_UV_COLOR);
        bufferBuilder22.vertex(0.0, integer20, double27).texture(1.0, double37).color(float29, float30, float31, 1.0f).next();
        bufferBuilder22.vertex(0.0, startHeight, double27).texture(1.0, double36).color(float29, float30, float31, 1.0f).next();
        bufferBuilder22.vertex(double28, startHeight, 0.0).texture(0.0, double36).color(float29, float30, float31, 1.0f).next();
        bufferBuilder22.vertex(double28, integer20, 0.0).texture(0.0, double37).color(float29, float30, float31, 1.0f).next();
        bufferBuilder22.vertex(0.0, integer20, double33).texture(1.0, double37).color(float29, float30, float31, 1.0f).next();
        bufferBuilder22.vertex(0.0, startHeight, double33).texture(1.0, double36).color(float29, float30, float31, 1.0f).next();
        bufferBuilder22.vertex(double30, startHeight, 0.0).texture(0.0, double36).color(float29, float30, float31, 1.0f).next();
        bufferBuilder22.vertex(double30, integer20, 0.0).texture(0.0, double37).color(float29, float30, float31, 1.0f).next();
        bufferBuilder22.vertex(double28, integer20, 0.0).texture(1.0, double37).color(float29, float30, float31, 1.0f).next();
        bufferBuilder22.vertex(double28, startHeight, 0.0).texture(1.0, double36).color(float29, float30, float31, 1.0f).next();
        bufferBuilder22.vertex(0.0, startHeight, double33).texture(0.0, double36).color(float29, float30, float31, 1.0f).next();
        bufferBuilder22.vertex(0.0, integer20, double33).texture(0.0, double37).color(float29, float30, float31, 1.0f).next();
        bufferBuilder22.vertex(double30, integer20, 0.0).texture(1.0, double37).color(float29, float30, float31, 1.0f).next();
        bufferBuilder22.vertex(double30, startHeight, 0.0).texture(1.0, double36).color(float29, float30, float31, 1.0f).next();
        bufferBuilder22.vertex(0.0, startHeight, double27).texture(0.0, double36).color(float29, float30, float31, 1.0f).next();
        bufferBuilder22.vertex(0.0, integer20, double27).texture(0.0, double37).color(float29, float30, float31, 1.0f).next();
        tessellator21.draw();
        GlStateManager.popMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.depthMask(false);
        double26 = -outerRadius;
        double27 = -outerRadius;
        double28 = outerRadius;
        double29 = -outerRadius;
        double30 = -outerRadius;
        double31 = outerRadius;
        double32 = outerRadius;
        double33 = outerRadius;
        double34 = 0.0;
        double35 = 1.0;
        double36 = -1.0 + double25;
        double37 = height * textureOffset + double36;
        bufferBuilder22.begin(7, VertexFormats.POSITION_UV_COLOR);
        bufferBuilder22.vertex(double26, integer20, double27).texture(1.0, double37).color(float29, float30, float31, 0.125f).next();
        bufferBuilder22.vertex(double26, startHeight, double27).texture(1.0, double36).color(float29, float30, float31, 0.125f).next();
        bufferBuilder22.vertex(double28, startHeight, double29).texture(0.0, double36).color(float29, float30, float31, 0.125f).next();
        bufferBuilder22.vertex(double28, integer20, double29).texture(0.0, double37).color(float29, float30, float31, 0.125f).next();
        bufferBuilder22.vertex(double32, integer20, double33).texture(1.0, double37).color(float29, float30, float31, 0.125f).next();
        bufferBuilder22.vertex(double32, startHeight, double33).texture(1.0, double36).color(float29, float30, float31, 0.125f).next();
        bufferBuilder22.vertex(double30, startHeight, double31).texture(0.0, double36).color(float29, float30, float31, 0.125f).next();
        bufferBuilder22.vertex(double30, integer20, double31).texture(0.0, double37).color(float29, float30, float31, 0.125f).next();
        bufferBuilder22.vertex(double28, integer20, double29).texture(1.0, double37).color(float29, float30, float31, 0.125f).next();
        bufferBuilder22.vertex(double28, startHeight, double29).texture(1.0, double36).color(float29, float30, float31, 0.125f).next();
        bufferBuilder22.vertex(double32, startHeight, double33).texture(0.0, double36).color(float29, float30, float31, 0.125f).next();
        bufferBuilder22.vertex(double32, integer20, double33).texture(0.0, double37).color(float29, float30, float31, 0.125f).next();
        bufferBuilder22.vertex(double30, integer20, double31).texture(1.0, double37).color(float29, float30, float31, 0.125f).next();
        bufferBuilder22.vertex(double30, startHeight, double31).texture(1.0, double36).color(float29, float30, float31, 0.125f).next();
        bufferBuilder22.vertex(double26, startHeight, double27).texture(0.0, double36).color(float29, float30, float31, 0.125f).next();
        bufferBuilder22.vertex(double26, integer20, double27).texture(0.0, double37).color(float29, float30, float31, 0.125f).next();
        tessellator21.draw();
        GlStateManager.popMatrix();
        GlStateManager.enableLighting();
        GlStateManager.enableTexture();
        GlStateManager.depthMask(true);
    }
    
    @Override
    public boolean a(final BeaconBlockEntity beaconBlockEntity) {
        return true;
    }
    
    static {
        BEAM_TEX = new Identifier("textures/entity/beacon_beam.png");
    }
}

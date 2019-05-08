package net.minecraft.client.render.entity;

import net.minecraft.entity.Entity;
import javax.annotation.Nullable;
import net.minecraft.util.Identifier;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormats;
import java.util.Random;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.Tessellator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.LightningEntity;

@Environment(EnvType.CLIENT)
public class LightningEntityRenderer extends EntityRenderer<LightningEntity>
{
    public LightningEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
    }
    
    @Override
    public void render(final LightningEntity entity, final double x, final double y, final double z, final float yaw, final float tickDelta) {
        final Tessellator tessellator10 = Tessellator.getInstance();
        final BufferBuilder bufferBuilder11 = tessellator10.getBufferBuilder();
        GlStateManager.disableTexture();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        final double[] arr12 = new double[8];
        final double[] arr13 = new double[8];
        double double14 = 0.0;
        double double15 = 0.0;
        final Random random18 = new Random(entity.seed);
        for (int integer19 = 7; integer19 >= 0; --integer19) {
            arr12[integer19] = double14;
            arr13[integer19] = double15;
            double14 += random18.nextInt(11) - 5;
            double15 += random18.nextInt(11) - 5;
        }
        for (int integer20 = 0; integer20 < 4; ++integer20) {
            final Random random19 = new Random(entity.seed);
            for (int integer21 = 0; integer21 < 3; ++integer21) {
                int integer22 = 7;
                int integer23 = 0;
                if (integer21 > 0) {
                    integer22 = 7 - integer21;
                }
                if (integer21 > 0) {
                    integer23 = integer22 - 2;
                }
                double double16 = arr12[integer22] - double14;
                double double17 = arr13[integer22] - double15;
                for (int integer24 = integer22; integer24 >= integer23; --integer24) {
                    final double double18 = double16;
                    final double double19 = double17;
                    if (integer21 == 0) {
                        double16 += random19.nextInt(11) - 5;
                        double17 += random19.nextInt(11) - 5;
                    }
                    else {
                        double16 += random19.nextInt(31) - 15;
                        double17 += random19.nextInt(31) - 15;
                    }
                    bufferBuilder11.begin(5, VertexFormats.POSITION_COLOR);
                    final float float32 = 0.5f;
                    final float float33 = 0.45f;
                    final float float34 = 0.45f;
                    final float float35 = 0.5f;
                    double double20 = 0.1 + integer20 * 0.2;
                    if (integer21 == 0) {
                        double20 *= integer24 * 0.1 + 1.0;
                    }
                    double double21 = 0.1 + integer20 * 0.2;
                    if (integer21 == 0) {
                        double21 *= (integer24 - 1) * 0.1 + 1.0;
                    }
                    for (int integer25 = 0; integer25 < 5; ++integer25) {
                        double double22 = x - double20;
                        double double23 = z - double20;
                        if (integer25 == 1 || integer25 == 2) {
                            double22 += double20 * 2.0;
                        }
                        if (integer25 == 2 || integer25 == 3) {
                            double23 += double20 * 2.0;
                        }
                        double double24 = x - double21;
                        double double25 = z - double21;
                        if (integer25 == 1 || integer25 == 2) {
                            double24 += double21 * 2.0;
                        }
                        if (integer25 == 2 || integer25 == 3) {
                            double25 += double21 * 2.0;
                        }
                        bufferBuilder11.vertex(double24 + double16, y + integer24 * 16, double25 + double17).color(0.45f, 0.45f, 0.5f, 0.3f).next();
                        bufferBuilder11.vertex(double22 + double18, y + (integer24 + 1) * 16, double23 + double19).color(0.45f, 0.45f, 0.5f, 0.3f).next();
                    }
                    tessellator10.draw();
                }
            }
        }
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.enableTexture();
    }
    
    @Nullable
    @Override
    protected Identifier getTexture(final LightningEntity lightningEntity) {
        return null;
    }
}

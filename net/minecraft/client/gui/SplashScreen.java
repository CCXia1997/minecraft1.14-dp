package net.minecraft.client.gui;

import java.io.InputStream;
import net.minecraft.resource.DefaultResourcePack;
import java.io.IOException;
import net.minecraft.client.resource.metadata.TextureResourceMetadata;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.ResourceManager;
import net.minecraft.client.texture.ResourceTexture;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.SystemUtil;
import net.minecraft.client.texture.Texture;
import net.minecraft.resource.ResourceReloadMonitor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class SplashScreen extends Overlay
{
    private static final Identifier LOGO;
    private final MinecraftClient client;
    private final ResourceReloadMonitor reloadMonitor;
    private final Runnable d;
    private final boolean e;
    private float f;
    private long g;
    private long h;
    
    public SplashScreen(final MinecraftClient minecraftClient, final ResourceReloadMonitor resourceReloadMonitor, final Runnable runnable, final boolean boolean4) {
        this.g = -1L;
        this.h = -1L;
        this.client = minecraftClient;
        this.reloadMonitor = resourceReloadMonitor;
        this.d = runnable;
        this.e = boolean4;
    }
    
    public static void a(final MinecraftClient minecraftClient) {
        minecraftClient.getTextureManager().registerTexture(SplashScreen.LOGO, new a());
    }
    
    @Override
    public void render(final int mouseX, final int mouseY, final float delta) {
        final int integer4 = this.client.window.getScaledWidth();
        final int integer5 = this.client.window.getScaledHeight();
        final long long6 = SystemUtil.getMeasuringTimeMs();
        if (this.e && (this.reloadMonitor.isLoadStageComplete() || this.client.currentScreen != null) && this.h == -1L) {
            this.h = long6;
        }
        final float float8 = (this.g > -1L) ? ((long6 - this.g) / 1000.0f) : -1.0f;
        final float float9 = (this.h > -1L) ? ((long6 - this.h) / 500.0f) : -1.0f;
        float float10;
        if (float8 >= 1.0f) {
            if (this.client.currentScreen != null) {
                this.client.currentScreen.render(0, 0, delta);
            }
            final int integer6 = MathHelper.ceil((1.0f - MathHelper.clamp(float8 - 1.0f, 0.0f, 1.0f)) * 255.0f);
            DrawableHelper.fill(0, 0, integer4, integer5, 0xFFFFFF | integer6 << 24);
            float10 = 1.0f - MathHelper.clamp(float8 - 1.0f, 0.0f, 1.0f);
        }
        else if (this.e) {
            if (this.client.currentScreen != null && float9 < 1.0f) {
                this.client.currentScreen.render(mouseX, mouseY, delta);
            }
            final int integer6 = MathHelper.ceil(MathHelper.clamp(float9, 0.15, 1.0) * 255.0);
            DrawableHelper.fill(0, 0, integer4, integer5, 0xFFFFFF | integer6 << 24);
            float10 = MathHelper.clamp(float9, 0.0f, 1.0f);
        }
        else {
            DrawableHelper.fill(0, 0, integer4, integer5, -1);
            float10 = 1.0f;
        }
        final int integer6 = (this.client.window.getScaledWidth() - 256) / 2;
        final int integer7 = (this.client.window.getScaledHeight() - 256) / 2;
        this.client.getTextureManager().bindTexture(SplashScreen.LOGO);
        GlStateManager.enableBlend();
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, float10);
        this.blit(integer6, integer7, 0, 0, 256, 256);
        final float float11 = this.reloadMonitor.getProgress();
        this.f = this.f * 0.95f + float11 * 0.050000012f;
        if (float8 < 1.0f) {
            this.renderProgressBar(integer4 / 2 - 150, integer5 / 4 * 3, integer4 / 2 + 150, integer5 / 4 * 3 + 10, this.f, 1.0f - MathHelper.clamp(float8, 0.0f, 1.0f));
        }
        if (float8 >= 2.0f) {
            this.client.setOverlay(null);
        }
        if (this.g == -1L && this.reloadMonitor.isApplyStageComplete() && (!this.e || float9 >= 2.0f)) {
            this.reloadMonitor.throwExceptions();
            this.g = SystemUtil.getMeasuringTimeMs();
            this.d.run();
            if (this.client.currentScreen != null) {
                this.client.currentScreen.init(this.client, this.client.window.getScaledWidth(), this.client.window.getScaledHeight());
            }
        }
    }
    
    private void renderProgressBar(final int minX, final int minY, final int maxX, final int maxY, final float progress, final float fadeAmount) {
        final int integer7 = MathHelper.ceil((maxX - minX - 2) * progress);
        DrawableHelper.fill(minX - 1, minY - 1, maxX + 1, maxY + 1, 0xFF000000 | Math.round((1.0f - fadeAmount) * 255.0f) << 16 | Math.round((1.0f - fadeAmount) * 255.0f) << 8 | Math.round((1.0f - fadeAmount) * 255.0f));
        DrawableHelper.fill(minX, minY, maxX, maxY, -1);
        DrawableHelper.fill(minX + 1, minY + 1, minX + integer7, maxY - 1, 0xFF000000 | (int)MathHelper.lerp(1.0f - fadeAmount, 226.0f, 255.0f) << 16 | (int)MathHelper.lerp(1.0f - fadeAmount, 40.0f, 255.0f) << 8 | (int)MathHelper.lerp(1.0f - fadeAmount, 55.0f, 255.0f));
    }
    
    @Override
    public boolean pausesGame() {
        return true;
    }
    
    static {
        LOGO = new Identifier("textures/gui/title/mojang.png");
    }
    
    @Environment(EnvType.CLIENT)
    static class a extends ResourceTexture
    {
        public a() {
            super(SplashScreen.LOGO);
        }
        
        @Override
        protected TextureData loadTextureData(final ResourceManager resourceManager) {
            final MinecraftClient minecraftClient2 = MinecraftClient.getInstance();
            final DefaultResourcePack defaultResourcePack3 = minecraftClient2.getResourcePackDownloader().getPack();
            try (final InputStream inputStream4 = defaultResourcePack3.open(ResourceType.ASSETS, SplashScreen.LOGO)) {
                return new TextureData(null, NativeImage.fromInputStream(inputStream4));
            }
            catch (IOException iOException4) {
                return new TextureData(iOException4);
            }
        }
    }
}

package net.minecraft.client.gui.menu;

import org.apache.logging.log4j.LogManager;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.gui.DrawableHelper;
import java.io.InputStream;
import net.minecraft.resource.Resource;
import java.io.Closeable;
import org.apache.commons.io.IOUtils;
import java.util.Collection;
import java.util.Random;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import net.minecraft.text.TextFormat;
import com.google.common.collect.Lists;
import net.minecraft.client.util.NarratorManager;
import java.util.List;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Logger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;

@Environment(EnvType.CLIENT)
public class EndCreditsScreen extends Screen
{
    private static final Logger LOGGER;
    private static final Identifier MINECRAFT_TITLE_TEXTURE;
    private static final Identifier EDITION_TITLE_TEXTURE;
    private static final Identifier VIGNETTE_TEXTURE;
    private final boolean inGame;
    private final Runnable respawn;
    private float g;
    private List<String> h;
    private int i;
    private float j;
    
    public EndCreditsScreen(final boolean inGame, final Runnable respawn) {
        super(NarratorManager.a);
        this.j = 0.5f;
        this.inGame = inGame;
        this.respawn = respawn;
        if (!inGame) {
            this.j = 0.75f;
        }
    }
    
    @Override
    public void tick() {
        this.minecraft.getMusicTracker().tick();
        this.minecraft.getSoundManager().tick(false);
        final float float1 = (this.i + this.height + this.height + 24) / this.j;
        if (this.g > float1) {
            this.respawn();
        }
    }
    
    @Override
    public void onClose() {
        this.respawn();
    }
    
    private void respawn() {
        this.respawn.run();
        this.minecraft.openScreen(null);
    }
    
    @Override
    protected void init() {
        if (this.h != null) {
            return;
        }
        this.h = Lists.newArrayList();
        Resource resource1 = null;
        try {
            final String string2 = "" + TextFormat.p + TextFormat.q + TextFormat.k + TextFormat.l;
            final int integer3 = 274;
            if (this.inGame) {
                resource1 = this.minecraft.getResourceManager().getResource(new Identifier("texts/end.txt"));
                final InputStream inputStream4 = resource1.getInputStream();
                final BufferedReader bufferedReader5 = new BufferedReader(new InputStreamReader(inputStream4, StandardCharsets.UTF_8));
                final Random random6 = new Random(8124371L);
                String string3;
                while ((string3 = bufferedReader5.readLine()) != null) {
                    String string4;
                    String string5;
                    for (string3 = string3.replaceAll("PLAYERNAME", this.minecraft.getSession().getUsername()); string3.contains(string2); string3 = string4 + TextFormat.p + TextFormat.q + "XXXXXXXX".substring(0, random6.nextInt(4) + 3) + string5) {
                        final int integer4 = string3.indexOf(string2);
                        string4 = string3.substring(0, integer4);
                        string5 = string3.substring(integer4 + string2.length());
                    }
                    this.h.addAll(this.minecraft.textRenderer.wrapStringToWidthAsList(string3, 274));
                    this.h.add("");
                }
                inputStream4.close();
                for (int integer4 = 0; integer4 < 8; ++integer4) {
                    this.h.add("");
                }
            }
            final InputStream inputStream4 = this.minecraft.getResourceManager().getResource(new Identifier("texts/credits.txt")).getInputStream();
            final BufferedReader bufferedReader5 = new BufferedReader(new InputStreamReader(inputStream4, StandardCharsets.UTF_8));
            String string6;
            while ((string6 = bufferedReader5.readLine()) != null) {
                string6 = string6.replaceAll("PLAYERNAME", this.minecraft.getSession().getUsername());
                string6 = string6.replaceAll("\t", "    ");
                this.h.addAll(this.minecraft.textRenderer.wrapStringToWidthAsList(string6, 274));
                this.h.add("");
            }
            inputStream4.close();
            this.i = this.h.size() * 12;
        }
        catch (Exception exception2) {
            EndCreditsScreen.LOGGER.error("Couldn't load credits", (Throwable)exception2);
        }
        finally {
            IOUtils.closeQuietly((Closeable)resource1);
        }
    }
    
    private void a(final int integer1, final int integer2, final float float3) {
        this.minecraft.getTextureManager().bindTexture(DrawableHelper.BACKGROUND_LOCATION);
        final int integer3 = this.width;
        final float float4 = -this.g * 0.5f * this.j;
        final float float5 = this.height - this.g * 0.5f * this.j;
        final float float6 = 0.015625f;
        float float7 = this.g * 0.02f;
        final float float8 = (this.i + this.height + this.height + 24) / this.j;
        final float float9 = (float8 - 20.0f - this.g) * 0.005f;
        if (float9 < float7) {
            float7 = float9;
        }
        if (float7 > 1.0f) {
            float7 = 1.0f;
        }
        float7 *= float7;
        float7 = float7 * 96.0f / 255.0f;
        final Tessellator tessellator11 = Tessellator.getInstance();
        final BufferBuilder bufferBuilder12 = tessellator11.getBufferBuilder();
        bufferBuilder12.begin(7, VertexFormats.POSITION_UV_COLOR);
        bufferBuilder12.vertex(0.0, this.height, this.blitOffset).texture(0.0, float4 * 0.015625f).color(float7, float7, float7, 1.0f).next();
        bufferBuilder12.vertex(integer3, this.height, this.blitOffset).texture(integer3 * 0.015625f, float4 * 0.015625f).color(float7, float7, float7, 1.0f).next();
        bufferBuilder12.vertex(integer3, 0.0, this.blitOffset).texture(integer3 * 0.015625f, float5 * 0.015625f).color(float7, float7, float7, 1.0f).next();
        bufferBuilder12.vertex(0.0, 0.0, this.blitOffset).texture(0.0, float5 * 0.015625f).color(float7, float7, float7, 1.0f).next();
        tessellator11.draw();
    }
    
    @Override
    public void render(final int mouseX, final int mouseY, final float delta) {
        this.a(mouseX, mouseY, delta);
        final int integer4 = 274;
        final int integer5 = this.width / 2 - 137;
        final int integer6 = this.height + 50;
        this.g += delta;
        final float float7 = -this.g * this.j;
        GlStateManager.pushMatrix();
        GlStateManager.translatef(0.0f, float7, 0.0f);
        this.minecraft.getTextureManager().bindTexture(EndCreditsScreen.MINECRAFT_TITLE_TEXTURE);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.enableAlphaTest();
        this.blit(integer5, integer6, 0, 0, 155, 44);
        this.blit(integer5 + 155, integer6, 0, 45, 155, 44);
        this.minecraft.getTextureManager().bindTexture(EndCreditsScreen.EDITION_TITLE_TEXTURE);
        DrawableHelper.blit(integer5 + 88, integer6 + 37, 0.0f, 0.0f, 98, 14, 128, 16);
        GlStateManager.disableAlphaTest();
        int integer7 = integer6 + 100;
        for (int integer8 = 0; integer8 < this.h.size(); ++integer8) {
            if (integer8 == this.h.size() - 1) {
                final float float8 = integer7 + float7 - (this.height / 2 - 6);
                if (float8 < 0.0f) {
                    GlStateManager.translatef(0.0f, -float8, 0.0f);
                }
            }
            if (integer7 + float7 + 12.0f + 8.0f > 0.0f && integer7 + float7 < this.height) {
                final String string10 = this.h.get(integer8);
                if (string10.startsWith("[C]")) {
                    this.font.drawWithShadow(string10.substring(3), (float)(integer5 + (274 - this.font.getStringWidth(string10.substring(3))) / 2), (float)integer7, 16777215);
                }
                else {
                    this.font.random.setSeed((long)(integer8 * 4238972211L + this.g / 4.0f));
                    this.font.drawWithShadow(string10, (float)integer5, (float)integer7, 16777215);
                }
            }
            integer7 += 12;
        }
        GlStateManager.popMatrix();
        this.minecraft.getTextureManager().bindTexture(EndCreditsScreen.VIGNETTE_TEXTURE);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR);
        int integer8 = this.width;
        final int integer9 = this.height;
        final Tessellator tessellator11 = Tessellator.getInstance();
        final BufferBuilder bufferBuilder12 = tessellator11.getBufferBuilder();
        bufferBuilder12.begin(7, VertexFormats.POSITION_UV_COLOR);
        bufferBuilder12.vertex(0.0, integer9, this.blitOffset).texture(0.0, 1.0).color(1.0f, 1.0f, 1.0f, 1.0f).next();
        bufferBuilder12.vertex(integer8, integer9, this.blitOffset).texture(1.0, 1.0).color(1.0f, 1.0f, 1.0f, 1.0f).next();
        bufferBuilder12.vertex(integer8, 0.0, this.blitOffset).texture(1.0, 0.0).color(1.0f, 1.0f, 1.0f, 1.0f).next();
        bufferBuilder12.vertex(0.0, 0.0, this.blitOffset).texture(0.0, 0.0).color(1.0f, 1.0f, 1.0f, 1.0f).next();
        tessellator11.draw();
        GlStateManager.disableBlend();
        super.render(mouseX, mouseY, delta);
    }
    
    static {
        LOGGER = LogManager.getLogger();
        MINECRAFT_TITLE_TEXTURE = new Identifier("textures/gui/title/minecraft.png");
        EDITION_TITLE_TEXTURE = new Identifier("textures/gui/title/edition.png");
        VIGNETTE_TEXTURE = new Identifier("textures/misc/vignette.png");
    }
}

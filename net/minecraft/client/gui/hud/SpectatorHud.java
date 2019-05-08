package net.minecraft.client.gui.hud;

import net.minecraft.client.gui.hud.spectator.SpectatorMenuCommand;
import net.minecraft.client.render.GuiLighting;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuState;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.SystemUtil;
import net.minecraft.client.gui.hud.spectator.SpectatorMenu;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuCloseCallback;
import net.minecraft.client.gui.DrawableHelper;

@Environment(EnvType.CLIENT)
public class SpectatorHud extends DrawableHelper implements SpectatorMenuCloseCallback
{
    private static final Identifier WIDGETS_TEX;
    public static final Identifier SPECTATOR_TEX;
    private final MinecraftClient client;
    private long lastKeyPressTime;
    private SpectatorMenu spectatorMenu;
    
    public SpectatorHud(final MinecraftClient client) {
        this.client = client;
    }
    
    public void onHotbarKeyPress(final int slot) {
        this.lastKeyPressTime = SystemUtil.getMeasuringTimeMs();
        if (this.spectatorMenu != null) {
            this.spectatorMenu.setSelectedSlot(slot);
        }
        else {
            this.spectatorMenu = new SpectatorMenu(this);
        }
    }
    
    private float getSpectatorMenuHeight() {
        final long long1 = this.lastKeyPressTime - SystemUtil.getMeasuringTimeMs() + 5000L;
        return MathHelper.clamp(long1 / 2000.0f, 0.0f, 1.0f);
    }
    
    public void draw(final float float1) {
        if (this.spectatorMenu == null) {
            return;
        }
        final float float2 = this.getSpectatorMenuHeight();
        if (float2 <= 0.0f) {
            this.spectatorMenu.close();
            return;
        }
        final int integer3 = this.client.window.getScaledWidth() / 2;
        final int integer4 = this.blitOffset;
        this.blitOffset = -90;
        final int integer5 = MathHelper.floor(this.client.window.getScaledHeight() - 22.0f * float2);
        final SpectatorMenuState spectatorMenuState6 = this.spectatorMenu.getCurrentState();
        this.drawSpectatorMenu(float2, integer3, integer5, spectatorMenuState6);
        this.blitOffset = integer4;
    }
    
    protected void drawSpectatorMenu(final float height, final int x, final int integer3, final SpectatorMenuState state) {
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, height);
        this.client.getTextureManager().bindTexture(SpectatorHud.WIDGETS_TEX);
        this.blit(x - 91, integer3, 0, 0, 182, 22);
        if (state.getSelectedSlot() >= 0) {
            this.blit(x - 91 - 1 + state.getSelectedSlot() * 20, integer3 - 1, 0, 22, 24, 22);
        }
        GuiLighting.enableForItems();
        for (int integer4 = 0; integer4 < 9; ++integer4) {
            this.drawSpectatorCommand(integer4, this.client.window.getScaledWidth() / 2 - 90 + integer4 * 20 + 2, (float)(integer3 + 3), height, state.getCommand(integer4));
        }
        GuiLighting.disable();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
    }
    
    private void drawSpectatorCommand(final int slot, final int x, final float y, final float alpha, final SpectatorMenuCommand command) {
        this.client.getTextureManager().bindTexture(SpectatorHud.SPECTATOR_TEX);
        if (command != SpectatorMenu.BLANK_COMMAND) {
            final int integer6 = (int)(alpha * 255.0f);
            GlStateManager.pushMatrix();
            GlStateManager.translatef((float)x, y, 0.0f);
            final float float7 = command.enabled() ? 1.0f : 0.25f;
            GlStateManager.color4f(float7, float7, float7, alpha);
            command.renderIcon(float7, integer6);
            GlStateManager.popMatrix();
            final String string8 = String.valueOf(this.client.options.keysHotbar[slot].getLocalizedName());
            if (integer6 > 3 && command.enabled()) {
                this.client.textRenderer.drawWithShadow(string8, (float)(x + 19 - 2 - this.client.textRenderer.getStringWidth(string8)), y + 6.0f + 3.0f, 16777215 + (integer6 << 24));
            }
        }
    }
    
    public void draw() {
        final int integer1 = (int)(this.getSpectatorMenuHeight() * 255.0f);
        if (integer1 > 3 && this.spectatorMenu != null) {
            final SpectatorMenuCommand spectatorMenuCommand2 = this.spectatorMenu.getSelectedCommand();
            final String string3 = (spectatorMenuCommand2 == SpectatorMenu.BLANK_COMMAND) ? this.spectatorMenu.getCurrentGroup().getPrompt().getFormattedText() : spectatorMenuCommand2.getName().getFormattedText();
            if (string3 != null) {
                final int integer2 = (this.client.window.getScaledWidth() - this.client.textRenderer.getStringWidth(string3)) / 2;
                final int integer3 = this.client.window.getScaledHeight() - 35;
                GlStateManager.pushMatrix();
                GlStateManager.enableBlend();
                GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                this.client.textRenderer.drawWithShadow(string3, (float)integer2, (float)integer3, 16777215 + (integer1 << 24));
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
            }
        }
    }
    
    @Override
    public void close(final SpectatorMenu menu) {
        this.spectatorMenu = null;
        this.lastKeyPressTime = 0L;
    }
    
    public boolean b() {
        return this.spectatorMenu != null;
    }
    
    public void a(final double double1) {
        int integer3;
        for (integer3 = this.spectatorMenu.getSelectedSlot() + (int)double1; integer3 >= 0 && integer3 <= 8 && (this.spectatorMenu.getCommand(integer3) == SpectatorMenu.BLANK_COMMAND || !this.spectatorMenu.getCommand(integer3).enabled()); integer3 += (int)double1) {}
        if (integer3 >= 0 && integer3 <= 8) {
            this.spectatorMenu.setSelectedSlot(integer3);
            this.lastKeyPressTime = SystemUtil.getMeasuringTimeMs();
        }
    }
    
    public void c() {
        this.lastKeyPressTime = SystemUtil.getMeasuringTimeMs();
        if (this.b()) {
            final int integer1 = this.spectatorMenu.getSelectedSlot();
            if (integer1 != -1) {
                this.spectatorMenu.setSelectedSlot(integer1);
            }
        }
        else {
            this.spectatorMenu = new SpectatorMenu(this);
        }
    }
    
    static {
        WIDGETS_TEX = new Identifier("textures/gui/widgets.png");
        SPECTATOR_TEX = new Identifier("textures/gui/spectator_widgets.png");
    }
}

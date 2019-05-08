package net.minecraft.client.gui.widget;

import net.minecraft.client.audio.SoundManager;
import net.minecraft.util.math.MathHelper;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.GameOptions;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class SliderWidget extends AbstractButtonWidget
{
    protected final GameOptions options;
    protected double value;
    
    protected SliderWidget(final int x, final int y, final int width, final int height, final double progress) {
        this(MinecraftClient.getInstance().options, x, y, width, height, progress);
    }
    
    protected SliderWidget(final GameOptions gameOptions, final int x, final int y, final int width, final int height, final double progress) {
        super(x, y, width, height, "");
        this.options = gameOptions;
        this.value = progress;
    }
    
    @Override
    protected int getYImage(final boolean isHovered) {
        return 0;
    }
    
    @Override
    protected String getNarrationMessage() {
        return I18n.translate("gui.narrate.slider", this.getMessage());
    }
    
    @Override
    protected void renderBg(final MinecraftClient client, final int mouseX, final int mouseY) {
        client.getTextureManager().bindTexture(SliderWidget.WIDGETS_LOCATION);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        final int integer4 = (this.isHovered() ? 2 : 1) * 20;
        this.blit(this.x + (int)(this.value * (this.width - 8)), this.y, 0, 46 + integer4, 4, 20);
        this.blit(this.x + (int)(this.value * (this.width - 8)) + 4, this.y, 196, 46 + integer4, 4, 20);
    }
    
    @Override
    public void onClick(final double mouseX, final double mouseY) {
        this.setValueFromMouse(mouseX);
    }
    
    @Override
    public boolean keyPressed(final int keyCode, final int scanCode, final int modifiers) {
        final boolean boolean4 = keyCode == 263;
        if (boolean4 || keyCode == 262) {
            final float float5 = boolean4 ? -1.0f : 1.0f;
            this.setValue(this.value + float5 / (this.width - 8));
        }
        return false;
    }
    
    private void setValueFromMouse(final double double1) {
        this.setValue((double1 - (this.x + 4)) / (this.width - 8));
    }
    
    private void setValue(final double mouseX) {
        final double double3 = this.value;
        this.value = MathHelper.clamp(mouseX, 0.0, 1.0);
        if (double3 != this.value) {
            this.applyValue();
        }
        this.updateMessage();
    }
    
    @Override
    protected void onDrag(final double mouseX, final double mouseY, final double deltaX, final double deltaY) {
        this.setValueFromMouse(mouseX);
        super.onDrag(mouseX, mouseY, deltaX, deltaY);
    }
    
    @Override
    public void playDownSound(final SoundManager soundManager) {
    }
    
    @Override
    public void onRelease(final double mouseX, final double mouseY) {
        super.playDownSound(MinecraftClient.getInstance().getSoundManager());
    }
    
    protected abstract void updateMessage();
    
    protected abstract void applyValue();
}

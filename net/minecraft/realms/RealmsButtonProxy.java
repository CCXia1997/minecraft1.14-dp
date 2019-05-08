package net.minecraft.realms;

import net.minecraft.client.MinecraftClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonWidget;

@Environment(EnvType.CLIENT)
public class RealmsButtonProxy extends ButtonWidget implements RealmsAbstractButtonProxy<RealmsButton>
{
    private final RealmsButton button;
    
    public RealmsButtonProxy(final RealmsButton button, final int x, final int y, final String text, final int width, final int height, final PressAction pressAction) {
        super(x, y, width, height, text, pressAction);
        this.button = button;
    }
    
    @Override
    public boolean active() {
        return this.active;
    }
    
    @Override
    public void active(final boolean enabled) {
        this.active = enabled;
    }
    
    @Override
    public boolean isVisible() {
        return this.visible;
    }
    
    @Override
    public void setVisible(final boolean visible) {
        this.visible = visible;
    }
    
    @Override
    public void setMessage(final String value) {
        super.setMessage(value);
    }
    
    @Override
    public int getWidth() {
        return super.getWidth();
    }
    
    public int y() {
        return this.y;
    }
    
    @Override
    public void onClick(final double mouseX, final double mouseY) {
        this.button.onPress();
    }
    
    @Override
    public void onRelease(final double mouseX, final double mouseY) {
        this.button.onRelease(mouseX, mouseY);
    }
    
    public void renderBg(final MinecraftClient client, final int mouseX, final int mouseY) {
        this.button.renderBg(mouseX, mouseY);
    }
    
    @Override
    public void renderButton(final int mouseX, final int mouseY, final float delta) {
        this.button.renderButton(mouseX, mouseY, delta);
    }
    
    public void superRenderButton(final int integer1, final int integer2, final float float3) {
        super.renderButton(integer1, integer2, float3);
    }
    
    @Override
    public RealmsButton getButton() {
        return this.button;
    }
    
    public int getYImage(final boolean isHovered) {
        return this.button.getYImage(isHovered);
    }
    
    public int getSuperYImage(final boolean boolean1) {
        return super.getYImage(boolean1);
    }
    
    public int getHeight() {
        return this.height;
    }
    
    @Override
    public boolean isHovered() {
        return super.isHovered();
    }
}

package net.minecraft.realms;

import net.minecraft.client.MinecraftClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.SliderWidget;

@Environment(EnvType.CLIENT)
public class RealmsSliderButtonProxy extends SliderWidget implements RealmsAbstractButtonProxy<RealmsSliderButton>
{
    private final RealmsSliderButton button;
    
    public RealmsSliderButtonProxy(final RealmsSliderButton realmsSliderButton, final int integer2, final int integer3, final int integer4, final int integer5, final double double6) {
        super(integer2, integer3, integer4, integer5, double6);
        this.button = realmsSliderButton;
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
        this.button.onClick(mouseX, mouseY);
    }
    
    @Override
    public void onRelease(final double mouseX, final double mouseY) {
        this.button.onRelease(mouseX, mouseY);
    }
    
    public void updateMessage() {
        this.button.updateMessage();
    }
    
    public void applyValue() {
        this.button.applyValue();
    }
    
    public double getValue() {
        return this.value;
    }
    
    public void setValue(final double double1) {
        this.value = double1;
    }
    
    public void renderBg(final MinecraftClient client, final int mouseX, final int mouseY) {
        super.renderBg(client, mouseX, mouseY);
    }
    
    @Override
    public RealmsSliderButton getButton() {
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
}

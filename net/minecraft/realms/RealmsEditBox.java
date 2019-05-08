package net.minecraft.realms;

import net.minecraft.client.gui.Element;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class RealmsEditBox extends RealmsGuiEventListener
{
    private final TextFieldWidget editBox;
    
    public RealmsEditBox(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5, final String string) {
        this.editBox = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, integer2, integer3, integer4, integer5, null, string);
    }
    
    public String getValue() {
        return this.editBox.getText();
    }
    
    public void tick() {
        this.editBox.tick();
    }
    
    public void setValue(final String string) {
        this.editBox.setText(string);
    }
    
    @Override
    public boolean charTyped(final char character, final int integer) {
        return this.editBox.charTyped(character, integer);
    }
    
    @Override
    public Element getProxy() {
        return this.editBox;
    }
    
    @Override
    public boolean keyPressed(final int integer1, final int integer2, final int integer3) {
        return this.editBox.keyPressed(integer1, integer2, integer3);
    }
    
    public boolean isFocused() {
        return this.editBox.isFocused();
    }
    
    @Override
    public boolean mouseClicked(final double double1, final double double3, final int integer5) {
        return this.editBox.mouseClicked(double1, double3, integer5);
    }
    
    @Override
    public boolean mouseReleased(final double double1, final double double3, final int integer5) {
        return this.editBox.mouseReleased(double1, double3, integer5);
    }
    
    @Override
    public boolean mouseDragged(final double double1, final double double3, final int integer, final double double6, final double double8) {
        return this.editBox.mouseDragged(double1, double3, integer, double6, double8);
    }
    
    @Override
    public boolean mouseScrolled(final double double1, final double double3, final double double5) {
        return this.editBox.mouseScrolled(double1, double3, double5);
    }
    
    public void render(final int integer1, final int integer2, final float float3) {
        this.editBox.render(integer1, integer2, float3);
    }
    
    public void setMaxLength(final int integer) {
        this.editBox.setMaxLength(integer);
    }
    
    public void setIsEditable(final boolean boolean1) {
        this.editBox.setIsEditable(boolean1);
    }
}

package net.minecraft.realms;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class RealmsButton extends AbstractRealmsButton<RealmsButtonProxy>
{
    protected static final Identifier WIDGETS_LOCATION;
    private final int id;
    private final RealmsButtonProxy proxy;
    
    public RealmsButton(final int integer1, final int integer2, final int integer3, final String string) {
        this(integer1, integer2, integer3, 200, 20, string);
    }
    
    public RealmsButton(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5, final String string) {
        this.id = integer1;
        this.proxy = new RealmsButtonProxy(this, integer2, integer3, string, integer4, integer5, buttonWidget -> this.onPress());
    }
    
    @Override
    public RealmsButtonProxy getProxy() {
        return this.proxy;
    }
    
    public int id() {
        return this.id;
    }
    
    public void setMessage(final String string) {
        this.proxy.setMessage(string);
    }
    
    public int getWidth() {
        return this.proxy.getWidth();
    }
    
    public int getHeight() {
        return this.proxy.getHeight();
    }
    
    public int y() {
        return this.proxy.y();
    }
    
    public int x() {
        return this.proxy.x;
    }
    
    public void renderBg(final int integer1, final int integer2) {
    }
    
    public int getYImage(final boolean boolean1) {
        return this.proxy.getSuperYImage(boolean1);
    }
    
    public abstract void onPress();
    
    public void onRelease(final double double1, final double double3) {
    }
    
    public void renderButton(final int integer1, final int integer2, final float float3) {
        this.getProxy().superRenderButton(integer1, integer2, float3);
    }
    
    public void drawCenteredString(final String string, final int integer2, final int integer3, final int integer4) {
        this.getProxy().drawCenteredString(MinecraftClient.getInstance().textRenderer, string, integer2, integer3, integer4);
    }
    
    static {
        WIDGETS_LOCATION = new Identifier("textures/gui/widgets.png");
    }
}

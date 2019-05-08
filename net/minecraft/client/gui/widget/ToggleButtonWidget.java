package net.minecraft.client.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ToggleButtonWidget extends AbstractButtonWidget
{
    protected Identifier texture;
    protected boolean toggled;
    protected int u;
    protected int v;
    protected int pressedUOffset;
    protected int hoverVOffset;
    
    public ToggleButtonWidget(final int id, final int left, final int top, final int width, final boolean boolean5) {
        super(id, left, top, width, "");
        this.toggled = boolean5;
    }
    
    public void setTextureUV(final int u, final int v, final int pressedUOffset, final int hoverVOffset, final Identifier texture) {
        this.u = u;
        this.v = v;
        this.pressedUOffset = pressedUOffset;
        this.hoverVOffset = hoverVOffset;
        this.texture = texture;
    }
    
    public void setToggled(final boolean boolean1) {
        this.toggled = boolean1;
    }
    
    public boolean isToggled() {
        return this.toggled;
    }
    
    public void setPos(final int x, final int y) {
        this.x = x;
        this.y = y;
    }
    
    @Override
    public void renderButton(final int mouseX, final int mouseY, final float delta) {
        final MinecraftClient minecraftClient4 = MinecraftClient.getInstance();
        minecraftClient4.getTextureManager().bindTexture(this.texture);
        GlStateManager.disableDepthTest();
        int integer5 = this.u;
        int integer6 = this.v;
        if (this.toggled) {
            integer5 += this.pressedUOffset;
        }
        if (this.isHovered()) {
            integer6 += this.hoverVOffset;
        }
        this.blit(this.x, this.y, integer5, integer6, this.width, this.height);
        GlStateManager.enableDepthTest();
    }
}

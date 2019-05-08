package net.minecraft.client.gui.widget;

import net.minecraft.client.gui.DrawableHelper;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class RecipeBookButtonWidget extends ButtonWidget
{
    private final Identifier texture;
    private final int u;
    private final int v;
    private final int d;
    private final int hoverVOffset;
    private final int f;
    
    public RecipeBookButtonWidget(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6, final int integer7, final Identifier identifier, final PressAction pressAction) {
        this(integer1, integer2, integer3, integer4, integer5, integer6, integer7, identifier, 256, 256, pressAction);
    }
    
    public RecipeBookButtonWidget(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6, final int integer7, final Identifier identifier, final int integer9, final int integer10, final PressAction pressAction) {
        this(integer1, integer2, integer3, integer4, integer5, integer6, integer7, identifier, integer9, integer10, pressAction, "");
    }
    
    public RecipeBookButtonWidget(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6, final int integer7, final Identifier identifier, final int integer9, final int integer10, final PressAction pressAction, final String string) {
        super(integer1, integer2, integer3, integer4, string, pressAction);
        this.hoverVOffset = integer9;
        this.f = integer10;
        this.u = integer5;
        this.v = integer6;
        this.d = integer7;
        this.texture = identifier;
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
        int integer5 = this.v;
        if (this.isHovered()) {
            integer5 += this.d;
        }
        DrawableHelper.blit(this.x, this.y, (float)this.u, (float)integer5, this.width, this.height, this.hoverVOffset, this.f);
        GlStateManager.enableDepthTest();
    }
}

package net.minecraft.client.gui.widget;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.gui.DrawableHelper;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class CheckboxWidget extends AbstractPressableButtonWidget
{
    private static final Identifier TEXTURE;
    boolean checked;
    
    public CheckboxWidget(final int x, final int y, final int width, final int height, final String message, final boolean checked) {
        super(x, y, width, height, message);
        this.checked = checked;
    }
    
    @Override
    public void onPress() {
        this.checked = !this.checked;
    }
    
    public boolean isChecked() {
        return this.checked;
    }
    
    @Override
    public void renderButton(final int mouseX, final int mouseY, final float delta) {
        final MinecraftClient minecraftClient4 = MinecraftClient.getInstance();
        minecraftClient4.getTextureManager().bindTexture(CheckboxWidget.TEXTURE);
        GlStateManager.enableDepthTest();
        final TextRenderer textRenderer5 = minecraftClient4.textRenderer;
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, this.alpha);
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        DrawableHelper.blit(this.x, this.y, 0.0f, this.checked ? 20.0f : 0.0f, 20, this.height, 32, 64);
        this.renderBg(minecraftClient4, mouseX, mouseY);
        final int integer6 = 14737632;
        this.drawString(textRenderer5, this.getMessage(), this.x + 24, this.y + (this.height - 8) / 2, 0xE0E0E0 | MathHelper.ceil(this.alpha * 255.0f) << 24);
    }
    
    static {
        TEXTURE = new Identifier("textures/gui/checkbox.png");
    }
}

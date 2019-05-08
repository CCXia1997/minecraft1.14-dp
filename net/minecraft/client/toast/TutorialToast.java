package net.minecraft.client.toast;

import net.minecraft.util.math.MathHelper;
import net.minecraft.client.gui.DrawableHelper;
import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.minecraft.text.TextComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class TutorialToast implements Toast
{
    private final Type type;
    private final String title;
    private final String description;
    private Visibility visibility;
    private long lastTime;
    private float lastProgress;
    private float progress;
    private final boolean hasProgressBar;
    
    public TutorialToast(final Type type, final TextComponent textComponent2, @Nullable final TextComponent textComponent3, final boolean hasProgressBar) {
        this.visibility = Visibility.a;
        this.type = type;
        this.title = textComponent2.getFormattedText();
        this.description = ((textComponent3 == null) ? null : textComponent3.getFormattedText());
        this.hasProgressBar = hasProgressBar;
    }
    
    @Override
    public Visibility draw(final ToastManager manager, final long currentTime) {
        manager.getGame().getTextureManager().bindTexture(TutorialToast.TOASTS_TEX);
        GlStateManager.color3f(1.0f, 1.0f, 1.0f);
        manager.blit(0, 0, 0, 96, 160, 32);
        this.type.drawIcon(manager, 6, 6);
        if (this.description == null) {
            manager.getGame().textRenderer.draw(this.title, 30.0f, 12.0f, -11534256);
        }
        else {
            manager.getGame().textRenderer.draw(this.title, 30.0f, 7.0f, -11534256);
            manager.getGame().textRenderer.draw(this.description, 30.0f, 18.0f, -16777216);
        }
        if (this.hasProgressBar) {
            DrawableHelper.fill(3, 28, 157, 29, -1);
            final float float4 = (float)MathHelper.clampedLerp(this.lastProgress, this.progress, (currentTime - this.lastTime) / 100.0f);
            int integer5;
            if (this.progress >= this.lastProgress) {
                integer5 = -16755456;
            }
            else {
                integer5 = -11206656;
            }
            DrawableHelper.fill(3, 28, (int)(3.0f + 154.0f * float4), 29, integer5);
            this.lastProgress = float4;
            this.lastTime = currentTime;
        }
        return this.visibility;
    }
    
    public void hide() {
        this.visibility = Visibility.b;
    }
    
    public void setProgress(final float progress) {
        this.progress = progress;
    }
    
    @Environment(EnvType.CLIENT)
    public enum Type
    {
        a(0, 0), 
        b(1, 0), 
        c(2, 0), 
        d(0, 1), 
        e(1, 1);
        
        private final int textureSlotX;
        private final int textureSlotY;
        
        private Type(final int integer1, final int integer2) {
            this.textureSlotX = integer1;
            this.textureSlotY = integer2;
        }
        
        public void drawIcon(final DrawableHelper drawableHelper, final int integer2, final int integer3) {
            GlStateManager.enableBlend();
            drawableHelper.blit(integer2, integer3, 176 + this.textureSlotX * 20, this.textureSlotY * 20, 20, 20);
            GlStateManager.enableBlend();
        }
    }
}

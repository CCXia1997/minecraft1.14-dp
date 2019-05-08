package net.minecraft.client.toast;

import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.minecraft.text.TextComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class SystemToast implements Toast
{
    private final Type c;
    private String d;
    private String e;
    private long startTime;
    private boolean justUpdated;
    
    public SystemToast(final Type type, final TextComponent textComponent2, @Nullable final TextComponent textComponent3) {
        this.c = type;
        this.d = textComponent2.getString();
        this.e = ((textComponent3 == null) ? null : textComponent3.getString());
    }
    
    @Override
    public Visibility draw(final ToastManager manager, final long currentTime) {
        if (this.justUpdated) {
            this.startTime = currentTime;
            this.justUpdated = false;
        }
        manager.getGame().getTextureManager().bindTexture(SystemToast.TOASTS_TEX);
        GlStateManager.color3f(1.0f, 1.0f, 1.0f);
        manager.blit(0, 0, 0, 64, 160, 32);
        if (this.e == null) {
            manager.getGame().textRenderer.draw(this.d, 18.0f, 12.0f, -256);
        }
        else {
            manager.getGame().textRenderer.draw(this.d, 18.0f, 7.0f, -256);
            manager.getGame().textRenderer.draw(this.e, 18.0f, 18.0f, -1);
        }
        return (currentTime - this.startTime < 5000L) ? Visibility.a : Visibility.b;
    }
    
    public void setContent(final TextComponent textComponent1, @Nullable final TextComponent textComponent2) {
        this.d = textComponent1.getString();
        this.e = ((textComponent2 == null) ? null : textComponent2.getString());
        this.justUpdated = true;
    }
    
    @Override
    public Type getType() {
        return this.c;
    }
    
    public static void show(final ToastManager toastManager, final Type type, final TextComponent textComponent3, @Nullable final TextComponent textComponent4) {
        final SystemToast systemToast5 = toastManager.<SystemToast>getToast(SystemToast.class, type);
        if (systemToast5 == null) {
            toastManager.add(new SystemToast(type, textComponent3, textComponent4));
        }
        else {
            systemToast5.setContent(textComponent3, textComponent4);
        }
    }
    
    @Environment(EnvType.CLIENT)
    public enum Type
    {
        a, 
        b, 
        c;
    }
}

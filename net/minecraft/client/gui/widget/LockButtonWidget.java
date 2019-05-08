package net.minecraft.client.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class LockButtonWidget extends ButtonWidget
{
    private boolean locked;
    
    public LockButtonWidget(final int x, final int y, final PressAction pressAction) {
        super(x, y, 20, 20, I18n.translate("narrator.button.difficulty_lock"), pressAction);
    }
    
    @Override
    protected String getNarrationMessage() {
        return super.getNarrationMessage() + ". " + (this.isLocked() ? I18n.translate("narrator.button.difficulty_lock.locked") : I18n.translate("narrator.button.difficulty_lock.unlocked"));
    }
    
    public boolean isLocked() {
        return this.locked;
    }
    
    public void setLocked(final boolean boolean1) {
        this.locked = boolean1;
    }
    
    @Override
    public void renderButton(final int mouseX, final int mouseY, final float delta) {
        MinecraftClient.getInstance().getTextureManager().bindTexture(ButtonWidget.WIDGETS_LOCATION);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        IconLocation iconLocation4;
        if (!this.active) {
            iconLocation4 = (this.locked ? IconLocation.LOCKED_DISABLED : IconLocation.UNLOCKED_DISABLED);
        }
        else if (this.isHovered()) {
            iconLocation4 = (this.locked ? IconLocation.LOCKED_HOVERED : IconLocation.UNLOCKED_HOVERED);
        }
        else {
            iconLocation4 = (this.locked ? IconLocation.LOCKED : IconLocation.UNLOCKED);
        }
        this.blit(this.x, this.y, iconLocation4.getU(), iconLocation4.getV(), this.width, this.height);
    }
    
    @Environment(EnvType.CLIENT)
    enum IconLocation
    {
        LOCKED(0, 146), 
        LOCKED_HOVERED(0, 166), 
        LOCKED_DISABLED(0, 186), 
        UNLOCKED(20, 146), 
        UNLOCKED_HOVERED(20, 166), 
        UNLOCKED_DISABLED(20, 186);
        
        private final int u;
        private final int v;
        
        private IconLocation(final int integer1, final int integer2) {
            this.u = integer1;
            this.v = integer2;
        }
        
        public int getU() {
            return this.u;
        }
        
        public int getV() {
            return this.v;
        }
    }
}

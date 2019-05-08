package net.minecraft.realms;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class AbstractRealmsButton<P extends AbstractButtonWidget>
{
    public abstract P getProxy();
    
    public boolean active() {
        return this.getProxy().active();
    }
    
    public void active(final boolean boolean1) {
        this.getProxy().active(boolean1);
    }
    
    public boolean isVisible() {
        return this.getProxy().isVisible();
    }
    
    public void setVisible(final boolean boolean1) {
        this.getProxy().setVisible(boolean1);
    }
    
    public void render(final int integer1, final int integer2, final float float3) {
        this.getProxy().render(integer1, integer2, float3);
    }
    
    public void blit(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6) {
        this.getProxy().blit(integer1, integer2, integer3, integer4, integer5, integer6);
    }
    
    public void tick() {
    }
}

package net.minecraft.client.gui.widget;

import net.minecraft.client.MinecraftClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class AbstractPressableButtonWidget extends AbstractButtonWidget
{
    public AbstractPressableButtonWidget(final int x, final int y, final int width, final int height, final String message) {
        super(x, y, width, height, message);
    }
    
    public abstract void onPress();
    
    @Override
    public void onClick(final double mouseX, final double mouseY) {
        this.onPress();
    }
    
    @Override
    public boolean keyPressed(final int keyCode, final int scanCode, final int modifiers) {
        if (!this.active || !this.visible) {
            return false;
        }
        if (keyCode == 257 || keyCode == 32 || keyCode == 335) {
            this.playDownSound(MinecraftClient.getInstance().getSoundManager());
            this.onPress();
            return true;
        }
        return false;
    }
}

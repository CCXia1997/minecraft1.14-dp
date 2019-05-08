package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ButtonWidget extends AbstractPressableButtonWidget
{
    protected final PressAction onPress;
    
    public ButtonWidget(final int x, final int y, final int width, final int height, final String message, final PressAction onPress) {
        super(x, y, width, height, message);
        this.onPress = onPress;
    }
    
    @Override
    public void onPress() {
        this.onPress.onPress(this);
    }
    
    @Environment(EnvType.CLIENT)
    public interface PressAction
    {
        void onPress(final ButtonWidget arg1);
    }
}

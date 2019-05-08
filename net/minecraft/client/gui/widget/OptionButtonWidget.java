package net.minecraft.client.gui.widget;

import net.minecraft.client.options.GameOption;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class OptionButtonWidget extends ButtonWidget
{
    private final GameOption a;
    
    public OptionButtonWidget(final int integer1, final int integer2, final int integer3, final int integer4, final GameOption gameOption, final String string, final PressAction pressAction) {
        super(integer1, integer2, integer3, integer4, string, pressAction);
        this.a = gameOption;
    }
}

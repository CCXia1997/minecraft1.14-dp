package net.minecraft.client.gui.widget;

import net.minecraft.client.options.GameOption;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.DoubleGameOption;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class GameOptionSliderWidget extends SliderWidget
{
    private final DoubleGameOption option;
    
    public GameOptionSliderWidget(final GameOptions gameOptions, final int integer2, final int integer3, final int integer4, final int integer5, final DoubleGameOption doubleGameOption) {
        super(gameOptions, integer2, integer3, integer4, integer5, (float)doubleGameOption.a(doubleGameOption.get(gameOptions)));
        this.option = doubleGameOption;
        this.updateMessage();
    }
    
    @Override
    public void renderButton(final int mouseX, final int mouseY, final float delta) {
        if (this.option == GameOption.FULLSCREEN_RESOLUTION) {
            this.updateMessage();
        }
        super.renderButton(mouseX, mouseY, delta);
    }
    
    @Override
    protected void applyValue() {
        this.option.set(this.options, this.option.b(this.value));
        this.options.write();
    }
    
    @Override
    protected void updateMessage() {
        this.setMessage(this.option.getDisplayString(this.options));
    }
}

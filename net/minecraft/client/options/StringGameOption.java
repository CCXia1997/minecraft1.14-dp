package net.minecraft.client.options;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import java.util.function.BiFunction;
import java.util.function.BiConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class StringGameOption extends GameOption
{
    private final BiConsumer<GameOptions, Integer> getter;
    private final BiFunction<GameOptions, StringGameOption, String> setter;
    
    public StringGameOption(final String key, final BiConsumer<GameOptions, Integer> getter, final BiFunction<GameOptions, StringGameOption, String> setter) {
        super(key);
        this.getter = getter;
        this.setter = setter;
    }
    
    public void a(final GameOptions options, final int integer) {
        this.getter.accept(options, integer);
        options.write();
    }
    
    @Override
    public AbstractButtonWidget createOptionButton(final GameOptions options, final int x, final int y, final int width) {
        return new OptionButtonWidget(x, y, width, 20, this, this.get(options), buttonWidget -> {
            this.a(options, 1);
            buttonWidget.setMessage(this.get(options));
        });
    }
    
    public String get(final GameOptions options) {
        return this.setter.apply(options, this);
    }
}

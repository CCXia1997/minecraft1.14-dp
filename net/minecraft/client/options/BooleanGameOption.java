package net.minecraft.client.options;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class BooleanGameOption extends GameOption
{
    private final Predicate<GameOptions> getter;
    private final BiConsumer<GameOptions, Boolean> setter;
    
    public BooleanGameOption(final String key, final Predicate<GameOptions> getter, final BiConsumer<GameOptions, Boolean> setter) {
        super(key);
        this.getter = getter;
        this.setter = setter;
    }
    
    public void set(final GameOptions options, final String value) {
        this.set(options, "true".equals(value));
    }
    
    public void set(final GameOptions options) {
        this.set(options, !this.get(options));
        options.write();
    }
    
    private void set(final GameOptions options, final boolean value) {
        this.setter.accept(options, value);
    }
    
    public boolean get(final GameOptions options) {
        return this.getter.test(options);
    }
    
    @Override
    public AbstractButtonWidget createOptionButton(final GameOptions options, final int x, final int y, final int width) {
        return new OptionButtonWidget(x, y, width, 20, this, this.getDisplayString(options), buttonWidget -> {
            this.set(options);
            buttonWidget.setMessage(this.getDisplayString(options));
        });
    }
    
    public String getDisplayString(final GameOptions options) {
        return this.getDisplayPrefix() + I18n.translate(this.get(options) ? "options.on" : "options.off");
    }
}

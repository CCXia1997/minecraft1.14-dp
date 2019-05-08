package net.minecraft.client.options;

import net.minecraft.util.math.MathHelper;
import net.minecraft.client.gui.widget.GameOptionSliderWidget;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import java.util.function.BiFunction;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class DoubleGameOption extends GameOption
{
    protected final float interval;
    protected final double min;
    protected double max;
    private final Function<GameOptions, Double> getter;
    private final BiConsumer<GameOptions, Double> setter;
    private final BiFunction<GameOptions, DoubleGameOption, String> displayStringGetter;
    
    public DoubleGameOption(final String key, final double min, final double max, final float interval, final Function<GameOptions, Double> getter, final BiConsumer<GameOptions, Double> setter, final BiFunction<GameOptions, DoubleGameOption, String> displayStringGetter) {
        super(key);
        this.min = min;
        this.max = max;
        this.interval = interval;
        this.getter = getter;
        this.setter = setter;
        this.displayStringGetter = displayStringGetter;
    }
    
    @Override
    public AbstractButtonWidget createOptionButton(final GameOptions options, final int x, final int y, final int width) {
        return new GameOptionSliderWidget(options, x, y, width, 20, this);
    }
    
    public double a(final double double1) {
        return MathHelper.clamp((this.c(double1) - this.min) / (this.max - this.min), 0.0, 1.0);
    }
    
    public double b(final double double1) {
        return this.c(MathHelper.lerp(MathHelper.clamp(double1, 0.0, 1.0), this.min, this.max));
    }
    
    private double c(double double1) {
        if (this.interval > 0.0f) {
            double1 = this.interval * Math.round(double1 / this.interval);
        }
        return MathHelper.clamp(double1, this.min, this.max);
    }
    
    public double getMin() {
        return this.min;
    }
    
    public double getMax() {
        return this.max;
    }
    
    public void setMax(final float max) {
        this.max = max;
    }
    
    public void set(final GameOptions options, final double value) {
        this.setter.accept(options, value);
    }
    
    public double get(final GameOptions options) {
        return this.getter.apply(options);
    }
    
    public String getDisplayString(final GameOptions options) {
        return this.displayStringGetter.apply(options, this);
    }
}

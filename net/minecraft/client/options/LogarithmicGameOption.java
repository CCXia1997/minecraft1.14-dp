package net.minecraft.client.options;

import java.util.function.BiFunction;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class LogarithmicGameOption extends DoubleGameOption
{
    public LogarithmicGameOption(final String key, final double min, final double max, final float interval, final Function<GameOptions, Double> getter, final BiConsumer<GameOptions, Double> setter, final BiFunction<GameOptions, DoubleGameOption, String> displayStringGetter) {
        super(key, min, max, interval, getter, setter, displayStringGetter);
    }
    
    @Override
    public double a(final double double1) {
        return Math.log(double1 / this.min) / Math.log(this.max / this.min);
    }
    
    @Override
    public double b(final double double1) {
        return this.min * Math.pow(2.718281828459045, Math.log(this.max / this.min) * double1);
    }
}

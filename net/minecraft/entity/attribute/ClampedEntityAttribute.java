package net.minecraft.entity.attribute;

import net.minecraft.util.math.MathHelper;
import javax.annotation.Nullable;

public class ClampedEntityAttribute extends AbstractEntityAttribute
{
    private final double minValue;
    private final double maxValue;
    private String name;
    
    public ClampedEntityAttribute(@Nullable final EntityAttribute parent, final String id, final double defaultValue, final double minValue, final double maxValue) {
        super(parent, id, defaultValue);
        this.minValue = minValue;
        this.maxValue = maxValue;
        if (minValue > maxValue) {
            throw new IllegalArgumentException("Minimum value cannot be bigger than maximum value!");
        }
        if (defaultValue < minValue) {
            throw new IllegalArgumentException("Default value cannot be lower than minimum value!");
        }
        if (defaultValue > maxValue) {
            throw new IllegalArgumentException("Default value cannot be bigger than maximum value!");
        }
    }
    
    public ClampedEntityAttribute setName(final String name) {
        this.name = name;
        return this;
    }
    
    public String getName() {
        return this.name;
    }
    
    @Override
    public double clamp(double value) {
        value = MathHelper.clamp(value, this.minValue, this.maxValue);
        return value;
    }
}

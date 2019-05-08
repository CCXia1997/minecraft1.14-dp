package net.minecraft.entity.attribute;

import javax.annotation.Nullable;

public interface EntityAttribute
{
    String getId();
    
    double clamp(final double arg1);
    
    double getDefaultValue();
    
    boolean isTracked();
    
    @Nullable
    EntityAttribute getParent();
}

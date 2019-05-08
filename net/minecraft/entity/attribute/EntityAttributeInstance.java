package net.minecraft.entity.attribute;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import javax.annotation.Nullable;
import java.util.UUID;
import java.util.Collection;

public interface EntityAttributeInstance
{
    EntityAttribute getAttribute();
    
    double getBaseValue();
    
    void setBaseValue(final double arg1);
    
    Collection<EntityAttributeModifier> getModifiers(final EntityAttributeModifier.Operation arg1);
    
    Collection<EntityAttributeModifier> getModifiers();
    
    boolean hasModifier(final EntityAttributeModifier arg1);
    
    @Nullable
    EntityAttributeModifier getModifier(final UUID arg1);
    
    void addModifier(final EntityAttributeModifier arg1);
    
    void removeModifier(final EntityAttributeModifier arg1);
    
    void removeModifier(final UUID arg1);
    
    @Environment(EnvType.CLIENT)
    void clearModifiers();
    
    double getValue();
}

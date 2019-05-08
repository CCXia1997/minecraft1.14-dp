package net.minecraft.potion;

import com.google.common.collect.UnmodifiableIterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.entity.effect.StatusEffectInstance;
import com.google.common.collect.ImmutableList;

public class Potion
{
    private final String name;
    private final ImmutableList<StatusEffectInstance> effectList;
    
    public static Potion byId(final String id) {
        return Registry.POTION.get(Identifier.create(id));
    }
    
    public Potion(final StatusEffectInstance... effects) {
        this((String)null, effects);
    }
    
    public Potion(@Nullable final String name, final StatusEffectInstance... effects) {
        this.name = name;
        this.effectList = ImmutableList.<StatusEffectInstance>copyOf(effects);
    }
    
    public String getName(final String string) {
        return string + ((this.name == null) ? Registry.POTION.getId(this).getPath() : this.name);
    }
    
    public List<StatusEffectInstance> getEffects() {
        return this.effectList;
    }
    
    public boolean hasInstantEffect() {
        if (!this.effectList.isEmpty()) {
            for (final StatusEffectInstance statusEffectInstance2 : this.effectList) {
                if (statusEffectInstance2.getEffectType().isInstant()) {
                    return true;
                }
            }
        }
        return false;
    }
}

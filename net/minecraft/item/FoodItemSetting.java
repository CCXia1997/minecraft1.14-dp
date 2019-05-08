package net.minecraft.item;

import com.google.common.collect.Lists;
import net.minecraft.entity.effect.StatusEffectInstance;
import org.apache.commons.lang3.tuple.Pair;
import java.util.List;

public class FoodItemSetting
{
    private final int hunger;
    private final float saturationModifier;
    private final boolean wolfFood;
    private final boolean alwaysEdible;
    private final boolean eatenFast;
    private final List<Pair<StatusEffectInstance, Float>> statusEffectChances;
    
    private FoodItemSetting(final int hunger, final float saturationModifier, final boolean isWolfFood, final boolean isAlwaysEdible, final boolean isEatenFaster, final List<Pair<StatusEffectInstance, Float>> statusEffects) {
        this.hunger = hunger;
        this.saturationModifier = saturationModifier;
        this.wolfFood = isWolfFood;
        this.alwaysEdible = isAlwaysEdible;
        this.eatenFast = isEatenFaster;
        this.statusEffectChances = statusEffects;
    }
    
    public int getHunger() {
        return this.hunger;
    }
    
    public float getSaturationModifier() {
        return this.saturationModifier;
    }
    
    public boolean isWolfFood() {
        return this.wolfFood;
    }
    
    public boolean isAlwaysEdible() {
        return this.alwaysEdible;
    }
    
    public boolean isEatenFast() {
        return this.eatenFast;
    }
    
    public List<Pair<StatusEffectInstance, Float>> getStatusEffectChances() {
        return this.statusEffectChances;
    }
    
    public static class Builder
    {
        private int hunger;
        private float saturationModifier;
        private boolean wolfFood;
        private boolean alwaysEdible;
        private boolean eatenFast;
        private final List<Pair<StatusEffectInstance, Float>> statusEffectChances;
        
        public Builder() {
            this.statusEffectChances = Lists.newArrayList();
        }
        
        public Builder hunger(final int amount) {
            this.hunger = amount;
            return this;
        }
        
        public Builder saturationModifier(final float amount) {
            this.saturationModifier = amount;
            return this;
        }
        
        public Builder wolfFood() {
            this.wolfFood = true;
            return this;
        }
        
        public Builder alwaysEdible() {
            this.alwaysEdible = true;
            return this;
        }
        
        public Builder eatenFast() {
            this.eatenFast = true;
            return this;
        }
        
        public Builder statusEffect(final StatusEffectInstance statusEffectInstance, final float chance) {
            this.statusEffectChances.add((Pair<StatusEffectInstance, Float>)Pair.of(statusEffectInstance, chance));
            return this;
        }
        
        public FoodItemSetting build() {
            return new FoodItemSetting(this.hunger, this.saturationModifier, this.wolfFood, this.alwaysEdible, this.eatenFast, this.statusEffectChances, null);
        }
    }
}

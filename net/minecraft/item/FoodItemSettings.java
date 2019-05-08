package net.minecraft.item;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public class FoodItemSettings
{
    public static final FoodItemSetting APPLE;
    public static final FoodItemSetting BAKED_POTATO;
    public static final FoodItemSetting BEEF;
    public static final FoodItemSetting BEETROOT;
    public static final FoodItemSetting BEETROOT_SOUP;
    public static final FoodItemSetting BREAD;
    public static final FoodItemSetting CARROT;
    public static final FoodItemSetting CHICKEN;
    public static final FoodItemSetting CHORUS_FRUIT;
    public static final FoodItemSetting COD;
    public static final FoodItemSetting COOKED_BEEF;
    public static final FoodItemSetting COOKED_CHICKEN;
    public static final FoodItemSetting COOKED_COD;
    public static final FoodItemSetting COOKED_MUTTON;
    public static final FoodItemSetting COOKED_PORKCHOP;
    public static final FoodItemSetting COOKED_RABBIT;
    public static final FoodItemSetting COOKED_SALMON;
    public static final FoodItemSetting COOKIE;
    public static final FoodItemSetting DRIED_KELP;
    public static final FoodItemSetting ENCHANTED_GOLDEN_APPLE;
    public static final FoodItemSetting GOLDEN_APPLE;
    public static final FoodItemSetting GOLDEN_CARROT;
    public static final FoodItemSetting MELON_SLICE;
    public static final FoodItemSetting MUSHROOM_STEW;
    public static final FoodItemSetting MUTTON;
    public static final FoodItemSetting POISONOUS_POTATO;
    public static final FoodItemSetting PORKCHOP;
    public static final FoodItemSetting POTATO;
    public static final FoodItemSetting PUFFERFISH;
    public static final FoodItemSetting PUMPKIN_PIE;
    public static final FoodItemSetting RABBIT;
    public static final FoodItemSetting RABBIT_STEW;
    public static final FoodItemSetting ROTTEN_FLESH;
    public static final FoodItemSetting SALMON;
    public static final FoodItemSetting SPIDER_EYE;
    public static final FoodItemSetting SUSPICIOUS_STEW;
    public static final FoodItemSetting SWEET_BERRIES;
    public static final FoodItemSetting TROPICAL_FISH;
    
    private static FoodItemSetting create(final int hunger) {
        return new FoodItemSetting.Builder().hunger(hunger).saturationModifier(0.6f).build();
    }
    
    static {
        APPLE = new FoodItemSetting.Builder().hunger(4).saturationModifier(0.3f).build();
        BAKED_POTATO = new FoodItemSetting.Builder().hunger(5).saturationModifier(0.6f).build();
        BEEF = new FoodItemSetting.Builder().hunger(3).saturationModifier(0.3f).wolfFood().build();
        BEETROOT = new FoodItemSetting.Builder().hunger(1).saturationModifier(0.6f).build();
        BEETROOT_SOUP = create(6);
        BREAD = new FoodItemSetting.Builder().hunger(5).saturationModifier(0.6f).build();
        CARROT = new FoodItemSetting.Builder().hunger(3).saturationModifier(0.6f).build();
        CHICKEN = new FoodItemSetting.Builder().hunger(2).saturationModifier(0.3f).statusEffect(new StatusEffectInstance(StatusEffects.q, 600, 0), 0.3f).wolfFood().build();
        CHORUS_FRUIT = new FoodItemSetting.Builder().hunger(4).saturationModifier(0.3f).alwaysEdible().build();
        COD = new FoodItemSetting.Builder().hunger(2).saturationModifier(0.1f).build();
        COOKED_BEEF = new FoodItemSetting.Builder().hunger(8).saturationModifier(0.8f).wolfFood().build();
        COOKED_CHICKEN = new FoodItemSetting.Builder().hunger(6).saturationModifier(0.6f).wolfFood().build();
        COOKED_COD = new FoodItemSetting.Builder().hunger(5).saturationModifier(0.6f).build();
        COOKED_MUTTON = new FoodItemSetting.Builder().hunger(6).saturationModifier(0.8f).wolfFood().build();
        COOKED_PORKCHOP = new FoodItemSetting.Builder().hunger(8).saturationModifier(0.8f).wolfFood().build();
        COOKED_RABBIT = new FoodItemSetting.Builder().hunger(5).saturationModifier(0.6f).wolfFood().build();
        COOKED_SALMON = new FoodItemSetting.Builder().hunger(6).saturationModifier(0.8f).build();
        COOKIE = new FoodItemSetting.Builder().hunger(2).saturationModifier(0.1f).build();
        DRIED_KELP = new FoodItemSetting.Builder().hunger(1).saturationModifier(0.3f).eatenFast().build();
        ENCHANTED_GOLDEN_APPLE = new FoodItemSetting.Builder().hunger(4).saturationModifier(1.2f).statusEffect(new StatusEffectInstance(StatusEffects.j, 400, 1), 1.0f).statusEffect(new StatusEffectInstance(StatusEffects.k, 6000, 0), 1.0f).statusEffect(new StatusEffectInstance(StatusEffects.l, 6000, 0), 1.0f).statusEffect(new StatusEffectInstance(StatusEffects.v, 2400, 3), 1.0f).alwaysEdible().build();
        GOLDEN_APPLE = new FoodItemSetting.Builder().hunger(4).saturationModifier(1.2f).statusEffect(new StatusEffectInstance(StatusEffects.j, 100, 1), 1.0f).statusEffect(new StatusEffectInstance(StatusEffects.v, 2400, 0), 1.0f).alwaysEdible().build();
        GOLDEN_CARROT = new FoodItemSetting.Builder().hunger(6).saturationModifier(1.2f).build();
        MELON_SLICE = new FoodItemSetting.Builder().hunger(2).saturationModifier(0.3f).build();
        MUSHROOM_STEW = create(6);
        MUTTON = new FoodItemSetting.Builder().hunger(2).saturationModifier(0.3f).wolfFood().build();
        POISONOUS_POTATO = new FoodItemSetting.Builder().hunger(2).saturationModifier(0.3f).statusEffect(new StatusEffectInstance(StatusEffects.s, 100, 0), 0.6f).build();
        PORKCHOP = new FoodItemSetting.Builder().hunger(3).saturationModifier(0.3f).wolfFood().build();
        POTATO = new FoodItemSetting.Builder().hunger(1).saturationModifier(0.3f).build();
        PUFFERFISH = new FoodItemSetting.Builder().hunger(1).saturationModifier(0.1f).statusEffect(new StatusEffectInstance(StatusEffects.s, 1200, 3), 1.0f).statusEffect(new StatusEffectInstance(StatusEffects.q, 300, 2), 1.0f).statusEffect(new StatusEffectInstance(StatusEffects.i, 300, 1), 1.0f).build();
        PUMPKIN_PIE = new FoodItemSetting.Builder().hunger(8).saturationModifier(0.3f).build();
        RABBIT = new FoodItemSetting.Builder().hunger(3).saturationModifier(0.3f).wolfFood().build();
        RABBIT_STEW = create(10);
        ROTTEN_FLESH = new FoodItemSetting.Builder().hunger(4).saturationModifier(0.1f).statusEffect(new StatusEffectInstance(StatusEffects.q, 600, 0), 0.8f).wolfFood().build();
        SALMON = new FoodItemSetting.Builder().hunger(2).saturationModifier(0.1f).build();
        SPIDER_EYE = new FoodItemSetting.Builder().hunger(2).saturationModifier(0.8f).statusEffect(new StatusEffectInstance(StatusEffects.s, 100, 0), 1.0f).build();
        SUSPICIOUS_STEW = create(6);
        SWEET_BERRIES = new FoodItemSetting.Builder().hunger(2).saturationModifier(0.1f).build();
        TROPICAL_FISH = new FoodItemSetting.Builder().hunger(1).saturationModifier(0.1f).build();
    }
}

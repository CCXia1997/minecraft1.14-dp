package net.minecraft.entity.effect;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.registry.Registry;

public class StatusEffects
{
    public static final StatusEffect a;
    public static final StatusEffect b;
    public static final StatusEffect c;
    public static final StatusEffect d;
    public static final StatusEffect e;
    public static final StatusEffect f;
    public static final StatusEffect g;
    public static final StatusEffect h;
    public static final StatusEffect i;
    public static final StatusEffect j;
    public static final StatusEffect k;
    public static final StatusEffect l;
    public static final StatusEffect m;
    public static final StatusEffect n;
    public static final StatusEffect o;
    public static final StatusEffect p;
    public static final StatusEffect q;
    public static final StatusEffect r;
    public static final StatusEffect s;
    public static final StatusEffect t;
    public static final StatusEffect u;
    public static final StatusEffect v;
    public static final StatusEffect w;
    public static final StatusEffect x;
    public static final StatusEffect y;
    public static final StatusEffect z;
    public static final StatusEffect A;
    public static final StatusEffect B;
    public static final StatusEffect C;
    public static final StatusEffect D;
    public static final StatusEffect E;
    public static final StatusEffect F;
    
    private static StatusEffect register(final int integer, final String string, final StatusEffect statusEffect) {
        return Registry.<StatusEffect>register(Registry.STATUS_EFFECT, integer, string, statusEffect);
    }
    
    static {
        a = register(1, "speed", new StatusEffect(StatusEffectType.a, 8171462).addAttributeModifier(EntityAttributes.MOVEMENT_SPEED, "91AEAA56-376B-4498-935B-2F7F68070635", 0.20000000298023224, EntityAttributeModifier.Operation.c));
        b = register(2, "slowness", new StatusEffect(StatusEffectType.b, 5926017).addAttributeModifier(EntityAttributes.MOVEMENT_SPEED, "7107DE5E-7CE8-4030-940E-514C1F160890", -0.15000000596046448, EntityAttributeModifier.Operation.c));
        c = register(3, "haste", new StatusEffect(StatusEffectType.a, 14270531).addAttributeModifier(EntityAttributes.ATTACK_SPEED, "AF8B6E3F-3328-4C0A-AA36-5BA2BB9DBEF3", 0.10000000149011612, EntityAttributeModifier.Operation.c));
        d = register(4, "mining_fatigue", new StatusEffect(StatusEffectType.b, 4866583).addAttributeModifier(EntityAttributes.ATTACK_SPEED, "55FCED67-E92A-486E-9800-B47F202C4386", -0.10000000149011612, EntityAttributeModifier.Operation.c));
        e = register(5, "strength", new DamageModifierStatusEffect(StatusEffectType.a, 9643043, 3.0).addAttributeModifier(EntityAttributes.ATTACK_DAMAGE, "648D7064-6A60-4F59-8ABE-C2C23A6DD7A9", 0.0, EntityAttributeModifier.Operation.a));
        f = register(6, "instant_health", new InstantStatusEffect(StatusEffectType.a, 16262179));
        g = register(7, "instant_damage", new InstantStatusEffect(StatusEffectType.b, 4393481));
        h = register(8, "jump_boost", new StatusEffect(StatusEffectType.a, 2293580));
        i = register(9, "nausea", new StatusEffect(StatusEffectType.b, 5578058));
        j = register(10, "regeneration", new StatusEffect(StatusEffectType.a, 13458603));
        k = register(11, "resistance", new StatusEffect(StatusEffectType.a, 10044730));
        l = register(12, "fire_resistance", new StatusEffect(StatusEffectType.a, 14981690));
        m = register(13, "water_breathing", new StatusEffect(StatusEffectType.a, 3035801));
        n = register(14, "invisibility", new StatusEffect(StatusEffectType.a, 8356754));
        o = register(15, "blindness", new StatusEffect(StatusEffectType.b, 2039587));
        p = register(16, "night_vision", new StatusEffect(StatusEffectType.a, 2039713));
        q = register(17, "hunger", new StatusEffect(StatusEffectType.b, 5797459));
        r = register(18, "weakness", new DamageModifierStatusEffect(StatusEffectType.b, 4738376, -4.0).addAttributeModifier(EntityAttributes.ATTACK_DAMAGE, "22653B89-116E-49DC-9B6B-9971489B5BE5", 0.0, EntityAttributeModifier.Operation.a));
        s = register(19, "poison", new StatusEffect(StatusEffectType.b, 5149489));
        t = register(20, "wither", new StatusEffect(StatusEffectType.b, 3484199));
        u = register(21, "health_boost", new HealthBoostStatusEffect(StatusEffectType.a, 16284963).addAttributeModifier(EntityAttributes.MAX_HEALTH, "5D6F0BA2-1186-46AC-B896-C61C5CEE99CC", 4.0, EntityAttributeModifier.Operation.a));
        v = register(22, "absorption", new AbsorptionStatusEffect(StatusEffectType.a, 2445989));
        w = register(23, "saturation", new InstantStatusEffect(StatusEffectType.a, 16262179));
        x = register(24, "glowing", new StatusEffect(StatusEffectType.c, 9740385));
        y = register(25, "levitation", new StatusEffect(StatusEffectType.b, 13565951));
        z = register(26, "luck", new StatusEffect(StatusEffectType.a, 3381504).addAttributeModifier(EntityAttributes.LUCK, "03C3C89D-7037-4B42-869F-B146BCB64D2E", 1.0, EntityAttributeModifier.Operation.a));
        A = register(27, "unluck", new StatusEffect(StatusEffectType.b, 12624973).addAttributeModifier(EntityAttributes.LUCK, "CC5AF142-2BD2-4215-B636-2605AED11727", -1.0, EntityAttributeModifier.Operation.a));
        B = register(28, "slow_falling", new StatusEffect(StatusEffectType.a, 16773073));
        C = register(29, "conduit_power", new StatusEffect(StatusEffectType.a, 1950417));
        D = register(30, "dolphins_grace", new StatusEffect(StatusEffectType.a, 8954814));
        E = register(31, "bad_omen", new StatusEffect(StatusEffectType.c, 745784) {
            @Override
            public boolean canApplyUpdateEffect(final int duration, final int integer2) {
                return true;
            }
            
            @Override
            public void applyUpdateEffect(final LivingEntity entity, final int integer) {
                if (entity instanceof ServerPlayerEntity && !entity.isSpectator()) {
                    final ServerPlayerEntity serverPlayerEntity3 = (ServerPlayerEntity)entity;
                    final ServerWorld serverWorld4 = serverPlayerEntity3.getServerWorld();
                    if (serverWorld4.getDifficulty() == Difficulty.PEACEFUL) {
                        return;
                    }
                    if (serverWorld4.isNearOccupiedPointOfInterest(new BlockPos(entity))) {
                        serverWorld4.getRaidManager().startRaid(serverPlayerEntity3);
                    }
                }
            }
        });
        F = register(32, "hero_of_the_village", new StatusEffect(StatusEffectType.a, 4521796));
    }
}

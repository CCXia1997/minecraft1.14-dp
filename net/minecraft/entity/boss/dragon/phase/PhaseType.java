package net.minecraft.entity.boss.dragon.phase;

import java.util.Arrays;
import java.lang.reflect.Constructor;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;

public class PhaseType<T extends Phase>
{
    private static PhaseType<?>[] types;
    public static final PhaseType<HoldingPatternPhase> HOLDING_PATTERN;
    public static final PhaseType<StrafePlayerPhase> STRAFE_PLAYER;
    public static final PhaseType<LandingApproachPhase> LANDING_APPROACH;
    public static final PhaseType<LandingPhase> LANDING;
    public static final PhaseType<TakeoffPhase> TAKEOFF;
    public static final PhaseType<SittingFlamingPhase> SITTING_FLAMING;
    public static final PhaseType<SittingScanningPhase> SITTING_SCANNING;
    public static final PhaseType<SittingAttackingPhase> SITTING_ATTACKING;
    public static final PhaseType<ChargingPlayerPhase> CHARGING_PLAYER;
    public static final PhaseType<DyingPhase> DYING;
    public static final PhaseType<HoverPhase> HOVER;
    private final Class<? extends Phase> phaseClass;
    private final int id;
    private final String name;
    
    private PhaseType(final int id, final Class<? extends Phase> phaseClass, final String name) {
        this.id = id;
        this.phaseClass = phaseClass;
        this.name = name;
    }
    
    public Phase create(final EnderDragonEntity dragon) {
        try {
            final Constructor<? extends Phase> constructor2 = this.getConstructor();
            return (Phase)constructor2.newInstance(dragon);
        }
        catch (Exception exception2) {
            throw new Error(exception2);
        }
    }
    
    protected Constructor<? extends Phase> getConstructor() throws NoSuchMethodException {
        return this.phaseClass.getConstructor(EnderDragonEntity.class);
    }
    
    public int getTypeId() {
        return this.id;
    }
    
    @Override
    public String toString() {
        return this.name + " (#" + this.id + ")";
    }
    
    public static PhaseType<?> getFromId(final int id) {
        if (id < 0 || id >= PhaseType.types.length) {
            return PhaseType.HOLDING_PATTERN;
        }
        return PhaseType.types[id];
    }
    
    public static int count() {
        return PhaseType.types.length;
    }
    
    private static <T extends Phase> PhaseType<T> register(final Class<T> phaseClass, final String name) {
        final PhaseType<T> phaseType3 = new PhaseType<T>(PhaseType.types.length, phaseClass, name);
        PhaseType.types = Arrays.<PhaseType<?>>copyOf(PhaseType.types, PhaseType.types.length + 1);
        return (PhaseType<T>)(PhaseType.types[phaseType3.getTypeId()] = phaseType3);
    }
    
    static {
        PhaseType.types = new PhaseType[0];
        HOLDING_PATTERN = PhaseType.<HoldingPatternPhase>register(HoldingPatternPhase.class, "HoldingPattern");
        STRAFE_PLAYER = PhaseType.<StrafePlayerPhase>register(StrafePlayerPhase.class, "StrafePlayer");
        LANDING_APPROACH = PhaseType.<LandingApproachPhase>register(LandingApproachPhase.class, "LandingApproach");
        LANDING = PhaseType.<LandingPhase>register(LandingPhase.class, "Landing");
        TAKEOFF = PhaseType.<TakeoffPhase>register(TakeoffPhase.class, "Takeoff");
        SITTING_FLAMING = PhaseType.<SittingFlamingPhase>register(SittingFlamingPhase.class, "SittingFlaming");
        SITTING_SCANNING = PhaseType.<SittingScanningPhase>register(SittingScanningPhase.class, "SittingScanning");
        SITTING_ATTACKING = PhaseType.<SittingAttackingPhase>register(SittingAttackingPhase.class, "SittingAttacking");
        CHARGING_PLAYER = PhaseType.<ChargingPlayerPhase>register(ChargingPlayerPhase.class, "ChargingPlayer");
        DYING = PhaseType.<DyingPhase>register(DyingPhase.class, "Dying");
        HOVER = PhaseType.<HoverPhase>register(HoverPhase.class, "Hover");
    }
}

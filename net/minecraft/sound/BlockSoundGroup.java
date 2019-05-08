package net.minecraft.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class BlockSoundGroup
{
    public static final BlockSoundGroup WOOD;
    public static final BlockSoundGroup GRAVEL;
    public static final BlockSoundGroup GRASS;
    public static final BlockSoundGroup STONE;
    public static final BlockSoundGroup METAL;
    public static final BlockSoundGroup GLASS;
    public static final BlockSoundGroup WOOL;
    public static final BlockSoundGroup SAND;
    public static final BlockSoundGroup SNOW;
    public static final BlockSoundGroup LADDER;
    public static final BlockSoundGroup ANVIL;
    public static final BlockSoundGroup SLIME;
    public static final BlockSoundGroup WET_GRASS;
    public static final BlockSoundGroup CORAL;
    public static final BlockSoundGroup BAMBOO;
    public static final BlockSoundGroup BAMBOO_SAPLING;
    public static final BlockSoundGroup SCAFFOLDING;
    public static final BlockSoundGroup SWEET_BERRY_BUSH;
    public static final BlockSoundGroup CROP;
    public static final BlockSoundGroup t;
    public static final BlockSoundGroup NETHER_WART;
    public static final BlockSoundGroup LANTERN;
    public final float volume;
    public final float pitch;
    private final SoundEvent breakSound;
    private final SoundEvent stepSound;
    private final SoundEvent placeSound;
    private final SoundEvent hitSound;
    private final SoundEvent fallSound;
    
    public BlockSoundGroup(final float float1, final float float2, final SoundEvent soundEvent3, final SoundEvent soundEvent4, final SoundEvent soundEvent5, final SoundEvent soundEvent6, final SoundEvent soundEvent7) {
        this.volume = float1;
        this.pitch = float2;
        this.breakSound = soundEvent3;
        this.stepSound = soundEvent4;
        this.placeSound = soundEvent5;
        this.hitSound = soundEvent6;
        this.fallSound = soundEvent7;
    }
    
    public float getVolume() {
        return this.volume;
    }
    
    public float getPitch() {
        return this.pitch;
    }
    
    @Environment(EnvType.CLIENT)
    public SoundEvent getBreakSound() {
        return this.breakSound;
    }
    
    public SoundEvent getStepSound() {
        return this.stepSound;
    }
    
    public SoundEvent getPlaceSound() {
        return this.placeSound;
    }
    
    @Environment(EnvType.CLIENT)
    public SoundEvent getHitSound() {
        return this.hitSound;
    }
    
    public SoundEvent getFallSound() {
        return this.fallSound;
    }
    
    static {
        WOOD = new BlockSoundGroup(1.0f, 1.0f, SoundEvents.nJ, SoundEvents.nR, SoundEvents.nO, SoundEvents.nN, SoundEvents.nM);
        GRAVEL = new BlockSoundGroup(1.0f, 1.0f, SoundEvents.ep, SoundEvents.et, SoundEvents.es, SoundEvents.er, SoundEvents.eq);
        GRASS = new BlockSoundGroup(1.0f, 1.0f, SoundEvents.ea, SoundEvents.ee, SoundEvents.ed, SoundEvents.ec, SoundEvents.eb);
        STONE = new BlockSoundGroup(1.0f, 1.0f, SoundEvents.lm, SoundEvents.lu, SoundEvents.lr, SoundEvents.lq, SoundEvents.lp);
        METAL = new BlockSoundGroup(1.0f, 1.5f, SoundEvents.gm, SoundEvents.gs, SoundEvents.gp, SoundEvents.go, SoundEvents.gn);
        GLASS = new BlockSoundGroup(1.0f, 1.0f, SoundEvents.dV, SoundEvents.dZ, SoundEvents.dY, SoundEvents.dX, SoundEvents.dW);
        WOOL = new BlockSoundGroup(1.0f, 1.0f, SoundEvents.aX, SoundEvents.bb, SoundEvents.ba, SoundEvents.aZ, SoundEvents.aY);
        SAND = new BlockSoundGroup(1.0f, 1.0f, SoundEvents.jF, SoundEvents.jJ, SoundEvents.jI, SoundEvents.jH, SoundEvents.jG);
        SNOW = new BlockSoundGroup(1.0f, 1.0f, SoundEvents.kT, SoundEvents.lb, SoundEvents.la, SoundEvents.kZ, SoundEvents.kU);
        LADDER = new BlockSoundGroup(1.0f, 1.0f, SoundEvents.fG, SoundEvents.fK, SoundEvents.fJ, SoundEvents.fI, SoundEvents.fH);
        ANVIL = new BlockSoundGroup(0.3f, 1.0f, SoundEvents.h, SoundEvents.n, SoundEvents.m, SoundEvents.k, SoundEvents.j);
        SLIME = new BlockSoundGroup(1.0f, 1.0f, SoundEvents.kF, SoundEvents.kJ, SoundEvents.kI, SoundEvents.kH, SoundEvents.kG);
        WET_GRASS = new BlockSoundGroup(1.0f, 1.0f, SoundEvents.ef, SoundEvents.ej, SoundEvents.ei, SoundEvents.eh, SoundEvents.eg);
        CORAL = new BlockSoundGroup(1.0f, 1.0f, SoundEvents.ek, SoundEvents.eo, SoundEvents.en, SoundEvents.em, SoundEvents.el);
        BAMBOO = new BlockSoundGroup(1.0f, 1.0f, SoundEvents.F, SoundEvents.J, SoundEvents.I, SoundEvents.H, SoundEvents.G);
        BAMBOO_SAPLING = new BlockSoundGroup(1.0f, 1.0f, SoundEvents.K, SoundEvents.J, SoundEvents.M, SoundEvents.L, SoundEvents.G);
        SCAFFOLDING = new BlockSoundGroup(1.0f, 1.0f, SoundEvents.jK, SoundEvents.jO, SoundEvents.jN, SoundEvents.jM, SoundEvents.jL);
        SWEET_BERRY_BUSH = new BlockSoundGroup(1.0f, 1.0f, SoundEvents.lz, SoundEvents.ee, SoundEvents.lA, SoundEvents.ec, SoundEvents.eb);
        CROP = new BlockSoundGroup(1.0f, 1.0f, SoundEvents.by, SoundEvents.ee, SoundEvents.bz, SoundEvents.ec, SoundEvents.eb);
        t = new BlockSoundGroup(1.0f, 1.0f, SoundEvents.nJ, SoundEvents.nR, SoundEvents.bz, SoundEvents.nN, SoundEvents.nM);
        NETHER_WART = new BlockSoundGroup(1.0f, 1.0f, SoundEvents.gM, SoundEvents.lu, SoundEvents.gN, SoundEvents.lq, SoundEvents.lp);
        LANTERN = new BlockSoundGroup(1.0f, 1.0f, SoundEvents.fL, SoundEvents.fP, SoundEvents.fO, SoundEvents.fN, SoundEvents.fM);
    }
}

package net.minecraft.entity.passive;

import java.util.Locale;
import net.minecraft.entity.Entity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.SpawnType;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.IWorld;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundEvent;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.entity.data.TrackedData;

public class TropicalFishEntity extends SchoolingFishEntity
{
    private static final TrackedData<Integer> VARIANT;
    private static final Identifier[] SHAPE_IDS;
    private static final Identifier[] SMALL_FISH_VARIETY_IDS;
    private static final Identifier[] LARGE_FISH_VARIETY_IDS;
    public static final int[] COMMON_VARIANTS;
    private boolean commonSpawn;
    
    private static int toVariant(final Variety variety, final DyeColor baseColor, final DyeColor patternColor) {
        return (variety.getShape() & 0xFF) | (variety.getPattern() & 0xFF) << 8 | (baseColor.getId() & 0xFF) << 16 | (patternColor.getId() & 0xFF) << 24;
    }
    
    public TropicalFishEntity(final EntityType<? extends TropicalFishEntity> type, final World world) {
        super(type, world);
        this.commonSpawn = true;
    }
    
    @Environment(EnvType.CLIENT)
    public static String getToolTipForVariant(final int variant) {
        return "entity.minecraft.tropical_fish.predefined." + variant;
    }
    
    @Environment(EnvType.CLIENT)
    public static DyeColor getBaseDyeColor(final int variant) {
        return DyeColor.byId(getBaseDyeColorIndex(variant));
    }
    
    @Environment(EnvType.CLIENT)
    public static DyeColor getPatternDyeColor(final int variant) {
        return DyeColor.byId(getPatternDyeColorIndex(variant));
    }
    
    @Environment(EnvType.CLIENT)
    public static String getTranslationKey(final int variant) {
        final int integer2 = getShape(variant);
        final int integer3 = getPattern(variant);
        return "entity.minecraft.tropical_fish.type." + Variety.getTranslateKey(integer2, integer3);
    }
    
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.<Integer>startTracking(TropicalFishEntity.VARIANT, 0);
    }
    
    @Override
    public void writeCustomDataToTag(final CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putInt("Variant", this.getVariant());
    }
    
    @Override
    public void readCustomDataFromTag(final CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        this.setVariant(tag.getInt("Variant"));
    }
    
    public void setVariant(final int integer) {
        this.dataTracker.<Integer>set(TropicalFishEntity.VARIANT, integer);
    }
    
    @Override
    public boolean spawnsTooManyForEachTry(final int count) {
        return !this.commonSpawn;
    }
    
    public int getVariant() {
        return this.dataTracker.<Integer>get(TropicalFishEntity.VARIANT);
    }
    
    @Override
    protected void copyDataToStack(final ItemStack itemStack) {
        super.copyDataToStack(itemStack);
        final CompoundTag compoundTag2 = itemStack.getOrCreateTag();
        compoundTag2.putInt("BucketVariantTag", this.getVariant());
    }
    
    @Override
    protected ItemStack getFishBucketItem() {
        return new ItemStack(Items.kK);
    }
    
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.lR;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.lS;
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource source) {
        return SoundEvents.lU;
    }
    
    @Override
    protected SoundEvent getFlopSound() {
        return SoundEvents.lT;
    }
    
    @Environment(EnvType.CLIENT)
    private static int getBaseDyeColorIndex(final int variant) {
        return (variant & 0xFF0000) >> 16;
    }
    
    @Environment(EnvType.CLIENT)
    public float[] getBaseColorComponents() {
        return DyeColor.byId(getBaseDyeColorIndex(this.getVariant())).getColorComponents();
    }
    
    @Environment(EnvType.CLIENT)
    private static int getPatternDyeColorIndex(final int variant) {
        return (variant & 0xFF000000) >> 24;
    }
    
    @Environment(EnvType.CLIENT)
    public float[] getPatternColorComponents() {
        return DyeColor.byId(getPatternDyeColorIndex(this.getVariant())).getColorComponents();
    }
    
    @Environment(EnvType.CLIENT)
    public static int getShape(final int variant) {
        return Math.min(variant & 0xFF, 1);
    }
    
    @Environment(EnvType.CLIENT)
    public int getShape() {
        return getShape(this.getVariant());
    }
    
    @Environment(EnvType.CLIENT)
    private static int getPattern(final int variant) {
        return Math.min((variant & 0xFF00) >> 8, 5);
    }
    
    @Environment(EnvType.CLIENT)
    public Identifier getVarietyId() {
        if (getShape(this.getVariant()) == 0) {
            return TropicalFishEntity.SMALL_FISH_VARIETY_IDS[getPattern(this.getVariant())];
        }
        return TropicalFishEntity.LARGE_FISH_VARIETY_IDS[getPattern(this.getVariant())];
    }
    
    @Environment(EnvType.CLIENT)
    public Identifier getShapeId() {
        return TropicalFishEntity.SHAPE_IDS[getShape(this.getVariant())];
    }
    
    @Nullable
    @Override
    public EntityData initialize(final IWorld iWorld, final LocalDifficulty localDifficulty, final SpawnType difficulty, @Nullable EntityData entityData, @Nullable final CompoundTag compoundTag) {
        entityData = super.initialize(iWorld, localDifficulty, difficulty, entityData, compoundTag);
        if (compoundTag != null && compoundTag.containsKey("BucketVariantTag", 3)) {
            this.setVariant(compoundTag.getInt("BucketVariantTag"));
            return entityData;
        }
        int integer6;
        int integer7;
        int integer8;
        int integer9;
        if (entityData instanceof Data) {
            final Data data10 = (Data)entityData;
            integer6 = data10.shape;
            integer7 = data10.pattern;
            integer8 = data10.baseColor;
            integer9 = data10.patternColor;
        }
        else if (this.random.nextFloat() < 0.9) {
            final int integer10 = TropicalFishEntity.COMMON_VARIANTS[this.random.nextInt(TropicalFishEntity.COMMON_VARIANTS.length)];
            integer6 = (integer10 & 0xFF);
            integer7 = (integer10 & 0xFF00) >> 8;
            integer8 = (integer10 & 0xFF0000) >> 16;
            integer9 = (integer10 & 0xFF000000) >> 24;
            entityData = new Data(this, integer6, integer7, integer8, integer9);
        }
        else {
            this.commonSpawn = false;
            integer6 = this.random.nextInt(2);
            integer7 = this.random.nextInt(6);
            integer8 = this.random.nextInt(15);
            integer9 = this.random.nextInt(15);
        }
        this.setVariant(integer6 | integer7 << 8 | integer8 << 16 | integer9 << 24);
        return entityData;
    }
    
    static {
        VARIANT = DataTracker.<Integer>registerData(TropicalFishEntity.class, TrackedDataHandlerRegistry.INTEGER);
        SHAPE_IDS = new Identifier[] { new Identifier("textures/entity/fish/tropical_a.png"), new Identifier("textures/entity/fish/tropical_b.png") };
        SMALL_FISH_VARIETY_IDS = new Identifier[] { new Identifier("textures/entity/fish/tropical_a_pattern_1.png"), new Identifier("textures/entity/fish/tropical_a_pattern_2.png"), new Identifier("textures/entity/fish/tropical_a_pattern_3.png"), new Identifier("textures/entity/fish/tropical_a_pattern_4.png"), new Identifier("textures/entity/fish/tropical_a_pattern_5.png"), new Identifier("textures/entity/fish/tropical_a_pattern_6.png") };
        LARGE_FISH_VARIETY_IDS = new Identifier[] { new Identifier("textures/entity/fish/tropical_b_pattern_1.png"), new Identifier("textures/entity/fish/tropical_b_pattern_2.png"), new Identifier("textures/entity/fish/tropical_b_pattern_3.png"), new Identifier("textures/entity/fish/tropical_b_pattern_4.png"), new Identifier("textures/entity/fish/tropical_b_pattern_5.png"), new Identifier("textures/entity/fish/tropical_b_pattern_6.png") };
        COMMON_VARIANTS = new int[] { toVariant(Variety.h, DyeColor.b, DyeColor.h), toVariant(Variety.g, DyeColor.h, DyeColor.h), toVariant(Variety.g, DyeColor.h, DyeColor.l), toVariant(Variety.l, DyeColor.a, DyeColor.h), toVariant(Variety.b, DyeColor.l, DyeColor.h), toVariant(Variety.a, DyeColor.b, DyeColor.a), toVariant(Variety.f, DyeColor.g, DyeColor.d), toVariant(Variety.j, DyeColor.k, DyeColor.e), toVariant(Variety.l, DyeColor.a, DyeColor.o), toVariant(Variety.f, DyeColor.a, DyeColor.e), toVariant(Variety.i, DyeColor.a, DyeColor.h), toVariant(Variety.l, DyeColor.a, DyeColor.b), toVariant(Variety.d, DyeColor.j, DyeColor.g), toVariant(Variety.e, DyeColor.f, DyeColor.d), toVariant(Variety.k, DyeColor.o, DyeColor.a), toVariant(Variety.c, DyeColor.h, DyeColor.o), toVariant(Variety.j, DyeColor.o, DyeColor.a), toVariant(Variety.g, DyeColor.a, DyeColor.e), toVariant(Variety.a, DyeColor.o, DyeColor.a), toVariant(Variety.b, DyeColor.h, DyeColor.a), toVariant(Variety.d, DyeColor.j, DyeColor.e), toVariant(Variety.g, DyeColor.e, DyeColor.e) };
    }
    
    enum Variety
    {
        a(0, 0), 
        b(0, 1), 
        c(0, 2), 
        d(0, 3), 
        e(0, 4), 
        f(0, 5), 
        g(1, 0), 
        h(1, 1), 
        i(1, 2), 
        j(1, 3), 
        k(1, 4), 
        l(1, 5);
        
        private final int shape;
        private final int pattern;
        private static final Variety[] VALUES;
        
        private Variety(final int shape, final int pattern) {
            this.shape = shape;
            this.pattern = pattern;
        }
        
        public int getShape() {
            return this.shape;
        }
        
        public int getPattern() {
            return this.pattern;
        }
        
        @Environment(EnvType.CLIENT)
        public static String getTranslateKey(final int shape, final int pattern) {
            return Variety.VALUES[pattern + 6 * shape].getTranslationKey();
        }
        
        @Environment(EnvType.CLIENT)
        public String getTranslationKey() {
            return this.name().toLowerCase(Locale.ROOT);
        }
        
        static {
            VALUES = values();
        }
    }
    
    static class Data extends SchoolingFishEntity.Data
    {
        private final int shape;
        private final int pattern;
        private final int baseColor;
        private final int patternColor;
        
        private Data(final TropicalFishEntity tropicalFishEntity, final int integer2, final int integer3, final int integer4, final int integer5) {
            super(tropicalFishEntity);
            this.shape = integer2;
            this.pattern = integer3;
            this.baseColor = integer4;
            this.patternColor = integer5;
        }
    }
}

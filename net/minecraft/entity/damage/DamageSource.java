package net.minecraft.entity.damage;

import net.minecraft.util.math.Vec3d;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import javax.annotation.Nullable;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

public class DamageSource
{
    public static final DamageSource IN_FIRE;
    public static final DamageSource LIGHTNING_BOLT;
    public static final DamageSource ON_FIRE;
    public static final DamageSource LAVA;
    public static final DamageSource HOT_FLOOR;
    public static final DamageSource IN_WALL;
    public static final DamageSource CRAMMING;
    public static final DamageSource DROWN;
    public static final DamageSource STARVE;
    public static final DamageSource CACTUS;
    public static final DamageSource FALL;
    public static final DamageSource FLY_INTO_WALL;
    public static final DamageSource OUT_OF_WORLD;
    public static final DamageSource GENERIC;
    public static final DamageSource MAGIC;
    public static final DamageSource WITHER;
    public static final DamageSource ANVIL;
    public static final DamageSource FALLING_BLOCK;
    public static final DamageSource DRAGON_BREATH;
    public static final DamageSource FIREWORKS;
    public static final DamageSource DRYOUT;
    public static final DamageSource SWEET_BERRY_BUSH;
    private boolean bypassesArmor;
    private boolean damageToCreative;
    private boolean unblockable;
    private float exhaustion;
    private boolean fire;
    private boolean projectile;
    private boolean scaleWithDifficulty;
    private boolean magic;
    private boolean explosive;
    public final String name;
    
    public static DamageSource mob(final LivingEntity attacker) {
        return new EntityDamageSource("mob", attacker);
    }
    
    public static DamageSource mobProjectile(final Entity projectile, final LivingEntity attacker) {
        return new ProjectileDamageSource("mob", projectile, attacker);
    }
    
    public static DamageSource player(final PlayerEntity attacker) {
        return new EntityDamageSource("player", attacker);
    }
    
    public static DamageSource arrow(final ProjectileEntity projectile, @Nullable final Entity attacker) {
        return new ProjectileDamageSource("arrow", projectile, attacker).setProjectile();
    }
    
    public static DamageSource trident(final Entity entity1, @Nullable final Entity entity2) {
        return new ProjectileDamageSource("trident", entity1, entity2).setProjectile();
    }
    
    public static DamageSource explosiveProjectile(final ExplosiveProjectileEntity projectile, @Nullable final Entity attacker) {
        if (attacker == null) {
            return new ProjectileDamageSource("onFire", projectile, projectile).setFire().setProjectile();
        }
        return new ProjectileDamageSource("fireball", projectile, attacker).setFire().setProjectile();
    }
    
    public static DamageSource thrownProjectile(final Entity projectile, @Nullable final Entity attacker) {
        return new ProjectileDamageSource("thrown", projectile, attacker).setProjectile();
    }
    
    public static DamageSource magic(final Entity magic, @Nullable final Entity attacker) {
        return new ProjectileDamageSource("indirectMagic", magic, attacker).setBypassesArmor().setUsesMagic();
    }
    
    public static DamageSource thorns(final Entity attacker) {
        return new EntityDamageSource("thorns", attacker).x().setUsesMagic();
    }
    
    public static DamageSource explosion(@Nullable final Explosion explosion) {
        if (explosion != null && explosion.getCausingEntity() != null) {
            return new EntityDamageSource("explosion.player", explosion.getCausingEntity()).setScaledWithDifficulty().setExplosive();
        }
        return new DamageSource("explosion").setScaledWithDifficulty().setExplosive();
    }
    
    public static DamageSource explosion(@Nullable final LivingEntity attacker) {
        if (attacker != null) {
            return new EntityDamageSource("explosion.player", attacker).setScaledWithDifficulty().setExplosive();
        }
        return new DamageSource("explosion").setScaledWithDifficulty().setExplosive();
    }
    
    public static DamageSource netherBed() {
        return new NetherBedDamageSource();
    }
    
    public boolean isProjectile() {
        return this.projectile;
    }
    
    public DamageSource setProjectile() {
        this.projectile = true;
        return this;
    }
    
    public boolean isExplosive() {
        return this.explosive;
    }
    
    public DamageSource setExplosive() {
        this.explosive = true;
        return this;
    }
    
    public boolean bypassesArmor() {
        return this.bypassesArmor;
    }
    
    public float getExhaustion() {
        return this.exhaustion;
    }
    
    public boolean doesDamageToCreative() {
        return this.damageToCreative;
    }
    
    public boolean isUnblockable() {
        return this.unblockable;
    }
    
    protected DamageSource(final String name) {
        this.exhaustion = 0.1f;
        this.name = name;
    }
    
    @Nullable
    public Entity getSource() {
        return this.getAttacker();
    }
    
    @Nullable
    public Entity getAttacker() {
        return null;
    }
    
    protected DamageSource setBypassesArmor() {
        this.bypassesArmor = true;
        this.exhaustion = 0.0f;
        return this;
    }
    
    protected DamageSource setDamageToCreative() {
        this.damageToCreative = true;
        return this;
    }
    
    protected DamageSource setUnblockable() {
        this.unblockable = true;
        this.exhaustion = 0.0f;
        return this;
    }
    
    protected DamageSource setFire() {
        this.fire = true;
        return this;
    }
    
    public TextComponent getDeathMessage(final LivingEntity livingEntity) {
        final LivingEntity livingEntity2 = livingEntity.cK();
        final String string3 = "death.attack." + this.name;
        final String string4 = string3 + ".player";
        if (livingEntity2 != null) {
            return new TranslatableTextComponent(string4, new Object[] { livingEntity.getDisplayName(), livingEntity2.getDisplayName() });
        }
        return new TranslatableTextComponent(string3, new Object[] { livingEntity.getDisplayName() });
    }
    
    public boolean isFire() {
        return this.fire;
    }
    
    public String getName() {
        return this.name;
    }
    
    public DamageSource setScaledWithDifficulty() {
        this.scaleWithDifficulty = true;
        return this;
    }
    
    public boolean isScaledWithDifficulty() {
        return this.scaleWithDifficulty;
    }
    
    public boolean getMagic() {
        return this.magic;
    }
    
    public DamageSource setUsesMagic() {
        this.magic = true;
        return this;
    }
    
    public boolean isSourceCreativePlayer() {
        final Entity entity1 = this.getAttacker();
        return entity1 instanceof PlayerEntity && ((PlayerEntity)entity1).abilities.creativeMode;
    }
    
    @Nullable
    public Vec3d w() {
        return null;
    }
    
    static {
        IN_FIRE = new DamageSource("inFire").setFire();
        LIGHTNING_BOLT = new DamageSource("lightningBolt");
        ON_FIRE = new DamageSource("onFire").setBypassesArmor().setFire();
        LAVA = new DamageSource("lava").setFire();
        HOT_FLOOR = new DamageSource("hotFloor").setFire();
        IN_WALL = new DamageSource("inWall").setBypassesArmor();
        CRAMMING = new DamageSource("cramming").setBypassesArmor();
        DROWN = new DamageSource("drown").setBypassesArmor();
        STARVE = new DamageSource("starve").setBypassesArmor().setUnblockable();
        CACTUS = new DamageSource("cactus");
        FALL = new DamageSource("fall").setBypassesArmor();
        FLY_INTO_WALL = new DamageSource("flyIntoWall").setBypassesArmor();
        OUT_OF_WORLD = new DamageSource("outOfWorld").setBypassesArmor().setDamageToCreative();
        GENERIC = new DamageSource("generic").setBypassesArmor();
        MAGIC = new DamageSource("magic").setBypassesArmor().setUsesMagic();
        WITHER = new DamageSource("wither").setBypassesArmor();
        ANVIL = new DamageSource("anvil");
        FALLING_BLOCK = new DamageSource("fallingBlock");
        DRAGON_BREATH = new DamageSource("dragonBreath").setBypassesArmor();
        FIREWORKS = new DamageSource("fireworks").setExplosive();
        DRYOUT = new DamageSource("dryout");
        SWEET_BERRY_BUSH = new DamageSource("sweetBerryBush");
    }
}

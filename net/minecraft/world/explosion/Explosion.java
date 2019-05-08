package net.minecraft.world.explosion;

import net.minecraft.entity.PrimedTntEntity;
import java.util.Iterator;
import net.minecraft.block.Blocks;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.loot.context.LootContextParameters;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.fluid.FluidState;
import net.minecraft.block.BlockState;
import java.util.Set;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.BlockView;
import com.google.common.collect.Sets;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.RayTraceContext;
import net.minecraft.util.math.MathHelper;
import com.google.common.collect.Maps;
import com.google.common.collect.Lists;
import java.util.Collection;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import javax.annotation.Nullable;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.player.PlayerEntity;
import java.util.Map;
import net.minecraft.util.math.BlockPos;
import java.util.List;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import java.util.Random;

public class Explosion
{
    private final boolean createFire;
    private final DestructionType blockDestructionType;
    private final Random random;
    private final World world;
    private final double x;
    private final double y;
    private final double z;
    private final Entity entity;
    private final float power;
    private DamageSource damageSource;
    private final List<BlockPos> affectedBlocks;
    private final Map<PlayerEntity, Vec3d> affectedPlayers;
    
    @Environment(EnvType.CLIENT)
    public Explosion(final World world, @Nullable final Entity entity, final double x, final double y, final double double7, final float float9, final List<BlockPos> list10) {
        this(world, entity, x, y, double7, float9, false, DestructionType.c, list10);
    }
    
    @Environment(EnvType.CLIENT)
    public Explosion(final World world, @Nullable final Entity entity, final double x, final double y, final double z, final float float9, final boolean boolean10, final DestructionType destructionType11, final List<BlockPos> list12) {
        this(world, entity, x, y, z, float9, boolean10, destructionType11);
        this.affectedBlocks.addAll(list12);
    }
    
    public Explosion(final World world, @Nullable final Entity entity, final double x, final double y, final double z, final float power, final boolean createFire, final DestructionType blockDestructionType) {
        this.random = new Random();
        this.affectedBlocks = Lists.newArrayList();
        this.affectedPlayers = Maps.newHashMap();
        this.world = world;
        this.entity = entity;
        this.power = power;
        this.x = x;
        this.y = y;
        this.z = z;
        this.createFire = createFire;
        this.blockDestructionType = blockDestructionType;
        this.damageSource = DamageSource.explosion(this);
    }
    
    public static float getExposure(final Vec3d source, final Entity entity) {
        final BoundingBox boundingBox3 = entity.getBoundingBox();
        final double double4 = 1.0 / ((boundingBox3.maxX - boundingBox3.minX) * 2.0 + 1.0);
        final double double5 = 1.0 / ((boundingBox3.maxY - boundingBox3.minY) * 2.0 + 1.0);
        final double double6 = 1.0 / ((boundingBox3.maxZ - boundingBox3.minZ) * 2.0 + 1.0);
        final double double7 = (1.0 - Math.floor(1.0 / double4) * double4) / 2.0;
        final double double8 = (1.0 - Math.floor(1.0 / double6) * double6) / 2.0;
        if (double4 < 0.0 || double5 < 0.0 || double6 < 0.0) {
            return 0.0f;
        }
        int integer14 = 0;
        int integer15 = 0;
        for (float float16 = 0.0f; float16 <= 1.0f; float16 += (float)double4) {
            for (float float17 = 0.0f; float17 <= 1.0f; float17 += (float)double5) {
                for (float float18 = 0.0f; float18 <= 1.0f; float18 += (float)double6) {
                    final double double9 = MathHelper.lerp(float16, boundingBox3.minX, boundingBox3.maxX);
                    final double double10 = MathHelper.lerp(float17, boundingBox3.minY, boundingBox3.maxY);
                    final double double11 = MathHelper.lerp(float18, boundingBox3.minZ, boundingBox3.maxZ);
                    final Vec3d vec3d25 = new Vec3d(double9 + double7, double10, double11 + double8);
                    if (entity.world.rayTrace(new RayTraceContext(vec3d25, source, RayTraceContext.ShapeType.b, RayTraceContext.FluidHandling.NONE, entity)).getType() == HitResult.Type.NONE) {
                        ++integer14;
                    }
                    ++integer15;
                }
            }
        }
        return integer14 / (float)integer15;
    }
    
    public void collectBlocksAndDamageEntities() {
        final Set<BlockPos> set1 = Sets.newHashSet();
        final int integer2 = 16;
        for (int integer3 = 0; integer3 < 16; ++integer3) {
            for (int integer4 = 0; integer4 < 16; ++integer4) {
                for (int integer5 = 0; integer5 < 16; ++integer5) {
                    if (integer3 == 0 || integer3 == 15 || integer4 == 0 || integer4 == 15 || integer5 == 0 || integer5 == 15) {
                        double double6 = integer3 / 15.0f * 2.0f - 1.0f;
                        double double7 = integer4 / 15.0f * 2.0f - 1.0f;
                        double double8 = integer5 / 15.0f * 2.0f - 1.0f;
                        final double double9 = Math.sqrt(double6 * double6 + double7 * double7 + double8 * double8);
                        double6 /= double9;
                        double7 /= double9;
                        double8 /= double9;
                        float float14 = this.power * (0.7f + this.world.random.nextFloat() * 0.6f);
                        double double10 = this.x;
                        double double11 = this.y;
                        double double12 = this.z;
                        final float float15 = 0.3f;
                        while (float14 > 0.0f) {
                            final BlockPos blockPos22 = new BlockPos(double10, double11, double12);
                            final BlockState blockState23 = this.world.getBlockState(blockPos22);
                            final FluidState fluidState24 = this.world.getFluidState(blockPos22);
                            if (!blockState23.isAir() || !fluidState24.isEmpty()) {
                                float float16 = Math.max(blockState23.getBlock().getBlastResistance(), fluidState24.getBlastResistance());
                                if (this.entity != null) {
                                    float16 = this.entity.getEffectiveExplosionResistance(this, this.world, blockPos22, blockState23, fluidState24, float16);
                                }
                                float14 -= (float16 + 0.3f) * 0.3f;
                            }
                            if (float14 > 0.0f && (this.entity == null || this.entity.canExplosionDestroyBlock(this, this.world, blockPos22, blockState23, float14))) {
                                set1.add(blockPos22);
                            }
                            double10 += double6 * 0.30000001192092896;
                            double11 += double7 * 0.30000001192092896;
                            double12 += double8 * 0.30000001192092896;
                            float14 -= 0.22500001f;
                        }
                    }
                }
            }
        }
        this.affectedBlocks.addAll(set1);
        final float float17 = this.power * 2.0f;
        int integer4 = MathHelper.floor(this.x - float17 - 1.0);
        int integer5 = MathHelper.floor(this.x + float17 + 1.0);
        final int integer6 = MathHelper.floor(this.y - float17 - 1.0);
        final int integer7 = MathHelper.floor(this.y + float17 + 1.0);
        final int integer8 = MathHelper.floor(this.z - float17 - 1.0);
        final int integer9 = MathHelper.floor(this.z + float17 + 1.0);
        final List<Entity> list10 = this.world.getEntities(this.entity, new BoundingBox(integer4, integer6, integer8, integer5, integer7, integer9));
        final Vec3d vec3d11 = new Vec3d(this.x, this.y, this.z);
        for (int integer10 = 0; integer10 < list10.size(); ++integer10) {
            final Entity entity13 = list10.get(integer10);
            if (!entity13.isImmuneToExplosion()) {
                final double double13 = MathHelper.sqrt(entity13.squaredDistanceTo(new Vec3d(this.x, this.y, this.z))) / float17;
                if (double13 <= 1.0) {
                    double double14 = entity13.x - this.x;
                    double double15 = entity13.y + entity13.getStandingEyeHeight() - this.y;
                    double double16 = entity13.z - this.z;
                    final double double17 = MathHelper.sqrt(double14 * double14 + double15 * double15 + double16 * double16);
                    if (double17 != 0.0) {
                        double14 /= double17;
                        double15 /= double17;
                        double16 /= double17;
                        final double double18 = getExposure(vec3d11, entity13);
                        final double double19 = (1.0 - double13) * double18;
                        entity13.damage(this.getDamageSource(), (float)(int)((double19 * double19 + double19) / 2.0 * 7.0 * float17 + 1.0));
                        double double20 = double19;
                        if (entity13 instanceof LivingEntity) {
                            double20 = ProtectionEnchantment.transformExplosionKnockback((LivingEntity)entity13, double19);
                        }
                        entity13.setVelocity(entity13.getVelocity().add(double14 * double20, double15 * double20, double16 * double20));
                        if (entity13 instanceof PlayerEntity) {
                            final PlayerEntity playerEntity30 = (PlayerEntity)entity13;
                            if (!playerEntity30.isSpectator() && (!playerEntity30.isCreative() || !playerEntity30.abilities.flying)) {
                                this.affectedPlayers.put(playerEntity30, new Vec3d(double14 * double19, double15 * double19, double16 * double19));
                            }
                        }
                    }
                }
            }
        }
    }
    
    public void affectWorld(final boolean boolean1) {
        this.world.playSound(null, this.x, this.y, this.z, SoundEvents.dJ, SoundCategory.e, 4.0f, (1.0f + (this.world.random.nextFloat() - this.world.random.nextFloat()) * 0.2f) * 0.7f);
        final boolean boolean2 = this.blockDestructionType != DestructionType.a;
        if (this.power < 2.0f || !boolean2) {
            this.world.addParticle(ParticleTypes.w, this.x, this.y, this.z, 1.0, 0.0, 0.0);
        }
        else {
            this.world.addParticle(ParticleTypes.v, this.x, this.y, this.z, 1.0, 0.0, 0.0);
        }
        if (boolean2) {
            for (final BlockPos blockPos4 : this.affectedBlocks) {
                final BlockState blockState5 = this.world.getBlockState(blockPos4);
                final Block block6 = blockState5.getBlock();
                if (boolean1) {
                    final double double7 = blockPos4.getX() + this.world.random.nextFloat();
                    final double double8 = blockPos4.getY() + this.world.random.nextFloat();
                    final double double9 = blockPos4.getZ() + this.world.random.nextFloat();
                    double double10 = double7 - this.x;
                    double double11 = double8 - this.y;
                    double double12 = double9 - this.z;
                    final double double13 = MathHelper.sqrt(double10 * double10 + double11 * double11 + double12 * double12);
                    double10 /= double13;
                    double11 /= double13;
                    double12 /= double13;
                    double double14 = 0.5 / (double13 / this.power + 0.1);
                    double14 *= this.world.random.nextFloat() * this.world.random.nextFloat() + 0.3f;
                    double10 *= double14;
                    double11 *= double14;
                    double12 *= double14;
                    this.world.addParticle(ParticleTypes.N, (double7 + this.x) / 2.0, (double8 + this.y) / 2.0, (double9 + this.z) / 2.0, double10, double11, double12);
                    this.world.addParticle(ParticleTypes.Q, double7, double8, double9, double10, double11, double12);
                }
                if (!blockState5.isAir()) {
                    if (block6.shouldDropItemsOnExplosion(this) && this.world instanceof ServerWorld) {
                        final BlockEntity blockEntity7 = block6.hasBlockEntity() ? this.world.getBlockEntity(blockPos4) : null;
                        final LootContext.Builder builder8 = new LootContext.Builder((ServerWorld)this.world).setRandom(this.world.random).<BlockPos>put(LootContextParameters.f, blockPos4).<ItemStack>put(LootContextParameters.i, ItemStack.EMPTY).<BlockEntity>putNullable(LootContextParameters.h, blockEntity7);
                        if (this.blockDestructionType == DestructionType.c) {
                            builder8.<Float>put(LootContextParameters.j, this.power);
                        }
                        Block.dropStacks(blockState5, builder8);
                    }
                    this.world.setBlockState(blockPos4, Blocks.AIR.getDefaultState(), 3);
                    block6.onDestroyedByExplosion(this.world, blockPos4, this);
                }
            }
        }
        if (this.createFire) {
            for (final BlockPos blockPos4 : this.affectedBlocks) {
                if (this.world.getBlockState(blockPos4).isAir() && this.world.getBlockState(blockPos4.down()).isFullOpaque(this.world, blockPos4.down()) && this.random.nextInt(3) == 0) {
                    this.world.setBlockState(blockPos4, Blocks.bM.getDefaultState());
                }
            }
        }
    }
    
    public DamageSource getDamageSource() {
        return this.damageSource;
    }
    
    public void setDamageSource(final DamageSource damageSource) {
        this.damageSource = damageSource;
    }
    
    public Map<PlayerEntity, Vec3d> getAffectedPlayers() {
        return this.affectedPlayers;
    }
    
    @Nullable
    public LivingEntity getCausingEntity() {
        if (this.entity == null) {
            return null;
        }
        if (this.entity instanceof PrimedTntEntity) {
            return ((PrimedTntEntity)this.entity).getCausingEntity();
        }
        if (this.entity instanceof LivingEntity) {
            return (LivingEntity)this.entity;
        }
        return null;
    }
    
    public void clearAffectedBlocks() {
        this.affectedBlocks.clear();
    }
    
    public List<BlockPos> getAffectedBlocks() {
        return this.affectedBlocks;
    }
    
    public enum DestructionType
    {
        a, 
        b, 
        c;
    }
}

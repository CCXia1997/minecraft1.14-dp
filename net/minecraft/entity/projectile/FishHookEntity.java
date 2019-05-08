package net.minecraft.entity.projectile;

import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.client.network.packet.EntitySpawnS2CPacket;
import net.minecraft.network.Packet;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;
import net.minecraft.world.loot.LootSupplier;
import net.minecraft.stat.Stats;
import net.minecraft.tag.ItemTags;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.world.loot.context.LootContextTypes;
import net.minecraft.world.loot.LootTables;
import net.minecraft.world.loot.context.LootContextParameters;
import net.minecraft.world.loot.context.LootContext;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.block.Block;
import net.minecraft.sound.SoundEvents;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.entity.ProjectileUtil;
import net.minecraft.world.RayTraceContext;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.fluid.FluidState;
import net.minecraft.entity.MovementType;
import net.minecraft.world.BlockView;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.Entity;

public class FishHookEntity extends Entity
{
    private static final TrackedData<Integer> HOOK_ENTITY_ID;
    private boolean d;
    private int e;
    private final PlayerEntity owner;
    private int g;
    private int ar;
    private int as;
    private int at;
    private float au;
    public Entity hookedEntity;
    private State state;
    private final int aw;
    private final int ax;
    
    private FishHookEntity(final World world, final PlayerEntity playerEntity, final int integer3, final int integer4) {
        super(EntityType.FISHING_BOBBER, world);
        this.state = State.a;
        this.ignoreCameraFrustum = true;
        this.owner = playerEntity;
        this.owner.fishHook = this;
        this.aw = Math.max(0, integer3);
        this.ax = Math.max(0, integer4);
    }
    
    @Environment(EnvType.CLIENT)
    public FishHookEntity(final World world, final PlayerEntity playerEntity, final double double3, final double double5, final double double7) {
        this(world, playerEntity, 0, 0);
        this.setPosition(double3, double5, double7);
        this.prevX = this.x;
        this.prevY = this.y;
        this.prevZ = this.z;
    }
    
    public FishHookEntity(final PlayerEntity playerEntity, final World world, final int integer3, final int integer4) {
        this(world, playerEntity, integer3, integer4);
        final float float5 = this.owner.pitch;
        final float float6 = this.owner.yaw;
        final float float7 = MathHelper.cos(-float6 * 0.017453292f - 3.1415927f);
        final float float8 = MathHelper.sin(-float6 * 0.017453292f - 3.1415927f);
        final float float9 = -MathHelper.cos(-float5 * 0.017453292f);
        final float float10 = MathHelper.sin(-float5 * 0.017453292f);
        final double double11 = this.owner.x - float8 * 0.3;
        final double double12 = this.owner.y + this.owner.getStandingEyeHeight();
        final double double13 = this.owner.z - float7 * 0.3;
        this.setPositionAndAngles(double11, double12, double13, float6, float5);
        Vec3d vec3d17 = new Vec3d(-float8, MathHelper.clamp(-(float10 / float9), -5.0f, 5.0f), -float7);
        final double double14 = vec3d17.length();
        vec3d17 = vec3d17.multiply(0.6 / double14 + 0.5 + this.random.nextGaussian() * 0.0045, 0.6 / double14 + 0.5 + this.random.nextGaussian() * 0.0045, 0.6 / double14 + 0.5 + this.random.nextGaussian() * 0.0045);
        this.setVelocity(vec3d17);
        this.yaw = (float)(MathHelper.atan2(vec3d17.x, vec3d17.z) * 57.2957763671875);
        this.pitch = (float)(MathHelper.atan2(vec3d17.y, MathHelper.sqrt(Entity.squaredHorizontalLength(vec3d17))) * 57.2957763671875);
        this.prevYaw = this.yaw;
        this.prevPitch = this.pitch;
    }
    
    @Override
    protected void initDataTracker() {
        this.getDataTracker().<Integer>startTracking(FishHookEntity.HOOK_ENTITY_ID, 0);
    }
    
    @Override
    public void onTrackedDataSet(final TrackedData<?> data) {
        if (FishHookEntity.HOOK_ENTITY_ID.equals(data)) {
            final int integer2 = this.getDataTracker().<Integer>get(FishHookEntity.HOOK_ENTITY_ID);
            this.hookedEntity = ((integer2 > 0) ? this.world.getEntityById(integer2 - 1) : null);
        }
        super.onTrackedDataSet(data);
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public boolean shouldRenderAtDistance(final double distance) {
        final double double3 = 64.0;
        return distance < 4096.0;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void setPositionAndRotations(final double x, final double y, final double z, final float float7, final float float8, final int integer9, final boolean boolean10) {
    }
    
    @Override
    public void tick() {
        super.tick();
        if (this.owner == null) {
            this.remove();
            return;
        }
        if (!this.world.isClient && this.k()) {
            return;
        }
        if (this.d) {
            ++this.e;
            if (this.e >= 1200) {
                this.remove();
                return;
            }
        }
        float float1 = 0.0f;
        final BlockPos blockPos2 = new BlockPos(this);
        final FluidState fluidState3 = this.world.getFluidState(blockPos2);
        if (fluidState3.matches(FluidTags.a)) {
            float1 = fluidState3.getHeight(this.world, blockPos2);
        }
        if (this.state == State.a) {
            if (this.hookedEntity != null) {
                this.setVelocity(Vec3d.ZERO);
                this.state = State.b;
                return;
            }
            if (float1 > 0.0f) {
                this.setVelocity(this.getVelocity().multiply(0.3, 0.2, 0.3));
                this.state = State.c;
                return;
            }
            if (!this.world.isClient) {
                this.m();
            }
            if (this.d || this.onGround || this.horizontalCollision) {
                this.g = 0;
                this.setVelocity(Vec3d.ZERO);
            }
            else {
                ++this.g;
            }
        }
        else {
            if (this.state == State.b) {
                if (this.hookedEntity != null) {
                    if (this.hookedEntity.removed) {
                        this.hookedEntity = null;
                        this.state = State.a;
                    }
                    else {
                        this.x = this.hookedEntity.x;
                        this.y = this.hookedEntity.getBoundingBox().minY + this.hookedEntity.getHeight() * 0.8;
                        this.z = this.hookedEntity.z;
                        this.setPosition(this.x, this.y, this.z);
                    }
                }
                return;
            }
            if (this.state == State.c) {
                final Vec3d vec3d4 = this.getVelocity();
                double double5 = this.y + vec3d4.y - blockPos2.getY() - float1;
                if (Math.abs(double5) < 0.01) {
                    double5 += Math.signum(double5) * 0.1;
                }
                this.setVelocity(vec3d4.x * 0.9, vec3d4.y - double5 * this.random.nextFloat() * 0.2, vec3d4.z * 0.9);
                if (!this.world.isClient && float1 > 0.0f) {
                    this.a(blockPos2);
                }
            }
        }
        if (!fluidState3.matches(FluidTags.a)) {
            this.setVelocity(this.getVelocity().add(0.0, -0.03, 0.0));
        }
        this.move(MovementType.a, this.getVelocity());
        this.l();
        final double double6 = 0.92;
        this.setVelocity(this.getVelocity().multiply(0.92));
        this.setPosition(this.x, this.y, this.z);
    }
    
    private boolean k() {
        final ItemStack itemStack1 = this.owner.getMainHandStack();
        final ItemStack itemStack2 = this.owner.getOffHandStack();
        final boolean boolean3 = itemStack1.getItem() == Items.kY;
        final boolean boolean4 = itemStack2.getItem() == Items.kY;
        if (this.owner.removed || !this.owner.isAlive() || (!boolean3 && !boolean4) || this.squaredDistanceTo(this.owner) > 1024.0) {
            this.remove();
            return true;
        }
        return false;
    }
    
    private void l() {
        final Vec3d vec3d1 = this.getVelocity();
        final float float2 = MathHelper.sqrt(Entity.squaredHorizontalLength(vec3d1));
        this.yaw = (float)(MathHelper.atan2(vec3d1.x, vec3d1.z) * 57.2957763671875);
        this.pitch = (float)(MathHelper.atan2(vec3d1.y, float2) * 57.2957763671875);
        while (this.pitch - this.prevPitch < -180.0f) {
            this.prevPitch -= 360.0f;
        }
        while (this.pitch - this.prevPitch >= 180.0f) {
            this.prevPitch += 360.0f;
        }
        while (this.yaw - this.prevYaw < -180.0f) {
            this.prevYaw -= 360.0f;
        }
        while (this.yaw - this.prevYaw >= 180.0f) {
            this.prevYaw += 360.0f;
        }
        this.pitch = MathHelper.lerp(0.2f, this.prevPitch, this.pitch);
        this.yaw = MathHelper.lerp(0.2f, this.prevYaw, this.yaw);
    }
    
    private void m() {
        final HitResult hitResult1 = ProjectileUtil.getCollision(this, this.getBoundingBox().stretch(this.getVelocity()).expand(1.0), entity -> !entity.isSpectator() && (entity.collides() || entity instanceof ItemEntity) && (entity != this.owner || this.g >= 5), RayTraceContext.ShapeType.a, true);
        if (hitResult1.getType() != HitResult.Type.NONE) {
            if (hitResult1.getType() == HitResult.Type.ENTITY) {
                this.hookedEntity = ((EntityHitResult)hitResult1).getEntity();
                this.n();
            }
            else {
                this.d = true;
            }
        }
    }
    
    private void n() {
        this.getDataTracker().<Integer>set(FishHookEntity.HOOK_ENTITY_ID, this.hookedEntity.getEntityId() + 1);
    }
    
    private void a(final BlockPos blockPos) {
        final ServerWorld serverWorld2 = (ServerWorld)this.world;
        int integer3 = 1;
        final BlockPos blockPos2 = blockPos.up();
        if (this.random.nextFloat() < 0.25f && this.world.hasRain(blockPos2)) {
            ++integer3;
        }
        if (this.random.nextFloat() < 0.5f && !this.world.isSkyVisible(blockPos2)) {
            --integer3;
        }
        if (this.ar > 0) {
            --this.ar;
            if (this.ar <= 0) {
                this.as = 0;
                this.at = 0;
            }
            else {
                this.setVelocity(this.getVelocity().add(0.0, -0.2 * this.random.nextFloat() * this.random.nextFloat(), 0.0));
            }
        }
        else if (this.at > 0) {
            this.at -= integer3;
            if (this.at > 0) {
                this.au += (float)(this.random.nextGaussian() * 4.0);
                final float float5 = this.au * 0.017453292f;
                final float float6 = MathHelper.sin(float5);
                final float float7 = MathHelper.cos(float5);
                final double double8 = this.x + float6 * this.at * 0.1f;
                final double double9 = MathHelper.floor(this.getBoundingBox().minY) + 1.0f;
                final double double10 = this.z + float7 * this.at * 0.1f;
                final Block block14 = serverWorld2.getBlockState(new BlockPos(double8, double9 - 1.0, double10)).getBlock();
                if (block14 == Blocks.A) {
                    if (this.random.nextFloat() < 0.15f) {
                        serverWorld2.<DefaultParticleType>spawnParticles(ParticleTypes.e, double8, double9 - 0.10000000149011612, double10, 1, float6, 0.1, float7, 0.0);
                    }
                    final float float8 = float6 * 0.04f;
                    final float float9 = float7 * 0.04f;
                    serverWorld2.<DefaultParticleType>spawnParticles(ParticleTypes.z, double8, double9, double10, 0, float9, 0.01, -float8, 1.0);
                    serverWorld2.<DefaultParticleType>spawnParticles(ParticleTypes.z, double8, double9, double10, 0, -float9, 0.01, float8, 1.0);
                }
            }
            else {
                final Vec3d vec3d5 = this.getVelocity();
                this.setVelocity(vec3d5.x, -0.4f * MathHelper.nextFloat(this.random, 0.6f, 1.0f), vec3d5.z);
                this.playSound(SoundEvents.ak, 0.25f, 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.4f);
                final double double11 = this.getBoundingBox().minY + 0.5;
                serverWorld2.<DefaultParticleType>spawnParticles(ParticleTypes.e, this.x, double11, this.z, (int)(1.0f + this.getWidth() * 20.0f), this.getWidth(), 0.0, this.getWidth(), 0.20000000298023224);
                serverWorld2.<DefaultParticleType>spawnParticles(ParticleTypes.z, this.x, double11, this.z, (int)(1.0f + this.getWidth() * 20.0f), this.getWidth(), 0.0, this.getWidth(), 0.20000000298023224);
                this.ar = MathHelper.nextInt(this.random, 20, 40);
            }
        }
        else if (this.as > 0) {
            this.as -= integer3;
            float float5 = 0.15f;
            if (this.as < 20) {
                float5 += (float)((20 - this.as) * 0.05);
            }
            else if (this.as < 40) {
                float5 += (float)((40 - this.as) * 0.02);
            }
            else if (this.as < 60) {
                float5 += (float)((60 - this.as) * 0.01);
            }
            if (this.random.nextFloat() < float5) {
                final float float6 = MathHelper.nextFloat(this.random, 0.0f, 360.0f) * 0.017453292f;
                final float float7 = MathHelper.nextFloat(this.random, 25.0f, 60.0f);
                final double double8 = this.x + MathHelper.sin(float6) * float7 * 0.1f;
                final double double9 = MathHelper.floor(this.getBoundingBox().minY) + 1.0f;
                final double double10 = this.z + MathHelper.cos(float6) * float7 * 0.1f;
                final Block block14 = serverWorld2.getBlockState(new BlockPos((int)double8, (int)double9 - 1, (int)double10)).getBlock();
                if (block14 == Blocks.A) {
                    serverWorld2.<DefaultParticleType>spawnParticles(ParticleTypes.X, double8, double9, double10, 2 + this.random.nextInt(2), 0.10000000149011612, 0.0, 0.10000000149011612, 0.0);
                }
            }
            if (this.as <= 0) {
                this.au = MathHelper.nextFloat(this.random, 0.0f, 360.0f);
                this.at = MathHelper.nextInt(this.random, 20, 80);
            }
        }
        else {
            this.as = MathHelper.nextInt(this.random, 100, 600);
            this.as -= this.ax * 20 * 5;
        }
    }
    
    public void writeCustomDataToTag(final CompoundTag tag) {
    }
    
    public void readCustomDataFromTag(final CompoundTag tag) {
    }
    
    public int b(final ItemStack itemStack) {
        if (this.world.isClient || this.owner == null) {
            return 0;
        }
        int integer2 = 0;
        if (this.hookedEntity != null) {
            this.f();
            Criterions.FISHING_ROD_HOOKED.handle((ServerPlayerEntity)this.owner, itemStack, this, Collections.emptyList());
            this.world.sendEntityStatus(this, (byte)31);
            integer2 = ((this.hookedEntity instanceof ItemEntity) ? 3 : 5);
        }
        else if (this.ar > 0) {
            final LootContext.Builder builder3 = new LootContext.Builder((ServerWorld)this.world).<BlockPos>put(LootContextParameters.f, new BlockPos(this)).<ItemStack>put(LootContextParameters.i, itemStack).setRandom(this.random).setLuck(this.aw + this.owner.getLuck());
            final LootSupplier lootSupplier4 = this.world.getServer().getLootManager().getSupplier(LootTables.GAMEPLAY_FISHING);
            final List<ItemStack> list5 = lootSupplier4.getDrops(builder3.build(LootContextTypes.FISHING));
            Criterions.FISHING_ROD_HOOKED.handle((ServerPlayerEntity)this.owner, itemStack, this, list5);
            for (final ItemStack itemStack2 : list5) {
                final ItemEntity itemEntity8 = new ItemEntity(this.world, this.x, this.y, this.z, itemStack2);
                final double double9 = this.owner.x - this.x;
                final double double10 = this.owner.y - this.y;
                final double double11 = this.owner.z - this.z;
                final double double12 = 0.1;
                itemEntity8.setVelocity(double9 * 0.1, double10 * 0.1 + Math.sqrt(Math.sqrt(double9 * double9 + double10 * double10 + double11 * double11)) * 0.08, double11 * 0.1);
                this.world.spawnEntity(itemEntity8);
                this.owner.world.spawnEntity(new ExperienceOrbEntity(this.owner.world, this.owner.x, this.owner.y + 0.5, this.owner.z + 0.5, this.random.nextInt(6) + 1));
                if (itemStack2.getItem().matches(ItemTags.I)) {
                    this.owner.increaseStat(Stats.P, 1);
                }
            }
            integer2 = 1;
        }
        if (this.d) {
            integer2 = 2;
        }
        this.remove();
        return integer2;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void handleStatus(final byte status) {
        if (status == 31 && this.world.isClient && this.hookedEntity instanceof PlayerEntity && ((PlayerEntity)this.hookedEntity).isMainPlayer()) {
            this.f();
        }
        super.handleStatus(status);
    }
    
    protected void f() {
        if (this.owner == null) {
            return;
        }
        final Vec3d vec3d1 = new Vec3d(this.owner.x - this.x, this.owner.y - this.y, this.owner.z - this.z).multiply(0.1);
        this.hookedEntity.setVelocity(this.hookedEntity.getVelocity().add(vec3d1));
    }
    
    @Override
    protected boolean canClimb() {
        return false;
    }
    
    @Override
    public void remove() {
        super.remove();
        if (this.owner != null) {
            this.owner.fishHook = null;
        }
    }
    
    @Nullable
    public PlayerEntity getOwner() {
        return this.owner;
    }
    
    @Override
    public boolean canUsePortals() {
        return false;
    }
    
    @Override
    public Packet<?> createSpawnPacket() {
        final Entity entity1 = this.getOwner();
        return new EntitySpawnS2CPacket(this, (entity1 == null) ? this.getEntityId() : entity1.getEntityId());
    }
    
    static {
        HOOK_ENTITY_ID = DataTracker.<Integer>registerData(FishHookEntity.class, TrackedDataHandlerRegistry.INTEGER);
    }
    
    enum State
    {
        a, 
        b, 
        c;
    }
}

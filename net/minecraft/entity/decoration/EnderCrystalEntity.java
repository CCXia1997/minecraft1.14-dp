package net.minecraft.entity.decoration;

import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.client.network.packet.EntitySpawnS2CPacket;
import net.minecraft.network.Packet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import javax.annotation.Nullable;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.nbt.Tag;
import net.minecraft.util.TagHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.block.Blocks;
import net.minecraft.world.dimension.TheEndDimension;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import java.util.Optional;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.Entity;

public class EnderCrystalEntity extends Entity
{
    private static final TrackedData<Optional<BlockPos>> BEAM_TARGET;
    private static final TrackedData<Boolean> SHOW_BOTTOM;
    public int b;
    
    public EnderCrystalEntity(final EntityType<? extends EnderCrystalEntity> type, final World world) {
        super(type, world);
        this.i = true;
        this.b = this.random.nextInt(100000);
    }
    
    public EnderCrystalEntity(final World world, final double double2, final double double4, final double double6) {
        this(EntityType.END_CRYSTAL, world);
        this.setPosition(double2, double4, double6);
    }
    
    @Override
    protected boolean canClimb() {
        return false;
    }
    
    @Override
    protected void initDataTracker() {
        this.getDataTracker().<Optional<BlockPos>>startTracking(EnderCrystalEntity.BEAM_TARGET, Optional.<BlockPos>empty());
        this.getDataTracker().<Boolean>startTracking(EnderCrystalEntity.SHOW_BOTTOM, true);
    }
    
    @Override
    public void tick() {
        this.prevX = this.x;
        this.prevY = this.y;
        this.prevZ = this.z;
        ++this.b;
        if (!this.world.isClient) {
            final BlockPos blockPos1 = new BlockPos(this);
            if (this.world.dimension instanceof TheEndDimension && this.world.getBlockState(blockPos1).isAir()) {
                this.world.setBlockState(blockPos1, Blocks.bM.getDefaultState());
            }
        }
    }
    
    @Override
    protected void writeCustomDataToTag(final CompoundTag tag) {
        if (this.getBeamTarget() != null) {
            tag.put("BeamTarget", TagHelper.serializeBlockPos(this.getBeamTarget()));
        }
        tag.putBoolean("ShowBottom", this.getShowBottom());
    }
    
    @Override
    protected void readCustomDataFromTag(final CompoundTag tag) {
        if (tag.containsKey("BeamTarget", 10)) {
            this.setBeamTarget(TagHelper.deserializeBlockPos(tag.getCompound("BeamTarget")));
        }
        if (tag.containsKey("ShowBottom", 1)) {
            this.setShowBottom(tag.getBoolean("ShowBottom"));
        }
    }
    
    @Override
    public boolean collides() {
        return true;
    }
    
    @Override
    public boolean damage(final DamageSource source, final float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        if (source.getAttacker() instanceof EnderDragonEntity) {
            return false;
        }
        if (!this.removed && !this.world.isClient) {
            this.remove();
            if (!this.world.isClient) {
                if (!source.isExplosive()) {
                    this.world.createExplosion(null, this.x, this.y, this.z, 6.0f, Explosion.DestructionType.c);
                }
                this.crystalDestroyed(source);
            }
        }
        return true;
    }
    
    @Override
    public void kill() {
        this.crystalDestroyed(DamageSource.GENERIC);
        super.kill();
    }
    
    private void crystalDestroyed(final DamageSource source) {
        if (this.world.dimension instanceof TheEndDimension) {
            final TheEndDimension theEndDimension2 = (TheEndDimension)this.world.dimension;
            final EnderDragonFight enderDragonFight3 = theEndDimension2.q();
            if (enderDragonFight3 != null) {
                enderDragonFight3.crystalDestroyed(this, source);
            }
        }
    }
    
    public void setBeamTarget(@Nullable final BlockPos blockPos) {
        this.getDataTracker().<Optional<BlockPos>>set(EnderCrystalEntity.BEAM_TARGET, Optional.<BlockPos>ofNullable(blockPos));
    }
    
    @Nullable
    public BlockPos getBeamTarget() {
        return this.getDataTracker().<Optional<BlockPos>>get(EnderCrystalEntity.BEAM_TARGET).orElse(null);
    }
    
    public void setShowBottom(final boolean boolean1) {
        this.getDataTracker().<Boolean>set(EnderCrystalEntity.SHOW_BOTTOM, boolean1);
    }
    
    public boolean getShowBottom() {
        return this.getDataTracker().<Boolean>get(EnderCrystalEntity.SHOW_BOTTOM);
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public boolean shouldRenderAtDistance(final double distance) {
        return super.shouldRenderAtDistance(distance) || this.getBeamTarget() != null;
    }
    
    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }
    
    static {
        BEAM_TARGET = DataTracker.<Optional<BlockPos>>registerData(EnderCrystalEntity.class, TrackedDataHandlerRegistry.OPTIONA_BLOCK_POS);
        SHOW_BOTTOM = DataTracker.<Boolean>registerData(EnderCrystalEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    }
}

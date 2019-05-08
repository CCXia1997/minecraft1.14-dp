package net.minecraft.entity.passive;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.entity.Entity;
import javax.annotation.Nullable;
import net.minecraft.util.Hand;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.SpawnType;
import net.minecraft.world.IWorld;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ViewableWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.block.Blocks;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import java.util.UUID;
import net.minecraft.block.Block;

public abstract class AnimalEntity extends PassiveEntity
{
    protected Block spawningGround;
    private int loveTicks;
    private UUID lovingPlayer;
    
    protected AnimalEntity(final EntityType<? extends AnimalEntity> type, final World world) {
        super(type, world);
        this.spawningGround = Blocks.i;
    }
    
    @Override
    protected void mobTick() {
        if (this.getBreedingAge() != 0) {
            this.loveTicks = 0;
        }
        super.mobTick();
    }
    
    @Override
    public void updateState() {
        super.updateState();
        if (this.getBreedingAge() != 0) {
            this.loveTicks = 0;
        }
        if (this.loveTicks > 0) {
            --this.loveTicks;
            if (this.loveTicks % 10 == 0) {
                final double double1 = this.random.nextGaussian() * 0.02;
                final double double2 = this.random.nextGaussian() * 0.02;
                final double double3 = this.random.nextGaussian() * 0.02;
                this.world.addParticle(ParticleTypes.E, this.x + this.random.nextFloat() * this.getWidth() * 2.0f - this.getWidth(), this.y + 0.5 + this.random.nextFloat() * this.getHeight(), this.z + this.random.nextFloat() * this.getWidth() * 2.0f - this.getWidth(), double1, double2, double3);
            }
        }
    }
    
    @Override
    public boolean damage(final DamageSource source, final float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        this.loveTicks = 0;
        return super.damage(source, amount);
    }
    
    @Override
    public float getPathfindingFavor(final BlockPos pos, final ViewableWorld world) {
        if (world.getBlockState(pos.down()).getBlock() == this.spawningGround) {
            return 10.0f;
        }
        return world.getBrightness(pos) - 0.5f;
    }
    
    @Override
    public void writeCustomDataToTag(final CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putInt("InLove", this.loveTicks);
        if (this.lovingPlayer != null) {
            tag.putUuid("LoveCause", this.lovingPlayer);
        }
    }
    
    @Override
    public double getHeightOffset() {
        return 0.14;
    }
    
    @Override
    public void readCustomDataFromTag(final CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        this.loveTicks = tag.getInt("InLove");
        this.lovingPlayer = (tag.hasUuid("LoveCause") ? tag.getUuid("LoveCause") : null);
    }
    
    @Override
    public boolean canSpawn(final IWorld iWorld, final SpawnType spawnType) {
        final int integer3 = MathHelper.floor(this.x);
        final int integer4 = MathHelper.floor(this.getBoundingBox().minY);
        final int integer5 = MathHelper.floor(this.z);
        final BlockPos blockPos6 = new BlockPos(integer3, integer4, integer5);
        return iWorld.getBlockState(blockPos6.down()).getBlock() == this.spawningGround && iWorld.getLightLevel(blockPos6, 0) > 8 && super.canSpawn(iWorld, spawnType);
    }
    
    @Override
    public int getMinAmbientSoundDelay() {
        return 120;
    }
    
    @Override
    public boolean canImmediatelyDespawn(final double distanceSquared) {
        return false;
    }
    
    @Override
    protected int getCurrentExperience(final PlayerEntity playerEntity) {
        return 1 + this.world.random.nextInt(3);
    }
    
    public boolean isBreedingItem(final ItemStack stack) {
        return stack.getItem() == Items.jP;
    }
    
    @Override
    public boolean interactMob(final PlayerEntity player, final Hand hand) {
        final ItemStack itemStack3 = player.getStackInHand(hand);
        if (this.isBreedingItem(itemStack3)) {
            if (this.getBreedingAge() == 0 && this.canEat()) {
                this.eat(player, itemStack3);
                this.lovePlayer(player);
                return true;
            }
            if (this.isChild()) {
                this.eat(player, itemStack3);
                this.growUp((int)(-this.getBreedingAge() / 20 * 0.1f), true);
                return true;
            }
        }
        return super.interactMob(player, hand);
    }
    
    protected void eat(final PlayerEntity player, final ItemStack stack) {
        if (!player.abilities.creativeMode) {
            stack.subtractAmount(1);
        }
    }
    
    public boolean canEat() {
        return this.loveTicks <= 0;
    }
    
    public void lovePlayer(@Nullable final PlayerEntity player) {
        this.loveTicks = 600;
        if (player != null) {
            this.lovingPlayer = player.getUuid();
        }
        this.world.sendEntityStatus(this, (byte)18);
    }
    
    public void setLoveTicks(final int loveTicks) {
        this.loveTicks = loveTicks;
    }
    
    @Nullable
    public ServerPlayerEntity getLovingPlayer() {
        if (this.lovingPlayer == null) {
            return null;
        }
        final PlayerEntity playerEntity1 = this.world.getPlayerByUuid(this.lovingPlayer);
        if (playerEntity1 instanceof ServerPlayerEntity) {
            return (ServerPlayerEntity)playerEntity1;
        }
        return null;
    }
    
    public boolean isInLove() {
        return this.loveTicks > 0;
    }
    
    public void resetLoveTicks() {
        this.loveTicks = 0;
    }
    
    public boolean canBreedWith(final AnimalEntity other) {
        return other != this && other.getClass() == this.getClass() && this.isInLove() && other.isInLove();
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void handleStatus(final byte status) {
        if (status == 18) {
            for (int integer2 = 0; integer2 < 7; ++integer2) {
                final double double3 = this.random.nextGaussian() * 0.02;
                final double double4 = this.random.nextGaussian() * 0.02;
                final double double5 = this.random.nextGaussian() * 0.02;
                this.world.addParticle(ParticleTypes.E, this.x + this.random.nextFloat() * this.getWidth() * 2.0f - this.getWidth(), this.y + 0.5 + this.random.nextFloat() * this.getHeight(), this.z + this.random.nextFloat() * this.getWidth() * 2.0f - this.getWidth(), double3, double4, double5);
            }
        }
        else {
            super.handleStatus(status);
        }
    }
}

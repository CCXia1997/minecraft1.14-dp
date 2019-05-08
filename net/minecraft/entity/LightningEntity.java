package net.minecraft.entity;

import net.minecraft.client.network.packet.EntitySpawnGlobalS2CPacket;
import net.minecraft.network.Packet;
import net.minecraft.nbt.CompoundTag;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.world.ViewableWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.Blocks;
import java.util.Iterator;
import java.util.List;
import java.util.Collection;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundCategory;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import javax.annotation.Nullable;
import net.minecraft.server.network.ServerPlayerEntity;

public class LightningEntity extends Entity
{
    private int ambientTick;
    public long seed;
    private int remainingActions;
    private final boolean cosmetic;
    @Nullable
    private ServerPlayerEntity channeller;
    
    public LightningEntity(final World world, final double x, final double y, final double z, final boolean cosmetic) {
        super(EntityType.LIGHTNING_BOLT, world);
        this.ignoreCameraFrustum = true;
        this.setPositionAndAngles(x, y, z, 0.0f, 0.0f);
        this.ambientTick = 2;
        this.seed = this.random.nextLong();
        this.remainingActions = this.random.nextInt(3) + 1;
        this.cosmetic = cosmetic;
        final Difficulty difficulty9 = world.getDifficulty();
        if (difficulty9 == Difficulty.NORMAL || difficulty9 == Difficulty.HARD) {
            this.spawnFire(4);
        }
    }
    
    @Override
    public SoundCategory getSoundCategory() {
        return SoundCategory.d;
    }
    
    public void setChanneller(@Nullable final ServerPlayerEntity channeller) {
        this.channeller = channeller;
    }
    
    @Override
    public void tick() {
        super.tick();
        if (this.ambientTick == 2) {
            this.world.playSound(null, this.x, this.y, this.z, SoundEvents.fX, SoundCategory.d, 10000.0f, 0.8f + this.random.nextFloat() * 0.2f);
            this.world.playSound(null, this.x, this.y, this.z, SoundEvents.fW, SoundCategory.d, 2.0f, 0.5f + this.random.nextFloat() * 0.2f);
        }
        --this.ambientTick;
        if (this.ambientTick < 0) {
            if (this.remainingActions == 0) {
                this.remove();
            }
            else if (this.ambientTick < -this.random.nextInt(10)) {
                --this.remainingActions;
                this.ambientTick = 1;
                this.seed = this.random.nextLong();
                this.spawnFire(0);
            }
        }
        if (this.ambientTick >= 0) {
            if (this.world.isClient) {
                this.world.setTicksSinceLightning(2);
            }
            else if (!this.cosmetic) {
                final double double1 = 3.0;
                final List<Entity> list3 = this.world.getEntities(this, new BoundingBox(this.x - 3.0, this.y - 3.0, this.z - 3.0, this.x + 3.0, this.y + 6.0 + 3.0, this.z + 3.0), Entity::isAlive);
                for (final Entity entity5 : list3) {
                    entity5.onStruckByLightning(this);
                }
                if (this.channeller != null) {
                    Criterions.CHANNELED_LIGHTNING.handle(this.channeller, list3);
                }
            }
        }
    }
    
    private void spawnFire(final int spreadAttempts) {
        if (this.cosmetic || this.world.isClient || !this.world.getGameRules().getBoolean("doFireTick")) {
            return;
        }
        final BlockState blockState2 = Blocks.bM.getDefaultState();
        final BlockPos blockPos3 = new BlockPos(this);
        if (this.world.getBlockState(blockPos3).isAir() && blockState2.canPlaceAt(this.world, blockPos3)) {
            this.world.setBlockState(blockPos3, blockState2);
        }
        for (int integer4 = 0; integer4 < spreadAttempts; ++integer4) {
            final BlockPos blockPos4 = blockPos3.add(this.random.nextInt(3) - 1, this.random.nextInt(3) - 1, this.random.nextInt(3) - 1);
            if (this.world.getBlockState(blockPos4).isAir() && blockState2.canPlaceAt(this.world, blockPos4)) {
                this.world.setBlockState(blockPos4, blockState2);
            }
        }
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public boolean shouldRenderAtDistance(final double distance) {
        final double double3 = 64.0 * getRenderDistanceMultiplier();
        return distance < double3 * double3;
    }
    
    @Override
    protected void initDataTracker() {
    }
    
    @Override
    protected void readCustomDataFromTag(final CompoundTag tag) {
    }
    
    @Override
    protected void writeCustomDataToTag(final CompoundTag tag) {
    }
    
    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnGlobalS2CPacket(this);
    }
}

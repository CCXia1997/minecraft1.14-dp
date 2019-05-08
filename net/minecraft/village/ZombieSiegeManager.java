package net.minecraft.village;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.SpawnHelper;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.entity.EntityData;
import net.minecraft.world.IWorld;
import net.minecraft.entity.SpawnType;
import net.minecraft.world.World;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.player.PlayerEntity;
import java.util.Iterator;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public class ZombieSiegeManager
{
    private final ServerWorld world;
    private boolean spawned;
    private State state;
    private int remaining;
    private int countdown;
    private int startX;
    private int startY;
    private int startZ;
    
    public ZombieSiegeManager(final ServerWorld serverWorld) {
        this.state = State.c;
        this.world = serverWorld;
    }
    
    public void tick() {
        if (this.world.isDaylight()) {
            this.state = State.c;
            this.spawned = false;
            return;
        }
        final float float1 = this.world.getSkyAngle(0.0f);
        if (float1 == 0.5) {
            this.state = ((this.world.random.nextInt(10) == 0) ? State.b : State.c);
        }
        if (this.state == State.c) {
            return;
        }
        if (!this.spawned) {
            if (!this.spawn()) {
                return;
            }
            this.spawned = true;
        }
        if (this.countdown > 0) {
            --this.countdown;
            return;
        }
        this.countdown = 2;
        if (this.remaining > 0) {
            this.trySpawnZombie();
            --this.remaining;
        }
        else {
            this.state = State.c;
        }
    }
    
    private boolean spawn() {
        for (final PlayerEntity playerEntity2 : this.world.getPlayers()) {
            if (!playerEntity2.isSpectator()) {
                final BlockPos blockPos3 = new BlockPos(playerEntity2);
                if (!this.world.isNearOccupiedPointOfInterest(blockPos3)) {
                    continue;
                }
                for (int integer4 = 0; integer4 < 10; ++integer4) {
                    final float float5 = this.world.random.nextFloat() * 6.2831855f;
                    this.startX = blockPos3.getX() + (int)(MathHelper.cos(float5) * 32.0f);
                    this.startY = blockPos3.getY();
                    this.startZ = blockPos3.getZ() + (int)(MathHelper.sin(float5) * 32.0f);
                }
                final Vec3d vec3d4 = this.getSpawnVector(new BlockPos(this.startX, this.startY, this.startZ));
                if (vec3d4 == null) {
                    continue;
                }
                this.countdown = 0;
                this.remaining = 20;
                return true;
            }
        }
        return false;
    }
    
    private void trySpawnZombie() {
        final Vec3d vec3d1 = this.getSpawnVector(new BlockPos(this.startX, this.startY, this.startZ));
        if (vec3d1 == null) {
            return;
        }
        ZombieEntity zombieEntity2;
        try {
            zombieEntity2 = new ZombieEntity(this.world);
            zombieEntity2.initialize(this.world, this.world.getLocalDifficulty(new BlockPos(zombieEntity2)), SpawnType.h, null, null);
        }
        catch (Exception exception3) {
            exception3.printStackTrace();
            return;
        }
        zombieEntity2.setPositionAndAngles(vec3d1.x, vec3d1.y, vec3d1.z, this.world.random.nextFloat() * 360.0f, 0.0f);
        this.world.spawnEntity(zombieEntity2);
    }
    
    @Nullable
    private Vec3d getSpawnVector(final BlockPos blockPos) {
        for (int integer2 = 0; integer2 < 10; ++integer2) {
            final BlockPos blockPos2 = blockPos.add(this.world.random.nextInt(16) - 8, this.world.random.nextInt(6) - 3, this.world.random.nextInt(16) - 8);
            if (this.world.isNearOccupiedPointOfInterest(blockPos2)) {
                if (SpawnHelper.canSpawn(SpawnRestriction.Location.a, this.world, blockPos2, null)) {
                    return new Vec3d(blockPos2.getX(), blockPos2.getY(), blockPos2.getZ());
                }
            }
        }
        return null;
    }
    
    enum State
    {
        a, 
        b, 
        c;
    }
}

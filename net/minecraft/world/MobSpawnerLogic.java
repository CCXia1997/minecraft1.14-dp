package net.minecraft.world;

import java.util.AbstractList;
import org.apache.logging.log4j.LogManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.util.function.Function;
import net.minecraft.nbt.Tag;
import net.minecraft.util.WeightedPicker;
import java.util.Iterator;
import net.minecraft.nbt.ListTag;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.registry.Registry;
import net.minecraft.entity.EntityType;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.Identifier;
import com.google.common.collect.Lists;
import net.minecraft.entity.Entity;
import java.util.List;
import org.apache.logging.log4j.Logger;

public abstract class MobSpawnerLogic
{
    private static final Logger LOGGER;
    private int spawnDelay;
    private final List<MobSpawnerEntry> spawnPotentials;
    private MobSpawnerEntry spawnEntry;
    private double e;
    private double f;
    private int minSpawnDelay;
    private int maxSpawnDelay;
    private int spawnCount;
    private Entity renderedEntity;
    private int maxNearbyEntities;
    private int requiredPlayerRange;
    private int spawnRange;
    
    public MobSpawnerLogic() {
        this.spawnDelay = 20;
        this.spawnPotentials = Lists.newArrayList();
        this.spawnEntry = new MobSpawnerEntry();
        this.minSpawnDelay = 200;
        this.maxSpawnDelay = 800;
        this.spawnCount = 4;
        this.maxNearbyEntities = 6;
        this.requiredPlayerRange = 16;
        this.spawnRange = 4;
    }
    
    @Nullable
    private Identifier getEntityId() {
        final String string1 = this.spawnEntry.getEntityTag().getString("id");
        try {
            return ChatUtil.isEmpty(string1) ? null : new Identifier(string1);
        }
        catch (InvalidIdentifierException invalidIdentifierException2) {
            final BlockPos blockPos3 = this.getPos();
            MobSpawnerLogic.LOGGER.warn("Invalid entity id '{}' at spawner {}:[{},{},{}]", string1, this.getWorld().dimension.getType(), blockPos3.getX(), blockPos3.getY(), blockPos3.getZ());
            return null;
        }
    }
    
    public void setEntityId(final EntityType<?> entityType) {
        this.spawnEntry.getEntityTag().putString("id", Registry.ENTITY_TYPE.getId(entityType).toString());
    }
    
    private boolean isPlayerInRange() {
        final BlockPos blockPos1 = this.getPos();
        return this.getWorld().isPlayerInRange(blockPos1.getX() + 0.5, blockPos1.getY() + 0.5, blockPos1.getZ() + 0.5, this.requiredPlayerRange);
    }
    
    public void update() {
        if (!this.isPlayerInRange()) {
            this.f = this.e;
            return;
        }
        final World world1 = this.getWorld();
        final BlockPos blockPos2 = this.getPos();
        if (world1.isClient) {
            final double double3 = blockPos2.getX() + world1.random.nextFloat();
            final double double4 = blockPos2.getY() + world1.random.nextFloat();
            final double double5 = blockPos2.getZ() + world1.random.nextFloat();
            world1.addParticle(ParticleTypes.Q, double3, double4, double5, 0.0, 0.0, 0.0);
            world1.addParticle(ParticleTypes.A, double3, double4, double5, 0.0, 0.0, 0.0);
            if (this.spawnDelay > 0) {
                --this.spawnDelay;
            }
            this.f = this.e;
            this.e = (this.e + 1000.0f / (this.spawnDelay + 200.0f)) % 360.0;
        }
        else {
            if (this.spawnDelay == -1) {
                this.updateSpawns();
            }
            if (this.spawnDelay > 0) {
                --this.spawnDelay;
                return;
            }
            boolean boolean3 = false;
            for (int integer4 = 0; integer4 < this.spawnCount; ++integer4) {
                final CompoundTag compoundTag5 = this.spawnEntry.getEntityTag();
                final Optional<EntityType<?>> optional6 = EntityType.fromTag(compoundTag5);
                if (!optional6.isPresent()) {
                    this.updateSpawns();
                    return;
                }
                final ListTag listTag7 = compoundTag5.getList("Pos", 6);
                final int integer5 = listTag7.size();
                final double double6 = (integer5 >= 1) ? listTag7.getDouble(0) : (blockPos2.getX() + (world1.random.nextDouble() - world1.random.nextDouble()) * this.spawnRange + 0.5);
                final double double7 = (integer5 >= 2) ? listTag7.getDouble(1) : (blockPos2.getY() + world1.random.nextInt(3) - 1);
                final double double8 = (integer5 >= 3) ? listTag7.getDouble(2) : (blockPos2.getZ() + (world1.random.nextDouble() - world1.random.nextDouble()) * this.spawnRange + 0.5);
                if (world1.doesNotCollide(optional6.get().createSimpleBoundingBox(double6, double7, double8))) {
                    final Entity entity8 = EntityType.loadEntityWithPassengers(compoundTag5, world1, entity7 -> {
                        entity7.setPositionAndAngles(double6, double7, double8, entity7.yaw, entity7.pitch);
                        return entity7;
                    });
                    if (entity8 == null) {
                        this.updateSpawns();
                        return;
                    }
                    final int integer6 = world1.<Entity>getEntities(entity8.getClass(), new BoundingBox(blockPos2.getX(), blockPos2.getY(), blockPos2.getZ(), blockPos2.getX() + 1, blockPos2.getY() + 1, blockPos2.getZ() + 1).expand(this.spawnRange)).size();
                    if (integer6 >= this.maxNearbyEntities) {
                        this.updateSpawns();
                        return;
                    }
                    entity8.setPositionAndAngles(entity8.x, entity8.y, entity8.z, world1.random.nextFloat() * 360.0f, 0.0f);
                    if (entity8 instanceof MobEntity) {
                        final MobEntity mobEntity17 = (MobEntity)entity8;
                        if (!mobEntity17.canSpawn(world1, SpawnType.c)) {
                            continue;
                        }
                        if (!mobEntity17.canSpawn(world1)) {
                            continue;
                        }
                        if (this.spawnEntry.getEntityTag().getSize() == 1 && this.spawnEntry.getEntityTag().containsKey("id", 8)) {
                            ((MobEntity)entity8).initialize(world1, world1.getLocalDifficulty(new BlockPos(entity8)), SpawnType.c, null, null);
                        }
                    }
                    this.spawnEntity(entity8);
                    world1.playLevelEvent(2004, blockPos2, 0);
                    if (entity8 instanceof MobEntity) {
                        ((MobEntity)entity8).playSpawnEffects();
                    }
                    boolean3 = true;
                }
            }
            if (boolean3) {
                this.updateSpawns();
            }
        }
    }
    
    private void spawnEntity(final Entity entity) {
        if (!this.getWorld().spawnEntity(entity)) {
            return;
        }
        for (final Entity entity2 : entity.getPassengerList()) {
            this.spawnEntity(entity2);
        }
    }
    
    private void updateSpawns() {
        if (this.maxSpawnDelay <= this.minSpawnDelay) {
            this.spawnDelay = this.minSpawnDelay;
        }
        else {
            this.spawnDelay = this.minSpawnDelay + this.getWorld().random.nextInt(this.maxSpawnDelay - this.minSpawnDelay);
        }
        if (!this.spawnPotentials.isEmpty()) {
            this.setSpawnEntry(WeightedPicker.<MobSpawnerEntry>getRandom(this.getWorld().random, this.spawnPotentials));
        }
        this.a(1);
    }
    
    public void deserialize(final CompoundTag compoundTag) {
        this.spawnDelay = compoundTag.getShort("Delay");
        this.spawnPotentials.clear();
        if (compoundTag.containsKey("SpawnPotentials", 9)) {
            final ListTag listTag2 = compoundTag.getList("SpawnPotentials", 10);
            for (int integer3 = 0; integer3 < listTag2.size(); ++integer3) {
                this.spawnPotentials.add(new MobSpawnerEntry(listTag2.getCompoundTag(integer3)));
            }
        }
        if (compoundTag.containsKey("SpawnData", 10)) {
            this.setSpawnEntry(new MobSpawnerEntry(1, compoundTag.getCompound("SpawnData")));
        }
        else if (!this.spawnPotentials.isEmpty()) {
            this.setSpawnEntry(WeightedPicker.<MobSpawnerEntry>getRandom(this.getWorld().random, this.spawnPotentials));
        }
        if (compoundTag.containsKey("MinSpawnDelay", 99)) {
            this.minSpawnDelay = compoundTag.getShort("MinSpawnDelay");
            this.maxSpawnDelay = compoundTag.getShort("MaxSpawnDelay");
            this.spawnCount = compoundTag.getShort("SpawnCount");
        }
        if (compoundTag.containsKey("MaxNearbyEntities", 99)) {
            this.maxNearbyEntities = compoundTag.getShort("MaxNearbyEntities");
            this.requiredPlayerRange = compoundTag.getShort("RequiredPlayerRange");
        }
        if (compoundTag.containsKey("SpawnRange", 99)) {
            this.spawnRange = compoundTag.getShort("SpawnRange");
        }
        if (this.getWorld() != null) {
            this.renderedEntity = null;
        }
    }
    
    public CompoundTag serialize(final CompoundTag compoundTag) {
        final Identifier identifier2 = this.getEntityId();
        if (identifier2 == null) {
            return compoundTag;
        }
        compoundTag.putShort("Delay", (short)this.spawnDelay);
        compoundTag.putShort("MinSpawnDelay", (short)this.minSpawnDelay);
        compoundTag.putShort("MaxSpawnDelay", (short)this.maxSpawnDelay);
        compoundTag.putShort("SpawnCount", (short)this.spawnCount);
        compoundTag.putShort("MaxNearbyEntities", (short)this.maxNearbyEntities);
        compoundTag.putShort("RequiredPlayerRange", (short)this.requiredPlayerRange);
        compoundTag.putShort("SpawnRange", (short)this.spawnRange);
        compoundTag.put("SpawnData", this.spawnEntry.getEntityTag().copy());
        final ListTag listTag3 = new ListTag();
        if (this.spawnPotentials.isEmpty()) {
            ((AbstractList<CompoundTag>)listTag3).add(this.spawnEntry.serialize());
        }
        else {
            for (final MobSpawnerEntry mobSpawnerEntry5 : this.spawnPotentials) {
                ((AbstractList<CompoundTag>)listTag3).add(mobSpawnerEntry5.serialize());
            }
        }
        compoundTag.put("SpawnPotentials", listTag3);
        return compoundTag;
    }
    
    @Environment(EnvType.CLIENT)
    public Entity getRenderedEntity() {
        if (this.renderedEntity == null) {
            this.renderedEntity = EntityType.loadEntityWithPassengers(this.spawnEntry.getEntityTag(), this.getWorld(), Function.<Entity>identity());
            if (this.spawnEntry.getEntityTag().getSize() == 1 && this.spawnEntry.getEntityTag().containsKey("id", 8) && this.renderedEntity instanceof MobEntity) {
                ((MobEntity)this.renderedEntity).initialize(this.getWorld(), this.getWorld().getLocalDifficulty(new BlockPos(this.renderedEntity)), SpawnType.c, null, null);
            }
        }
        return this.renderedEntity;
    }
    
    public boolean b(final int integer) {
        if (integer == 1 && this.getWorld().isClient) {
            this.spawnDelay = this.minSpawnDelay;
            return true;
        }
        return false;
    }
    
    public void setSpawnEntry(final MobSpawnerEntry mobSpawnerEntry) {
        this.spawnEntry = mobSpawnerEntry;
    }
    
    public abstract void a(final int arg1);
    
    public abstract World getWorld();
    
    public abstract BlockPos getPos();
    
    @Environment(EnvType.CLIENT)
    public double e() {
        return this.e;
    }
    
    @Environment(EnvType.CLIENT)
    public double f() {
        return this.f;
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}

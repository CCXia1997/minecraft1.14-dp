package net.minecraft.entity.raid;

import java.util.AbstractList;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.CompoundTag;
import javax.annotation.Nullable;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.stat.Stats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.Packet;
import net.minecraft.client.network.packet.EntityStatusS2CPacket;
import net.minecraft.entity.effect.StatusEffects;
import java.util.Optional;
import net.minecraft.village.PointOfInterestStorage;
import java.util.Objects;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.entity.Entity;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.LivingEntity;
import java.util.Iterator;
import com.google.common.collect.Maps;
import net.minecraft.server.world.ServerWorld;
import java.util.Map;
import net.minecraft.world.PersistentState;

public class RaidManager extends PersistentState
{
    private final Map<Integer, Raid> raids;
    private final ServerWorld world;
    private int nextAvailableId;
    private int currentTime;
    
    public RaidManager(final ServerWorld serverWorld) {
        super(nameFor(serverWorld.dimension));
        this.raids = Maps.newHashMap();
        this.world = serverWorld;
        this.nextAvailableId = 1;
        this.markDirty();
    }
    
    public Raid getRaid(final int id) {
        return this.raids.get(id);
    }
    
    public void tick() {
        ++this.currentTime;
        final Iterator<Raid> iterator1 = this.raids.values().iterator();
        while (iterator1.hasNext()) {
            final Raid raid2 = iterator1.next();
            if (raid2.hasStopped()) {
                iterator1.remove();
                this.markDirty();
            }
            else {
                raid2.tick();
            }
        }
        if (this.currentTime % 200 == 0) {
            this.markDirty();
        }
    }
    
    public static boolean isValidRaiderFor(final RaiderEntity raiderEntity, final Raid raid) {
        return raiderEntity != null && raid != null && raid.getWorld() != null && raiderEntity.isAlive() && raiderEntity.canJoinRaid() && raiderEntity.getDespawnCounter() <= 2400 && raiderEntity.world.getDimension().getType() == raid.getWorld().getDimension().getType();
    }
    
    public static boolean isLivingAroundVillage(final LivingEntity livingEntity, final BlockPos blockPos, final int integer) {
        return blockPos.getSquaredDistance(new BlockPos(livingEntity.x, livingEntity.y, livingEntity.z)) < integer * integer + 24;
    }
    
    @Nullable
    public Raid startRaid(final ServerPlayerEntity serverPlayerEntity) {
        if (serverPlayerEntity.isSpectator()) {
            return null;
        }
        final DimensionType dimensionType2 = serverPlayerEntity.world.getDimension().getType();
        if (dimensionType2 == DimensionType.b) {
            return null;
        }
        final BlockPos blockPos3 = new BlockPos(serverPlayerEntity);
        Optional<BlockPos> optional4 = this.world.getPointOfInterestStorage().getNearestPosition(pointOfInterestType -> pointOfInterestType == PointOfInterestType.r, Objects::nonNull, blockPos3, 15, PointOfInterestStorage.OccupationStatus.ANY);
        if (!optional4.isPresent()) {
            optional4 = Optional.<BlockPos>of(blockPos3);
        }
        final Raid raid5 = this.getOrCreateRaid(serverPlayerEntity.getServerWorld(), optional4.get());
        boolean boolean6 = false;
        if (!raid5.hasStarted()) {
            if (!this.raids.containsKey(raid5.getRaidId())) {
                this.raids.put(raid5.getRaidId(), raid5);
            }
            boolean6 = true;
        }
        else if (raid5.getBadOmenLevel() < raid5.getMaxAcceptableBadOmenLevel()) {
            boolean6 = true;
        }
        else {
            serverPlayerEntity.removeStatusEffect(StatusEffects.E);
            serverPlayerEntity.networkHandler.sendPacket(new EntityStatusS2CPacket(serverPlayerEntity, (byte)43));
        }
        if (boolean6) {
            raid5.start(serverPlayerEntity);
            serverPlayerEntity.networkHandler.sendPacket(new EntityStatusS2CPacket(serverPlayerEntity, (byte)43));
            if (!raid5.hasSpawned()) {
                serverPlayerEntity.incrementStat(Stats.ay);
                Criterions.VOLUNTARY_EXILE.handle(serverPlayerEntity);
            }
        }
        this.markDirty();
        return raid5;
    }
    
    private Raid getOrCreateRaid(final ServerWorld serverWorld, final BlockPos blockPos) {
        final Raid raid3 = serverWorld.getRaidAt(blockPos);
        return (raid3 != null) ? raid3 : new Raid(this.nextId(), serverWorld, blockPos);
    }
    
    @Override
    public void fromTag(final CompoundTag compoundTag) {
        this.nextAvailableId = compoundTag.getInt("NextAvailableID");
        this.currentTime = compoundTag.getInt("Tick");
        final ListTag listTag2 = compoundTag.getList("Raids", 10);
        for (int integer3 = 0; integer3 < listTag2.size(); ++integer3) {
            final CompoundTag compoundTag2 = listTag2.getCompoundTag(integer3);
            final Raid raid5 = new Raid(this.world, compoundTag2);
            this.raids.put(raid5.getRaidId(), raid5);
        }
    }
    
    @Override
    public CompoundTag toTag(final CompoundTag compoundTag) {
        compoundTag.putInt("NextAvailableID", this.nextAvailableId);
        compoundTag.putInt("Tick", this.currentTime);
        final ListTag listTag2 = new ListTag();
        for (final Raid raid4 : this.raids.values()) {
            final CompoundTag compoundTag2 = new CompoundTag();
            raid4.toTag(compoundTag2);
            ((AbstractList<CompoundTag>)listTag2).add(compoundTag2);
        }
        compoundTag.put("Raids", listTag2);
        return compoundTag;
    }
    
    public static String nameFor(final Dimension dimension) {
        return "raids" + dimension.getType().getSuffix();
    }
    
    private int nextId() {
        return ++this.nextAvailableId;
    }
    
    @Nullable
    public Raid getRaidAt(final BlockPos blockPos) {
        Raid raid2 = null;
        double double3 = 2.147483647E9;
        for (final Raid raid3 : this.raids.values()) {
            final double double4 = raid3.getCenter().getSquaredDistance(blockPos);
            if (!raid3.isActive()) {
                continue;
            }
            if (double4 >= double3) {
                continue;
            }
            raid2 = raid3;
            double3 = double4;
        }
        return raid2;
    }
}

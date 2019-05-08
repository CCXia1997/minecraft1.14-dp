package net.minecraft.entity.ai.brain.task;

import java.util.AbstractList;
import net.minecraft.entity.LivingEntity;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ListTag;
import net.minecraft.item.FireworkItem;
import com.google.common.collect.Lists;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import java.util.Random;
import net.minecraft.entity.FireworkEntity;
import net.minecraft.util.DyeColor;
import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import com.mojang.datafixers.util.Pair;
import java.util.Set;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.server.world.ServerWorld;
import javax.annotation.Nullable;
import net.minecraft.entity.raid.Raid;
import net.minecraft.entity.passive.VillagerEntity;

public class CelebrateRaidWinTask extends Task<VillagerEntity>
{
    @Nullable
    private Raid raid;
    
    public CelebrateRaidWinTask(final int minRunTime, final int maxRunTime) {
        super(minRunTime, maxRunTime);
    }
    
    @Override
    protected boolean shouldRun(final ServerWorld world, final VillagerEntity entity) {
        this.raid = world.getRaidAt(new BlockPos(entity));
        return this.raid != null && this.raid.hasWon() && world.isSkyVisible(new BlockPos(entity));
    }
    
    @Override
    protected boolean shouldKeepRunning(final ServerWorld world, final VillagerEntity entity, final long time) {
        return this.raid != null && !this.raid.hasStopped();
    }
    
    @Override
    protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
        return ImmutableSet.of();
    }
    
    @Override
    protected void finishRunning(final ServerWorld serverWorld, final VillagerEntity villagerEntity, final long time) {
        this.raid = null;
        villagerEntity.getBrain().refreshActivities(serverWorld.getTimeOfDay(), serverWorld.getTime());
    }
    
    @Override
    protected void keepRunning(final ServerWorld world, final VillagerEntity entity, final long time) {
        final Random random5 = entity.getRand();
        if (random5.nextInt(100) == 0) {
            entity.playCelebrateSound();
        }
        if (random5.nextInt(200) == 0 && world.isSkyVisible(new BlockPos(entity))) {
            final DyeColor dyeColor6 = DyeColor.values()[random5.nextInt(DyeColor.values().length)];
            final int integer7 = random5.nextInt(3);
            final ItemStack itemStack8 = this.createFirework(dyeColor6, integer7);
            final FireworkEntity fireworkEntity9 = new FireworkEntity(entity.world, entity.x, entity.y + entity.getStandingEyeHeight(), entity.z, itemStack8);
            entity.world.spawnEntity(fireworkEntity9);
        }
    }
    
    private ItemStack createFirework(final DyeColor color, final int flight) {
        final ItemStack itemStack3 = new ItemStack(Items.nX, 1);
        final ItemStack itemStack4 = new ItemStack(Items.nY);
        final CompoundTag compoundTag5 = itemStack4.getOrCreateSubCompoundTag("Explosion");
        final List<Integer> list6 = Lists.newArrayList();
        list6.add(color.getFireworkColor());
        compoundTag5.putIntArray("Colors", list6);
        compoundTag5.putByte("Type", (byte)FireworkItem.Type.e.getId());
        final CompoundTag compoundTag6 = itemStack3.getOrCreateSubCompoundTag("Fireworks");
        final ListTag listTag8 = new ListTag();
        final CompoundTag compoundTag7 = itemStack4.getSubCompoundTag("Explosion");
        if (compoundTag7 != null) {
            ((AbstractList<CompoundTag>)listTag8).add(compoundTag7);
        }
        compoundTag6.putByte("Flight", (byte)flight);
        if (!listTag8.isEmpty()) {
            compoundTag6.put("Explosions", listTag8);
        }
        return itemStack3;
    }
}

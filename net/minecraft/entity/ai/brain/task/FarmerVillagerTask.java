package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.entity.ai.brain.LookTarget;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.BlockPosLookTarget;
import net.minecraft.block.Block;
import net.minecraft.block.CropBlock;
import net.minecraft.block.BlockState;
import java.util.function.Predicate;
import com.google.common.collect.ImmutableList;
import net.minecraft.entity.Entity;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Function;
import net.minecraft.util.GlobalPos;
import java.util.List;
import net.minecraft.village.VillagerProfession;
import net.minecraft.server.world.ServerWorld;
import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import com.mojang.datafixers.util.Pair;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.passive.VillagerEntity;

public class FarmerVillagerTask extends Task<VillagerEntity>
{
    @Nullable
    private BlockPos a;
    private boolean b;
    private boolean c;
    private long d;
    private int e;
    
    @Override
    protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
        return ImmutableSet.of(Pair.of(MemoryModuleType.l, MemoryModuleState.b), Pair.of(MemoryModuleType.k, MemoryModuleState.b), Pair.of(MemoryModuleType.e, MemoryModuleState.a));
    }
    
    @Override
    protected boolean shouldRun(final ServerWorld world, final VillagerEntity entity) {
        if (!world.getGameRules().getBoolean("mobGriefing")) {
            return false;
        }
        if (entity.getVillagerData().getProfession() != VillagerProfession.f) {
            return false;
        }
        final Set<BlockPos> set3 = entity.getBrain().<List<GlobalPos>>getOptionalMemory(MemoryModuleType.e).get().stream().map(GlobalPos::getPos).collect(Collectors.toSet());
        final BlockPos blockPos2 = new BlockPos(entity);
        final List<BlockPos> list5 = ImmutableList.<BlockPos>of(blockPos2.down(), blockPos2.south(), blockPos2.north(), blockPos2.east(), blockPos2.west()).stream().filter(set3::contains).collect(Collectors.toList());
        this.b = entity.hasSeedToPlant();
        this.c = entity.canBreed();
        final List<BlockPos> list6 = list5.stream().map(BlockPos::up).filter(blockPos -> this.a(world.getBlockState(blockPos))).collect(Collectors.toList());
        if (!list6.isEmpty()) {
            this.a = list6.get(world.getRandom().nextInt(list6.size()));
            return true;
        }
        return false;
    }
    
    private boolean a(final BlockState blockState) {
        final Block block2 = blockState.getBlock();
        return (block2 instanceof CropBlock && ((CropBlock)block2).isValidState(blockState) && this.c) || (blockState.isAir() && this.b);
    }
    
    @Override
    protected void run(final ServerWorld world, final VillagerEntity entity, final long time) {
        if (time > this.d && this.a != null) {
            entity.getBrain().<BlockPosLookTarget>putMemory((MemoryModuleType<BlockPosLookTarget>)MemoryModuleType.l, new BlockPosLookTarget(this.a));
            entity.getBrain().<WalkTarget>putMemory(MemoryModuleType.k, new WalkTarget(new BlockPosLookTarget(this.a), 0.5f, 1));
        }
    }
    
    @Override
    protected void finishRunning(final ServerWorld serverWorld, final VillagerEntity villagerEntity, final long time) {
        villagerEntity.getBrain().forget(MemoryModuleType.l);
        villagerEntity.getBrain().forget(MemoryModuleType.k);
        this.e = 0;
        this.d = time + 40L;
    }
    
    @Override
    protected void keepRunning(final ServerWorld world, final VillagerEntity entity, final long time) {
        if (this.e > 15 && this.a != null && time > this.d) {
            final BlockState blockState5 = world.getBlockState(this.a);
            final Block block6 = blockState5.getBlock();
            if (block6 instanceof CropBlock && ((CropBlock)block6).isValidState(blockState5) && this.c) {
                world.breakBlock(this.a, true);
            }
            else if (blockState5.isAir() && this.b) {
                final BasicInventory basicInventory7 = entity.getInventory();
                int integer8 = 0;
                while (integer8 < basicInventory7.getInvSize()) {
                    final ItemStack itemStack9 = basicInventory7.getInvStack(integer8);
                    boolean boolean10 = false;
                    if (!itemStack9.isEmpty()) {
                        if (itemStack9.getItem() == Items.jO) {
                            world.setBlockState(this.a, Blocks.bU.getDefaultState(), 3);
                            boolean10 = true;
                        }
                        else if (itemStack9.getItem() == Items.nJ) {
                            world.setBlockState(this.a, Blocks.eN.getDefaultState(), 3);
                            boolean10 = true;
                        }
                        else if (itemStack9.getItem() == Items.nI) {
                            world.setBlockState(this.a, Blocks.eM.getDefaultState(), 3);
                            boolean10 = true;
                        }
                        else if (itemStack9.getItem() == Items.oP) {
                            world.setBlockState(this.a, Blocks.iv.getDefaultState(), 3);
                            boolean10 = true;
                        }
                    }
                    if (boolean10) {
                        itemStack9.subtractAmount(1);
                        if (itemStack9.isEmpty()) {
                            basicInventory7.setInvStack(integer8, ItemStack.EMPTY);
                            break;
                        }
                        break;
                    }
                    else {
                        ++integer8;
                    }
                }
            }
        }
        ++this.e;
    }
    
    @Override
    protected boolean shouldKeepRunning(final ServerWorld world, final VillagerEntity entity, final long time) {
        return this.e < 30;
    }
}

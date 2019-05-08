package net.minecraft.item;

import com.google.common.collect.Maps;
import com.google.common.collect.Iterables;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.block.FluidBlock;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.RayTraceContext;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.MobSpawnerLogic;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.util.math.Direction;
import java.util.Objects;
import net.minecraft.entity.SpawnType;
import net.minecraft.world.BlockView;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.block.Blocks;
import net.minecraft.util.ActionResult;
import net.minecraft.entity.EntityType;
import java.util.Map;

public class SpawnEggItem extends Item
{
    private static final Map<EntityType<?>, SpawnEggItem> SPAWN_EGGS;
    private final int primaryColor;
    private final int secondaryColor;
    private final EntityType<?> type;
    
    public SpawnEggItem(final EntityType<?> type, final int primaryColor, final int secondaryColor, final Settings settings) {
        super(settings);
        this.type = type;
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
        SpawnEggItem.SPAWN_EGGS.put(type, this);
    }
    
    @Override
    public ActionResult useOnBlock(final ItemUsageContext usageContext) {
        final World world2 = usageContext.getWorld();
        if (world2.isClient) {
            return ActionResult.a;
        }
        final ItemStack itemStack3 = usageContext.getItemStack();
        final BlockPos blockPos4 = usageContext.getBlockPos();
        final Direction direction5 = usageContext.getFacing();
        final BlockState blockState6 = world2.getBlockState(blockPos4);
        final Block block7 = blockState6.getBlock();
        if (block7 == Blocks.bN) {
            final BlockEntity blockEntity8 = world2.getBlockEntity(blockPos4);
            if (blockEntity8 instanceof MobSpawnerBlockEntity) {
                final MobSpawnerLogic mobSpawnerLogic9 = ((MobSpawnerBlockEntity)blockEntity8).getLogic();
                final EntityType<?> entityType10 = this.entityTypeFromTag(itemStack3.getTag());
                mobSpawnerLogic9.setEntityId(entityType10);
                blockEntity8.markDirty();
                world2.updateListeners(blockPos4, blockState6, blockState6, 3);
                itemStack3.subtractAmount(1);
                return ActionResult.a;
            }
        }
        BlockPos blockPos5;
        if (blockState6.getCollisionShape(world2, blockPos4).isEmpty()) {
            blockPos5 = blockPos4;
        }
        else {
            blockPos5 = blockPos4.offset(direction5);
        }
        final EntityType<?> entityType11 = this.entityTypeFromTag(itemStack3.getTag());
        if (entityType11.spawnFromItemStack(world2, itemStack3, usageContext.getPlayer(), blockPos5, SpawnType.m, true, !Objects.equals(blockPos4, blockPos5) && direction5 == Direction.UP) != null) {
            itemStack3.subtractAmount(1);
        }
        return ActionResult.a;
    }
    
    @Override
    public TypedActionResult<ItemStack> use(final World world, final PlayerEntity player, final Hand hand) {
        final ItemStack itemStack4 = player.getStackInHand(hand);
        if (world.isClient) {
            return new TypedActionResult<ItemStack>(ActionResult.PASS, itemStack4);
        }
        final HitResult hitResult5 = Item.getHitResult(world, player, RayTraceContext.FluidHandling.b);
        if (hitResult5.getType() != HitResult.Type.BLOCK) {
            return new TypedActionResult<ItemStack>(ActionResult.PASS, itemStack4);
        }
        final BlockHitResult blockHitResult6 = (BlockHitResult)hitResult5;
        final BlockPos blockPos7 = blockHitResult6.getBlockPos();
        if (!(world.getBlockState(blockPos7).getBlock() instanceof FluidBlock)) {
            return new TypedActionResult<ItemStack>(ActionResult.PASS, itemStack4);
        }
        if (!world.canPlayerModifyAt(player, blockPos7) || !player.canPlaceOn(blockPos7, blockHitResult6.getSide(), itemStack4)) {
            return new TypedActionResult<ItemStack>(ActionResult.c, itemStack4);
        }
        final EntityType<?> entityType8 = this.entityTypeFromTag(itemStack4.getTag());
        if (entityType8.spawnFromItemStack(world, itemStack4, player, blockPos7, SpawnType.m, false, false) == null) {
            return new TypedActionResult<ItemStack>(ActionResult.PASS, itemStack4);
        }
        if (!player.abilities.creativeMode) {
            itemStack4.subtractAmount(1);
        }
        player.incrementStat(Stats.c.getOrCreateStat(this));
        return new TypedActionResult<ItemStack>(ActionResult.a, itemStack4);
    }
    
    public boolean isOfSameEntityType(@Nullable final CompoundTag tag, final EntityType<?> type) {
        return Objects.equals(this.entityTypeFromTag(tag), type);
    }
    
    @Environment(EnvType.CLIENT)
    public int getColor(final int num) {
        return (num == 0) ? this.primaryColor : this.secondaryColor;
    }
    
    @Environment(EnvType.CLIENT)
    public static SpawnEggItem forEntity(@Nullable final EntityType<?> type) {
        return SpawnEggItem.SPAWN_EGGS.get(type);
    }
    
    public static Iterable<SpawnEggItem> iterator() {
        return Iterables.<SpawnEggItem>unmodifiableIterable(SpawnEggItem.SPAWN_EGGS.values());
    }
    
    public EntityType<?> entityTypeFromTag(@Nullable final CompoundTag tag) {
        if (tag != null && tag.containsKey("EntityTag", 10)) {
            final CompoundTag compoundTag2 = tag.getCompound("EntityTag");
            if (compoundTag2.containsKey("id", 8)) {
                return EntityType.get(compoundTag2.getString("id")).orElse(this.type);
            }
        }
        return this.type;
    }
    
    static {
        SPAWN_EGGS = Maps.newIdentityHashMap();
    }
}

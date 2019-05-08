package net.minecraft.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.World;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.EntityType;

public class DecorationItem extends Item
{
    private final EntityType<? extends AbstractDecorationEntity> entityType;
    
    public DecorationItem(final EntityType<? extends AbstractDecorationEntity> entityType, final Settings settings) {
        super(settings);
        this.entityType = entityType;
    }
    
    @Override
    public ActionResult useOnBlock(final ItemUsageContext usageContext) {
        final BlockPos blockPos2 = usageContext.getBlockPos();
        final Direction direction3 = usageContext.getFacing();
        final BlockPos blockPos3 = blockPos2.offset(direction3);
        final PlayerEntity playerEntity5 = usageContext.getPlayer();
        final ItemStack itemStack6 = usageContext.getItemStack();
        if (playerEntity5 != null && !this.canPlaceOn(playerEntity5, direction3, itemStack6, blockPos3)) {
            return ActionResult.c;
        }
        final World world7 = usageContext.getWorld();
        AbstractDecorationEntity abstractDecorationEntity8;
        if (this.entityType == EntityType.PAINTING) {
            abstractDecorationEntity8 = new PaintingEntity(world7, blockPos3, direction3);
        }
        else {
            if (this.entityType != EntityType.ITEM_FRAME) {
                return ActionResult.a;
            }
            abstractDecorationEntity8 = new ItemFrameEntity(world7, blockPos3, direction3);
        }
        final CompoundTag compoundTag9 = itemStack6.getTag();
        if (compoundTag9 != null) {
            EntityType.loadFromEntityTag(world7, playerEntity5, abstractDecorationEntity8, compoundTag9);
        }
        if (abstractDecorationEntity8.i()) {
            if (!world7.isClient) {
                abstractDecorationEntity8.onPlace();
                world7.spawnEntity(abstractDecorationEntity8);
            }
            itemStack6.subtractAmount(1);
        }
        return ActionResult.a;
    }
    
    protected boolean canPlaceOn(final PlayerEntity player, final Direction facing, final ItemStack stack, final BlockPos pos) {
        return !facing.getAxis().isVertical() && player.canPlaceOn(pos, facing, stack);
    }
}

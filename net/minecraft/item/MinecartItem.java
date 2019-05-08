package net.minecraft.item;

import net.minecraft.state.property.Property;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.entity.Entity;
import net.minecraft.block.enums.RailShape;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.block.dispenser.DispenserBehavior;

public class MinecartItem extends Item
{
    private static final DispenserBehavior DISPENSER_BEHAVIOR;
    private final AbstractMinecartEntity.Type type;
    
    public MinecartItem(final AbstractMinecartEntity.Type type, final Settings settings) {
        super(settings);
        this.type = type;
        DispenserBlock.registerBehavior(this, MinecartItem.DISPENSER_BEHAVIOR);
    }
    
    @Override
    public ActionResult useOnBlock(final ItemUsageContext usageContext) {
        final World world2 = usageContext.getWorld();
        final BlockPos blockPos3 = usageContext.getBlockPos();
        final BlockState blockState4 = world2.getBlockState(blockPos3);
        if (!blockState4.matches(BlockTags.B)) {
            return ActionResult.c;
        }
        final ItemStack itemStack5 = usageContext.getItemStack();
        if (!world2.isClient) {
            final RailShape railShape6 = (blockState4.getBlock() instanceof AbstractRailBlock) ? blockState4.<RailShape>get(((AbstractRailBlock)blockState4.getBlock()).getShapeProperty()) : RailShape.a;
            double double7 = 0.0;
            if (railShape6.isAscending()) {
                double7 = 0.5;
            }
            final AbstractMinecartEntity abstractMinecartEntity9 = AbstractMinecartEntity.create(world2, blockPos3.getX() + 0.5, blockPos3.getY() + 0.0625 + double7, blockPos3.getZ() + 0.5, this.type);
            if (itemStack5.hasDisplayName()) {
                abstractMinecartEntity9.setCustomName(itemStack5.getDisplayName());
            }
            world2.spawnEntity(abstractMinecartEntity9);
        }
        itemStack5.subtractAmount(1);
        return ActionResult.a;
    }
    
    static {
        DISPENSER_BEHAVIOR = new ItemDispenserBehavior() {
            private final ItemDispenserBehavior b = new ItemDispenserBehavior();
            
            public ItemStack dispenseStack(final BlockPointer pointer, final ItemStack stack) {
                final Direction direction3 = pointer.getBlockState().<Direction>get((Property<Direction>)DispenserBlock.FACING);
                final World world4 = pointer.getWorld();
                final double double5 = pointer.getX() + direction3.getOffsetX() * 1.125;
                final double double6 = Math.floor(pointer.getY()) + direction3.getOffsetY();
                final double double7 = pointer.getZ() + direction3.getOffsetZ() * 1.125;
                final BlockPos blockPos11 = pointer.getBlockPos().offset(direction3);
                final BlockState blockState12 = world4.getBlockState(blockPos11);
                final RailShape railShape13 = (blockState12.getBlock() instanceof AbstractRailBlock) ? blockState12.<RailShape>get(((AbstractRailBlock)blockState12.getBlock()).getShapeProperty()) : RailShape.a;
                double double8;
                if (blockState12.matches(BlockTags.B)) {
                    if (railShape13.isAscending()) {
                        double8 = 0.6;
                    }
                    else {
                        double8 = 0.1;
                    }
                }
                else {
                    if (!blockState12.isAir() || !world4.getBlockState(blockPos11.down()).matches(BlockTags.B)) {
                        return this.b.dispense(pointer, stack);
                    }
                    final BlockState blockState13 = world4.getBlockState(blockPos11.down());
                    final RailShape railShape14 = (blockState13.getBlock() instanceof AbstractRailBlock) ? blockState13.<RailShape>get(((AbstractRailBlock)blockState13.getBlock()).getShapeProperty()) : RailShape.a;
                    if (direction3 == Direction.DOWN || !railShape14.isAscending()) {
                        double8 = -0.9;
                    }
                    else {
                        double8 = -0.4;
                    }
                }
                final AbstractMinecartEntity abstractMinecartEntity16 = AbstractMinecartEntity.create(world4, double5, double6 + double8, double7, ((MinecartItem)stack.getItem()).type);
                if (stack.hasDisplayName()) {
                    abstractMinecartEntity16.setCustomName(stack.getDisplayName());
                }
                world4.spawnEntity(abstractMinecartEntity16);
                stack.subtractAmount(1);
                return stack;
            }
            
            @Override
            protected void playSound(final BlockPointer blockPointer) {
                blockPointer.getWorld().playLevelEvent(1000, blockPointer.getBlockPos(), 0);
            }
        };
    }
}

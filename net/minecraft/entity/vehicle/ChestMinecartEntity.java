package net.minecraft.entity.vehicle;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.inventory.Inventory;
import net.minecraft.container.GenericContainer;
import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.Direction;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemProvider;
import net.minecraft.block.Blocks;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;

public class ChestMinecartEntity extends StorageMinecartEntity
{
    public ChestMinecartEntity(final EntityType<? extends ChestMinecartEntity> type, final World world) {
        super(type, world);
    }
    
    public ChestMinecartEntity(final World world, final double double2, final double double4, final double double6) {
        super(EntityType.CHEST_MINECART, double2, double4, double6, world);
    }
    
    @Override
    public void dropItems(final DamageSource damageSource) {
        super.dropItems(damageSource);
        if (this.world.getGameRules().getBoolean("doEntityDrops")) {
            this.dropItem(Blocks.bP);
        }
    }
    
    @Override
    public int getInvSize() {
        return 27;
    }
    
    @Override
    public Type getMinecartType() {
        return Type.b;
    }
    
    @Override
    public BlockState getDefaultContainedBlock() {
        return ((AbstractPropertyContainer<O, BlockState>)Blocks.bP.getDefaultState()).<Comparable, Direction>with((Property<Comparable>)ChestBlock.FACING, Direction.NORTH);
    }
    
    @Override
    public int getDefaultBlockOffset() {
        return 8;
    }
    
    public Container a(final int integer, final PlayerInventory playerInventory) {
        return GenericContainer.createGeneric9x3(integer, playerInventory, this);
    }
}

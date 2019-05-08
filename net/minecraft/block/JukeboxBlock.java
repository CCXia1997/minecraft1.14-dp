package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.property.Properties;
import net.minecraft.state.StateFactory;
import net.minecraft.item.Item;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.world.BlockView;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.JukeboxBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.state.property.Property;
import net.minecraft.state.property.BooleanProperty;

public class JukeboxBlock extends BlockWithEntity
{
    public static final BooleanProperty HAS_RECORD;
    
    protected JukeboxBlock(final Settings settings) {
        super(settings);
        this.setDefaultState(((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).<Comparable, Boolean>with((Property<Comparable>)JukeboxBlock.HAS_RECORD, false));
    }
    
    @Override
    public boolean activate(BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockHitResult blockHitResult) {
        if (state.<Boolean>get((Property<Boolean>)JukeboxBlock.HAS_RECORD)) {
            this.removeRecord(world, pos);
            state = ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Boolean>with((Property<Comparable>)JukeboxBlock.HAS_RECORD, false);
            world.setBlockState(pos, state, 2);
            return true;
        }
        return false;
    }
    
    public void setRecord(final IWorld iWorld, final BlockPos pos, final BlockState state, final ItemStack itemStack) {
        final BlockEntity blockEntity5 = iWorld.getBlockEntity(pos);
        if (!(blockEntity5 instanceof JukeboxBlockEntity)) {
            return;
        }
        ((JukeboxBlockEntity)blockEntity5).setRecord(itemStack.copy());
        iWorld.setBlockState(pos, ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Boolean>with((Property<Comparable>)JukeboxBlock.HAS_RECORD, true), 2);
    }
    
    private void removeRecord(final World world, final BlockPos blockPos) {
        if (world.isClient) {
            return;
        }
        final BlockEntity blockEntity3 = world.getBlockEntity(blockPos);
        if (!(blockEntity3 instanceof JukeboxBlockEntity)) {
            return;
        }
        final JukeboxBlockEntity jukeboxBlockEntity4 = (JukeboxBlockEntity)blockEntity3;
        final ItemStack itemStack5 = jukeboxBlockEntity4.getRecord();
        if (itemStack5.isEmpty()) {
            return;
        }
        world.playLevelEvent(1010, blockPos, 0);
        jukeboxBlockEntity4.clear();
        final float float6 = 0.7f;
        final double double7 = world.random.nextFloat() * 0.7f + 0.15000000596046448;
        final double double8 = world.random.nextFloat() * 0.7f + 0.06000000238418579 + 0.6;
        final double double9 = world.random.nextFloat() * 0.7f + 0.15000000596046448;
        final ItemStack itemStack6 = itemStack5.copy();
        final ItemEntity itemEntity14 = new ItemEntity(world, blockPos.getX() + double7, blockPos.getY() + double8, blockPos.getZ() + double9, itemStack6);
        itemEntity14.setToDefaultPickupDelay();
        world.spawnEntity(itemEntity14);
    }
    
    @Override
    public void onBlockRemoved(final BlockState state, final World world, final BlockPos pos, final BlockState newState, final boolean boolean5) {
        if (state.getBlock() == newState.getBlock()) {
            return;
        }
        this.removeRecord(world, pos);
        super.onBlockRemoved(state, world, pos, newState, boolean5);
    }
    
    @Override
    public BlockEntity createBlockEntity(final BlockView view) {
        return new JukeboxBlockEntity();
    }
    
    @Override
    public boolean hasComparatorOutput(final BlockState state) {
        return true;
    }
    
    @Override
    public int getComparatorOutput(final BlockState state, final World world, final BlockPos pos) {
        final BlockEntity blockEntity4 = world.getBlockEntity(pos);
        if (blockEntity4 instanceof JukeboxBlockEntity) {
            final Item item5 = ((JukeboxBlockEntity)blockEntity4).getRecord().getItem();
            if (item5 instanceof MusicDiscItem) {
                return ((MusicDiscItem)item5).getComparatorOutput();
            }
        }
        return 0;
    }
    
    @Override
    public BlockRenderType getRenderType(final BlockState state) {
        return BlockRenderType.c;
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(JukeboxBlock.HAS_RECORD);
    }
    
    static {
        HAS_RECORD = Properties.HAS_RECORD;
    }
}

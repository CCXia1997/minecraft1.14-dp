package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.Direction;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.text.TextComponent;
import net.minecraft.client.network.ClientDummyContainerProvider;
import net.minecraft.container.LoomContainer;
import net.minecraft.container.BlockContext;
import net.minecraft.container.NameableContainerProvider;
import net.minecraft.stat.Stats;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.text.TranslatableTextComponent;

public class LoomBlock extends HorizontalFacingBlock
{
    private static final TranslatableTextComponent CONTAINER_NAME;
    
    protected LoomBlock(final Settings settings) {
        super(settings);
    }
    
    @Override
    public boolean activate(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockHitResult blockHitResult) {
        if (world.isClient) {
            return true;
        }
        player.openContainer(state.createContainerProvider(world, pos));
        player.incrementStat(Stats.av);
        return true;
    }
    
    @Override
    public NameableContainerProvider createContainerProvider(final BlockState state, final World world, final BlockPos pos) {
        return new ClientDummyContainerProvider((integer, playerInventory, playerEntity) -> new LoomContainer(integer, playerInventory, BlockContext.create(world, pos)), LoomBlock.CONTAINER_NAME);
    }
    
    @Override
    public BlockState getPlacementState(final ItemPlacementContext ctx) {
        return ((AbstractPropertyContainer<O, BlockState>)this.getDefaultState()).<Comparable, Direction>with((Property<Comparable>)LoomBlock.FACING, ctx.getPlayerHorizontalFacing().getOpposite());
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(LoomBlock.FACING);
    }
    
    static {
        CONTAINER_NAME = new TranslatableTextComponent("container.loom", new Object[0]);
    }
}

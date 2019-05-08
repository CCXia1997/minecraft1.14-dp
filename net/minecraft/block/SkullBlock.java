package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.property.Properties;
import net.minecraft.state.StateFactory;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.state.property.Property;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.state.property.IntegerProperty;

public class SkullBlock extends AbstractSkullBlock
{
    public static final IntegerProperty ROTATION;
    protected static final VoxelShape SHAPE;
    
    protected SkullBlock(final SkullType skullType, final Settings settings) {
        super(skullType, settings);
        this.setDefaultState(((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).<Comparable, Integer>with((Property<Comparable>)SkullBlock.ROTATION, 0));
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        return SkullBlock.SHAPE;
    }
    
    @Override
    public VoxelShape h(final BlockState state, final BlockView view, final BlockPos pos) {
        return VoxelShapes.empty();
    }
    
    @Override
    public BlockState getPlacementState(final ItemPlacementContext ctx) {
        return ((AbstractPropertyContainer<O, BlockState>)this.getDefaultState()).<Comparable, Integer>with((Property<Comparable>)SkullBlock.ROTATION, MathHelper.floor(ctx.getPlayerYaw() * 16.0f / 360.0f + 0.5) & 0xF);
    }
    
    @Override
    public BlockState rotate(final BlockState state, final BlockRotation rotation) {
        return ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Integer>with((Property<Comparable>)SkullBlock.ROTATION, rotation.rotate(state.<Integer>get((Property<Integer>)SkullBlock.ROTATION), 16));
    }
    
    @Override
    public BlockState mirror(final BlockState state, final BlockMirror mirror) {
        return ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Integer>with((Property<Comparable>)SkullBlock.ROTATION, mirror.mirror(state.<Integer>get((Property<Integer>)SkullBlock.ROTATION), 16));
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(SkullBlock.ROTATION);
    }
    
    static {
        ROTATION = Properties.ROTATION_16;
        SHAPE = Block.createCuboidShape(4.0, 0.0, 4.0, 12.0, 8.0, 12.0);
    }
    
    public enum Type implements SkullType
    {
        SKELETON, 
        WITHER_SKELETON, 
        PLAYER, 
        ZOMBIE, 
        CREEPER, 
        DRAGON;
    }
    
    public interface SkullType
    {
    }
}

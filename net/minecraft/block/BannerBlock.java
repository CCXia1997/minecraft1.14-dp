package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import com.google.common.collect.Maps;
import net.minecraft.state.property.Properties;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.state.StateFactory;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.world.IWorld;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.world.BlockView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ViewableWorld;
import net.minecraft.state.property.Property;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.DyeColor;
import java.util.Map;
import net.minecraft.state.property.IntegerProperty;

public class BannerBlock extends AbstractBannerBlock
{
    public static final IntegerProperty ROTATION;
    private static final Map<DyeColor, Block> COLORED_BANNERS;
    private static final VoxelShape SHAPE;
    
    public BannerBlock(final DyeColor dyeColor, final Settings settings) {
        super(dyeColor, settings);
        this.setDefaultState(((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).<Comparable, Integer>with((Property<Comparable>)BannerBlock.ROTATION, 0));
        BannerBlock.COLORED_BANNERS.put(dyeColor, this);
    }
    
    @Override
    public boolean canPlaceAt(final BlockState state, final ViewableWorld world, final BlockPos pos) {
        return world.getBlockState(pos.down()).getMaterial().isSolid();
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        return BannerBlock.SHAPE;
    }
    
    @Override
    public BlockState getPlacementState(final ItemPlacementContext ctx) {
        return ((AbstractPropertyContainer<O, BlockState>)this.getDefaultState()).<Comparable, Integer>with((Property<Comparable>)BannerBlock.ROTATION, MathHelper.floor((180.0f + ctx.getPlayerYaw()) * 16.0f / 360.0f + 0.5) & 0xF);
    }
    
    @Override
    public BlockState getStateForNeighborUpdate(final BlockState state, final Direction facing, final BlockState neighborState, final IWorld world, final BlockPos pos, final BlockPos neighborPos) {
        if (facing == Direction.DOWN && !state.canPlaceAt(world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
    }
    
    @Override
    public BlockState rotate(final BlockState state, final BlockRotation rotation) {
        return ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Integer>with((Property<Comparable>)BannerBlock.ROTATION, rotation.rotate(state.<Integer>get((Property<Integer>)BannerBlock.ROTATION), 16));
    }
    
    @Override
    public BlockState mirror(final BlockState state, final BlockMirror mirror) {
        return ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Integer>with((Property<Comparable>)BannerBlock.ROTATION, mirror.mirror(state.<Integer>get((Property<Integer>)BannerBlock.ROTATION), 16));
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(BannerBlock.ROTATION);
    }
    
    @Environment(EnvType.CLIENT)
    public static Block getForColor(final DyeColor color) {
        return BannerBlock.COLORED_BANNERS.getOrDefault(color, Blocks.gS);
    }
    
    static {
        ROTATION = Properties.ROTATION_16;
        COLORED_BANNERS = Maps.newHashMap();
        SHAPE = Block.createCuboidShape(4.0, 0.0, 4.0, 12.0, 16.0, 12.0);
    }
}

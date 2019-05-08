package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.structure.Structure;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import javax.annotation.Nullable;
import net.minecraft.block.entity.JigsawBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.BlockRotation;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.Direction;

public class JigsawBlock extends FacingBlock implements BlockEntityProvider
{
    protected JigsawBlock(final Settings settings) {
        super(settings);
        this.setDefaultState(((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).<Comparable, Direction>with((Property<Comparable>)JigsawBlock.FACING, Direction.UP));
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(JigsawBlock.FACING);
    }
    
    @Override
    public BlockState rotate(final BlockState state, final BlockRotation rotation) {
        return ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Direction>with((Property<Comparable>)JigsawBlock.FACING, rotation.rotate(state.<Direction>get((Property<Direction>)JigsawBlock.FACING)));
    }
    
    @Override
    public BlockState getPlacementState(final ItemPlacementContext ctx) {
        return ((AbstractPropertyContainer<O, BlockState>)this.getDefaultState()).<Comparable, Direction>with((Property<Comparable>)JigsawBlock.FACING, ctx.getFacing());
    }
    
    @Nullable
    @Override
    public BlockEntity createBlockEntity(final BlockView view) {
        return new JigsawBlockEntity();
    }
    
    @Override
    public boolean activate(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockHitResult blockHitResult) {
        final BlockEntity blockEntity7 = world.getBlockEntity(pos);
        if (blockEntity7 instanceof JigsawBlockEntity && player.isCreativeLevelTwoOp()) {
            player.openJigsawScreen((JigsawBlockEntity)blockEntity7);
            return true;
        }
        return false;
    }
    
    public static boolean attachmentMatches(final Structure.StructureBlockInfo info1, final Structure.StructureBlockInfo info2) {
        return info1.state.<Comparable>get((Property<Comparable>)JigsawBlock.FACING) == info2.state.<Direction>get((Property<Direction>)JigsawBlock.FACING).getOpposite() && info1.tag.getString("attachement_type").equals(info2.tag.getString("attachement_type"));
    }
}

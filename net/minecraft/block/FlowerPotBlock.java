package net.minecraft.block;

import com.google.common.collect.Maps;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.IWorld;
import net.minecraft.util.math.Direction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stats;
import net.minecraft.item.BlockItem;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.util.shape.VoxelShape;
import java.util.Map;

public class FlowerPotBlock extends Block
{
    private static final Map<Block, Block> CONTENT_TO_POTTED;
    protected static final VoxelShape SHAPE;
    private final Block content;
    
    public FlowerPotBlock(final Block content, final Settings settings) {
        super(settings);
        this.content = content;
        FlowerPotBlock.CONTENT_TO_POTTED.put(content, this);
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        return FlowerPotBlock.SHAPE;
    }
    
    @Override
    public BlockRenderType getRenderType(final BlockState state) {
        return BlockRenderType.c;
    }
    
    @Override
    public boolean activate(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockHitResult blockHitResult) {
        final ItemStack itemStack7 = player.getStackInHand(hand);
        final Item item8 = itemStack7.getItem();
        final Block block9 = (item8 instanceof BlockItem) ? FlowerPotBlock.CONTENT_TO_POTTED.getOrDefault(((BlockItem)item8).getBlock(), Blocks.AIR) : Blocks.AIR;
        final boolean boolean10 = block9 == Blocks.AIR;
        final boolean boolean11 = this.content == Blocks.AIR;
        if (boolean10 != boolean11) {
            if (boolean11) {
                world.setBlockState(pos, block9.getDefaultState(), 3);
                player.incrementStat(Stats.af);
                if (!player.abilities.creativeMode) {
                    itemStack7.subtractAmount(1);
                }
            }
            else {
                final ItemStack itemStack8 = new ItemStack(this.content);
                if (itemStack7.isEmpty()) {
                    player.setStackInHand(hand, itemStack8);
                }
                else if (!player.giveItemStack(itemStack8)) {
                    player.dropItem(itemStack8, false);
                }
                world.setBlockState(pos, Blocks.en.getDefaultState(), 3);
            }
        }
        return true;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public ItemStack getPickStack(final BlockView world, final BlockPos pos, final BlockState state) {
        if (this.content == Blocks.AIR) {
            return super.getPickStack(world, pos, state);
        }
        return new ItemStack(this.content);
    }
    
    @Override
    public BlockState getStateForNeighborUpdate(final BlockState state, final Direction facing, final BlockState neighborState, final IWorld world, final BlockPos pos, final BlockPos neighborPos) {
        if (facing == Direction.DOWN && !state.canPlaceAt(world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
    }
    
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }
    
    public Block getContent() {
        return this.content;
    }
    
    static {
        CONTENT_TO_POTTED = Maps.newHashMap();
        SHAPE = Block.createCuboidShape(5.0, 0.0, 5.0, 11.0, 6.0, 11.0);
    }
}

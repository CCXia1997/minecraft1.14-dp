package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.property.Properties;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.Property;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.state.property.EnumProperty;

public class StructureBlock extends BlockWithEntity
{
    public static final EnumProperty<StructureBlockMode> MODE;
    
    protected StructureBlock(final Settings settings) {
        super(settings);
    }
    
    @Override
    public BlockEntity createBlockEntity(final BlockView view) {
        return new StructureBlockBlockEntity();
    }
    
    @Override
    public boolean activate(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockHitResult blockHitResult) {
        final BlockEntity blockEntity7 = world.getBlockEntity(pos);
        return blockEntity7 instanceof StructureBlockBlockEntity && ((StructureBlockBlockEntity)blockEntity7).openScreen(player);
    }
    
    @Override
    public void onPlaced(final World world, final BlockPos pos, final BlockState state, @Nullable final LivingEntity placer, final ItemStack itemStack) {
        if (world.isClient) {
            return;
        }
        if (placer != null) {
            final BlockEntity blockEntity6 = world.getBlockEntity(pos);
            if (blockEntity6 instanceof StructureBlockBlockEntity) {
                ((StructureBlockBlockEntity)blockEntity6).setAuthor(placer);
            }
        }
    }
    
    @Override
    public BlockRenderType getRenderType(final BlockState state) {
        return BlockRenderType.c;
    }
    
    @Override
    public BlockState getPlacementState(final ItemPlacementContext ctx) {
        return ((AbstractPropertyContainer<O, BlockState>)this.getDefaultState()).<StructureBlockMode, StructureBlockMode>with(StructureBlock.MODE, StructureBlockMode.d);
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(StructureBlock.MODE);
    }
    
    @Override
    public void neighborUpdate(final BlockState state, final World world, final BlockPos pos, final Block block, final BlockPos neighborPos, final boolean boolean6) {
        if (world.isClient) {
            return;
        }
        final BlockEntity blockEntity7 = world.getBlockEntity(pos);
        if (!(blockEntity7 instanceof StructureBlockBlockEntity)) {
            return;
        }
        final StructureBlockBlockEntity structureBlockBlockEntity8 = (StructureBlockBlockEntity)blockEntity7;
        final boolean boolean7 = world.isReceivingRedstonePower(pos);
        final boolean boolean8 = structureBlockBlockEntity8.isPowered();
        if (boolean7 && !boolean8) {
            structureBlockBlockEntity8.setPowered(true);
            this.doAction(structureBlockBlockEntity8);
        }
        else if (!boolean7 && boolean8) {
            structureBlockBlockEntity8.setPowered(false);
        }
    }
    
    private void doAction(final StructureBlockBlockEntity blockEntity) {
        switch (blockEntity.getMode()) {
            case a: {
                blockEntity.saveStructure(false);
                break;
            }
            case b: {
                blockEntity.loadStructure(false);
                break;
            }
            case c: {
                blockEntity.unloadStructure();
            }
        }
    }
    
    static {
        MODE = Properties.STRUCTURE_BLOCK_MODE;
    }
}

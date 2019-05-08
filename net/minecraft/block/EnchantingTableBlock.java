package net.minecraft.block;

import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.LivingEntity;
import javax.annotation.Nullable;
import net.minecraft.text.TextComponent;
import net.minecraft.client.network.ClientDummyContainerProvider;
import net.minecraft.container.EnchantingTableContainer;
import net.minecraft.container.BlockContext;
import net.minecraft.util.Nameable;
import net.minecraft.container.NameableContainerProvider;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.block.entity.EnchantingTableBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.util.shape.VoxelShape;

public class EnchantingTableBlock extends BlockWithEntity
{
    protected static final VoxelShape SHAPE;
    
    protected EnchantingTableBlock(final Settings settings) {
        super(settings);
    }
    
    @Override
    public boolean n(final BlockState state) {
        return true;
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        return EnchantingTableBlock.SHAPE;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void randomDisplayTick(final BlockState state, final World world, final BlockPos pos, final Random rnd) {
        super.randomDisplayTick(state, world, pos, rnd);
        for (int integer5 = -2; integer5 <= 2; ++integer5) {
            for (int integer6 = -2; integer6 <= 2; ++integer6) {
                if (integer5 > -2 && integer5 < 2 && integer6 == -1) {
                    integer6 = 2;
                }
                if (rnd.nextInt(16) == 0) {
                    for (int integer7 = 0; integer7 <= 1; ++integer7) {
                        final BlockPos blockPos8 = pos.add(integer5, integer7, integer6);
                        if (world.getBlockState(blockPos8).getBlock() == Blocks.bH) {
                            if (!world.isAir(pos.add(integer5 / 2, 0, integer6 / 2))) {
                                break;
                            }
                            world.addParticle(ParticleTypes.s, pos.getX() + 0.5, pos.getY() + 2.0, pos.getZ() + 0.5, integer5 + rnd.nextFloat() - 0.5, integer7 - rnd.nextFloat() - 1.0f, integer6 + rnd.nextFloat() - 0.5);
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public BlockRenderType getRenderType(final BlockState state) {
        return BlockRenderType.c;
    }
    
    @Override
    public BlockEntity createBlockEntity(final BlockView view) {
        return new EnchantingTableBlockEntity();
    }
    
    @Override
    public boolean activate(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockHitResult blockHitResult) {
        if (world.isClient) {
            return true;
        }
        player.openContainer(state.createContainerProvider(world, pos));
        return true;
    }
    
    @Nullable
    @Override
    public NameableContainerProvider createContainerProvider(final BlockState state, final World world, final BlockPos pos) {
        final BlockEntity blockEntity4 = world.getBlockEntity(pos);
        if (blockEntity4 instanceof EnchantingTableBlockEntity) {
            final TextComponent textComponent5 = ((Nameable)blockEntity4).getDisplayName();
            return new ClientDummyContainerProvider((integer, playerInventory, playerEntity) -> new EnchantingTableContainer(integer, playerInventory, BlockContext.create(world, pos)), textComponent5);
        }
        return null;
    }
    
    @Override
    public void onPlaced(final World world, final BlockPos pos, final BlockState state, final LivingEntity placer, final ItemStack itemStack) {
        if (itemStack.hasDisplayName()) {
            final BlockEntity blockEntity6 = world.getBlockEntity(pos);
            if (blockEntity6 instanceof EnchantingTableBlockEntity) {
                ((EnchantingTableBlockEntity)blockEntity6).setCustomName(itemStack.getDisplayName());
            }
        }
    }
    
    @Override
    public boolean canPlaceAtSide(final BlockState world, final BlockView view, final BlockPos pos, final BlockPlacementEnvironment env) {
        return false;
    }
    
    static {
        SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 12.0, 16.0);
    }
}

package net.minecraft.block;

import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerInventory;
import javax.annotation.Nullable;
import net.minecraft.text.TextComponent;
import net.minecraft.client.network.ClientDummyContainerProvider;
import net.minecraft.container.CartographyTableContainer;
import net.minecraft.container.BlockContext;
import net.minecraft.container.NameableContainerProvider;
import net.minecraft.stat.Stats;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.text.TranslatableTextComponent;

public class CartographyTableBlock extends Block
{
    private static final TranslatableTextComponent CONTAINER_NAME;
    
    protected CartographyTableBlock(final Settings settings) {
        super(settings);
    }
    
    @Override
    public boolean activate(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockHitResult blockHitResult) {
        player.openContainer(state.createContainerProvider(world, pos));
        player.incrementStat(Stats.au);
        return true;
    }
    
    @Nullable
    @Override
    public NameableContainerProvider createContainerProvider(final BlockState state, final World world, final BlockPos pos) {
        return new ClientDummyContainerProvider((integer, playerInventory, playerEntity) -> new CartographyTableContainer(integer, playerInventory, BlockContext.create(world, pos)), CartographyTableBlock.CONTAINER_NAME);
    }
    
    static {
        CONTAINER_NAME = new TranslatableTextComponent("container.cartography_table", new Object[0]);
    }
}

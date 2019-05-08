package net.minecraft.block;

import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.client.network.ClientDummyContainerProvider;
import net.minecraft.container.CraftingTableContainer;
import net.minecraft.container.BlockContext;
import net.minecraft.container.NameableContainerProvider;
import net.minecraft.stat.Stats;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.text.TextComponent;

public class CraftingTableBlock extends Block
{
    private static final TextComponent CONTAINER_NAME;
    
    protected CraftingTableBlock(final Settings settings) {
        super(settings);
    }
    
    @Override
    public boolean activate(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockHitResult blockHitResult) {
        player.openContainer(state.createContainerProvider(world, pos));
        player.incrementStat(Stats.al);
        return true;
    }
    
    @Override
    public NameableContainerProvider createContainerProvider(final BlockState state, final World world, final BlockPos pos) {
        return new ClientDummyContainerProvider((integer, playerInventory, playerEntity) -> new CraftingTableContainer(integer, playerInventory, BlockContext.create(world, pos)), CraftingTableBlock.CONTAINER_NAME);
    }
    
    static {
        CONTAINER_NAME = new TranslatableTextComponent("container.crafting", new Object[0]);
    }
}

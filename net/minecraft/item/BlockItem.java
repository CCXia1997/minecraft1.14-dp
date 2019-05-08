package net.minecraft.item;

import net.minecraft.state.AbstractPropertyContainer;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.BlockView;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.text.TextComponent;
import java.util.List;
import net.minecraft.util.DefaultedList;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.ViewableWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.state.property.Property;
import java.util.Iterator;
import net.minecraft.state.StateFactory;
import net.minecraft.nbt.CompoundTag;
import javax.annotation.Nullable;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.BlockState;
import net.minecraft.sound.SoundCategory;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.block.Block;

public class BlockItem extends Item
{
    @Deprecated
    private final Block block;
    
    public BlockItem(final Block block, final Settings settings) {
        super(settings);
        this.block = block;
    }
    
    @Override
    public ActionResult useOnBlock(final ItemUsageContext usageContext) {
        final ActionResult actionResult2 = this.place(new ItemPlacementContext(usageContext));
        if (actionResult2 != ActionResult.a && this.isFood()) {
            return this.use(usageContext.world, usageContext.player, usageContext.hand).getResult();
        }
        return actionResult2;
    }
    
    public ActionResult place(final ItemPlacementContext itemPlacementContext) {
        if (!itemPlacementContext.canPlace()) {
            return ActionResult.c;
        }
        final ItemPlacementContext itemPlacementContext2 = this.getPlacementContext(itemPlacementContext);
        if (itemPlacementContext2 == null) {
            return ActionResult.c;
        }
        final BlockState blockState3 = this.getBlockState(itemPlacementContext2);
        if (blockState3 == null) {
            return ActionResult.c;
        }
        if (!this.setBlockState(itemPlacementContext2, blockState3)) {
            return ActionResult.c;
        }
        final BlockPos blockPos4 = itemPlacementContext2.getBlockPos();
        final World world5 = itemPlacementContext2.getWorld();
        final PlayerEntity playerEntity6 = itemPlacementContext2.getPlayer();
        final ItemStack itemStack7 = itemPlacementContext2.getItemStack();
        BlockState blockState4 = world5.getBlockState(blockPos4);
        final Block block9 = blockState4.getBlock();
        if (block9 == blockState3.getBlock()) {
            blockState4 = this.place(blockPos4, world5, itemStack7, blockState4);
            this.afterBlockPlaced(blockPos4, world5, playerEntity6, itemStack7, blockState4);
            block9.onPlaced(world5, blockPos4, blockState4, playerEntity6, itemStack7);
            if (playerEntity6 instanceof ServerPlayerEntity) {
                Criterions.PLACED_BLOCK.handle((ServerPlayerEntity)playerEntity6, blockPos4, itemStack7);
            }
        }
        final BlockSoundGroup blockSoundGroup10 = blockState4.getSoundGroup();
        world5.playSound(playerEntity6, blockPos4, this.getPlaceSound(blockState4), SoundCategory.e, (blockSoundGroup10.getVolume() + 1.0f) / 2.0f, blockSoundGroup10.getPitch() * 0.8f);
        itemStack7.subtractAmount(1);
        return ActionResult.a;
    }
    
    protected SoundEvent getPlaceSound(final BlockState blockState) {
        return blockState.getSoundGroup().getPlaceSound();
    }
    
    @Nullable
    public ItemPlacementContext getPlacementContext(final ItemPlacementContext itemPlacementContext) {
        return itemPlacementContext;
    }
    
    protected boolean afterBlockPlaced(final BlockPos blockPos, final World world, @Nullable final PlayerEntity playerEntity, final ItemStack itemStack, final BlockState blockState) {
        return copyItemTagToBlockEntity(world, playerEntity, blockPos, itemStack);
    }
    
    @Nullable
    protected BlockState getBlockState(final ItemPlacementContext itemPlacementContext) {
        final BlockState blockState2 = this.getBlock().getPlacementState(itemPlacementContext);
        return (blockState2 != null && this.canPlace(itemPlacementContext, blockState2)) ? blockState2 : null;
    }
    
    private BlockState place(final BlockPos pos, final World world, final ItemStack stack, final BlockState state) {
        BlockState blockState5 = state;
        final CompoundTag compoundTag6 = stack.getTag();
        if (compoundTag6 != null) {
            final CompoundTag compoundTag7 = compoundTag6.getCompound("BlockStateTag");
            final StateFactory<Block, BlockState> stateFactory8 = blockState5.getBlock().getStateFactory();
            for (final String string10 : compoundTag7.getKeys()) {
                final Property<?> property11 = stateFactory8.getProperty(string10);
                if (property11 != null) {
                    final String string11 = compoundTag7.getTag(string10).asString();
                    blockState5 = BlockItem.addProperty(blockState5, property11, string11);
                }
            }
        }
        if (blockState5 != state) {
            world.setBlockState(pos, blockState5, 2);
        }
        return blockState5;
    }
    
    private static <T extends Comparable<T>> BlockState addProperty(final BlockState state, final Property<T> property, final String name) {
        return property.getValue(name).<BlockState>map(comparable -> (BlockState)((AbstractPropertyContainer<Object, Object>)state).<T, Comparable>with(property, comparable)).orElse(state);
    }
    
    protected boolean canPlace(final ItemPlacementContext context, final BlockState state) {
        final PlayerEntity playerEntity3 = context.getPlayer();
        final VerticalEntityPosition verticalEntityPosition4 = (playerEntity3 == null) ? VerticalEntityPosition.minValue() : VerticalEntityPosition.fromEntity(playerEntity3);
        return (!this.shouldCheckIfStateAllowsPlacement() || state.canPlaceAt(context.getWorld(), context.getBlockPos())) && context.getWorld().canPlace(state, context.getBlockPos(), verticalEntityPosition4);
    }
    
    protected boolean shouldCheckIfStateAllowsPlacement() {
        return true;
    }
    
    protected boolean setBlockState(final ItemPlacementContext itemPlacementContext, final BlockState blockState) {
        return itemPlacementContext.getWorld().setBlockState(itemPlacementContext.getBlockPos(), blockState, 11);
    }
    
    public static boolean copyItemTagToBlockEntity(final World world, @Nullable final PlayerEntity player, final BlockPos pos, final ItemStack stack) {
        final MinecraftServer minecraftServer5 = world.getServer();
        if (minecraftServer5 == null) {
            return false;
        }
        final CompoundTag compoundTag6 = stack.getSubCompoundTag("BlockEntityTag");
        if (compoundTag6 != null) {
            final BlockEntity blockEntity7 = world.getBlockEntity(pos);
            if (blockEntity7 != null) {
                if (!world.isClient && blockEntity7.shouldNotCopyTagFromItem() && (player == null || !player.isCreativeLevelTwoOp())) {
                    return false;
                }
                final CompoundTag compoundTag7 = blockEntity7.toTag(new CompoundTag());
                final CompoundTag compoundTag8 = compoundTag7.copy();
                compoundTag7.copyFrom(compoundTag6);
                compoundTag7.putInt("x", pos.getX());
                compoundTag7.putInt("y", pos.getY());
                compoundTag7.putInt("z", pos.getZ());
                if (!compoundTag7.equals(compoundTag8)) {
                    blockEntity7.fromTag(compoundTag7);
                    blockEntity7.markDirty();
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public String getTranslationKey() {
        return this.getBlock().getTranslationKey();
    }
    
    @Override
    public void appendItemsForGroup(final ItemGroup itemGroup, final DefaultedList<ItemStack> defaultedList) {
        if (this.isInItemGroup(itemGroup)) {
            this.getBlock().addStacksForDisplay(itemGroup, defaultedList);
        }
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void buildTooltip(final ItemStack stack, @Nullable final World world, final List<TextComponent> tooltip, final TooltipContext options) {
        super.buildTooltip(stack, world, tooltip, options);
        this.getBlock().buildTooltip(stack, world, tooltip, options);
    }
    
    public Block getBlock() {
        return this.block;
    }
    
    public void registerBlockItemMap(final Map<Block, Item> map, final Item item) {
        map.put(this.getBlock(), item);
    }
}

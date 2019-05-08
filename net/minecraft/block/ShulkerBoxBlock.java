package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import java.util.function.Consumer;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.Item;
import net.minecraft.container.Container;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.block.piston.PistonBehavior;
import java.util.Iterator;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.inventory.Inventories;
import net.minecraft.util.DefaultedList;
import net.minecraft.text.StringTextComponent;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.text.TextComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.loot.context.LootContextParameters;
import java.util.List;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.state.StateFactory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.stat.Stats;
import net.minecraft.container.NameableContainerProvider;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;
import net.minecraft.state.property.Property;
import javax.annotation.Nullable;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.state.property.EnumProperty;

public class ShulkerBoxBlock extends BlockWithEntity
{
    public static final EnumProperty<Direction> FACING;
    public static final Identifier b;
    @Nullable
    private final DyeColor color;
    
    public ShulkerBoxBlock(@Nullable final DyeColor dyeColor, final Settings settings) {
        super(settings);
        this.color = dyeColor;
        this.setDefaultState(((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).<Direction, Direction>with(ShulkerBoxBlock.FACING, Direction.UP));
    }
    
    @Override
    public BlockEntity createBlockEntity(final BlockView view) {
        return new ShulkerBoxBlockEntity(this.color);
    }
    
    @Override
    public boolean canSuffocate(final BlockState state, final BlockView view, final BlockPos pos) {
        return true;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public boolean hasBlockEntityBreakingRender(final BlockState state) {
        return true;
    }
    
    @Override
    public BlockRenderType getRenderType(final BlockState state) {
        return BlockRenderType.b;
    }
    
    @Override
    public boolean activate(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockHitResult blockHitResult) {
        if (world.isClient) {
            return true;
        }
        if (player.isSpectator()) {
            return true;
        }
        final BlockEntity blockEntity7 = world.getBlockEntity(pos);
        if (blockEntity7 instanceof ShulkerBoxBlockEntity) {
            final Direction direction8 = state.<Direction>get(ShulkerBoxBlock.FACING);
            final ShulkerBoxBlockEntity shulkerBoxBlockEntity10 = (ShulkerBoxBlockEntity)blockEntity7;
            boolean boolean9;
            if (shulkerBoxBlockEntity10.getAnimationStage() == ShulkerBoxBlockEntity.AnimationStage.CLOSED) {
                final BoundingBox boundingBox11 = VoxelShapes.fullCube().getBoundingBox().stretch(0.5f * direction8.getOffsetX(), 0.5f * direction8.getOffsetY(), 0.5f * direction8.getOffsetZ()).shrink(direction8.getOffsetX(), direction8.getOffsetY(), direction8.getOffsetZ());
                boolean9 = world.doesNotCollide(boundingBox11.offset(pos.offset(direction8)));
            }
            else {
                boolean9 = true;
            }
            if (boolean9) {
                player.openContainer(shulkerBoxBlockEntity10);
                player.incrementStat(Stats.ao);
            }
            return true;
        }
        return false;
    }
    
    @Override
    public BlockState getPlacementState(final ItemPlacementContext ctx) {
        return ((AbstractPropertyContainer<O, BlockState>)this.getDefaultState()).<Direction, Direction>with(ShulkerBoxBlock.FACING, ctx.getFacing());
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(ShulkerBoxBlock.FACING);
    }
    
    @Override
    public void onBreak(final World world, final BlockPos pos, final BlockState state, final PlayerEntity player) {
        final BlockEntity blockEntity5 = world.getBlockEntity(pos);
        if (blockEntity5 instanceof ShulkerBoxBlockEntity) {
            final ShulkerBoxBlockEntity shulkerBoxBlockEntity6 = (ShulkerBoxBlockEntity)blockEntity5;
            if (!world.isClient && player.isCreative()) {
                final ItemStack itemStack7 = getItemStack(this.getColor());
                final CompoundTag compoundTag8 = shulkerBoxBlockEntity6.serializeInventory(new CompoundTag());
                if (!compoundTag8.isEmpty()) {
                    itemStack7.setChildTag("BlockEntityTag", compoundTag8);
                }
                final ItemEntity itemEntity9 = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), itemStack7);
                itemEntity9.setToDefaultPickupDelay();
                world.spawnEntity(itemEntity9);
            }
            else {
                shulkerBoxBlockEntity6.checkLootInteraction(player);
            }
        }
        super.onBreak(world, pos, state, player);
    }
    
    @Override
    public List<ItemStack> getDroppedStacks(final BlockState state, LootContext.Builder builder) {
        final BlockEntity blockEntity3 = builder.<BlockEntity>getNullable(LootContextParameters.h);
        if (blockEntity3 instanceof ShulkerBoxBlockEntity) {
            final ShulkerBoxBlockEntity shulkerBoxBlockEntity4 = (ShulkerBoxBlockEntity)blockEntity3;
            int integer4;
            final ShulkerBoxBlockEntity shulkerBoxBlockEntity5;
            builder = builder.putDrop(ShulkerBoxBlock.b, (lootContext, consumer) -> {
                for (integer4 = 0; integer4 < shulkerBoxBlockEntity5.getInvSize(); ++integer4) {
                    consumer.accept(shulkerBoxBlockEntity5.getInvStack(integer4));
                }
                return;
            });
        }
        return super.getDroppedStacks(state, builder);
    }
    
    @Override
    public void onPlaced(final World world, final BlockPos pos, final BlockState state, final LivingEntity placer, final ItemStack itemStack) {
        if (itemStack.hasDisplayName()) {
            final BlockEntity blockEntity6 = world.getBlockEntity(pos);
            if (blockEntity6 instanceof ShulkerBoxBlockEntity) {
                ((ShulkerBoxBlockEntity)blockEntity6).setCustomName(itemStack.getDisplayName());
            }
        }
    }
    
    @Override
    public void onBlockRemoved(final BlockState state, final World world, final BlockPos pos, final BlockState newState, final boolean boolean5) {
        if (state.getBlock() == newState.getBlock()) {
            return;
        }
        final BlockEntity blockEntity6 = world.getBlockEntity(pos);
        if (blockEntity6 instanceof ShulkerBoxBlockEntity) {
            world.updateHorizontalAdjacent(pos, state.getBlock());
        }
        super.onBlockRemoved(state, world, pos, newState, boolean5);
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void buildTooltip(final ItemStack stack, @Nullable final BlockView view, final List<TextComponent> tooltip, final TooltipContext options) {
        super.buildTooltip(stack, view, tooltip, options);
        final CompoundTag compoundTag5 = stack.getSubCompoundTag("BlockEntityTag");
        if (compoundTag5 != null) {
            if (compoundTag5.containsKey("LootTable", 8)) {
                tooltip.add(new StringTextComponent("???????"));
            }
            if (compoundTag5.containsKey("Items", 9)) {
                final DefaultedList<ItemStack> defaultedList6 = DefaultedList.<ItemStack>create(27, ItemStack.EMPTY);
                Inventories.fromTag(compoundTag5, defaultedList6);
                int integer7 = 0;
                int integer8 = 0;
                for (final ItemStack itemStack10 : defaultedList6) {
                    if (!itemStack10.isEmpty()) {
                        ++integer8;
                        if (integer7 > 4) {
                            continue;
                        }
                        ++integer7;
                        final TextComponent textComponent11 = itemStack10.getDisplayName().copy();
                        textComponent11.append(" x").append(String.valueOf(itemStack10.getAmount()));
                        tooltip.add(textComponent11);
                    }
                }
                if (integer8 - integer7 > 0) {
                    tooltip.add(new TranslatableTextComponent("container.shulkerBox.more", new Object[] { integer8 - integer7 }).applyFormat(TextFormat.u));
                }
            }
        }
    }
    
    @Override
    public PistonBehavior getPistonBehavior(final BlockState state) {
        return PistonBehavior.b;
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        final BlockEntity blockEntity5 = view.getBlockEntity(pos);
        if (blockEntity5 instanceof ShulkerBoxBlockEntity) {
            return VoxelShapes.cuboid(((ShulkerBoxBlockEntity)blockEntity5).getBoundingBox(state));
        }
        return VoxelShapes.fullCube();
    }
    
    @Override
    public boolean isFullBoundsCubeForCulling(final BlockState state) {
        return false;
    }
    
    @Override
    public boolean hasComparatorOutput(final BlockState state) {
        return true;
    }
    
    @Override
    public int getComparatorOutput(final BlockState state, final World world, final BlockPos pos) {
        return Container.calculateComparatorOutput((Inventory)world.getBlockEntity(pos));
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public ItemStack getPickStack(final BlockView world, final BlockPos pos, final BlockState state) {
        final ItemStack itemStack4 = super.getPickStack(world, pos, state);
        final ShulkerBoxBlockEntity shulkerBoxBlockEntity5 = (ShulkerBoxBlockEntity)world.getBlockEntity(pos);
        final CompoundTag compoundTag6 = shulkerBoxBlockEntity5.serializeInventory(new CompoundTag());
        if (!compoundTag6.isEmpty()) {
            itemStack4.setChildTag("BlockEntityTag", compoundTag6);
        }
        return itemStack4;
    }
    
    @Nullable
    @Environment(EnvType.CLIENT)
    public static DyeColor getColor(final Item item) {
        return getColor(Block.getBlockFromItem(item));
    }
    
    @Nullable
    @Environment(EnvType.CLIENT)
    public static DyeColor getColor(final Block block) {
        if (block instanceof ShulkerBoxBlock) {
            return ((ShulkerBoxBlock)block).getColor();
        }
        return null;
    }
    
    public static Block get(@Nullable final DyeColor dyeColor) {
        if (dyeColor == null) {
            return Blocks.iH;
        }
        switch (dyeColor) {
            case a: {
                return Blocks.iI;
            }
            case b: {
                return Blocks.iJ;
            }
            case c: {
                return Blocks.iK;
            }
            case d: {
                return Blocks.iL;
            }
            case e: {
                return Blocks.iM;
            }
            case f: {
                return Blocks.iN;
            }
            case g: {
                return Blocks.iO;
            }
            case h: {
                return Blocks.iP;
            }
            case i: {
                return Blocks.iQ;
            }
            case j: {
                return Blocks.iR;
            }
            default: {
                return Blocks.iS;
            }
            case l: {
                return Blocks.iT;
            }
            case m: {
                return Blocks.iU;
            }
            case n: {
                return Blocks.iV;
            }
            case o: {
                return Blocks.iW;
            }
            case BLACK: {
                return Blocks.iX;
            }
        }
    }
    
    @Nullable
    public DyeColor getColor() {
        return this.color;
    }
    
    public static ItemStack getItemStack(@Nullable final DyeColor color) {
        return new ItemStack(get(color));
    }
    
    @Override
    public BlockState rotate(final BlockState state, final BlockRotation rotation) {
        return ((AbstractPropertyContainer<O, BlockState>)state).<Direction, Direction>with(ShulkerBoxBlock.FACING, rotation.rotate(state.<Direction>get(ShulkerBoxBlock.FACING)));
    }
    
    @Override
    public BlockState mirror(final BlockState state, final BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.<Direction>get(ShulkerBoxBlock.FACING)));
    }
    
    static {
        FACING = FacingBlock.FACING;
        b = new Identifier("contents");
    }
}

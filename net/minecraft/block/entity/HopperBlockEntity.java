package net.minecraft.block.entity;

import java.util.stream.Stream;
import net.minecraft.container.HopperContainer;
import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.block.ChestBlock;
import net.minecraft.world.IWorld;
import net.minecraft.block.InventoryProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.predicate.entity.EntityPredicates;
import javax.annotation.Nullable;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.SidedInventory;
import java.util.stream.IntStream;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.math.Direction;
import java.util.Iterator;
import net.minecraft.state.property.Property;
import net.minecraft.block.HopperBlock;
import java.util.function.Supplier;
import net.minecraft.block.Blocks;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.text.TextComponent;
import java.util.List;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Tickable;

public class HopperBlockEntity extends LootableContainerBlockEntity implements Hopper, Tickable
{
    private DefaultedList<ItemStack> inventory;
    private int transferCooldown;
    private long lastTickTime;
    
    public HopperBlockEntity() {
        super(BlockEntityType.HOPPER);
        this.inventory = DefaultedList.<ItemStack>create(5, ItemStack.EMPTY);
        this.transferCooldown = -1;
    }
    
    @Override
    public void fromTag(final CompoundTag compoundTag) {
        super.fromTag(compoundTag);
        this.inventory = DefaultedList.<ItemStack>create(this.getInvSize(), ItemStack.EMPTY);
        if (!this.deserializeLootTable(compoundTag)) {
            Inventories.fromTag(compoundTag, this.inventory);
        }
        this.transferCooldown = compoundTag.getInt("TransferCooldown");
    }
    
    @Override
    public CompoundTag toTag(final CompoundTag compoundTag) {
        super.toTag(compoundTag);
        if (!this.serializeLootTable(compoundTag)) {
            Inventories.toTag(compoundTag, this.inventory);
        }
        compoundTag.putInt("TransferCooldown", this.transferCooldown);
        return compoundTag;
    }
    
    @Override
    public int getInvSize() {
        return this.inventory.size();
    }
    
    @Override
    public ItemStack takeInvStack(final int slot, final int integer2) {
        this.checkLootInteraction(null);
        return Inventories.splitStack(this.getInvStackList(), slot, integer2);
    }
    
    @Override
    public void setInvStack(final int slot, final ItemStack itemStack) {
        this.checkLootInteraction(null);
        this.getInvStackList().set(slot, itemStack);
        if (itemStack.getAmount() > this.getInvMaxStackAmount()) {
            itemStack.setAmount(this.getInvMaxStackAmount());
        }
    }
    
    @Override
    protected TextComponent getContainerName() {
        return new TranslatableTextComponent("container.hopper", new Object[0]);
    }
    
    @Override
    public void tick() {
        if (this.world == null || this.world.isClient) {
            return;
        }
        if (this.getCachedState().getBlock() != Blocks.fq) {
            return;
        }
        --this.transferCooldown;
        this.lastTickTime = this.world.getTime();
        if (!this.needsCooldown()) {
            this.setCooldown(0);
            this.insertAndExtract(() -> extract(this));
        }
    }
    
    private boolean insertAndExtract(final Supplier<Boolean> extractMethod) {
        if (this.world == null || this.world.isClient) {
            return false;
        }
        if (!this.needsCooldown() && this.getCachedState().<Boolean>get((Property<Boolean>)HopperBlock.ENABLED)) {
            boolean boolean2 = false;
            if (!this.isEmpty()) {
                boolean2 = this.insert();
            }
            if (!this.isFull()) {
                boolean2 |= extractMethod.get();
            }
            if (boolean2) {
                this.setCooldown(8);
                this.markDirty();
                return true;
            }
        }
        return false;
    }
    
    private boolean isEmpty() {
        for (final ItemStack itemStack2 : this.inventory) {
            if (!itemStack2.isEmpty()) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public boolean isInvEmpty() {
        return this.isEmpty();
    }
    
    private boolean isFull() {
        for (final ItemStack itemStack2 : this.inventory) {
            if (itemStack2.isEmpty() || itemStack2.getAmount() != itemStack2.getMaxAmount()) {
                return false;
            }
        }
        return true;
    }
    
    private boolean insert() {
        final Inventory inventory1 = this.getOutputInventory();
        if (inventory1 == null) {
            return false;
        }
        final Direction direction2 = this.getCachedState().<Direction>get((Property<Direction>)HopperBlock.FACING).getOpposite();
        if (this.isInventoryFull(inventory1, direction2)) {
            return false;
        }
        for (int integer3 = 0; integer3 < this.getInvSize(); ++integer3) {
            if (!this.getInvStack(integer3).isEmpty()) {
                final ItemStack itemStack4 = this.getInvStack(integer3).copy();
                final ItemStack itemStack5 = transfer(this, inventory1, this.takeInvStack(integer3, 1), direction2);
                if (itemStack5.isEmpty()) {
                    inventory1.markDirty();
                    return true;
                }
                this.setInvStack(integer3, itemStack4);
            }
        }
        return false;
    }
    
    private static IntStream getAvailableSlots(final Inventory inventory, final Direction side) {
        if (inventory instanceof SidedInventory) {
            return IntStream.of(((SidedInventory)inventory).getInvAvailableSlots(side));
        }
        return IntStream.range(0, inventory.getInvSize());
    }
    
    private boolean isInventoryFull(final Inventory inv, final Direction direction) {
        final ItemStack itemStack3;
        return getAvailableSlots(inv, direction).allMatch(integer -> {
            itemStack3 = inv.getInvStack(integer);
            return itemStack3.getAmount() >= itemStack3.getMaxAmount();
        });
    }
    
    private static boolean isInventoryEmpty(final Inventory inv, final Direction facing) {
        return getAvailableSlots(inv, facing).allMatch(integer -> inv.getInvStack(integer).isEmpty());
    }
    
    public static boolean extract(final Hopper hopper) {
        final Inventory inventory2 = getInputInventory(hopper);
        if (inventory2 != null) {
            final Direction direction3 = Direction.DOWN;
            return !isInventoryEmpty(inventory2, direction3) && getAvailableSlots(inventory2, direction3).anyMatch(integer -> extract(hopper, inventory2, integer, direction3));
        }
        for (final ItemEntity itemEntity4 : getInputItemEntities(hopper)) {
            if (extract(hopper, itemEntity4)) {
                return true;
            }
        }
        return false;
    }
    
    private static boolean extract(final Hopper hopper, final Inventory inventory, final int slot, final Direction side) {
        final ItemStack itemStack5 = inventory.getInvStack(slot);
        if (!itemStack5.isEmpty() && canExtract(inventory, itemStack5, slot, side)) {
            final ItemStack itemStack6 = itemStack5.copy();
            final ItemStack itemStack7 = transfer(inventory, hopper, inventory.takeInvStack(slot, 1), null);
            if (itemStack7.isEmpty()) {
                inventory.markDirty();
                return true;
            }
            inventory.setInvStack(slot, itemStack6);
        }
        return false;
    }
    
    public static boolean extract(final Inventory inventory, final ItemEntity itemEntity) {
        boolean boolean3 = false;
        final ItemStack itemStack4 = itemEntity.getStack().copy();
        final ItemStack itemStack5 = transfer(null, inventory, itemStack4, null);
        if (itemStack5.isEmpty()) {
            boolean3 = true;
            itemEntity.remove();
        }
        else {
            itemEntity.setStack(itemStack5);
        }
        return boolean3;
    }
    
    public static ItemStack transfer(@Nullable final Inventory from, final Inventory to, ItemStack stack, @Nullable final Direction side) {
        if (to instanceof SidedInventory && side != null) {
            final SidedInventory sidedInventory5 = (SidedInventory)to;
            final int[] arr6 = sidedInventory5.getInvAvailableSlots(side);
            for (int integer7 = 0; integer7 < arr6.length && !stack.isEmpty(); stack = transfer(from, to, stack, arr6[integer7], side), ++integer7) {}
        }
        else {
            for (int integer8 = to.getInvSize(), integer9 = 0; integer9 < integer8 && !stack.isEmpty(); stack = transfer(from, to, stack, integer9, side), ++integer9) {}
        }
        return stack;
    }
    
    private static boolean canInsert(final Inventory inventory, final ItemStack stack, final int slot, @Nullable final Direction side) {
        return inventory.isValidInvStack(slot, stack) && (!(inventory instanceof SidedInventory) || ((SidedInventory)inventory).canInsertInvStack(slot, stack, side));
    }
    
    private static boolean canExtract(final Inventory inv, final ItemStack stack, final int slot, final Direction facing) {
        return !(inv instanceof SidedInventory) || ((SidedInventory)inv).canExtractInvStack(slot, stack, facing);
    }
    
    private static ItemStack transfer(@Nullable final Inventory from, final Inventory to, ItemStack stack, final int slot, @Nullable final Direction direction) {
        final ItemStack itemStack6 = to.getInvStack(slot);
        if (canInsert(to, stack, slot, direction)) {
            boolean boolean7 = false;
            final boolean boolean8 = to.isInvEmpty();
            if (itemStack6.isEmpty()) {
                to.setInvStack(slot, stack);
                stack = ItemStack.EMPTY;
                boolean7 = true;
            }
            else if (canMergeItems(itemStack6, stack)) {
                final int integer9 = stack.getMaxAmount() - itemStack6.getAmount();
                final int integer10 = Math.min(stack.getAmount(), integer9);
                stack.subtractAmount(integer10);
                itemStack6.addAmount(integer10);
                boolean7 = (integer10 > 0);
            }
            if (boolean7) {
                if (boolean8 && to instanceof HopperBlockEntity) {
                    final HopperBlockEntity hopperBlockEntity9 = (HopperBlockEntity)to;
                    if (!hopperBlockEntity9.isDisabled()) {
                        int integer10 = 0;
                        if (from instanceof HopperBlockEntity) {
                            final HopperBlockEntity hopperBlockEntity10 = (HopperBlockEntity)from;
                            if (hopperBlockEntity9.lastTickTime >= hopperBlockEntity10.lastTickTime) {
                                integer10 = 1;
                            }
                        }
                        hopperBlockEntity9.setCooldown(8 - integer10);
                    }
                }
                to.markDirty();
            }
        }
        return stack;
    }
    
    @Nullable
    private Inventory getOutputInventory() {
        final Direction direction1 = this.getCachedState().<Direction>get((Property<Direction>)HopperBlock.FACING);
        return getInventoryAt(this.getWorld(), this.pos.offset(direction1));
    }
    
    @Nullable
    public static Inventory getInputInventory(final Hopper hopper) {
        return getInventoryAt(hopper.getWorld(), hopper.getHopperX(), hopper.getHopperY() + 1.0, hopper.getHopperZ());
    }
    
    public static List<ItemEntity> getInputItemEntities(final Hopper hopper) {
        return hopper.getInputAreaShape().getBoundingBoxes().stream().flatMap(boundingBox -> hopper.getWorld().<Entity>getEntities(ItemEntity.class, boundingBox.offset(hopper.getHopperX() - 0.5, hopper.getHopperY() - 0.5, hopper.getHopperZ() - 0.5), EntityPredicates.VALID_ENTITY).stream()).collect(Collectors.toList());
    }
    
    @Nullable
    public static Inventory getInventoryAt(final World world, final BlockPos blockPos) {
        return getInventoryAt(world, blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5);
    }
    
    @Nullable
    public static Inventory getInventoryAt(final World world, final double x, final double y, final double z) {
        Inventory inventory8 = null;
        final BlockPos blockPos9 = new BlockPos(x, y, z);
        final BlockState blockState10 = world.getBlockState(blockPos9);
        final Block block11 = blockState10.getBlock();
        if (block11 instanceof InventoryProvider) {
            inventory8 = ((InventoryProvider)block11).getInventory(blockState10, world, blockPos9);
        }
        else if (block11.hasBlockEntity()) {
            final BlockEntity blockEntity12 = world.getBlockEntity(blockPos9);
            if (blockEntity12 instanceof Inventory) {
                inventory8 = (Inventory)blockEntity12;
                if (inventory8 instanceof ChestBlockEntity && block11 instanceof ChestBlock) {
                    inventory8 = ChestBlock.getInventory(blockState10, world, blockPos9, true);
                }
            }
        }
        if (inventory8 == null) {
            final List<Entity> list12 = world.getEntities((Entity)null, new BoundingBox(x - 0.5, y - 0.5, z - 0.5, x + 0.5, y + 0.5, z + 0.5), EntityPredicates.VALID_INVENTORIES);
            if (!list12.isEmpty()) {
                inventory8 = (Inventory)list12.get(world.random.nextInt(list12.size()));
            }
        }
        return inventory8;
    }
    
    private static boolean canMergeItems(final ItemStack first, final ItemStack second) {
        return first.getItem() == second.getItem() && first.getDamage() == second.getDamage() && first.getAmount() <= first.getMaxAmount() && ItemStack.areTagsEqual(first, second);
    }
    
    @Override
    public double getHopperX() {
        return this.pos.getX() + 0.5;
    }
    
    @Override
    public double getHopperY() {
        return this.pos.getY() + 0.5;
    }
    
    @Override
    public double getHopperZ() {
        return this.pos.getZ() + 0.5;
    }
    
    private void setCooldown(final int cooldown) {
        this.transferCooldown = cooldown;
    }
    
    private boolean needsCooldown() {
        return this.transferCooldown > 0;
    }
    
    private boolean isDisabled() {
        return this.transferCooldown > 8;
    }
    
    @Override
    protected DefaultedList<ItemStack> getInvStackList() {
        return this.inventory;
    }
    
    @Override
    protected void setInvStackList(final DefaultedList<ItemStack> list) {
        this.inventory = list;
    }
    
    public void onEntityCollided(final Entity entity) {
        if (entity instanceof ItemEntity) {
            final BlockPos blockPos2 = this.getPos();
            if (VoxelShapes.matchesAnywhere(VoxelShapes.cuboid(entity.getBoundingBox().offset(-blockPos2.getX(), -blockPos2.getY(), -blockPos2.getZ())), this.getInputAreaShape(), BooleanBiFunction.AND)) {
                this.insertAndExtract(() -> extract(this, (ItemEntity)entity));
            }
        }
    }
    
    @Override
    protected Container createContainer(final int integer, final PlayerInventory playerInventory) {
        return new HopperContainer(integer, playerInventory, this);
    }
}

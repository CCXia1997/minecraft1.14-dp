package net.minecraft.container;

import net.minecraft.util.math.BlockPos;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import net.minecraft.entity.player.PlayerInventory;
import java.util.Iterator;
import net.minecraft.inventory.Inventory;
import net.minecraft.block.Block;
import com.google.common.collect.Sets;
import com.google.common.collect.Lists;
import net.minecraft.entity.player.PlayerEntity;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import javax.annotation.Nullable;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;

public abstract class Container
{
    private final DefaultedList<ItemStack> stackList;
    public final List<Slot> slotList;
    private final List<Property> properties;
    @Nullable
    private final ContainerType<?> type;
    public final int syncId;
    @Environment(EnvType.CLIENT)
    private short actionId;
    private int quickCraftStage;
    private int quickCraftButton;
    private final Set<Slot> quickCraftSlots;
    private final List<ContainerListener> listeners;
    private final Set<PlayerEntity> restrictedPlayers;
    
    protected Container(@Nullable final ContainerType<?> type, final int syncId) {
        this.stackList = DefaultedList.<ItemStack>create();
        this.slotList = Lists.newArrayList();
        this.properties = Lists.newArrayList();
        this.quickCraftStage = -1;
        this.quickCraftSlots = Sets.newHashSet();
        this.listeners = Lists.newArrayList();
        this.restrictedPlayers = Sets.newHashSet();
        this.type = type;
        this.syncId = syncId;
    }
    
    protected static boolean canUse(final BlockContext blockContext, final PlayerEntity playerEntity, final Block block) {
        return blockContext.<Boolean>run((world, blockPos) -> {
            if (world.getBlockState(blockPos).getBlock() != block) {
                return false;
            }
            else {
                return playerEntity.squaredDistanceTo(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5) <= 64.0;
            }
        }, Boolean.valueOf(true));
    }
    
    public ContainerType<?> getType() {
        if (this.type == null) {
            throw new UnsupportedOperationException("Unable to construct this menu by type");
        }
        return this.type;
    }
    
    protected static void checkContainerSize(final Inventory inventory, final int expectedSize) {
        final int integer3 = inventory.getInvSize();
        if (integer3 < expectedSize) {
            throw new IllegalArgumentException("Container size " + integer3 + " is smaller than expected " + expectedSize);
        }
    }
    
    protected static void checkContainerDataCount(final PropertyDelegate data, final int expectedCount) {
        final int integer3 = data.size();
        if (integer3 < expectedCount) {
            throw new IllegalArgumentException("Container data count " + integer3 + " is smaller than expected " + expectedCount);
        }
    }
    
    protected Slot addSlot(final Slot slot) {
        slot.id = this.slotList.size();
        this.slotList.add(slot);
        this.stackList.add(ItemStack.EMPTY);
        return slot;
    }
    
    protected Property addProperty(final Property property) {
        this.properties.add(property);
        return property;
    }
    
    protected void addProperties(final PropertyDelegate propertyDelegate) {
        for (int integer2 = 0; integer2 < propertyDelegate.size(); ++integer2) {
            this.addProperty(Property.create(propertyDelegate, integer2));
        }
    }
    
    public void addListener(final ContainerListener listener) {
        if (this.listeners.contains(listener)) {
            return;
        }
        this.listeners.add(listener);
        listener.onContainerRegistered(this, this.getStacks());
        this.sendContentUpdates();
    }
    
    @Environment(EnvType.CLIENT)
    public void removeListener(final ContainerListener listener) {
        this.listeners.remove(listener);
    }
    
    public DefaultedList<ItemStack> getStacks() {
        final DefaultedList<ItemStack> defaultedList1 = DefaultedList.<ItemStack>create();
        for (int integer2 = 0; integer2 < this.slotList.size(); ++integer2) {
            defaultedList1.add(this.slotList.get(integer2).getStack());
        }
        return defaultedList1;
    }
    
    public void sendContentUpdates() {
        for (int integer1 = 0; integer1 < this.slotList.size(); ++integer1) {
            final ItemStack itemStack2 = this.slotList.get(integer1).getStack();
            ItemStack itemStack3 = this.stackList.get(integer1);
            if (!ItemStack.areEqual(itemStack3, itemStack2)) {
                itemStack3 = (itemStack2.isEmpty() ? ItemStack.EMPTY : itemStack2.copy());
                this.stackList.set(integer1, itemStack3);
                for (final ContainerListener containerListener5 : this.listeners) {
                    containerListener5.onContainerSlotUpdate(this, integer1, itemStack3);
                }
            }
        }
        for (int integer1 = 0; integer1 < this.properties.size(); ++integer1) {
            final Property property2 = this.properties.get(integer1);
            if (property2.detectChanges()) {
                for (final ContainerListener containerListener6 : this.listeners) {
                    containerListener6.onContainerPropertyUpdate(this, integer1, property2.get());
                }
            }
        }
    }
    
    public boolean onButtonClick(final PlayerEntity player, final int id) {
        return false;
    }
    
    public Slot getSlot(final int integer) {
        return this.slotList.get(integer);
    }
    
    public ItemStack transferSlot(final PlayerEntity player, final int invSlot) {
        final Slot slot3 = this.slotList.get(invSlot);
        if (slot3 != null) {
            return slot3.getStack();
        }
        return ItemStack.EMPTY;
    }
    
    public ItemStack onSlotClick(final int slotId, final int clickData, final SlotActionType actionType, final PlayerEntity playerEntity) {
        ItemStack itemStack5 = ItemStack.EMPTY;
        final PlayerInventory playerInventory6 = playerEntity.inventory;
        if (actionType == SlotActionType.f) {
            final int integer7 = this.quickCraftButton;
            this.quickCraftButton = unpackButtonId(clickData);
            if ((integer7 != 1 || this.quickCraftButton != 2) && integer7 != this.quickCraftButton) {
                this.endQuickCraft();
            }
            else if (playerInventory6.getCursorStack().isEmpty()) {
                this.endQuickCraft();
            }
            else if (this.quickCraftButton == 0) {
                this.quickCraftStage = unpackQuickCraftStage(clickData);
                if (shouldQuickCraftContinue(this.quickCraftStage, playerEntity)) {
                    this.quickCraftButton = 1;
                    this.quickCraftSlots.clear();
                }
                else {
                    this.endQuickCraft();
                }
            }
            else if (this.quickCraftButton == 1) {
                final Slot slot8 = this.slotList.get(slotId);
                final ItemStack itemStack6 = playerInventory6.getCursorStack();
                if (slot8 != null && canInsertItemIntoSlot(slot8, itemStack6, true) && slot8.canInsert(itemStack6) && (this.quickCraftStage == 2 || itemStack6.getAmount() > this.quickCraftSlots.size()) && this.canInsertIntoSlot(slot8)) {
                    this.quickCraftSlots.add(slot8);
                }
            }
            else if (this.quickCraftButton == 2) {
                if (!this.quickCraftSlots.isEmpty()) {
                    final ItemStack itemStack7 = playerInventory6.getCursorStack().copy();
                    int integer8 = playerInventory6.getCursorStack().getAmount();
                    for (final Slot slot9 : this.quickCraftSlots) {
                        final ItemStack itemStack8 = playerInventory6.getCursorStack();
                        if (slot9 != null && canInsertItemIntoSlot(slot9, itemStack8, true) && slot9.canInsert(itemStack8) && (this.quickCraftStage == 2 || itemStack8.getAmount() >= this.quickCraftSlots.size()) && this.canInsertIntoSlot(slot9)) {
                            final ItemStack itemStack9 = itemStack7.copy();
                            final int integer9 = slot9.hasStack() ? slot9.getStack().getAmount() : 0;
                            calculateStackSize(this.quickCraftSlots, this.quickCraftStage, itemStack9, integer9);
                            final int integer10 = Math.min(itemStack9.getMaxAmount(), slot9.getMaxStackAmount(itemStack9));
                            if (itemStack9.getAmount() > integer10) {
                                itemStack9.setAmount(integer10);
                            }
                            integer8 -= itemStack9.getAmount() - integer9;
                            slot9.setStack(itemStack9);
                        }
                    }
                    itemStack7.setAmount(integer8);
                    playerInventory6.setCursorStack(itemStack7);
                }
                this.endQuickCraft();
            }
            else {
                this.endQuickCraft();
            }
        }
        else if (this.quickCraftButton != 0) {
            this.endQuickCraft();
        }
        else if ((actionType == SlotActionType.a || actionType == SlotActionType.b) && (clickData == 0 || clickData == 1)) {
            if (slotId == -999) {
                if (!playerInventory6.getCursorStack().isEmpty()) {
                    if (clickData == 0) {
                        playerEntity.dropItem(playerInventory6.getCursorStack(), true);
                        playerInventory6.setCursorStack(ItemStack.EMPTY);
                    }
                    if (clickData == 1) {
                        playerEntity.dropItem(playerInventory6.getCursorStack().split(1), true);
                    }
                }
            }
            else if (actionType == SlotActionType.b) {
                if (slotId < 0) {
                    return ItemStack.EMPTY;
                }
                final Slot slot10 = this.slotList.get(slotId);
                if (slot10 == null || !slot10.canTakeItems(playerEntity)) {
                    return ItemStack.EMPTY;
                }
                for (ItemStack itemStack7 = this.transferSlot(playerEntity, slotId); !itemStack7.isEmpty() && ItemStack.areEqualIgnoreTags(slot10.getStack(), itemStack7); itemStack7 = this.transferSlot(playerEntity, slotId)) {
                    itemStack5 = itemStack7.copy();
                }
            }
            else {
                if (slotId < 0) {
                    return ItemStack.EMPTY;
                }
                final Slot slot10 = this.slotList.get(slotId);
                if (slot10 != null) {
                    ItemStack itemStack7 = slot10.getStack();
                    final ItemStack itemStack6 = playerInventory6.getCursorStack();
                    if (!itemStack7.isEmpty()) {
                        itemStack5 = itemStack7.copy();
                    }
                    if (itemStack7.isEmpty()) {
                        if (!itemStack6.isEmpty() && slot10.canInsert(itemStack6)) {
                            int integer11 = (clickData == 0) ? itemStack6.getAmount() : 1;
                            if (integer11 > slot10.getMaxStackAmount(itemStack6)) {
                                integer11 = slot10.getMaxStackAmount(itemStack6);
                            }
                            slot10.setStack(itemStack6.split(integer11));
                        }
                    }
                    else if (slot10.canTakeItems(playerEntity)) {
                        if (itemStack6.isEmpty()) {
                            if (itemStack7.isEmpty()) {
                                slot10.setStack(ItemStack.EMPTY);
                                playerInventory6.setCursorStack(ItemStack.EMPTY);
                            }
                            else {
                                final int integer11 = (clickData == 0) ? itemStack7.getAmount() : ((itemStack7.getAmount() + 1) / 2);
                                playerInventory6.setCursorStack(slot10.takeStack(integer11));
                                if (itemStack7.isEmpty()) {
                                    slot10.setStack(ItemStack.EMPTY);
                                }
                                slot10.onTakeItem(playerEntity, playerInventory6.getCursorStack());
                            }
                        }
                        else if (slot10.canInsert(itemStack6)) {
                            if (canStacksCombine(itemStack7, itemStack6)) {
                                int integer11 = (clickData == 0) ? itemStack6.getAmount() : 1;
                                if (integer11 > slot10.getMaxStackAmount(itemStack6) - itemStack7.getAmount()) {
                                    integer11 = slot10.getMaxStackAmount(itemStack6) - itemStack7.getAmount();
                                }
                                if (integer11 > itemStack6.getMaxAmount() - itemStack7.getAmount()) {
                                    integer11 = itemStack6.getMaxAmount() - itemStack7.getAmount();
                                }
                                itemStack6.subtractAmount(integer11);
                                itemStack7.addAmount(integer11);
                            }
                            else if (itemStack6.getAmount() <= slot10.getMaxStackAmount(itemStack6)) {
                                slot10.setStack(itemStack6);
                                playerInventory6.setCursorStack(itemStack7);
                            }
                        }
                        else if (itemStack6.getMaxAmount() > 1 && canStacksCombine(itemStack7, itemStack6) && !itemStack7.isEmpty()) {
                            final int integer11 = itemStack7.getAmount();
                            if (integer11 + itemStack6.getAmount() <= itemStack6.getMaxAmount()) {
                                itemStack6.addAmount(integer11);
                                itemStack7 = slot10.takeStack(integer11);
                                if (itemStack7.isEmpty()) {
                                    slot10.setStack(ItemStack.EMPTY);
                                }
                                slot10.onTakeItem(playerEntity, playerInventory6.getCursorStack());
                            }
                        }
                    }
                    slot10.markDirty();
                }
            }
        }
        else if (actionType == SlotActionType.c && clickData >= 0 && clickData < 9) {
            final Slot slot10 = this.slotList.get(slotId);
            final ItemStack itemStack7 = playerInventory6.getInvStack(clickData);
            final ItemStack itemStack6 = slot10.getStack();
            if (!itemStack7.isEmpty() || !itemStack6.isEmpty()) {
                if (itemStack7.isEmpty()) {
                    if (slot10.canTakeItems(playerEntity)) {
                        playerInventory6.setInvStack(clickData, itemStack6);
                        slot10.onTake(itemStack6.getAmount());
                        slot10.setStack(ItemStack.EMPTY);
                        slot10.onTakeItem(playerEntity, itemStack6);
                    }
                }
                else if (itemStack6.isEmpty()) {
                    if (slot10.canInsert(itemStack7)) {
                        final int integer11 = slot10.getMaxStackAmount(itemStack7);
                        if (itemStack7.getAmount() > integer11) {
                            slot10.setStack(itemStack7.split(integer11));
                        }
                        else {
                            slot10.setStack(itemStack7);
                            playerInventory6.setInvStack(clickData, ItemStack.EMPTY);
                        }
                    }
                }
                else if (slot10.canTakeItems(playerEntity) && slot10.canInsert(itemStack7)) {
                    final int integer11 = slot10.getMaxStackAmount(itemStack7);
                    if (itemStack7.getAmount() > integer11) {
                        slot10.setStack(itemStack7.split(integer11));
                        slot10.onTakeItem(playerEntity, itemStack6);
                        if (!playerInventory6.insertStack(itemStack6)) {
                            playerEntity.dropItem(itemStack6, true);
                        }
                    }
                    else {
                        slot10.setStack(itemStack7);
                        playerInventory6.setInvStack(clickData, itemStack6);
                        slot10.onTakeItem(playerEntity, itemStack6);
                    }
                }
            }
        }
        else if (actionType == SlotActionType.d && playerEntity.abilities.creativeMode && playerInventory6.getCursorStack().isEmpty() && slotId >= 0) {
            final Slot slot10 = this.slotList.get(slotId);
            if (slot10 != null && slot10.hasStack()) {
                final ItemStack itemStack7 = slot10.getStack().copy();
                itemStack7.setAmount(itemStack7.getMaxAmount());
                playerInventory6.setCursorStack(itemStack7);
            }
        }
        else if (actionType == SlotActionType.e && playerInventory6.getCursorStack().isEmpty() && slotId >= 0) {
            final Slot slot10 = this.slotList.get(slotId);
            if (slot10 != null && slot10.hasStack() && slot10.canTakeItems(playerEntity)) {
                final ItemStack itemStack7 = slot10.takeStack((clickData == 0) ? 1 : slot10.getStack().getAmount());
                slot10.onTakeItem(playerEntity, itemStack7);
                playerEntity.dropItem(itemStack7, true);
            }
        }
        else if (actionType == SlotActionType.g && slotId >= 0) {
            final Slot slot10 = this.slotList.get(slotId);
            final ItemStack itemStack7 = playerInventory6.getCursorStack();
            if (!itemStack7.isEmpty() && (slot10 == null || !slot10.hasStack() || !slot10.canTakeItems(playerEntity))) {
                final int integer8 = (clickData == 0) ? 0 : (this.slotList.size() - 1);
                final int integer11 = (clickData == 0) ? 1 : -1;
                for (int integer12 = 0; integer12 < 2; ++integer12) {
                    for (int integer13 = integer8; integer13 >= 0 && integer13 < this.slotList.size() && itemStack7.getAmount() < itemStack7.getMaxAmount(); integer13 += integer11) {
                        final Slot slot11 = this.slotList.get(integer13);
                        if (slot11.hasStack() && canInsertItemIntoSlot(slot11, itemStack7, true) && slot11.canTakeItems(playerEntity) && this.canInsertIntoSlot(itemStack7, slot11)) {
                            final ItemStack itemStack10 = slot11.getStack();
                            if (integer12 != 0 || itemStack10.getAmount() != itemStack10.getMaxAmount()) {
                                final int integer10 = Math.min(itemStack7.getMaxAmount() - itemStack7.getAmount(), itemStack10.getAmount());
                                final ItemStack itemStack11 = slot11.takeStack(integer10);
                                itemStack7.addAmount(integer10);
                                if (itemStack11.isEmpty()) {
                                    slot11.setStack(ItemStack.EMPTY);
                                }
                                slot11.onTakeItem(playerEntity, itemStack11);
                            }
                        }
                    }
                }
            }
            this.sendContentUpdates();
        }
        return itemStack5;
    }
    
    public static boolean canStacksCombine(final ItemStack itemStack1, final ItemStack itemStack2) {
        return itemStack1.getItem() == itemStack2.getItem() && ItemStack.areTagsEqual(itemStack1, itemStack2);
    }
    
    public boolean canInsertIntoSlot(final ItemStack stack, final Slot slot) {
        return true;
    }
    
    public void close(final PlayerEntity player) {
        final PlayerInventory playerInventory2 = player.inventory;
        if (!playerInventory2.getCursorStack().isEmpty()) {
            player.dropItem(playerInventory2.getCursorStack(), false);
            playerInventory2.setCursorStack(ItemStack.EMPTY);
        }
    }
    
    protected void dropInventory(final PlayerEntity playerEntity, final World world, final Inventory inventory) {
        if (!playerEntity.isAlive() || (playerEntity instanceof ServerPlayerEntity && ((ServerPlayerEntity)playerEntity).o())) {
            for (int integer4 = 0; integer4 < inventory.getInvSize(); ++integer4) {
                playerEntity.dropItem(inventory.removeInvStack(integer4), false);
            }
            return;
        }
        for (int integer4 = 0; integer4 < inventory.getInvSize(); ++integer4) {
            playerEntity.inventory.offerOrDrop(world, inventory.removeInvStack(integer4));
        }
    }
    
    public void onContentChanged(final Inventory inventory) {
        this.sendContentUpdates();
    }
    
    public void setStackInSlot(final int slot, final ItemStack itemStack) {
        this.getSlot(slot).setStack(itemStack);
    }
    
    @Environment(EnvType.CLIENT)
    public void updateSlotStacks(final List<ItemStack> stacks) {
        for (int integer2 = 0; integer2 < stacks.size(); ++integer2) {
            this.getSlot(integer2).setStack(stacks.get(integer2));
        }
    }
    
    public void setProperties(final int pos, final int propertyId) {
        this.properties.get(pos).set(propertyId);
    }
    
    @Environment(EnvType.CLIENT)
    public short getNextActionId(final PlayerInventory playerInventory) {
        return (short)(++this.actionId);
    }
    
    public boolean isRestricted(final PlayerEntity playerEntity) {
        return !this.restrictedPlayers.contains(playerEntity);
    }
    
    public void setPlayerRestriction(final PlayerEntity playerEntity, final boolean unrestricted) {
        if (unrestricted) {
            this.restrictedPlayers.remove(playerEntity);
        }
        else {
            this.restrictedPlayers.add(playerEntity);
        }
    }
    
    public abstract boolean canUse(final PlayerEntity arg1);
    
    protected boolean insertItem(final ItemStack stack, final int startIndex, final int endIndex, final boolean fromLast) {
        boolean boolean5 = false;
        int integer6 = startIndex;
        if (fromLast) {
            integer6 = endIndex - 1;
        }
        if (stack.canStack()) {
            while (!stack.isEmpty()) {
                if (fromLast) {
                    if (integer6 < startIndex) {
                        break;
                    }
                }
                else if (integer6 >= endIndex) {
                    break;
                }
                final Slot slot7 = this.slotList.get(integer6);
                final ItemStack itemStack8 = slot7.getStack();
                if (!itemStack8.isEmpty() && canStacksCombine(stack, itemStack8)) {
                    final int integer7 = itemStack8.getAmount() + stack.getAmount();
                    if (integer7 <= stack.getMaxAmount()) {
                        stack.setAmount(0);
                        itemStack8.setAmount(integer7);
                        slot7.markDirty();
                        boolean5 = true;
                    }
                    else if (itemStack8.getAmount() < stack.getMaxAmount()) {
                        stack.subtractAmount(stack.getMaxAmount() - itemStack8.getAmount());
                        itemStack8.setAmount(stack.getMaxAmount());
                        slot7.markDirty();
                        boolean5 = true;
                    }
                }
                if (fromLast) {
                    --integer6;
                }
                else {
                    ++integer6;
                }
            }
        }
        if (!stack.isEmpty()) {
            if (fromLast) {
                integer6 = endIndex - 1;
            }
            else {
                integer6 = startIndex;
            }
            while (true) {
                if (fromLast) {
                    if (integer6 < startIndex) {
                        break;
                    }
                }
                else if (integer6 >= endIndex) {
                    break;
                }
                final Slot slot7 = this.slotList.get(integer6);
                final ItemStack itemStack8 = slot7.getStack();
                if (itemStack8.isEmpty() && slot7.canInsert(stack)) {
                    if (stack.getAmount() > slot7.getMaxStackAmount()) {
                        slot7.setStack(stack.split(slot7.getMaxStackAmount()));
                    }
                    else {
                        slot7.setStack(stack.split(stack.getAmount()));
                    }
                    slot7.markDirty();
                    boolean5 = true;
                    break;
                }
                if (fromLast) {
                    --integer6;
                }
                else {
                    ++integer6;
                }
            }
        }
        return boolean5;
    }
    
    public static int unpackQuickCraftStage(final int clickData) {
        return clickData >> 2 & 0x3;
    }
    
    public static int unpackButtonId(final int clickData) {
        return clickData & 0x3;
    }
    
    @Environment(EnvType.CLIENT)
    public static int packClickData(final int buttonId, final int quickCraftStage) {
        return (buttonId & 0x3) | (quickCraftStage & 0x3) << 2;
    }
    
    public static boolean shouldQuickCraftContinue(final int integer, final PlayerEntity playerEntity) {
        return integer == 0 || integer == 1 || (integer == 2 && playerEntity.abilities.creativeMode);
    }
    
    protected void endQuickCraft() {
        this.quickCraftButton = 0;
        this.quickCraftSlots.clear();
    }
    
    public static boolean canInsertItemIntoSlot(@Nullable final Slot slot, final ItemStack stack, final boolean boolean3) {
        final boolean boolean4 = slot == null || !slot.hasStack();
        if (!boolean4 && stack.isEqualIgnoreTags(slot.getStack()) && ItemStack.areTagsEqual(slot.getStack(), stack)) {
            return slot.getStack().getAmount() + (boolean3 ? 0 : stack.getAmount()) <= stack.getMaxAmount();
        }
        return boolean4;
    }
    
    public static void calculateStackSize(final Set<Slot> slots, final int rmode, final ItemStack stack, final int stackSize) {
        switch (rmode) {
            case 0: {
                stack.setAmount(MathHelper.floor(stack.getAmount() / (float)slots.size()));
                break;
            }
            case 1: {
                stack.setAmount(1);
                break;
            }
            case 2: {
                stack.setAmount(stack.getItem().getMaxAmount());
                break;
            }
        }
        stack.addAmount(stackSize);
    }
    
    public boolean canInsertIntoSlot(final Slot slot) {
        return true;
    }
    
    public static int calculateComparatorOutput(@Nullable final BlockEntity entity) {
        if (entity instanceof Inventory) {
            return calculateComparatorOutput((Inventory)entity);
        }
        return 0;
    }
    
    public static int calculateComparatorOutput(@Nullable final Inventory inventory) {
        if (inventory == null) {
            return 0;
        }
        int integer2 = 0;
        float float3 = 0.0f;
        for (int integer3 = 0; integer3 < inventory.getInvSize(); ++integer3) {
            final ItemStack itemStack5 = inventory.getInvStack(integer3);
            if (!itemStack5.isEmpty()) {
                float3 += itemStack5.getAmount() / (float)Math.min(inventory.getInvMaxStackAmount(), itemStack5.getMaxAmount());
                ++integer2;
            }
        }
        float3 /= inventory.getInvSize();
        return MathHelper.floor(float3 * 14.0f) + ((integer2 > 0) ? 1 : 0);
    }
}

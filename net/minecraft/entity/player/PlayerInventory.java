package net.minecraft.entity.player;

import java.util.AbstractList;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.tag.Tag;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.Inventories;
import net.minecraft.network.Packet;
import net.minecraft.client.network.packet.GuiSlotUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import java.util.Iterator;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemProvider;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import com.google.common.collect.ImmutableList;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Nameable;
import net.minecraft.inventory.Inventory;

public class PlayerInventory implements Inventory, Nameable
{
    public final DefaultedList<ItemStack> main;
    public final DefaultedList<ItemStack> armor;
    public final DefaultedList<ItemStack> offHand;
    private final List<DefaultedList<ItemStack>> combinedInventory;
    public int selectedSlot;
    public final PlayerEntity player;
    private ItemStack cursorStack;
    private int changeCount;
    
    public PlayerInventory(final PlayerEntity playerEntity) {
        this.main = DefaultedList.<ItemStack>create(36, ItemStack.EMPTY);
        this.armor = DefaultedList.<ItemStack>create(4, ItemStack.EMPTY);
        this.offHand = DefaultedList.<ItemStack>create(1, ItemStack.EMPTY);
        this.combinedInventory = ImmutableList.<DefaultedList<ItemStack>>of(this.main, this.armor, this.offHand);
        this.cursorStack = ItemStack.EMPTY;
        this.player = playerEntity;
    }
    
    public ItemStack getMainHandStack() {
        if (isValidHotbarIndex(this.selectedSlot)) {
            return this.main.get(this.selectedSlot);
        }
        return ItemStack.EMPTY;
    }
    
    public static int getHotbarSize() {
        return 9;
    }
    
    private boolean canStackAddMore(final ItemStack existingStack, final ItemStack itemStack2) {
        return !existingStack.isEmpty() && this.areItemsEqual(existingStack, itemStack2) && existingStack.canStack() && existingStack.getAmount() < existingStack.getMaxAmount() && existingStack.getAmount() < this.getInvMaxStackAmount();
    }
    
    private boolean areItemsEqual(final ItemStack stack1, final ItemStack itemStack2) {
        return stack1.getItem() == itemStack2.getItem() && ItemStack.areTagsEqual(stack1, itemStack2);
    }
    
    public int getEmptySlot() {
        for (int integer1 = 0; integer1 < this.main.size(); ++integer1) {
            if (this.main.get(integer1).isEmpty()) {
                return integer1;
            }
        }
        return -1;
    }
    
    @Environment(EnvType.CLIENT)
    public void addPickBlock(final ItemStack itemStack) {
        final int integer2 = this.getSlotWithStack(itemStack);
        if (isValidHotbarIndex(integer2)) {
            this.selectedSlot = integer2;
            return;
        }
        if (integer2 == -1) {
            this.selectedSlot = this.getSwappableHotbarSlot();
            if (!this.main.get(this.selectedSlot).isEmpty()) {
                final int integer3 = this.getEmptySlot();
                if (integer3 != -1) {
                    this.main.set(integer3, this.main.get(this.selectedSlot));
                }
            }
            this.main.set(this.selectedSlot, itemStack);
        }
        else {
            this.swapSlotWithHotbar(integer2);
        }
    }
    
    public void swapSlotWithHotbar(final int integer) {
        this.selectedSlot = this.getSwappableHotbarSlot();
        final ItemStack itemStack2 = this.main.get(this.selectedSlot);
        this.main.set(this.selectedSlot, this.main.get(integer));
        this.main.set(integer, itemStack2);
    }
    
    public static boolean isValidHotbarIndex(final int slot) {
        return slot >= 0 && slot < 9;
    }
    
    @Environment(EnvType.CLIENT)
    public int getSlotWithStack(final ItemStack itemStack) {
        for (int integer2 = 0; integer2 < this.main.size(); ++integer2) {
            if (!this.main.get(integer2).isEmpty() && this.areItemsEqual(itemStack, this.main.get(integer2))) {
                return integer2;
            }
        }
        return -1;
    }
    
    public int c(final ItemStack itemStack) {
        for (int integer2 = 0; integer2 < this.main.size(); ++integer2) {
            final ItemStack itemStack2 = this.main.get(integer2);
            if (!this.main.get(integer2).isEmpty() && this.areItemsEqual(itemStack, this.main.get(integer2)) && !this.main.get(integer2).isDamaged() && !itemStack2.hasEnchantments() && !itemStack2.hasDisplayName()) {
                return integer2;
            }
        }
        return -1;
    }
    
    public int getSwappableHotbarSlot() {
        for (int integer1 = 0; integer1 < 9; ++integer1) {
            final int integer2 = (this.selectedSlot + integer1) % 9;
            if (this.main.get(integer2).isEmpty()) {
                return integer2;
            }
        }
        for (int integer1 = 0; integer1 < 9; ++integer1) {
            final int integer2 = (this.selectedSlot + integer1) % 9;
            if (!this.main.get(integer2).hasEnchantments()) {
                return integer2;
            }
        }
        return this.selectedSlot;
    }
    
    @Environment(EnvType.CLIENT)
    public void a(double double1) {
        if (double1 > 0.0) {
            double1 = 1.0;
        }
        if (double1 < 0.0) {
            double1 = -1.0;
        }
        this.selectedSlot -= (int)double1;
        while (this.selectedSlot < 0) {
            this.selectedSlot += 9;
        }
        while (this.selectedSlot >= 9) {
            this.selectedSlot -= 9;
        }
    }
    
    public int a(final Predicate<ItemStack> predicate, final int integer) {
        int integer2 = 0;
        for (int integer3 = 0; integer3 < this.getInvSize(); ++integer3) {
            final ItemStack itemStack5 = this.getInvStack(integer3);
            if (!itemStack5.isEmpty()) {
                if (predicate.test(itemStack5)) {
                    final int integer4 = (integer <= 0) ? itemStack5.getAmount() : Math.min(integer - integer2, itemStack5.getAmount());
                    integer2 += integer4;
                    if (integer != 0) {
                        itemStack5.subtractAmount(integer4);
                        if (itemStack5.isEmpty()) {
                            this.setInvStack(integer3, ItemStack.EMPTY);
                        }
                        if (integer > 0 && integer2 >= integer) {
                            return integer2;
                        }
                    }
                }
            }
        }
        if (!this.cursorStack.isEmpty() && predicate.test(this.cursorStack)) {
            final int integer3 = (integer <= 0) ? this.cursorStack.getAmount() : Math.min(integer - integer2, this.cursorStack.getAmount());
            integer2 += integer3;
            if (integer != 0) {
                this.cursorStack.subtractAmount(integer3);
                if (this.cursorStack.isEmpty()) {
                    this.cursorStack = ItemStack.EMPTY;
                }
                if (integer > 0 && integer2 >= integer) {
                    return integer2;
                }
            }
        }
        return integer2;
    }
    
    private int addStack(final ItemStack itemStack) {
        int integer2 = this.getOccupiedSlotWithRoomForStack(itemStack);
        if (integer2 == -1) {
            integer2 = this.getEmptySlot();
        }
        if (integer2 == -1) {
            return itemStack.getAmount();
        }
        return this.d(integer2, itemStack);
    }
    
    private int d(final int integer, final ItemStack itemStack) {
        final Item item3 = itemStack.getItem();
        int integer2 = itemStack.getAmount();
        ItemStack itemStack2 = this.getInvStack(integer);
        if (itemStack2.isEmpty()) {
            itemStack2 = new ItemStack(item3, 0);
            if (itemStack.hasTag()) {
                itemStack2.setTag(itemStack.getTag().copy());
            }
            this.setInvStack(integer, itemStack2);
        }
        int integer3 = integer2;
        if (integer3 > itemStack2.getMaxAmount() - itemStack2.getAmount()) {
            integer3 = itemStack2.getMaxAmount() - itemStack2.getAmount();
        }
        if (integer3 > this.getInvMaxStackAmount() - itemStack2.getAmount()) {
            integer3 = this.getInvMaxStackAmount() - itemStack2.getAmount();
        }
        if (integer3 == 0) {
            return integer2;
        }
        integer2 -= integer3;
        itemStack2.addAmount(integer3);
        itemStack2.setUpdateCooldown(5);
        return integer2;
    }
    
    public int getOccupiedSlotWithRoomForStack(final ItemStack itemStack) {
        if (this.canStackAddMore(this.getInvStack(this.selectedSlot), itemStack)) {
            return this.selectedSlot;
        }
        if (this.canStackAddMore(this.getInvStack(40), itemStack)) {
            return 40;
        }
        for (int integer2 = 0; integer2 < this.main.size(); ++integer2) {
            if (this.canStackAddMore(this.main.get(integer2), itemStack)) {
                return integer2;
            }
        }
        return -1;
    }
    
    public void updateItems() {
        for (final DefaultedList<ItemStack> defaultedList2 : this.combinedInventory) {
            for (int integer3 = 0; integer3 < defaultedList2.size(); ++integer3) {
                if (!defaultedList2.get(integer3).isEmpty()) {
                    defaultedList2.get(integer3).update(this.player.world, this.player, integer3, this.selectedSlot == integer3);
                }
            }
        }
    }
    
    public boolean insertStack(final ItemStack itemStack) {
        return this.insertStack(-1, itemStack);
    }
    
    public boolean insertStack(int slot, final ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return false;
        }
        try {
            if (!itemStack.isDamaged()) {
                int integer3;
                do {
                    integer3 = itemStack.getAmount();
                    if (slot == -1) {
                        itemStack.setAmount(this.addStack(itemStack));
                    }
                    else {
                        itemStack.setAmount(this.d(slot, itemStack));
                    }
                } while (!itemStack.isEmpty() && itemStack.getAmount() < integer3);
                if (itemStack.getAmount() == integer3 && this.player.abilities.creativeMode) {
                    itemStack.setAmount(0);
                    return true;
                }
                return itemStack.getAmount() < integer3;
            }
            else {
                if (slot == -1) {
                    slot = this.getEmptySlot();
                }
                if (slot >= 0) {
                    this.main.set(slot, itemStack.copy());
                    this.main.get(slot).setUpdateCooldown(5);
                    itemStack.setAmount(0);
                    return true;
                }
                if (this.player.abilities.creativeMode) {
                    itemStack.setAmount(0);
                    return true;
                }
                return false;
            }
        }
        catch (Throwable throwable3) {
            final CrashReport crashReport4 = CrashReport.create(throwable3, "Adding item to inventory");
            final CrashReportSection crashReportSection5 = crashReport4.addElement("Item being added");
            crashReportSection5.add("Item ID", Item.getRawIdByItem(itemStack.getItem()));
            crashReportSection5.add("Item data", itemStack.getDamage());
            crashReportSection5.add("Item name", () -> itemStack.getDisplayName().getString());
            throw new CrashException(crashReport4);
        }
    }
    
    public void offerOrDrop(final World world, final ItemStack itemStack) {
        if (world.isClient) {
            return;
        }
        while (!itemStack.isEmpty()) {
            int integer3 = this.getOccupiedSlotWithRoomForStack(itemStack);
            if (integer3 == -1) {
                integer3 = this.getEmptySlot();
            }
            if (integer3 == -1) {
                this.player.dropItem(itemStack, false);
                break;
            }
            final int integer4 = itemStack.getMaxAmount() - this.getInvStack(integer3).getAmount();
            if (!this.insertStack(integer3, itemStack.split(integer4))) {
                continue;
            }
            ((ServerPlayerEntity)this.player).networkHandler.sendPacket(new GuiSlotUpdateS2CPacket(-2, integer3, this.getInvStack(integer3)));
        }
    }
    
    @Override
    public ItemStack takeInvStack(int slot, final int integer2) {
        List<ItemStack> list3 = null;
        for (final DefaultedList<ItemStack> defaultedList5 : this.combinedInventory) {
            if (slot < defaultedList5.size()) {
                list3 = defaultedList5;
                break;
            }
            slot -= defaultedList5.size();
        }
        if (list3 != null && !list3.get(slot).isEmpty()) {
            return Inventories.splitStack(list3, slot, integer2);
        }
        return ItemStack.EMPTY;
    }
    
    public void removeOne(final ItemStack itemStack) {
        for (final DefaultedList<ItemStack> defaultedList3 : this.combinedInventory) {
            for (int integer4 = 0; integer4 < defaultedList3.size(); ++integer4) {
                if (defaultedList3.get(integer4) == itemStack) {
                    defaultedList3.set(integer4, ItemStack.EMPTY);
                    break;
                }
            }
        }
    }
    
    @Override
    public ItemStack removeInvStack(int slot) {
        DefaultedList<ItemStack> defaultedList2 = null;
        for (final DefaultedList<ItemStack> defaultedList3 : this.combinedInventory) {
            if (slot < defaultedList3.size()) {
                defaultedList2 = defaultedList3;
                break;
            }
            slot -= defaultedList3.size();
        }
        if (defaultedList2 != null && !defaultedList2.get(slot).isEmpty()) {
            final ItemStack itemStack3 = defaultedList2.get(slot);
            defaultedList2.set(slot, ItemStack.EMPTY);
            return itemStack3;
        }
        return ItemStack.EMPTY;
    }
    
    @Override
    public void setInvStack(int slot, final ItemStack itemStack) {
        DefaultedList<ItemStack> defaultedList3 = null;
        for (final DefaultedList<ItemStack> defaultedList4 : this.combinedInventory) {
            if (slot < defaultedList4.size()) {
                defaultedList3 = defaultedList4;
                break;
            }
            slot -= defaultedList4.size();
        }
        if (defaultedList3 != null) {
            defaultedList3.set(slot, itemStack);
        }
    }
    
    public float getBlockBreakingSpeed(final BlockState blockState) {
        return this.main.get(this.selectedSlot).getBlockBreakingSpeed(blockState);
    }
    
    public ListTag serialize(final ListTag listTag) {
        for (int integer2 = 0; integer2 < this.main.size(); ++integer2) {
            if (!this.main.get(integer2).isEmpty()) {
                final CompoundTag compoundTag3 = new CompoundTag();
                compoundTag3.putByte("Slot", (byte)integer2);
                this.main.get(integer2).toTag(compoundTag3);
                ((AbstractList<CompoundTag>)listTag).add(compoundTag3);
            }
        }
        for (int integer2 = 0; integer2 < this.armor.size(); ++integer2) {
            if (!this.armor.get(integer2).isEmpty()) {
                final CompoundTag compoundTag3 = new CompoundTag();
                compoundTag3.putByte("Slot", (byte)(integer2 + 100));
                this.armor.get(integer2).toTag(compoundTag3);
                ((AbstractList<CompoundTag>)listTag).add(compoundTag3);
            }
        }
        for (int integer2 = 0; integer2 < this.offHand.size(); ++integer2) {
            if (!this.offHand.get(integer2).isEmpty()) {
                final CompoundTag compoundTag3 = new CompoundTag();
                compoundTag3.putByte("Slot", (byte)(integer2 + 150));
                this.offHand.get(integer2).toTag(compoundTag3);
                ((AbstractList<CompoundTag>)listTag).add(compoundTag3);
            }
        }
        return listTag;
    }
    
    public void deserialize(final ListTag listTag) {
        this.main.clear();
        this.armor.clear();
        this.offHand.clear();
        for (int integer2 = 0; integer2 < listTag.size(); ++integer2) {
            final CompoundTag compoundTag3 = listTag.getCompoundTag(integer2);
            final int integer3 = compoundTag3.getByte("Slot") & 0xFF;
            final ItemStack itemStack5 = ItemStack.fromTag(compoundTag3);
            if (!itemStack5.isEmpty()) {
                if (integer3 >= 0 && integer3 < this.main.size()) {
                    this.main.set(integer3, itemStack5);
                }
                else if (integer3 >= 100 && integer3 < this.armor.size() + 100) {
                    this.armor.set(integer3 - 100, itemStack5);
                }
                else if (integer3 >= 150 && integer3 < this.offHand.size() + 150) {
                    this.offHand.set(integer3 - 150, itemStack5);
                }
            }
        }
    }
    
    @Override
    public int getInvSize() {
        return this.main.size() + this.armor.size() + this.offHand.size();
    }
    
    @Override
    public boolean isInvEmpty() {
        for (final ItemStack itemStack2 : this.main) {
            if (!itemStack2.isEmpty()) {
                return false;
            }
        }
        for (final ItemStack itemStack2 : this.armor) {
            if (!itemStack2.isEmpty()) {
                return false;
            }
        }
        for (final ItemStack itemStack2 : this.offHand) {
            if (!itemStack2.isEmpty()) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public ItemStack getInvStack(int slot) {
        List<ItemStack> list2 = null;
        for (final DefaultedList<ItemStack> defaultedList4 : this.combinedInventory) {
            if (slot < defaultedList4.size()) {
                list2 = defaultedList4;
                break;
            }
            slot -= defaultedList4.size();
        }
        return (list2 == null) ? ItemStack.EMPTY : list2.get(slot);
    }
    
    @Override
    public TextComponent getName() {
        return new TranslatableTextComponent("container.inventory", new Object[0]);
    }
    
    public boolean isUsingEffectiveTool(final BlockState blockState) {
        return this.getInvStack(this.selectedSlot).isEffectiveOn(blockState);
    }
    
    @Environment(EnvType.CLIENT)
    public ItemStack getArmorStack(final int integer) {
        return this.armor.get(integer);
    }
    
    public void damageArmor(float armor) {
        if (armor <= 0.0f) {
            return;
        }
        armor /= 4.0f;
        if (armor < 1.0f) {
            armor = 1.0f;
        }
        for (int integer2 = 0; integer2 < this.armor.size(); ++integer2) {
            final ItemStack itemStack3 = this.armor.get(integer2);
            if (itemStack3.getItem() instanceof ArmorItem) {
                final int integer3 = integer2;
                itemStack3.<PlayerEntity>applyDamage((int)armor, this.player, playerEntity -> playerEntity.sendEquipmentBreakStatus(EquipmentSlot.a(EquipmentSlot.Type.ARMOR, integer3)));
            }
        }
    }
    
    public void dropAll() {
        for (final List<ItemStack> list2 : this.combinedInventory) {
            for (int integer3 = 0; integer3 < list2.size(); ++integer3) {
                final ItemStack itemStack4 = list2.get(integer3);
                if (!itemStack4.isEmpty()) {
                    this.player.dropItem(itemStack4, true, false);
                    list2.set(integer3, ItemStack.EMPTY);
                }
            }
        }
    }
    
    @Override
    public void markDirty() {
        ++this.changeCount;
    }
    
    @Environment(EnvType.CLIENT)
    public int getChangeCount() {
        return this.changeCount;
    }
    
    public void setCursorStack(final ItemStack itemStack) {
        this.cursorStack = itemStack;
    }
    
    public ItemStack getCursorStack() {
        return this.cursorStack;
    }
    
    @Override
    public boolean canPlayerUseInv(final PlayerEntity playerEntity) {
        return !this.player.removed && playerEntity.squaredDistanceTo(this.player) <= 64.0;
    }
    
    public boolean contains(final ItemStack stack) {
        for (final List<ItemStack> list3 : this.combinedInventory) {
            for (final ItemStack itemStack5 : list3) {
                if (!itemStack5.isEmpty() && itemStack5.isEqualIgnoreTags(stack)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Environment(EnvType.CLIENT)
    public boolean contains(final Tag<Item> tag) {
        for (final List<ItemStack> list3 : this.combinedInventory) {
            for (final ItemStack itemStack5 : list3) {
                if (!itemStack5.isEmpty() && tag.contains(itemStack5.getItem())) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public void clone(final PlayerInventory playerInventory) {
        for (int integer2 = 0; integer2 < this.getInvSize(); ++integer2) {
            this.setInvStack(integer2, playerInventory.getInvStack(integer2));
        }
        this.selectedSlot = playerInventory.selectedSlot;
    }
    
    @Override
    public void clear() {
        for (final List<ItemStack> list2 : this.combinedInventory) {
            list2.clear();
        }
    }
    
    public void populateRecipeFinder(final RecipeFinder recipeFinder) {
        for (final ItemStack itemStack3 : this.main) {
            recipeFinder.addNormalItem(itemStack3);
        }
    }
}

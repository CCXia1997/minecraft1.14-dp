package net.minecraft.recipe;

import org.apache.logging.log4j.LogManager;
import java.util.List;
import com.google.common.collect.Lists;
import net.minecraft.container.Slot;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import java.util.Iterator;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.item.ItemStack;
import net.minecraft.container.PlayerContainer;
import net.minecraft.container.CraftingTableContainer;
import net.minecraft.network.Packet;
import net.minecraft.client.network.packet.CraftResponseS2CPacket;
import it.unimi.dsi.fastutil.ints.IntList;
import javax.annotation.Nullable;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.container.CraftingContainer;
import net.minecraft.entity.player.PlayerInventory;
import org.apache.logging.log4j.Logger;
import net.minecraft.inventory.Inventory;

public class InputSlotFiller<C extends Inventory> implements RecipeGridAligner<Integer>
{
    protected static final Logger LOGGER;
    protected final RecipeFinder recipeFinder;
    protected PlayerInventory inventory;
    protected CraftingContainer<C> craftingContainer;
    
    public InputSlotFiller(final CraftingContainer<C> craftingContainer) {
        this.recipeFinder = new RecipeFinder();
        this.craftingContainer = craftingContainer;
    }
    
    public void fillInputSlots(final ServerPlayerEntity entity, @Nullable final Recipe<C> recipe, final boolean craftAll) {
        if (recipe == null || !entity.getRecipeBook().contains(recipe)) {
            return;
        }
        this.inventory = entity.inventory;
        if (!this.canReturnInputs() && !entity.isCreative()) {
            return;
        }
        this.recipeFinder.clear();
        entity.inventory.populateRecipeFinder(this.recipeFinder);
        this.craftingContainer.populateRecipeFinder(this.recipeFinder);
        if (this.recipeFinder.findRecipe(recipe, null)) {
            this.fillInputSlots(recipe, craftAll);
        }
        else {
            this.returnInputs();
            entity.networkHandler.sendPacket(new CraftResponseS2CPacket(entity.container.syncId, recipe));
        }
        entity.inventory.markDirty();
    }
    
    protected void returnInputs() {
        for (int integer1 = 0; integer1 < this.craftingContainer.getCraftingWidth() * this.craftingContainer.getCraftingHeight() + 1; ++integer1) {
            if (integer1 == this.craftingContainer.getCraftingResultSlotIndex()) {
                if (this.craftingContainer instanceof CraftingTableContainer) {
                    continue;
                }
                if (this.craftingContainer instanceof PlayerContainer) {
                    continue;
                }
            }
            this.returnSlot(integer1);
        }
        this.craftingContainer.clearCraftingSlots();
    }
    
    protected void returnSlot(final int integer) {
        final ItemStack itemStack2 = this.craftingContainer.getSlot(integer).getStack();
        if (itemStack2.isEmpty()) {
            return;
        }
        while (itemStack2.getAmount() > 0) {
            int integer2 = this.inventory.getOccupiedSlotWithRoomForStack(itemStack2);
            if (integer2 == -1) {
                integer2 = this.inventory.getEmptySlot();
            }
            final ItemStack itemStack3 = itemStack2.copy();
            itemStack3.setAmount(1);
            if (!this.inventory.insertStack(integer2, itemStack3)) {
                InputSlotFiller.LOGGER.error("Can't find any space for item in the inventory");
            }
            this.craftingContainer.getSlot(integer).takeStack(1);
        }
    }
    
    protected void fillInputSlots(final Recipe<C> recipe, final boolean craftAll) {
        final boolean boolean3 = this.craftingContainer.matches(recipe);
        final int integer4 = this.recipeFinder.countRecipeCrafts(recipe, null);
        if (boolean3) {
            for (int integer5 = 0; integer5 < this.craftingContainer.getCraftingHeight() * this.craftingContainer.getCraftingWidth() + 1; ++integer5) {
                if (integer5 != this.craftingContainer.getCraftingResultSlotIndex()) {
                    final ItemStack itemStack6 = this.craftingContainer.getSlot(integer5).getStack();
                    if (!itemStack6.isEmpty() && Math.min(integer4, itemStack6.getMaxAmount()) < itemStack6.getAmount() + 1) {
                        return;
                    }
                }
            }
        }
        int integer5 = this.getAmountToFill(craftAll, integer4, boolean3);
        final IntList intList6 = (IntList)new IntArrayList();
        if (this.recipeFinder.findRecipe(recipe, intList6, integer5)) {
            int integer6 = integer5;
            for (final int integer7 : intList6) {
                final int integer8 = RecipeFinder.getStackFromId(integer7).getMaxAmount();
                if (integer8 < integer6) {
                    integer6 = integer8;
                }
            }
            integer5 = integer6;
            if (this.recipeFinder.findRecipe(recipe, intList6, integer5)) {
                this.returnInputs();
                this.alignRecipeToGrid(this.craftingContainer.getCraftingWidth(), this.craftingContainer.getCraftingHeight(), this.craftingContainer.getCraftingResultSlotIndex(), recipe, (Iterator<Integer>)intList6.iterator(), integer5);
            }
        }
    }
    
    @Override
    public void acceptAlignedInput(final Iterator<Integer> inputs, final int slot, final int amount, final int gridX, final int gridY) {
        final Slot slot2 = this.craftingContainer.getSlot(slot);
        final ItemStack itemStack7 = RecipeFinder.getStackFromId(inputs.next());
        if (!itemStack7.isEmpty()) {
            for (int integer8 = 0; integer8 < amount; ++integer8) {
                this.fillInputSlot(slot2, itemStack7);
            }
        }
    }
    
    protected int getAmountToFill(final boolean craftAll, final int limit, final boolean recipeInCraftingSlots) {
        int integer4 = 1;
        if (craftAll) {
            integer4 = limit;
        }
        else if (recipeInCraftingSlots) {
            integer4 = 64;
            for (int integer5 = 0; integer5 < this.craftingContainer.getCraftingWidth() * this.craftingContainer.getCraftingHeight() + 1; ++integer5) {
                if (integer5 != this.craftingContainer.getCraftingResultSlotIndex()) {
                    final ItemStack itemStack6 = this.craftingContainer.getSlot(integer5).getStack();
                    if (!itemStack6.isEmpty() && integer4 > itemStack6.getAmount()) {
                        integer4 = itemStack6.getAmount();
                    }
                }
            }
            if (integer4 < 64) {
                ++integer4;
            }
        }
        return integer4;
    }
    
    protected void fillInputSlot(final Slot slot, final ItemStack itemStack) {
        final int integer3 = this.inventory.c(itemStack);
        if (integer3 == -1) {
            return;
        }
        final ItemStack itemStack2 = this.inventory.getInvStack(integer3).copy();
        if (itemStack2.isEmpty()) {
            return;
        }
        if (itemStack2.getAmount() > 1) {
            this.inventory.takeInvStack(integer3, 1);
        }
        else {
            this.inventory.removeInvStack(integer3);
        }
        itemStack2.setAmount(1);
        if (slot.getStack().isEmpty()) {
            slot.setStack(itemStack2);
        }
        else {
            slot.getStack().addAmount(1);
        }
    }
    
    private boolean canReturnInputs() {
        final List<ItemStack> list1 = Lists.newArrayList();
        final int integer2 = this.getFreeInventorySlots();
        for (int integer3 = 0; integer3 < this.craftingContainer.getCraftingWidth() * this.craftingContainer.getCraftingHeight() + 1; ++integer3) {
            if (integer3 != this.craftingContainer.getCraftingResultSlotIndex()) {
                final ItemStack itemStack4 = this.craftingContainer.getSlot(integer3).getStack().copy();
                if (!itemStack4.isEmpty()) {
                    final int integer4 = this.inventory.getOccupiedSlotWithRoomForStack(itemStack4);
                    if (integer4 == -1 && list1.size() <= integer2) {
                        for (final ItemStack itemStack5 : list1) {
                            if (itemStack5.isEqualIgnoreTags(itemStack4) && itemStack5.getAmount() != itemStack5.getMaxAmount() && itemStack5.getAmount() + itemStack4.getAmount() <= itemStack5.getMaxAmount()) {
                                itemStack5.addAmount(itemStack4.getAmount());
                                itemStack4.setAmount(0);
                                break;
                            }
                        }
                        if (!itemStack4.isEmpty()) {
                            if (list1.size() >= integer2) {
                                return false;
                            }
                            list1.add(itemStack4);
                        }
                    }
                    else if (integer4 == -1) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    private int getFreeInventorySlots() {
        int integer1 = 0;
        for (final ItemStack itemStack3 : this.inventory.main) {
            if (itemStack3.isEmpty()) {
                ++integer1;
            }
        }
        return integer1;
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}

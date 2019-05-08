package net.minecraft.container;

import net.minecraft.util.math.BlockPos;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.recipe.RecipeFinder;
import java.util.Optional;
import net.minecraft.network.Packet;
import net.minecraft.client.network.packet.GuiSlotUpdateS2CPacket;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.crafting.CraftingRecipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import net.minecraft.inventory.Inventory;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.CraftingInventory;

public class CraftingTableContainer extends CraftingContainer<CraftingInventory>
{
    private final CraftingInventory craftingInv;
    private final CraftingResultInventory resultInv;
    private final BlockContext context;
    private final PlayerEntity player;
    
    public CraftingTableContainer(final int syncId, final PlayerInventory playerInventory) {
        this(syncId, playerInventory, BlockContext.EMPTY);
    }
    
    public CraftingTableContainer(final int syncId, final PlayerInventory playerInventory, final BlockContext blockContext) {
        super(ContainerType.CRAFTING, syncId);
        this.craftingInv = new CraftingInventory(this, 3, 3);
        this.resultInv = new CraftingResultInventory();
        this.context = blockContext;
        this.player = playerInventory.player;
        this.addSlot(new CraftingResultSlot(playerInventory.player, this.craftingInv, this.resultInv, 0, 124, 35));
        for (int integer4 = 0; integer4 < 3; ++integer4) {
            for (int integer5 = 0; integer5 < 3; ++integer5) {
                this.addSlot(new Slot(this.craftingInv, integer5 + integer4 * 3, 30 + integer5 * 18, 17 + integer4 * 18));
            }
        }
        for (int integer4 = 0; integer4 < 3; ++integer4) {
            for (int integer5 = 0; integer5 < 9; ++integer5) {
                this.addSlot(new Slot(playerInventory, integer5 + integer4 * 9 + 9, 8 + integer5 * 18, 84 + integer4 * 18));
            }
        }
        for (int integer4 = 0; integer4 < 9; ++integer4) {
            this.addSlot(new Slot(playerInventory, integer4, 8 + integer4 * 18, 142));
        }
    }
    
    protected static void updateResult(final int syncId, final World world, final PlayerEntity player, final CraftingInventory craftingInventory, final CraftingResultInventory resultInventory) {
        if (world.isClient) {
            return;
        }
        final ServerPlayerEntity serverPlayerEntity6 = (ServerPlayerEntity)player;
        ItemStack itemStack7 = ItemStack.EMPTY;
        final Optional<CraftingRecipe> optional8 = world.getServer().getRecipeManager().<CraftingInventory, CraftingRecipe>getFirstMatch(RecipeType.CRAFTING, craftingInventory, world);
        if (optional8.isPresent()) {
            final CraftingRecipe craftingRecipe9 = optional8.get();
            if (resultInventory.shouldCraftRecipe(world, serverPlayerEntity6, craftingRecipe9)) {
                itemStack7 = craftingRecipe9.craft(craftingInventory);
            }
        }
        resultInventory.setInvStack(0, itemStack7);
        serverPlayerEntity6.networkHandler.sendPacket(new GuiSlotUpdateS2CPacket(syncId, 0, itemStack7));
    }
    
    @Override
    public void onContentChanged(final Inventory inventory) {
        this.context.run((world, blockPos) -> updateResult(this.syncId, world, this.player, this.craftingInv, this.resultInv));
    }
    
    @Override
    public void populateRecipeFinder(final RecipeFinder recipeFinder) {
        this.craftingInv.provideRecipeInputs(recipeFinder);
    }
    
    @Override
    public void clearCraftingSlots() {
        this.craftingInv.clear();
        this.resultInv.clear();
    }
    
    @Override
    public boolean matches(final Recipe<? super CraftingInventory> recipe) {
        return recipe.matches(this.craftingInv, this.player.world);
    }
    
    @Override
    public void close(final PlayerEntity player) {
        super.close(player);
        this.context.run((world, blockPos) -> this.dropInventory(player, world, this.craftingInv));
    }
    
    @Override
    public boolean canUse(final PlayerEntity player) {
        return Container.canUse(this.context, player, Blocks.bT);
    }
    
    @Override
    public ItemStack transferSlot(final PlayerEntity player, final int invSlot) {
        ItemStack itemStack3 = ItemStack.EMPTY;
        final Slot slot4 = this.slotList.get(invSlot);
        if (slot4 != null && slot4.hasStack()) {
            final ItemStack itemStack4 = slot4.getStack();
            itemStack3 = itemStack4.copy();
            if (invSlot == 0) {
                final ItemStack stack;
                this.context.run((world, blockPos) -> stack.getItem().onCrafted(stack, world, player));
                if (!this.insertItem(itemStack4, 10, 46, true)) {
                    return ItemStack.EMPTY;
                }
                slot4.onStackChanged(itemStack4, itemStack3);
            }
            else if (invSlot >= 10 && invSlot < 37) {
                if (!this.insertItem(itemStack4, 37, 46, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (invSlot >= 37 && invSlot < 46) {
                if (!this.insertItem(itemStack4, 10, 37, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.insertItem(itemStack4, 10, 46, false)) {
                return ItemStack.EMPTY;
            }
            if (itemStack4.isEmpty()) {
                slot4.setStack(ItemStack.EMPTY);
            }
            else {
                slot4.markDirty();
            }
            if (itemStack4.getAmount() == itemStack3.getAmount()) {
                return ItemStack.EMPTY;
            }
            final ItemStack itemStack5 = slot4.onTakeItem(player, itemStack4);
            if (invSlot == 0) {
                player.dropItem(itemStack5, false);
            }
        }
        return itemStack3;
    }
    
    @Override
    public boolean canInsertIntoSlot(final ItemStack stack, final Slot slot) {
        return slot.inventory != this.resultInv && super.canInsertIntoSlot(stack, slot);
    }
    
    @Override
    public int getCraftingResultSlotIndex() {
        return 0;
    }
    
    @Override
    public int getCraftingWidth() {
        return this.craftingInv.getWidth();
    }
    
    @Override
    public int getCraftingHeight() {
        return this.craftingInv.getHeight();
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public int getCraftingSlotCount() {
        return 10;
    }
}

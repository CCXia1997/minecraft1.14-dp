package net.minecraft.container;

import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeType;
import net.minecraft.block.Blocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.BasicInventory;
import com.google.common.collect.Lists;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.StonecuttingRecipe;
import java.util.List;
import net.minecraft.world.World;
import net.minecraft.item.Item;
import com.google.common.collect.ImmutableList;

public class StonecutterContainer extends Container
{
    static final ImmutableList<Item> INGREDIENTS;
    private final BlockContext context;
    private final Property selectedRecipe;
    private final World world;
    private List<StonecuttingRecipe> availableRecipes;
    private ItemStack inputStack;
    private long lastTakeTime;
    final Slot inputSlot;
    final Slot outputSlot;
    private Runnable contentsChangedListener;
    public final Inventory inventory;
    private final CraftingResultInventory n;
    
    public StonecutterContainer(final int syncId, final PlayerInventory playerInventory) {
        this(syncId, playerInventory, BlockContext.EMPTY);
    }
    
    public StonecutterContainer(final int syncId, final PlayerInventory playerInventory, final BlockContext blockContext) {
        super(ContainerType.w, syncId);
        this.selectedRecipe = Property.create();
        this.availableRecipes = Lists.newArrayList();
        this.inputStack = ItemStack.EMPTY;
        this.contentsChangedListener = (() -> {});
        this.inventory = new BasicInventory(1) {
            @Override
            public void markDirty() {
                super.markDirty();
                StonecutterContainer.this.onContentChanged(this);
                StonecutterContainer.this.contentsChangedListener.run();
            }
        };
        this.n = new CraftingResultInventory();
        this.context = blockContext;
        this.world = playerInventory.player.world;
        this.inputSlot = this.addSlot(new Slot(this.inventory, 0, 20, 33));
        this.outputSlot = this.addSlot(new Slot(this.n, 1, 143, 33) {
            @Override
            public boolean canInsert(final ItemStack stack) {
                return false;
            }
            
            @Override
            public ItemStack onTakeItem(final PlayerEntity player, final ItemStack stack) {
                final ItemStack itemStack3 = StonecutterContainer.this.inputSlot.takeStack(1);
                if (!itemStack3.isEmpty()) {
                    StonecutterContainer.this.populateResult();
                }
                stack.getItem().onCrafted(stack, player.world, player);
                final long long3;
                blockContext.run((world, blockPos) -> {
                    long3 = world.getTime();
                    if (StonecutterContainer.this.lastTakeTime != long3) {
                        world.playSound(null, blockPos, SoundEvents.ml, SoundCategory.e, 1.0f, 1.0f);
                        StonecutterContainer.this.lastTakeTime = long3;
                    }
                    return;
                });
                return super.onTakeItem(player, stack);
            }
        });
        for (int integer4 = 0; integer4 < 3; ++integer4) {
            for (int integer5 = 0; integer5 < 9; ++integer5) {
                this.addSlot(new Slot(playerInventory, integer5 + integer4 * 9 + 9, 8 + integer5 * 18, 84 + integer4 * 18));
            }
        }
        for (int integer4 = 0; integer4 < 9; ++integer4) {
            this.addSlot(new Slot(playerInventory, integer4, 8 + integer4 * 18, 142));
        }
        this.addProperty(this.selectedRecipe);
    }
    
    @Environment(EnvType.CLIENT)
    public int getSelectedRecipe() {
        return this.selectedRecipe.get();
    }
    
    @Environment(EnvType.CLIENT)
    public List<StonecuttingRecipe> getAvailableRecipes() {
        return this.availableRecipes;
    }
    
    @Environment(EnvType.CLIENT)
    public int getAvailableRecipeCount() {
        return this.availableRecipes.size();
    }
    
    @Environment(EnvType.CLIENT)
    public boolean canCraft() {
        return this.inputSlot.hasStack() && !this.availableRecipes.isEmpty();
    }
    
    @Override
    public boolean canUse(final PlayerEntity player) {
        return Container.canUse(this.context, player, Blocks.lS);
    }
    
    @Override
    public boolean onButtonClick(final PlayerEntity player, final int id) {
        if (id >= 0 && id < this.availableRecipes.size()) {
            this.selectedRecipe.set(id);
            this.populateResult();
        }
        return true;
    }
    
    @Override
    public void onContentChanged(final Inventory inventory) {
        final ItemStack itemStack2 = this.inputSlot.getStack();
        if (itemStack2.getItem() != this.inputStack.getItem()) {
            this.inputStack = itemStack2.copy();
            this.updateInput(inventory, itemStack2);
        }
    }
    
    private void updateInput(final Inventory inventory, final ItemStack itemStack) {
        this.availableRecipes.clear();
        this.selectedRecipe.set(-1);
        this.outputSlot.setStack(ItemStack.EMPTY);
        if (!itemStack.isEmpty()) {
            this.availableRecipes = this.world.getRecipeManager().<Inventory, StonecuttingRecipe>getAllMatches(RecipeType.f, inventory, this.world);
        }
    }
    
    private void populateResult() {
        if (!this.availableRecipes.isEmpty()) {
            final StonecuttingRecipe stonecuttingRecipe1 = this.availableRecipes.get(this.selectedRecipe.get());
            this.outputSlot.setStack(stonecuttingRecipe1.craft(this.inventory));
        }
        else {
            this.outputSlot.setStack(ItemStack.EMPTY);
        }
        this.sendContentUpdates();
    }
    
    @Override
    public ContainerType<?> getType() {
        return ContainerType.w;
    }
    
    @Environment(EnvType.CLIENT)
    public void setContentsChangedListener(final Runnable runnable) {
        this.contentsChangedListener = runnable;
    }
    
    @Override
    public boolean canInsertIntoSlot(final ItemStack stack, final Slot slot) {
        return false;
    }
    
    @Override
    public ItemStack transferSlot(final PlayerEntity player, final int invSlot) {
        ItemStack itemStack3 = ItemStack.EMPTY;
        final Slot slot4 = this.slotList.get(invSlot);
        if (slot4 != null && slot4.hasStack()) {
            final ItemStack itemStack4 = slot4.getStack();
            final Item item6 = itemStack4.getItem();
            itemStack3 = itemStack4.copy();
            if (invSlot == 1) {
                item6.onCrafted(itemStack4, player.world, player);
                if (!this.insertItem(itemStack4, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
                slot4.onStackChanged(itemStack4, itemStack3);
            }
            else if (invSlot == 0) {
                if (!this.insertItem(itemStack4, 2, 38, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (StonecutterContainer.INGREDIENTS.contains(item6)) {
                if (!this.insertItem(itemStack4, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (invSlot >= 2 && invSlot < 29) {
                if (!this.insertItem(itemStack4, 29, 38, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (invSlot >= 29 && invSlot < 38 && !this.insertItem(itemStack4, 2, 29, false)) {
                return ItemStack.EMPTY;
            }
            if (itemStack4.isEmpty()) {
                slot4.setStack(ItemStack.EMPTY);
            }
            slot4.markDirty();
            if (itemStack4.getAmount() == itemStack3.getAmount()) {
                return ItemStack.EMPTY;
            }
            slot4.onTakeItem(player, itemStack4);
            this.sendContentUpdates();
        }
        return itemStack3;
    }
    
    @Override
    public void close(final PlayerEntity player) {
        super.close(player);
        this.n.removeInvStack(1);
        this.context.run((world, blockPos) -> this.dropInventory(player, player.world, this.inventory));
    }
    
    static {
        INGREDIENTS = ImmutableList.<Item>of(Items.STONE, Items.SANDSTONE, Items.RED_SANDSTONE, Items.QUARTZ_BLOCK, Items.COBBLESTONE, Items.STONE_BRICKS, Items.BRICKS, Items.NETHER_BRICKS, Items.RED_NETHER_BRICKS, Items.PURPUR_BLOCK, Items.PRISMARINE, Items.PRISMARINE_BRICKS, Items.DARK_PRISMARINE, Items.ANDESITE, Items.POLISHED_ANDESITE, Items.GRANITE, Items.POLISHED_GRANITE, Items.DIORITE, Items.POLISHED_DIORITE, Items.MOSSY_STONE_BRICKS, Items.MOSSY_COBBLESTONE, Items.SMOOTH_SANDSTONE, Items.SMOOTH_RED_SANDSTONE, Items.SMOOTH_QUARTZ, Items.END_STONE, Items.END_STONE_BRICKS, Items.SMOOTH_STONE, Items.CUT_SANDSTONE, Items.CUT_RED_SANDSTONE);
    }
}

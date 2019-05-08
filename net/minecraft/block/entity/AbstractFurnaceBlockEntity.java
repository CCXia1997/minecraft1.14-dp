package net.minecraft.block.entity;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import java.util.Collection;
import com.google.common.collect.Lists;
import net.minecraft.entity.player.PlayerEntity;
import java.util.List;
import net.minecraft.util.math.Direction;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.state.property.Property;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.MathHelper;
import net.minecraft.recipe.Recipe;
import net.minecraft.inventory.Inventories;
import net.minecraft.nbt.CompoundTag;
import java.util.Iterator;
import net.minecraft.tag.Tag;
import net.minecraft.tag.ItemTags;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.Items;
import net.minecraft.item.Item;
import com.google.common.collect.Maps;
import net.minecraft.recipe.cooking.CookingRecipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import java.util.Map;
import net.minecraft.container.PropertyDelegate;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Tickable;
import net.minecraft.recipe.RecipeInputProvider;
import net.minecraft.recipe.RecipeUnlocker;
import net.minecraft.inventory.SidedInventory;

public abstract class AbstractFurnaceBlockEntity extends LockableContainerBlockEntity implements SidedInventory, RecipeUnlocker, RecipeInputProvider, Tickable
{
    private static final int[] TOP_SLOTS;
    private static final int[] BOTTOM_SLOTS;
    private static final int[] SIDE_SLOTS;
    protected DefaultedList<ItemStack> inventory;
    private int burnTime;
    private int fuelTime;
    private int cookTime;
    private int cookTimeTotal;
    protected final PropertyDelegate propertyDelegate;
    private final Map<Identifier, Integer> recipesUsed;
    protected final RecipeType<? extends CookingRecipe> recipeType;
    
    protected AbstractFurnaceBlockEntity(final BlockEntityType<?> blockEntityType, final RecipeType<? extends CookingRecipe> recipeType) {
        super(blockEntityType);
        this.inventory = DefaultedList.<ItemStack>create(3, ItemStack.EMPTY);
        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(final int key) {
                switch (key) {
                    case 0: {
                        return AbstractFurnaceBlockEntity.this.burnTime;
                    }
                    case 1: {
                        return AbstractFurnaceBlockEntity.this.fuelTime;
                    }
                    case 2: {
                        return AbstractFurnaceBlockEntity.this.cookTime;
                    }
                    case 3: {
                        return AbstractFurnaceBlockEntity.this.cookTimeTotal;
                    }
                    default: {
                        return 0;
                    }
                }
            }
            
            @Override
            public void set(final int key, final int value) {
                switch (key) {
                    case 0: {
                        AbstractFurnaceBlockEntity.this.burnTime = value;
                        break;
                    }
                    case 1: {
                        AbstractFurnaceBlockEntity.this.fuelTime = value;
                        break;
                    }
                    case 2: {
                        AbstractFurnaceBlockEntity.this.cookTime = value;
                        break;
                    }
                    case 3: {
                        AbstractFurnaceBlockEntity.this.cookTimeTotal = value;
                        break;
                    }
                }
            }
            
            @Override
            public int size() {
                return 4;
            }
        };
        this.recipesUsed = Maps.newHashMap();
        this.recipeType = recipeType;
    }
    
    public static Map<Item, Integer> createFuelTimeMap() {
        final Map<Item, Integer> map1 = Maps.newLinkedHashMap();
        addFuel(map1, Items.kz, 20000);
        addFuel(map1, Blocks.gK, 16000);
        addFuel(map1, Items.mh, 2400);
        addFuel(map1, Items.jh, 1600);
        addFuel(map1, Items.ji, 1600);
        addFuel(map1, ItemTags.o, 300);
        addFuel(map1, ItemTags.b, 300);
        addFuel(map1, ItemTags.h, 300);
        addFuel(map1, ItemTags.i, 150);
        addFuel(map1, ItemTags.l, 300);
        addFuel(map1, ItemTags.k, 300);
        addFuel(map1, Blocks.cH, 300);
        addFuel(map1, Blocks.if_, 300);
        addFuel(map1, Blocks.ie, 300);
        addFuel(map1, Blocks.ig, 300);
        addFuel(map1, Blocks.ii, 300);
        addFuel(map1, Blocks.ih, 300);
        addFuel(map1, Blocks.dI, 300);
        addFuel(map1, Blocks.ia, 300);
        addFuel(map1, Blocks.hZ, 300);
        addFuel(map1, Blocks.ib, 300);
        addFuel(map1, Blocks.id, 300);
        addFuel(map1, Blocks.ic, 300);
        addFuel(map1, Blocks.av, 300);
        addFuel(map1, Blocks.bH, 300);
        addFuel(map1, Blocks.lQ, 300);
        addFuel(map1, Blocks.cG, 300);
        addFuel(map1, Blocks.bP, 300);
        addFuel(map1, Blocks.fj, 300);
        addFuel(map1, Blocks.bT, 300);
        addFuel(map1, Blocks.fn, 300);
        addFuel(map1, ItemTags.v, 300);
        addFuel(map1, Items.jf, 300);
        addFuel(map1, Items.kY, 300);
        addFuel(map1, Blocks.ce, 300);
        addFuel(map1, ItemTags.J, 200);
        addFuel(map1, Items.jo, 200);
        addFuel(map1, Items.jn, 200);
        addFuel(map1, Items.jJ, 200);
        addFuel(map1, Items.jq, 200);
        addFuel(map1, Items.jp, 200);
        addFuel(map1, ItemTags.g, 200);
        addFuel(map1, ItemTags.H, 200);
        addFuel(map1, ItemTags.a, 100);
        addFuel(map1, ItemTags.d, 100);
        addFuel(map1, Items.jz, 100);
        addFuel(map1, ItemTags.n, 100);
        addFuel(map1, Items.jA, 100);
        addFuel(map1, ItemTags.f, 67);
        addFuel(map1, Blocks.jW, 4001);
        addFuel(map1, Items.py, 300);
        addFuel(map1, Blocks.kQ, 50);
        addFuel(map1, Blocks.aS, 100);
        addFuel(map1, Blocks.lI, 50);
        addFuel(map1, Blocks.lJ, 300);
        addFuel(map1, Blocks.lK, 300);
        addFuel(map1, Blocks.lN, 300);
        addFuel(map1, Blocks.lO, 300);
        addFuel(map1, Blocks.lR, 300);
        addFuel(map1, Blocks.lZ, 300);
        return map1;
    }
    
    private static void addFuel(final Map<Item, Integer> fuelTimes, final Tag<Item> tag, final int fuelTime) {
        for (final Item item5 : tag.values()) {
            fuelTimes.put(item5, fuelTime);
        }
    }
    
    private static void addFuel(final Map<Item, Integer> fuelTimes, final ItemProvider item, final int fuelTime) {
        fuelTimes.put(item.getItem(), fuelTime);
    }
    
    private boolean isBurning() {
        return this.burnTime > 0;
    }
    
    @Override
    public void fromTag(final CompoundTag compoundTag) {
        super.fromTag(compoundTag);
        Inventories.fromTag(compoundTag, this.inventory = DefaultedList.<ItemStack>create(this.getInvSize(), ItemStack.EMPTY));
        this.burnTime = compoundTag.getShort("BurnTime");
        this.cookTime = compoundTag.getShort("CookTime");
        this.cookTimeTotal = compoundTag.getShort("CookTimeTotal");
        this.fuelTime = this.getFuelTime(this.inventory.get(1));
        for (int integer2 = compoundTag.getShort("RecipesUsedSize"), integer3 = 0; integer3 < integer2; ++integer3) {
            final Identifier identifier4 = new Identifier(compoundTag.getString("RecipeLocation" + integer3));
            final int integer4 = compoundTag.getInt("RecipeAmount" + integer3);
            this.recipesUsed.put(identifier4, integer4);
        }
    }
    
    @Override
    public CompoundTag toTag(final CompoundTag compoundTag) {
        super.toTag(compoundTag);
        compoundTag.putShort("BurnTime", (short)this.burnTime);
        compoundTag.putShort("CookTime", (short)this.cookTime);
        compoundTag.putShort("CookTimeTotal", (short)this.cookTimeTotal);
        Inventories.toTag(compoundTag, this.inventory);
        compoundTag.putShort("RecipesUsedSize", (short)this.recipesUsed.size());
        int integer2 = 0;
        for (final Map.Entry<Identifier, Integer> entry4 : this.recipesUsed.entrySet()) {
            compoundTag.putString("RecipeLocation" + integer2, entry4.getKey().toString());
            compoundTag.putInt("RecipeAmount" + integer2, entry4.getValue());
            ++integer2;
        }
        return compoundTag;
    }
    
    @Override
    public void tick() {
        final boolean boolean1 = this.isBurning();
        boolean boolean2 = false;
        if (this.isBurning()) {
            --this.burnTime;
        }
        if (!this.world.isClient) {
            final ItemStack itemStack3 = this.inventory.get(1);
            if (this.isBurning() || (!itemStack3.isEmpty() && !this.inventory.get(0).isEmpty())) {
                final Recipe<?> recipe4 = this.world.getRecipeManager().getFirstMatch(this.recipeType, this, this.world).orElse((CookingRecipe)null);
                if (!this.isBurning() && this.canAcceptRecipeOutput(recipe4)) {
                    this.burnTime = this.getFuelTime(itemStack3);
                    this.fuelTime = this.burnTime;
                    if (this.isBurning()) {
                        boolean2 = true;
                        if (!itemStack3.isEmpty()) {
                            final Item item5 = itemStack3.getItem();
                            itemStack3.subtractAmount(1);
                            if (itemStack3.isEmpty()) {
                                final Item item6 = item5.getRecipeRemainder();
                                this.inventory.set(1, (item6 == null) ? ItemStack.EMPTY : new ItemStack(item6));
                            }
                        }
                    }
                }
                if (this.isBurning() && this.canAcceptRecipeOutput(recipe4)) {
                    ++this.cookTime;
                    if (this.cookTime == this.cookTimeTotal) {
                        this.cookTime = 0;
                        this.cookTimeTotal = this.getCookTime();
                        this.craftRecipe(recipe4);
                        boolean2 = true;
                    }
                }
                else {
                    this.cookTime = 0;
                }
            }
            else if (!this.isBurning() && this.cookTime > 0) {
                this.cookTime = MathHelper.clamp(this.cookTime - 2, 0, this.cookTimeTotal);
            }
            if (boolean1 != this.isBurning()) {
                boolean2 = true;
                this.world.setBlockState(this.pos, ((AbstractPropertyContainer<O, BlockState>)this.world.getBlockState(this.pos)).<Comparable, Boolean>with((Property<Comparable>)AbstractFurnaceBlock.LIT, this.isBurning()), 3);
            }
        }
        if (boolean2) {
            this.markDirty();
        }
    }
    
    protected boolean canAcceptRecipeOutput(@Nullable final Recipe<?> recipe) {
        if (this.inventory.get(0).isEmpty() || recipe == null) {
            return false;
        }
        final ItemStack itemStack2 = recipe.getOutput();
        if (itemStack2.isEmpty()) {
            return false;
        }
        final ItemStack itemStack3 = this.inventory.get(2);
        return itemStack3.isEmpty() || (itemStack3.isEqualIgnoreTags(itemStack2) && ((itemStack3.getAmount() < this.getInvMaxStackAmount() && itemStack3.getAmount() < itemStack3.getMaxAmount()) || itemStack3.getAmount() < itemStack2.getMaxAmount()));
    }
    
    private void craftRecipe(@Nullable final Recipe<?> recipe) {
        if (recipe == null || !this.canAcceptRecipeOutput(recipe)) {
            return;
        }
        final ItemStack itemStack2 = this.inventory.get(0);
        final ItemStack itemStack3 = recipe.getOutput();
        final ItemStack itemStack4 = this.inventory.get(2);
        if (itemStack4.isEmpty()) {
            this.inventory.set(2, itemStack3.copy());
        }
        else if (itemStack4.getItem() == itemStack3.getItem()) {
            itemStack4.addAmount(1);
        }
        if (!this.world.isClient) {
            this.setLastRecipe(recipe);
        }
        if (itemStack2.getItem() == Blocks.an.getItem() && !this.inventory.get(1).isEmpty() && this.inventory.get(1).getItem() == Items.kx) {
            this.inventory.set(1, new ItemStack(Items.ky));
        }
        itemStack2.subtractAmount(1);
    }
    
    protected int getFuelTime(final ItemStack fuel) {
        if (fuel.isEmpty()) {
            return 0;
        }
        final Item item2 = fuel.getItem();
        return createFuelTimeMap().getOrDefault(item2, 0);
    }
    
    protected int getCookTime() {
        return this.world.getRecipeManager().getFirstMatch(this.recipeType, this, this.world).<Integer>map(CookingRecipe::getCookTime).orElse(200);
    }
    
    public static boolean canUseAsFuel(final ItemStack stack) {
        return createFuelTimeMap().containsKey(stack.getItem());
    }
    
    @Override
    public int[] getInvAvailableSlots(final Direction side) {
        if (side == Direction.DOWN) {
            return AbstractFurnaceBlockEntity.BOTTOM_SLOTS;
        }
        if (side == Direction.UP) {
            return AbstractFurnaceBlockEntity.TOP_SLOTS;
        }
        return AbstractFurnaceBlockEntity.SIDE_SLOTS;
    }
    
    @Override
    public boolean canInsertInvStack(final int slot, final ItemStack stack, @Nullable final Direction direction) {
        return this.isValidInvStack(slot, stack);
    }
    
    @Override
    public boolean canExtractInvStack(final int slot, final ItemStack stack, final Direction direction) {
        if (direction == Direction.DOWN && slot == 1) {
            final Item item4 = stack.getItem();
            if (item4 != Items.ky && item4 != Items.kx) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int getInvSize() {
        return this.inventory.size();
    }
    
    @Override
    public boolean isInvEmpty() {
        for (final ItemStack itemStack2 : this.inventory) {
            if (!itemStack2.isEmpty()) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public ItemStack getInvStack(final int slot) {
        return this.inventory.get(slot);
    }
    
    @Override
    public ItemStack takeInvStack(final int slot, final int integer2) {
        return Inventories.splitStack(this.inventory, slot, integer2);
    }
    
    @Override
    public ItemStack removeInvStack(final int slot) {
        return Inventories.removeStack(this.inventory, slot);
    }
    
    @Override
    public void setInvStack(final int slot, final ItemStack itemStack) {
        final ItemStack itemStack2 = this.inventory.get(slot);
        final boolean boolean4 = !itemStack.isEmpty() && itemStack.isEqualIgnoreTags(itemStack2) && ItemStack.areTagsEqual(itemStack, itemStack2);
        this.inventory.set(slot, itemStack);
        if (itemStack.getAmount() > this.getInvMaxStackAmount()) {
            itemStack.setAmount(this.getInvMaxStackAmount());
        }
        if (slot == 0 && !boolean4) {
            this.cookTimeTotal = this.getCookTime();
            this.cookTime = 0;
            this.markDirty();
        }
    }
    
    @Override
    public boolean canPlayerUseInv(final PlayerEntity playerEntity) {
        return this.world.getBlockEntity(this.pos) == this && playerEntity.squaredDistanceTo(this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5) <= 64.0;
    }
    
    @Override
    public boolean isValidInvStack(final int slot, final ItemStack itemStack) {
        if (slot == 2) {
            return false;
        }
        if (slot == 1) {
            final ItemStack itemStack2 = this.inventory.get(1);
            return canUseAsFuel(itemStack) || (itemStack.getItem() == Items.kx && itemStack2.getItem() != Items.kx);
        }
        return true;
    }
    
    @Override
    public void clear() {
        this.inventory.clear();
    }
    
    @Override
    public void setLastRecipe(@Nullable final Recipe<?> recipe) {
        if (recipe != null) {
            this.recipesUsed.compute(recipe.getId(), (identifier, integer) -> 1 + ((integer == null) ? 0 : integer));
        }
    }
    
    @Nullable
    @Override
    public Recipe<?> getLastRecipe() {
        return null;
    }
    
    @Override
    public void unlockLastRecipe(final PlayerEntity player) {
    }
    
    public void dropExperience(final PlayerEntity player) {
        final List<Recipe<?>> list2 = Lists.newArrayList();
        for (final Map.Entry<Identifier, Integer> entry4 : this.recipesUsed.entrySet()) {
            final List<CookingRecipe> list3;
            final Map.Entry<K, Integer> entry5;
            player.world.getRecipeManager().get(entry4.getKey()).ifPresent(recipe -> {
                list3.add(recipe);
                dropExperience(player, entry5.getValue(), recipe.getExperience());
                return;
            });
        }
        player.unlockRecipes(list2);
        this.recipesUsed.clear();
    }
    
    private static void dropExperience(final PlayerEntity player, int totalExperience, final float experienceFraction) {
        if (experienceFraction == 0.0f) {
            totalExperience = 0;
        }
        else if (experienceFraction < 1.0f) {
            int integer4 = MathHelper.floor(totalExperience * experienceFraction);
            if (integer4 < MathHelper.ceil(totalExperience * experienceFraction) && Math.random() < totalExperience * experienceFraction - integer4) {
                ++integer4;
            }
            totalExperience = integer4;
        }
        while (totalExperience > 0) {
            final int integer4 = ExperienceOrbEntity.roundToOrbSize(totalExperience);
            totalExperience -= integer4;
            player.world.spawnEntity(new ExperienceOrbEntity(player.world, player.x, player.y + 0.5, player.z + 0.5, integer4));
        }
    }
    
    @Override
    public void provideRecipeInputs(final RecipeFinder recipeFinder) {
        for (final ItemStack itemStack3 : this.inventory) {
            recipeFinder.addItem(itemStack3);
        }
    }
    
    static {
        TOP_SLOTS = new int[] { 0 };
        BOTTOM_SLOTS = new int[] { 2, 1 };
        SIDE_SLOTS = new int[] { 1 };
    }
}

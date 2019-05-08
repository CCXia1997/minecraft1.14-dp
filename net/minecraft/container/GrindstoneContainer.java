package net.minecraft.container;

import net.minecraft.block.Blocks;
import net.minecraft.item.ItemProvider;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import java.util.Iterator;
import net.minecraft.enchantment.Enchantment;
import java.util.Map;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.world.World;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;

public class GrindstoneContainer extends Container
{
    private final Inventory resultInventory;
    private final Inventory craftingInventory;
    private final BlockContext context;
    
    public GrindstoneContainer(final int syncId, final PlayerInventory playerInventory) {
        this(syncId, playerInventory, BlockContext.EMPTY);
    }
    
    public GrindstoneContainer(final int syncId, final PlayerInventory playerInventory, final BlockContext blockContext) {
        super(ContainerType.GRINDSTONE, syncId);
        this.resultInventory = new CraftingResultInventory();
        this.craftingInventory = new BasicInventory(2) {
            @Override
            public void markDirty() {
                super.markDirty();
                GrindstoneContainer.this.onContentChanged(this);
            }
        };
        this.context = blockContext;
        this.addSlot(new Slot(this.craftingInventory, 0, 49, 19) {
            @Override
            public boolean canInsert(final ItemStack stack) {
                return stack.hasDurability() || stack.getItem() == Items.nZ || stack.hasEnchantments();
            }
        });
        this.addSlot(new Slot(this.craftingInventory, 1, 49, 40) {
            @Override
            public boolean canInsert(final ItemStack stack) {
                return stack.hasDurability() || stack.getItem() == Items.nZ || stack.hasEnchantments();
            }
        });
        this.addSlot(new Slot(this.resultInventory, 2, 129, 34) {
            @Override
            public boolean canInsert(final ItemStack stack) {
                return false;
            }
            
            @Override
            public ItemStack onTakeItem(final PlayerEntity player, final ItemStack stack) {
                int integer3;
                int integer4;
                blockContext.run((world, blockPos) -> {
                    integer3 = this.getExperience(world);
                    while (integer3 > 0) {
                        integer4 = ExperienceOrbEntity.roundToOrbSize(integer3);
                        integer3 -= integer4;
                        world.spawnEntity(new ExperienceOrbEntity(world, blockPos.getX(), blockPos.getY() + 0.5, blockPos.getZ() + 0.5, integer4));
                    }
                    world.playLevelEvent(1042, blockPos, 0);
                    return;
                });
                GrindstoneContainer.this.craftingInventory.setInvStack(0, ItemStack.EMPTY);
                GrindstoneContainer.this.craftingInventory.setInvStack(1, ItemStack.EMPTY);
                return stack;
            }
            
            private int getExperience(final World world) {
                int integer2 = 0;
                integer2 += this.getExperience(GrindstoneContainer.this.craftingInventory.getInvStack(0));
                integer2 += this.getExperience(GrindstoneContainer.this.craftingInventory.getInvStack(1));
                if (integer2 > 0) {
                    final int integer3 = (int)Math.ceil(integer2 / 2.0);
                    return integer3 + world.random.nextInt(integer3);
                }
                return 0;
            }
            
            private int getExperience(final ItemStack stack) {
                int integer2 = 0;
                final Map<Enchantment, Integer> map3 = EnchantmentHelper.getEnchantments(stack);
                for (final Map.Entry<Enchantment, Integer> entry5 : map3.entrySet()) {
                    final Enchantment enchantment6 = entry5.getKey();
                    final Integer integer3 = entry5.getValue();
                    if (!enchantment6.isCursed()) {
                        integer2 += enchantment6.getMinimumPower(integer3);
                    }
                }
                return integer2;
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
    }
    
    @Override
    public void onContentChanged(final Inventory inventory) {
        super.onContentChanged(inventory);
        if (inventory == this.craftingInventory) {
            this.updateResult();
        }
    }
    
    private void updateResult() {
        final ItemStack itemStack1 = this.craftingInventory.getInvStack(0);
        final ItemStack itemStack2 = this.craftingInventory.getInvStack(1);
        final boolean boolean3 = !itemStack1.isEmpty() || !itemStack2.isEmpty();
        final boolean boolean4 = !itemStack1.isEmpty() && !itemStack2.isEmpty();
        if (boolean3) {
            final boolean boolean5 = (!itemStack1.isEmpty() && itemStack1.getItem() != Items.nZ && !itemStack1.hasEnchantments()) || (!itemStack2.isEmpty() && itemStack2.getItem() != Items.nZ && !itemStack2.hasEnchantments());
            if (itemStack1.getAmount() > 1 || itemStack2.getAmount() > 1 || (!boolean4 && boolean5)) {
                this.resultInventory.setInvStack(0, ItemStack.EMPTY);
                this.sendContentUpdates();
                return;
            }
            int integer7 = 1;
            int integer11;
            ItemStack itemStack3;
            if (boolean4) {
                if (itemStack1.getItem() != itemStack2.getItem()) {
                    this.resultInventory.setInvStack(0, ItemStack.EMPTY);
                    this.sendContentUpdates();
                    return;
                }
                final Item item9 = itemStack1.getItem();
                final int integer8 = item9.getDurability() - itemStack1.getDamage();
                final int integer9 = item9.getDurability() - itemStack2.getDamage();
                final int integer10 = integer8 + integer9 + item9.getDurability() * 5 / 100;
                integer11 = Math.max(item9.getDurability() - integer10, 0);
                itemStack3 = this.transferEnchantments(itemStack1, itemStack2);
                if (!itemStack3.hasDurability()) {
                    if (!ItemStack.areEqual(itemStack1, itemStack2)) {
                        this.resultInventory.setInvStack(0, ItemStack.EMPTY);
                        this.sendContentUpdates();
                        return;
                    }
                    integer7 = 2;
                }
            }
            else {
                final boolean boolean6 = !itemStack1.isEmpty();
                integer11 = (boolean6 ? itemStack1.getDamage() : itemStack2.getDamage());
                itemStack3 = (boolean6 ? itemStack1 : itemStack2);
            }
            this.resultInventory.setInvStack(0, this.grind(itemStack3, integer11, integer7));
        }
        else {
            this.resultInventory.setInvStack(0, ItemStack.EMPTY);
        }
        this.sendContentUpdates();
    }
    
    private ItemStack transferEnchantments(final ItemStack target, final ItemStack source) {
        final ItemStack itemStack3 = target.copy();
        final Map<Enchantment, Integer> map4 = EnchantmentHelper.getEnchantments(source);
        for (final Map.Entry<Enchantment, Integer> entry6 : map4.entrySet()) {
            final Enchantment enchantment7 = entry6.getKey();
            if (!enchantment7.isCursed() || EnchantmentHelper.getLevel(enchantment7, itemStack3) == 0) {
                itemStack3.addEnchantment(enchantment7, entry6.getValue());
            }
        }
        return itemStack3;
    }
    
    private ItemStack grind(final ItemStack item, final int damage, final int amount) {
        ItemStack itemStack4 = item.copy();
        itemStack4.removeSubTag("Enchantments");
        itemStack4.removeSubTag("StoredEnchantments");
        if (damage > 0) {
            itemStack4.setDamage(damage);
        }
        else {
            itemStack4.removeSubTag("Damage");
        }
        itemStack4.setAmount(amount);
        final Map<Enchantment, Integer> map5 = EnchantmentHelper.getEnchantments(item).entrySet().stream().filter(entry -> entry.getKey().isCursed()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        EnchantmentHelper.set(map5, itemStack4);
        if (itemStack4.getItem() == Items.nZ && map5.size() == 0) {
            itemStack4 = new ItemStack(Items.kS);
        }
        itemStack4.setRepairCost(0);
        for (int integer6 = 0; integer6 < map5.size(); ++integer6) {
            itemStack4.setRepairCost(AnvilContainer.getNextCost(itemStack4.getRepairCost()));
        }
        return itemStack4;
    }
    
    @Override
    public void close(final PlayerEntity player) {
        super.close(player);
        this.context.run((world, blockPos) -> this.dropInventory(player, world, this.craftingInventory));
    }
    
    @Override
    public boolean canUse(final PlayerEntity player) {
        return Container.canUse(this.context, player, Blocks.lP);
    }
    
    @Override
    public ItemStack transferSlot(final PlayerEntity player, final int invSlot) {
        ItemStack itemStack3 = ItemStack.EMPTY;
        final Slot slot4 = this.slotList.get(invSlot);
        if (slot4 != null && slot4.hasStack()) {
            final ItemStack itemStack4 = slot4.getStack();
            itemStack3 = itemStack4.copy();
            final ItemStack itemStack5 = this.craftingInventory.getInvStack(0);
            final ItemStack itemStack6 = this.craftingInventory.getInvStack(1);
            if (invSlot == 2) {
                if (!this.insertItem(itemStack4, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }
                slot4.onStackChanged(itemStack4, itemStack3);
            }
            else if (invSlot == 0 || invSlot == 1) {
                if (!this.insertItem(itemStack4, 3, 39, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (itemStack5.isEmpty() || itemStack6.isEmpty()) {
                if (!this.insertItem(itemStack4, 0, 2, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (invSlot >= 3 && invSlot < 30) {
                if (!this.insertItem(itemStack4, 30, 39, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (invSlot >= 30 && invSlot < 39 && !this.insertItem(itemStack4, 3, 30, false)) {
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
            slot4.onTakeItem(player, itemStack4);
        }
        return itemStack3;
    }
}

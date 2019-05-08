package net.minecraft.container;

import org.apache.logging.log4j.LogManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.text.TextComponent;
import net.minecraft.text.StringTextComponent;
import org.apache.commons.lang3.StringUtils;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.Items;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.block.BlockState;
import net.minecraft.block.AnvilBlock;
import net.minecraft.tag.BlockTags;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import org.apache.logging.log4j.Logger;

public class AnvilContainer extends Container
{
    private static final Logger LOGGER;
    private final Inventory result;
    private final Inventory inventory;
    private final Property levelCost;
    private final BlockContext context;
    private int h;
    private String newItemName;
    private final PlayerEntity player;
    
    public AnvilContainer(final int integer, final PlayerInventory playerInventory) {
        this(integer, playerInventory, BlockContext.EMPTY);
    }
    
    public AnvilContainer(final int integer, final PlayerInventory playerInventory, final BlockContext blockContext) {
        super(ContainerType.ANVIL, integer);
        this.result = new CraftingResultInventory();
        this.inventory = new BasicInventory(2) {
            @Override
            public void markDirty() {
                super.markDirty();
                AnvilContainer.this.onContentChanged(this);
            }
        };
        this.levelCost = Property.create();
        this.context = blockContext;
        this.player = playerInventory.player;
        this.addProperty(this.levelCost);
        this.addSlot(new Slot(this.inventory, 0, 27, 47));
        this.addSlot(new Slot(this.inventory, 1, 76, 47));
        this.addSlot(new Slot(this.result, 2, 134, 47) {
            @Override
            public boolean canInsert(final ItemStack stack) {
                return false;
            }
            
            @Override
            public boolean canTakeItems(final PlayerEntity playerEntity) {
                return (playerEntity.abilities.creativeMode || playerEntity.experience >= AnvilContainer.this.levelCost.get()) && AnvilContainer.this.levelCost.get() > 0 && this.hasStack();
            }
            
            @Override
            public ItemStack onTakeItem(final PlayerEntity player, final ItemStack stack) {
                if (!player.abilities.creativeMode) {
                    player.c(-AnvilContainer.this.levelCost.get());
                }
                AnvilContainer.this.inventory.setInvStack(0, ItemStack.EMPTY);
                if (AnvilContainer.this.h > 0) {
                    final ItemStack itemStack3 = AnvilContainer.this.inventory.getInvStack(1);
                    if (!itemStack3.isEmpty() && itemStack3.getAmount() > AnvilContainer.this.h) {
                        itemStack3.subtractAmount(AnvilContainer.this.h);
                        AnvilContainer.this.inventory.setInvStack(1, itemStack3);
                    }
                    else {
                        AnvilContainer.this.inventory.setInvStack(1, ItemStack.EMPTY);
                    }
                }
                else {
                    AnvilContainer.this.inventory.setInvStack(1, ItemStack.EMPTY);
                }
                AnvilContainer.this.levelCost.set(0);
                final BlockState blockState4;
                BlockState blockState5;
                blockContext.run((world, blockPos) -> {
                    blockState4 = world.getBlockState(blockPos);
                    if (!player.abilities.creativeMode && blockState4.matches(BlockTags.A) && player.getRand().nextFloat() < 0.12f) {
                        blockState5 = AnvilBlock.getLandingState(blockState4);
                        if (blockState5 == null) {
                            world.clearBlockState(blockPos, false);
                            world.playLevelEvent(1029, blockPos, 0);
                        }
                        else {
                            world.setBlockState(blockPos, blockState5, 2);
                            world.playLevelEvent(1030, blockPos, 0);
                        }
                    }
                    else {
                        world.playLevelEvent(1030, blockPos, 0);
                    }
                    return;
                });
                return stack;
            }
        });
        for (int integer2 = 0; integer2 < 3; ++integer2) {
            for (int integer3 = 0; integer3 < 9; ++integer3) {
                this.addSlot(new Slot(playerInventory, integer3 + integer2 * 9 + 9, 8 + integer3 * 18, 84 + integer2 * 18));
            }
        }
        for (int integer2 = 0; integer2 < 9; ++integer2) {
            this.addSlot(new Slot(playerInventory, integer2, 8 + integer2 * 18, 142));
        }
    }
    
    @Override
    public void onContentChanged(final Inventory inventory) {
        super.onContentChanged(inventory);
        if (inventory == this.inventory) {
            this.updateResult();
        }
    }
    
    public void updateResult() {
        final ItemStack itemStack1 = this.inventory.getInvStack(0);
        this.levelCost.set(1);
        int integer2 = 0;
        int integer3 = 0;
        int integer4 = 0;
        if (itemStack1.isEmpty()) {
            this.result.setInvStack(0, ItemStack.EMPTY);
            this.levelCost.set(0);
            return;
        }
        ItemStack itemStack2 = itemStack1.copy();
        final ItemStack itemStack3 = this.inventory.getInvStack(1);
        final Map<Enchantment, Integer> map7 = EnchantmentHelper.getEnchantments(itemStack2);
        integer3 += itemStack1.getRepairCost() + (itemStack3.isEmpty() ? 0 : itemStack3.getRepairCost());
        this.h = 0;
        if (!itemStack3.isEmpty()) {
            final boolean boolean8 = itemStack3.getItem() == Items.nZ && !EnchantedBookItem.getEnchantmentTag(itemStack3).isEmpty();
            if (itemStack2.hasDurability() && itemStack2.getItem().canRepair(itemStack1, itemStack3)) {
                int integer5 = Math.min(itemStack2.getDamage(), itemStack2.getDurability() / 4);
                if (integer5 <= 0) {
                    this.result.setInvStack(0, ItemStack.EMPTY);
                    this.levelCost.set(0);
                    return;
                }
                int integer6;
                for (integer6 = 0; integer5 > 0 && integer6 < itemStack3.getAmount(); integer5 = Math.min(itemStack2.getDamage(), itemStack2.getDurability() / 4), ++integer6) {
                    final int integer7 = itemStack2.getDamage() - integer5;
                    itemStack2.setDamage(integer7);
                    ++integer2;
                }
                this.h = integer6;
            }
            else {
                if (!boolean8 && (itemStack2.getItem() != itemStack3.getItem() || !itemStack2.hasDurability())) {
                    this.result.setInvStack(0, ItemStack.EMPTY);
                    this.levelCost.set(0);
                    return;
                }
                if (itemStack2.hasDurability() && !boolean8) {
                    final int integer5 = itemStack1.getDurability() - itemStack1.getDamage();
                    final int integer6 = itemStack3.getDurability() - itemStack3.getDamage();
                    final int integer7 = integer6 + itemStack2.getDurability() * 12 / 100;
                    final int integer8 = integer5 + integer7;
                    int integer9 = itemStack2.getDurability() - integer8;
                    if (integer9 < 0) {
                        integer9 = 0;
                    }
                    if (integer9 < itemStack2.getDamage()) {
                        itemStack2.setDamage(integer9);
                        integer2 += 2;
                    }
                }
                final Map<Enchantment, Integer> map8 = EnchantmentHelper.getEnchantments(itemStack3);
                boolean boolean9 = false;
                boolean boolean10 = false;
                for (final Enchantment enchantment13 : map8.keySet()) {
                    if (enchantment13 == null) {
                        continue;
                    }
                    final int integer10 = map7.containsKey(enchantment13) ? map7.get(enchantment13) : 0;
                    int integer11 = map8.get(enchantment13);
                    integer11 = ((integer10 == integer11) ? (integer11 + 1) : Math.max(integer11, integer10));
                    boolean boolean11 = enchantment13.isAcceptableItem(itemStack1);
                    if (this.player.abilities.creativeMode || itemStack1.getItem() == Items.nZ) {
                        boolean11 = true;
                    }
                    for (final Enchantment enchantment14 : map7.keySet()) {
                        if (enchantment14 != enchantment13 && !enchantment13.isDifferent(enchantment14)) {
                            boolean11 = false;
                            ++integer2;
                        }
                    }
                    if (!boolean11) {
                        boolean10 = true;
                    }
                    else {
                        boolean9 = true;
                        if (integer11 > enchantment13.getMaximumLevel()) {
                            integer11 = enchantment13.getMaximumLevel();
                        }
                        map7.put(enchantment13, integer11);
                        int integer12 = 0;
                        switch (enchantment13.getWeight()) {
                            case COMMON: {
                                integer12 = 1;
                                break;
                            }
                            case UNCOMMON: {
                                integer12 = 2;
                                break;
                            }
                            case RARE: {
                                integer12 = 4;
                                break;
                            }
                            case LEGENDARY: {
                                integer12 = 8;
                                break;
                            }
                        }
                        if (boolean8) {
                            integer12 = Math.max(1, integer12 / 2);
                        }
                        integer2 += integer12 * integer11;
                        if (itemStack1.getAmount() <= 1) {
                            continue;
                        }
                        integer2 = 40;
                    }
                }
                if (boolean10 && !boolean9) {
                    this.result.setInvStack(0, ItemStack.EMPTY);
                    this.levelCost.set(0);
                    return;
                }
            }
        }
        if (StringUtils.isBlank((CharSequence)this.newItemName)) {
            if (itemStack1.hasDisplayName()) {
                integer4 = 1;
                integer2 += integer4;
                itemStack2.removeDisplayName();
            }
        }
        else if (!this.newItemName.equals(itemStack1.getDisplayName().getString())) {
            integer4 = 1;
            integer2 += integer4;
            itemStack2.setDisplayName(new StringTextComponent(this.newItemName));
        }
        this.levelCost.set(integer3 + integer2);
        if (integer2 <= 0) {
            itemStack2 = ItemStack.EMPTY;
        }
        if (integer4 == integer2 && integer4 > 0 && this.levelCost.get() >= 40) {
            this.levelCost.set(39);
        }
        if (this.levelCost.get() >= 40 && !this.player.abilities.creativeMode) {
            itemStack2 = ItemStack.EMPTY;
        }
        if (!itemStack2.isEmpty()) {
            int integer13 = itemStack2.getRepairCost();
            if (!itemStack3.isEmpty() && integer13 < itemStack3.getRepairCost()) {
                integer13 = itemStack3.getRepairCost();
            }
            if (integer4 != integer2 || integer4 == 0) {
                integer13 = getNextCost(integer13);
            }
            itemStack2.setRepairCost(integer13);
            EnchantmentHelper.set(map7, itemStack2);
        }
        this.result.setInvStack(0, itemStack2);
        this.sendContentUpdates();
    }
    
    public static int getNextCost(final int cost) {
        return cost * 2 + 1;
    }
    
    @Override
    public void close(final PlayerEntity player) {
        super.close(player);
        this.context.run((world, blockPos) -> this.dropInventory(player, world, this.inventory));
    }
    
    @Override
    public boolean canUse(final PlayerEntity player) {
        return this.context.<Boolean>run((world, blockPos) -> {
            if (!world.getBlockState(blockPos).matches(BlockTags.A)) {
                return false;
            }
            else {
                return player.squaredDistanceTo(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5) <= 64.0;
            }
        }, Boolean.valueOf(true));
    }
    
    @Override
    public ItemStack transferSlot(final PlayerEntity player, final int invSlot) {
        ItemStack itemStack3 = ItemStack.EMPTY;
        final Slot slot4 = this.slotList.get(invSlot);
        if (slot4 != null && slot4.hasStack()) {
            final ItemStack itemStack4 = slot4.getStack();
            itemStack3 = itemStack4.copy();
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
            else if (invSlot >= 3 && invSlot < 39 && !this.insertItem(itemStack4, 0, 2, false)) {
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
    
    public void setNewItemName(final String string) {
        this.newItemName = string;
        if (this.getSlot(2).hasStack()) {
            final ItemStack itemStack2 = this.getSlot(2).getStack();
            if (StringUtils.isBlank((CharSequence)string)) {
                itemStack2.removeDisplayName();
            }
            else {
                itemStack2.setDisplayName(new StringTextComponent(this.newItemName));
            }
        }
        this.updateResult();
    }
    
    @Environment(EnvType.CLIENT)
    public int getLevelCost() {
        return this.levelCost.get();
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}

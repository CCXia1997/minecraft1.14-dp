package net.minecraft.container;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemProvider;
import net.minecraft.entity.player.PlayerEntity;
import java.util.List;
import net.minecraft.util.registry.Registry;
import net.minecraft.enchantment.InfoEnchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.entity.player.PlayerInventory;
import java.util.Random;
import net.minecraft.inventory.Inventory;

public class EnchantingTableContainer extends Container
{
    private final Inventory inventory;
    private final BlockContext context;
    private final Random random;
    private final Property seed;
    public final int[] enchantmentPower;
    public final int[] enchantmentId;
    public final int[] enchantmentLevel;
    
    public EnchantingTableContainer(final int syncId, final PlayerInventory playerInventory) {
        this(syncId, playerInventory, BlockContext.EMPTY);
    }
    
    public EnchantingTableContainer(final int syncId, final PlayerInventory playerInventory, final BlockContext blockContext) {
        super(ContainerType.ENCHANTMENT, syncId);
        this.inventory = new BasicInventory(2) {
            @Override
            public void markDirty() {
                super.markDirty();
                EnchantingTableContainer.this.onContentChanged(this);
            }
        };
        this.random = new Random();
        this.seed = Property.create();
        this.enchantmentPower = new int[3];
        this.enchantmentId = new int[] { -1, -1, -1 };
        this.enchantmentLevel = new int[] { -1, -1, -1 };
        this.context = blockContext;
        this.addSlot(new Slot(this.inventory, 0, 15, 47) {
            @Override
            public boolean canInsert(final ItemStack stack) {
                return true;
            }
            
            @Override
            public int getMaxStackAmount() {
                return 1;
            }
        });
        this.addSlot(new Slot(this.inventory, 1, 35, 47) {
            @Override
            public boolean canInsert(final ItemStack stack) {
                return stack.getItem() == Items.ll;
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
        this.addProperty(Property.create(this.enchantmentPower, 0));
        this.addProperty(Property.create(this.enchantmentPower, 1));
        this.addProperty(Property.create(this.enchantmentPower, 2));
        this.addProperty(this.seed).set(playerInventory.player.getEnchantmentTableSeed());
        this.addProperty(Property.create(this.enchantmentId, 0));
        this.addProperty(Property.create(this.enchantmentId, 1));
        this.addProperty(Property.create(this.enchantmentId, 2));
        this.addProperty(Property.create(this.enchantmentLevel, 0));
        this.addProperty(Property.create(this.enchantmentLevel, 1));
        this.addProperty(Property.create(this.enchantmentLevel, 2));
    }
    
    @Override
    public void onContentChanged(final Inventory inventory) {
        if (inventory == this.inventory) {
            final ItemStack itemStack2 = inventory.getInvStack(0);
            if (itemStack2.isEmpty() || !itemStack2.isEnchantable()) {
                for (int integer3 = 0; integer3 < 3; ++integer3) {
                    this.enchantmentPower[integer3] = 0;
                    this.enchantmentId[integer3] = -1;
                    this.enchantmentLevel[integer3] = -1;
                }
            }
            else {
                int integer4;
                int integer5;
                int integer6;
                int integer7;
                final ItemStack itemStack3;
                int integer8;
                List<InfoEnchantment> list6;
                InfoEnchantment infoEnchantment7;
                this.context.run((world, blockPos) -> {
                    integer4 = 0;
                    for (integer5 = -1; integer5 <= 1; ++integer5) {
                        for (integer6 = -1; integer6 <= 1; ++integer6) {
                            if (integer5 != 0 || integer6 != 0) {
                                if (world.isAir(blockPos.add(integer6, 0, integer5)) && world.isAir(blockPos.add(integer6, 1, integer5))) {
                                    if (world.getBlockState(blockPos.add(integer6 * 2, 0, integer5 * 2)).getBlock() == Blocks.bH) {
                                        ++integer4;
                                    }
                                    if (world.getBlockState(blockPos.add(integer6 * 2, 1, integer5 * 2)).getBlock() == Blocks.bH) {
                                        ++integer4;
                                    }
                                    if (integer6 != 0 && integer5 != 0) {
                                        if (world.getBlockState(blockPos.add(integer6 * 2, 0, integer5)).getBlock() == Blocks.bH) {
                                            ++integer4;
                                        }
                                        if (world.getBlockState(blockPos.add(integer6 * 2, 1, integer5)).getBlock() == Blocks.bH) {
                                            ++integer4;
                                        }
                                        if (world.getBlockState(blockPos.add(integer6, 0, integer5 * 2)).getBlock() == Blocks.bH) {
                                            ++integer4;
                                        }
                                        if (world.getBlockState(blockPos.add(integer6, 1, integer5 * 2)).getBlock() == Blocks.bH) {
                                            ++integer4;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    this.random.setSeed(this.seed.get());
                    for (integer7 = 0; integer7 < 3; ++integer7) {
                        this.enchantmentPower[integer7] = EnchantmentHelper.calculateEnchantmentPower(this.random, integer7, integer4, itemStack3);
                        this.enchantmentId[integer7] = -1;
                        this.enchantmentLevel[integer7] = -1;
                        if (this.enchantmentPower[integer7] < integer7 + 1) {
                            this.enchantmentPower[integer7] = 0;
                        }
                    }
                    for (integer8 = 0; integer8 < 3; ++integer8) {
                        if (this.enchantmentPower[integer8] > 0) {
                            list6 = this.getRandomEnchantments(itemStack3, integer8, this.enchantmentPower[integer8]);
                            if (list6 != null && !list6.isEmpty()) {
                                infoEnchantment7 = list6.get(this.random.nextInt(list6.size()));
                                this.enchantmentId[integer8] = Registry.ENCHANTMENT.getRawId(infoEnchantment7.enchantment);
                                this.enchantmentLevel[integer8] = infoEnchantment7.level;
                            }
                        }
                    }
                    this.sendContentUpdates();
                });
            }
        }
    }
    
    @Override
    public boolean onButtonClick(final PlayerEntity player, final int id) {
        final ItemStack itemStack3 = this.inventory.getInvStack(0);
        final ItemStack itemStack4 = this.inventory.getInvStack(1);
        final int integer5 = id + 1;
        if ((itemStack4.isEmpty() || itemStack4.getAmount() < integer5) && !player.abilities.creativeMode) {
            return false;
        }
        if (this.enchantmentPower[id] > 0 && !itemStack3.isEmpty() && ((player.experience >= integer5 && player.experience >= this.enchantmentPower[id]) || player.abilities.creativeMode)) {
            final ItemStack itemStack6;
            ItemStack itemStack5;
            final List<InfoEnchantment> list9;
            final int integer7;
            boolean boolean10;
            int integer6;
            InfoEnchantment infoEnchantment12;
            final ItemStack itemStack7;
            this.context.run((world, blockPos) -> {
                itemStack5 = itemStack6;
                list9 = this.getRandomEnchantments(itemStack5, id, this.enchantmentPower[id]);
                if (!list9.isEmpty()) {
                    player.a(itemStack5, integer7);
                    boolean10 = (itemStack5.getItem() == Items.kS);
                    if (boolean10) {
                        itemStack5 = new ItemStack(Items.nZ);
                        this.inventory.setInvStack(0, itemStack5);
                    }
                    for (integer6 = 0; integer6 < list9.size(); ++integer6) {
                        infoEnchantment12 = list9.get(integer6);
                        if (boolean10) {
                            EnchantedBookItem.addEnchantment(itemStack5, infoEnchantment12);
                        }
                        else {
                            itemStack5.addEnchantment(infoEnchantment12.enchantment, infoEnchantment12.level);
                        }
                    }
                    if (!player.abilities.creativeMode) {
                        itemStack7.subtractAmount(integer7);
                        if (itemStack7.isEmpty()) {
                            this.inventory.setInvStack(1, ItemStack.EMPTY);
                        }
                    }
                    player.incrementStat(Stats.ai);
                    if (player instanceof ServerPlayerEntity) {
                        Criterions.ENCHANTED_ITEM.handle((ServerPlayerEntity)player, itemStack5, integer7);
                    }
                    this.inventory.markDirty();
                    this.seed.set(player.getEnchantmentTableSeed());
                    this.onContentChanged(this.inventory);
                    world.playSound(null, blockPos, SoundEvents.ct, SoundCategory.e, 1.0f, world.random.nextFloat() * 0.1f + 0.9f);
                }
                return;
            });
            return true;
        }
        return false;
    }
    
    private List<InfoEnchantment> getRandomEnchantments(final ItemStack stack, final int integer2, final int integer3) {
        this.random.setSeed(this.seed.get() + integer2);
        final List<InfoEnchantment> list4 = EnchantmentHelper.getEnchantments(this.random, stack, integer3, false);
        if (stack.getItem() == Items.kS && list4.size() > 1) {
            list4.remove(this.random.nextInt(list4.size()));
        }
        return list4;
    }
    
    @Environment(EnvType.CLIENT)
    public int getLapisCount() {
        final ItemStack itemStack1 = this.inventory.getInvStack(1);
        if (itemStack1.isEmpty()) {
            return 0;
        }
        return itemStack1.getAmount();
    }
    
    @Environment(EnvType.CLIENT)
    public int getSeed() {
        return this.seed.get();
    }
    
    @Override
    public void close(final PlayerEntity player) {
        super.close(player);
        this.context.run((world, blockPos) -> this.dropInventory(player, player.world, this.inventory));
    }
    
    @Override
    public boolean canUse(final PlayerEntity player) {
        return Container.canUse(this.context, player, Blocks.dR);
    }
    
    @Override
    public ItemStack transferSlot(final PlayerEntity player, final int invSlot) {
        ItemStack itemStack3 = ItemStack.EMPTY;
        final Slot slot4 = this.slotList.get(invSlot);
        if (slot4 != null && slot4.hasStack()) {
            final ItemStack itemStack4 = slot4.getStack();
            itemStack3 = itemStack4.copy();
            if (invSlot == 0) {
                if (!this.insertItem(itemStack4, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (invSlot == 1) {
                if (!this.insertItem(itemStack4, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (itemStack4.getItem() == Items.ll) {
                if (!this.insertItem(itemStack4, 1, 2, true)) {
                    return ItemStack.EMPTY;
                }
            }
            else {
                if (this.slotList.get(0).hasStack() || !this.slotList.get(0).canInsert(itemStack4)) {
                    return ItemStack.EMPTY;
                }
                if (itemStack4.hasTag() && itemStack4.getAmount() == 1) {
                    this.slotList.get(0).setStack(itemStack4.copy());
                    itemStack4.setAmount(0);
                }
                else if (!itemStack4.isEmpty()) {
                    this.slotList.get(0).setStack(new ItemStack(itemStack4.getItem()));
                    itemStack4.subtractAmount(1);
                }
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

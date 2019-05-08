package net.minecraft.item;

import java.util.AbstractList;
import java.util.Iterator;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.DefaultedList;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.enchantment.InfoEnchantment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.text.TextComponent;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.world.World;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class EnchantedBookItem extends Item
{
    public EnchantedBookItem(final Settings settings) {
        super(settings);
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public boolean hasEnchantmentGlint(final ItemStack stack) {
        return true;
    }
    
    @Override
    public boolean isTool(final ItemStack stack) {
        return false;
    }
    
    public static ListTag getEnchantmentTag(final ItemStack stack) {
        final CompoundTag compoundTag2 = stack.getTag();
        if (compoundTag2 != null) {
            return compoundTag2.getList("StoredEnchantments", 10);
        }
        return new ListTag();
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void buildTooltip(final ItemStack stack, @Nullable final World world, final List<TextComponent> tooltip, final TooltipContext options) {
        super.buildTooltip(stack, world, tooltip, options);
        ItemStack.appendEnchantmentComponents(tooltip, getEnchantmentTag(stack));
    }
    
    public static void addEnchantment(final ItemStack stack, final InfoEnchantment enchantmentInfo) {
        final ListTag listTag3 = getEnchantmentTag(stack);
        boolean boolean4 = true;
        final Identifier identifier5 = Registry.ENCHANTMENT.getId(enchantmentInfo.enchantment);
        for (int integer6 = 0; integer6 < listTag3.size(); ++integer6) {
            final CompoundTag compoundTag7 = listTag3.getCompoundTag(integer6);
            final Identifier identifier6 = Identifier.create(compoundTag7.getString("id"));
            if (identifier6 != null && identifier6.equals(identifier5)) {
                if (compoundTag7.getInt("lvl") < enchantmentInfo.level) {
                    compoundTag7.putShort("lvl", (short)enchantmentInfo.level);
                }
                boolean4 = false;
                break;
            }
        }
        if (boolean4) {
            final CompoundTag compoundTag8 = new CompoundTag();
            compoundTag8.putString("id", String.valueOf(identifier5));
            compoundTag8.putShort("lvl", (short)enchantmentInfo.level);
            ((AbstractList<CompoundTag>)listTag3).add(compoundTag8);
        }
        stack.getOrCreateTag().put("StoredEnchantments", listTag3);
    }
    
    public static ItemStack makeStack(final InfoEnchantment infoEnchantment) {
        final ItemStack itemStack2 = new ItemStack(Items.nZ);
        addEnchantment(itemStack2, infoEnchantment);
        return itemStack2;
    }
    
    @Override
    public void appendItemsForGroup(final ItemGroup itemGroup, final DefaultedList<ItemStack> defaultedList) {
        if (itemGroup == ItemGroup.SEARCH) {
            for (final Enchantment enchantment4 : Registry.ENCHANTMENT) {
                if (enchantment4.type != null) {
                    for (int integer5 = enchantment4.getMinimumLevel(); integer5 <= enchantment4.getMaximumLevel(); ++integer5) {
                        defaultedList.add(makeStack(new InfoEnchantment(enchantment4, integer5)));
                    }
                }
            }
        }
        else if (itemGroup.getEnchantments().length != 0) {
            for (final Enchantment enchantment4 : Registry.ENCHANTMENT) {
                if (itemGroup.containsEnchantments(enchantment4.type)) {
                    defaultedList.add(makeStack(new InfoEnchantment(enchantment4, enchantment4.getMaximumLevel())));
                }
            }
        }
    }
}

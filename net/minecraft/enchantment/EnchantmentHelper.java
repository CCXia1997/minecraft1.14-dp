package net.minecraft.enchantment;

import java.util.AbstractList;
import java.util.Collection;
import net.minecraft.util.WeightedPicker;
import net.minecraft.util.math.MathHelper;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.Item;
import java.util.Random;
import javax.annotation.Nullable;
import java.util.List;
import com.google.common.collect.Lists;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import org.apache.commons.lang3.mutable.MutableFloat;
import net.minecraft.entity.EntityGroup;
import org.apache.commons.lang3.mutable.MutableInt;
import net.minecraft.entity.damage.DamageSource;
import java.util.Iterator;
import net.minecraft.nbt.Tag;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.Items;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.item.ItemStack;

public class EnchantmentHelper
{
    public static int getLevel(final Enchantment enchantment, final ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        }
        final Identifier identifier3 = Registry.ENCHANTMENT.getId(enchantment);
        final ListTag listTag4 = stack.getEnchantmentList();
        for (int integer5 = 0; integer5 < listTag4.size(); ++integer5) {
            final CompoundTag compoundTag6 = listTag4.getCompoundTag(integer5);
            final Identifier identifier4 = Identifier.create(compoundTag6.getString("id"));
            if (identifier4 != null && identifier4.equals(identifier3)) {
                return compoundTag6.getInt("lvl");
            }
        }
        return 0;
    }
    
    public static Map<Enchantment, Integer> getEnchantments(final ItemStack stack) {
        final Map<Enchantment, Integer> map2 = Maps.newLinkedHashMap();
        final ListTag listTag3 = (stack.getItem() == Items.nZ) ? EnchantedBookItem.getEnchantmentTag(stack) : stack.getEnchantmentList();
        for (int integer4 = 0; integer4 < listTag3.size(); ++integer4) {
            final CompoundTag compoundTag5 = listTag3.getCompoundTag(integer4);
            final Integer n;
            Registry.ENCHANTMENT.getOrEmpty(Identifier.create(compoundTag5.getString("id"))).ifPresent(enchantment -> n = map2.put(enchantment, compoundTag5.getInt("lvl")));
        }
        return map2;
    }
    
    public static void set(final Map<Enchantment, Integer> enchantments, final ItemStack stack) {
        final ListTag listTag3 = new ListTag();
        for (final Map.Entry<Enchantment, Integer> entry5 : enchantments.entrySet()) {
            final Enchantment enchantment6 = entry5.getKey();
            if (enchantment6 == null) {
                continue;
            }
            final int integer7 = entry5.getValue();
            final CompoundTag compoundTag8 = new CompoundTag();
            compoundTag8.putString("id", String.valueOf(Registry.ENCHANTMENT.getId(enchantment6)));
            compoundTag8.putShort("lvl", (short)integer7);
            ((AbstractList<CompoundTag>)listTag3).add(compoundTag8);
            if (stack.getItem() != Items.nZ) {
                continue;
            }
            EnchantedBookItem.addEnchantment(stack, new InfoEnchantment(enchantment6, integer7));
        }
        if (listTag3.isEmpty()) {
            stack.removeSubTag("Enchantments");
        }
        else if (stack.getItem() != Items.nZ) {
            stack.setChildTag("Enchantments", listTag3);
        }
    }
    
    private static void accept(final Consumer enchantmentHandler, final ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }
        final ListTag listTag3 = stack.getEnchantmentList();
        for (int integer4 = 0; integer4 < listTag3.size(); ++integer4) {
            final String string5 = listTag3.getCompoundTag(integer4).getString("id");
            final int integer5 = listTag3.getCompoundTag(integer4).getInt("lvl");
            Registry.ENCHANTMENT.getOrEmpty(Identifier.create(string5)).ifPresent(enchantment -> enchantmentHandler.accept(enchantment, integer5));
        }
    }
    
    private static void accept(final Consumer enchantmentHandler, final Iterable<ItemStack> stacks) {
        for (final ItemStack itemStack4 : stacks) {
            accept(enchantmentHandler, itemStack4);
        }
    }
    
    public static int getProtectionAmount(final Iterable<ItemStack> equipment, final DamageSource source) {
        final MutableInt mutableInt3 = new MutableInt();
        accept((enchantment, integer) -> mutableInt3.add(enchantment.getProtectionAmount(integer, source)), equipment);
        return mutableInt3.intValue();
    }
    
    public static float getAttackDamage(final ItemStack stack, final EntityGroup group) {
        final MutableFloat mutableFloat3 = new MutableFloat();
        accept((enchantment, integer) -> mutableFloat3.add(enchantment.getAttackDamage(integer, group)), stack);
        return mutableFloat3.floatValue();
    }
    
    public static float getSweepingMultiplier(final LivingEntity entity) {
        final int integer2 = getEquipmentLevel(Enchantments.r, entity);
        if (integer2 > 0) {
            return SweepingEnchantment.getMultiplier(integer2);
        }
        return 0.0f;
    }
    
    public static void onUserDamaged(final LivingEntity user, final Entity attacker) {
        final Consumer consumer3 = (enchantment, integer) -> enchantment.onUserDamaged(user, attacker, integer);
        if (user != null) {
            accept(consumer3, user.getItemsEquipped());
        }
        if (attacker instanceof PlayerEntity) {
            accept(consumer3, user.getMainHandStack());
        }
    }
    
    public static void onTargetDamaged(final LivingEntity user, final Entity target) {
        final Consumer consumer3 = (enchantment, integer) -> enchantment.onTargetDamaged(user, target, integer);
        if (user != null) {
            accept(consumer3, user.getItemsEquipped());
        }
        if (user instanceof PlayerEntity) {
            accept(consumer3, user.getMainHandStack());
        }
    }
    
    public static int getEquipmentLevel(final Enchantment ench, final LivingEntity entity) {
        final Iterable<ItemStack> iterable3 = ench.getEquipment(entity).values();
        if (iterable3 == null) {
            return 0;
        }
        int integer4 = 0;
        for (final ItemStack itemStack6 : iterable3) {
            final int integer5 = getLevel(ench, itemStack6);
            if (integer5 > integer4) {
                integer4 = integer5;
            }
        }
        return integer4;
    }
    
    public static int getKnockback(final LivingEntity entity) {
        return getEquipmentLevel(Enchantments.o, entity);
    }
    
    public static int getFireAspect(final LivingEntity entity) {
        return getEquipmentLevel(Enchantments.p, entity);
    }
    
    public static int getRespiration(final LivingEntity entity) {
        return getEquipmentLevel(Enchantments.f, entity);
    }
    
    public static int getDepthStrider(final LivingEntity entity) {
        return getEquipmentLevel(Enchantments.i, entity);
    }
    
    public static int getEfficiency(final LivingEntity entity) {
        return getEquipmentLevel(Enchantments.s, entity);
    }
    
    public static int getLuckOfTheSea(final ItemStack stack) {
        return getLevel(Enchantments.A, stack);
    }
    
    public static int getLure(final ItemStack stack) {
        return getLevel(Enchantments.B, stack);
    }
    
    public static int getLooting(final LivingEntity entity) {
        return getEquipmentLevel(Enchantments.q, entity);
    }
    
    public static boolean hasAquaAffinity(final LivingEntity entity) {
        return getEquipmentLevel(Enchantments.g, entity) > 0;
    }
    
    public static boolean hasFrostWalker(final LivingEntity entity) {
        return getEquipmentLevel(Enchantments.j, entity) > 0;
    }
    
    public static boolean hasBindingCurse(final ItemStack stack) {
        return getLevel(Enchantments.k, stack) > 0;
    }
    
    public static boolean hasVanishingCurse(final ItemStack stack) {
        return getLevel(Enchantments.K, stack) > 0;
    }
    
    public static int getLoyalty(final ItemStack stack) {
        return getLevel(Enchantments.C, stack);
    }
    
    public static int getRiptide(final ItemStack stack) {
        return getLevel(Enchantments.E, stack);
    }
    
    public static boolean hasChanneling(final ItemStack stack) {
        return getLevel(Enchantments.F, stack) > 0;
    }
    
    @Nullable
    public static Map.Entry<EquipmentSlot, ItemStack> getRandomEnchantedEquipment(final Enchantment enchantment, final LivingEntity livingEntity) {
        final Map<EquipmentSlot, ItemStack> map3 = enchantment.getEquipment(livingEntity);
        if (map3.isEmpty()) {
            return null;
        }
        final List<Map.Entry<EquipmentSlot, ItemStack>> list4 = Lists.newArrayList();
        for (final Map.Entry<EquipmentSlot, ItemStack> entry6 : map3.entrySet()) {
            final ItemStack itemStack7 = entry6.getValue();
            if (!itemStack7.isEmpty() && getLevel(enchantment, itemStack7) > 0) {
                list4.add(entry6);
            }
        }
        return list4.isEmpty() ? null : list4.get(livingEntity.getRand().nextInt(list4.size()));
    }
    
    public static int calculateEnchantmentPower(final Random random, final int num, int enchantmentPower, final ItemStack rstack) {
        final Item item5 = rstack.getItem();
        final int integer6 = item5.getEnchantability();
        if (integer6 <= 0) {
            return 0;
        }
        if (enchantmentPower > 15) {
            enchantmentPower = 15;
        }
        final int integer7 = random.nextInt(8) + 1 + (enchantmentPower >> 1) + random.nextInt(enchantmentPower + 1);
        if (num == 0) {
            return Math.max(integer7 / 3, 1);
        }
        if (num == 1) {
            return integer7 * 2 / 3 + 1;
        }
        return Math.max(integer7, enchantmentPower * 2);
    }
    
    public static ItemStack enchant(final Random random, ItemStack target, final int level, final boolean hasTreasure) {
        final List<InfoEnchantment> list5 = getEnchantments(random, target, level, hasTreasure);
        final boolean boolean6 = target.getItem() == Items.kS;
        if (boolean6) {
            target = new ItemStack(Items.nZ);
        }
        for (final InfoEnchantment infoEnchantment8 : list5) {
            if (boolean6) {
                EnchantedBookItem.addEnchantment(target, infoEnchantment8);
            }
            else {
                target.addEnchantment(infoEnchantment8.enchantment, infoEnchantment8.level);
            }
        }
        return target;
    }
    
    public static List<InfoEnchantment> getEnchantments(final Random random, final ItemStack stack, int level, final boolean hasTreasure) {
        final List<InfoEnchantment> list5 = Lists.newArrayList();
        final Item item6 = stack.getItem();
        final int integer7 = item6.getEnchantability();
        if (integer7 <= 0) {
            return list5;
        }
        level += 1 + random.nextInt(integer7 / 4 + 1) + random.nextInt(integer7 / 4 + 1);
        final float float8 = (random.nextFloat() + random.nextFloat() - 1.0f) * 0.15f;
        level = MathHelper.clamp(Math.round(level + level * float8), 1, Integer.MAX_VALUE);
        List<InfoEnchantment> list6 = getHighestApplicableEnchantmentsAtPower(level, stack, hasTreasure);
        if (!list6.isEmpty()) {
            list5.add(WeightedPicker.<InfoEnchantment>getRandom(random, list6));
            while (random.nextInt(50) <= level) {
                level = level * 4 / 5 + 1;
                list6 = getHighestApplicableEnchantmentsAtPower(level, stack, hasTreasure);
                for (final InfoEnchantment infoEnchantment11 : list5) {
                    remove(list6, infoEnchantment11);
                }
                if (list6.isEmpty()) {
                    break;
                }
                list5.add(WeightedPicker.<InfoEnchantment>getRandom(random, list6));
                level /= 2;
            }
        }
        return list5;
    }
    
    public static void remove(final List<InfoEnchantment> infos, final InfoEnchantment info) {
        final Iterator<InfoEnchantment> iterator3 = infos.iterator();
        while (iterator3.hasNext()) {
            if (!info.enchantment.isDifferent(iterator3.next().enchantment)) {
                iterator3.remove();
            }
        }
    }
    
    public static boolean contains(final Collection<Enchantment> collection, final Enchantment enchantment) {
        for (final Enchantment enchantment2 : collection) {
            if (!enchantment2.isDifferent(enchantment)) {
                return false;
            }
        }
        return true;
    }
    
    public static List<InfoEnchantment> getHighestApplicableEnchantmentsAtPower(final int power, final ItemStack stack, final boolean boolean3) {
        final List<InfoEnchantment> list4 = Lists.newArrayList();
        final Item item5 = stack.getItem();
        final boolean boolean4 = stack.getItem() == Items.kS;
        for (final Enchantment enchantment8 : Registry.ENCHANTMENT) {
            if (enchantment8.isTreasure() && !boolean3) {
                continue;
            }
            if (!enchantment8.type.isAcceptableItem(item5) && !boolean4) {
                continue;
            }
            for (int integer9 = enchantment8.getMaximumLevel(); integer9 > enchantment8.getMinimumLevel() - 1; --integer9) {
                if (power >= enchantment8.getMinimumPower(integer9)) {
                    list4.add(new InfoEnchantment(enchantment8, integer9));
                    break;
                }
            }
        }
        return list4;
    }
    
    @FunctionalInterface
    interface Consumer
    {
        void accept(final Enchantment arg1, final int arg2);
    }
}

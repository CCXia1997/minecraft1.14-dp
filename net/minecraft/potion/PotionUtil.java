package net.minecraft.potion;

import java.util.AbstractList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.text.StringTextComponent;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.util.Pair;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttribute;
import java.util.Map;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import java.util.Iterator;
import net.minecraft.nbt.ListTag;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import com.google.common.collect.Lists;
import java.util.Collection;
import net.minecraft.entity.effect.StatusEffectInstance;
import java.util.List;
import net.minecraft.item.ItemStack;

public class PotionUtil
{
    public static List<StatusEffectInstance> getPotionEffects(final ItemStack stack) {
        return getPotionEffects(stack.getTag());
    }
    
    public static List<StatusEffectInstance> getPotionEffects(final Potion potion, final Collection<StatusEffectInstance> custom) {
        final List<StatusEffectInstance> list3 = Lists.newArrayList();
        list3.addAll(potion.getEffects());
        list3.addAll(custom);
        return list3;
    }
    
    public static List<StatusEffectInstance> getPotionEffects(@Nullable final CompoundTag tag) {
        final List<StatusEffectInstance> list2 = Lists.newArrayList();
        list2.addAll(getPotion(tag).getEffects());
        getCustomPotionEffects(tag, list2);
        return list2;
    }
    
    public static List<StatusEffectInstance> getCustomPotionEffects(final ItemStack stack) {
        return getCustomPotionEffects(stack.getTag());
    }
    
    public static List<StatusEffectInstance> getCustomPotionEffects(@Nullable final CompoundTag tag) {
        final List<StatusEffectInstance> list2 = Lists.newArrayList();
        getCustomPotionEffects(tag, list2);
        return list2;
    }
    
    public static void getCustomPotionEffects(@Nullable final CompoundTag tag, final List<StatusEffectInstance> list) {
        if (tag != null && tag.containsKey("CustomPotionEffects", 9)) {
            final ListTag listTag3 = tag.getList("CustomPotionEffects", 10);
            for (int integer4 = 0; integer4 < listTag3.size(); ++integer4) {
                final CompoundTag compoundTag5 = listTag3.getCompoundTag(integer4);
                final StatusEffectInstance statusEffectInstance6 = StatusEffectInstance.deserialize(compoundTag5);
                if (statusEffectInstance6 != null) {
                    list.add(statusEffectInstance6);
                }
            }
        }
    }
    
    public static int getColor(final ItemStack stack) {
        final CompoundTag compoundTag2 = stack.getTag();
        if (compoundTag2 != null && compoundTag2.containsKey("CustomPotionColor", 99)) {
            return compoundTag2.getInt("CustomPotionColor");
        }
        return (getPotion(stack) == Potions.a) ? 16253176 : getColor(getPotionEffects(stack));
    }
    
    public static int getColor(final Potion potion) {
        return (potion == Potions.a) ? 16253176 : getColor(potion.getEffects());
    }
    
    public static int getColor(final Collection<StatusEffectInstance> effects) {
        final int integer2 = 3694022;
        if (effects.isEmpty()) {
            return 3694022;
        }
        float float3 = 0.0f;
        float float4 = 0.0f;
        float float5 = 0.0f;
        int integer3 = 0;
        for (final StatusEffectInstance statusEffectInstance8 : effects) {
            if (!statusEffectInstance8.shouldShowParticles()) {
                continue;
            }
            final int integer4 = statusEffectInstance8.getEffectType().getColor();
            final int integer5 = statusEffectInstance8.getAmplifier() + 1;
            float3 += integer5 * (integer4 >> 16 & 0xFF) / 255.0f;
            float4 += integer5 * (integer4 >> 8 & 0xFF) / 255.0f;
            float5 += integer5 * (integer4 >> 0 & 0xFF) / 255.0f;
            integer3 += integer5;
        }
        if (integer3 == 0) {
            return 0;
        }
        float3 = float3 / integer3 * 255.0f;
        float4 = float4 / integer3 * 255.0f;
        float5 = float5 / integer3 * 255.0f;
        return (int)float3 << 16 | (int)float4 << 8 | (int)float5;
    }
    
    public static Potion getPotion(final ItemStack stack) {
        return getPotion(stack.getTag());
    }
    
    public static Potion getPotion(@Nullable final CompoundTag compound) {
        if (compound == null) {
            return Potions.a;
        }
        return Potion.byId(compound.getString("Potion"));
    }
    
    public static ItemStack setPotion(final ItemStack stack, final Potion potion) {
        final Identifier identifier3 = Registry.POTION.getId(potion);
        if (potion == Potions.a) {
            stack.removeSubTag("Potion");
        }
        else {
            stack.getOrCreateTag().putString("Potion", identifier3.toString());
        }
        return stack;
    }
    
    public static ItemStack setCustomPotionEffects(final ItemStack stack, final Collection<StatusEffectInstance> effects) {
        if (effects.isEmpty()) {
            return stack;
        }
        final CompoundTag compoundTag3 = stack.getOrCreateTag();
        final ListTag listTag4 = compoundTag3.getList("CustomPotionEffects", 9);
        for (final StatusEffectInstance statusEffectInstance6 : effects) {
            ((AbstractList<CompoundTag>)listTag4).add(statusEffectInstance6.serialize(new CompoundTag()));
        }
        compoundTag3.put("CustomPotionEffects", listTag4);
        return stack;
    }
    
    @Environment(EnvType.CLIENT)
    public static void buildTooltip(final ItemStack stack, final List<TextComponent> list, final float float3) {
        final List<StatusEffectInstance> list2 = getPotionEffects(stack);
        final List<Pair<String, EntityAttributeModifier>> list3 = Lists.newArrayList();
        if (list2.isEmpty()) {
            list.add(new TranslatableTextComponent("effect.none", new Object[0]).applyFormat(TextFormat.h));
        }
        else {
            for (final StatusEffectInstance statusEffectInstance7 : list2) {
                final TextComponent textComponent8 = new TranslatableTextComponent(statusEffectInstance7.getTranslationKey(), new Object[0]);
                final StatusEffect statusEffect9 = statusEffectInstance7.getEffectType();
                final Map<EntityAttribute, EntityAttributeModifier> map10 = statusEffect9.getAttributeModifiers();
                if (!map10.isEmpty()) {
                    for (final Map.Entry<EntityAttribute, EntityAttributeModifier> entry12 : map10.entrySet()) {
                        final EntityAttributeModifier entityAttributeModifier13 = entry12.getValue();
                        final EntityAttributeModifier entityAttributeModifier14 = new EntityAttributeModifier(entityAttributeModifier13.getName(), statusEffect9.a(statusEffectInstance7.getAmplifier(), entityAttributeModifier13), entityAttributeModifier13.getOperation());
                        list3.add(new Pair<String, EntityAttributeModifier>(entry12.getKey().getId(), entityAttributeModifier14));
                    }
                }
                if (statusEffectInstance7.getAmplifier() > 0) {
                    textComponent8.append(" ").append(new TranslatableTextComponent("potion.potency." + statusEffectInstance7.getAmplifier(), new Object[0]));
                }
                if (statusEffectInstance7.getDuration() > 20) {
                    textComponent8.append(" (").append(StatusEffectUtil.durationToString(statusEffectInstance7, float3)).append(")");
                }
                list.add(textComponent8.applyFormat(statusEffect9.getType().getFormatting()));
            }
        }
        if (!list3.isEmpty()) {
            list.add(new StringTextComponent(""));
            list.add(new TranslatableTextComponent("potion.whenDrank", new Object[0]).applyFormat(TextFormat.f));
            for (final Pair<String, EntityAttributeModifier> pair7 : list3) {
                final EntityAttributeModifier entityAttributeModifier15 = pair7.getRight();
                final double double9 = entityAttributeModifier15.getAmount();
                double double10;
                if (entityAttributeModifier15.getOperation() == EntityAttributeModifier.Operation.b || entityAttributeModifier15.getOperation() == EntityAttributeModifier.Operation.c) {
                    double10 = entityAttributeModifier15.getAmount() * 100.0;
                }
                else {
                    double10 = entityAttributeModifier15.getAmount();
                }
                if (double9 > 0.0) {
                    list.add(new TranslatableTextComponent("attribute.modifier.plus." + entityAttributeModifier15.getOperation().getId(), new Object[] { ItemStack.MODIFIER_FORMAT.format(double10), new TranslatableTextComponent("attribute.name." + pair7.getLeft(), new Object[0]) }).applyFormat(TextFormat.j));
                }
                else {
                    if (double9 >= 0.0) {
                        continue;
                    }
                    double10 *= -1.0;
                    list.add(new TranslatableTextComponent("attribute.modifier.take." + entityAttributeModifier15.getOperation().getId(), new Object[] { ItemStack.MODIFIER_FORMAT.format(double10), new TranslatableTextComponent("attribute.name." + pair7.getLeft(), new Object[0]) }).applyFormat(TextFormat.m));
                }
            }
        }
    }
}

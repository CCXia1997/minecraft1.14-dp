package net.minecraft.enchantment;

import net.minecraft.entity.Entity;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.util.SystemUtil;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.damage.DamageSource;
import com.google.common.collect.Maps;
import net.minecraft.item.ItemStack;
import java.util.Map;
import net.minecraft.entity.LivingEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.registry.Registry;
import javax.annotation.Nullable;
import net.minecraft.entity.EquipmentSlot;

public abstract class Enchantment
{
    private final EquipmentSlot[] slotTypes;
    private final Weight weight;
    @Nullable
    public EnchantmentTarget type;
    @Nullable
    protected String translationName;
    
    @Nullable
    @Environment(EnvType.CLIENT)
    public static Enchantment byRawId(final int id) {
        return Registry.ENCHANTMENT.get(id);
    }
    
    protected Enchantment(final Weight weight, final EnchantmentTarget type, final EquipmentSlot[] slotTypes) {
        this.weight = weight;
        this.type = type;
        this.slotTypes = slotTypes;
    }
    
    public Map<EquipmentSlot, ItemStack> getEquipment(final LivingEntity livingEntity) {
        final Map<EquipmentSlot, ItemStack> map2 = Maps.newEnumMap(EquipmentSlot.class);
        for (final EquipmentSlot equipmentSlot6 : this.slotTypes) {
            final ItemStack itemStack7 = livingEntity.getEquippedStack(equipmentSlot6);
            if (!itemStack7.isEmpty()) {
                map2.put(equipmentSlot6, itemStack7);
            }
        }
        return map2;
    }
    
    public Weight getWeight() {
        return this.weight;
    }
    
    public int getMinimumLevel() {
        return 1;
    }
    
    public int getMaximumLevel() {
        return 1;
    }
    
    public int getMinimumPower(final int integer) {
        return 1 + integer * 10;
    }
    
    public int getProtectionAmount(final int level, final DamageSource source) {
        return 0;
    }
    
    public float getAttackDamage(final int level, final EntityGroup group) {
        return 0.0f;
    }
    
    public final boolean isDifferent(final Enchantment other) {
        return this.differs(other) && other.differs(this);
    }
    
    protected boolean differs(final Enchantment other) {
        return this != other;
    }
    
    protected String getOrCreateTranslationKey() {
        if (this.translationName == null) {
            this.translationName = SystemUtil.createTranslationKey("enchantment", Registry.ENCHANTMENT.getId(this));
        }
        return this.translationName;
    }
    
    public String getTranslationKey() {
        return this.getOrCreateTranslationKey();
    }
    
    public TextComponent getTextComponent(final int level) {
        final TextComponent textComponent2 = new TranslatableTextComponent(this.getTranslationKey(), new Object[0]);
        if (this.isCursed()) {
            textComponent2.applyFormat(TextFormat.m);
        }
        else {
            textComponent2.applyFormat(TextFormat.h);
        }
        if (level != 1 || this.getMaximumLevel() != 1) {
            textComponent2.append(" ").append(new TranslatableTextComponent("enchantment.level." + level, new Object[0]));
        }
        return textComponent2;
    }
    
    public boolean isAcceptableItem(final ItemStack stack) {
        return this.type.isAcceptableItem(stack.getItem());
    }
    
    public void onTargetDamaged(final LivingEntity user, final Entity target, final int level) {
    }
    
    public void onUserDamaged(final LivingEntity user, final Entity attacker, final int level) {
    }
    
    public boolean isTreasure() {
        return false;
    }
    
    public boolean isCursed() {
        return false;
    }
    
    public enum Weight
    {
        COMMON(30), 
        UNCOMMON(10), 
        RARE(3), 
        LEGENDARY(1);
        
        private final int weight;
        
        private Weight(final int weight) {
            this.weight = weight;
        }
        
        public int getWeight() {
            return this.weight;
        }
    }
}

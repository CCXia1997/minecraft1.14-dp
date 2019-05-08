package net.minecraft.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.entity.EquipmentSlot;

public interface ArmorMaterial
{
    int getDurability(final EquipmentSlot arg1);
    
    int getProtectionAmount(final EquipmentSlot arg1);
    
    int getEnchantability();
    
    SoundEvent getEquipSound();
    
    Ingredient getRepairIngredient();
    
    @Environment(EnvType.CLIENT)
    String getName();
    
    float getToughness();
}

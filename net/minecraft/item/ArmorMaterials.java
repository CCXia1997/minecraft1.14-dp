package net.minecraft.item;

import net.minecraft.sound.SoundEvents;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EquipmentSlot;
import java.util.function.Supplier;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Lazy;
import net.minecraft.sound.SoundEvent;

public enum ArmorMaterials implements ArmorMaterial
{
    a("leather", 5, new int[] { 1, 2, 3, 1 }, 15, SoundEvents.v, 0.0f, () -> Ingredient.ofItems(Items.kF)), 
    b("chainmail", 15, new int[] { 1, 4, 5, 2 }, 12, SoundEvents.p, 0.0f, () -> Ingredient.ofItems(Items.jk)), 
    c("iron", 15, new int[] { 2, 5, 6, 2 }, 9, SoundEvents.u, 0.0f, () -> Ingredient.ofItems(Items.jk)), 
    d("gold", 7, new int[] { 1, 3, 5, 2 }, 25, SoundEvents.t, 0.0f, () -> Ingredient.ofItems(Items.jl)), 
    e("diamond", 33, new int[] { 3, 6, 8, 3 }, 10, SoundEvents.q, 2.0f, () -> Ingredient.ofItems(Items.jj)), 
    f("turtle", 25, new int[] { 2, 5, 6, 2 }, 9, SoundEvents.w, 0.0f, () -> Ingredient.ofItems(Items.iZ));
    
    private static final int[] BASE_DURABILITY;
    private final String name;
    private final int durabilityMultiplier;
    private final int[] protectionAmounts;
    private final int enchantability;
    private final SoundEvent equipSound;
    private final float toughness;
    private final Lazy<Ingredient> repairIngredientSupplier;
    
    private ArmorMaterials(final String name, final int durabilityMultiplier, final int[] protectionAmounts, final int enchantability, final SoundEvent equipSound, final float toughness, final Supplier<Ingredient> ingredientSupplier) {
        this.name = name;
        this.durabilityMultiplier = durabilityMultiplier;
        this.protectionAmounts = protectionAmounts;
        this.enchantability = enchantability;
        this.equipSound = equipSound;
        this.toughness = toughness;
        this.repairIngredientSupplier = new Lazy<Ingredient>(ingredientSupplier);
    }
    
    @Override
    public int getDurability(final EquipmentSlot slot) {
        return ArmorMaterials.BASE_DURABILITY[slot.getEntitySlotId()] * this.durabilityMultiplier;
    }
    
    @Override
    public int getProtectionAmount(final EquipmentSlot slot) {
        return this.protectionAmounts[slot.getEntitySlotId()];
    }
    
    @Override
    public int getEnchantability() {
        return this.enchantability;
    }
    
    @Override
    public SoundEvent getEquipSound() {
        return this.equipSound;
    }
    
    @Override
    public Ingredient getRepairIngredient() {
        return this.repairIngredientSupplier.get();
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public float getToughness() {
        return this.toughness;
    }
    
    static {
        BASE_DURABILITY = new int[] { 13, 15, 16, 11 };
    }
}

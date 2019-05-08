package net.minecraft.item;

import net.minecraft.tag.ItemTags;
import net.minecraft.block.Blocks;
import java.util.function.Supplier;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Lazy;

public enum ToolMaterials implements ToolMaterial
{
    a(0, 59, 2.0f, 0.0f, 15, () -> Ingredient.fromTag(ItemTags.b)), 
    b(1, 131, 4.0f, 1.0f, 5, () -> Ingredient.ofItems(Blocks.m)), 
    c(2, 250, 6.0f, 2.0f, 14, () -> Ingredient.ofItems(Items.jk)), 
    d(3, 1561, 8.0f, 3.0f, 10, () -> Ingredient.ofItems(Items.jj)), 
    e(0, 32, 12.0f, 0.0f, 22, () -> Ingredient.ofItems(Items.jl));
    
    private final int miningLevel;
    private final int durability;
    private final float blockBreakSpeed;
    private final float attackDamage;
    private final int enchantability;
    private final Lazy<Ingredient> repairIngredient;
    
    private ToolMaterials(final int miningLevel, final int durability, final float blockBreakSpeed, final float attackDamage, final int enchantibility, final Supplier<Ingredient> supplier) {
        this.miningLevel = miningLevel;
        this.durability = durability;
        this.blockBreakSpeed = blockBreakSpeed;
        this.attackDamage = attackDamage;
        this.enchantability = enchantibility;
        this.repairIngredient = new Lazy<Ingredient>(supplier);
    }
    
    @Override
    public int getDurability() {
        return this.durability;
    }
    
    @Override
    public float getBlockBreakingSpeed() {
        return this.blockBreakSpeed;
    }
    
    @Override
    public float getAttackDamage() {
        return this.attackDamage;
    }
    
    @Override
    public int getMiningLevel() {
        return this.miningLevel;
    }
    
    @Override
    public int getEnchantability() {
        return this.enchantability;
    }
    
    @Override
    public Ingredient getRepairIngredient() {
        return this.repairIngredient.get();
    }
}

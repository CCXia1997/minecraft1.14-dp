package net.minecraft.item;

public class ToolItem extends Item
{
    private final ToolMaterial material;
    
    public ToolItem(final ToolMaterial material, final Settings settings) {
        super(settings.durabilityIfNotSet(material.getDurability()));
        this.material = material;
    }
    
    public ToolMaterial getType() {
        return this.material;
    }
    
    @Override
    public int getEnchantability() {
        return this.material.getEnchantability();
    }
    
    @Override
    public boolean canRepair(final ItemStack tool, final ItemStack ingredient) {
        return this.material.getRepairIngredient().a(ingredient) || super.canRepair(tool, ingredient);
    }
}

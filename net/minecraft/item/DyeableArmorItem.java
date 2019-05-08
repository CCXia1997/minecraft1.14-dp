package net.minecraft.item;

import net.minecraft.entity.EquipmentSlot;

public class DyeableArmorItem extends ArmorItem implements DyeableItem
{
    public DyeableArmorItem(final ArmorMaterial material, final EquipmentSlot slot, final Settings settings) {
        super(material, slot, settings);
    }
}

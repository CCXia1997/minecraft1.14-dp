package net.minecraft.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class NetherStarItem extends Item
{
    public NetherStarItem(final Settings settings) {
        super(settings);
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public boolean hasEnchantmentGlint(final ItemStack stack) {
        return true;
    }
}

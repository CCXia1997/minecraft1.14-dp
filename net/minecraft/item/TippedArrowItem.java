package net.minecraft.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.text.TextComponent;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.world.World;
import java.util.Iterator;
import net.minecraft.potion.Potion;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.DefaultedList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;

public class TippedArrowItem extends ArrowItem
{
    public TippedArrowItem(final Settings settings) {
        super(settings);
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public ItemStack getDefaultStack() {
        return PotionUtil.setPotion(super.getDefaultStack(), Potions.D);
    }
    
    @Override
    public void appendItemsForGroup(final ItemGroup itemGroup, final DefaultedList<ItemStack> defaultedList) {
        if (this.isInItemGroup(itemGroup)) {
            for (final Potion potion4 : Registry.POTION) {
                if (!potion4.getEffects().isEmpty()) {
                    defaultedList.add(PotionUtil.setPotion(new ItemStack(this), potion4));
                }
            }
        }
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void buildTooltip(final ItemStack stack, @Nullable final World world, final List<TextComponent> tooltip, final TooltipContext options) {
        PotionUtil.buildTooltip(stack, tooltip, 0.125f);
    }
    
    @Override
    public String getTranslationKey(final ItemStack stack) {
        return PotionUtil.getPotion(stack).getName(this.getTranslationKey() + ".effect.");
    }
}

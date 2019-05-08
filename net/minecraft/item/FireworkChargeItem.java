package net.minecraft.item;

import net.minecraft.util.DyeColor;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TranslatableTextComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.text.TextComponent;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.world.World;

public class FireworkChargeItem extends Item
{
    public FireworkChargeItem(final Settings settings) {
        super(settings);
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void buildTooltip(final ItemStack stack, @Nullable final World world, final List<TextComponent> tooltip, final TooltipContext options) {
        final CompoundTag compoundTag5 = stack.getSubCompoundTag("Explosion");
        if (compoundTag5 != null) {
            buildTooltip(compoundTag5, tooltip);
        }
    }
    
    @Environment(EnvType.CLIENT)
    public static void buildTooltip(final CompoundTag compoundTag, final List<TextComponent> list) {
        final FireworkItem.Type type3 = FireworkItem.Type.fromId(compoundTag.getByte("Type"));
        list.add(new TranslatableTextComponent("item.minecraft.firework_star.shape." + type3.getName(), new Object[0]).applyFormat(TextFormat.h));
        final int[] arr4 = compoundTag.getIntArray("Colors");
        if (arr4.length > 0) {
            list.add(appendColorNames(new StringTextComponent("").applyFormat(TextFormat.h), arr4));
        }
        final int[] arr5 = compoundTag.getIntArray("FadeColors");
        if (arr5.length > 0) {
            list.add(appendColorNames(new TranslatableTextComponent("item.minecraft.firework_star.fade_to", new Object[0]).append(" ").applyFormat(TextFormat.h), arr5));
        }
        if (compoundTag.getBoolean("Trail")) {
            list.add(new TranslatableTextComponent("item.minecraft.firework_star.trail", new Object[0]).applyFormat(TextFormat.h));
        }
        if (compoundTag.getBoolean("Flicker")) {
            list.add(new TranslatableTextComponent("item.minecraft.firework_star.flicker", new Object[0]).applyFormat(TextFormat.h));
        }
    }
    
    @Environment(EnvType.CLIENT)
    private static TextComponent appendColorNames(final TextComponent textComponent, final int[] colors) {
        for (int integer3 = 0; integer3 < colors.length; ++integer3) {
            if (integer3 > 0) {
                textComponent.append(", ");
            }
            textComponent.append(getColorTextComponent(colors[integer3]));
        }
        return textComponent;
    }
    
    @Environment(EnvType.CLIENT)
    private static TextComponent getColorTextComponent(final int colorRGB) {
        final DyeColor dyeColor2 = DyeColor.byFireworkColor(colorRGB);
        if (dyeColor2 == null) {
            return new TranslatableTextComponent("item.minecraft.firework_star.custom_color", new Object[0]);
        }
        return new TranslatableTextComponent("item.minecraft.firework_star." + dyeColor2.getName(), new Object[0]);
    }
}

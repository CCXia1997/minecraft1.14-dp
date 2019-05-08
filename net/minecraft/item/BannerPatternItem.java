package net.minecraft.item;

import net.minecraft.text.TranslatableTextComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.TextFormat;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.text.TextComponent;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.world.World;
import net.minecraft.block.entity.BannerPattern;

public class BannerPatternItem extends Item
{
    private final BannerPattern pattern;
    
    public BannerPatternItem(final BannerPattern pattern, final Settings settings) {
        super(settings);
        this.pattern = pattern;
    }
    
    public BannerPattern getPattern() {
        return this.pattern;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void buildTooltip(final ItemStack stack, @Nullable final World world, final List<TextComponent> tooltip, final TooltipContext options) {
        tooltip.add(this.nameTextComponent().applyFormat(TextFormat.h));
    }
    
    @Environment(EnvType.CLIENT)
    public TextComponent nameTextComponent() {
        return new TranslatableTextComponent(this.getTranslationKey() + ".desc", new Object[0]);
    }
}

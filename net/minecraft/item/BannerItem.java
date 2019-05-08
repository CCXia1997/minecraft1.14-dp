package net.minecraft.item;

import net.minecraft.client.item.TooltipContext;
import javax.annotation.Nullable;
import net.minecraft.world.World;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.util.DyeColor;
import net.minecraft.text.TextComponent;
import java.util.List;
import org.apache.commons.lang3.Validate;
import net.minecraft.block.AbstractBannerBlock;
import net.minecraft.block.Block;

public class BannerItem extends WallStandingBlockItem
{
    public BannerItem(final Block standingBlock, final Block wallBlock, final Settings settings) {
        super(standingBlock, wallBlock, settings);
        Validate.isInstanceOf((Class)AbstractBannerBlock.class, standingBlock);
        Validate.isInstanceOf((Class)AbstractBannerBlock.class, wallBlock);
    }
    
    @Environment(EnvType.CLIENT)
    public static void buildBannerTooltip(final ItemStack stack, final List<TextComponent> list) {
        final CompoundTag compoundTag3 = stack.getSubCompoundTag("BlockEntityTag");
        if (compoundTag3 == null || !compoundTag3.containsKey("Patterns")) {
            return;
        }
        final ListTag listTag4 = compoundTag3.getList("Patterns", 10);
        for (int integer5 = 0; integer5 < listTag4.size() && integer5 < 6; ++integer5) {
            final CompoundTag compoundTag4 = listTag4.getCompoundTag(integer5);
            final DyeColor dyeColor7 = DyeColor.byId(compoundTag4.getInt("Color"));
            final BannerPattern bannerPattern8 = BannerPattern.byId(compoundTag4.getString("Pattern"));
            if (bannerPattern8 != null) {
                list.add(new TranslatableTextComponent("block.minecraft.banner." + bannerPattern8.getName() + '.' + dyeColor7.getName(), new Object[0]).applyFormat(TextFormat.h));
            }
        }
    }
    
    public DyeColor getColor() {
        return ((AbstractBannerBlock)this.getBlock()).getColor();
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void buildTooltip(final ItemStack stack, @Nullable final World world, final List<TextComponent> tooltip, final TooltipContext options) {
        buildBannerTooltip(stack, tooltip);
    }
}

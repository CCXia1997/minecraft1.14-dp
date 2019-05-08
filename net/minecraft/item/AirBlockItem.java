package net.minecraft.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.BlockView;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.text.TextComponent;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.world.World;
import net.minecraft.block.Block;

public class AirBlockItem extends Item
{
    private final Block block;
    
    public AirBlockItem(final Block block, final Settings settings) {
        super(settings);
        this.block = block;
    }
    
    @Override
    public String getTranslationKey() {
        return this.block.getTranslationKey();
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void buildTooltip(final ItemStack stack, @Nullable final World world, final List<TextComponent> tooltip, final TooltipContext options) {
        super.buildTooltip(stack, world, tooltip, options);
        this.block.buildTooltip(stack, world, tooltip, options);
    }
}

package net.minecraft.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DyeColor;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.UseAction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.text.TextComponent;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.world.World;
import net.minecraft.block.DispenserBlock;
import net.minecraft.util.Identifier;

public class ShieldItem extends Item
{
    public ShieldItem(final Settings settings) {
        super(settings);
        this.addProperty(new Identifier("blocking"), (itemStack, world, livingEntity) -> (livingEntity != null && livingEntity.isUsingItem() && livingEntity.getActiveItem() == itemStack) ? 1.0f : 0.0f);
        DispenserBlock.registerBehavior(this, ArmorItem.DISPENSER_BEHAVIOR);
    }
    
    @Override
    public String getTranslationKey(final ItemStack stack) {
        if (stack.getSubCompoundTag("BlockEntityTag") != null) {
            return this.getTranslationKey() + '.' + getColor(stack).getName();
        }
        return super.getTranslationKey(stack);
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void buildTooltip(final ItemStack stack, @Nullable final World world, final List<TextComponent> tooltip, final TooltipContext options) {
        BannerItem.buildBannerTooltip(stack, tooltip);
    }
    
    @Override
    public UseAction getUseAction(final ItemStack stack) {
        return UseAction.d;
    }
    
    @Override
    public int getMaxUseTime(final ItemStack stack) {
        return 72000;
    }
    
    @Override
    public TypedActionResult<ItemStack> use(final World world, final PlayerEntity player, final Hand hand) {
        final ItemStack itemStack4 = player.getStackInHand(hand);
        player.setCurrentHand(hand);
        return new TypedActionResult<ItemStack>(ActionResult.a, itemStack4);
    }
    
    @Override
    public boolean canRepair(final ItemStack tool, final ItemStack ingredient) {
        return ItemTags.b.contains(ingredient.getItem()) || super.canRepair(tool, ingredient);
    }
    
    public static DyeColor getColor(final ItemStack stack) {
        return DyeColor.byId(stack.getOrCreateSubCompoundTag("BlockEntityTag").getInt("Base"));
    }
}

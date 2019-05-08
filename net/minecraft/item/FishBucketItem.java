package net.minecraft.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.entity.passive.TropicalFishEntity;
import net.minecraft.text.TextFormat;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.text.TextComponent;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.FishEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.IWorld;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.fluid.Fluid;
import net.minecraft.entity.EntityType;

public class FishBucketItem extends BucketItem
{
    private final EntityType<?> fishType;
    
    public FishBucketItem(final EntityType<?> fishType, final Fluid fluid, final Settings settings) {
        super(fluid, settings);
        this.fishType = fishType;
    }
    
    @Override
    public void onEmptied(final World world, final ItemStack stack, final BlockPos pos) {
        if (!world.isClient) {
            this.spawnFish(world, stack, pos);
        }
    }
    
    @Override
    protected void playEmptyingSound(@Nullable final PlayerEntity player, final IWorld world, final BlockPos pos) {
        world.playSound(player, pos, SoundEvents.ax, SoundCategory.g, 1.0f, 1.0f);
    }
    
    private void spawnFish(final World world, final ItemStack stack, final BlockPos pos) {
        final Entity entity4 = this.fishType.spawnFromItemStack(world, stack, null, pos, SpawnType.l, true, false);
        if (entity4 != null) {
            ((FishEntity)entity4).setFromBucket(true);
        }
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void buildTooltip(final ItemStack stack, @Nullable final World world, final List<TextComponent> tooltip, final TooltipContext options) {
        if (this.fishType == EntityType.TROPICAL_FISH) {
            final CompoundTag compoundTag5 = stack.getTag();
            if (compoundTag5 != null && compoundTag5.containsKey("BucketVariantTag", 3)) {
                final int integer6 = compoundTag5.getInt("BucketVariantTag");
                final TextFormat[] arr7 = { TextFormat.u, TextFormat.h };
                final String string8 = "color.minecraft." + TropicalFishEntity.getBaseDyeColor(integer6);
                final String string9 = "color.minecraft." + TropicalFishEntity.getPatternDyeColor(integer6);
                for (int integer7 = 0; integer7 < TropicalFishEntity.COMMON_VARIANTS.length; ++integer7) {
                    if (integer6 == TropicalFishEntity.COMMON_VARIANTS[integer7]) {
                        tooltip.add(new TranslatableTextComponent(TropicalFishEntity.getToolTipForVariant(integer7), new Object[0]).applyFormat(arr7));
                        return;
                    }
                }
                tooltip.add(new TranslatableTextComponent(TropicalFishEntity.getTranslationKey(integer6), new Object[0]).applyFormat(arr7));
                final TextComponent textComponent10 = new TranslatableTextComponent(string8, new Object[0]);
                if (!string8.equals(string9)) {
                    textComponent10.append(", ").append(new TranslatableTextComponent(string9, new Object[0]));
                }
                textComponent10.applyFormat(arr7);
                tooltip.add(textComponent10);
            }
        }
    }
}

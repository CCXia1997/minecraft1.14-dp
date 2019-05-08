package net.minecraft.item;

import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.StringTag;
import net.minecraft.entity.Entity;
import net.minecraft.text.TextFormatter;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.LecternBlock;
import net.minecraft.block.Blocks;
import net.minecraft.util.ActionResult;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.client.item.TooltipContext;
import java.util.List;
import net.minecraft.world.World;
import net.minecraft.text.StringTextComponent;
import net.minecraft.util.ChatUtil;
import net.minecraft.text.TextComponent;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;

public class WrittenBookItem extends Item
{
    public WrittenBookItem(final Settings settings) {
        super(settings);
    }
    
    public static boolean isValidBook(@Nullable final CompoundTag compoundTag) {
        if (!WritableBookItem.isValidBook(compoundTag)) {
            return false;
        }
        if (!compoundTag.containsKey("title", 8)) {
            return false;
        }
        final String string2 = compoundTag.getString("title");
        return string2.length() <= 32 && compoundTag.containsKey("author", 8);
    }
    
    public static int getBookGeneration(final ItemStack stack) {
        return stack.getTag().getInt("generation");
    }
    
    public static int getPageCount(final ItemStack itemStack) {
        final CompoundTag compoundTag2 = itemStack.getTag();
        return (compoundTag2 != null) ? compoundTag2.getList("pages", 8).size() : 0;
    }
    
    @Override
    public TextComponent getTranslatedNameTrimmed(final ItemStack stack) {
        if (stack.hasTag()) {
            final CompoundTag compoundTag2 = stack.getTag();
            final String string3 = compoundTag2.getString("title");
            if (!ChatUtil.isEmpty(string3)) {
                return new StringTextComponent(string3);
            }
        }
        return super.getTranslatedNameTrimmed(stack);
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void buildTooltip(final ItemStack stack, @Nullable final World world, final List<TextComponent> tooltip, final TooltipContext options) {
        if (stack.hasTag()) {
            final CompoundTag compoundTag5 = stack.getTag();
            final String string6 = compoundTag5.getString("author");
            if (!ChatUtil.isEmpty(string6)) {
                tooltip.add(new TranslatableTextComponent("book.byAuthor", new Object[] { string6 }).applyFormat(TextFormat.h));
            }
            tooltip.add(new TranslatableTextComponent("book.generation." + compoundTag5.getInt("generation"), new Object[0]).applyFormat(TextFormat.h));
        }
    }
    
    @Override
    public ActionResult useOnBlock(final ItemUsageContext usageContext) {
        final World world2 = usageContext.getWorld();
        final BlockPos blockPos3 = usageContext.getBlockPos();
        final BlockState blockState4 = world2.getBlockState(blockPos3);
        if (blockState4.getBlock() == Blocks.lQ) {
            return LecternBlock.putBookIfAbsent(world2, blockPos3, blockState4, usageContext.getItemStack()) ? ActionResult.a : ActionResult.PASS;
        }
        return ActionResult.PASS;
    }
    
    @Override
    public TypedActionResult<ItemStack> use(final World world, final PlayerEntity player, final Hand hand) {
        final ItemStack itemStack4 = player.getStackInHand(hand);
        player.openEditBookScreen(itemStack4, hand);
        player.incrementStat(Stats.c.getOrCreateStat(this));
        return new TypedActionResult<ItemStack>(ActionResult.a, itemStack4);
    }
    
    public static boolean resolve(final ItemStack book, @Nullable final ServerCommandSource commandSource, @Nullable final PlayerEntity player) {
        final CompoundTag compoundTag4 = book.getTag();
        if (compoundTag4 == null || compoundTag4.getBoolean("resolved")) {
            return false;
        }
        compoundTag4.putBoolean("resolved", true);
        if (!isValidBook(compoundTag4)) {
            return false;
        }
        final ListTag listTag5 = compoundTag4.getList("pages", 8);
        for (int integer6 = 0; integer6 < listTag5.size(); ++integer6) {
            final String string7 = listTag5.getString(integer6);
            TextComponent textComponent8;
            try {
                textComponent8 = TextComponent.Serializer.fromLenientJsonString(string7);
                textComponent8 = TextFormatter.resolveAndStyle(commandSource, textComponent8, player);
            }
            catch (Exception exception9) {
                textComponent8 = new StringTextComponent(string7);
            }
            listTag5.d(integer6, new StringTag(TextComponent.Serializer.toJsonString(textComponent8)));
        }
        compoundTag4.put("pages", listTag5);
        return true;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public boolean hasEnchantmentGlint(final ItemStack stack) {
        return true;
    }
}

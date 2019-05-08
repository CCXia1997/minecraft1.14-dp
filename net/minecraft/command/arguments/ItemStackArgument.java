package net.minecraft.command.arguments;

import net.minecraft.text.TranslatableTextComponent;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.util.registry.Registry;
import net.minecraft.item.ItemProvider;
import net.minecraft.nbt.Tag;
import net.minecraft.util.TagHelper;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.item.Item;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import net.minecraft.item.ItemStack;
import java.util.function.Predicate;

public class ItemStackArgument implements Predicate<ItemStack>
{
    private static final Dynamic2CommandExceptionType OVERSTACKED_EXCEPTION;
    private final Item item;
    @Nullable
    private final CompoundTag tag;
    
    public ItemStackArgument(final Item item, @Nullable final CompoundTag compoundTag) {
        this.item = item;
        this.tag = compoundTag;
    }
    
    public Item getItem() {
        return this.item;
    }
    
    public boolean a(final ItemStack itemStack) {
        return itemStack.getItem() == this.item && TagHelper.areTagsEqual(this.tag, itemStack.getTag(), true);
    }
    
    public ItemStack createStack(final int amount, final boolean checkOverstack) throws CommandSyntaxException {
        final ItemStack itemStack3 = new ItemStack(this.item, amount);
        if (this.tag != null) {
            itemStack3.setTag(this.tag);
        }
        if (checkOverstack && amount > itemStack3.getMaxAmount()) {
            throw ItemStackArgument.OVERSTACKED_EXCEPTION.create(Registry.ITEM.getId(this.item), itemStack3.getMaxAmount());
        }
        return itemStack3;
    }
    
    public String c() {
        final StringBuilder stringBuilder1 = new StringBuilder(Registry.ITEM.getRawId(this.item));
        if (this.tag != null) {
            stringBuilder1.append(this.tag);
        }
        return stringBuilder1.toString();
    }
    
    static {
        OVERSTACKED_EXCEPTION = new Dynamic2CommandExceptionType((object1, object2) -> new TranslatableTextComponent("arguments.item.overstacked", new Object[] { object1, object2 }));
    }
}

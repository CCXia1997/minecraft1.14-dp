package net.minecraft.command;

import net.minecraft.command.arguments.NbtPathArgumentType;
import net.minecraft.nbt.Tag;
import net.minecraft.text.TextComponent;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.CompoundTag;

public interface DataCommandObject
{
    void setTag(final CompoundTag arg1) throws CommandSyntaxException;
    
    CompoundTag getTag() throws CommandSyntaxException;
    
    TextComponent getModifiedFeedback();
    
    TextComponent getQueryFeedback(final Tag arg1);
    
    TextComponent getGetFeedback(final NbtPathArgumentType.NbtPath arg1, final double arg2, final int arg3);
}

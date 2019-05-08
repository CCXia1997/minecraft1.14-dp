package net.minecraft.text;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.Entity;
import javax.annotation.Nullable;
import net.minecraft.server.command.ServerCommandSource;

public interface TextComponentWithSelectors
{
    TextComponent resolve(@Nullable final ServerCommandSource arg1, @Nullable final Entity arg2) throws CommandSyntaxException;
}

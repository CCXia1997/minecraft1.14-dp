package net.minecraft.server.command;

import net.minecraft.text.Style;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.text.TextFormatter;
import net.minecraft.text.event.ClickEvent;
import net.minecraft.text.TextFormat;
import net.minecraft.text.StringTextComponent;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.CommandDispatcher;

public class SeedCommand
{
    public static void register(final CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("seed").requires(serverCommandSource -> serverCommandSource.getMinecraftServer().isSinglePlayer() || serverCommandSource.hasPermissionLevel(2))).executes(commandContext -> {
            final long long2 = ((ServerCommandSource)commandContext.getSource()).getWorld().getSeed();
            final long n;
            final TextComponent textComponent4 = TextFormatter.bracketed(new StringTextComponent(String.valueOf(long2)).modifyStyle(style3 -> style3.setColor(TextFormat.k).setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, String.valueOf(n))).setInsertion(String.valueOf(n))));
            ((ServerCommandSource)commandContext.getSource()).sendFeedback(new TranslatableTextComponent("commands.seed.success", new Object[] { textComponent4 }), false);
            return (int)long2;
        }));
    }
}

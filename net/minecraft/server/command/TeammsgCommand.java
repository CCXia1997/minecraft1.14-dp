package net.minecraft.server.command;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import java.util.Iterator;
import net.minecraft.text.Style;
import java.util.function.Consumer;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.event.ClickEvent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.text.event.HoverEvent;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.TextComponent;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.command.arguments.MessageArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class TeammsgCommand
{
    private static final SimpleCommandExceptionType NO_TEAM_EXCEPTION;
    
    public static void register(final CommandDispatcher<ServerCommandSource> dispatcher) {
        final LiteralCommandNode<ServerCommandSource> literalCommandNode2 = (LiteralCommandNode<ServerCommandSource>)dispatcher.register((LiteralArgumentBuilder)CommandManager.literal("teammsg").then(CommandManager.argument("message", (com.mojang.brigadier.arguments.ArgumentType<Object>)MessageArgumentType.create()).executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), MessageArgumentType.getMessage((CommandContext<ServerCommandSource>)commandContext, "message")))));
        dispatcher.register((LiteralArgumentBuilder)CommandManager.literal("tm").redirect((CommandNode)literalCommandNode2));
    }
    
    private static int execute(final ServerCommandSource source, final TextComponent message) throws CommandSyntaxException {
        final Entity entity3 = source.getEntityOrThrow();
        final Team team4 = (Team)entity3.getScoreboardTeam();
        if (team4 == null) {
            throw TeammsgCommand.NO_TEAM_EXCEPTION.create();
        }
        final HoverEvent hoverEvent;
        final Consumer<Style> consumer5 = style -> {
            new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableTextComponent("chat.type.team.hover", new Object[0]));
            style.setHoverEvent(hoverEvent).setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/teammsg "));
            return;
        };
        final TextComponent textComponent6 = team4.getFormattedName().modifyStyle(consumer5);
        for (final TextComponent textComponent7 : textComponent6.getSiblings()) {
            textComponent7.modifyStyle(consumer5);
        }
        final List<ServerPlayerEntity> list7 = source.getMinecraftServer().getPlayerManager().getPlayerList();
        for (final ServerPlayerEntity serverPlayerEntity9 : list7) {
            if (serverPlayerEntity9 == entity3) {
                serverPlayerEntity9.sendMessage(new TranslatableTextComponent("chat.type.team.sent", new Object[] { textComponent6, source.getDisplayName(), message.copy() }));
            }
            else {
                if (serverPlayerEntity9.getScoreboardTeam() != team4) {
                    continue;
                }
                serverPlayerEntity9.sendMessage(new TranslatableTextComponent("chat.type.team.text", new Object[] { textComponent6, source.getDisplayName(), message.copy() }));
            }
        }
        return list7.size();
    }
    
    static {
        NO_TEAM_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("commands.teammsg.failed.noteam", new Object[0]));
    }
}

package net.minecraft.server.command;

import org.apache.logging.log4j.LogManager;
import java.util.Collection;
import net.minecraft.text.Style;
import com.mojang.brigadier.context.CommandContext;
import java.util.function.Predicate;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.ArgumentBuilder;
import java.util.Iterator;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.command.suggestion.SuggestionProviders;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import java.util.Map;
import net.minecraft.network.Packet;
import net.minecraft.client.network.packet.CommandTreeS2CPacket;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import com.google.common.collect.Maps;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TextComponent;
import net.minecraft.text.event.HoverEvent;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.text.event.ClickEvent;
import net.minecraft.text.TextFormat;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextFormatter;
import net.minecraft.command.CommandException;
import com.mojang.brigadier.StringReader;
import net.minecraft.server.dedicated.command.WhitelistCommand;
import net.minecraft.server.dedicated.command.StopCommand;
import net.minecraft.server.dedicated.command.SetIdleTimeoutCommand;
import net.minecraft.server.dedicated.command.SaveOnCommand;
import net.minecraft.server.dedicated.command.SaveOffCommand;
import net.minecraft.server.dedicated.command.SaveAllCommand;
import net.minecraft.server.dedicated.command.PardonIpCommand;
import net.minecraft.server.dedicated.command.PardonCommand;
import net.minecraft.server.dedicated.command.OpCommand;
import net.minecraft.server.dedicated.command.DeOpCommand;
import net.minecraft.server.dedicated.command.BanCommand;
import net.minecraft.server.dedicated.command.BanListCommand;
import net.minecraft.server.dedicated.command.BanIpCommand;
import com.mojang.brigadier.CommandDispatcher;
import org.apache.logging.log4j.Logger;

public class CommandManager
{
    private static final Logger LOGGER;
    private final CommandDispatcher<ServerCommandSource> dispatcher;
    
    public CommandManager(final boolean isDedicatedServer) {
        AdvancementCommand.register(this.dispatcher = (CommandDispatcher<ServerCommandSource>)new CommandDispatcher());
        ExecuteCommand.register(this.dispatcher);
        BossBarCommand.register(this.dispatcher);
        ClearCommand.register(this.dispatcher);
        CloneCommand.register(this.dispatcher);
        DataCommand.register(this.dispatcher);
        DatapackCommand.register(this.dispatcher);
        DebugCommand.register(this.dispatcher);
        DefaultGameModeCommand.register(this.dispatcher);
        DifficultyCommand.register(this.dispatcher);
        EffectCommand.register(this.dispatcher);
        MeCommand.register(this.dispatcher);
        EnchantCommand.register(this.dispatcher);
        ExperienceCommand.register(this.dispatcher);
        FillCommand.register(this.dispatcher);
        ForceLoadCommand.register(this.dispatcher);
        FunctionCommand.register(this.dispatcher);
        GameModeCommand.register(this.dispatcher);
        GameRuleCommand.register(this.dispatcher);
        GiveCommand.register(this.dispatcher);
        HelpCommand.register(this.dispatcher);
        KickCommand.register(this.dispatcher);
        KillCommand.register(this.dispatcher);
        ListCommand.register(this.dispatcher);
        LocateCommand.register(this.dispatcher);
        LootCommand.register(this.dispatcher);
        MessageCommand.register(this.dispatcher);
        ParticleCommand.register(this.dispatcher);
        PlaySoundCommand.register(this.dispatcher);
        PublishCommand.register(this.dispatcher);
        ReloadCommand.register(this.dispatcher);
        RecipeCommand.register(this.dispatcher);
        ReplaceItemCommand.register(this.dispatcher);
        SayCommand.register(this.dispatcher);
        ScheduleCommand.register(this.dispatcher);
        ScoreboardCommand.register(this.dispatcher);
        SeedCommand.register(this.dispatcher);
        SetBlockCommand.register(this.dispatcher);
        SpawnPointCommand.register(this.dispatcher);
        SetWorldSpawnCommand.register(this.dispatcher);
        SpreadPlayersCommand.register(this.dispatcher);
        StopSoundCommand.register(this.dispatcher);
        SummonCommand.register(this.dispatcher);
        TagCommand.register(this.dispatcher);
        TeamCommand.register(this.dispatcher);
        TeammsgCommand.register(this.dispatcher);
        TeleportCommand.register(this.dispatcher);
        TellRawCommand.register(this.dispatcher);
        TimeCommand.register(this.dispatcher);
        TitleCommand.register(this.dispatcher);
        TriggerCommand.register(this.dispatcher);
        WeatherCommand.register(this.dispatcher);
        WorldBorderCommand.register(this.dispatcher);
        if (isDedicatedServer) {
            BanIpCommand.register(this.dispatcher);
            BanListCommand.register(this.dispatcher);
            BanCommand.register(this.dispatcher);
            DeOpCommand.register(this.dispatcher);
            OpCommand.register(this.dispatcher);
            PardonCommand.register(this.dispatcher);
            PardonIpCommand.register(this.dispatcher);
            SaveAllCommand.register(this.dispatcher);
            SaveOffCommand.register(this.dispatcher);
            SaveOnCommand.register(this.dispatcher);
            SetIdleTimeoutCommand.register(this.dispatcher);
            StopCommand.register(this.dispatcher);
            WhitelistCommand.register(this.dispatcher);
        }
        this.dispatcher.findAmbiguities((commandNode1, commandNode2, commandNode3, collection) -> CommandManager.LOGGER.warn("Ambiguity between arguments {} and {} with inputs: {}", this.dispatcher.getPath(commandNode2), this.dispatcher.getPath(commandNode3), collection));
        this.dispatcher.setConsumer((commandContext, boolean2, integer) -> ((ServerCommandSource)commandContext.getSource()).onCommandComplete((CommandContext<ServerCommandSource>)commandContext, boolean2, integer));
    }
    
    public int execute(final ServerCommandSource commandSource, final String string) {
        final StringReader stringReader3 = new StringReader(string);
        if (stringReader3.canRead() && stringReader3.peek() == '/') {
            stringReader3.skip();
        }
        commandSource.getMinecraftServer().getProfiler().push(string);
        try {
            return this.dispatcher.execute(stringReader3, commandSource);
        }
        catch (CommandException commandException4) {
            commandSource.sendError(commandException4.getMessageComponent());
            return 0;
        }
        catch (CommandSyntaxException commandSyntaxException4) {
            commandSource.sendError(TextFormatter.message(commandSyntaxException4.getRawMessage()));
            if (commandSyntaxException4.getInput() != null && commandSyntaxException4.getCursor() >= 0) {
                final int integer5 = Math.min(commandSyntaxException4.getInput().length(), commandSyntaxException4.getCursor());
                final TextComponent textComponent6 = new StringTextComponent("").applyFormat(TextFormat.h).modifyStyle(style -> style.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, string)));
                if (integer5 > 10) {
                    textComponent6.append("...");
                }
                textComponent6.append(commandSyntaxException4.getInput().substring(Math.max(0, integer5 - 10), integer5));
                if (integer5 < commandSyntaxException4.getInput().length()) {
                    final TextComponent textComponent7 = new StringTextComponent(commandSyntaxException4.getInput().substring(integer5)).applyFormat(TextFormat.m, TextFormat.t);
                    textComponent6.append(textComponent7);
                }
                textComponent6.append(new TranslatableTextComponent("command.context.here", new Object[0]).applyFormat(TextFormat.m, TextFormat.u));
                commandSource.sendError(textComponent6);
            }
            return 0;
        }
        catch (Exception exception4) {
            final TextComponent textComponent8 = new StringTextComponent((exception4.getMessage() == null) ? exception4.getClass().getName() : exception4.getMessage());
            if (CommandManager.LOGGER.isDebugEnabled()) {
                final StackTraceElement[] arr6 = exception4.getStackTrace();
                for (int integer6 = 0; integer6 < Math.min(arr6.length, 3); ++integer6) {
                    textComponent8.append("\n\n").append(arr6[integer6].getMethodName()).append("\n ").append(arr6[integer6].getFileName()).append(":").append(String.valueOf(arr6[integer6].getLineNumber()));
                }
            }
            commandSource.sendError(new TranslatableTextComponent("command.failed", new Object[0]).modifyStyle(style -> style.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, textComponent8))));
            return 0;
        }
        finally {
            commandSource.getMinecraftServer().getProfiler().pop();
        }
    }
    
    public void sendCommandTree(final ServerPlayerEntity serverPlayerEntity) {
        final Map<CommandNode<ServerCommandSource>, CommandNode<CommandSource>> map2 = Maps.newHashMap();
        final RootCommandNode<CommandSource> rootCommandNode3 = (RootCommandNode<CommandSource>)new RootCommandNode();
        map2.put((CommandNode<ServerCommandSource>)this.dispatcher.getRoot(), (CommandNode<CommandSource>)rootCommandNode3);
        this.makeTreeForSource((CommandNode<ServerCommandSource>)this.dispatcher.getRoot(), (CommandNode<CommandSource>)rootCommandNode3, serverPlayerEntity.getCommandSource(), map2);
        serverPlayerEntity.networkHandler.sendPacket(new CommandTreeS2CPacket(rootCommandNode3));
    }
    
    private void makeTreeForSource(final CommandNode<ServerCommandSource> tree, final CommandNode<CommandSource> result, final ServerCommandSource source, final Map<CommandNode<ServerCommandSource>, CommandNode<CommandSource>> resultNodes) {
        for (final CommandNode<ServerCommandSource> commandNode6 : tree.getChildren()) {
            if (commandNode6.canUse(source)) {
                final ArgumentBuilder<CommandSource, ?> argumentBuilder7 = commandNode6.createBuilder();
                argumentBuilder7.requires(commandSource -> true);
                if (argumentBuilder7.getCommand() != null) {
                    argumentBuilder7.executes(commandContext -> 0);
                }
                if (argumentBuilder7 instanceof RequiredArgumentBuilder) {
                    final RequiredArgumentBuilder<CommandSource, ?> requiredArgumentBuilder8 = argumentBuilder7;
                    if (requiredArgumentBuilder8.getSuggestionsProvider() != null) {
                        requiredArgumentBuilder8.suggests((SuggestionProvider)SuggestionProviders.getLocalProvider((SuggestionProvider<CommandSource>)requiredArgumentBuilder8.getSuggestionsProvider()));
                    }
                }
                if (argumentBuilder7.getRedirect() != null) {
                    argumentBuilder7.redirect((CommandNode)resultNodes.get(argumentBuilder7.getRedirect()));
                }
                final CommandNode<CommandSource> commandNode7 = (CommandNode<CommandSource>)argumentBuilder7.build();
                resultNodes.put(commandNode6, commandNode7);
                result.addChild((CommandNode)commandNode7);
                if (commandNode6.getChildren().isEmpty()) {
                    continue;
                }
                this.makeTreeForSource(commandNode6, commandNode7, source, resultNodes);
            }
        }
    }
    
    public static LiteralArgumentBuilder<ServerCommandSource> literal(final String string) {
        return (LiteralArgumentBuilder<ServerCommandSource>)LiteralArgumentBuilder.literal(string);
    }
    
    public static <T> RequiredArgumentBuilder<ServerCommandSource, T> argument(final String name, final ArgumentType<T> type) {
        return (RequiredArgumentBuilder<ServerCommandSource, T>)RequiredArgumentBuilder.argument(name, (ArgumentType)type);
    }
    
    public static Predicate<String> getCommandValidator(final CommandParser commandParser) {
        return string -> {
            try {
                commandParser.parse(new StringReader(string));
                return true;
            }
            catch (CommandSyntaxException commandSyntaxException3) {
                return false;
            }
        };
    }
    
    public CommandDispatcher<ServerCommandSource> getDispatcher() {
        return this.dispatcher;
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
    
    @FunctionalInterface
    public interface CommandParser
    {
        void parse(final StringReader arg1) throws CommandSyntaxException;
    }
}

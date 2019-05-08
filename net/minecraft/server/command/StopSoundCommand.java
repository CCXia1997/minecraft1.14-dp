package net.minecraft.server.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.context.CommandContext;
import java.util.Iterator;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.network.Packet;
import net.minecraft.client.network.packet.StopSoundS2CPacket;
import net.minecraft.util.Identifier;
import javax.annotation.Nullable;
import net.minecraft.server.network.ServerPlayerEntity;
import java.util.Collection;
import net.minecraft.command.EntitySelector;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.sound.SoundCategory;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.command.arguments.IdentifierArgumentType;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.command.arguments.EntityArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.CommandDispatcher;

public class StopSoundCommand
{
    public static void register(final CommandDispatcher<ServerCommandSource> dispatcher) {
        final RequiredArgumentBuilder<ServerCommandSource, EntitySelector> requiredArgumentBuilder2 = (RequiredArgumentBuilder<ServerCommandSource, EntitySelector>)((RequiredArgumentBuilder)CommandManager.argument("targets", (com.mojang.brigadier.arguments.ArgumentType<Object>)EntityArgumentType.players()).executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), null, null))).then(CommandManager.literal("*").then(CommandManager.argument("sound", (com.mojang.brigadier.arguments.ArgumentType<Object>)IdentifierArgumentType.create()).suggests((SuggestionProvider)SuggestionProviders.AVAILABLE_SOUNDS).executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), null, IdentifierArgumentType.getIdentifier((CommandContext<ServerCommandSource>)commandContext, "sound")))));
        for (final SoundCategory soundCategory6 : SoundCategory.values()) {
            requiredArgumentBuilder2.then(((LiteralArgumentBuilder)CommandManager.literal(soundCategory6.getName()).executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), soundCategory6, null))).then(CommandManager.argument("sound", (com.mojang.brigadier.arguments.ArgumentType<Object>)IdentifierArgumentType.create()).suggests((SuggestionProvider)SuggestionProviders.AVAILABLE_SOUNDS).executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), soundCategory6, IdentifierArgumentType.getIdentifier((CommandContext<ServerCommandSource>)commandContext, "sound")))));
        }
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("stopsound").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).then((ArgumentBuilder)requiredArgumentBuilder2));
    }
    
    private static int execute(final ServerCommandSource source, final Collection<ServerPlayerEntity> targets, @Nullable final SoundCategory category, @Nullable final Identifier sound) {
        final StopSoundS2CPacket stopSoundS2CPacket5 = new StopSoundS2CPacket(sound, category);
        for (final ServerPlayerEntity serverPlayerEntity7 : targets) {
            serverPlayerEntity7.networkHandler.sendPacket(stopSoundS2CPacket5);
        }
        if (category != null) {
            if (sound != null) {
                source.sendFeedback(new TranslatableTextComponent("commands.stopsound.success.source.sound", new Object[] { sound, category.getName() }), true);
            }
            else {
                source.sendFeedback(new TranslatableTextComponent("commands.stopsound.success.source.any", new Object[] { category.getName() }), true);
            }
        }
        else if (sound != null) {
            source.sendFeedback(new TranslatableTextComponent("commands.stopsound.success.sourceless.sound", new Object[] { sound }), true);
        }
        else {
            source.sendFeedback(new TranslatableTextComponent("commands.stopsound.success.sourceless.any", new Object[0]), true);
        }
        return targets.size();
    }
}

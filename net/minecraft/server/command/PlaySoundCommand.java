package net.minecraft.server.command;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Iterator;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.network.Packet;
import net.minecraft.client.network.packet.PlaySoundIdS2CPacket;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.server.network.ServerPlayerEntity;
import java.util.Collection;
import com.mojang.brigadier.arguments.FloatArgumentType;
import net.minecraft.command.arguments.Vec3ArgumentType;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.util.Identifier;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.sound.SoundCategory;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.command.suggestion.SuggestionProviders;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.command.arguments.IdentifierArgumentType;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class PlaySoundCommand
{
    private static final SimpleCommandExceptionType FAILED_EXCEPTION;
    
    public static void register(final CommandDispatcher<ServerCommandSource> dispatcher) {
        final RequiredArgumentBuilder<ServerCommandSource, Identifier> requiredArgumentBuilder2 = (RequiredArgumentBuilder<ServerCommandSource, Identifier>)CommandManager.argument("sound", (com.mojang.brigadier.arguments.ArgumentType<Object>)IdentifierArgumentType.create()).suggests((SuggestionProvider)SuggestionProviders.AVAILABLE_SOUNDS);
        for (final SoundCategory soundCategory6 : SoundCategory.values()) {
            requiredArgumentBuilder2.then((ArgumentBuilder)makeArgumentsForCategory(soundCategory6));
        }
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("playsound").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).then((ArgumentBuilder)requiredArgumentBuilder2));
    }
    
    private static LiteralArgumentBuilder<ServerCommandSource> makeArgumentsForCategory(final SoundCategory category) {
        return (LiteralArgumentBuilder<ServerCommandSource>)CommandManager.literal(category.getName()).then(((RequiredArgumentBuilder)CommandManager.argument("targets", (com.mojang.brigadier.arguments.ArgumentType<Object>)EntityArgumentType.players()).executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), IdentifierArgumentType.getIdentifier((CommandContext<ServerCommandSource>)commandContext, "sound"), category, ((ServerCommandSource)commandContext.getSource()).getPosition(), 1.0f, 1.0f, 0.0f))).then(((RequiredArgumentBuilder)CommandManager.argument("pos", (com.mojang.brigadier.arguments.ArgumentType<Object>)Vec3ArgumentType.create()).executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), IdentifierArgumentType.getIdentifier((CommandContext<ServerCommandSource>)commandContext, "sound"), category, Vec3ArgumentType.getVec3((CommandContext<ServerCommandSource>)commandContext, "pos"), 1.0f, 1.0f, 0.0f))).then(((RequiredArgumentBuilder)CommandManager.argument("volume", (com.mojang.brigadier.arguments.ArgumentType<Object>)FloatArgumentType.floatArg(0.0f)).executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), IdentifierArgumentType.getIdentifier((CommandContext<ServerCommandSource>)commandContext, "sound"), category, Vec3ArgumentType.getVec3((CommandContext<ServerCommandSource>)commandContext, "pos"), (float)commandContext.getArgument("volume", (Class)Float.class), 1.0f, 0.0f))).then(((RequiredArgumentBuilder)CommandManager.argument("pitch", (com.mojang.brigadier.arguments.ArgumentType<Object>)FloatArgumentType.floatArg(0.0f, 2.0f)).executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), IdentifierArgumentType.getIdentifier((CommandContext<ServerCommandSource>)commandContext, "sound"), category, Vec3ArgumentType.getVec3((CommandContext<ServerCommandSource>)commandContext, "pos"), (float)commandContext.getArgument("volume", (Class)Float.class), (float)commandContext.getArgument("pitch", (Class)Float.class), 0.0f))).then(CommandManager.argument("minVolume", (com.mojang.brigadier.arguments.ArgumentType<Object>)FloatArgumentType.floatArg(0.0f, 1.0f)).executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), IdentifierArgumentType.getIdentifier((CommandContext<ServerCommandSource>)commandContext, "sound"), category, Vec3ArgumentType.getVec3((CommandContext<ServerCommandSource>)commandContext, "pos"), (float)commandContext.getArgument("volume", (Class)Float.class), (float)commandContext.getArgument("pitch", (Class)Float.class), (float)commandContext.getArgument("minVolume", (Class)Float.class))))))));
    }
    
    private static int execute(final ServerCommandSource source, final Collection<ServerPlayerEntity> targets, final Identifier sound, final SoundCategory category, final Vec3d pos, final float volume, final float pitch, final float minVolume) throws CommandSyntaxException {
        final double double9 = Math.pow((volume > 1.0f) ? ((double)(volume * 16.0f)) : 16.0, 2.0);
        int integer11 = 0;
        for (final ServerPlayerEntity serverPlayerEntity13 : targets) {
            final double double10 = pos.x - serverPlayerEntity13.x;
            final double double11 = pos.y - serverPlayerEntity13.y;
            final double double12 = pos.z - serverPlayerEntity13.z;
            final double double13 = double10 * double10 + double11 * double11 + double12 * double12;
            Vec3d vec3d22 = pos;
            float float23 = volume;
            if (double13 > double9) {
                if (minVolume <= 0.0f) {
                    continue;
                }
                final double double14 = MathHelper.sqrt(double13);
                vec3d22 = new Vec3d(serverPlayerEntity13.x + double10 / double14 * 2.0, serverPlayerEntity13.y + double11 / double14 * 2.0, serverPlayerEntity13.z + double12 / double14 * 2.0);
                float23 = minVolume;
            }
            serverPlayerEntity13.networkHandler.sendPacket(new PlaySoundIdS2CPacket(sound, category, vec3d22, float23, pitch));
            ++integer11;
        }
        if (integer11 == 0) {
            throw PlaySoundCommand.FAILED_EXCEPTION.create();
        }
        if (targets.size() == 1) {
            source.sendFeedback(new TranslatableTextComponent("commands.playsound.success.single", new Object[] { sound, targets.iterator().next().getDisplayName() }), true);
        }
        else {
            source.sendFeedback(new TranslatableTextComponent("commands.playsound.success.single", new Object[] { sound, targets.iterator().next().getDisplayName() }), true);
        }
        return integer11;
    }
    
    static {
        FAILED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("commands.playsound.failed", new Object[0]));
    }
}

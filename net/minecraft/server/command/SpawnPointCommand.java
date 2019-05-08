package net.minecraft.server.command;

import java.util.Collections;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.context.CommandContext;
import java.util.Iterator;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.server.network.ServerPlayerEntity;
import java.util.Collection;
import net.minecraft.command.arguments.BlockPosArgumentType;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.command.arguments.EntityArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.CommandDispatcher;

public class SpawnPointCommand
{
    public static void register(final CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("spawnpoint").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), Collections.<ServerPlayerEntity>singleton(((ServerCommandSource)commandContext.getSource()).getPlayer()), new BlockPos(((ServerCommandSource)commandContext.getSource()).getPosition())))).then(((RequiredArgumentBuilder)CommandManager.argument("targets", (com.mojang.brigadier.arguments.ArgumentType<Object>)EntityArgumentType.players()).executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), new BlockPos(((ServerCommandSource)commandContext.getSource()).getPosition())))).then(CommandManager.argument("pos", (com.mojang.brigadier.arguments.ArgumentType<Object>)BlockPosArgumentType.create()).executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), BlockPosArgumentType.getBlockPos((CommandContext<ServerCommandSource>)commandContext, "pos"))))));
    }
    
    private static int execute(final ServerCommandSource source, final Collection<ServerPlayerEntity> targets, final BlockPos pos) {
        for (final ServerPlayerEntity serverPlayerEntity5 : targets) {
            serverPlayerEntity5.setPlayerSpawn(pos, true);
        }
        if (targets.size() == 1) {
            source.sendFeedback(new TranslatableTextComponent("commands.spawnpoint.success.single", new Object[] { pos.getX(), pos.getY(), pos.getZ(), targets.iterator().next().getDisplayName() }), true);
        }
        else {
            source.sendFeedback(new TranslatableTextComponent("commands.spawnpoint.success.multiple", new Object[] { pos.getX(), pos.getY(), pos.getZ(), targets.size() }), true);
        }
        return targets.size();
    }
}

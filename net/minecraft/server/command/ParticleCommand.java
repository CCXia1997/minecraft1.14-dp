package net.minecraft.server.command;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Iterator;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;
import java.util.Collection;
import net.minecraft.util.math.Vec3d;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.command.arguments.EntityArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import net.minecraft.command.arguments.Vec3ArgumentType;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.command.arguments.ParticleArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class ParticleCommand
{
    private static final SimpleCommandExceptionType FAILED_EXCPETION;
    
    public static void register(final CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("particle").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).then(((RequiredArgumentBuilder)CommandManager.argument("name", (com.mojang.brigadier.arguments.ArgumentType<Object>)ParticleArgumentType.create()).executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), ParticleArgumentType.getParticle((CommandContext<ServerCommandSource>)commandContext, "name"), ((ServerCommandSource)commandContext.getSource()).getPosition(), Vec3d.ZERO, 0.0f, 0, false, ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getPlayerManager().getPlayerList()))).then(((RequiredArgumentBuilder)CommandManager.argument("pos", (com.mojang.brigadier.arguments.ArgumentType<Object>)Vec3ArgumentType.create()).executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), ParticleArgumentType.getParticle((CommandContext<ServerCommandSource>)commandContext, "name"), Vec3ArgumentType.getVec3((CommandContext<ServerCommandSource>)commandContext, "pos"), Vec3d.ZERO, 0.0f, 0, false, ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getPlayerManager().getPlayerList()))).then(CommandManager.argument("delta", (com.mojang.brigadier.arguments.ArgumentType<Object>)Vec3ArgumentType.create(false)).then(CommandManager.argument("speed", (com.mojang.brigadier.arguments.ArgumentType<Object>)FloatArgumentType.floatArg(0.0f)).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("count", (com.mojang.brigadier.arguments.ArgumentType<Object>)IntegerArgumentType.integer(0)).executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), ParticleArgumentType.getParticle((CommandContext<ServerCommandSource>)commandContext, "name"), Vec3ArgumentType.getVec3((CommandContext<ServerCommandSource>)commandContext, "pos"), Vec3ArgumentType.getVec3((CommandContext<ServerCommandSource>)commandContext, "delta"), FloatArgumentType.getFloat(commandContext, "speed"), IntegerArgumentType.getInteger(commandContext, "count"), false, ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getPlayerManager().getPlayerList()))).then(((LiteralArgumentBuilder)CommandManager.literal("force").executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), ParticleArgumentType.getParticle((CommandContext<ServerCommandSource>)commandContext, "name"), Vec3ArgumentType.getVec3((CommandContext<ServerCommandSource>)commandContext, "pos"), Vec3ArgumentType.getVec3((CommandContext<ServerCommandSource>)commandContext, "delta"), FloatArgumentType.getFloat(commandContext, "speed"), IntegerArgumentType.getInteger(commandContext, "count"), true, ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getPlayerManager().getPlayerList()))).then(CommandManager.argument("viewers", (com.mojang.brigadier.arguments.ArgumentType<Object>)EntityArgumentType.players()).executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), ParticleArgumentType.getParticle((CommandContext<ServerCommandSource>)commandContext, "name"), Vec3ArgumentType.getVec3((CommandContext<ServerCommandSource>)commandContext, "pos"), Vec3ArgumentType.getVec3((CommandContext<ServerCommandSource>)commandContext, "delta"), FloatArgumentType.getFloat(commandContext, "speed"), IntegerArgumentType.getInteger(commandContext, "count"), true, EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "viewers")))))).then(((LiteralArgumentBuilder)CommandManager.literal("normal").executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), ParticleArgumentType.getParticle((CommandContext<ServerCommandSource>)commandContext, "name"), Vec3ArgumentType.getVec3((CommandContext<ServerCommandSource>)commandContext, "pos"), Vec3ArgumentType.getVec3((CommandContext<ServerCommandSource>)commandContext, "delta"), FloatArgumentType.getFloat(commandContext, "speed"), IntegerArgumentType.getInteger(commandContext, "count"), false, ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getPlayerManager().getPlayerList()))).then(CommandManager.argument("viewers", (com.mojang.brigadier.arguments.ArgumentType<Object>)EntityArgumentType.players()).executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), ParticleArgumentType.getParticle((CommandContext<ServerCommandSource>)commandContext, "name"), Vec3ArgumentType.getVec3((CommandContext<ServerCommandSource>)commandContext, "pos"), Vec3ArgumentType.getVec3((CommandContext<ServerCommandSource>)commandContext, "delta"), FloatArgumentType.getFloat(commandContext, "speed"), IntegerArgumentType.getInteger(commandContext, "count"), false, EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "viewers")))))))))));
    }
    
    private static int execute(final ServerCommandSource source, final ParticleParameters parameters, final Vec3d pos, final Vec3d delta, final float speed, final int count, final boolean force, final Collection<ServerPlayerEntity> viewers) throws CommandSyntaxException {
        int integer9 = 0;
        for (final ServerPlayerEntity serverPlayerEntity11 : viewers) {
            if (source.getWorld().<ParticleParameters>spawnParticles(serverPlayerEntity11, parameters, force, pos.x, pos.y, pos.z, count, delta.x, delta.y, delta.z, speed)) {
                ++integer9;
            }
        }
        if (integer9 == 0) {
            throw ParticleCommand.FAILED_EXCPETION.create();
        }
        source.sendFeedback(new TranslatableTextComponent("commands.particle.success", new Object[] { Registry.PARTICLE_TYPE.getId(parameters.getType()).toString() }), true);
        return integer9;
    }
    
    static {
        FAILED_EXCPETION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("commands.particle.failed", new Object[0]));
    }
}

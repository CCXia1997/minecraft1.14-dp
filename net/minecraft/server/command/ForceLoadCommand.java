package net.minecraft.server.command;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.arguments.BlockPosArgumentType;
import net.minecraft.server.world.ServerWorld;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Iterator;
import java.util.function.Function;
import com.google.common.base.Joiner;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.util.math.ColumnPos;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.command.arguments.ColumnPosArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;

public class ForceLoadCommand
{
    private static final Dynamic2CommandExceptionType TOOBIG_EXCEPTION;
    private static final Dynamic2CommandExceptionType QUERY_FAILURE_EXCEPTION;
    private static final SimpleCommandExceptionType ADDED_FAILURE_EXCEPTION;
    private static final SimpleCommandExceptionType REMOVED_FAILURE_EXCEPTION;
    
    public static void register(final CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("forceload").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).then(((LiteralArgumentBuilder)CommandManager.literal("add").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(4))).then(((RequiredArgumentBuilder)CommandManager.argument("from", (com.mojang.brigadier.arguments.ArgumentType<Object>)ColumnPosArgumentType.create()).executes(commandContext -> executeChange((ServerCommandSource)commandContext.getSource(), ColumnPosArgumentType.getColumnPos((CommandContext<ServerCommandSource>)commandContext, "from"), ColumnPosArgumentType.getColumnPos((CommandContext<ServerCommandSource>)commandContext, "from"), true))).then(CommandManager.argument("to", (com.mojang.brigadier.arguments.ArgumentType<Object>)ColumnPosArgumentType.create()).executes(commandContext -> executeChange((ServerCommandSource)commandContext.getSource(), ColumnPosArgumentType.getColumnPos((CommandContext<ServerCommandSource>)commandContext, "from"), ColumnPosArgumentType.getColumnPos((CommandContext<ServerCommandSource>)commandContext, "to"), true)))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("remove").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(4))).then(((RequiredArgumentBuilder)CommandManager.argument("from", (com.mojang.brigadier.arguments.ArgumentType<Object>)ColumnPosArgumentType.create()).executes(commandContext -> executeChange((ServerCommandSource)commandContext.getSource(), ColumnPosArgumentType.getColumnPos((CommandContext<ServerCommandSource>)commandContext, "from"), ColumnPosArgumentType.getColumnPos((CommandContext<ServerCommandSource>)commandContext, "from"), false))).then(CommandManager.argument("to", (com.mojang.brigadier.arguments.ArgumentType<Object>)ColumnPosArgumentType.create()).executes(commandContext -> executeChange((ServerCommandSource)commandContext.getSource(), ColumnPosArgumentType.getColumnPos((CommandContext<ServerCommandSource>)commandContext, "from"), ColumnPosArgumentType.getColumnPos((CommandContext<ServerCommandSource>)commandContext, "to"), false))))).then(CommandManager.literal("all").executes(commandContext -> executeRemoveAll((ServerCommandSource)commandContext.getSource()))))).then(((LiteralArgumentBuilder)CommandManager.literal("query").executes(commandContext -> executeQuery((ServerCommandSource)commandContext.getSource()))).then(CommandManager.argument("pos", (com.mojang.brigadier.arguments.ArgumentType<Object>)ColumnPosArgumentType.create()).executes(commandContext -> executeQuery((ServerCommandSource)commandContext.getSource(), ColumnPosArgumentType.getColumnPos((CommandContext<ServerCommandSource>)commandContext, "pos"))))));
    }
    
    private static int executeQuery(final ServerCommandSource source, final ColumnPos pos) throws CommandSyntaxException {
        final ChunkPos chunkPos3 = new ChunkPos(pos.x >> 4, pos.z >> 4);
        final DimensionType dimensionType4 = source.getWorld().getDimension().getType();
        final boolean boolean5 = source.getMinecraftServer().getWorld(dimensionType4).getForcedChunks().contains(chunkPos3.toLong());
        if (boolean5) {
            source.sendFeedback(new TranslatableTextComponent("commands.forceload.query.success", new Object[] { chunkPos3, dimensionType4 }), false);
            return 1;
        }
        throw ForceLoadCommand.QUERY_FAILURE_EXCEPTION.create(chunkPos3, dimensionType4);
    }
    
    private static int executeQuery(final ServerCommandSource source) {
        final DimensionType dimensionType2 = source.getWorld().getDimension().getType();
        final LongSet longSet3 = source.getMinecraftServer().getWorld(dimensionType2).getForcedChunks();
        final int integer4 = longSet3.size();
        if (integer4 > 0) {
            final String string5 = Joiner.on(", ").join(longSet3.stream().sorted().map(ChunkPos::new).map(ChunkPos::toString).iterator());
            if (integer4 == 1) {
                source.sendFeedback(new TranslatableTextComponent("commands.forceload.list.single", new Object[] { dimensionType2, string5 }), false);
            }
            else {
                source.sendFeedback(new TranslatableTextComponent("commands.forceload.list.multiple", new Object[] { integer4, dimensionType2, string5 }), false);
            }
        }
        else {
            source.sendError(new TranslatableTextComponent("commands.forceload.added.none", new Object[] { dimensionType2 }));
        }
        return integer4;
    }
    
    private static int executeRemoveAll(final ServerCommandSource source) {
        final DimensionType dimensionType2 = source.getWorld().getDimension().getType();
        final ServerWorld serverWorld3 = source.getMinecraftServer().getWorld(dimensionType2);
        final LongSet longSet4 = serverWorld3.getForcedChunks();
        longSet4.forEach(long2 -> serverWorld3.setChunkForced(ChunkPos.getPackedX(long2), ChunkPos.getPackedZ(long2), false));
        source.sendFeedback(new TranslatableTextComponent("commands.forceload.removed.all", new Object[] { dimensionType2 }), true);
        return 0;
    }
    
    private static int executeChange(final ServerCommandSource source, final ColumnPos from, final ColumnPos to, final boolean forceLoaded) throws CommandSyntaxException {
        final int integer5 = Math.min(from.x, to.x);
        final int integer6 = Math.min(from.z, to.z);
        final int integer7 = Math.max(from.x, to.x);
        final int integer8 = Math.max(from.z, to.z);
        if (integer5 < -30000000 || integer6 < -30000000 || integer7 >= 30000000 || integer8 >= 30000000) {
            throw BlockPosArgumentType.OUT_OF_WORLD_EXCEPTION.create();
        }
        final int integer9 = integer5 >> 4;
        final int integer10 = integer6 >> 4;
        final int integer11 = integer7 >> 4;
        final int integer12 = integer8 >> 4;
        final long long13 = (integer11 - integer9 + 1L) * (integer12 - integer10 + 1L);
        if (long13 > 256L) {
            throw ForceLoadCommand.TOOBIG_EXCEPTION.create(256, long13);
        }
        final DimensionType dimensionType15 = source.getWorld().getDimension().getType();
        final ServerWorld serverWorld16 = source.getMinecraftServer().getWorld(dimensionType15);
        ChunkPos chunkPos17 = null;
        int integer13 = 0;
        for (int integer14 = integer9; integer14 <= integer11; ++integer14) {
            for (int integer15 = integer10; integer15 <= integer12; ++integer15) {
                final boolean boolean21 = serverWorld16.setChunkForced(integer14, integer15, forceLoaded);
                if (boolean21) {
                    ++integer13;
                    if (chunkPos17 == null) {
                        chunkPos17 = new ChunkPos(integer14, integer15);
                    }
                }
            }
        }
        if (integer13 == 0) {
            throw (forceLoaded ? ForceLoadCommand.ADDED_FAILURE_EXCEPTION : ForceLoadCommand.REMOVED_FAILURE_EXCEPTION).create();
        }
        if (integer13 == 1) {
            source.sendFeedback(new TranslatableTextComponent("commands.forceload." + (forceLoaded ? "added" : "removed") + ".single", new Object[] { chunkPos17, dimensionType15 }), true);
        }
        else {
            final ChunkPos chunkPos18 = new ChunkPos(integer9, integer10);
            final ChunkPos chunkPos19 = new ChunkPos(integer11, integer12);
            source.sendFeedback(new TranslatableTextComponent("commands.forceload." + (forceLoaded ? "added" : "removed") + ".multiple", new Object[] { integer13, dimensionType15, chunkPos18, chunkPos19 }), true);
        }
        return integer13;
    }
    
    static {
        TOOBIG_EXCEPTION = new Dynamic2CommandExceptionType((object1, object2) -> new TranslatableTextComponent("commands.forceload.toobig", new Object[] { object1, object2 }));
        QUERY_FAILURE_EXCEPTION = new Dynamic2CommandExceptionType((object1, object2) -> new TranslatableTextComponent("commands.forceload.query.failure", new Object[] { object1, object2 }));
        ADDED_FAILURE_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("commands.forceload.added.failure", new Object[0]));
        REMOVED_FAILURE_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("commands.forceload.removed.failure", new Object[0]));
    }
}

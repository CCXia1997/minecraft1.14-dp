package net.minecraft.server.command;

import javax.annotation.Nullable;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Iterator;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.BlockState;
import java.util.Deque;
import java.util.List;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import java.util.Collection;
import net.minecraft.block.Blocks;
import net.minecraft.util.Clearable;
import net.minecraft.block.Block;
import net.minecraft.world.BlockView;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ViewableWorld;
import com.google.common.collect.Lists;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.command.arguments.BlockPredicateArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.command.arguments.BlockPosArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.block.pattern.CachedBlockPosition;
import java.util.function.Predicate;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class CloneCommand
{
    private static final SimpleCommandExceptionType OVERLAP_EXCEPTION;
    private static final Dynamic2CommandExceptionType TOOBIG_EXCEPTION;
    private static final SimpleCommandExceptionType FAILED_EXCEPTION;
    public static final Predicate<CachedBlockPosition> IS_AIR_PREDICATE;
    
    public static void register(final CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("clone").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).then(CommandManager.argument("begin", (com.mojang.brigadier.arguments.ArgumentType<Object>)BlockPosArgumentType.create()).then(CommandManager.argument("end", (com.mojang.brigadier.arguments.ArgumentType<Object>)BlockPosArgumentType.create()).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("destination", (com.mojang.brigadier.arguments.ArgumentType<Object>)BlockPosArgumentType.create()).executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "begin"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "end"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "destination"), cachedBlockPosition -> true, Mode.c))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("replace").executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "begin"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "end"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "destination"), cachedBlockPosition -> true, Mode.c))).then(CommandManager.literal("force").executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "begin"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "end"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "destination"), cachedBlockPosition -> true, Mode.a)))).then(CommandManager.literal("move").executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "begin"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "end"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "destination"), cachedBlockPosition -> true, Mode.b)))).then(CommandManager.literal("normal").executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "begin"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "end"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "destination"), cachedBlockPosition -> true, Mode.c))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("masked").executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "begin"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "end"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "destination"), CloneCommand.IS_AIR_PREDICATE, Mode.c))).then(CommandManager.literal("force").executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "begin"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "end"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "destination"), CloneCommand.IS_AIR_PREDICATE, Mode.a)))).then(CommandManager.literal("move").executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "begin"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "end"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "destination"), CloneCommand.IS_AIR_PREDICATE, Mode.b)))).then(CommandManager.literal("normal").executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "begin"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "end"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "destination"), CloneCommand.IS_AIR_PREDICATE, Mode.c))))).then(CommandManager.literal("filtered").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("filter", (com.mojang.brigadier.arguments.ArgumentType<Object>)BlockPredicateArgumentType.create()).executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "begin"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "end"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "destination"), BlockPredicateArgumentType.getBlockPredicate((CommandContext<ServerCommandSource>)commandContext, "filter"), Mode.c))).then(CommandManager.literal("force").executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "begin"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "end"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "destination"), BlockPredicateArgumentType.getBlockPredicate((CommandContext<ServerCommandSource>)commandContext, "filter"), Mode.a)))).then(CommandManager.literal("move").executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "begin"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "end"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "destination"), BlockPredicateArgumentType.getBlockPredicate((CommandContext<ServerCommandSource>)commandContext, "filter"), Mode.b)))).then(CommandManager.literal("normal").executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "begin"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "end"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "destination"), BlockPredicateArgumentType.getBlockPredicate((CommandContext<ServerCommandSource>)commandContext, "filter"), Mode.c)))))))));
    }
    
    private static int execute(final ServerCommandSource source, final BlockPos begin, final BlockPos end, final BlockPos destination, final Predicate<CachedBlockPosition> filter, final Mode mode) throws CommandSyntaxException {
        final MutableIntBoundingBox mutableIntBoundingBox7 = new MutableIntBoundingBox(begin, end);
        final BlockPos blockPos8 = destination.add(mutableIntBoundingBox7.getSize());
        final MutableIntBoundingBox mutableIntBoundingBox8 = new MutableIntBoundingBox(destination, blockPos8);
        if (!mode.allowsOverlap() && mutableIntBoundingBox8.intersects(mutableIntBoundingBox7)) {
            throw CloneCommand.OVERLAP_EXCEPTION.create();
        }
        final int integer10 = mutableIntBoundingBox7.getBlockCountX() * mutableIntBoundingBox7.getBlockCountY() * mutableIntBoundingBox7.getBlockCountZ();
        if (integer10 > 32768) {
            throw CloneCommand.TOOBIG_EXCEPTION.create(32768, integer10);
        }
        final ServerWorld serverWorld11 = source.getWorld();
        if (!serverWorld11.isAreaLoaded(begin, end) || !serverWorld11.isAreaLoaded(destination, blockPos8)) {
            throw BlockPosArgumentType.UNLOADED_EXCEPTION.create();
        }
        final List<BlockInfo> list12 = Lists.newArrayList();
        final List<BlockInfo> list13 = Lists.newArrayList();
        final List<BlockInfo> list14 = Lists.newArrayList();
        final Deque<BlockPos> deque15 = Lists.newLinkedList();
        final BlockPos blockPos9 = new BlockPos(mutableIntBoundingBox8.minX - mutableIntBoundingBox7.minX, mutableIntBoundingBox8.minY - mutableIntBoundingBox7.minY, mutableIntBoundingBox8.minZ - mutableIntBoundingBox7.minZ);
        for (int integer11 = mutableIntBoundingBox7.minZ; integer11 <= mutableIntBoundingBox7.maxZ; ++integer11) {
            for (int integer12 = mutableIntBoundingBox7.minY; integer12 <= mutableIntBoundingBox7.maxY; ++integer12) {
                for (int integer13 = mutableIntBoundingBox7.minX; integer13 <= mutableIntBoundingBox7.maxX; ++integer13) {
                    final BlockPos blockPos10 = new BlockPos(integer13, integer12, integer11);
                    final BlockPos blockPos11 = blockPos10.add(blockPos9);
                    final CachedBlockPosition cachedBlockPosition22 = new CachedBlockPosition(serverWorld11, blockPos10, false);
                    final BlockState blockState23 = cachedBlockPosition22.getBlockState();
                    if (filter.test(cachedBlockPosition22)) {
                        final BlockEntity blockEntity24 = serverWorld11.getBlockEntity(blockPos10);
                        if (blockEntity24 != null) {
                            final CompoundTag compoundTag25 = blockEntity24.toTag(new CompoundTag());
                            list13.add(new BlockInfo(blockPos11, blockState23, compoundTag25));
                            deque15.addLast(blockPos10);
                        }
                        else if (blockState23.isFullOpaque(serverWorld11, blockPos10) || Block.isShapeFullCube(blockState23.getCollisionShape(serverWorld11, blockPos10))) {
                            list12.add(new BlockInfo(blockPos11, blockState23, null));
                            deque15.addLast(blockPos10);
                        }
                        else {
                            list14.add(new BlockInfo(blockPos11, blockState23, null));
                            deque15.addFirst(blockPos10);
                        }
                    }
                }
            }
        }
        if (mode == Mode.b) {
            for (final BlockPos blockPos12 : deque15) {
                final BlockEntity blockEntity25 = serverWorld11.getBlockEntity(blockPos12);
                Clearable.clear(blockEntity25);
                serverWorld11.setBlockState(blockPos12, Blocks.gg.getDefaultState(), 2);
            }
            for (final BlockPos blockPos12 : deque15) {
                serverWorld11.setBlockState(blockPos12, Blocks.AIR.getDefaultState(), 3);
            }
        }
        final List<BlockInfo> list15 = Lists.newArrayList();
        list15.addAll(list12);
        list15.addAll(list13);
        list15.addAll(list14);
        final List<BlockInfo> list16 = Lists.<BlockInfo>reverse(list15);
        for (final BlockInfo blockInfo20 : list16) {
            final BlockEntity blockEntity26 = serverWorld11.getBlockEntity(blockInfo20.pos);
            Clearable.clear(blockEntity26);
            serverWorld11.setBlockState(blockInfo20.pos, Blocks.gg.getDefaultState(), 2);
        }
        int integer13 = 0;
        for (final BlockInfo blockInfo21 : list15) {
            if (serverWorld11.setBlockState(blockInfo21.pos, blockInfo21.state, 2)) {
                ++integer13;
            }
        }
        for (final BlockInfo blockInfo21 : list13) {
            final BlockEntity blockEntity27 = serverWorld11.getBlockEntity(blockInfo21.pos);
            if (blockInfo21.blockEntityTag != null && blockEntity27 != null) {
                blockInfo21.blockEntityTag.putInt("x", blockInfo21.pos.getX());
                blockInfo21.blockEntityTag.putInt("y", blockInfo21.pos.getY());
                blockInfo21.blockEntityTag.putInt("z", blockInfo21.pos.getZ());
                blockEntity27.fromTag(blockInfo21.blockEntityTag);
                blockEntity27.markDirty();
            }
            serverWorld11.setBlockState(blockInfo21.pos, blockInfo21.state, 2);
        }
        for (final BlockInfo blockInfo21 : list16) {
            serverWorld11.updateNeighbors(blockInfo21.pos, blockInfo21.state.getBlock());
        }
        serverWorld11.getBlockTickScheduler().copyScheduledTicks(mutableIntBoundingBox7, blockPos9);
        if (integer13 == 0) {
            throw CloneCommand.FAILED_EXCEPTION.create();
        }
        source.sendFeedback(new TranslatableTextComponent("commands.clone.success", new Object[] { integer13 }), true);
        return integer13;
    }
    
    static {
        OVERLAP_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("commands.clone.overlap", new Object[0]));
        TOOBIG_EXCEPTION = new Dynamic2CommandExceptionType((object1, object2) -> new TranslatableTextComponent("commands.clone.toobig", new Object[] { object1, object2 }));
        FAILED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("commands.clone.failed", new Object[0]));
        IS_AIR_PREDICATE = (cachedBlockPosition -> !cachedBlockPosition.getBlockState().isAir());
    }
    
    enum Mode
    {
        a(true), 
        b(true), 
        c(false);
        
        private final boolean allowsOverlap;
        
        private Mode(final boolean allowsOverlap) {
            this.allowsOverlap = allowsOverlap;
        }
        
        public boolean allowsOverlap() {
            return this.allowsOverlap;
        }
    }
    
    static class BlockInfo
    {
        public final BlockPos pos;
        public final BlockState state;
        @Nullable
        public final CompoundTag blockEntityTag;
        
        public BlockInfo(final BlockPos pos, final BlockState state, @Nullable final CompoundTag blockEntityTag) {
            this.pos = pos;
            this.state = state;
            this.blockEntityTag = blockEntityTag;
        }
    }
}

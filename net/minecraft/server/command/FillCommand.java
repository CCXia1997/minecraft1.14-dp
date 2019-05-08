package net.minecraft.server.command;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.state.property.Property;
import java.util.Collections;
import net.minecraft.block.Blocks;
import com.mojang.brigadier.Message;
import net.minecraft.util.math.Vec3i;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import java.util.Iterator;
import net.minecraft.server.world.ServerWorld;
import java.util.List;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Clearable;
import net.minecraft.world.ViewableWorld;
import net.minecraft.util.math.BlockPos;
import com.google.common.collect.Lists;
import javax.annotation.Nullable;
import net.minecraft.block.pattern.CachedBlockPosition;
import java.util.function.Predicate;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.command.arguments.BlockPredicateArgumentType;
import net.minecraft.command.arguments.BlockStateArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.command.arguments.BlockPosArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.arguments.BlockStateArgument;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;

public class FillCommand
{
    private static final Dynamic2CommandExceptionType TOOBIG_EXCEPTION;
    private static final BlockStateArgument AIR_BLOCK_ARGUMENT;
    private static final SimpleCommandExceptionType FAILED_EXCEPTION;
    
    public static void register(final CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("fill").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).then(CommandManager.argument("from", (com.mojang.brigadier.arguments.ArgumentType<Object>)BlockPosArgumentType.create()).then(CommandManager.argument("to", (com.mojang.brigadier.arguments.ArgumentType<Object>)BlockPosArgumentType.create()).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("block", (com.mojang.brigadier.arguments.ArgumentType<Object>)BlockStateArgumentType.create()).executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), new MutableIntBoundingBox(BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "from"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "to")), BlockStateArgumentType.getBlockState((CommandContext<ServerCommandSource>)commandContext, "block"), Mode.a, null))).then(((LiteralArgumentBuilder)CommandManager.literal("replace").executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), new MutableIntBoundingBox(BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "from"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "to")), BlockStateArgumentType.getBlockState((CommandContext<ServerCommandSource>)commandContext, "block"), Mode.a, null))).then(CommandManager.argument("filter", (com.mojang.brigadier.arguments.ArgumentType<Object>)BlockPredicateArgumentType.create()).executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), new MutableIntBoundingBox(BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "from"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "to")), BlockStateArgumentType.getBlockState((CommandContext<ServerCommandSource>)commandContext, "block"), Mode.a, BlockPredicateArgumentType.getBlockPredicate((CommandContext<ServerCommandSource>)commandContext, "filter")))))).then(CommandManager.literal("keep").executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), new MutableIntBoundingBox(BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "from"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "to")), BlockStateArgumentType.getBlockState((CommandContext<ServerCommandSource>)commandContext, "block"), Mode.a, cachedBlockPosition -> cachedBlockPosition.getWorld().isAir(cachedBlockPosition.getBlockPos()))))).then(CommandManager.literal("outline").executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), new MutableIntBoundingBox(BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "from"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "to")), BlockStateArgumentType.getBlockState((CommandContext<ServerCommandSource>)commandContext, "block"), Mode.b, null)))).then(CommandManager.literal("hollow").executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), new MutableIntBoundingBox(BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "from"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "to")), BlockStateArgumentType.getBlockState((CommandContext<ServerCommandSource>)commandContext, "block"), Mode.c, null)))).then(CommandManager.literal("destroy").executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), new MutableIntBoundingBox(BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "from"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "to")), BlockStateArgumentType.getBlockState((CommandContext<ServerCommandSource>)commandContext, "block"), Mode.d, null)))))));
    }
    
    private static int execute(final ServerCommandSource source, final MutableIntBoundingBox range, final BlockStateArgument block, final Mode mode, @Nullable final Predicate<CachedBlockPosition> filter) throws CommandSyntaxException {
        final int integer6 = range.getBlockCountX() * range.getBlockCountY() * range.getBlockCountZ();
        if (integer6 > 32768) {
            throw FillCommand.TOOBIG_EXCEPTION.create(32768, integer6);
        }
        final List<BlockPos> list7 = Lists.newArrayList();
        final ServerWorld serverWorld8 = source.getWorld();
        int integer7 = 0;
        for (final BlockPos blockPos11 : BlockPos.iterate(range.minX, range.minY, range.minZ, range.maxX, range.maxY, range.maxZ)) {
            if (filter != null && !filter.test(new CachedBlockPosition(serverWorld8, blockPos11, true))) {
                continue;
            }
            final BlockStateArgument blockStateArgument12 = mode.filter.filter(range, blockPos11, block, serverWorld8);
            if (blockStateArgument12 == null) {
                continue;
            }
            final BlockEntity blockEntity13 = serverWorld8.getBlockEntity(blockPos11);
            Clearable.clear(blockEntity13);
            if (!blockStateArgument12.setBlockState(serverWorld8, blockPos11, 2)) {
                continue;
            }
            list7.add(blockPos11.toImmutable());
            ++integer7;
        }
        for (final BlockPos blockPos11 : list7) {
            final Block block2 = serverWorld8.getBlockState(blockPos11).getBlock();
            serverWorld8.updateNeighbors(blockPos11, block2);
        }
        if (integer7 == 0) {
            throw FillCommand.FAILED_EXCEPTION.create();
        }
        source.sendFeedback(new TranslatableTextComponent("commands.fill.success", new Object[] { integer7 }), true);
        return integer7;
    }
    
    static {
        TOOBIG_EXCEPTION = new Dynamic2CommandExceptionType((object1, object2) -> new TranslatableTextComponent("commands.fill.toobig", new Object[] { object1, object2 }));
        AIR_BLOCK_ARGUMENT = new BlockStateArgument(Blocks.AIR.getDefaultState(), Collections.<Property<?>>emptySet(), null);
        FAILED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("commands.fill.failed", new Object[0]));
    }
    
    enum Mode
    {
        a((mutableIntBoundingBox, blockPos, blockStateArgument, serverWorld) -> blockStateArgument), 
        b((mutableIntBoundingBox, blockPos, blockStateArgument, serverWorld) -> {
            if (blockPos.getX() == mutableIntBoundingBox.minX || blockPos.getX() == mutableIntBoundingBox.maxX || blockPos.getY() == mutableIntBoundingBox.minY || blockPos.getY() == mutableIntBoundingBox.maxY || blockPos.getZ() == mutableIntBoundingBox.minZ || blockPos.getZ() == mutableIntBoundingBox.maxZ) {
                return blockStateArgument;
            }
            else {
                return null;
            }
        }), 
        c((mutableIntBoundingBox, blockPos, blockStateArgument, serverWorld) -> {
            if (blockPos.getX() == mutableIntBoundingBox.minX || blockPos.getX() == mutableIntBoundingBox.maxX || blockPos.getY() == mutableIntBoundingBox.minY || blockPos.getY() == mutableIntBoundingBox.maxY || blockPos.getZ() == mutableIntBoundingBox.minZ || blockPos.getZ() == mutableIntBoundingBox.maxZ) {
                return blockStateArgument;
            }
            else {
                return FillCommand.AIR_BLOCK_ARGUMENT;
            }
        }), 
        d((mutableIntBoundingBox, blockPos, blockStateArgument, serverWorld) -> {
            serverWorld.breakBlock(blockPos, true);
            return blockStateArgument;
        });
        
        public final SetBlockCommand.Filter filter;
        
        private Mode(final SetBlockCommand.Filter filter) {
            this.filter = filter;
        }
    }
}

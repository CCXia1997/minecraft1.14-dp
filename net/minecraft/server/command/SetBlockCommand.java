package net.minecraft.server.command;

import net.minecraft.util.math.MutableIntBoundingBox;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Clearable;
import net.minecraft.world.ViewableWorld;
import javax.annotation.Nullable;
import net.minecraft.block.pattern.CachedBlockPosition;
import java.util.function.Predicate;
import net.minecraft.command.arguments.BlockStateArgument;
import net.minecraft.util.math.BlockPos;
import net.minecraft.command.arguments.BlockStateArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.command.arguments.BlockPosArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class SetBlockCommand
{
    private static final SimpleCommandExceptionType FAILED_EXCEPTION;
    
    public static void register(final CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("setblock").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).then(CommandManager.argument("pos", (com.mojang.brigadier.arguments.ArgumentType<Object>)BlockPosArgumentType.create()).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("block", (com.mojang.brigadier.arguments.ArgumentType<Object>)BlockStateArgumentType.create()).executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "pos"), BlockStateArgumentType.getBlockState((CommandContext<ServerCommandSource>)commandContext, "block"), Mode.a, null))).then(CommandManager.literal("destroy").executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "pos"), BlockStateArgumentType.getBlockState((CommandContext<ServerCommandSource>)commandContext, "block"), Mode.b, null)))).then(CommandManager.literal("keep").executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "pos"), BlockStateArgumentType.getBlockState((CommandContext<ServerCommandSource>)commandContext, "block"), Mode.a, cachedBlockPosition -> cachedBlockPosition.getWorld().isAir(cachedBlockPosition.getBlockPos()))))).then(CommandManager.literal("replace").executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "pos"), BlockStateArgumentType.getBlockState((CommandContext<ServerCommandSource>)commandContext, "block"), Mode.a, null))))));
    }
    
    private static int execute(final ServerCommandSource source, final BlockPos pos, final BlockStateArgument block, final Mode mode, @Nullable final Predicate<CachedBlockPosition> condition) throws CommandSyntaxException {
        final ServerWorld serverWorld6 = source.getWorld();
        if (condition != null && !condition.test(new CachedBlockPosition(serverWorld6, pos, true))) {
            throw SetBlockCommand.FAILED_EXCEPTION.create();
        }
        boolean boolean7;
        if (mode == Mode.b) {
            serverWorld6.breakBlock(pos, true);
            boolean7 = !block.getBlockState().isAir();
        }
        else {
            final BlockEntity blockEntity8 = serverWorld6.getBlockEntity(pos);
            Clearable.clear(blockEntity8);
            boolean7 = true;
        }
        if (boolean7 && !block.setBlockState(serverWorld6, pos, 2)) {
            throw SetBlockCommand.FAILED_EXCEPTION.create();
        }
        serverWorld6.updateNeighbors(pos, block.getBlockState().getBlock());
        source.sendFeedback(new TranslatableTextComponent("commands.setblock.success", new Object[] { pos.getX(), pos.getY(), pos.getZ() }), true);
        return 1;
    }
    
    static {
        FAILED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("commands.setblock.failed", new Object[0]));
    }
    
    public enum Mode
    {
        a, 
        b;
    }
    
    public interface Filter
    {
        @Nullable
        BlockStateArgument filter(final MutableIntBoundingBox arg1, final BlockPos arg2, final BlockStateArgument arg3, final ServerWorld arg4);
    }
}

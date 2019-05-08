package net.minecraft.server.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.network.Packet;
import net.minecraft.client.network.packet.PlayerSpawnPositionS2CPacket;
import net.minecraft.util.math.BlockPos;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.command.arguments.BlockPosArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.CommandDispatcher;

public class SetWorldSpawnCommand
{
    public static void register(final CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("setworldspawn").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), new BlockPos(((ServerCommandSource)commandContext.getSource()).getPosition())))).then(CommandManager.argument("pos", (com.mojang.brigadier.arguments.ArgumentType<Object>)BlockPosArgumentType.create()).executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getBlockPos((CommandContext<ServerCommandSource>)commandContext, "pos")))));
    }
    
    private static int execute(final ServerCommandSource source, final BlockPos pos) {
        source.getWorld().setSpawnPos(pos);
        source.getMinecraftServer().getPlayerManager().sendToAll(new PlayerSpawnPositionS2CPacket(pos));
        source.sendFeedback(new TranslatableTextComponent("commands.setworldspawn.success", new Object[] { pos.getX(), pos.getY(), pos.getZ() }), true);
        return 1;
    }
}

package net.minecraft.server.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import java.util.Iterator;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import java.util.Collection;
import net.minecraft.command.arguments.ItemStackArgument;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.command.arguments.ItemStackArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.command.arguments.EntityArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.CommandDispatcher;

public class GiveCommand
{
    public static void register(final CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("give").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).then(CommandManager.argument("targets", (com.mojang.brigadier.arguments.ArgumentType<Object>)EntityArgumentType.players()).then(((RequiredArgumentBuilder)CommandManager.argument("item", (com.mojang.brigadier.arguments.ArgumentType<Object>)ItemStackArgumentType.create()).executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), ItemStackArgumentType.getItemStackArgument((com.mojang.brigadier.context.CommandContext<Object>)commandContext, "item"), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), 1))).then(CommandManager.argument("count", (com.mojang.brigadier.arguments.ArgumentType<Object>)IntegerArgumentType.integer(1)).executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), ItemStackArgumentType.getItemStackArgument((com.mojang.brigadier.context.CommandContext<Object>)commandContext, "item"), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), IntegerArgumentType.getInteger(commandContext, "count")))))));
    }
    
    private static int execute(final ServerCommandSource source, final ItemStackArgument item, final Collection<ServerPlayerEntity> targets, final int count) throws CommandSyntaxException {
        for (final ServerPlayerEntity serverPlayerEntity6 : targets) {
            int integer7 = count;
            while (integer7 > 0) {
                final int integer8 = Math.min(item.getItem().getMaxAmount(), integer7);
                integer7 -= integer8;
                final ItemStack itemStack9 = item.createStack(integer8, false);
                final boolean boolean10 = serverPlayerEntity6.inventory.insertStack(itemStack9);
                if (!boolean10 || !itemStack9.isEmpty()) {
                    final ItemEntity itemEntity11 = serverPlayerEntity6.dropItem(itemStack9, false);
                    if (itemEntity11 == null) {
                        continue;
                    }
                    itemEntity11.resetPickupDelay();
                    itemEntity11.setOwner(serverPlayerEntity6.getUuid());
                }
                else {
                    itemStack9.setAmount(1);
                    final ItemEntity itemEntity11 = serverPlayerEntity6.dropItem(itemStack9, false);
                    if (itemEntity11 != null) {
                        itemEntity11.u();
                    }
                    serverPlayerEntity6.world.playSound(null, serverPlayerEntity6.x, serverPlayerEntity6.y, serverPlayerEntity6.z, SoundEvents.fF, SoundCategory.h, 0.2f, ((serverPlayerEntity6.getRand().nextFloat() - serverPlayerEntity6.getRand().nextFloat()) * 0.7f + 1.0f) * 2.0f);
                    serverPlayerEntity6.playerContainer.sendContentUpdates();
                }
            }
        }
        if (targets.size() == 1) {
            source.sendFeedback(new TranslatableTextComponent("commands.give.success.single", new Object[] { count, item.createStack(count, false).toTextComponent(), targets.iterator().next().getDisplayName() }), true);
        }
        else {
            source.sendFeedback(new TranslatableTextComponent("commands.give.success.single", new Object[] { count, item.createStack(count, false).toTextComponent(), targets.size() }), true);
        }
        return targets.size();
    }
}

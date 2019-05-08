package net.minecraft.server.command;

import com.mojang.brigadier.Message;
import java.util.Collections;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Iterator;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.item.ItemStack;
import java.util.function.Predicate;
import net.minecraft.server.network.ServerPlayerEntity;
import java.util.Collection;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.command.arguments.ItemPredicateArgumentType;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.command.arguments.EntityArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;

public class ClearCommand
{
    private static final DynamicCommandExceptionType FAILED_SINGLE_EXCEPTION;
    private static final DynamicCommandExceptionType FAILED_MULTIPLE_EXCEPTION;
    
    public static void register(final CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("clear").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), Collections.<ServerPlayerEntity>singleton(((ServerCommandSource)commandContext.getSource()).getPlayer()), itemStack -> true, -1))).then(((RequiredArgumentBuilder)CommandManager.argument("targets", (com.mojang.brigadier.arguments.ArgumentType<Object>)EntityArgumentType.players()).executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), itemStack -> true, -1))).then(((RequiredArgumentBuilder)CommandManager.argument("item", (com.mojang.brigadier.arguments.ArgumentType<Object>)ItemPredicateArgumentType.create()).executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), ItemPredicateArgumentType.getItemPredicate((CommandContext<ServerCommandSource>)commandContext, "item"), -1))).then(CommandManager.argument("maxCount", (com.mojang.brigadier.arguments.ArgumentType<Object>)IntegerArgumentType.integer(0)).executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), ItemPredicateArgumentType.getItemPredicate((CommandContext<ServerCommandSource>)commandContext, "item"), IntegerArgumentType.getInteger(commandContext, "maxCount")))))));
    }
    
    private static int execute(final ServerCommandSource source, final Collection<ServerPlayerEntity> targets, final Predicate<ItemStack> item, final int maxCount) throws CommandSyntaxException {
        int integer5 = 0;
        for (final ServerPlayerEntity serverPlayerEntity7 : targets) {
            integer5 += serverPlayerEntity7.inventory.a(item, maxCount);
            serverPlayerEntity7.container.sendContentUpdates();
            serverPlayerEntity7.l();
        }
        if (integer5 != 0) {
            if (maxCount == 0) {
                if (targets.size() == 1) {
                    source.sendFeedback(new TranslatableTextComponent("commands.clear.test.single", new Object[] { integer5, targets.iterator().next().getDisplayName() }), true);
                }
                else {
                    source.sendFeedback(new TranslatableTextComponent("commands.clear.test.multiple", new Object[] { integer5, targets.size() }), true);
                }
            }
            else if (targets.size() == 1) {
                source.sendFeedback(new TranslatableTextComponent("commands.clear.success.single", new Object[] { integer5, targets.iterator().next().getDisplayName() }), true);
            }
            else {
                source.sendFeedback(new TranslatableTextComponent("commands.clear.success.multiple", new Object[] { integer5, targets.size() }), true);
            }
            return integer5;
        }
        if (targets.size() == 1) {
            throw ClearCommand.FAILED_SINGLE_EXCEPTION.create(targets.iterator().next().getName().getFormattedText());
        }
        throw ClearCommand.FAILED_MULTIPLE_EXCEPTION.create(targets.size());
    }
    
    static {
        final TranslatableTextComponent translatableTextComponent;
        FAILED_SINGLE_EXCEPTION = new DynamicCommandExceptionType(object -> {
            new TranslatableTextComponent("clear.failed.single", new Object[] { object });
            return translatableTextComponent;
        });
        final TranslatableTextComponent translatableTextComponent2;
        FAILED_MULTIPLE_EXCEPTION = new DynamicCommandExceptionType(object -> {
            new TranslatableTextComponent("clear.failed.multiple", new Object[] { object });
            return translatableTextComponent2;
        });
    }
}

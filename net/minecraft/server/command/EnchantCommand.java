package net.minecraft.server.command;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.item.ItemStack;
import java.util.Iterator;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import java.util.Collection;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.command.arguments.ItemEnchantmentArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.command.arguments.EntityArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;

public class EnchantCommand
{
    private static final DynamicCommandExceptionType FAILED_ENTITY_EXCEPTION;
    private static final DynamicCommandExceptionType FAILED_ITEMLESS_EXCEPTION;
    private static final DynamicCommandExceptionType FAILED_INCOMPATIBLE_EXCEPTION;
    private static final Dynamic2CommandExceptionType FAILED_LEVEL_EXCEPTION;
    private static final SimpleCommandExceptionType FAILED_EXCEPTION;
    
    public static void register(final CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("enchant").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).then(CommandManager.argument("targets", (com.mojang.brigadier.arguments.ArgumentType<Object>)EntityArgumentType.entities()).then(((RequiredArgumentBuilder)CommandManager.argument("enchantment", (com.mojang.brigadier.arguments.ArgumentType<Object>)ItemEnchantmentArgumentType.create()).executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntities((CommandContext<ServerCommandSource>)commandContext, "targets"), ItemEnchantmentArgumentType.getEnchantment((CommandContext<ServerCommandSource>)commandContext, "enchantment"), 1))).then(CommandManager.argument("level", (com.mojang.brigadier.arguments.ArgumentType<Object>)IntegerArgumentType.integer(0)).executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntities((CommandContext<ServerCommandSource>)commandContext, "targets"), ItemEnchantmentArgumentType.getEnchantment((CommandContext<ServerCommandSource>)commandContext, "enchantment"), IntegerArgumentType.getInteger(commandContext, "level")))))));
    }
    
    private static int execute(final ServerCommandSource source, final Collection<? extends Entity> targets, final Enchantment enchantment, final int level) throws CommandSyntaxException {
        if (level > enchantment.getMaximumLevel()) {
            throw EnchantCommand.FAILED_LEVEL_EXCEPTION.create(level, enchantment.getMaximumLevel());
        }
        int integer5 = 0;
        for (final Entity entity7 : targets) {
            if (entity7 instanceof LivingEntity) {
                final LivingEntity livingEntity8 = (LivingEntity)entity7;
                final ItemStack itemStack9 = livingEntity8.getMainHandStack();
                if (!itemStack9.isEmpty()) {
                    if (enchantment.isAcceptableItem(itemStack9) && EnchantmentHelper.contains(EnchantmentHelper.getEnchantments(itemStack9).keySet(), enchantment)) {
                        itemStack9.addEnchantment(enchantment, level);
                        ++integer5;
                    }
                    else {
                        if (targets.size() == 1) {
                            throw EnchantCommand.FAILED_INCOMPATIBLE_EXCEPTION.create(itemStack9.getItem().getTranslatedNameTrimmed(itemStack9).getString());
                        }
                        continue;
                    }
                }
                else {
                    if (targets.size() == 1) {
                        throw EnchantCommand.FAILED_ITEMLESS_EXCEPTION.create(livingEntity8.getName().getString());
                    }
                    continue;
                }
            }
            else {
                if (targets.size() == 1) {
                    throw EnchantCommand.FAILED_ENTITY_EXCEPTION.create(entity7.getName().getString());
                }
                continue;
            }
        }
        if (integer5 == 0) {
            throw EnchantCommand.FAILED_EXCEPTION.create();
        }
        if (targets.size() == 1) {
            source.sendFeedback(new TranslatableTextComponent("commands.enchant.success.single", new Object[] { enchantment.getTextComponent(level), ((Entity)targets.iterator().next()).getDisplayName() }), true);
        }
        else {
            source.sendFeedback(new TranslatableTextComponent("commands.enchant.success.multiple", new Object[] { enchantment.getTextComponent(level), targets.size() }), true);
        }
        return integer5;
    }
    
    static {
        final TranslatableTextComponent translatableTextComponent;
        FAILED_ENTITY_EXCEPTION = new DynamicCommandExceptionType(object -> {
            new TranslatableTextComponent("commands.enchant.failed.entity", new Object[] { object });
            return translatableTextComponent;
        });
        final TranslatableTextComponent translatableTextComponent2;
        FAILED_ITEMLESS_EXCEPTION = new DynamicCommandExceptionType(object -> {
            new TranslatableTextComponent("commands.enchant.failed.itemless", new Object[] { object });
            return translatableTextComponent2;
        });
        final TranslatableTextComponent translatableTextComponent3;
        FAILED_INCOMPATIBLE_EXCEPTION = new DynamicCommandExceptionType(object -> {
            new TranslatableTextComponent("commands.enchant.failed.incompatible", new Object[] { object });
            return translatableTextComponent3;
        });
        FAILED_LEVEL_EXCEPTION = new Dynamic2CommandExceptionType((object1, object2) -> new TranslatableTextComponent("commands.enchant.failed.level", new Object[] { object1, object2 }));
        FAILED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("commands.enchant.failed", new Object[0]));
    }
}

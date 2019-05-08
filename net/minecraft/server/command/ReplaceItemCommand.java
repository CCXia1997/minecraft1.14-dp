package net.minecraft.server.command;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.CommandContext;
import java.util.Iterator;
import java.util.List;
import net.minecraft.server.network.ServerPlayerEntity;
import com.google.common.collect.Lists;
import net.minecraft.entity.Entity;
import java.util.Collection;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.command.arguments.EntityArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.command.arguments.ItemStackArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.command.arguments.ItemSlotArgumentType;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.command.arguments.BlockPosArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class ReplaceItemCommand
{
    public static final SimpleCommandExceptionType BLOCK_FAILED_EXCEPTION;
    public static final DynamicCommandExceptionType SLOT_INAPPLICABLE_EXCEPTION;
    public static final Dynamic2CommandExceptionType ENTITY_FAILED_EXCEPTION;
    
    public static void register(final CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("replaceitem").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).then(CommandManager.literal("block").then(CommandManager.argument("pos", (com.mojang.brigadier.arguments.ArgumentType<Object>)BlockPosArgumentType.create()).then(CommandManager.argument("slot", (com.mojang.brigadier.arguments.ArgumentType<Object>)ItemSlotArgumentType.create()).then(((RequiredArgumentBuilder)CommandManager.argument("item", (com.mojang.brigadier.arguments.ArgumentType<Object>)ItemStackArgumentType.create()).executes(commandContext -> executeBlock((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "pos"), ItemSlotArgumentType.getItemSlot((CommandContext<ServerCommandSource>)commandContext, "slot"), ItemStackArgumentType.getItemStackArgument((com.mojang.brigadier.context.CommandContext<Object>)commandContext, "item").createStack(1, false)))).then(CommandManager.argument("count", (com.mojang.brigadier.arguments.ArgumentType<Object>)IntegerArgumentType.integer(1, 64)).executes(commandContext -> executeBlock((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "pos"), ItemSlotArgumentType.getItemSlot((CommandContext<ServerCommandSource>)commandContext, "slot"), ItemStackArgumentType.getItemStackArgument((com.mojang.brigadier.context.CommandContext<Object>)commandContext, "item").createStack(IntegerArgumentType.getInteger(commandContext, "count"), true))))))))).then(CommandManager.literal("entity").then(CommandManager.argument("targets", (com.mojang.brigadier.arguments.ArgumentType<Object>)EntityArgumentType.entities()).then(CommandManager.argument("slot", (com.mojang.brigadier.arguments.ArgumentType<Object>)ItemSlotArgumentType.create()).then(((RequiredArgumentBuilder)CommandManager.argument("item", (com.mojang.brigadier.arguments.ArgumentType<Object>)ItemStackArgumentType.create()).executes(commandContext -> executeEntity((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntities((CommandContext<ServerCommandSource>)commandContext, "targets"), ItemSlotArgumentType.getItemSlot((CommandContext<ServerCommandSource>)commandContext, "slot"), ItemStackArgumentType.getItemStackArgument((com.mojang.brigadier.context.CommandContext<Object>)commandContext, "item").createStack(1, false)))).then(CommandManager.argument("count", (com.mojang.brigadier.arguments.ArgumentType<Object>)IntegerArgumentType.integer(1, 64)).executes(commandContext -> executeEntity((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntities((CommandContext<ServerCommandSource>)commandContext, "targets"), ItemSlotArgumentType.getItemSlot((CommandContext<ServerCommandSource>)commandContext, "slot"), ItemStackArgumentType.getItemStackArgument((com.mojang.brigadier.context.CommandContext<Object>)commandContext, "item").createStack(IntegerArgumentType.getInteger(commandContext, "count"), true)))))))));
    }
    
    private static int executeBlock(final ServerCommandSource source, final BlockPos pos, final int slot, final ItemStack item) throws CommandSyntaxException {
        final BlockEntity blockEntity5 = source.getWorld().getBlockEntity(pos);
        if (!(blockEntity5 instanceof Inventory)) {
            throw ReplaceItemCommand.BLOCK_FAILED_EXCEPTION.create();
        }
        final Inventory inventory6 = (Inventory)blockEntity5;
        if (slot < 0 || slot >= inventory6.getInvSize()) {
            throw ReplaceItemCommand.SLOT_INAPPLICABLE_EXCEPTION.create(slot);
        }
        inventory6.setInvStack(slot, item);
        source.sendFeedback(new TranslatableTextComponent("commands.replaceitem.block.success", new Object[] { pos.getX(), pos.getY(), pos.getZ(), item.toTextComponent() }), true);
        return 1;
    }
    
    private static int executeEntity(final ServerCommandSource source, final Collection<? extends Entity> targets, final int slot, final ItemStack item) throws CommandSyntaxException {
        final List<Entity> list5 = Lists.newArrayListWithCapacity(targets.size());
        for (final Entity entity7 : targets) {
            if (entity7 instanceof ServerPlayerEntity) {
                ((ServerPlayerEntity)entity7).playerContainer.sendContentUpdates();
            }
            if (entity7.equip(slot, item.copy())) {
                list5.add(entity7);
                if (!(entity7 instanceof ServerPlayerEntity)) {
                    continue;
                }
                ((ServerPlayerEntity)entity7).playerContainer.sendContentUpdates();
            }
        }
        if (list5.isEmpty()) {
            throw ReplaceItemCommand.ENTITY_FAILED_EXCEPTION.create(item.toTextComponent(), slot);
        }
        if (list5.size() == 1) {
            source.sendFeedback(new TranslatableTextComponent("commands.replaceitem.entity.success.single", new Object[] { list5.iterator().next().getDisplayName(), item.toTextComponent() }), true);
        }
        else {
            source.sendFeedback(new TranslatableTextComponent("commands.replaceitem.entity.success.multiple", new Object[] { list5.size(), item.toTextComponent() }), true);
        }
        return list5.size();
    }
    
    static {
        BLOCK_FAILED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("commands.replaceitem.block.failed", new Object[0]));
        final TranslatableTextComponent translatableTextComponent;
        SLOT_INAPPLICABLE_EXCEPTION = new DynamicCommandExceptionType(object -> {
            new TranslatableTextComponent("commands.replaceitem.slot.inapplicable", new Object[] { object });
            return translatableTextComponent;
        });
        ENTITY_FAILED_EXCEPTION = new Dynamic2CommandExceptionType((object1, object2) -> new TranslatableTextComponent("commands.replaceitem.entity.failed", new Object[] { object1, object2 }));
    }
}

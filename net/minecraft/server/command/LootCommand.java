package net.minecraft.server.command;

import net.minecraft.world.loot.LootManager;
import java.util.concurrent.CompletableFuture;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.Message;
import net.minecraft.world.loot.LootSupplier;
import net.minecraft.world.loot.context.LootContextTypes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.block.BlockState;
import net.minecraft.world.loot.context.LootContextParameters;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.Identifier;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.entity.ItemEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import java.util.Collection;
import java.util.Objects;
import java.util.Iterator;
import com.google.common.collect.Lists;
import net.minecraft.item.ItemStack;
import java.util.List;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.command.arguments.Vec3ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.command.arguments.ItemSlotArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.command.arguments.ItemStackArgumentType;
import net.minecraft.command.arguments.BlockPosArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.command.arguments.IdentifierArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;

public class LootCommand
{
    public static final SuggestionProvider<ServerCommandSource> SUGGESTION_PROVIDER;
    private static final DynamicCommandExceptionType NO_HELD_ITEMS_EXCEPTION;
    private static final DynamicCommandExceptionType NO_LOOT_TABLE_EXCEPTION;
    
    public static void register(final CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)LootCommand.<LiteralArgumentBuilder>addTargetArguments((LiteralArgumentBuilder)CommandManager.literal("loot").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2)), (argumentBuilder, target) -> argumentBuilder.then(CommandManager.literal("fish").then(CommandManager.argument("loot_table", (com.mojang.brigadier.arguments.ArgumentType<Object>)IdentifierArgumentType.create()).suggests((SuggestionProvider)LootCommand.SUGGESTION_PROVIDER).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("pos", (com.mojang.brigadier.arguments.ArgumentType<Object>)BlockPosArgumentType.create()).executes(commandContext -> executeFish((CommandContext<ServerCommandSource>)commandContext, IdentifierArgumentType.getIdentifier((CommandContext<ServerCommandSource>)commandContext, "loot_table"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "pos"), ItemStack.EMPTY, target))).then(CommandManager.argument("tool", (com.mojang.brigadier.arguments.ArgumentType<Object>)ItemStackArgumentType.create()).executes(commandContext -> executeFish((CommandContext<ServerCommandSource>)commandContext, IdentifierArgumentType.getIdentifier((CommandContext<ServerCommandSource>)commandContext, "loot_table"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "pos"), ItemStackArgumentType.getItemStackArgument((com.mojang.brigadier.context.CommandContext<Object>)commandContext, "tool").createStack(1, false), target)))).then(CommandManager.literal("mainhand").executes(commandContext -> executeFish((CommandContext<ServerCommandSource>)commandContext, IdentifierArgumentType.getIdentifier((CommandContext<ServerCommandSource>)commandContext, "loot_table"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "pos"), getHeldItem((ServerCommandSource)commandContext.getSource(), EquipmentSlot.HAND_MAIN), target)))).then(CommandManager.literal("offhand").executes(commandContext -> executeFish((CommandContext<ServerCommandSource>)commandContext, IdentifierArgumentType.getIdentifier((CommandContext<ServerCommandSource>)commandContext, "loot_table"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "pos"), getHeldItem((ServerCommandSource)commandContext.getSource(), EquipmentSlot.HAND_OFF), target)))))).then(CommandManager.literal("loot").then(CommandManager.argument("loot_table", (com.mojang.brigadier.arguments.ArgumentType<Object>)IdentifierArgumentType.create()).suggests((SuggestionProvider)LootCommand.SUGGESTION_PROVIDER).executes(commandContext -> executeLoot((CommandContext<ServerCommandSource>)commandContext, IdentifierArgumentType.getIdentifier((CommandContext<ServerCommandSource>)commandContext, "loot_table"), target)))).then(CommandManager.literal("kill").then(CommandManager.argument("target", (com.mojang.brigadier.arguments.ArgumentType<Object>)EntityArgumentType.entity()).executes(commandContext -> executeKill((CommandContext<ServerCommandSource>)commandContext, EntityArgumentType.getEntity((CommandContext<ServerCommandSource>)commandContext, "target"), target)))).then(CommandManager.literal("mine").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("pos", (com.mojang.brigadier.arguments.ArgumentType<Object>)BlockPosArgumentType.create()).executes(commandContext -> executeMine((CommandContext<ServerCommandSource>)commandContext, BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "pos"), ItemStack.EMPTY, target))).then(CommandManager.argument("tool", (com.mojang.brigadier.arguments.ArgumentType<Object>)ItemStackArgumentType.create()).executes(commandContext -> executeMine((CommandContext<ServerCommandSource>)commandContext, BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "pos"), ItemStackArgumentType.getItemStackArgument((com.mojang.brigadier.context.CommandContext<Object>)commandContext, "tool").createStack(1, false), target)))).then(CommandManager.literal("mainhand").executes(commandContext -> executeMine((CommandContext<ServerCommandSource>)commandContext, BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "pos"), getHeldItem((ServerCommandSource)commandContext.getSource(), EquipmentSlot.HAND_MAIN), target)))).then(CommandManager.literal("offhand").executes(commandContext -> executeMine((CommandContext<ServerCommandSource>)commandContext, BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "pos"), getHeldItem((ServerCommandSource)commandContext.getSource(), EquipmentSlot.HAND_OFF), target)))))));
    }
    
    private static <T extends ArgumentBuilder<ServerCommandSource, T>> T addTargetArguments(final T rootArgument, final SourceConstructor sourceConstructor) {
        return (T)rootArgument.then(((LiteralArgumentBuilder)CommandManager.literal("replace").then(CommandManager.literal("entity").then(CommandManager.argument("entities", (com.mojang.brigadier.arguments.ArgumentType<Object>)EntityArgumentType.entities()).then(sourceConstructor.construct(CommandManager.argument("slot", (com.mojang.brigadier.arguments.ArgumentType<Object>)ItemSlotArgumentType.create()), (commandContext, list, feedbackMessage) -> executeReplace(EntityArgumentType.getEntities(commandContext, "entities"), ItemSlotArgumentType.getItemSlot(commandContext, "slot"), list.size(), list, feedbackMessage)).then((ArgumentBuilder)sourceConstructor.construct(CommandManager.argument("count", (com.mojang.brigadier.arguments.ArgumentType<Object>)IntegerArgumentType.integer(0)), (commandContext, list, feedbackMessage) -> executeReplace(EntityArgumentType.getEntities(commandContext, "entities"), ItemSlotArgumentType.getItemSlot(commandContext, "slot"), IntegerArgumentType.getInteger((CommandContext)commandContext, "count"), list, feedbackMessage))))))).then(CommandManager.literal("block").then(CommandManager.argument("targetPos", (com.mojang.brigadier.arguments.ArgumentType<Object>)BlockPosArgumentType.create()).then(sourceConstructor.construct(CommandManager.argument("slot", (com.mojang.brigadier.arguments.ArgumentType<Object>)ItemSlotArgumentType.create()), (commandContext, list, feedbackMessage) -> executeBlock((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos(commandContext, "targetPos"), ItemSlotArgumentType.getItemSlot(commandContext, "slot"), list.size(), list, feedbackMessage)).then((ArgumentBuilder)sourceConstructor.construct(CommandManager.argument("count", (com.mojang.brigadier.arguments.ArgumentType<Object>)IntegerArgumentType.integer(0)), (commandContext, list, feedbackMessage) -> executeBlock((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos(commandContext, "targetPos"), IntegerArgumentType.getInteger((CommandContext)commandContext, "slot"), IntegerArgumentType.getInteger((CommandContext)commandContext, "count"), list, feedbackMessage))))))).then(CommandManager.literal("insert").then((ArgumentBuilder)sourceConstructor.construct(CommandManager.argument("targetPos", (com.mojang.brigadier.arguments.ArgumentType<Object>)BlockPosArgumentType.create()), (commandContext, list, feedbackMessage) -> executeInsert((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos(commandContext, "targetPos"), list, feedbackMessage)))).then(CommandManager.literal("give").then((ArgumentBuilder)sourceConstructor.construct(CommandManager.argument("players", (com.mojang.brigadier.arguments.ArgumentType<Object>)EntityArgumentType.players()), (commandContext, list, feedbackMessage) -> executeGive(EntityArgumentType.getPlayers(commandContext, "players"), list, feedbackMessage)))).then(CommandManager.literal("spawn").then((ArgumentBuilder)sourceConstructor.construct(CommandManager.argument("targetPos", (com.mojang.brigadier.arguments.ArgumentType<Object>)Vec3ArgumentType.create()), (commandContext, list, feedbackMessage) -> executeSpawn((ServerCommandSource)commandContext.getSource(), Vec3ArgumentType.getVec3(commandContext, "targetPos"), list, feedbackMessage))));
    }
    
    private static Inventory getBlockInventory(final ServerCommandSource source, final BlockPos pos) throws CommandSyntaxException {
        final BlockEntity blockEntity3 = source.getWorld().getBlockEntity(pos);
        if (!(blockEntity3 instanceof Inventory)) {
            throw ReplaceItemCommand.BLOCK_FAILED_EXCEPTION.create();
        }
        return (Inventory)blockEntity3;
    }
    
    private static int executeInsert(final ServerCommandSource source, final BlockPos targetPos, final List<ItemStack> stacks, final FeedbackMessage messageSender) throws CommandSyntaxException {
        final Inventory inventory5 = getBlockInventory(source, targetPos);
        final List<ItemStack> list6 = Lists.newArrayListWithCapacity(stacks.size());
        for (final ItemStack itemStack8 : stacks) {
            if (insert(inventory5, itemStack8.copy())) {
                inventory5.markDirty();
                list6.add(itemStack8);
            }
        }
        messageSender.accept(list6);
        return list6.size();
    }
    
    private static boolean insert(final Inventory inventory, final ItemStack stack) {
        boolean boolean3 = false;
        for (int integer4 = 0; integer4 < inventory.getInvSize() && !stack.isEmpty(); ++integer4) {
            final ItemStack itemStack5 = inventory.getInvStack(integer4);
            if (inventory.isValidInvStack(integer4, stack)) {
                if (itemStack5.isEmpty()) {
                    inventory.setInvStack(integer4, stack);
                    boolean3 = true;
                    break;
                }
                if (itemsMatch(itemStack5, stack)) {
                    final int integer5 = stack.getMaxAmount() - itemStack5.getAmount();
                    final int integer6 = Math.min(stack.getAmount(), integer5);
                    stack.subtractAmount(integer6);
                    itemStack5.addAmount(integer6);
                    boolean3 = true;
                }
            }
        }
        return boolean3;
    }
    
    private static int executeBlock(final ServerCommandSource source, final BlockPos targetPos, final int slot, final int stackCount, final List<ItemStack> stacks, final FeedbackMessage messageSender) throws CommandSyntaxException {
        final Inventory inventory7 = getBlockInventory(source, targetPos);
        final int integer8 = inventory7.getInvSize();
        if (slot < 0 || slot >= integer8) {
            throw ReplaceItemCommand.SLOT_INAPPLICABLE_EXCEPTION.create(slot);
        }
        final List<ItemStack> list9 = Lists.newArrayListWithCapacity(stacks.size());
        for (int integer9 = 0; integer9 < stackCount; ++integer9) {
            final int integer10 = slot + integer9;
            final ItemStack itemStack12 = (integer9 < stacks.size()) ? stacks.get(integer9) : ItemStack.EMPTY;
            if (inventory7.isValidInvStack(integer10, itemStack12)) {
                inventory7.setInvStack(integer10, itemStack12);
                list9.add(itemStack12);
            }
        }
        messageSender.accept(list9);
        return list9.size();
    }
    
    private static boolean itemsMatch(final ItemStack first, final ItemStack second) {
        return first.getItem() == second.getItem() && first.getDamage() == second.getDamage() && first.getAmount() <= first.getMaxAmount() && Objects.equals(first.getTag(), second.getTag());
    }
    
    private static int executeGive(final Collection<ServerPlayerEntity> players, final List<ItemStack> stacks, final FeedbackMessage messageSender) throws CommandSyntaxException {
        final List<ItemStack> list4 = Lists.newArrayListWithCapacity(stacks.size());
        for (final ItemStack itemStack6 : stacks) {
            for (final ServerPlayerEntity serverPlayerEntity8 : players) {
                if (serverPlayerEntity8.inventory.insertStack(itemStack6.copy())) {
                    list4.add(itemStack6);
                }
            }
        }
        messageSender.accept(list4);
        return list4.size();
    }
    
    private static void replace(final Entity entity, final List<ItemStack> stacks, final int slot, final int stackCount, final List<ItemStack> addedStacks) {
        for (int integer6 = 0; integer6 < stackCount; ++integer6) {
            final ItemStack itemStack7 = (integer6 < stacks.size()) ? stacks.get(integer6) : ItemStack.EMPTY;
            if (entity.equip(slot + integer6, itemStack7.copy())) {
                addedStacks.add(itemStack7);
            }
        }
    }
    
    private static int executeReplace(final Collection<? extends Entity> targets, final int slot, final int stackCount, final List<ItemStack> stacks, final FeedbackMessage messageSender) throws CommandSyntaxException {
        final List<ItemStack> list6 = Lists.newArrayListWithCapacity(stacks.size());
        for (final Entity entity8 : targets) {
            if (entity8 instanceof ServerPlayerEntity) {
                final ServerPlayerEntity serverPlayerEntity9 = (ServerPlayerEntity)entity8;
                serverPlayerEntity9.playerContainer.sendContentUpdates();
                replace(entity8, stacks, slot, stackCount, list6);
                serverPlayerEntity9.playerContainer.sendContentUpdates();
            }
            else {
                replace(entity8, stacks, slot, stackCount, list6);
            }
        }
        messageSender.accept(list6);
        return list6.size();
    }
    
    private static int executeSpawn(final ServerCommandSource source, final Vec3d pos, final List<ItemStack> stacks, final FeedbackMessage messageSender) throws CommandSyntaxException {
        final ServerWorld serverWorld5 = source.getWorld();
        final ServerWorld world;
        final ItemEntity itemEntity4;
        stacks.forEach(itemStack -> {
            itemEntity4 = new ItemEntity(world, pos.x, pos.y, pos.z, itemStack.copy());
            itemEntity4.setToDefaultPickupDelay();
            world.spawnEntity(itemEntity4);
            return;
        });
        messageSender.accept(stacks);
        return stacks.size();
    }
    
    private static void sendDroppedFeedback(final ServerCommandSource source, final List<ItemStack> stacks) {
        if (stacks.size() == 1) {
            final ItemStack itemStack3 = stacks.get(0);
            source.sendFeedback(new TranslatableTextComponent("commands.drop.success.single", new Object[] { itemStack3.getAmount(), itemStack3.toTextComponent() }), false);
        }
        else {
            source.sendFeedback(new TranslatableTextComponent("commands.drop.success.multiple", new Object[] { stacks.size() }), false);
        }
    }
    
    private static void sendDroppedFeedback(final ServerCommandSource source, final List<ItemStack> stacks, final Identifier lootTable) {
        if (stacks.size() == 1) {
            final ItemStack itemStack4 = stacks.get(0);
            source.sendFeedback(new TranslatableTextComponent("commands.drop.success.single_with_table", new Object[] { itemStack4.getAmount(), itemStack4.toTextComponent(), lootTable }), false);
        }
        else {
            source.sendFeedback(new TranslatableTextComponent("commands.drop.success.multiple_with_table", new Object[] { stacks.size(), lootTable }), false);
        }
    }
    
    private static ItemStack getHeldItem(final ServerCommandSource source, final EquipmentSlot slot) throws CommandSyntaxException {
        final Entity entity3 = source.getEntityOrThrow();
        if (entity3 instanceof LivingEntity) {
            return ((LivingEntity)entity3).getEquippedStack(slot);
        }
        throw LootCommand.NO_HELD_ITEMS_EXCEPTION.create(entity3.getDisplayName());
    }
    
    private static int executeMine(final CommandContext<ServerCommandSource> context, final BlockPos pos, final ItemStack stack, final Target constructor) throws CommandSyntaxException {
        final ServerCommandSource serverCommandSource5 = (ServerCommandSource)context.getSource();
        final ServerWorld serverWorld6 = serverCommandSource5.getWorld();
        final BlockState blockState7 = serverWorld6.getBlockState(pos);
        final BlockEntity blockEntity8 = serverWorld6.getBlockEntity(pos);
        final LootContext.Builder builder9 = new LootContext.Builder(serverWorld6).<BlockPos>put(LootContextParameters.f, pos).<BlockState>put(LootContextParameters.g, blockState7).<BlockEntity>putNullable(LootContextParameters.h, blockEntity8).<Entity>putNullable(LootContextParameters.a, serverCommandSource5.getEntity()).<ItemStack>put(LootContextParameters.i, stack);
        final List<ItemStack> list2 = blockState7.getDroppedStacks(builder9);
        return constructor.accept(context, list2, list -> sendDroppedFeedback(serverCommandSource5, list, blockState7.getBlock().getDropTableId()));
    }
    
    private static int executeKill(final CommandContext<ServerCommandSource> context, final Entity entity, final Target constructor) throws CommandSyntaxException {
        if (!(entity instanceof LivingEntity)) {
            throw LootCommand.NO_LOOT_TABLE_EXCEPTION.create(entity.getDisplayName());
        }
        final Identifier identifier4 = ((LivingEntity)entity).getLootTable();
        final ServerCommandSource serverCommandSource5 = (ServerCommandSource)context.getSource();
        final LootContext.Builder builder6 = new LootContext.Builder(serverCommandSource5.getWorld());
        final Entity entity2 = serverCommandSource5.getEntity();
        if (entity2 instanceof PlayerEntity) {
            builder6.<PlayerEntity>put(LootContextParameters.b, (PlayerEntity)entity2);
        }
        builder6.<DamageSource>put(LootContextParameters.c, DamageSource.MAGIC);
        builder6.<Entity>putNullable(LootContextParameters.e, entity2);
        builder6.<Entity>putNullable(LootContextParameters.d, entity2);
        builder6.<Entity>put(LootContextParameters.a, entity);
        builder6.<BlockPos>put(LootContextParameters.f, new BlockPos(serverCommandSource5.getPosition()));
        final LootSupplier lootSupplier8 = serverCommandSource5.getMinecraftServer().getLootManager().getSupplier(identifier4);
        final List<ItemStack> list2 = lootSupplier8.getDrops(builder6.build(LootContextTypes.ENTITY));
        return constructor.accept(context, list2, list -> sendDroppedFeedback(serverCommandSource5, list, identifier4));
    }
    
    private static int executeLoot(final CommandContext<ServerCommandSource> context, final Identifier lootTable, final Target constructor) throws CommandSyntaxException {
        final ServerCommandSource serverCommandSource4 = (ServerCommandSource)context.getSource();
        final LootContext.Builder builder5 = new LootContext.Builder(serverCommandSource4.getWorld()).<Entity>putNullable(LootContextParameters.a, serverCommandSource4.getEntity()).<BlockPos>put(LootContextParameters.f, new BlockPos(serverCommandSource4.getPosition()));
        return getFeedbackMessageSingle(context, lootTable, builder5.build(LootContextTypes.CHEST), constructor);
    }
    
    private static int executeFish(final CommandContext<ServerCommandSource> context, final Identifier lootTable, final BlockPos pos, final ItemStack stack, final Target constructor) throws CommandSyntaxException {
        final ServerCommandSource serverCommandSource6 = (ServerCommandSource)context.getSource();
        final LootContext lootContext7 = new LootContext.Builder(serverCommandSource6.getWorld()).<BlockPos>put(LootContextParameters.f, pos).<ItemStack>put(LootContextParameters.i, stack).build(LootContextTypes.FISHING);
        return getFeedbackMessageSingle(context, lootTable, lootContext7, constructor);
    }
    
    private static int getFeedbackMessageSingle(final CommandContext<ServerCommandSource> context, final Identifier lootTable, final LootContext lootContext, final Target constructor) throws CommandSyntaxException {
        final ServerCommandSource serverCommandSource5 = (ServerCommandSource)context.getSource();
        final LootSupplier lootSupplier6 = serverCommandSource5.getMinecraftServer().getLootManager().getSupplier(lootTable);
        final List<ItemStack> list2 = lootSupplier6.getDrops(lootContext);
        return constructor.accept(context, list2, list -> sendDroppedFeedback(serverCommandSource5, list));
    }
    
    static {
        SUGGESTION_PROVIDER = ((commandContext, suggestionsBuilder) -> {
            final LootManager lootManager3 = ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getLootManager();
            return CommandSource.suggestIdentifiers(lootManager3.getSupplierNames(), suggestionsBuilder);
        });
        final TranslatableTextComponent translatableTextComponent;
        NO_HELD_ITEMS_EXCEPTION = new DynamicCommandExceptionType(object -> {
            new TranslatableTextComponent("commands.drop.no_held_items", new Object[] { object });
            return translatableTextComponent;
        });
        final TranslatableTextComponent translatableTextComponent2;
        NO_LOOT_TABLE_EXCEPTION = new DynamicCommandExceptionType(object -> {
            new TranslatableTextComponent("commands.drop.no_loot_table", new Object[] { object });
            return translatableTextComponent2;
        });
    }
    
    @FunctionalInterface
    interface SourceConstructor
    {
        ArgumentBuilder<ServerCommandSource, ?> construct(final ArgumentBuilder<ServerCommandSource, ?> arg1, final Target arg2);
    }
    
    @FunctionalInterface
    interface Target
    {
        int accept(final CommandContext<ServerCommandSource> arg1, final List<ItemStack> arg2, final FeedbackMessage arg3) throws CommandSyntaxException;
    }
    
    @FunctionalInterface
    interface FeedbackMessage
    {
        void accept(final List<ItemStack> arg1) throws CommandSyntaxException;
    }
}

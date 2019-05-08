package net.minecraft.server.command;

import com.mojang.brigadier.Message;
import java.util.List;
import net.minecraft.entity.Entity;
import com.google.common.collect.Lists;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ByteTag;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.server.world.ServerWorld;
import java.util.OptionalInt;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import java.util.Collections;
import net.minecraft.util.NumberRange;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import java.util.function.BiPredicate;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.Command;
import net.minecraft.command.arguments.NumberRangeArgumentType;
import net.minecraft.world.ViewableWorld;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.command.arguments.BlockPredicateArgumentType;
import net.minecraft.command.arguments.BlockPosArgumentType;
import net.minecraft.nbt.Tag;
import java.util.function.IntFunction;
import net.minecraft.command.DataCommandObject;
import net.minecraft.entity.boss.CommandBossBar;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import java.util.Collection;
import java.util.Iterator;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.minecraft.command.arguments.NbtPathArgumentType;
import net.minecraft.command.arguments.IdentifierArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.command.arguments.ObjectiveArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.command.arguments.ScoreHolderArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.command.arguments.DimensionArgumentType;
import net.minecraft.command.arguments.SwizzleArgumentType;
import net.minecraft.command.arguments.EntityAnchorArgumentType;
import net.minecraft.command.arguments.RotationArgumentType;
import net.minecraft.command.arguments.Vec3ArgumentType;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.command.arguments.EntityArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ResultConsumer;
import java.util.function.BinaryOperator;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;

public class ExecuteCommand
{
    private static final Dynamic2CommandExceptionType BLOCKS_TOOBIG_EXCEPTION;
    private static final SimpleCommandExceptionType CONDITIONAL_FAIL_EXCEPTION;
    private static final DynamicCommandExceptionType CONDITIONAL_FAIL_COUNT_EXCEPTION;
    private static final BinaryOperator<ResultConsumer<ServerCommandSource>> BINARY_RESULT_CONSUMER;
    
    public static void register(final CommandDispatcher<ServerCommandSource> dispatcher) {
        final LiteralCommandNode<ServerCommandSource> literalCommandNode2 = (LiteralCommandNode<ServerCommandSource>)dispatcher.register((LiteralArgumentBuilder)CommandManager.literal("execute").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2)));
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("execute").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).then(CommandManager.literal("run").redirect((CommandNode)dispatcher.getRoot()))).then((ArgumentBuilder)addConditionArguments((CommandNode<ServerCommandSource>)literalCommandNode2, CommandManager.literal("if"), true))).then((ArgumentBuilder)addConditionArguments((CommandNode<ServerCommandSource>)literalCommandNode2, CommandManager.literal("unless"), false))).then(CommandManager.literal("as").then(CommandManager.argument("targets", (com.mojang.brigadier.arguments.ArgumentType<Object>)EntityArgumentType.entities()).fork((CommandNode)literalCommandNode2, commandContext -> {
            final List<ServerCommandSource> list2 = Lists.newArrayList();
            for (final Entity entity4 : EntityArgumentType.getOptionalEntities((CommandContext<ServerCommandSource>)commandContext, "targets")) {
                list2.add(((ServerCommandSource)commandContext.getSource()).withEntity(entity4));
            }
            return list2;
        })))).then(CommandManager.literal("at").then(CommandManager.argument("targets", (com.mojang.brigadier.arguments.ArgumentType<Object>)EntityArgumentType.entities()).fork((CommandNode)literalCommandNode2, commandContext -> {
            final List<ServerCommandSource> list2 = Lists.newArrayList();
            for (final Entity entity4 : EntityArgumentType.getOptionalEntities((CommandContext<ServerCommandSource>)commandContext, "targets")) {
                list2.add(((ServerCommandSource)commandContext.getSource()).withWorld((ServerWorld)entity4.world).withPosition(entity4.getPosVector()).withRotation(entity4.getRotationClient()));
            }
            return list2;
        })))).then(((LiteralArgumentBuilder)CommandManager.literal("store").then((ArgumentBuilder)addStoreArguments(literalCommandNode2, CommandManager.literal("result"), true))).then((ArgumentBuilder)addStoreArguments(literalCommandNode2, CommandManager.literal("success"), false)))).then(((LiteralArgumentBuilder)CommandManager.literal("positioned").then(CommandManager.argument("pos", (com.mojang.brigadier.arguments.ArgumentType<Object>)Vec3ArgumentType.create()).redirect((CommandNode)literalCommandNode2, commandContext -> ((ServerCommandSource)commandContext.getSource()).withPosition(Vec3ArgumentType.getVec3((CommandContext<ServerCommandSource>)commandContext, "pos"))))).then(CommandManager.literal("as").then(CommandManager.argument("targets", (com.mojang.brigadier.arguments.ArgumentType<Object>)EntityArgumentType.entities()).fork((CommandNode)literalCommandNode2, commandContext -> {
            final List<ServerCommandSource> list2 = Lists.newArrayList();
            for (final Entity entity4 : EntityArgumentType.getOptionalEntities((CommandContext<ServerCommandSource>)commandContext, "targets")) {
                list2.add(((ServerCommandSource)commandContext.getSource()).withPosition(entity4.getPosVector()));
            }
            return list2;
        }))))).then(((LiteralArgumentBuilder)CommandManager.literal("rotated").then(CommandManager.argument("rot", (com.mojang.brigadier.arguments.ArgumentType<Object>)RotationArgumentType.create()).redirect((CommandNode)literalCommandNode2, commandContext -> ((ServerCommandSource)commandContext.getSource()).withRotation(RotationArgumentType.getRotation((CommandContext<ServerCommandSource>)commandContext, "rot").toAbsoluteRotation((ServerCommandSource)commandContext.getSource()))))).then(CommandManager.literal("as").then(CommandManager.argument("targets", (com.mojang.brigadier.arguments.ArgumentType<Object>)EntityArgumentType.entities()).fork((CommandNode)literalCommandNode2, commandContext -> {
            final List<ServerCommandSource> list2 = Lists.newArrayList();
            for (final Entity entity4 : EntityArgumentType.getOptionalEntities((CommandContext<ServerCommandSource>)commandContext, "targets")) {
                list2.add(((ServerCommandSource)commandContext.getSource()).withRotation(entity4.getRotationClient()));
            }
            return list2;
        }))))).then(((LiteralArgumentBuilder)CommandManager.literal("facing").then(CommandManager.literal("entity").then(CommandManager.argument("targets", (com.mojang.brigadier.arguments.ArgumentType<Object>)EntityArgumentType.entities()).then(CommandManager.argument("anchor", (com.mojang.brigadier.arguments.ArgumentType<Object>)EntityAnchorArgumentType.create()).fork((CommandNode)literalCommandNode2, commandContext -> {
            final List<ServerCommandSource> list2 = Lists.newArrayList();
            final EntityAnchorArgumentType.EntityAnchor entityAnchor3 = EntityAnchorArgumentType.getEntityAnchor((CommandContext<ServerCommandSource>)commandContext, "anchor");
            for (final Entity entity5 : EntityArgumentType.getOptionalEntities((CommandContext<ServerCommandSource>)commandContext, "targets")) {
                list2.add(((ServerCommandSource)commandContext.getSource()).withLookingAt(entity5, entityAnchor3));
            }
            return list2;
        }))))).then(CommandManager.argument("pos", (com.mojang.brigadier.arguments.ArgumentType<Object>)Vec3ArgumentType.create()).redirect((CommandNode)literalCommandNode2, commandContext -> ((ServerCommandSource)commandContext.getSource()).withLookingAt(Vec3ArgumentType.getVec3((CommandContext<ServerCommandSource>)commandContext, "pos")))))).then(CommandManager.literal("align").then(CommandManager.argument("axes", (com.mojang.brigadier.arguments.ArgumentType<Object>)SwizzleArgumentType.create()).redirect((CommandNode)literalCommandNode2, commandContext -> ((ServerCommandSource)commandContext.getSource()).withPosition(((ServerCommandSource)commandContext.getSource()).getPosition().floorAlongAxes(SwizzleArgumentType.getSwizzle((CommandContext<ServerCommandSource>)commandContext, "axes"))))))).then(CommandManager.literal("anchored").then(CommandManager.argument("anchor", (com.mojang.brigadier.arguments.ArgumentType<Object>)EntityAnchorArgumentType.create()).redirect((CommandNode)literalCommandNode2, commandContext -> ((ServerCommandSource)commandContext.getSource()).withEntityAnchor(EntityAnchorArgumentType.getEntityAnchor((CommandContext<ServerCommandSource>)commandContext, "anchor")))))).then(CommandManager.literal("in").then(CommandManager.argument("dimension", (com.mojang.brigadier.arguments.ArgumentType<Object>)DimensionArgumentType.create()).redirect((CommandNode)literalCommandNode2, commandContext -> ((ServerCommandSource)commandContext.getSource()).withWorld(((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getWorld(DimensionArgumentType.getDimensionArgument((CommandContext<ServerCommandSource>)commandContext, "dimension")))))));
    }
    
    private static ArgumentBuilder<ServerCommandSource, ?> addStoreArguments(final LiteralCommandNode<ServerCommandSource> node, final LiteralArgumentBuilder<ServerCommandSource> builder, final boolean requestResult) {
        builder.then(CommandManager.literal("score").then(CommandManager.argument("targets", (com.mojang.brigadier.arguments.ArgumentType<Object>)ScoreHolderArgumentType.scoreHolders()).suggests((SuggestionProvider)ScoreHolderArgumentType.SUGGESTION_PROVIDER).then(CommandManager.argument("objective", (com.mojang.brigadier.arguments.ArgumentType<Object>)ObjectiveArgumentType.create()).redirect((CommandNode)node, commandContext -> executeStoreScore((ServerCommandSource)commandContext.getSource(), ScoreHolderArgumentType.getScoreboardScoreHolders((CommandContext<ServerCommandSource>)commandContext, "targets"), ObjectiveArgumentType.getObjective((CommandContext<ServerCommandSource>)commandContext, "objective"), requestResult)))));
        builder.then(CommandManager.literal("bossbar").then(((RequiredArgumentBuilder)CommandManager.argument("id", (com.mojang.brigadier.arguments.ArgumentType<Object>)IdentifierArgumentType.create()).suggests((SuggestionProvider)BossBarCommand.suggestionProvider).then(CommandManager.literal("value").redirect((CommandNode)node, commandContext -> executeStoreBossbar((ServerCommandSource)commandContext.getSource(), BossBarCommand.createBossBar((CommandContext<ServerCommandSource>)commandContext), true, requestResult)))).then(CommandManager.literal("max").redirect((CommandNode)node, commandContext -> executeStoreBossbar((ServerCommandSource)commandContext.getSource(), BossBarCommand.createBossBar((CommandContext<ServerCommandSource>)commandContext), false, requestResult)))));
        for (final DataCommand.ObjectType objectType5 : DataCommand.TARGET_OBJECT_TYPES) {
            final Object o;
            objectType5.addArgumentsToBuilder(builder, argumentBuilder -> argumentBuilder.then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("path", (com.mojang.brigadier.arguments.ArgumentType<Object>)NbtPathArgumentType.create()).then(CommandManager.literal("int").then(CommandManager.argument("scale", (com.mojang.brigadier.arguments.ArgumentType<Object>)DoubleArgumentType.doubleArg()).redirect((CommandNode)node, commandContext -> executeStoreData((ServerCommandSource)commandContext.getSource(), ((DataCommand.ObjectType)o).getObject((CommandContext<ServerCommandSource>)commandContext), NbtPathArgumentType.getNbtPath((CommandContext<ServerCommandSource>)commandContext, "path"), integer -> new IntTag((int)(integer * DoubleArgumentType.getDouble(commandContext, "scale"))), requestResult))))).then(CommandManager.literal("float").then(CommandManager.argument("scale", (com.mojang.brigadier.arguments.ArgumentType<Object>)DoubleArgumentType.doubleArg()).redirect((CommandNode)node, commandContext -> executeStoreData((ServerCommandSource)commandContext.getSource(), ((DataCommand.ObjectType)o).getObject((CommandContext<ServerCommandSource>)commandContext), NbtPathArgumentType.getNbtPath((CommandContext<ServerCommandSource>)commandContext, "path"), integer -> new FloatTag((float)(integer * DoubleArgumentType.getDouble(commandContext, "scale"))), requestResult))))).then(CommandManager.literal("short").then(CommandManager.argument("scale", (com.mojang.brigadier.arguments.ArgumentType<Object>)DoubleArgumentType.doubleArg()).redirect((CommandNode)node, commandContext -> executeStoreData((ServerCommandSource)commandContext.getSource(), ((DataCommand.ObjectType)o).getObject((CommandContext<ServerCommandSource>)commandContext), NbtPathArgumentType.getNbtPath((CommandContext<ServerCommandSource>)commandContext, "path"), integer -> new ShortTag((short)(integer * DoubleArgumentType.getDouble(commandContext, "scale"))), requestResult))))).then(CommandManager.literal("long").then(CommandManager.argument("scale", (com.mojang.brigadier.arguments.ArgumentType<Object>)DoubleArgumentType.doubleArg()).redirect((CommandNode)node, commandContext -> executeStoreData((ServerCommandSource)commandContext.getSource(), ((DataCommand.ObjectType)o).getObject((CommandContext<ServerCommandSource>)commandContext), NbtPathArgumentType.getNbtPath((CommandContext<ServerCommandSource>)commandContext, "path"), integer -> new LongTag((long)(integer * DoubleArgumentType.getDouble(commandContext, "scale"))), requestResult))))).then(CommandManager.literal("double").then(CommandManager.argument("scale", (com.mojang.brigadier.arguments.ArgumentType<Object>)DoubleArgumentType.doubleArg()).redirect((CommandNode)node, commandContext -> executeStoreData((ServerCommandSource)commandContext.getSource(), ((DataCommand.ObjectType)o).getObject((CommandContext<ServerCommandSource>)commandContext), NbtPathArgumentType.getNbtPath((CommandContext<ServerCommandSource>)commandContext, "path"), integer -> new DoubleTag(integer * DoubleArgumentType.getDouble(commandContext, "scale")), requestResult))))).then(CommandManager.literal("byte").then(CommandManager.argument("scale", (com.mojang.brigadier.arguments.ArgumentType<Object>)DoubleArgumentType.doubleArg()).redirect((CommandNode)node, commandContext -> executeStoreData((ServerCommandSource)commandContext.getSource(), ((DataCommand.ObjectType)o).getObject((CommandContext<ServerCommandSource>)commandContext), NbtPathArgumentType.getNbtPath((CommandContext<ServerCommandSource>)commandContext, "path"), integer -> new ByteTag((byte)(integer * DoubleArgumentType.getDouble(commandContext, "scale"))), requestResult))))));
        }
        return builder;
    }
    
    private static ServerCommandSource executeStoreScore(final ServerCommandSource source, final Collection<String> targets, final ScoreboardObjective objective, final boolean requestResult) {
        final Scoreboard scoreboard5 = source.getMinecraftServer().getScoreboard();
        return source.mergeConsumers((ResultConsumer<ServerCommandSource>)((commandContext, boolean6, integer) -> {
            for (final String string9 : targets) {
                final ScoreboardPlayerScore scoreboardPlayerScore10 = scoreboard5.getPlayerScore(string9, objective);
                final int integer2 = requestResult ? integer : (boolean6 ? 1 : 0);
                scoreboardPlayerScore10.setScore(integer2);
            }
        }), ExecuteCommand.BINARY_RESULT_CONSUMER);
    }
    
    private static ServerCommandSource executeStoreBossbar(final ServerCommandSource source, final CommandBossBar bossBar, final boolean storeInValue, final boolean requestResult) {
        return source.mergeConsumers((ResultConsumer<ServerCommandSource>)((commandContext, boolean5, integer) -> {
            final int integer2 = requestResult ? integer : (boolean5 ? 1 : 0);
            if (storeInValue) {
                bossBar.setValue(integer2);
            }
            else {
                bossBar.setMaxValue(integer2);
            }
        }), ExecuteCommand.BINARY_RESULT_CONSUMER);
    }
    
    private static ServerCommandSource executeStoreData(final ServerCommandSource source, final DataCommandObject object, final NbtPathArgumentType.NbtPath path, final IntFunction<Tag> tagSetter, final boolean requestResult) {
        return source.mergeConsumers((ResultConsumer<ServerCommandSource>)((commandContext, boolean6, integer) -> {
            try {
                final CompoundTag compoundTag8 = object.getTag();
                final int integer2 = requestResult ? integer : (boolean6 ? 1 : 0);
                path.put(compoundTag8, () -> tagSetter.apply(integer2));
                object.setTag(compoundTag8);
            }
            catch (CommandSyntaxException ex) {}
        }), ExecuteCommand.BINARY_RESULT_CONSUMER);
    }
    
    private static ArgumentBuilder<ServerCommandSource, ?> addConditionArguments(final CommandNode<ServerCommandSource> root, final LiteralArgumentBuilder<ServerCommandSource> argumentBuilder, final boolean positive) {
        ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)argumentBuilder.then(CommandManager.literal("block").then(CommandManager.argument("pos", (com.mojang.brigadier.arguments.ArgumentType<Object>)BlockPosArgumentType.create()).then((ArgumentBuilder)addConditionLogic(root, CommandManager.argument("block", (com.mojang.brigadier.arguments.ArgumentType<Object>)BlockPredicateArgumentType.create()), positive, commandContext -> BlockPredicateArgumentType.getBlockPredicate(commandContext, "block").test(new CachedBlockPosition(((ServerCommandSource)commandContext.getSource()).getWorld(), BlockPosArgumentType.getLoadedBlockPos(commandContext, "pos"), true))))))).then(CommandManager.literal("score").then(CommandManager.argument("target", (com.mojang.brigadier.arguments.ArgumentType<Object>)ScoreHolderArgumentType.scoreHolder()).suggests((SuggestionProvider)ScoreHolderArgumentType.SUGGESTION_PROVIDER).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("targetObjective", (com.mojang.brigadier.arguments.ArgumentType<Object>)ObjectiveArgumentType.create()).then(CommandManager.literal("=").then(CommandManager.argument("source", (com.mojang.brigadier.arguments.ArgumentType<Object>)ScoreHolderArgumentType.scoreHolder()).suggests((SuggestionProvider)ScoreHolderArgumentType.SUGGESTION_PROVIDER).then((ArgumentBuilder)addConditionLogic(root, CommandManager.argument("sourceObjective", (com.mojang.brigadier.arguments.ArgumentType<Object>)ObjectiveArgumentType.create()), positive, commandContext -> testScoreCondition(commandContext, Integer::equals)))))).then(CommandManager.literal("<").then(CommandManager.argument("source", (com.mojang.brigadier.arguments.ArgumentType<Object>)ScoreHolderArgumentType.scoreHolder()).suggests((SuggestionProvider)ScoreHolderArgumentType.SUGGESTION_PROVIDER).then((ArgumentBuilder)addConditionLogic(root, CommandManager.argument("sourceObjective", (com.mojang.brigadier.arguments.ArgumentType<Object>)ObjectiveArgumentType.create()), positive, commandContext -> testScoreCondition(commandContext, (integer1, integer2) -> integer1 < integer2)))))).then(CommandManager.literal("<=").then(CommandManager.argument("source", (com.mojang.brigadier.arguments.ArgumentType<Object>)ScoreHolderArgumentType.scoreHolder()).suggests((SuggestionProvider)ScoreHolderArgumentType.SUGGESTION_PROVIDER).then((ArgumentBuilder)addConditionLogic(root, CommandManager.argument("sourceObjective", (com.mojang.brigadier.arguments.ArgumentType<Object>)ObjectiveArgumentType.create()), positive, commandContext -> testScoreCondition(commandContext, (integer1, integer2) -> integer1 <= integer2)))))).then(CommandManager.literal(">").then(CommandManager.argument("source", (com.mojang.brigadier.arguments.ArgumentType<Object>)ScoreHolderArgumentType.scoreHolder()).suggests((SuggestionProvider)ScoreHolderArgumentType.SUGGESTION_PROVIDER).then((ArgumentBuilder)addConditionLogic(root, CommandManager.argument("sourceObjective", (com.mojang.brigadier.arguments.ArgumentType<Object>)ObjectiveArgumentType.create()), positive, commandContext -> testScoreCondition(commandContext, (integer1, integer2) -> integer1 > integer2)))))).then(CommandManager.literal(">=").then(CommandManager.argument("source", (com.mojang.brigadier.arguments.ArgumentType<Object>)ScoreHolderArgumentType.scoreHolder()).suggests((SuggestionProvider)ScoreHolderArgumentType.SUGGESTION_PROVIDER).then((ArgumentBuilder)addConditionLogic(root, CommandManager.argument("sourceObjective", (com.mojang.brigadier.arguments.ArgumentType<Object>)ObjectiveArgumentType.create()), positive, commandContext -> testScoreCondition(commandContext, (integer1, integer2) -> integer1 >= integer2)))))).then(CommandManager.literal("matches").then((ArgumentBuilder)addConditionLogic(root, CommandManager.argument("range", (com.mojang.brigadier.arguments.ArgumentType<Object>)NumberRangeArgumentType.create()), positive, commandContext -> testScoreMatch(commandContext, NumberRangeArgumentType.IntRangeArgumentType.getRangeArgument(commandContext, "range"))))))))).then(CommandManager.literal("blocks").then(CommandManager.argument("start", (com.mojang.brigadier.arguments.ArgumentType<Object>)BlockPosArgumentType.create()).then(CommandManager.argument("end", (com.mojang.brigadier.arguments.ArgumentType<Object>)BlockPosArgumentType.create()).then(((RequiredArgumentBuilder)CommandManager.argument("destination", (com.mojang.brigadier.arguments.ArgumentType<Object>)BlockPosArgumentType.create()).then((ArgumentBuilder)addBlocksConditionLogic(root, CommandManager.literal("all"), positive, false))).then((ArgumentBuilder)addBlocksConditionLogic(root, CommandManager.literal("masked"), positive, true))))))).then(CommandManager.literal("entity").then(((RequiredArgumentBuilder)CommandManager.argument("entities", (com.mojang.brigadier.arguments.ArgumentType<Object>)EntityArgumentType.entities()).fork((CommandNode)root, commandContext -> getSourceOrEmptyForConditionFork((CommandContext<ServerCommandSource>)commandContext, positive, !EntityArgumentType.getOptionalEntities((CommandContext<ServerCommandSource>)commandContext, "entities").isEmpty()))).executes((Command)getExistsConditionExecute(positive, commandContext -> EntityArgumentType.getOptionalEntities(commandContext, "entities").size()))));
        for (final DataCommand.ObjectType objectType5 : DataCommand.SOURCE_OBJECT_TYPES) {
            final DataCommand.ObjectType objectType6;
            argumentBuilder.then((ArgumentBuilder)objectType5.addArgumentsToBuilder(CommandManager.literal("data"), argumentBuilder -> argumentBuilder.then(((RequiredArgumentBuilder)CommandManager.argument("path", (com.mojang.brigadier.arguments.ArgumentType<Object>)NbtPathArgumentType.create()).fork((CommandNode)root, commandContext -> getSourceOrEmptyForConditionFork((CommandContext<ServerCommandSource>)commandContext, positive, countPathMatches(objectType6.getObject((CommandContext<ServerCommandSource>)commandContext), NbtPathArgumentType.getNbtPath((CommandContext<ServerCommandSource>)commandContext, "path")) > 0))).executes((Command)getExistsConditionExecute(positive, commandContext -> countPathMatches(objectType6.getObject(commandContext), NbtPathArgumentType.getNbtPath(commandContext, "path")))))));
        }
        return argumentBuilder;
    }
    
    private static Command<ServerCommandSource> getExistsConditionExecute(final boolean positive, final ExistsCondition condition) {
        if (positive) {
            return (Command<ServerCommandSource>)(commandContext -> {
                final int integer3 = condition.test((CommandContext<ServerCommandSource>)commandContext);
                if (integer3 > 0) {
                    ((ServerCommandSource)commandContext.getSource()).sendFeedback(new TranslatableTextComponent("commands.execute.conditional.pass_count", new Object[] { integer3 }), false);
                    return integer3;
                }
                throw ExecuteCommand.CONDITIONAL_FAIL_EXCEPTION.create();
            });
        }
        return (Command<ServerCommandSource>)(commandContext -> {
            final int integer3 = condition.test((CommandContext<ServerCommandSource>)commandContext);
            if (integer3 == 0) {
                ((ServerCommandSource)commandContext.getSource()).sendFeedback(new TranslatableTextComponent("commands.execute.conditional.pass", new Object[0]), false);
                return 1;
            }
            throw ExecuteCommand.CONDITIONAL_FAIL_COUNT_EXCEPTION.create(integer3);
        });
    }
    
    private static int countPathMatches(final DataCommandObject object, final NbtPathArgumentType.NbtPath path) throws CommandSyntaxException {
        return path.count(object.getTag());
    }
    
    private static boolean testScoreCondition(final CommandContext<ServerCommandSource> context, final BiPredicate<Integer, Integer> condition) throws CommandSyntaxException {
        final String string3 = ScoreHolderArgumentType.getScoreHolder(context, "target");
        final ScoreboardObjective scoreboardObjective4 = ObjectiveArgumentType.getObjective(context, "targetObjective");
        final String string4 = ScoreHolderArgumentType.getScoreHolder(context, "source");
        final ScoreboardObjective scoreboardObjective5 = ObjectiveArgumentType.getObjective(context, "sourceObjective");
        final Scoreboard scoreboard7 = ((ServerCommandSource)context.getSource()).getMinecraftServer().getScoreboard();
        if (!scoreboard7.playerHasObjective(string3, scoreboardObjective4) || !scoreboard7.playerHasObjective(string4, scoreboardObjective5)) {
            return false;
        }
        final ScoreboardPlayerScore scoreboardPlayerScore8 = scoreboard7.getPlayerScore(string3, scoreboardObjective4);
        final ScoreboardPlayerScore scoreboardPlayerScore9 = scoreboard7.getPlayerScore(string4, scoreboardObjective5);
        return condition.test(scoreboardPlayerScore8.getScore(), scoreboardPlayerScore9.getScore());
    }
    
    private static boolean testScoreMatch(final CommandContext<ServerCommandSource> context, final NumberRange.IntRange range) throws CommandSyntaxException {
        final String string3 = ScoreHolderArgumentType.getScoreHolder(context, "target");
        final ScoreboardObjective scoreboardObjective4 = ObjectiveArgumentType.getObjective(context, "targetObjective");
        final Scoreboard scoreboard5 = ((ServerCommandSource)context.getSource()).getMinecraftServer().getScoreboard();
        return scoreboard5.playerHasObjective(string3, scoreboardObjective4) && range.test(scoreboard5.getPlayerScore(string3, scoreboardObjective4).getScore());
    }
    
    private static Collection<ServerCommandSource> getSourceOrEmptyForConditionFork(final CommandContext<ServerCommandSource> context, final boolean positive, final boolean value) {
        if (value == positive) {
            return Collections.singleton(context.getSource());
        }
        return Collections.emptyList();
    }
    
    private static ArgumentBuilder<ServerCommandSource, ?> addConditionLogic(final CommandNode<ServerCommandSource> root, final ArgumentBuilder<ServerCommandSource, ?> builder, final boolean positive, final Condition condition) {
        return builder.fork((CommandNode)root, commandContext -> getSourceOrEmptyForConditionFork((CommandContext<ServerCommandSource>)commandContext, positive, condition.test((CommandContext<ServerCommandSource>)commandContext))).executes(commandContext -> {
            if (positive == condition.test((CommandContext<ServerCommandSource>)commandContext)) {
                ((ServerCommandSource)commandContext.getSource()).sendFeedback(new TranslatableTextComponent("commands.execute.conditional.pass", new Object[0]), false);
                return 1;
            }
            throw ExecuteCommand.CONDITIONAL_FAIL_EXCEPTION.create();
        });
    }
    
    private static ArgumentBuilder<ServerCommandSource, ?> addBlocksConditionLogic(final CommandNode<ServerCommandSource> root, final ArgumentBuilder<ServerCommandSource, ?> builder, final boolean positive, final boolean masked) {
        return builder.fork((CommandNode)root, commandContext -> getSourceOrEmptyForConditionFork((CommandContext<ServerCommandSource>)commandContext, positive, testBlocksCondition((CommandContext<ServerCommandSource>)commandContext, masked).isPresent())).executes(positive ? (commandContext -> executePositiveBlockCondition((CommandContext<ServerCommandSource>)commandContext, masked)) : (commandContext -> executeNegativeBlockCondition((CommandContext<ServerCommandSource>)commandContext, masked)));
    }
    
    private static int executePositiveBlockCondition(final CommandContext<ServerCommandSource> context, final boolean masked) throws CommandSyntaxException {
        final OptionalInt optionalInt3 = testBlocksCondition(context, masked);
        if (optionalInt3.isPresent()) {
            ((ServerCommandSource)context.getSource()).sendFeedback(new TranslatableTextComponent("commands.execute.conditional.pass_count", new Object[] { optionalInt3.getAsInt() }), false);
            return optionalInt3.getAsInt();
        }
        throw ExecuteCommand.CONDITIONAL_FAIL_EXCEPTION.create();
    }
    
    private static int executeNegativeBlockCondition(final CommandContext<ServerCommandSource> context, final boolean masked) throws CommandSyntaxException {
        final OptionalInt optionalInt3 = testBlocksCondition(context, masked);
        if (optionalInt3.isPresent()) {
            throw ExecuteCommand.CONDITIONAL_FAIL_COUNT_EXCEPTION.create(optionalInt3.getAsInt());
        }
        ((ServerCommandSource)context.getSource()).sendFeedback(new TranslatableTextComponent("commands.execute.conditional.pass", new Object[0]), false);
        return 1;
    }
    
    private static OptionalInt testBlocksCondition(final CommandContext<ServerCommandSource> context, final boolean masked) throws CommandSyntaxException {
        return testBlocksCondition(((ServerCommandSource)context.getSource()).getWorld(), BlockPosArgumentType.getLoadedBlockPos(context, "start"), BlockPosArgumentType.getLoadedBlockPos(context, "end"), BlockPosArgumentType.getLoadedBlockPos(context, "destination"), masked);
    }
    
    private static OptionalInt testBlocksCondition(final ServerWorld world, final BlockPos start, final BlockPos end, final BlockPos destination, final boolean masked) throws CommandSyntaxException {
        final MutableIntBoundingBox mutableIntBoundingBox6 = new MutableIntBoundingBox(start, end);
        final MutableIntBoundingBox mutableIntBoundingBox7 = new MutableIntBoundingBox(destination, destination.add(mutableIntBoundingBox6.getSize()));
        final BlockPos blockPos8 = new BlockPos(mutableIntBoundingBox7.minX - mutableIntBoundingBox6.minX, mutableIntBoundingBox7.minY - mutableIntBoundingBox6.minY, mutableIntBoundingBox7.minZ - mutableIntBoundingBox6.minZ);
        final int integer9 = mutableIntBoundingBox6.getBlockCountX() * mutableIntBoundingBox6.getBlockCountY() * mutableIntBoundingBox6.getBlockCountZ();
        if (integer9 > 32768) {
            throw ExecuteCommand.BLOCKS_TOOBIG_EXCEPTION.create(32768, integer9);
        }
        int integer10 = 0;
        for (int integer11 = mutableIntBoundingBox6.minZ; integer11 <= mutableIntBoundingBox6.maxZ; ++integer11) {
            for (int integer12 = mutableIntBoundingBox6.minY; integer12 <= mutableIntBoundingBox6.maxY; ++integer12) {
                for (int integer13 = mutableIntBoundingBox6.minX; integer13 <= mutableIntBoundingBox6.maxX; ++integer13) {
                    final BlockPos blockPos9 = new BlockPos(integer13, integer12, integer11);
                    final BlockPos blockPos10 = blockPos9.add(blockPos8);
                    final BlockState blockState16 = world.getBlockState(blockPos9);
                    if (!masked || blockState16.getBlock() != Blocks.AIR) {
                        if (blockState16 != world.getBlockState(blockPos10)) {
                            return OptionalInt.empty();
                        }
                        final BlockEntity blockEntity17 = world.getBlockEntity(blockPos9);
                        final BlockEntity blockEntity18 = world.getBlockEntity(blockPos10);
                        if (blockEntity17 != null) {
                            if (blockEntity18 == null) {
                                return OptionalInt.empty();
                            }
                            final CompoundTag compoundTag19 = blockEntity17.toTag(new CompoundTag());
                            compoundTag19.remove("x");
                            compoundTag19.remove("y");
                            compoundTag19.remove("z");
                            final CompoundTag compoundTag20 = blockEntity18.toTag(new CompoundTag());
                            compoundTag20.remove("x");
                            compoundTag20.remove("y");
                            compoundTag20.remove("z");
                            if (!compoundTag19.equals(compoundTag20)) {
                                return OptionalInt.empty();
                            }
                        }
                        ++integer10;
                    }
                }
            }
        }
        return OptionalInt.of(integer10);
    }
    
    static {
        BLOCKS_TOOBIG_EXCEPTION = new Dynamic2CommandExceptionType((object1, object2) -> new TranslatableTextComponent("commands.execute.blocks.toobig", new Object[] { object1, object2 }));
        CONDITIONAL_FAIL_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("commands.execute.conditional.fail", new Object[0]));
        final TranslatableTextComponent translatableTextComponent;
        CONDITIONAL_FAIL_COUNT_EXCEPTION = new DynamicCommandExceptionType(object -> {
            new TranslatableTextComponent("commands.execute.conditional.fail_count", new Object[] { object });
            return translatableTextComponent;
        });
        BINARY_RESULT_CONSUMER = (BinaryOperator)((resultConsumer1, resultConsumer2) -> (commandContext, boolean4, integer) -> {
            resultConsumer1.onCommandComplete(commandContext, boolean4, integer);
            resultConsumer2.onCommandComplete(commandContext, boolean4, integer);
        });
    }
    
    @FunctionalInterface
    interface ExistsCondition
    {
        int test(final CommandContext<ServerCommandSource> arg1) throws CommandSyntaxException;
    }
    
    @FunctionalInterface
    interface Condition
    {
        boolean test(final CommandContext<ServerCommandSource> arg1) throws CommandSyntaxException;
    }
}

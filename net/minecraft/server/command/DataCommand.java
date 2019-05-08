package net.minecraft.server.command;

import java.util.stream.Collector;
import com.google.common.collect.ImmutableList;
import net.minecraft.command.BlockDataObject;
import net.minecraft.command.EntityDataObject;
import net.minecraft.text.TranslatableTextComponent;
import com.mojang.brigadier.Message;
import java.util.Collections;
import net.minecraft.nbt.StringTag;
import net.minecraft.util.math.MathHelper;
import net.minecraft.nbt.AbstractNumberTag;
import net.minecraft.command.DataCommandObject;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.arguments.NbtTagArgumentType;
import java.util.function.BiConsumer;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.AbstractListTag;
import net.minecraft.nbt.ListTag;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Supplier;
import net.minecraft.nbt.CompoundTag;
import com.google.common.collect.Iterables;
import net.minecraft.nbt.Tag;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.minecraft.command.arguments.NbtPathArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.command.arguments.NbtCompoundTagArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.CommandDispatcher;
import java.util.function.Function;
import java.util.List;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class DataCommand
{
    private static final SimpleCommandExceptionType MERGE_FAILED_EXCEPTION;
    private static final DynamicCommandExceptionType GET_INVALID_EXCEPTION;
    private static final DynamicCommandExceptionType GET_UNKNOWN_EXCEPTION;
    private static final SimpleCommandExceptionType GET_MULTIPLE_EXCEPTION;
    private static final DynamicCommandExceptionType MODIFY_EXPECTED_LIST_EXCEPTION;
    private static final DynamicCommandExceptionType MODIFY_EXPECTED_OBJECT_EXCEPTION;
    private static final DynamicCommandExceptionType MODIFY_INVALID_INDEX_EXCEPTION;
    public static final List<Function<String, ObjectType>> OBJECT_TYPES;
    public static final List<ObjectType> TARGET_OBJECT_TYPES;
    public static final List<ObjectType> SOURCE_OBJECT_TYPES;
    
    public static void register(final CommandDispatcher<ServerCommandSource> dispatcher) {
        final LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder2 = (LiteralArgumentBuilder<ServerCommandSource>)CommandManager.literal("data").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2));
        for (final ObjectType objectType4 : DataCommand.TARGET_OBJECT_TYPES) {
            final Object o;
            final int integer5;
            final Collection<Tag> collection5;
            int integer6;
            final Iterator<Tag> iterator2;
            Tag tag8;
            CompoundTag compoundTag2;
            CompoundTag compoundTag3;
            final Iterator<Tag> iterator3;
            Tag tag9;
            ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)literalArgumentBuilder2.then((ArgumentBuilder)objectType4.addArgumentsToBuilder(CommandManager.literal("merge"), argumentBuilder -> argumentBuilder.then(CommandManager.argument("nbt", (com.mojang.brigadier.arguments.ArgumentType<Object>)NbtCompoundTagArgumentType.create()).executes(commandContext -> executeMerge((ServerCommandSource)commandContext.getSource(), objectType4.getObject((CommandContext<ServerCommandSource>)commandContext), NbtCompoundTagArgumentType.getCompoundTag((com.mojang.brigadier.context.CommandContext<Object>)commandContext, "nbt"))))))).then((ArgumentBuilder)objectType4.addArgumentsToBuilder(CommandManager.literal("get"), argumentBuilder -> argumentBuilder.executes(commandContext -> executeGet((ServerCommandSource)commandContext.getSource(), ((ObjectType)o).getObject((CommandContext<ServerCommandSource>)commandContext))).then(((RequiredArgumentBuilder)CommandManager.argument("path", (com.mojang.brigadier.arguments.ArgumentType<Object>)NbtPathArgumentType.create()).executes(commandContext -> executeGet((ServerCommandSource)commandContext.getSource(), ((ObjectType)o).getObject((CommandContext<ServerCommandSource>)commandContext), NbtPathArgumentType.getNbtPath((CommandContext<ServerCommandSource>)commandContext, "path")))).then(CommandManager.argument("scale", (com.mojang.brigadier.arguments.ArgumentType<Object>)DoubleArgumentType.doubleArg()).executes(commandContext -> executeGet((ServerCommandSource)commandContext.getSource(), ((ObjectType)o).getObject((CommandContext<ServerCommandSource>)commandContext), NbtPathArgumentType.getNbtPath((CommandContext<ServerCommandSource>)commandContext, "path"), DoubleArgumentType.getDouble(commandContext, "scale")))))))).then((ArgumentBuilder)objectType4.addArgumentsToBuilder(CommandManager.literal("remove"), argumentBuilder -> argumentBuilder.then(CommandManager.argument("path", (com.mojang.brigadier.arguments.ArgumentType<Object>)NbtPathArgumentType.create()).executes(commandContext -> executeRemove((ServerCommandSource)commandContext.getSource(), objectType4.getObject((CommandContext<ServerCommandSource>)commandContext), NbtPathArgumentType.getNbtPath((CommandContext<ServerCommandSource>)commandContext, "path"))))))).then((ArgumentBuilder)addModifyArgument((argumentBuilder, modifyArgumentCreator) -> argumentBuilder.then(CommandManager.literal("insert").then(CommandManager.argument("index", (com.mojang.brigadier.arguments.ArgumentType<Object>)IntegerArgumentType.integer()).then((ArgumentBuilder)modifyArgumentCreator.create((commandContext, compoundTag, nbtPath, list) -> {
                integer5 = IntegerArgumentType.getInteger(commandContext, "index");
                return executeInsert(integer5, compoundTag, nbtPath, list);
            })))).then(CommandManager.literal("prepend").then((ArgumentBuilder)modifyArgumentCreator.create((commandContext, compoundTag, nbtPath, list) -> executeInsert(0, compoundTag, nbtPath, list)))).then(CommandManager.literal("append").then((ArgumentBuilder)modifyArgumentCreator.create((commandContext, compoundTag, nbtPath, list) -> executeInsert(-1, compoundTag, nbtPath, list)))).then(CommandManager.literal("set").then((ArgumentBuilder)modifyArgumentCreator.create((commandContext, compoundTag, nbtPath, list) -> nbtPath.put(compoundTag, (Tag)Iterables.<Tag>getLast(list)::copy)))).then(CommandManager.literal("merge").then((ArgumentBuilder)modifyArgumentCreator.create((commandContext, compoundTag, nbtPath, list) -> {
                collection5 = nbtPath.putIfAbsent(compoundTag, (Supplier<Tag>)CompoundTag::new);
                integer6 = 0;
                collection5.iterator();
                while (iterator2.hasNext()) {
                    tag8 = iterator2.next();
                    if (!(tag8 instanceof CompoundTag)) {
                        throw DataCommand.MODIFY_EXPECTED_OBJECT_EXCEPTION.create(tag8);
                    }
                    else {
                        compoundTag2 = (CompoundTag)tag8;
                        compoundTag3 = compoundTag2.copy();
                        list.iterator();
                        while (iterator3.hasNext()) {
                            tag9 = iterator3.next();
                            if (!(tag9 instanceof CompoundTag)) {
                                throw DataCommand.MODIFY_EXPECTED_OBJECT_EXCEPTION.create(tag9);
                            }
                            else {
                                compoundTag2.copyFrom((CompoundTag)tag9);
                            }
                        }
                        integer6 += (compoundTag3.equals(compoundTag2) ? 0 : 1);
                    }
                }
                return integer6;
            })))));
        }
        dispatcher.register((LiteralArgumentBuilder)literalArgumentBuilder2);
    }
    
    private static int executeInsert(final int integer, final CompoundTag sourceTag, final NbtPathArgumentType.NbtPath path, final List<Tag> tags) throws CommandSyntaxException {
        final Collection<Tag> collection5 = path.putIfAbsent(sourceTag, (Supplier<Tag>)ListTag::new);
        int integer2 = 0;
        for (final Tag tag8 : collection5) {
            if (!(tag8 instanceof AbstractListTag)) {
                throw DataCommand.MODIFY_EXPECTED_LIST_EXCEPTION.create(tag8);
            }
            boolean boolean9 = false;
            final AbstractListTag<?> abstractListTag10 = tag8;
            int integer3 = (integer < 0) ? (abstractListTag10.size() + integer + 1) : integer;
            for (final Tag tag9 : tags) {
                try {
                    if (!abstractListTag10.addTag(integer3, tag9.copy())) {
                        continue;
                    }
                    ++integer3;
                    boolean9 = true;
                }
                catch (IndexOutOfBoundsException indexOutOfBoundsException14) {
                    throw DataCommand.MODIFY_INVALID_INDEX_EXCEPTION.create(integer3);
                }
            }
            integer2 += (boolean9 ? 1 : 0);
        }
        return integer2;
    }
    
    private static ArgumentBuilder<ServerCommandSource, ?> addModifyArgument(final BiConsumer<ArgumentBuilder<ServerCommandSource, ?>, ModifyArgumentCreator> subArgumentAdder) {
        final LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder2 = CommandManager.literal("modify");
        for (final ObjectType objectType4 : DataCommand.TARGET_OBJECT_TYPES) {
            final ArgumentBuilder<ServerCommandSource, ?> argumentBuilder2;
            final Iterator<ObjectType> iterator2;
            ObjectType objectType5;
            final ObjectType objectType6;
            final Object o;
            final Object o2;
            final Object o3;
            objectType4.addArgumentsToBuilder(literalArgumentBuilder2, argumentBuilder -> {
                argumentBuilder2 = CommandManager.argument("targetPath", (com.mojang.brigadier.arguments.ArgumentType<Object>)NbtPathArgumentType.create());
                DataCommand.SOURCE_OBJECT_TYPES.iterator();
                while (iterator2.hasNext()) {
                    objectType5 = iterator2.next();
                    subArgumentAdder.accept(argumentBuilder2, modifyOperation -> objectType6.addArgumentsToBuilder(CommandManager.literal("from"), argumentBuilder -> argumentBuilder.executes(commandContext -> {
                        final List<Tag> list5 = Collections.singletonList(((ObjectType)o).getObject((CommandContext<ServerCommandSource>)commandContext).getTag());
                        return executeModify((CommandContext<ServerCommandSource>)commandContext, o2, modifyOperation, list5);
                    }).then(CommandManager.argument("sourcePath", (com.mojang.brigadier.arguments.ArgumentType<Object>)NbtPathArgumentType.create()).executes(commandContext -> {
                        final DataCommandObject dataCommandObject5 = ((ObjectType)o).getObject((CommandContext<ServerCommandSource>)commandContext);
                        final NbtPathArgumentType.NbtPath nbtPath6 = NbtPathArgumentType.getNbtPath((CommandContext<ServerCommandSource>)commandContext, "sourcePath");
                        final List<Tag> list7 = nbtPath6.get(dataCommandObject5.getTag());
                        return executeModify((CommandContext<ServerCommandSource>)commandContext, o2, modifyOperation, list7);
                    }))));
                }
                subArgumentAdder.accept(argumentBuilder2, modifyOperation -> (LiteralArgumentBuilder)CommandManager.literal("value").then(CommandManager.argument("value", (com.mojang.brigadier.arguments.ArgumentType<Object>)NbtTagArgumentType.create()).executes(commandContext -> {
                    final List<Tag> list4 = Collections.<Tag>singletonList(NbtTagArgumentType.getTag((com.mojang.brigadier.context.CommandContext<Object>)commandContext, "value"));
                    return executeModify((CommandContext<ServerCommandSource>)commandContext, o3, modifyOperation, list4);
                })));
                return argumentBuilder.then((ArgumentBuilder)argumentBuilder2);
            });
        }
        return literalArgumentBuilder2;
    }
    
    private static int executeModify(final CommandContext<ServerCommandSource> context, final ObjectType objectType, final ModifyOperation modifier, final List<Tag> tags) throws CommandSyntaxException {
        final DataCommandObject dataCommandObject5 = objectType.getObject(context);
        final NbtPathArgumentType.NbtPath nbtPath6 = NbtPathArgumentType.getNbtPath(context, "targetPath");
        final CompoundTag compoundTag7 = dataCommandObject5.getTag();
        final int integer8 = modifier.modify(context, compoundTag7, nbtPath6, tags);
        if (integer8 == 0) {
            throw DataCommand.MERGE_FAILED_EXCEPTION.create();
        }
        dataCommandObject5.setTag(compoundTag7);
        ((ServerCommandSource)context.getSource()).sendFeedback(dataCommandObject5.getModifiedFeedback(), true);
        return integer8;
    }
    
    private static int executeRemove(final ServerCommandSource source, final DataCommandObject object, final NbtPathArgumentType.NbtPath path) throws CommandSyntaxException {
        final CompoundTag compoundTag4 = object.getTag();
        final int integer5 = path.remove(compoundTag4);
        if (integer5 == 0) {
            throw DataCommand.MERGE_FAILED_EXCEPTION.create();
        }
        object.setTag(compoundTag4);
        source.sendFeedback(object.getModifiedFeedback(), true);
        return integer5;
    }
    
    private static Tag getTag(final NbtPathArgumentType.NbtPath path, final DataCommandObject object) throws CommandSyntaxException {
        final Collection<Tag> collection3 = path.get(object.getTag());
        final Iterator<Tag> iterator4 = collection3.iterator();
        final Tag tag5 = iterator4.next();
        if (iterator4.hasNext()) {
            throw DataCommand.GET_MULTIPLE_EXCEPTION.create();
        }
        return tag5;
    }
    
    private static int executeGet(final ServerCommandSource source, final DataCommandObject object, final NbtPathArgumentType.NbtPath path) throws CommandSyntaxException {
        final Tag tag4 = getTag(path, object);
        int integer5;
        if (tag4 instanceof AbstractNumberTag) {
            integer5 = MathHelper.floor(((AbstractNumberTag)tag4).getDouble());
        }
        else if (tag4 instanceof AbstractListTag) {
            integer5 = ((AbstractListTag)tag4).size();
        }
        else if (tag4 instanceof CompoundTag) {
            integer5 = ((CompoundTag)tag4).getSize();
        }
        else {
            if (!(tag4 instanceof StringTag)) {
                throw DataCommand.GET_UNKNOWN_EXCEPTION.create(path.toString());
            }
            integer5 = tag4.asString().length();
        }
        source.sendFeedback(object.getQueryFeedback(tag4), false);
        return integer5;
    }
    
    private static int executeGet(final ServerCommandSource source, final DataCommandObject object, final NbtPathArgumentType.NbtPath path, final double scale) throws CommandSyntaxException {
        final Tag tag6 = getTag(path, object);
        if (!(tag6 instanceof AbstractNumberTag)) {
            throw DataCommand.GET_INVALID_EXCEPTION.create(path.toString());
        }
        final int integer7 = MathHelper.floor(((AbstractNumberTag)tag6).getDouble() * scale);
        source.sendFeedback(object.getGetFeedback(path, scale, integer7), false);
        return integer7;
    }
    
    private static int executeGet(final ServerCommandSource source, final DataCommandObject object) throws CommandSyntaxException {
        source.sendFeedback(object.getQueryFeedback(object.getTag()), false);
        return 1;
    }
    
    private static int executeMerge(final ServerCommandSource source, final DataCommandObject object, final CompoundTag tag) throws CommandSyntaxException {
        final CompoundTag compoundTag4 = object.getTag();
        final CompoundTag compoundTag5 = compoundTag4.copy().copyFrom(tag);
        if (compoundTag4.equals(compoundTag5)) {
            throw DataCommand.MERGE_FAILED_EXCEPTION.create();
        }
        object.setTag(compoundTag5);
        source.sendFeedback(object.getModifiedFeedback(), true);
        return 1;
    }
    
    static {
        MERGE_FAILED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("commands.data.merge.failed", new Object[0]));
        final TranslatableTextComponent translatableTextComponent;
        GET_INVALID_EXCEPTION = new DynamicCommandExceptionType(object -> {
            new TranslatableTextComponent("commands.data.get.invalid", new Object[] { object });
            return translatableTextComponent;
        });
        final TranslatableTextComponent translatableTextComponent2;
        GET_UNKNOWN_EXCEPTION = new DynamicCommandExceptionType(object -> {
            new TranslatableTextComponent("commands.data.get.unknown", new Object[] { object });
            return translatableTextComponent2;
        });
        GET_MULTIPLE_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableTextComponent("commands.data.get.multiple", new Object[0]));
        final TranslatableTextComponent translatableTextComponent3;
        MODIFY_EXPECTED_LIST_EXCEPTION = new DynamicCommandExceptionType(object -> {
            new TranslatableTextComponent("commands.data.modify.expected_list", new Object[] { object });
            return translatableTextComponent3;
        });
        final TranslatableTextComponent translatableTextComponent4;
        MODIFY_EXPECTED_OBJECT_EXCEPTION = new DynamicCommandExceptionType(object -> {
            new TranslatableTextComponent("commands.data.modify.expected_object", new Object[] { object });
            return translatableTextComponent4;
        });
        final TranslatableTextComponent translatableTextComponent5;
        MODIFY_INVALID_INDEX_EXCEPTION = new DynamicCommandExceptionType(object -> {
            new TranslatableTextComponent("commands.data.modify.invalid_index", new Object[] { object });
            return translatableTextComponent5;
        });
        OBJECT_TYPES = ImmutableList.<Function<String, ObjectType>>of(EntityDataObject.a, BlockDataObject.a);
        TARGET_OBJECT_TYPES = DataCommand.OBJECT_TYPES.stream().map(function -> function.apply("target")).collect(ImmutableList.toImmutableList());
        SOURCE_OBJECT_TYPES = DataCommand.OBJECT_TYPES.stream().map(function -> function.apply("source")).collect(ImmutableList.toImmutableList());
    }
    
    public interface ObjectType
    {
        DataCommandObject getObject(final CommandContext<ServerCommandSource> arg1) throws CommandSyntaxException;
        
        ArgumentBuilder<ServerCommandSource, ?> addArgumentsToBuilder(final ArgumentBuilder<ServerCommandSource, ?> arg1, final Function<ArgumentBuilder<ServerCommandSource, ?>, ArgumentBuilder<ServerCommandSource, ?>> arg2);
    }
    
    interface ModifyArgumentCreator
    {
        ArgumentBuilder<ServerCommandSource, ?> create(final ModifyOperation arg1);
    }
    
    interface ModifyOperation
    {
        int modify(final CommandContext<ServerCommandSource> arg1, final CompoundTag arg2, final NbtPathArgumentType.NbtPath arg3, final List<Tag> arg4) throws CommandSyntaxException;
    }
}

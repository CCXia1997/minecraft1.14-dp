package net.minecraft.world.loot.function;

import java.util.Iterator;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import java.util.function.Consumer;
import com.google.gson.JsonArray;
import com.google.gson.JsonSerializationContext;
import net.minecraft.util.Identifier;
import net.minecraft.world.loot.context.LootContextParameters;
import net.minecraft.nbt.ListTag;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import net.minecraft.util.JsonHelper;
import com.google.gson.JsonObject;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.nbt.CompoundTag;
import java.util.function.Supplier;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.item.ItemStack;
import com.google.common.collect.ImmutableSet;
import net.minecraft.world.loot.context.LootContextParameter;
import java.util.Set;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.StringReader;
import net.minecraft.command.arguments.NbtPathArgumentType;
import java.util.Collection;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.loot.condition.LootCondition;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.Tag;
import net.minecraft.entity.Entity;
import java.util.function.Function;
import java.util.List;

public class CopyNbtLootFunction extends ConditionalLootFunction
{
    private final Source source;
    private final List<Operation> operations;
    private static final Function<Entity, Tag> ENTITY_TAG_GETTER;
    private static final Function<BlockEntity, Tag> BLOCK_ENTITY_TAG_GETTER;
    
    private CopyNbtLootFunction(final LootCondition[] conditions, final Source source, final List<Operation> operations) {
        super(conditions);
        this.source = source;
        this.operations = ImmutableList.copyOf(operations);
    }
    
    private static NbtPathArgumentType.NbtPath parseNbtPath(final String nbtPath) {
        try {
            return new NbtPathArgumentType().a(new StringReader(nbtPath));
        }
        catch (CommandSyntaxException commandSyntaxException2) {
            throw new IllegalArgumentException("Failed to parse path " + nbtPath, (Throwable)commandSyntaxException2);
        }
    }
    
    @Override
    public Set<LootContextParameter<?>> getRequiredParameters() {
        return ImmutableSet.<LootContextParameter<?>>of(this.source.parameter);
    }
    
    public ItemStack process(final ItemStack stack, final LootContext context) {
        final Tag tag3 = this.source.getter.apply(context);
        if (tag3 != null) {
            this.operations.forEach(operation -> operation.execute((Supplier<Tag>)stack::getOrCreateTag, tag3));
        }
        return stack;
    }
    
    public static Builder builder(final Source source) {
        return new Builder(source);
    }
    
    static {
        ENTITY_TAG_GETTER = NbtPredicate::entityToTag;
        BLOCK_ENTITY_TAG_GETTER = (blockEntity -> blockEntity.toTag(new CompoundTag()));
    }
    
    static class Operation
    {
        private final String sourcePath;
        private final NbtPathArgumentType.NbtPath parsedSourcePath;
        private final String targetPath;
        private final NbtPathArgumentType.NbtPath parsedTargetPath;
        private final Operator operator;
        
        private Operation(final String source, final String target, final Operator operator) {
            this.sourcePath = source;
            this.parsedSourcePath = parseNbtPath(source);
            this.targetPath = target;
            this.parsedTargetPath = parseNbtPath(target);
            this.operator = operator;
        }
        
        public void execute(final Supplier<Tag> itemTagTagGetter, final Tag sourceEntityTag) {
            try {
                final List<Tag> list3 = this.parsedSourcePath.get(sourceEntityTag);
                if (!list3.isEmpty()) {
                    this.operator.merge(itemTagTagGetter.get(), this.parsedTargetPath, list3);
                }
            }
            catch (CommandSyntaxException ex) {}
        }
        
        public JsonObject toJson() {
            final JsonObject jsonObject1 = new JsonObject();
            jsonObject1.addProperty("source", this.sourcePath);
            jsonObject1.addProperty("target", this.targetPath);
            jsonObject1.addProperty("op", this.operator.name);
            return jsonObject1;
        }
        
        public static Operation fromJson(final JsonObject json) {
            final String string2 = JsonHelper.getString(json, "source");
            final String string3 = JsonHelper.getString(json, "target");
            final Operator operator4 = Operator.get(JsonHelper.getString(json, "op"));
            return new Operation(string2, string3, operator4);
        }
    }
    
    public static class Builder extends ConditionalLootFunction.Builder<Builder>
    {
        private final Source source;
        private final List<Operation> operations;
        
        private Builder(final Source source) {
            this.operations = Lists.newArrayList();
            this.source = source;
        }
        
        public Builder withOperation(final String source, final String target, final Operator operator) {
            this.operations.add(new Operation(source, target, operator));
            return this;
        }
        
        public Builder withOperation(final String source, final String target) {
            return this.withOperation(source, target, Operator.REPLACE);
        }
        
        @Override
        protected Builder getThisBuilder() {
            return this;
        }
        
        @Override
        public LootFunction build() {
            return new CopyNbtLootFunction(this.getConditions(), this.source, this.operations, null);
        }
    }
    
    public enum Operator
    {
        REPLACE("replace") {
            @Override
            public void merge(final Tag itemTag, final NbtPathArgumentType.NbtPath tragetPath, final List<Tag> sourceTags) throws CommandSyntaxException {
                tragetPath.put(itemTag, (Tag)Iterables.<Tag>getLast(sourceTags)::copy);
            }
        }, 
        APPEND("append") {
            @Override
            public void merge(final Tag itemTag, final NbtPathArgumentType.NbtPath tragetPath, final List<Tag> sourceTags) throws CommandSyntaxException {
                final List<Tag> list4 = tragetPath.putIfAbsent(itemTag, (Supplier<Tag>)ListTag::new);
                list4.forEach(foundTag -> {
                    if (foundTag instanceof ListTag) {
                        sourceTags.forEach(listTag -> foundTag.add(listTag.copy()));
                    }
                });
            }
        }, 
        MERGE("merge") {
            @Override
            public void merge(final Tag itemTag, final NbtPathArgumentType.NbtPath tragetPath, final List<Tag> sourceTags) throws CommandSyntaxException {
                final List<Tag> list4 = tragetPath.putIfAbsent(itemTag, (Supplier<Tag>)CompoundTag::new);
                list4.forEach(foundTag -> {
                    if (foundTag instanceof CompoundTag) {
                        sourceTags.forEach(compoundTag -> {
                            if (compoundTag instanceof CompoundTag) {
                                foundTag.copyFrom(compoundTag);
                            }
                        });
                    }
                });
            }
        };
        
        private final String name;
        
        public abstract void merge(final Tag arg1, final NbtPathArgumentType.NbtPath arg2, final List<Tag> arg3) throws CommandSyntaxException;
        
        private Operator(final String name) {
            this.name = name;
        }
        
        public static Operator get(final String name) {
            for (final Operator operator5 : values()) {
                if (operator5.name.equals(name)) {
                    return operator5;
                }
            }
            throw new IllegalArgumentException("Invalid merge strategy" + name);
        }
    }
    
    public enum Source
    {
        a("this", (LootContextParameter<T>)LootContextParameters.a, CopyNbtLootFunction.ENTITY_TAG_GETTER), 
        b("killer", (LootContextParameter<T>)LootContextParameters.d, CopyNbtLootFunction.ENTITY_TAG_GETTER), 
        c("killer_player", (LootContextParameter<T>)LootContextParameters.b, CopyNbtLootFunction.ENTITY_TAG_GETTER), 
        d("block_entity", (LootContextParameter<T>)LootContextParameters.h, CopyNbtLootFunction.BLOCK_ENTITY_TAG_GETTER);
        
        public final String name;
        public final LootContextParameter<?> parameter;
        public final Function<LootContext, Tag> getter;
        
        private <T> Source(final String name, final LootContextParameter<T> parameter, final Function<? super T, Tag> operator) {
            this.name = name;
            this.parameter = parameter;
            final Object object4;
            this.getter = (Function<LootContext, Tag>)(context -> {
                object4 = context.<T>get(parameter);
                return (object4 != null) ? operator.apply(object4) : null;
            });
        }
        
        public static Source get(final String name) {
            for (final Source source5 : values()) {
                if (source5.name.equals(name)) {
                    return source5;
                }
            }
            throw new IllegalArgumentException("Invalid tag source " + name);
        }
    }
    
    public static class Factory extends ConditionalLootFunction.Factory<CopyNbtLootFunction>
    {
        public Factory() {
            super(new Identifier("copy_nbt"), CopyNbtLootFunction.class);
        }
        
        @Override
        public void toJson(final JsonObject json, final CopyNbtLootFunction function, final JsonSerializationContext context) {
            super.toJson(json, function, context);
            json.addProperty("source", function.source.name);
            final JsonArray jsonArray4 = new JsonArray();
            function.operations.stream().map(Operation::toJson).forEach(jsonArray4::add);
            json.add("ops", jsonArray4);
        }
        
        @Override
        public CopyNbtLootFunction fromJson(final JsonObject json, final JsonDeserializationContext context, final LootCondition[] conditions) {
            final Source source4 = Source.get(JsonHelper.getString(json, "source"));
            final List<Operation> list5 = Lists.newArrayList();
            final JsonArray jsonArray6 = JsonHelper.getArray(json, "ops");
            for (final JsonElement jsonElement8 : jsonArray6) {
                final JsonObject jsonObject9 = JsonHelper.asObject(jsonElement8, "op");
                list5.add(Operation.fromJson(jsonObject9));
            }
            return new CopyNbtLootFunction(conditions, source4, list5, null);
        }
    }
}

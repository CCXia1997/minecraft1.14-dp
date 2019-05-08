package net.minecraft.command.arguments;

import com.google.common.collect.Maps;
import org.apache.logging.log4j.LogManager;
import java.util.Collection;
import java.util.Iterator;
import com.google.gson.JsonArray;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.CommandDispatcher;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.PacketByteBuf;
import javax.annotation.Nullable;
import java.util.function.Supplier;
import net.minecraft.command.arguments.serialize.ConstantArgumentSerializer;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.command.arguments.serialize.ArgumentSerializer;
import net.minecraft.util.Identifier;
import java.util.Map;
import org.apache.logging.log4j.Logger;

public class ArgumentTypes
{
    private static final Logger LOGGER;
    private static final Map<Class<?>, Entry<?>> classMap;
    private static final Map<Identifier, Entry<?>> idMap;
    
    public static <T extends ArgumentType<?>> void register(final String string, final Class<T> class2, final ArgumentSerializer<T> argumentSerializer) {
        final Identifier identifier4 = new Identifier(string);
        if (ArgumentTypes.classMap.containsKey(class2)) {
            throw new IllegalArgumentException("Class " + class2.getName() + " already has a serializer!");
        }
        if (ArgumentTypes.idMap.containsKey(identifier4)) {
            throw new IllegalArgumentException("'" + identifier4 + "' is already a registered serializer!");
        }
        final Entry<T> entry5 = new Entry<T>((Class)class2, (ArgumentSerializer)argumentSerializer, identifier4);
        ArgumentTypes.classMap.put(class2, entry5);
        ArgumentTypes.idMap.put(identifier4, entry5);
    }
    
    public static void register() {
        BrigadierArgumentTypes.register();
        ArgumentTypes.<ArgumentType>register("entity", (Class<ArgumentType>)EntityArgumentType.class, (ArgumentSerializer<ArgumentType>)new EntityArgumentType.Serializer());
        ArgumentTypes.<ArgumentType>register("game_profile", (Class<ArgumentType>)GameProfileArgumentType.class, new ConstantArgumentSerializer<ArgumentType>((Supplier<ArgumentType>)GameProfileArgumentType::create));
        ArgumentTypes.<ArgumentType>register("block_pos", (Class<ArgumentType>)BlockPosArgumentType.class, new ConstantArgumentSerializer<ArgumentType>((Supplier<ArgumentType>)BlockPosArgumentType::create));
        ArgumentTypes.<ArgumentType>register("column_pos", (Class<ArgumentType>)ColumnPosArgumentType.class, new ConstantArgumentSerializer<ArgumentType>((Supplier<ArgumentType>)ColumnPosArgumentType::create));
        ArgumentTypes.<ArgumentType>register("vec3", (Class<ArgumentType>)Vec3ArgumentType.class, new ConstantArgumentSerializer<ArgumentType>((Supplier<ArgumentType>)Vec3ArgumentType::create));
        ArgumentTypes.<ArgumentType>register("vec2", (Class<ArgumentType>)Vec2ArgumentType.class, new ConstantArgumentSerializer<ArgumentType>((Supplier<ArgumentType>)Vec2ArgumentType::create));
        ArgumentTypes.<ArgumentType>register("block_state", (Class<ArgumentType>)BlockStateArgumentType.class, new ConstantArgumentSerializer<ArgumentType>((Supplier<ArgumentType>)BlockStateArgumentType::create));
        ArgumentTypes.<ArgumentType>register("block_predicate", (Class<ArgumentType>)BlockPredicateArgumentType.class, new ConstantArgumentSerializer<ArgumentType>((Supplier<ArgumentType>)BlockPredicateArgumentType::create));
        ArgumentTypes.<ArgumentType>register("item_stack", (Class<ArgumentType>)ItemStackArgumentType.class, new ConstantArgumentSerializer<ArgumentType>((Supplier<ArgumentType>)ItemStackArgumentType::create));
        ArgumentTypes.<ArgumentType>register("item_predicate", (Class<ArgumentType>)ItemPredicateArgumentType.class, new ConstantArgumentSerializer<ArgumentType>((Supplier<ArgumentType>)ItemPredicateArgumentType::create));
        ArgumentTypes.<ArgumentType>register("color", (Class<ArgumentType>)ColorArgumentType.class, new ConstantArgumentSerializer<ArgumentType>((Supplier<ArgumentType>)ColorArgumentType::create));
        ArgumentTypes.<ArgumentType>register("component", (Class<ArgumentType>)ComponentArgumentType.class, new ConstantArgumentSerializer<ArgumentType>((Supplier<ArgumentType>)ComponentArgumentType::create));
        ArgumentTypes.<ArgumentType>register("message", (Class<ArgumentType>)MessageArgumentType.class, new ConstantArgumentSerializer<ArgumentType>((Supplier<ArgumentType>)MessageArgumentType::create));
        ArgumentTypes.<ArgumentType>register("nbt_compound_tag", (Class<ArgumentType>)NbtCompoundTagArgumentType.class, new ConstantArgumentSerializer<ArgumentType>((Supplier<ArgumentType>)NbtCompoundTagArgumentType::create));
        ArgumentTypes.<ArgumentType>register("nbt_tag", (Class<ArgumentType>)NbtTagArgumentType.class, new ConstantArgumentSerializer<ArgumentType>((Supplier<ArgumentType>)NbtTagArgumentType::create));
        ArgumentTypes.<ArgumentType>register("nbt_path", (Class<ArgumentType>)NbtPathArgumentType.class, new ConstantArgumentSerializer<ArgumentType>((Supplier<ArgumentType>)NbtPathArgumentType::create));
        ArgumentTypes.<ArgumentType>register("objective", (Class<ArgumentType>)ObjectiveArgumentType.class, new ConstantArgumentSerializer<ArgumentType>((Supplier<ArgumentType>)ObjectiveArgumentType::create));
        ArgumentTypes.<ArgumentType>register("objective_criteria", (Class<ArgumentType>)ObjectiveCriteriaArgumentType.class, new ConstantArgumentSerializer<ArgumentType>((Supplier<ArgumentType>)ObjectiveCriteriaArgumentType::create));
        ArgumentTypes.<ArgumentType>register("operation", (Class<ArgumentType>)OperationArgumentType.class, new ConstantArgumentSerializer<ArgumentType>((Supplier<ArgumentType>)OperationArgumentType::create));
        ArgumentTypes.<ArgumentType>register("particle", (Class<ArgumentType>)ParticleArgumentType.class, new ConstantArgumentSerializer<ArgumentType>((Supplier<ArgumentType>)ParticleArgumentType::create));
        ArgumentTypes.<ArgumentType>register("rotation", (Class<ArgumentType>)RotationArgumentType.class, new ConstantArgumentSerializer<ArgumentType>((Supplier<ArgumentType>)RotationArgumentType::create));
        ArgumentTypes.<ArgumentType>register("scoreboard_slot", (Class<ArgumentType>)ScoreboardSlotArgumentType.class, new ConstantArgumentSerializer<ArgumentType>((Supplier<ArgumentType>)ScoreboardSlotArgumentType::create));
        ArgumentTypes.<ArgumentType>register("score_holder", (Class<ArgumentType>)ScoreHolderArgumentType.class, (ArgumentSerializer<ArgumentType>)new ScoreHolderArgumentType.Serializer());
        ArgumentTypes.<ArgumentType>register("swizzle", (Class<ArgumentType>)SwizzleArgumentType.class, new ConstantArgumentSerializer<ArgumentType>((Supplier<ArgumentType>)SwizzleArgumentType::create));
        ArgumentTypes.<ArgumentType>register("team", (Class<ArgumentType>)TeamArgumentType.class, new ConstantArgumentSerializer<ArgumentType>((Supplier<ArgumentType>)TeamArgumentType::create));
        ArgumentTypes.<ArgumentType>register("item_slot", (Class<ArgumentType>)ItemSlotArgumentType.class, new ConstantArgumentSerializer<ArgumentType>((Supplier<ArgumentType>)ItemSlotArgumentType::create));
        ArgumentTypes.<ArgumentType>register("resource_location", (Class<ArgumentType>)IdentifierArgumentType.class, new ConstantArgumentSerializer<ArgumentType>((Supplier<ArgumentType>)IdentifierArgumentType::create));
        ArgumentTypes.<ArgumentType>register("mob_effect", (Class<ArgumentType>)MobEffectArgumentType.class, new ConstantArgumentSerializer<ArgumentType>((Supplier<ArgumentType>)MobEffectArgumentType::create));
        ArgumentTypes.<ArgumentType>register("function", (Class<ArgumentType>)FunctionArgumentType.class, new ConstantArgumentSerializer<ArgumentType>((Supplier<ArgumentType>)FunctionArgumentType::create));
        ArgumentTypes.<ArgumentType>register("entity_anchor", (Class<ArgumentType>)EntityAnchorArgumentType.class, new ConstantArgumentSerializer<ArgumentType>((Supplier<ArgumentType>)EntityAnchorArgumentType::create));
        ArgumentTypes.<ArgumentType>register("int_range", (Class<ArgumentType>)NumberRangeArgumentType.IntRangeArgumentType.class, (ArgumentSerializer<ArgumentType>)new NumberRangeArgumentType.IntRangeArgumentType.Serializer());
        ArgumentTypes.<ArgumentType>register("float_range", (Class<ArgumentType>)NumberRangeArgumentType.FloatRangeArgumentType.class, (ArgumentSerializer<ArgumentType>)new NumberRangeArgumentType.FloatRangeArgumentType.Serializer());
        ArgumentTypes.<ArgumentType>register("item_enchantment", (Class<ArgumentType>)ItemEnchantmentArgumentType.class, new ConstantArgumentSerializer<ArgumentType>((Supplier<ArgumentType>)ItemEnchantmentArgumentType::create));
        ArgumentTypes.<ArgumentType>register("entity_summon", (Class<ArgumentType>)EntitySummonArgumentType.class, new ConstantArgumentSerializer<ArgumentType>((Supplier<ArgumentType>)EntitySummonArgumentType::create));
        ArgumentTypes.<ArgumentType>register("dimension", (Class<ArgumentType>)DimensionArgumentType.class, new ConstantArgumentSerializer<ArgumentType>((Supplier<ArgumentType>)DimensionArgumentType::create));
        ArgumentTypes.<ArgumentType>register("time", (Class<ArgumentType>)TimeArgumentType.class, new ConstantArgumentSerializer<ArgumentType>((Supplier<ArgumentType>)TimeArgumentType::create));
    }
    
    @Nullable
    private static Entry<?> byId(final Identifier identifier) {
        return ArgumentTypes.idMap.get(identifier);
    }
    
    @Nullable
    private static Entry<?> byClass(final ArgumentType<?> argumentType) {
        return ArgumentTypes.classMap.get(argumentType.getClass());
    }
    
    public static <T extends ArgumentType<?>> void toPacket(final PacketByteBuf packetByteBuf, final T argumentType) {
        final Entry<T> entry3 = (Entry<T>)byClass(argumentType);
        if (entry3 == null) {
            ArgumentTypes.LOGGER.error("Could not serialize {} ({}) - will not be sent to client!", argumentType, argumentType.getClass());
            packetByteBuf.writeIdentifier(new Identifier(""));
            return;
        }
        packetByteBuf.writeIdentifier(entry3.id);
        entry3.serializer.toPacket(argumentType, packetByteBuf);
    }
    
    @Nullable
    public static ArgumentType<?> fromPacket(final PacketByteBuf packetByteBuf) {
        final Identifier identifier2 = packetByteBuf.readIdentifier();
        final Entry<?> entry3 = byId(identifier2);
        if (entry3 == null) {
            ArgumentTypes.LOGGER.error("Could not deserialize {}", identifier2);
            return null;
        }
        return entry3.serializer.fromPacket(packetByteBuf);
    }
    
    private static <T extends ArgumentType<?>> void toJson(final JsonObject jsonObject, final T argumentType) {
        final Entry<T> entry3 = (Entry<T>)byClass(argumentType);
        if (entry3 == null) {
            ArgumentTypes.LOGGER.error("Could not serialize argument {} ({})!", argumentType, argumentType.getClass());
            jsonObject.addProperty("type", "unknown");
        }
        else {
            jsonObject.addProperty("type", "argument");
            jsonObject.addProperty("parser", entry3.id.toString());
            final JsonObject jsonObject2 = new JsonObject();
            entry3.serializer.toJson(argumentType, jsonObject2);
            if (jsonObject2.size() > 0) {
                jsonObject.add("properties", jsonObject2);
            }
        }
    }
    
    public static <S> JsonObject toJson(final CommandDispatcher<S> commandDispatcher, final CommandNode<S> commandNode) {
        final JsonObject jsonObject3 = new JsonObject();
        if (commandNode instanceof RootCommandNode) {
            jsonObject3.addProperty("type", "root");
        }
        else if (commandNode instanceof LiteralCommandNode) {
            jsonObject3.addProperty("type", "literal");
        }
        else if (commandNode instanceof ArgumentCommandNode) {
            ArgumentTypes.<ArgumentType>toJson(jsonObject3, ((ArgumentCommandNode)commandNode).getType());
        }
        else {
            ArgumentTypes.LOGGER.error("Could not serialize node {} ({})!", commandNode, commandNode.getClass());
            jsonObject3.addProperty("type", "unknown");
        }
        final JsonObject jsonObject4 = new JsonObject();
        for (final CommandNode<S> commandNode2 : commandNode.getChildren()) {
            jsonObject4.add(commandNode2.getName(), ArgumentTypes.toJson((com.mojang.brigadier.CommandDispatcher<Object>)commandDispatcher, (com.mojang.brigadier.tree.CommandNode<Object>)commandNode2));
        }
        if (jsonObject4.size() > 0) {
            jsonObject3.add("children", jsonObject4);
        }
        if (commandNode.getCommand() != null) {
            jsonObject3.addProperty("executable", true);
        }
        if (commandNode.getRedirect() != null) {
            final Collection<String> collection5 = (Collection<String>)commandDispatcher.getPath(commandNode.getRedirect());
            if (!collection5.isEmpty()) {
                final JsonArray jsonArray6 = new JsonArray();
                for (final String string8 : collection5) {
                    jsonArray6.add(string8);
                }
                jsonObject3.add("redirect", jsonArray6);
            }
        }
        return jsonObject3;
    }
    
    static {
        LOGGER = LogManager.getLogger();
        classMap = Maps.newHashMap();
        idMap = Maps.newHashMap();
    }
    
    static class Entry<T extends ArgumentType<?>>
    {
        public final Class<T> argClass;
        public final ArgumentSerializer<T> serializer;
        public final Identifier id;
        
        private Entry(final Class<T> class1, final ArgumentSerializer<T> argumentSerializer, final Identifier identifier) {
            this.argClass = class1;
            this.serializer = argumentSerializer;
            this.id = identifier;
        }
    }
}

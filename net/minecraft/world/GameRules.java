package net.minecraft.world;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.command.CommandManager;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.server.command.ServerCommandSource;
import com.mojang.brigadier.context.CommandContext;
import java.util.function.BiFunction;
import com.mojang.brigadier.arguments.ArgumentType;
import java.util.function.Supplier;
import java.util.function.BiConsumer;
import net.minecraft.util.SystemUtil;
import net.minecraft.network.Packet;
import net.minecraft.entity.Entity;
import net.minecraft.client.network.packet.EntityStatusS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import java.util.Set;
import net.minecraft.nbt.CompoundTag;
import javax.annotation.Nullable;
import net.minecraft.server.MinecraftServer;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class GameRules
{
    private static final TreeMap<String, Key> KEYS;
    private final TreeMap<String, Value> rules;
    
    public GameRules() {
        this.rules = new TreeMap<String, Value>();
        for (final Map.Entry<String, Key> entry2 : GameRules.KEYS.entrySet()) {
            this.rules.put(entry2.getKey(), entry2.getValue().createValue());
        }
    }
    
    public void put(final String key, final String value, @Nullable final MinecraftServer minecraftServer) {
        final Value value2 = this.rules.get(key);
        if (value2 != null) {
            value2.set(value, minecraftServer);
        }
    }
    
    public boolean getBoolean(final String string) {
        final Value value2 = this.rules.get(string);
        return value2 != null && value2.getBoolean();
    }
    
    public int getInteger(final String string) {
        final Value value2 = this.rules.get(string);
        if (value2 != null) {
            return value2.getInteger();
        }
        return 0;
    }
    
    public CompoundTag serialize() {
        final CompoundTag compoundTag1 = new CompoundTag();
        for (final String string3 : this.rules.keySet()) {
            final Value value4 = this.rules.get(string3);
            compoundTag1.putString(string3, value4.getString());
        }
        return compoundTag1;
    }
    
    public void deserialize(final CompoundTag compoundTag) {
        final Set<String> set2 = compoundTag.getKeys();
        for (final String string4 : set2) {
            this.put(string4, compoundTag.getString(string4), null);
        }
    }
    
    public Value get(final String string) {
        return this.rules.get(string);
    }
    
    public static TreeMap<String, Key> getKeys() {
        return GameRules.KEYS;
    }
    
    static {
        final int byte3;
        final Iterator<ServerPlayerEntity> iterator;
        ServerPlayerEntity serverPlayerEntity5;
        KEYS = SystemUtil.<TreeMap<String, Key>>consume(new TreeMap<String, Key>(), treeMap -> {
            treeMap.put("doFireTick", new Key("true", Type.BOOLEAN));
            treeMap.put("mobGriefing", new Key("true", Type.BOOLEAN));
            treeMap.put("keepInventory", new Key("false", Type.BOOLEAN));
            treeMap.put("doMobSpawning", new Key("true", Type.BOOLEAN));
            treeMap.put("doMobLoot", new Key("true", Type.BOOLEAN));
            treeMap.put("doTileDrops", new Key("true", Type.BOOLEAN));
            treeMap.put("doEntityDrops", new Key("true", Type.BOOLEAN));
            treeMap.put("commandBlockOutput", new Key("true", Type.BOOLEAN));
            treeMap.put("naturalRegeneration", new Key("true", Type.BOOLEAN));
            treeMap.put("doDaylightCycle", new Key("true", Type.BOOLEAN));
            treeMap.put("logAdminCommands", new Key("true", Type.BOOLEAN));
            treeMap.put("showDeathMessages", new Key("true", Type.BOOLEAN));
            treeMap.put("randomTickSpeed", new Key("3", Type.INTEGER));
            treeMap.put("sendCommandFeedback", new Key("true", Type.BOOLEAN));
            treeMap.put("reducedDebugInfo", new Key("false", Type.BOOLEAN, (minecraftServer, value) -> {
                byte3 = (value.getBoolean() ? 22 : 23);
                minecraftServer.getPlayerManager().getPlayerList().iterator();
                while (iterator.hasNext()) {
                    serverPlayerEntity5 = iterator.next();
                    serverPlayerEntity5.networkHandler.sendPacket(new EntityStatusS2CPacket(serverPlayerEntity5, (byte)byte3));
                }
                return;
            }));
            treeMap.put("spectatorsGenerateChunks", new Key("true", Type.BOOLEAN));
            treeMap.put("spawnRadius", new Key("10", Type.INTEGER));
            treeMap.put("disableElytraMovementCheck", new Key("false", Type.BOOLEAN));
            treeMap.put("maxEntityCramming", new Key("24", Type.INTEGER));
            treeMap.put("doWeatherCycle", new Key("true", Type.BOOLEAN));
            treeMap.put("doLimitedCrafting", new Key("false", Type.BOOLEAN));
            treeMap.put("maxCommandChainLength", new Key("65536", Type.INTEGER));
            treeMap.put("announceAdvancements", new Key("true", Type.BOOLEAN));
        });
    }
    
    public static class Key
    {
        private final Type type;
        private final String defaultValue;
        private final BiConsumer<MinecraftServer, Value> c;
        
        public Key(final String string, final Type type) {
            this(string, type, (minecraftServer, value) -> {});
        }
        
        public Key(final String string, final Type type, final BiConsumer<MinecraftServer, Value> biConsumer) {
            this.type = type;
            this.defaultValue = string;
            this.c = biConsumer;
        }
        
        public Value createValue() {
            return new Value(this.defaultValue, this.type, this.c);
        }
        
        public Type getType() {
            return this.type;
        }
    }
    
    public static class Value
    {
        private String asString;
        private boolean asBoolean;
        private int asInteger;
        private double asDouble;
        private final Type type;
        private final BiConsumer<MinecraftServer, Value> applyConsumer;
        
        public Value(final String string, final Type type, final BiConsumer<MinecraftServer, Value> biConsumer) {
            this.type = type;
            this.applyConsumer = biConsumer;
            this.set(string, null);
        }
        
        public void set(final String string, @Nullable final MinecraftServer minecraftServer) {
            this.asString = string;
            this.asBoolean = Boolean.parseBoolean(string);
            this.asInteger = (this.asBoolean ? 1 : 0);
            try {
                this.asInteger = Integer.parseInt(string);
            }
            catch (NumberFormatException ex) {}
            try {
                this.asDouble = Double.parseDouble(string);
            }
            catch (NumberFormatException ex2) {}
            if (minecraftServer != null) {
                this.applyConsumer.accept(minecraftServer, this);
            }
        }
        
        public String getString() {
            return this.asString;
        }
        
        public boolean getBoolean() {
            return this.asBoolean;
        }
        
        public int getInteger() {
            return this.asInteger;
        }
        
        public Type getType() {
            return this.type;
        }
    }
    
    public enum Type
    {
        STRING((Supplier<ArgumentType<?>>)StringArgumentType::greedyString, (commandContext, string) -> (String)commandContext.getArgument(string, (Class)String.class)), 
        BOOLEAN((Supplier<ArgumentType<?>>)BoolArgumentType::bool, (commandContext, string) -> ((Boolean)commandContext.getArgument(string, (Class)Boolean.class)).toString()), 
        INTEGER((Supplier<ArgumentType<?>>)IntegerArgumentType::integer, (commandContext, string) -> ((Integer)commandContext.getArgument(string, (Class)Integer.class)).toString());
        
        private final Supplier<ArgumentType<?>> argumentType;
        private final BiFunction<CommandContext<ServerCommandSource>, String, String> argumentProvider;
        
        private Type(final Supplier<ArgumentType<?>> supplier, final BiFunction<CommandContext<ServerCommandSource>, String, String> biFunction) {
            this.argumentType = supplier;
            this.argumentProvider = biFunction;
        }
        
        public RequiredArgumentBuilder<ServerCommandSource, ?> argument(final String name) {
            return CommandManager.argument(name, this.argumentType.get());
        }
        
        public void set(final CommandContext<ServerCommandSource> context, final String name, final Value value) {
            value.set(this.argumentProvider.apply(context, name), ((ServerCommandSource)context.getSource()).getMinecraftServer());
        }
    }
}

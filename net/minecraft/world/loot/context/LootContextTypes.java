package net.minecraft.world.loot.context;

import com.google.common.collect.HashBiMap;
import javax.annotation.Nullable;
import java.util.function.Consumer;
import net.minecraft.util.Identifier;
import com.google.common.collect.BiMap;

public class LootContextTypes
{
    private static final BiMap<Identifier, LootContextType> MAP;
    public static final LootContextType EMPTY;
    public static final LootContextType CHEST;
    public static final LootContextType FISHING;
    public static final LootContextType ENTITY;
    public static final LootContextType GIFT;
    public static final LootContextType ADVANCEMENT_REWARD;
    public static final LootContextType GENERIC;
    public static final LootContextType BLOCK;
    
    private static LootContextType register(final String name, final Consumer<LootContextType.Builder> type) {
        final LootContextType.Builder builder3 = new LootContextType.Builder();
        type.accept(builder3);
        final LootContextType lootContextType4 = builder3.build();
        final Identifier identifier5 = new Identifier(name);
        final LootContextType lootContextType5 = LootContextTypes.MAP.put(identifier5, lootContextType4);
        if (lootContextType5 != null) {
            throw new IllegalStateException("Loot table parameter set " + identifier5 + " is already registered");
        }
        return lootContextType4;
    }
    
    @Nullable
    public static LootContextType get(final Identifier id) {
        return LootContextTypes.MAP.get(id);
    }
    
    @Nullable
    public static Identifier getId(final LootContextType type) {
        return LootContextTypes.MAP.inverse().get(type);
    }
    
    static {
        MAP = HashBiMap.create();
        EMPTY = register("empty", builder -> {});
        CHEST = register("chest", builder -> builder.require(LootContextParameters.f).allow(LootContextParameters.a));
        FISHING = register("fishing", builder -> builder.require(LootContextParameters.f).require(LootContextParameters.i));
        ENTITY = register("entity", builder -> builder.require(LootContextParameters.a).require(LootContextParameters.f).require(LootContextParameters.c).allow(LootContextParameters.d).allow(LootContextParameters.e).allow(LootContextParameters.b));
        GIFT = register("gift", builder -> builder.require(LootContextParameters.f).require(LootContextParameters.a));
        ADVANCEMENT_REWARD = register("advancement_reward", builder -> builder.require(LootContextParameters.a).require(LootContextParameters.f));
        GENERIC = register("generic", builder -> builder.require(LootContextParameters.a).require(LootContextParameters.b).require(LootContextParameters.c).require(LootContextParameters.d).require(LootContextParameters.e).require(LootContextParameters.f).require(LootContextParameters.g).require(LootContextParameters.h).require(LootContextParameters.i).require(LootContextParameters.j));
        BLOCK = register("block", builder -> builder.require(LootContextParameters.g).require(LootContextParameters.f).require(LootContextParameters.i).allow(LootContextParameters.a).allow(LootContextParameters.h).allow(LootContextParameters.j));
    }
}

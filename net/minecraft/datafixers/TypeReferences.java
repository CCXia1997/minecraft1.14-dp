package net.minecraft.datafixers;

import com.mojang.datafixers.DSL;

public class TypeReferences
{
    public static final DSL.TypeReference LEVEL;
    public static final DSL.TypeReference PLAYER;
    public static final DSL.TypeReference CHUNK;
    public static final DSL.TypeReference HOTBAR;
    public static final DSL.TypeReference OPTIONS;
    public static final DSL.TypeReference STRUCTURE;
    public static final DSL.TypeReference STATS;
    public static final DSL.TypeReference SAVED_DATA;
    public static final DSL.TypeReference ADVANCEMENTS;
    public static final DSL.TypeReference POI_CHUNK;
    public static final DSL.TypeReference BLOCK_ENTITY;
    public static final DSL.TypeReference ITEM_STACK;
    public static final DSL.TypeReference BLOCK_STATE;
    public static final DSL.TypeReference ENTITY_NAME;
    public static final DSL.TypeReference ENTITY_TREE;
    public static final DSL.TypeReference ENTITY;
    public static final DSL.TypeReference BLOCK_NAME;
    public static final DSL.TypeReference ITEM_NAME;
    public static final DSL.TypeReference UNTAGGED_SPAWNER;
    public static final DSL.TypeReference STRUCTURE_FEATURE;
    public static final DSL.TypeReference OBJECTIVE;
    public static final DSL.TypeReference TEAM;
    public static final DSL.TypeReference RECIPE;
    public static final DSL.TypeReference BIOME;
    
    static {
        LEVEL = (() -> "level");
        PLAYER = (() -> "player");
        CHUNK = (() -> "chunk");
        HOTBAR = (() -> "hotbar");
        OPTIONS = (() -> "options");
        STRUCTURE = (() -> "structure");
        STATS = (() -> "stats");
        SAVED_DATA = (() -> "saved_data");
        ADVANCEMENTS = (() -> "advancements");
        POI_CHUNK = (() -> "poi_chunk");
        BLOCK_ENTITY = (() -> "block_entity");
        ITEM_STACK = (() -> "item_stack");
        BLOCK_STATE = (() -> "block_state");
        ENTITY_NAME = (() -> "entity_name");
        ENTITY_TREE = (() -> "entity_tree");
        ENTITY = (() -> "entity");
        BLOCK_NAME = (() -> "block_name");
        ITEM_NAME = (() -> "item_name");
        UNTAGGED_SPAWNER = (() -> "untagged_spawner");
        STRUCTURE_FEATURE = (() -> "structure_feature");
        OBJECTIVE = (() -> "objective");
        TEAM = (() -> "team");
        RECIPE = (() -> "recipe");
        BIOME = (() -> "biome");
    }
}

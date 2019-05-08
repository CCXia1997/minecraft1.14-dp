package net.minecraft.world.loot.function;

import net.minecraft.world.gen.feature.Feature;
import net.minecraft.util.JsonHelper;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import java.util.Locale;
import net.minecraft.item.map.MapState;
import net.minecraft.world.World;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.Items;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import com.google.common.collect.ImmutableSet;
import net.minecraft.world.loot.context.LootContextParameters;
import net.minecraft.world.loot.context.LootContextParameter;
import java.util.Set;
import net.minecraft.world.loot.condition.LootCondition;
import net.minecraft.item.map.MapIcon;
import org.apache.logging.log4j.Logger;

public class ExplorationMapLootFunction extends ConditionalLootFunction
{
    private static final Logger LOGGER;
    public static final MapIcon.Type DEFAULT_DECORATION;
    private final String destination;
    private final MapIcon.Type decoration;
    private final byte zoom;
    private final int searchRadius;
    private final boolean skipExistingChunks;
    
    private ExplorationMapLootFunction(final LootCondition[] conditions, final String destination, final MapIcon.Type decoration, final byte zoom, final int searchRadius, final boolean skipExistingChunks) {
        super(conditions);
        this.destination = destination;
        this.decoration = decoration;
        this.zoom = zoom;
        this.searchRadius = searchRadius;
        this.skipExistingChunks = skipExistingChunks;
    }
    
    @Override
    public Set<LootContextParameter<?>> getRequiredParameters() {
        return ImmutableSet.of(LootContextParameters.f);
    }
    
    public ItemStack process(final ItemStack stack, final LootContext context) {
        if (stack.getItem() != Items.nM) {
            return stack;
        }
        final BlockPos blockPos3 = context.<BlockPos>get(LootContextParameters.f);
        if (blockPos3 != null) {
            final ServerWorld serverWorld4 = context.getWorld();
            final BlockPos blockPos4 = serverWorld4.locateStructure(this.destination, blockPos3, this.searchRadius, this.skipExistingChunks);
            if (blockPos4 != null) {
                final ItemStack itemStack6 = FilledMapItem.createMap(serverWorld4, blockPos4.getX(), blockPos4.getZ(), this.zoom, true, true);
                FilledMapItem.fillExplorationMap(serverWorld4, itemStack6);
                MapState.addDecorationsTag(itemStack6, blockPos4, "+", this.decoration);
                itemStack6.setDisplayName(new TranslatableTextComponent("filled_map." + this.destination.toLowerCase(Locale.ROOT), new Object[0]));
                return itemStack6;
            }
        }
        return stack;
    }
    
    public static Builder create() {
        return new Builder();
    }
    
    static {
        LOGGER = LogManager.getLogger();
        DEFAULT_DECORATION = MapIcon.Type.i;
    }
    
    public static class Builder extends ConditionalLootFunction.Builder<Builder>
    {
        private String destination;
        private MapIcon.Type decoration;
        private byte zoom;
        private int searchRadius;
        private boolean skipExistingChunks;
        
        public Builder() {
            this.destination = "Buried_Treasure";
            this.decoration = ExplorationMapLootFunction.DEFAULT_DECORATION;
            this.zoom = 2;
            this.searchRadius = 50;
            this.skipExistingChunks = true;
        }
        
        @Override
        protected Builder getThisBuilder() {
            return this;
        }
        
        public Builder withDestination(final String destination) {
            this.destination = destination;
            return this;
        }
        
        public Builder withDecoration(final MapIcon.Type decoration) {
            this.decoration = decoration;
            return this;
        }
        
        public Builder withZoom(final byte zoom) {
            this.zoom = zoom;
            return this;
        }
        
        public Builder withSkipExistingChunks(final boolean skipExistingChunks) {
            this.skipExistingChunks = skipExistingChunks;
            return this;
        }
        
        @Override
        public LootFunction build() {
            return new ExplorationMapLootFunction(this.getConditions(), this.destination, this.decoration, this.zoom, this.searchRadius, this.skipExistingChunks, null);
        }
    }
    
    public static class Factory extends ConditionalLootFunction.Factory<ExplorationMapLootFunction>
    {
        protected Factory() {
            super(new Identifier("exploration_map"), ExplorationMapLootFunction.class);
        }
        
        @Override
        public void toJson(final JsonObject json, final ExplorationMapLootFunction function, final JsonSerializationContext context) {
            super.toJson(json, function, context);
            if (!function.destination.equals("Buried_Treasure")) {
                json.add("destination", context.serialize(function.destination));
            }
            if (function.decoration != ExplorationMapLootFunction.DEFAULT_DECORATION) {
                json.add("decoration", context.serialize(function.decoration.toString().toLowerCase(Locale.ROOT)));
            }
            if (function.zoom != 2) {
                json.addProperty("zoom", function.zoom);
            }
            if (function.searchRadius != 50) {
                json.addProperty("search_radius", function.searchRadius);
            }
            if (!function.skipExistingChunks) {
                json.addProperty("skip_existing_chunks", function.skipExistingChunks);
            }
        }
        
        @Override
        public ExplorationMapLootFunction fromJson(final JsonObject json, final JsonDeserializationContext context, final LootCondition[] conditions) {
            String string4 = json.has("destination") ? JsonHelper.getString(json, "destination") : "Buried_Treasure";
            string4 = (Feature.STRUCTURES.containsKey(string4.toLowerCase(Locale.ROOT)) ? string4 : "Buried_Treasure");
            final String string5 = json.has("decoration") ? JsonHelper.getString(json, "decoration") : "mansion";
            MapIcon.Type type6 = ExplorationMapLootFunction.DEFAULT_DECORATION;
            try {
                type6 = MapIcon.Type.valueOf(string5.toUpperCase(Locale.ROOT));
            }
            catch (IllegalArgumentException illegalArgumentException7) {
                ExplorationMapLootFunction.LOGGER.error("Error while parsing loot table decoration entry. Found {}. Defaulting to " + ExplorationMapLootFunction.DEFAULT_DECORATION, string5);
            }
            final byte byte7 = JsonHelper.getByte(json, "zoom", (byte)2);
            final int integer8 = JsonHelper.getInt(json, "search_radius", 50);
            final boolean boolean9 = JsonHelper.getBoolean(json, "skip_existing_chunks", true);
            return new ExplorationMapLootFunction(conditions, string4, type6, byte7, integer8, boolean9, null);
        }
    }
}

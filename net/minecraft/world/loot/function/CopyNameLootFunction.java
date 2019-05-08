package net.minecraft.world.loot.function;

import net.minecraft.util.JsonHelper;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;
import net.minecraft.world.loot.context.LootContextParameters;
import net.minecraft.util.Nameable;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.item.ItemStack;
import com.google.common.collect.ImmutableSet;
import net.minecraft.world.loot.context.LootContextParameter;
import java.util.Set;
import net.minecraft.world.loot.condition.LootCondition;

public class CopyNameLootFunction extends ConditionalLootFunction
{
    private final Source source;
    
    private CopyNameLootFunction(final LootCondition[] conditions, final Source source) {
        super(conditions);
        this.source = source;
    }
    
    @Override
    public Set<LootContextParameter<?>> getRequiredParameters() {
        return ImmutableSet.<LootContextParameter<?>>of(this.source.parameter);
    }
    
    public ItemStack process(final ItemStack stack, final LootContext context) {
        final Object object3 = context.get(this.source.parameter);
        if (object3 instanceof Nameable) {
            final Nameable nameable4 = (Nameable)object3;
            if (nameable4.hasCustomName()) {
                stack.setDisplayName(nameable4.getDisplayName());
            }
        }
        return stack;
    }
    
    public static Builder<?> builder(final Source source) {
        return ConditionalLootFunction.builder(conditions -> new CopyNameLootFunction(conditions, source));
    }
    
    public enum Source
    {
        THIS("this", LootContextParameters.a), 
        KILLER("killer", LootContextParameters.d), 
        KILLER_PLAYER("killer_player", LootContextParameters.b), 
        BLOCK_ENTITY("block_entity", LootContextParameters.h);
        
        public final String name;
        public final LootContextParameter<?> parameter;
        
        private Source(final String name, final LootContextParameter<?> parameter) {
            this.name = name;
            this.parameter = parameter;
        }
        
        public static Source get(final String name) {
            for (final Source source5 : values()) {
                if (source5.name.equals(name)) {
                    return source5;
                }
            }
            throw new IllegalArgumentException("Invalid name source " + name);
        }
    }
    
    public static class Factory extends ConditionalLootFunction.Factory<CopyNameLootFunction>
    {
        public Factory() {
            super(new Identifier("copy_name"), CopyNameLootFunction.class);
        }
        
        @Override
        public void toJson(final JsonObject json, final CopyNameLootFunction function, final JsonSerializationContext context) {
            super.toJson(json, function, context);
            json.addProperty("source", function.source.name);
        }
        
        @Override
        public CopyNameLootFunction fromJson(final JsonObject json, final JsonDeserializationContext context, final LootCondition[] conditions) {
            final Source source4 = Source.get(JsonHelper.getString(json, "source"));
            return new CopyNameLootFunction(conditions, source4, null);
        }
    }
}

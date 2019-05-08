package net.minecraft.world.loot.function;

import net.minecraft.util.JsonHelper;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.text.TextFormatter;
import java.util.function.UnaryOperator;
import net.minecraft.entity.Entity;
import com.google.common.collect.ImmutableSet;
import net.minecraft.world.loot.context.LootContextParameter;
import java.util.Set;
import net.minecraft.world.loot.condition.LootCondition;
import javax.annotation.Nullable;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.text.TextComponent;
import org.apache.logging.log4j.Logger;

public class SetNameLootFunction extends ConditionalLootFunction
{
    private static final Logger LOGGER;
    private final TextComponent name;
    @Nullable
    private final LootContext.EntityTarget entity;
    
    private SetNameLootFunction(final LootCondition[] conditions, @Nullable final TextComponent name, @Nullable final LootContext.EntityTarget entity) {
        super(conditions);
        this.name = name;
        this.entity = entity;
    }
    
    @Override
    public Set<LootContextParameter<?>> getRequiredParameters() {
        return (Set<LootContextParameter<?>>)((this.entity != null) ? ImmutableSet.<LootContextParameter<? extends Entity>>of(this.entity.getIdentifier()) : ImmutableSet.<LootContextParameter<? extends Entity>>of());
    }
    
    public static UnaryOperator<TextComponent> applySourceEntity(final LootContext context, @Nullable final LootContext.EntityTarget sourceEntity) {
        if (sourceEntity != null) {
            final Entity entity3 = context.<Entity>get(sourceEntity.getIdentifier());
            if (entity3 != null) {
                final ServerCommandSource serverCommandSource4 = entity3.getCommandSource().withLevel(2);
                final ServerCommandSource source;
                final Entity entity4;
                return (UnaryOperator<TextComponent>)(textComponent -> {
                    try {
                        return TextFormatter.resolveAndStyle(source, textComponent, entity4);
                    }
                    catch (CommandSyntaxException commandSyntaxException4) {
                        SetNameLootFunction.LOGGER.warn("Failed to resolve text component", (Throwable)commandSyntaxException4);
                        return textComponent;
                    }
                });
            }
        }
        return (UnaryOperator<TextComponent>)(textComponent -> textComponent);
    }
    
    public ItemStack process(final ItemStack stack, final LootContext context) {
        if (this.name != null) {
            stack.setDisplayName(applySourceEntity(context, this.entity).apply(this.name));
        }
        return stack;
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
    
    public static class Factory extends ConditionalLootFunction.Factory<SetNameLootFunction>
    {
        public Factory() {
            super(new Identifier("set_name"), SetNameLootFunction.class);
        }
        
        @Override
        public void toJson(final JsonObject json, final SetNameLootFunction function, final JsonSerializationContext context) {
            super.toJson(json, function, context);
            if (function.name != null) {
                json.add("name", TextComponent.Serializer.toJson(function.name));
            }
            if (function.entity != null) {
                json.add("entity", context.serialize(function.entity));
            }
        }
        
        @Override
        public SetNameLootFunction fromJson(final JsonObject json, final JsonDeserializationContext context, final LootCondition[] conditions) {
            final TextComponent textComponent4 = TextComponent.Serializer.fromJson(json.get("name"));
            final LootContext.EntityTarget entityTarget5 = JsonHelper.<LootContext.EntityTarget>deserialize(json, "entity", (LootContext.EntityTarget)null, context, LootContext.EntityTarget.class);
            return new SetNameLootFunction(conditions, textComponent4, entityTarget5, null);
        }
    }
}

package net.minecraft.world.loot.function;

import java.util.stream.Collector;
import com.google.common.collect.Streams;
import net.minecraft.util.JsonHelper;
import com.google.gson.JsonDeserializationContext;
import java.util.Iterator;
import com.google.gson.JsonElement;
import com.google.gson.JsonArray;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import java.util.function.UnaryOperator;
import net.minecraft.nbt.ListTag;
import java.util.function.Consumer;
import net.minecraft.nbt.StringTag;
import java.util.function.Function;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.Entity;
import com.google.common.collect.ImmutableSet;
import net.minecraft.world.loot.context.LootContextParameter;
import java.util.Set;
import java.util.Collection;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.loot.condition.LootCondition;
import javax.annotation.Nullable;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.text.TextComponent;
import java.util.List;

public class SetLoreLootFunction extends ConditionalLootFunction
{
    private final boolean replace;
    private final List<TextComponent> lore;
    @Nullable
    private final LootContext.EntityTarget entity;
    
    public SetLoreLootFunction(final LootCondition[] conditions, final boolean replace, final List<TextComponent> lore, @Nullable final LootContext.EntityTarget entity) {
        super(conditions);
        this.replace = replace;
        this.lore = ImmutableList.copyOf(lore);
        this.entity = entity;
    }
    
    @Override
    public Set<LootContextParameter<?>> getRequiredParameters() {
        return (Set<LootContextParameter<?>>)((this.entity != null) ? ImmutableSet.<LootContextParameter<? extends Entity>>of(this.entity.getIdentifier()) : ImmutableSet.<LootContextParameter<? extends Entity>>of());
    }
    
    public ItemStack process(final ItemStack stack, final LootContext context) {
        final ListTag listTag3 = this.getLoreForMerge(stack, !this.lore.isEmpty());
        if (listTag3 != null) {
            if (this.replace) {
                listTag3.clear();
            }
            final UnaryOperator<TextComponent> unaryOperator4 = SetNameLootFunction.applySourceEntity(context, this.entity);
            this.lore.stream().map(unaryOperator4).map(TextComponent.Serializer::toJsonString).map(StringTag::new).forEach(listTag3::add);
        }
        return stack;
    }
    
    @Nullable
    private ListTag getLoreForMerge(final ItemStack stack, final boolean otherLoreExists) {
        CompoundTag compoundTag3;
        if (stack.hasTag()) {
            compoundTag3 = stack.getTag();
        }
        else {
            if (!otherLoreExists) {
                return null;
            }
            compoundTag3 = new CompoundTag();
            stack.setTag(compoundTag3);
        }
        CompoundTag compoundTag4;
        if (compoundTag3.containsKey("display", 10)) {
            compoundTag4 = compoundTag3.getCompound("display");
        }
        else {
            if (!otherLoreExists) {
                return null;
            }
            compoundTag4 = new CompoundTag();
            compoundTag3.put("display", compoundTag4);
        }
        if (compoundTag4.containsKey("Lore", 9)) {
            return compoundTag4.getList("Lore", 8);
        }
        if (otherLoreExists) {
            final ListTag listTag5 = new ListTag();
            compoundTag4.put("Lore", listTag5);
            return listTag5;
        }
        return null;
    }
    
    public static class Factory extends ConditionalLootFunction.Factory<SetLoreLootFunction>
    {
        public Factory() {
            super(new Identifier("set_lore"), SetLoreLootFunction.class);
        }
        
        @Override
        public void toJson(final JsonObject json, final SetLoreLootFunction function, final JsonSerializationContext context) {
            super.toJson(json, function, context);
            json.addProperty("replace", function.replace);
            final JsonArray jsonArray4 = new JsonArray();
            for (final TextComponent textComponent6 : function.lore) {
                jsonArray4.add(TextComponent.Serializer.toJson(textComponent6));
            }
            json.add("lore", jsonArray4);
            if (function.entity != null) {
                json.add("entity", context.serialize(function.entity));
            }
        }
        
        @Override
        public SetLoreLootFunction fromJson(final JsonObject json, final JsonDeserializationContext context, final LootCondition[] conditions) {
            final boolean boolean4 = JsonHelper.getBoolean(json, "replace", false);
            final List<TextComponent> list5 = Streams.stream((Iterable<Object>)JsonHelper.getArray(json, "lore")).map(TextComponent.Serializer::fromJson).collect(ImmutableList.toImmutableList());
            final LootContext.EntityTarget entityTarget6 = JsonHelper.<LootContext.EntityTarget>deserialize(json, "entity", (LootContext.EntityTarget)null, context, LootContext.EntityTarget.class);
            return new SetLoreLootFunction(conditions, boolean4, list5, entityTarget6);
        }
    }
}

package net.minecraft.world.loot.function;

import net.minecraft.util.JsonHelper;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;
import com.mojang.authlib.GameProfile;
import net.minecraft.nbt.Tag;
import net.minecraft.util.TagHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.Entity;
import com.google.common.collect.ImmutableSet;
import net.minecraft.world.loot.context.LootContextParameter;
import java.util.Set;
import net.minecraft.world.loot.condition.LootCondition;
import net.minecraft.world.loot.context.LootContext;

public class FillPlayerHeadLootFunction extends ConditionalLootFunction
{
    private final LootContext.EntityTarget entity;
    
    public FillPlayerHeadLootFunction(final LootCondition[] conditions, final LootContext.EntityTarget entity) {
        super(conditions);
        this.entity = entity;
    }
    
    @Override
    public Set<LootContextParameter<?>> getRequiredParameters() {
        return ImmutableSet.of(this.entity.getIdentifier());
    }
    
    public ItemStack process(final ItemStack stack, final LootContext context) {
        if (stack.getItem() == Items.PLAYER_HEAD) {
            final Entity entity3 = context.<Entity>get(this.entity.getIdentifier());
            if (entity3 instanceof PlayerEntity) {
                final GameProfile gameProfile4 = ((PlayerEntity)entity3).getGameProfile();
                stack.getOrCreateTag().put("SkullOwner", TagHelper.serializeProfile(new CompoundTag(), gameProfile4));
            }
        }
        return stack;
    }
    
    public static class Factory extends ConditionalLootFunction.Factory<FillPlayerHeadLootFunction>
    {
        public Factory() {
            super(new Identifier("fill_player_head"), FillPlayerHeadLootFunction.class);
        }
        
        @Override
        public void toJson(final JsonObject json, final FillPlayerHeadLootFunction function, final JsonSerializationContext context) {
            super.toJson(json, function, context);
            json.add("entity", context.serialize(function.entity));
        }
        
        @Override
        public FillPlayerHeadLootFunction fromJson(final JsonObject json, final JsonDeserializationContext context, final LootCondition[] conditions) {
            final LootContext.EntityTarget entityTarget4 = JsonHelper.<LootContext.EntityTarget>deserialize(json, "entity", context, LootContext.EntityTarget.class);
            return new FillPlayerHeadLootFunction(conditions, entityTarget4);
        }
    }
}

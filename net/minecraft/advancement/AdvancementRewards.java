package net.minecraft.advancement;

import com.google.common.collect.Lists;
import javax.annotation.Nullable;
import java.util.List;
import com.google.gson.JsonParseException;
import net.minecraft.util.JsonHelper;
import com.google.gson.JsonDeserializationContext;
import java.lang.reflect.Type;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonNull;
import com.google.gson.JsonElement;
import java.util.Arrays;
import net.minecraft.server.MinecraftServer;
import net.minecraft.entity.ItemEntity;
import java.util.Iterator;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.world.loot.context.LootContextTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.Entity;
import net.minecraft.world.loot.context.LootContextParameters;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.util.Identifier;

public class AdvancementRewards
{
    public static final AdvancementRewards NONE;
    private final int experience;
    private final Identifier[] loot;
    private final Identifier[] recipes;
    private final CommandFunction.LazyContainer function;
    
    public AdvancementRewards(final int experience, final Identifier[] loot, final Identifier[] recipes, final CommandFunction.LazyContainer lazyContainer) {
        this.experience = experience;
        this.loot = loot;
        this.recipes = recipes;
        this.function = lazyContainer;
    }
    
    public void apply(final ServerPlayerEntity serverPlayerEntity) {
        serverPlayerEntity.addExperience(this.experience);
        final LootContext lootContext2 = new LootContext.Builder(serverPlayerEntity.getServerWorld()).<Entity>put(LootContextParameters.a, serverPlayerEntity).<BlockPos>put(LootContextParameters.f, new BlockPos(serverPlayerEntity)).setRandom(serverPlayerEntity.getRand()).build(LootContextTypes.ADVANCEMENT_REWARD);
        boolean boolean3 = false;
        for (final Identifier identifier7 : this.loot) {
            for (final ItemStack itemStack9 : serverPlayerEntity.server.getLootManager().getSupplier(identifier7).getDrops(lootContext2)) {
                if (serverPlayerEntity.giveItemStack(itemStack9)) {
                    serverPlayerEntity.world.playSound(null, serverPlayerEntity.x, serverPlayerEntity.y, serverPlayerEntity.z, SoundEvents.fF, SoundCategory.h, 0.2f, ((serverPlayerEntity.getRand().nextFloat() - serverPlayerEntity.getRand().nextFloat()) * 0.7f + 1.0f) * 2.0f);
                    boolean3 = true;
                }
                else {
                    final ItemEntity itemEntity10 = serverPlayerEntity.dropItem(itemStack9, false);
                    if (itemEntity10 == null) {
                        continue;
                    }
                    itemEntity10.resetPickupDelay();
                    itemEntity10.setOwner(serverPlayerEntity.getUuid());
                }
            }
        }
        if (boolean3) {
            serverPlayerEntity.playerContainer.sendContentUpdates();
        }
        if (this.recipes.length > 0) {
            serverPlayerEntity.unlockRecipes(this.recipes);
        }
        final MinecraftServer minecraftServer4 = serverPlayerEntity.server;
        this.function.get(minecraftServer4.getCommandFunctionManager()).ifPresent(commandFunction -> minecraftServer4.getCommandFunctionManager().execute(commandFunction, serverPlayerEntity.getCommandSource().withSilent().withLevel(2)));
    }
    
    @Override
    public String toString() {
        return "AdvancementRewards{experience=" + this.experience + ", loot=" + Arrays.toString(this.loot) + ", recipes=" + Arrays.toString(this.recipes) + ", function=" + this.function + '}';
    }
    
    public JsonElement toJson() {
        if (this == AdvancementRewards.NONE) {
            return JsonNull.INSTANCE;
        }
        final JsonObject jsonObject1 = new JsonObject();
        if (this.experience != 0) {
            jsonObject1.addProperty("experience", this.experience);
        }
        if (this.loot.length > 0) {
            final JsonArray jsonArray2 = new JsonArray();
            for (final Identifier identifier6 : this.loot) {
                jsonArray2.add(identifier6.toString());
            }
            jsonObject1.add("loot", jsonArray2);
        }
        if (this.recipes.length > 0) {
            final JsonArray jsonArray2 = new JsonArray();
            for (final Identifier identifier6 : this.recipes) {
                jsonArray2.add(identifier6.toString());
            }
            jsonObject1.add("recipes", jsonArray2);
        }
        if (this.function.getId() != null) {
            jsonObject1.addProperty("function", this.function.getId().toString());
        }
        return jsonObject1;
    }
    
    static {
        NONE = new AdvancementRewards(0, new Identifier[0], new Identifier[0], CommandFunction.LazyContainer.EMPTY);
    }
    
    public static class Deserializer implements JsonDeserializer<AdvancementRewards>
    {
        public AdvancementRewards a(final JsonElement functionJson, final Type unused, final JsonDeserializationContext context) throws JsonParseException {
            final JsonObject jsonObject4 = JsonHelper.asObject(functionJson, "rewards");
            final int integer5 = JsonHelper.getInt(jsonObject4, "experience", 0);
            final JsonArray jsonArray6 = JsonHelper.getArray(jsonObject4, "loot", new JsonArray());
            final Identifier[] arr7 = new Identifier[jsonArray6.size()];
            for (int integer6 = 0; integer6 < arr7.length; ++integer6) {
                arr7[integer6] = new Identifier(JsonHelper.asString(jsonArray6.get(integer6), "loot[" + integer6 + "]"));
            }
            final JsonArray jsonArray7 = JsonHelper.getArray(jsonObject4, "recipes", new JsonArray());
            final Identifier[] arr8 = new Identifier[jsonArray7.size()];
            for (int integer7 = 0; integer7 < arr8.length; ++integer7) {
                arr8[integer7] = new Identifier(JsonHelper.asString(jsonArray7.get(integer7), "recipes[" + integer7 + "]"));
            }
            CommandFunction.LazyContainer lazyContainer10;
            if (jsonObject4.has("function")) {
                lazyContainer10 = new CommandFunction.LazyContainer(new Identifier(JsonHelper.getString(jsonObject4, "function")));
            }
            else {
                lazyContainer10 = CommandFunction.LazyContainer.EMPTY;
            }
            return new AdvancementRewards(integer5, arr7, arr8, lazyContainer10);
        }
    }
    
    public static class Builder
    {
        private int experience;
        private final List<Identifier> loot;
        private final List<Identifier> recipes;
        @Nullable
        private Identifier function;
        
        public Builder() {
            this.loot = Lists.newArrayList();
            this.recipes = Lists.newArrayList();
        }
        
        public static Builder experience(final int integer) {
            return new Builder().setExperience(integer);
        }
        
        public Builder setExperience(final int integer) {
            this.experience += integer;
            return this;
        }
        
        public static Builder recipe(final Identifier identifier) {
            return new Builder().addRecipe(identifier);
        }
        
        public Builder addRecipe(final Identifier identifier) {
            this.recipes.add(identifier);
            return this;
        }
        
        public AdvancementRewards build() {
            return new AdvancementRewards(this.experience, (Identifier[])this.loot.<qs>toArray((qs[])new Identifier[0]), (Identifier[])this.recipes.<qs>toArray((qs[])new Identifier[0]), (this.function == null) ? CommandFunction.LazyContainer.EMPTY : new CommandFunction.LazyContainer(this.function));
        }
    }
}

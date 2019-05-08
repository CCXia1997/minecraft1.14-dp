package net.minecraft.world.loot.condition;

import com.google.common.collect.Maps;
import net.minecraft.util.JsonHelper;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;
import net.minecraft.scoreboard.ScoreboardObjective;
import java.util.Iterator;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.entity.Entity;
import com.google.common.collect.ImmutableSet;
import net.minecraft.world.loot.context.LootContextParameter;
import java.util.Set;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.UniformLootTableRange;
import java.util.Map;

public class EntityScoresLootCondition implements LootCondition
{
    private final Map<String, UniformLootTableRange> scores;
    private final LootContext.EntityTarget target;
    
    private EntityScoresLootCondition(final Map<String, UniformLootTableRange> scores, final LootContext.EntityTarget target) {
        this.scores = ImmutableMap.copyOf(scores);
        this.target = target;
    }
    
    @Override
    public Set<LootContextParameter<?>> getRequiredParameters() {
        return ImmutableSet.of(this.target.getIdentifier());
    }
    
    public boolean a(final LootContext context) {
        final Entity entity2 = context.<Entity>get(this.target.getIdentifier());
        if (entity2 == null) {
            return false;
        }
        final Scoreboard scoreboard3 = entity2.world.getScoreboard();
        for (final Map.Entry<String, UniformLootTableRange> entry5 : this.scores.entrySet()) {
            if (!this.entityScoreIsInRange(entity2, scoreboard3, entry5.getKey(), entry5.getValue())) {
                return false;
            }
        }
        return true;
    }
    
    protected boolean entityScoreIsInRange(final Entity entity, final Scoreboard scoreboard, final String objective, final UniformLootTableRange scoreRange) {
        final ScoreboardObjective scoreboardObjective5 = scoreboard.getNullableObjective(objective);
        if (scoreboardObjective5 == null) {
            return false;
        }
        final String string6 = entity.getEntityName();
        return scoreboard.playerHasObjective(string6, scoreboardObjective5) && scoreRange.contains(scoreboard.getPlayerScore(string6, scoreboardObjective5).getScore());
    }
    
    public static class Factory extends LootCondition.Factory<EntityScoresLootCondition>
    {
        protected Factory() {
            super(new Identifier("entity_scores"), EntityScoresLootCondition.class);
        }
        
        @Override
        public void toJson(final JsonObject json, final EntityScoresLootCondition condition, final JsonSerializationContext context) {
            final JsonObject jsonObject4 = new JsonObject();
            for (final Map.Entry<String, UniformLootTableRange> entry6 : condition.scores.entrySet()) {
                jsonObject4.add(entry6.getKey(), context.serialize(entry6.getValue()));
            }
            json.add("scores", jsonObject4);
            json.add("entity", context.serialize(condition.target));
        }
        
        @Override
        public EntityScoresLootCondition fromJson(final JsonObject json, final JsonDeserializationContext context) {
            final Set<Map.Entry<String, JsonElement>> set3 = JsonHelper.getObject(json, "scores").entrySet();
            final Map<String, UniformLootTableRange> map4 = Maps.newLinkedHashMap();
            for (final Map.Entry<String, JsonElement> entry6 : set3) {
                map4.put(entry6.getKey(), JsonHelper.<UniformLootTableRange>deserialize((JsonElement)entry6.getValue(), "score", context, UniformLootTableRange.class));
            }
            return new EntityScoresLootCondition(map4, JsonHelper.<LootContext.EntityTarget>deserialize(json, "entity", context, LootContext.EntityTarget.class), null);
        }
    }
}

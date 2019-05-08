package net.minecraft.world.loot.function;

import com.google.gson.JsonParseException;
import com.google.gson.JsonElement;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.JsonHelper;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonObject;
import java.util.Random;
import com.google.common.collect.Maps;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.item.ItemStack;
import com.google.common.collect.ImmutableSet;
import net.minecraft.world.loot.context.LootContextParameters;
import net.minecraft.world.loot.context.LootContextParameter;
import java.util.Set;
import net.minecraft.world.loot.condition.LootCondition;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.Identifier;
import java.util.Map;

public class ApplyBonusLootFunction extends ConditionalLootFunction
{
    private static final Map<Identifier, FormulaFactory> FACTORIES;
    private final Enchantment enchantment;
    private final Formula formula;
    
    private ApplyBonusLootFunction(final LootCondition[] conditions, final Enchantment enchantment, final Formula formula) {
        super(conditions);
        this.enchantment = enchantment;
        this.formula = formula;
    }
    
    @Override
    public Set<LootContextParameter<?>> getRequiredParameters() {
        return ImmutableSet.of(LootContextParameters.i);
    }
    
    public ItemStack process(final ItemStack stack, final LootContext context) {
        final ItemStack itemStack3 = context.<ItemStack>get(LootContextParameters.i);
        if (itemStack3 != null) {
            final int integer4 = EnchantmentHelper.getLevel(this.enchantment, itemStack3);
            final int integer5 = this.formula.getValue(context.getRandom(), stack.getAmount(), integer4);
            stack.setAmount(integer5);
        }
        return stack;
    }
    
    public static Builder<?> binomialWithBonusCount(final Enchantment enchantment, final float probability, final int extra) {
        final ApplyBonusLootFunction applyBonusLootFunction;
        return ConditionalLootFunction.builder(conditions -> {
            new ApplyBonusLootFunction(conditions, enchantment, new BinomialWithBonusCount(extra, probability));
            return applyBonusLootFunction;
        });
    }
    
    public static Builder<?> oreDrops(final Enchantment enchantment) {
        final ApplyBonusLootFunction applyBonusLootFunction;
        return ConditionalLootFunction.builder(conditions -> {
            new ApplyBonusLootFunction(conditions, enchantment, new OreDrops());
            return applyBonusLootFunction;
        });
    }
    
    public static Builder<?> uniformBonusCount(final Enchantment enchantment) {
        final ApplyBonusLootFunction applyBonusLootFunction;
        return ConditionalLootFunction.builder(conditions -> {
            new ApplyBonusLootFunction(conditions, enchantment, new UniformBonusCount(1));
            return applyBonusLootFunction;
        });
    }
    
    public static Builder<?> uniformBonusCount(final Enchantment enchantment, final int bonusMultiplier) {
        final ApplyBonusLootFunction applyBonusLootFunction;
        return ConditionalLootFunction.builder(conditions -> {
            new ApplyBonusLootFunction(conditions, enchantment, new UniformBonusCount(bonusMultiplier));
            return applyBonusLootFunction;
        });
    }
    
    static {
        (FACTORIES = Maps.newHashMap()).put(BinomialWithBonusCount.ID, BinomialWithBonusCount::fromJson);
        ApplyBonusLootFunction.FACTORIES.put(OreDrops.ID, OreDrops::fromJson);
        ApplyBonusLootFunction.FACTORIES.put(UniformBonusCount.ID, UniformBonusCount::fromJson);
    }
    
    static final class BinomialWithBonusCount implements Formula
    {
        public static final Identifier ID;
        private final int extra;
        private final float probability;
        
        public BinomialWithBonusCount(final int extra, final float probability) {
            this.extra = extra;
            this.probability = probability;
        }
        
        @Override
        public int getValue(final Random random, int initialCount, final int enchantmentLevel) {
            for (int integer4 = 0; integer4 < enchantmentLevel + this.extra; ++integer4) {
                if (random.nextFloat() < this.probability) {
                    ++initialCount;
                }
            }
            return initialCount;
        }
        
        @Override
        public void toJson(final JsonObject json, final JsonSerializationContext context) {
            json.addProperty("extra", this.extra);
            json.addProperty("probability", this.probability);
        }
        
        public static Formula fromJson(final JsonObject json, final JsonDeserializationContext context) {
            final int integer3 = JsonHelper.getInt(json, "extra");
            final float float4 = JsonHelper.getFloat(json, "probability");
            return new BinomialWithBonusCount(integer3, float4);
        }
        
        @Override
        public Identifier getId() {
            return BinomialWithBonusCount.ID;
        }
        
        static {
            ID = new Identifier("binomial_with_bonus_count");
        }
    }
    
    static final class UniformBonusCount implements Formula
    {
        public static final Identifier ID;
        private final int bonusMultiplier;
        
        public UniformBonusCount(final int bonusMultiplier) {
            this.bonusMultiplier = bonusMultiplier;
        }
        
        @Override
        public int getValue(final Random random, final int initialCount, final int enchantmentLevel) {
            return initialCount + random.nextInt(this.bonusMultiplier * enchantmentLevel + 1);
        }
        
        @Override
        public void toJson(final JsonObject json, final JsonSerializationContext context) {
            json.addProperty("bonusMultiplier", this.bonusMultiplier);
        }
        
        public static Formula fromJson(final JsonObject json, final JsonDeserializationContext context) {
            final int integer3 = JsonHelper.getInt(json, "bonusMultiplier");
            return new UniformBonusCount(integer3);
        }
        
        @Override
        public Identifier getId() {
            return UniformBonusCount.ID;
        }
        
        static {
            ID = new Identifier("uniform_bonus_count");
        }
    }
    
    static final class OreDrops implements Formula
    {
        public static final Identifier ID;
        
        private OreDrops() {
        }
        
        @Override
        public int getValue(final Random random, final int initialCount, final int enchantmentLevel) {
            if (enchantmentLevel > 0) {
                int integer4 = random.nextInt(enchantmentLevel + 2) - 1;
                if (integer4 < 0) {
                    integer4 = 0;
                }
                return initialCount * (integer4 + 1);
            }
            return initialCount;
        }
        
        @Override
        public void toJson(final JsonObject json, final JsonSerializationContext context) {
        }
        
        public static Formula fromJson(final JsonObject json, final JsonDeserializationContext context) {
            return new OreDrops();
        }
        
        @Override
        public Identifier getId() {
            return OreDrops.ID;
        }
        
        static {
            ID = new Identifier("ore_drops");
        }
    }
    
    public static class Factory extends ConditionalLootFunction.Factory<ApplyBonusLootFunction>
    {
        public Factory() {
            super(new Identifier("apply_bonus"), ApplyBonusLootFunction.class);
        }
        
        @Override
        public void toJson(final JsonObject json, final ApplyBonusLootFunction function, final JsonSerializationContext context) {
            super.toJson(json, function, context);
            json.addProperty("enchantment", Registry.ENCHANTMENT.getId(function.enchantment).toString());
            json.addProperty("formula", function.formula.getId().toString());
            final JsonObject jsonObject4 = new JsonObject();
            function.formula.toJson(jsonObject4, context);
            if (jsonObject4.size() > 0) {
                json.add("parameters", jsonObject4);
            }
        }
        
        @Override
        public ApplyBonusLootFunction fromJson(final JsonObject json, final JsonDeserializationContext context, final LootCondition[] conditions) {
            final Identifier identifier4 = new Identifier(JsonHelper.getString(json, "enchantment"));
            final Object o;
            final Object o2;
            final Enchantment enchantment5 = Registry.ENCHANTMENT.getOrEmpty(identifier4).<Throwable>orElseThrow(() -> {
                new JsonParseException("Invalid enchantment id: " + o2);
                return o;
            });
            final Identifier identifier5 = new Identifier(JsonHelper.getString(json, "formula"));
            final FormulaFactory formulaFactory7 = ApplyBonusLootFunction.FACTORIES.get(identifier5);
            if (formulaFactory7 == null) {
                throw new JsonParseException("Invalid formula id: " + identifier5);
            }
            Formula formula8;
            if (json.has("parameters")) {
                formula8 = formulaFactory7.deserialize(JsonHelper.getObject(json, "parameters"), context);
            }
            else {
                formula8 = formulaFactory7.deserialize(new JsonObject(), context);
            }
            return new ApplyBonusLootFunction(conditions, enchantment5, formula8, null);
        }
    }
    
    interface Formula
    {
        int getValue(final Random arg1, final int arg2, final int arg3);
        
        void toJson(final JsonObject arg1, final JsonSerializationContext arg2);
        
        Identifier getId();
    }
    
    interface FormulaFactory
    {
        Formula deserialize(final JsonObject arg1, final JsonDeserializationContext arg2);
    }
}

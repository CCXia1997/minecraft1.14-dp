package net.minecraft.advancement.criterion;

import com.google.common.collect.Maps;
import javax.annotation.Nullable;
import net.minecraft.util.Identifier;
import java.util.Map;

public class Criterions
{
    private static final Map<Identifier, Criterion<?>> VALUES;
    public static final ImpossibleCriterion IMPOSSIBLE;
    public static final OnKilledCriterion PLAYER_KILLED_ENTITY;
    public static final OnKilledCriterion ENTITY_KILLED_PLAYER;
    public static final EnterBlockCriterion ENTER_BLOCK;
    public static final InventoryChangedCriterion INVENTORY_CHANGED;
    public static final RecipeUnlockedCriterion RECIPE_UNLOCKED;
    public static final PlayerHurtEntityCriterion PLAYER_HURT_ENTITY;
    public static final EntityHurtPlayerCriterion ENTITY_HURT_PLAYER;
    public static final EnchantedItemCriterion ENCHANTED_ITEM;
    public static final FilledBucketCriterion FILLED_BUCKET;
    public static final BrewedPotionCriterion BREWED_POTION;
    public static final ConstructBeaconCriterion CONSTRUCT_BEACON;
    public static final UsedEnderEyeCriterion USED_ENDER_EYE;
    public static final SummonedEntityCriterion SUMMONED_ENTITY;
    public static final BredAnimalsCriterion BRED_ANIMALS;
    public static final LocationArrivalCriterion LOCATION;
    public static final LocationArrivalCriterion SLEPT_IN_BED;
    public static final CuredZombieVillagerCriterion CURED_ZOMBIE_VILLAGER;
    public static final VillagerTradeCriterion VILLAGER_TRADE;
    public static final ItemDurabilityChangedCriterion ITEM_DURABILITY_CHANGED;
    public static final LevitationCriterion LEVITATION;
    public static final ChangedDimensionCriterion CHANGED_DIMENSION;
    public static final TickCriterion TICK;
    public static final TameAnimalCriterion TAME_ANIMAL;
    public static final PlacedBlockCriterion PLACED_BLOCK;
    public static final ConsumeItemCriterion CONSUME_ITEM;
    public static final EffectsChangedCriterion EFFECTS_CHANGED;
    public static final UsedTotemCriterion USED_TOTEM;
    public static final NetherTravelCriterion NETHER_TRAVEL;
    public static final FishingRodHookedCriterion FISHING_ROD_HOOKED;
    public static final ChanneledLightningCriterion CHANNELED_LIGHTNING;
    public static final ShotCrossbowCriterion SHOT_CROSSBOW;
    public static final KilledByCrossbowCriterion KILLED_BY_CROSSBOW;
    public static final LocationArrivalCriterion HERO_OF_THE_VILLAGE;
    public static final LocationArrivalCriterion VOLUNTARY_EXILE;
    
    private static <T extends Criterion<?>> T register(final T object) {
        if (Criterions.VALUES.containsKey(object.getId())) {
            throw new IllegalArgumentException("Duplicate criterion id " + object.getId());
        }
        Criterions.VALUES.put(object.getId(), object);
        return object;
    }
    
    @Nullable
    public static <T extends CriterionConditions> Criterion<T> getById(final Identifier id) {
        return (Criterion<T>)Criterions.VALUES.get(id);
    }
    
    public static Iterable<? extends Criterion<?>> getAllCriterions() {
        return Criterions.VALUES.values();
    }
    
    static {
        VALUES = Maps.newHashMap();
        IMPOSSIBLE = Criterions.<ImpossibleCriterion>register(new ImpossibleCriterion());
        PLAYER_KILLED_ENTITY = Criterions.<OnKilledCriterion>register(new OnKilledCriterion(new Identifier("player_killed_entity")));
        ENTITY_KILLED_PLAYER = Criterions.<OnKilledCriterion>register(new OnKilledCriterion(new Identifier("entity_killed_player")));
        ENTER_BLOCK = Criterions.<EnterBlockCriterion>register(new EnterBlockCriterion());
        INVENTORY_CHANGED = Criterions.<InventoryChangedCriterion>register(new InventoryChangedCriterion());
        RECIPE_UNLOCKED = Criterions.<RecipeUnlockedCriterion>register(new RecipeUnlockedCriterion());
        PLAYER_HURT_ENTITY = Criterions.<PlayerHurtEntityCriterion>register(new PlayerHurtEntityCriterion());
        ENTITY_HURT_PLAYER = Criterions.<EntityHurtPlayerCriterion>register(new EntityHurtPlayerCriterion());
        ENCHANTED_ITEM = Criterions.<EnchantedItemCriterion>register(new EnchantedItemCriterion());
        FILLED_BUCKET = Criterions.<FilledBucketCriterion>register(new FilledBucketCriterion());
        BREWED_POTION = Criterions.<BrewedPotionCriterion>register(new BrewedPotionCriterion());
        CONSTRUCT_BEACON = Criterions.<ConstructBeaconCriterion>register(new ConstructBeaconCriterion());
        USED_ENDER_EYE = Criterions.<UsedEnderEyeCriterion>register(new UsedEnderEyeCriterion());
        SUMMONED_ENTITY = Criterions.<SummonedEntityCriterion>register(new SummonedEntityCriterion());
        BRED_ANIMALS = Criterions.<BredAnimalsCriterion>register(new BredAnimalsCriterion());
        LOCATION = Criterions.<LocationArrivalCriterion>register(new LocationArrivalCriterion(new Identifier("location")));
        SLEPT_IN_BED = Criterions.<LocationArrivalCriterion>register(new LocationArrivalCriterion(new Identifier("slept_in_bed")));
        CURED_ZOMBIE_VILLAGER = Criterions.<CuredZombieVillagerCriterion>register(new CuredZombieVillagerCriterion());
        VILLAGER_TRADE = Criterions.<VillagerTradeCriterion>register(new VillagerTradeCriterion());
        ITEM_DURABILITY_CHANGED = Criterions.<ItemDurabilityChangedCriterion>register(new ItemDurabilityChangedCriterion());
        LEVITATION = Criterions.<LevitationCriterion>register(new LevitationCriterion());
        CHANGED_DIMENSION = Criterions.<ChangedDimensionCriterion>register(new ChangedDimensionCriterion());
        TICK = Criterions.<TickCriterion>register(new TickCriterion());
        TAME_ANIMAL = Criterions.<TameAnimalCriterion>register(new TameAnimalCriterion());
        PLACED_BLOCK = Criterions.<PlacedBlockCriterion>register(new PlacedBlockCriterion());
        CONSUME_ITEM = Criterions.<ConsumeItemCriterion>register(new ConsumeItemCriterion());
        EFFECTS_CHANGED = Criterions.<EffectsChangedCriterion>register(new EffectsChangedCriterion());
        USED_TOTEM = Criterions.<UsedTotemCriterion>register(new UsedTotemCriterion());
        NETHER_TRAVEL = Criterions.<NetherTravelCriterion>register(new NetherTravelCriterion());
        FISHING_ROD_HOOKED = Criterions.<FishingRodHookedCriterion>register(new FishingRodHookedCriterion());
        CHANNELED_LIGHTNING = Criterions.<ChanneledLightningCriterion>register(new ChanneledLightningCriterion());
        SHOT_CROSSBOW = Criterions.<ShotCrossbowCriterion>register(new ShotCrossbowCriterion());
        KILLED_BY_CROSSBOW = Criterions.<KilledByCrossbowCriterion>register(new KilledByCrossbowCriterion());
        HERO_OF_THE_VILLAGE = Criterions.<LocationArrivalCriterion>register(new LocationArrivalCriterion(new Identifier("hero_of_the_village")));
        VOLUNTARY_EXILE = Criterions.<LocationArrivalCriterion>register(new LocationArrivalCriterion(new Identifier("voluntary_exile")));
    }
}

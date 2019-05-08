package net.minecraft.data.server;

import net.minecraft.world.biome.Biomes;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.util.registry.Registry;
import net.minecraft.predicate.entity.EntityEquipmentPredicate;
import net.minecraft.tag.EntityTags;
import net.minecraft.entity.raid.Raid;
import net.minecraft.advancement.criterion.KilledByCrossbowCriterion;
import net.minecraft.advancement.criterion.ShotCrossbowCriterion;
import net.minecraft.advancement.criterion.UsedTotemCriterion;
import net.minecraft.predicate.entity.DistancePredicate;
import net.minecraft.util.NumberRange;
import net.minecraft.advancement.criterion.SummonedEntityCriterion;
import net.minecraft.advancement.criterion.ChanneledLightningCriterion;
import net.minecraft.advancement.criterion.PlayerHurtEntityCriterion;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.DamageSourcePredicate;
import net.minecraft.predicate.DamagePredicate;
import net.minecraft.advancement.criterion.VillagerTradeCriterion;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.LocationArrivalCriterion;
import net.minecraft.block.Blocks;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.advancement.criterion.OnKilledCriterion;
import net.minecraft.advancement.CriteriaMerger;
import net.minecraft.text.TextComponent;
import net.minecraft.item.ItemProvider;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.util.Identifier;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.item.Items;
import net.minecraft.entity.EntityType;
import net.minecraft.world.biome.Biome;
import net.minecraft.advancement.Advancement;
import java.util.function.Consumer;

public class AdventureTabAdvancementGenerator implements Consumer<Consumer<Advancement>>
{
    private static final Biome[] BIOMES;
    private static final EntityType<?>[] MONSTERS;
    
    public void a(final Consumer<Advancement> consumer) {
        final Advancement advancement2 = Advancement.Task.create().display(Items.nM, new TranslatableTextComponent("advancements.adventure.root.title", new Object[0]), new TranslatableTextComponent("advancements.adventure.root.description", new Object[0]), new Identifier("textures/gui/advancements/backgrounds/adventure.png"), AdvancementFrame.TASK, false, false, false).criteriaMerger(CriteriaMerger.OR).criterion("killed_something", OnKilledCriterion.Conditions.createPlayerKilledEntity()).criterion("killed_by_something", OnKilledCriterion.Conditions.createEntityKilledPlayer()).build(consumer, "adventure/root");
        final Advancement advancement3 = Advancement.Task.create().parent(advancement2).display(Blocks.aK, new TranslatableTextComponent("advancements.adventure.sleep_in_bed.title", new Object[0]), new TranslatableTextComponent("advancements.adventure.sleep_in_bed.description", new Object[0]), null, AdvancementFrame.TASK, true, true, false).criterion("slept_in_bed", LocationArrivalCriterion.Conditions.createSleptInBed()).build(consumer, "adventure/sleep_in_bed");
        final Advancement advancement4 = this.b(Advancement.Task.create()).parent(advancement3).display(Items.kg, new TranslatableTextComponent("advancements.adventure.adventuring_time.title", new Object[0]), new TranslatableTextComponent("advancements.adventure.adventuring_time.description", new Object[0]), null, AdvancementFrame.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(500)).build(consumer, "adventure/adventuring_time");
        final Advancement advancement5 = Advancement.Task.create().parent(advancement2).display(Items.nF, new TranslatableTextComponent("advancements.adventure.trade.title", new Object[0]), new TranslatableTextComponent("advancements.adventure.trade.description", new Object[0]), null, AdvancementFrame.TASK, true, true, false).criterion("traded", VillagerTradeCriterion.Conditions.any()).build(consumer, "adventure/trade");
        final Advancement advancement6 = this.a(Advancement.Task.create()).parent(advancement2).display(Items.jm, new TranslatableTextComponent("advancements.adventure.kill_a_mob.title", new Object[0]), new TranslatableTextComponent("advancements.adventure.kill_a_mob.description", new Object[0]), null, AdvancementFrame.TASK, true, true, false).criteriaMerger(CriteriaMerger.OR).build(consumer, "adventure/kill_a_mob");
        final Advancement advancement7 = this.a(Advancement.Task.create()).parent(advancement6).display(Items.jv, new TranslatableTextComponent("advancements.adventure.kill_all_mobs.title", new Object[0]), new TranslatableTextComponent("advancements.adventure.kill_all_mobs.description", new Object[0]), null, AdvancementFrame.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(100)).build(consumer, "adventure/kill_all_mobs");
        final Advancement advancement8 = Advancement.Task.create().parent(advancement6).display(Items.jf, new TranslatableTextComponent("advancements.adventure.shoot_arrow.title", new Object[0]), new TranslatableTextComponent("advancements.adventure.shoot_arrow.description", new Object[0]), null, AdvancementFrame.TASK, true, true, false).criterion("shot_arrow", PlayerHurtEntityCriterion.Conditions.create(DamagePredicate.Builder.create().type(DamageSourcePredicate.Builder.create().projectile(true).directEntity(EntityPredicate.Builder.create().type(EntityType.ARROW))))).build(consumer, "adventure/shoot_arrow");
        final Advancement advancement9 = Advancement.Task.create().parent(advancement6).display(Items.pu, new TranslatableTextComponent("advancements.adventure.throw_trident.title", new Object[0]), new TranslatableTextComponent("advancements.adventure.throw_trident.description", new Object[0]), null, AdvancementFrame.TASK, true, true, false).criterion("shot_trident", PlayerHurtEntityCriterion.Conditions.create(DamagePredicate.Builder.create().type(DamageSourcePredicate.Builder.create().projectile(true).directEntity(EntityPredicate.Builder.create().type(EntityType.TRIDENT))))).build(consumer, "adventure/throw_trident");
        final Advancement advancement10 = Advancement.Task.create().parent(advancement9).display(Items.pu, new TranslatableTextComponent("advancements.adventure.very_very_frightening.title", new Object[0]), new TranslatableTextComponent("advancements.adventure.very_very_frightening.description", new Object[0]), null, AdvancementFrame.TASK, true, true, false).criterion("struck_villager", ChanneledLightningCriterion.Conditions.create(EntityPredicate.Builder.create().type(EntityType.VILLAGER).build())).build(consumer, "adventure/very_very_frightening");
        final Advancement advancement11 = Advancement.Task.create().parent(advancement5).display(Blocks.cN, new TranslatableTextComponent("advancements.adventure.summon_iron_golem.title", new Object[0]), new TranslatableTextComponent("advancements.adventure.summon_iron_golem.description", new Object[0]), null, AdvancementFrame.GOAL, true, true, false).criterion("summoned_golem", SummonedEntityCriterion.Conditions.create(EntityPredicate.Builder.create().type(EntityType.IRON_GOLEM))).build(consumer, "adventure/summon_iron_golem");
        final Advancement advancement12 = Advancement.Task.create().parent(advancement8).display(Items.jg, new TranslatableTextComponent("advancements.adventure.sniper_duel.title", new Object[0]), new TranslatableTextComponent("advancements.adventure.sniper_duel.description", new Object[0]), null, AdvancementFrame.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(50)).criterion("killed_skeleton", OnKilledCriterion.Conditions.createPlayerKilledEntity(EntityPredicate.Builder.create().type(EntityType.SKELETON).distance(DistancePredicate.horizontal(NumberRange.FloatRange.atLeast(50.0f))), DamageSourcePredicate.Builder.create().projectile(true))).build(consumer, "adventure/sniper_duel");
        final Advancement advancement13 = Advancement.Task.create().parent(advancement6).display(Items.pd, new TranslatableTextComponent("advancements.adventure.totem_of_undying.title", new Object[0]), new TranslatableTextComponent("advancements.adventure.totem_of_undying.description", new Object[0]), null, AdvancementFrame.GOAL, true, true, false).criterion("used_totem", UsedTotemCriterion.Conditions.create(Items.pd)).build(consumer, "adventure/totem_of_undying");
        final Advancement advancement14 = Advancement.Task.create().parent(advancement2).display(Items.py, new TranslatableTextComponent("advancements.adventure.ol_betsy.title", new Object[0]), new TranslatableTextComponent("advancements.adventure.ol_betsy.description", new Object[0]), null, AdvancementFrame.TASK, true, true, false).criterion("shot_crossbow", ShotCrossbowCriterion.Conditions.create(Items.py)).build(consumer, "adventure/ol_betsy");
        final Advancement advancement15 = Advancement.Task.create().parent(advancement14).display(Items.py, new TranslatableTextComponent("advancements.adventure.whos_the_pillager_now.title", new Object[0]), new TranslatableTextComponent("advancements.adventure.whos_the_pillager_now.description", new Object[0]), null, AdvancementFrame.TASK, true, true, false).criterion("kill_pillager", KilledByCrossbowCriterion.Conditions.create(EntityPredicate.Builder.create().type(EntityType.PILLAGER))).build(consumer, "adventure/whos_the_pillager_now");
        final Advancement advancement16 = Advancement.Task.create().parent(advancement14).display(Items.py, new TranslatableTextComponent("advancements.adventure.two_birds_one_arrow.title", new Object[0]), new TranslatableTextComponent("advancements.adventure.two_birds_one_arrow.description", new Object[0]), null, AdvancementFrame.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(65)).criterion("two_birds", KilledByCrossbowCriterion.Conditions.create(EntityPredicate.Builder.create().type(EntityType.PHANTOM), EntityPredicate.Builder.create().type(EntityType.PHANTOM))).build(consumer, "adventure/two_birds_one_arrow");
        final Advancement advancement17 = Advancement.Task.create().parent(advancement14).display(Items.py, new TranslatableTextComponent("advancements.adventure.arbalistic.title", new Object[0]), new TranslatableTextComponent("advancements.adventure.arbalistic.description", new Object[0]), null, AdvancementFrame.CHALLENGE, true, true, true).rewards(AdvancementRewards.Builder.experience(85)).criterion("arbalistic", KilledByCrossbowCriterion.Conditions.create(NumberRange.IntRange.exactly(5))).build(consumer, "adventure/arbalistic");
        final Advancement advancement18 = Advancement.Task.create().parent(advancement2).display(Raid.OMINOUS_BANNER, new TranslatableTextComponent("advancements.adventure.voluntary_exile.title", new Object[0]), new TranslatableTextComponent("advancements.adventure.voluntary_exile.description", new Object[0]), null, AdvancementFrame.TASK, true, true, true).criterion("voluntary_exile", OnKilledCriterion.Conditions.createPlayerKilledEntity(EntityPredicate.Builder.create().type(EntityTags.b).equipment(EntityEquipmentPredicate.b))).build(consumer, "adventure/voluntary_exile");
        final Advancement advancement19 = Advancement.Task.create().parent(advancement18).display(Raid.OMINOUS_BANNER, new TranslatableTextComponent("advancements.adventure.hero_of_the_village.title", new Object[0]), new TranslatableTextComponent("advancements.adventure.hero_of_the_village.description", new Object[0]), null, AdvancementFrame.CHALLENGE, true, true, true).rewards(AdvancementRewards.Builder.experience(100)).criterion("hero_of_the_village", LocationArrivalCriterion.Conditions.createHeroOfTheVillage()).build(consumer, "adventure/hero_of_the_village");
    }
    
    private Advancement.Task a(final Advancement.Task task) {
        for (final EntityType<?> entityType5 : AdventureTabAdvancementGenerator.MONSTERS) {
            task.criterion(Registry.ENTITY_TYPE.getId(entityType5).toString(), OnKilledCriterion.Conditions.createPlayerKilledEntity(EntityPredicate.Builder.create().type(entityType5)));
        }
        return task;
    }
    
    private Advancement.Task b(final Advancement.Task task) {
        for (final Biome biome5 : AdventureTabAdvancementGenerator.BIOMES) {
            task.criterion(Registry.BIOME.getId(biome5).toString(), LocationArrivalCriterion.Conditions.create(LocationPredicate.biome(biome5)));
        }
        return task;
    }
    
    static {
        BIOMES = new Biome[] { Biomes.D, Biomes.i, Biomes.h, Biomes.d, Biomes.t, Biomes.I, Biomes.F, Biomes.M, Biomes.f, Biomes.A, Biomes.n, Biomes.u, Biomes.o, Biomes.N, Biomes.K, Biomes.c, Biomes.m, Biomes.H, Biomes.B, Biomes.x, Biomes.y, Biomes.q, Biomes.e, Biomes.s, Biomes.w, Biomes.r, Biomes.L, Biomes.G, Biomes.O, Biomes.E, Biomes.g, Biomes.C, Biomes.p, Biomes.J, Biomes.T, Biomes.U, Biomes.V, Biomes.X, Biomes.Y, Biomes.Z, Biomes.aw, Biomes.ax };
        MONSTERS = new EntityType[] { EntityType.CAVE_SPIDER, EntityType.SPIDER, EntityType.ZOMBIE_PIGMAN, EntityType.ENDERMAN, EntityType.BLAZE, EntityType.CREEPER, EntityType.EVOKER, EntityType.GHAST, EntityType.GUARDIAN, EntityType.HUSK, EntityType.MAGMA_CUBE, EntityType.SHULKER, EntityType.SILVERFISH, EntityType.SKELETON, EntityType.SLIME, EntityType.STRAY, EntityType.VINDICATOR, EntityType.WITCH, EntityType.WITHER_SKELETON, EntityType.ZOMBIE, EntityType.ZOMBIE_VILLAGER, EntityType.PHANTOM, EntityType.DROWNED, EntityType.PILLAGER, EntityType.RAVAGER };
    }
}

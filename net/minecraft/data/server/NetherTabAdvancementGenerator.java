package net.minecraft.data.server;

import net.minecraft.advancement.criterion.EffectsChangedCriterion;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.predicate.entity.EntityEffectPredicate;
import net.minecraft.advancement.criterion.BrewedPotionCriterion;
import net.minecraft.advancement.criterion.ConstructBeaconCriterion;
import net.minecraft.advancement.criterion.SummonedEntityCriterion;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.advancement.criterion.NetherTravelCriterion;
import net.minecraft.predicate.entity.DistancePredicate;
import net.minecraft.util.NumberRange;
import net.minecraft.advancement.criterion.LocationArrivalCriterion;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.advancement.criterion.OnKilledCriterion;
import net.minecraft.predicate.entity.DamageSourcePredicate;
import net.minecraft.entity.EntityType;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.item.Items;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.advancement.criterion.ChangedDimensionCriterion;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.text.TextComponent;
import net.minecraft.item.ItemProvider;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.util.Identifier;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.block.Blocks;
import net.minecraft.advancement.Advancement;
import java.util.function.Consumer;

public class NetherTabAdvancementGenerator implements Consumer<Consumer<Advancement>>
{
    public void a(final Consumer<Advancement> consumer) {
        final Advancement advancement2 = Advancement.Task.create().display(Blocks.iD, new TranslatableTextComponent("advancements.nether.root.title", new Object[0]), new TranslatableTextComponent("advancements.nether.root.description", new Object[0]), new Identifier("textures/gui/advancements/backgrounds/nether.png"), AdvancementFrame.TASK, false, false, false).criterion("entered_nether", ChangedDimensionCriterion.Conditions.to(DimensionType.b)).build(consumer, "nether/root");
        final Advancement advancement3 = Advancement.Task.create().parent(advancement2).display(Items.nC, new TranslatableTextComponent("advancements.nether.return_to_sender.title", new Object[0]), new TranslatableTextComponent("advancements.nether.return_to_sender.description", new Object[0]), null, AdvancementFrame.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(50)).criterion("killed_ghast", OnKilledCriterion.Conditions.createPlayerKilledEntity(EntityPredicate.Builder.create().type(EntityType.GHAST), DamageSourcePredicate.Builder.create().projectile(true).directEntity(EntityPredicate.Builder.create().type(EntityType.FIREBALL)))).build(consumer, "nether/return_to_sender");
        final Advancement advancement4 = Advancement.Task.create().parent(advancement2).display(Blocks.dN, new TranslatableTextComponent("advancements.nether.find_fortress.title", new Object[0]), new TranslatableTextComponent("advancements.nether.find_fortress.description", new Object[0]), null, AdvancementFrame.TASK, true, true, false).criterion("fortress", LocationArrivalCriterion.Conditions.create(LocationPredicate.feature(Feature.NETHER_BRIDGE))).build(consumer, "nether/find_fortress");
        final Advancement advancement5 = Advancement.Task.create().parent(advancement2).display(Items.nM, new TranslatableTextComponent("advancements.nether.fast_travel.title", new Object[0]), new TranslatableTextComponent("advancements.nether.fast_travel.description", new Object[0]), null, AdvancementFrame.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(100)).criterion("travelled", NetherTravelCriterion.Conditions.distance(DistancePredicate.horizontal(NumberRange.FloatRange.atLeast(7000.0f)))).build(consumer, "nether/fast_travel");
        final Advancement advancement6 = Advancement.Task.create().parent(advancement3).display(Items.mi, new TranslatableTextComponent("advancements.nether.uneasy_alliance.title", new Object[0]), new TranslatableTextComponent("advancements.nether.uneasy_alliance.description", new Object[0]), null, AdvancementFrame.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(100)).criterion("killed_ghast", OnKilledCriterion.Conditions.createPlayerKilledEntity(EntityPredicate.Builder.create().type(EntityType.GHAST).location(LocationPredicate.dimension(DimensionType.a)))).build(consumer, "nether/uneasy_alliance");
        final Advancement advancement7 = Advancement.Task.create().parent(advancement4).display(Blocks.eW, new TranslatableTextComponent("advancements.nether.get_wither_skull.title", new Object[0]), new TranslatableTextComponent("advancements.nether.get_wither_skull.description", new Object[0]), null, AdvancementFrame.TASK, true, true, false).criterion("wither_skull", InventoryChangedCriterion.Conditions.items(Blocks.eW)).build(consumer, "nether/get_wither_skull");
        final Advancement advancement8 = Advancement.Task.create().parent(advancement7).display(Items.nV, new TranslatableTextComponent("advancements.nether.summon_wither.title", new Object[0]), new TranslatableTextComponent("advancements.nether.summon_wither.description", new Object[0]), null, AdvancementFrame.TASK, true, true, false).criterion("summoned", SummonedEntityCriterion.Conditions.create(EntityPredicate.Builder.create().type(EntityType.WITHER))).build(consumer, "nether/summon_wither");
        final Advancement advancement9 = Advancement.Task.create().parent(advancement4).display(Items.mh, new TranslatableTextComponent("advancements.nether.obtain_blaze_rod.title", new Object[0]), new TranslatableTextComponent("advancements.nether.obtain_blaze_rod.description", new Object[0]), null, AdvancementFrame.TASK, true, true, false).criterion("blaze_rod", InventoryChangedCriterion.Conditions.items(Items.mh)).build(consumer, "nether/obtain_blaze_rod");
        final Advancement advancement10 = Advancement.Task.create().parent(advancement8).display(Blocks.ek, new TranslatableTextComponent("advancements.nether.create_beacon.title", new Object[0]), new TranslatableTextComponent("advancements.nether.create_beacon.description", new Object[0]), null, AdvancementFrame.TASK, true, true, false).criterion("beacon", ConstructBeaconCriterion.Conditions.level(NumberRange.IntRange.atLeast(1))).build(consumer, "nether/create_beacon");
        final Advancement advancement11 = Advancement.Task.create().parent(advancement10).display(Blocks.ek, new TranslatableTextComponent("advancements.nether.create_full_beacon.title", new Object[0]), new TranslatableTextComponent("advancements.nether.create_full_beacon.description", new Object[0]), null, AdvancementFrame.GOAL, true, true, false).criterion("beacon", ConstructBeaconCriterion.Conditions.level(NumberRange.IntRange.exactly(4))).build(consumer, "nether/create_full_beacon");
        final Advancement advancement12 = Advancement.Task.create().parent(advancement9).display(Items.ml, new TranslatableTextComponent("advancements.nether.brew_potion.title", new Object[0]), new TranslatableTextComponent("advancements.nether.brew_potion.description", new Object[0]), null, AdvancementFrame.TASK, true, true, false).criterion("potion", BrewedPotionCriterion.Conditions.any()).build(consumer, "nether/brew_potion");
        final Advancement advancement13 = Advancement.Task.create().parent(advancement12).display(Items.kG, new TranslatableTextComponent("advancements.nether.all_potions.title", new Object[0]), new TranslatableTextComponent("advancements.nether.all_potions.description", new Object[0]), null, AdvancementFrame.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(100)).criterion("all_effects", EffectsChangedCriterion.Conditions.create(EntityEffectPredicate.create().withEffect(StatusEffects.a).withEffect(StatusEffects.b).withEffect(StatusEffects.e).withEffect(StatusEffects.h).withEffect(StatusEffects.j).withEffect(StatusEffects.l).withEffect(StatusEffects.m).withEffect(StatusEffects.n).withEffect(StatusEffects.p).withEffect(StatusEffects.r).withEffect(StatusEffects.s).withEffect(StatusEffects.B).withEffect(StatusEffects.k))).build(consumer, "nether/all_potions");
        final Advancement advancement14 = Advancement.Task.create().parent(advancement13).display(Items.kx, new TranslatableTextComponent("advancements.nether.all_effects.title", new Object[0]), new TranslatableTextComponent("advancements.nether.all_effects.description", new Object[0]), null, AdvancementFrame.CHALLENGE, true, true, true).rewards(AdvancementRewards.Builder.experience(1000)).criterion("all_effects", EffectsChangedCriterion.Conditions.create(EntityEffectPredicate.create().withEffect(StatusEffects.a).withEffect(StatusEffects.b).withEffect(StatusEffects.e).withEffect(StatusEffects.h).withEffect(StatusEffects.j).withEffect(StatusEffects.l).withEffect(StatusEffects.m).withEffect(StatusEffects.n).withEffect(StatusEffects.p).withEffect(StatusEffects.r).withEffect(StatusEffects.s).withEffect(StatusEffects.t).withEffect(StatusEffects.c).withEffect(StatusEffects.d).withEffect(StatusEffects.y).withEffect(StatusEffects.x).withEffect(StatusEffects.v).withEffect(StatusEffects.q).withEffect(StatusEffects.i).withEffect(StatusEffects.k).withEffect(StatusEffects.B).withEffect(StatusEffects.C).withEffect(StatusEffects.D).withEffect(StatusEffects.w).withEffect(StatusEffects.o).withEffect(StatusEffects.E).withEffect(StatusEffects.F))).build(consumer, "nether/all_effects");
    }
}

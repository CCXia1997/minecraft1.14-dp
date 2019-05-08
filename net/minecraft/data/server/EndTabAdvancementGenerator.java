package net.minecraft.data.server;

import net.minecraft.advancement.criterion.LevitationCriterion;
import net.minecraft.predicate.entity.DistancePredicate;
import net.minecraft.util.NumberRange;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.advancement.criterion.LocationArrivalCriterion;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.advancement.criterion.SummonedEntityCriterion;
import net.minecraft.advancement.criterion.EnterBlockCriterion;
import net.minecraft.item.Items;
import net.minecraft.advancement.criterion.OnKilledCriterion;
import net.minecraft.entity.EntityType;
import net.minecraft.predicate.entity.EntityPredicate;
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

public class EndTabAdvancementGenerator implements Consumer<Consumer<Advancement>>
{
    public void a(final Consumer<Advancement> consumer) {
        final Advancement advancement2 = Advancement.Task.create().display(Blocks.dW, new TranslatableTextComponent("advancements.end.root.title", new Object[0]), new TranslatableTextComponent("advancements.end.root.description", new Object[0]), new Identifier("textures/gui/advancements/backgrounds/end.png"), AdvancementFrame.TASK, false, false, false).criterion("entered_end", ChangedDimensionCriterion.Conditions.to(DimensionType.c)).build(consumer, "end/root");
        final Advancement advancement3 = Advancement.Task.create().parent(advancement2).display(Blocks.fe, new TranslatableTextComponent("advancements.end.kill_dragon.title", new Object[0]), new TranslatableTextComponent("advancements.end.kill_dragon.description", new Object[0]), null, AdvancementFrame.TASK, true, true, false).criterion("killed_dragon", OnKilledCriterion.Conditions.createPlayerKilledEntity(EntityPredicate.Builder.create().type(EntityType.ENDER_DRAGON))).build(consumer, "end/kill_dragon");
        final Advancement advancement4 = Advancement.Task.create().parent(advancement3).display(Items.mg, new TranslatableTextComponent("advancements.end.enter_end_gateway.title", new Object[0]), new TranslatableTextComponent("advancements.end.enter_end_gateway.description", new Object[0]), null, AdvancementFrame.TASK, true, true, false).criterion("entered_end_gateway", EnterBlockCriterion.Conditions.block(Blocks.ix)).build(consumer, "end/enter_end_gateway");
        final Advancement advancement5 = Advancement.Task.create().parent(advancement3).display(Items.oL, new TranslatableTextComponent("advancements.end.respawn_dragon.title", new Object[0]), new TranslatableTextComponent("advancements.end.respawn_dragon.description", new Object[0]), null, AdvancementFrame.GOAL, true, true, false).criterion("summoned_dragon", SummonedEntityCriterion.Conditions.create(EntityPredicate.Builder.create().type(EntityType.ENDER_DRAGON))).build(consumer, "end/respawn_dragon");
        final Advancement advancement6 = Advancement.Task.create().parent(advancement4).display(Blocks.ir, new TranslatableTextComponent("advancements.end.find_end_city.title", new Object[0]), new TranslatableTextComponent("advancements.end.find_end_city.description", new Object[0]), null, AdvancementFrame.TASK, true, true, false).criterion("in_city", LocationArrivalCriterion.Conditions.create(LocationPredicate.feature(Feature.END_CITY))).build(consumer, "end/find_end_city");
        final Advancement advancement7 = Advancement.Task.create().parent(advancement3).display(Items.oR, new TranslatableTextComponent("advancements.end.dragon_breath.title", new Object[0]), new TranslatableTextComponent("advancements.end.dragon_breath.description", new Object[0]), null, AdvancementFrame.GOAL, true, true, false).criterion("dragon_breath", InventoryChangedCriterion.Conditions.items(Items.oR)).build(consumer, "end/dragon_breath");
        final Advancement advancement8 = Advancement.Task.create().parent(advancement6).display(Items.pe, new TranslatableTextComponent("advancements.end.levitate.title", new Object[0]), new TranslatableTextComponent("advancements.end.levitate.description", new Object[0]), null, AdvancementFrame.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(50)).criterion("levitated", LevitationCriterion.Conditions.create(DistancePredicate.y(NumberRange.FloatRange.atLeast(50.0f)))).build(consumer, "end/levitate");
        final Advancement advancement9 = Advancement.Task.create().parent(advancement6).display(Items.oX, new TranslatableTextComponent("advancements.end.elytra.title", new Object[0]), new TranslatableTextComponent("advancements.end.elytra.description", new Object[0]), null, AdvancementFrame.GOAL, true, true, false).criterion("elytra", InventoryChangedCriterion.Conditions.items(Items.oX)).build(consumer, "end/elytra");
        final Advancement advancement10 = Advancement.Task.create().parent(advancement3).display(Blocks.dX, new TranslatableTextComponent("advancements.end.dragon_egg.title", new Object[0]), new TranslatableTextComponent("advancements.end.dragon_egg.description", new Object[0]), null, AdvancementFrame.GOAL, true, true, false).criterion("dragon_egg", InventoryChangedCriterion.Conditions.items(Blocks.dX)).build(consumer, "end/dragon_egg");
    }
}

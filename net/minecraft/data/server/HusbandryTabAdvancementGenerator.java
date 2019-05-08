package net.minecraft.data.server;

import net.minecraft.entity.passive.CatEntity;
import net.minecraft.advancement.criterion.FishingRodHookedCriterion;
import net.minecraft.advancement.criterion.FilledBucketCriterion;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.util.registry.Registry;
import net.minecraft.advancement.criterion.TameAnimalCriterion;
import net.minecraft.advancement.criterion.ItemDurabilityChangedCriterion;
import net.minecraft.util.NumberRange;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.BredAnimalsCriterion;
import net.minecraft.advancement.criterion.PlacedBlockCriterion;
import net.minecraft.advancement.CriteriaMerger;
import net.minecraft.item.Items;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.advancement.criterion.ConsumeItemCriterion;
import net.minecraft.text.TextComponent;
import net.minecraft.item.ItemProvider;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.util.Identifier;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.entity.EntityType;
import net.minecraft.advancement.Advancement;
import java.util.function.Consumer;

public class HusbandryTabAdvancementGenerator implements Consumer<Consumer<Advancement>>
{
    private static final EntityType<?>[] a;
    private static final Item[] b;
    private static final Item[] c;
    private static final Item[] d;
    
    public void a(final Consumer<Advancement> consumer) {
        final Advancement advancement2 = Advancement.Task.create().display(Blocks.gs, new TranslatableTextComponent("advancements.husbandry.root.title", new Object[0]), new TranslatableTextComponent("advancements.husbandry.root.description", new Object[0]), new Identifier("textures/gui/advancements/backgrounds/husbandry.png"), AdvancementFrame.TASK, false, false, false).criterion("consumed_item", ConsumeItemCriterion.Conditions.any()).build(consumer, "husbandry/root");
        final Advancement advancement3 = Advancement.Task.create().parent(advancement2).display(Items.jP, new TranslatableTextComponent("advancements.husbandry.plant_seed.title", new Object[0]), new TranslatableTextComponent("advancements.husbandry.plant_seed.description", new Object[0]), null, AdvancementFrame.TASK, true, true, false).criteriaMerger(CriteriaMerger.OR).criterion("wheat", PlacedBlockCriterion.Conditions.block(Blocks.bU)).criterion("pumpkin_stem", PlacedBlockCriterion.Conditions.block(Blocks.dF)).criterion("melon_stem", PlacedBlockCriterion.Conditions.block(Blocks.dG)).criterion("beetroots", PlacedBlockCriterion.Conditions.block(Blocks.iv)).criterion("nether_wart", PlacedBlockCriterion.Conditions.block(Blocks.dQ)).build(consumer, "husbandry/plant_seed");
        final Advancement advancement4 = Advancement.Task.create().parent(advancement2).display(Items.jP, new TranslatableTextComponent("advancements.husbandry.breed_an_animal.title", new Object[0]), new TranslatableTextComponent("advancements.husbandry.breed_an_animal.description", new Object[0]), null, AdvancementFrame.TASK, true, true, false).criteriaMerger(CriteriaMerger.OR).criterion("bred", BredAnimalsCriterion.Conditions.any()).build(consumer, "husbandry/breed_an_animal");
        final Advancement advancement5 = this.a(Advancement.Task.create()).parent(advancement3).display(Items.je, new TranslatableTextComponent("advancements.husbandry.balanced_diet.title", new Object[0]), new TranslatableTextComponent("advancements.husbandry.balanced_diet.description", new Object[0]), null, AdvancementFrame.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(100)).build(consumer, "husbandry/balanced_diet");
        final Advancement advancement6 = Advancement.Task.create().parent(advancement3).display(Items.jM, new TranslatableTextComponent("advancements.husbandry.break_diamond_hoe.title", new Object[0]), new TranslatableTextComponent("advancements.husbandry.break_diamond_hoe.description", new Object[0]), null, AdvancementFrame.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(100)).criterion("broke_hoe", ItemDurabilityChangedCriterion.Conditions.create(ItemPredicate.Builder.create().item(Items.jM).build(), NumberRange.IntRange.exactly(0))).build(consumer, "husbandry/break_diamond_hoe");
        final Advancement advancement7 = Advancement.Task.create().parent(advancement2).display(Items.oq, new TranslatableTextComponent("advancements.husbandry.tame_an_animal.title", new Object[0]), new TranslatableTextComponent("advancements.husbandry.tame_an_animal.description", new Object[0]), null, AdvancementFrame.TASK, true, true, false).criterion("tamed_animal", TameAnimalCriterion.Conditions.any()).build(consumer, "husbandry/tame_an_animal");
        final Advancement advancement8 = this.b(Advancement.Task.create()).parent(advancement4).display(Items.nN, new TranslatableTextComponent("advancements.husbandry.breed_all_animals.title", new Object[0]), new TranslatableTextComponent("advancements.husbandry.breed_all_animals.description", new Object[0]), null, AdvancementFrame.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(100)).build(consumer, "husbandry/bred_all_animals");
        final Advancement advancement9 = this.d(Advancement.Task.create()).parent(advancement2).criteriaMerger(CriteriaMerger.OR).display(Items.kY, new TranslatableTextComponent("advancements.husbandry.fishy_business.title", new Object[0]), new TranslatableTextComponent("advancements.husbandry.fishy_business.description", new Object[0]), null, AdvancementFrame.TASK, true, true, false).build(consumer, "husbandry/fishy_business");
        final Advancement advancement10 = this.c(Advancement.Task.create()).parent(advancement9).criteriaMerger(CriteriaMerger.OR).display(Items.kH, new TranslatableTextComponent("advancements.husbandry.tactical_fishing.title", new Object[0]), new TranslatableTextComponent("advancements.husbandry.tactical_fishing.description", new Object[0]), null, AdvancementFrame.TASK, true, true, false).build(consumer, "husbandry/tactical_fishing");
        final Advancement advancement11 = this.e(Advancement.Task.create()).parent(advancement7).display(Items.lb, new TranslatableTextComponent("advancements.husbandry.complete_catalogue.title", new Object[0]), new TranslatableTextComponent("advancements.husbandry.complete_catalogue.description", new Object[0]), null, AdvancementFrame.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(50)).build(consumer, "husbandry/complete_catalogue");
    }
    
    private Advancement.Task a(final Advancement.Task task) {
        for (final Item item5 : HusbandryTabAdvancementGenerator.d) {
            task.criterion(Registry.ITEM.getId(item5).getPath(), ConsumeItemCriterion.Conditions.item(item5));
        }
        return task;
    }
    
    private Advancement.Task b(final Advancement.Task task) {
        for (final EntityType<?> entityType5 : HusbandryTabAdvancementGenerator.a) {
            task.criterion(EntityType.getId(entityType5).toString(), BredAnimalsCriterion.Conditions.create(EntityPredicate.Builder.create().type(entityType5)));
        }
        return task;
    }
    
    private Advancement.Task c(final Advancement.Task task) {
        for (final Item item5 : HusbandryTabAdvancementGenerator.c) {
            task.criterion(Registry.ITEM.getId(item5).getPath(), FilledBucketCriterion.Conditions.create(ItemPredicate.Builder.create().item(item5).build()));
        }
        return task;
    }
    
    private Advancement.Task d(final Advancement.Task task) {
        for (final Item item5 : HusbandryTabAdvancementGenerator.b) {
            task.criterion(Registry.ITEM.getId(item5).getPath(), FishingRodHookedCriterion.Conditions.create(ItemPredicate.ANY, EntityPredicate.ANY, ItemPredicate.Builder.create().item(item5).build()));
        }
        return task;
    }
    
    private Advancement.Task e(final Advancement.Task task) {
        CatEntity.TEXTURES.forEach((integer, identifier) -> task.criterion(identifier.getPath(), TameAnimalCriterion.Conditions.create(EntityPredicate.Builder.create().type(identifier).build())));
        return task;
    }
    
    static {
        a = new EntityType[] { EntityType.HORSE, EntityType.SHEEP, EntityType.COW, EntityType.MOOSHROOM, EntityType.PIG, EntityType.CHICKEN, EntityType.WOLF, EntityType.OCELOT, EntityType.RABBIT, EntityType.LLAMA, EntityType.TURTLE, EntityType.CAT, EntityType.PANDA, EntityType.B };
        b = new Item[] { Items.lb, Items.ld, Items.le, Items.lc };
        c = new Item[] { Items.kJ, Items.kK, Items.kH, Items.kI };
        d = new Item[] { Items.je, Items.jB, Items.jQ, Items.km, Items.kn, Items.kp, Items.kq, Items.lb, Items.lc, Items.ld, Items.le, Items.lf, Items.lg, Items.lU, Items.lX, Items.mb, Items.mc, Items.md, Items.me, Items.mf, Items.mn, Items.nI, Items.nJ, Items.nK, Items.nL, Items.nN, Items.nW, Items.og, Items.oh, Items.oi, Items.ot, Items.ou, Items.oM, Items.oO, Items.oQ, Items.lY, Items.pz, Items.pR };
    }
}

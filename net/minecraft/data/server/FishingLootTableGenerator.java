package net.minecraft.data.server;

import net.minecraft.world.loot.condition.LocationCheckLootCondition;
import net.minecraft.world.biome.Biomes;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.loot.condition.LootCondition;
import net.minecraft.world.loot.LootSupplier;
import net.minecraft.util.Identifier;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class FishingLootTableGenerator implements Consumer<BiConsumer<Identifier, LootSupplier.Builder>>
{
    public static final LootCondition.Builder a;
    public static final LootCondition.Builder b;
    public static final LootCondition.Builder c;
    public static final LootCondition.Builder d;
    public static final LootCondition.Builder e;
    public static final LootCondition.Builder f;
    public static final LootCondition.Builder g;
    
    public void a(final BiConsumer<Identifier, LootSupplier.Builder> biConsumer) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: getstatic       net/minecraft/world/loot/LootTables.GAMEPLAY_FISHING:Lnet/minecraft/util/Identifier;
        //     4: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //     7: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //    10: iconst_1       
        //    11: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //    14: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //    17: getstatic       net/minecraft/world/loot/LootTables.GAMEPLAY_FISHING_JUNK:Lnet/minecraft/util/Identifier;
        //    20: invokestatic    net/minecraft/world/loot/entry/LootTableEntry.builder:(Lnet/minecraft/util/Identifier;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //    23: bipush          10
        //    25: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.setWeight:(I)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //    28: bipush          -2
        //    30: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.setQuality:(I)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //    33: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //    36: getstatic       net/minecraft/world/loot/LootTables.GAMEPLAY_FISHING_TREASURE:Lnet/minecraft/util/Identifier;
        //    39: invokestatic    net/minecraft/world/loot/entry/LootTableEntry.builder:(Lnet/minecraft/util/Identifier;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //    42: iconst_5       
        //    43: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.setWeight:(I)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //    46: iconst_2       
        //    47: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.setQuality:(I)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //    50: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //    53: getstatic       net/minecraft/world/loot/LootTables.GAMEPLAY_FISHING_FISH:Lnet/minecraft/util/Identifier;
        //    56: invokestatic    net/minecraft/world/loot/entry/LootTableEntry.builder:(Lnet/minecraft/util/Identifier;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //    59: bipush          85
        //    61: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.setWeight:(I)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //    64: iconst_m1      
        //    65: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.setQuality:(I)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //    68: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //    71: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //    74: invokeinterface java/util/function/BiConsumer.accept:(Ljava/lang/Object;Ljava/lang/Object;)V
        //    79: aload_1         /* biConsumer */
        //    80: getstatic       net/minecraft/world/loot/LootTables.GAMEPLAY_FISHING_FISH:Lnet/minecraft/util/Identifier;
        //    83: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //    86: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //    89: getstatic       net/minecraft/item/Items.lb:Lnet/minecraft/item/Item;
        //    92: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //    95: bipush          60
        //    97: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.setWeight:(I)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   100: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   103: getstatic       net/minecraft/item/Items.lc:Lnet/minecraft/item/Item;
        //   106: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   109: bipush          25
        //   111: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.setWeight:(I)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   114: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   117: getstatic       net/minecraft/item/Items.ld:Lnet/minecraft/item/Item;
        //   120: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   123: iconst_2       
        //   124: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.setWeight:(I)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   127: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   130: getstatic       net/minecraft/item/Items.le:Lnet/minecraft/item/Item;
        //   133: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   136: bipush          13
        //   138: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.setWeight:(I)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   141: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   144: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //   147: invokeinterface java/util/function/BiConsumer.accept:(Ljava/lang/Object;Ljava/lang/Object;)V
        //   152: aload_1         /* biConsumer */
        //   153: getstatic       net/minecraft/world/loot/LootTables.GAMEPLAY_FISHING_JUNK:Lnet/minecraft/util/Identifier;
        //   156: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //   159: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //   162: getstatic       net/minecraft/item/Items.jU:Lnet/minecraft/item/Item;
        //   165: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   168: bipush          10
        //   170: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.setWeight:(I)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   173: fconst_0       
        //   174: ldc             0.9
        //   176: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //   179: invokestatic    net/minecraft/world/loot/function/SetDamageLootFunction.builder:(Lnet/minecraft/world/loot/UniformLootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //   182: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   185: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   188: getstatic       net/minecraft/item/Items.kF:Lnet/minecraft/item/Item;
        //   191: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   194: bipush          10
        //   196: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.setWeight:(I)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   199: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   202: getstatic       net/minecraft/item/Items.lB:Lnet/minecraft/item/Item;
        //   205: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   208: bipush          10
        //   210: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.setWeight:(I)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   213: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   216: getstatic       net/minecraft/item/Items.ml:Lnet/minecraft/item/Item;
        //   219: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   222: bipush          10
        //   224: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.setWeight:(I)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   227: new             Lnet/minecraft/nbt/CompoundTag;
        //   230: dup            
        //   231: invokespecial   net/minecraft/nbt/CompoundTag.<init>:()V
        //   234: invokedynamic   BootstrapMethod #0, accept:()Ljava/util/function/Consumer;
        //   239: invokestatic    net/minecraft/util/SystemUtil.consume:(Ljava/lang/Object;Ljava/util/function/Consumer;)Ljava/lang/Object;
        //   242: checkcast       Lnet/minecraft/nbt/CompoundTag;
        //   245: invokestatic    net/minecraft/world/loot/function/SetNbtLootFunction.builder:(Lnet/minecraft/nbt/CompoundTag;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //   248: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   251: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   254: getstatic       net/minecraft/item/Items.jG:Lnet/minecraft/item/Item;
        //   257: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   260: iconst_5       
        //   261: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.setWeight:(I)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   264: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   267: getstatic       net/minecraft/item/Items.kY:Lnet/minecraft/item/Item;
        //   270: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   273: iconst_2       
        //   274: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.setWeight:(I)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   277: fconst_0       
        //   278: ldc             0.9
        //   280: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //   283: invokestatic    net/minecraft/world/loot/function/SetDamageLootFunction.builder:(Lnet/minecraft/world/loot/UniformLootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //   286: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   289: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   292: getstatic       net/minecraft/item/Items.jA:Lnet/minecraft/item/Item;
        //   295: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   298: bipush          10
        //   300: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.setWeight:(I)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   303: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   306: getstatic       net/minecraft/item/Items.jz:Lnet/minecraft/item/Item;
        //   309: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   312: iconst_5       
        //   313: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.setWeight:(I)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   316: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   319: getstatic       net/minecraft/item/Items.lh:Lnet/minecraft/item/Item;
        //   322: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   325: iconst_1       
        //   326: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.setWeight:(I)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   329: bipush          10
        //   331: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //   334: invokestatic    net/minecraft/world/loot/function/SetCountLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //   337: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   340: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   343: getstatic       net/minecraft/block/Blocks.ed:Lnet/minecraft/block/Block;
        //   346: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   349: bipush          10
        //   351: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.setWeight:(I)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   354: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   357: getstatic       net/minecraft/item/Items.mf:Lnet/minecraft/item/Item;
        //   360: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   363: bipush          10
        //   365: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.setWeight:(I)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   368: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   371: getstatic       net/minecraft/block/Blocks.kQ:Lnet/minecraft/block/Block;
        //   374: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   377: getstatic       net/minecraft/data/server/FishingLootTableGenerator.a:Lnet/minecraft/world/loot/condition/LootCondition$Builder;
        //   380: getstatic       net/minecraft/data/server/FishingLootTableGenerator.b:Lnet/minecraft/world/loot/condition/LootCondition$Builder;
        //   383: invokeinterface net/minecraft/world/loot/condition/LootCondition$Builder.withCondition:(Lnet/minecraft/world/loot/condition/LootCondition$Builder;)Lnet/minecraft/world/loot/condition/AlternativeLootCondition$Builder;
        //   388: getstatic       net/minecraft/data/server/FishingLootTableGenerator.c:Lnet/minecraft/world/loot/condition/LootCondition$Builder;
        //   391: invokevirtual   net/minecraft/world/loot/condition/AlternativeLootCondition$Builder.withCondition:(Lnet/minecraft/world/loot/condition/LootCondition$Builder;)Lnet/minecraft/world/loot/condition/AlternativeLootCondition$Builder;
        //   394: getstatic       net/minecraft/data/server/FishingLootTableGenerator.d:Lnet/minecraft/world/loot/condition/LootCondition$Builder;
        //   397: invokevirtual   net/minecraft/world/loot/condition/AlternativeLootCondition$Builder.withCondition:(Lnet/minecraft/world/loot/condition/LootCondition$Builder;)Lnet/minecraft/world/loot/condition/AlternativeLootCondition$Builder;
        //   400: getstatic       net/minecraft/data/server/FishingLootTableGenerator.e:Lnet/minecraft/world/loot/condition/LootCondition$Builder;
        //   403: invokevirtual   net/minecraft/world/loot/condition/AlternativeLootCondition$Builder.withCondition:(Lnet/minecraft/world/loot/condition/LootCondition$Builder;)Lnet/minecraft/world/loot/condition/AlternativeLootCondition$Builder;
        //   406: getstatic       net/minecraft/data/server/FishingLootTableGenerator.f:Lnet/minecraft/world/loot/condition/LootCondition$Builder;
        //   409: invokevirtual   net/minecraft/world/loot/condition/AlternativeLootCondition$Builder.withCondition:(Lnet/minecraft/world/loot/condition/LootCondition$Builder;)Lnet/minecraft/world/loot/condition/AlternativeLootCondition$Builder;
        //   412: getstatic       net/minecraft/data/server/FishingLootTableGenerator.g:Lnet/minecraft/world/loot/condition/LootCondition$Builder;
        //   415: invokevirtual   net/minecraft/world/loot/condition/AlternativeLootCondition$Builder.withCondition:(Lnet/minecraft/world/loot/condition/LootCondition$Builder;)Lnet/minecraft/world/loot/condition/AlternativeLootCondition$Builder;
        //   418: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withCondition:(Lnet/minecraft/world/loot/condition/LootCondition$Builder;)Lnet/minecraft/world/loot/entry/LootEntry$Builder;
        //   421: checkcast       Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   424: bipush          10
        //   426: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.setWeight:(I)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   429: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   432: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //   435: invokeinterface java/util/function/BiConsumer.accept:(Ljava/lang/Object;Ljava/lang/Object;)V
        //   440: aload_1         /* biConsumer */
        //   441: getstatic       net/minecraft/world/loot/LootTables.GAMEPLAY_FISHING_TREASURE:Lnet/minecraft/util/Identifier;
        //   444: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //   447: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //   450: getstatic       net/minecraft/block/Blocks.dM:Lnet/minecraft/block/Block;
        //   453: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   456: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   459: getstatic       net/minecraft/item/Items.or:Lnet/minecraft/item/Item;
        //   462: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   465: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   468: getstatic       net/minecraft/item/Items.kB:Lnet/minecraft/item/Item;
        //   471: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   474: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   477: getstatic       net/minecraft/item/Items.jf:Lnet/minecraft/item/Item;
        //   480: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   483: fconst_0       
        //   484: ldc_w           0.25
        //   487: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //   490: invokestatic    net/minecraft/world/loot/function/SetDamageLootFunction.builder:(Lnet/minecraft/world/loot/UniformLootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //   493: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   496: bipush          30
        //   498: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //   501: invokestatic    net/minecraft/world/loot/function/EnchantWithLevelsLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/EnchantWithLevelsLootFunction$Builder;
        //   504: invokevirtual   net/minecraft/world/loot/function/EnchantWithLevelsLootFunction$Builder.allowTreasureEnchantments:()Lnet/minecraft/world/loot/function/EnchantWithLevelsLootFunction$Builder;
        //   507: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   510: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   513: getstatic       net/minecraft/item/Items.kY:Lnet/minecraft/item/Item;
        //   516: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   519: fconst_0       
        //   520: ldc_w           0.25
        //   523: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //   526: invokestatic    net/minecraft/world/loot/function/SetDamageLootFunction.builder:(Lnet/minecraft/world/loot/UniformLootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //   529: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   532: bipush          30
        //   534: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //   537: invokestatic    net/minecraft/world/loot/function/EnchantWithLevelsLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/EnchantWithLevelsLootFunction$Builder;
        //   540: invokevirtual   net/minecraft/world/loot/function/EnchantWithLevelsLootFunction$Builder.allowTreasureEnchantments:()Lnet/minecraft/world/loot/function/EnchantWithLevelsLootFunction$Builder;
        //   543: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   546: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   549: getstatic       net/minecraft/item/Items.kS:Lnet/minecraft/item/Item;
        //   552: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   555: bipush          30
        //   557: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //   560: invokestatic    net/minecraft/world/loot/function/EnchantWithLevelsLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/EnchantWithLevelsLootFunction$Builder;
        //   563: invokevirtual   net/minecraft/world/loot/function/EnchantWithLevelsLootFunction$Builder.allowTreasureEnchantments:()Lnet/minecraft/world/loot/function/EnchantWithLevelsLootFunction$Builder;
        //   566: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   569: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   572: getstatic       net/minecraft/item/Items.pw:Lnet/minecraft/item/Item;
        //   575: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   578: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   581: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //   584: invokeinterface java/util/function/BiConsumer.accept:(Ljava/lang/Object;Ljava/lang/Object;)V
        //   589: return         
        //    Signature:
        //  (Ljava/util/function/BiConsumer<Lnet/minecraft/util/Identifier;Lnet/minecraft/world/loot/LootSupplier$Builder;>;)V
        // 
        // The error that occurred was:
        // 
        // java.lang.UnsupportedOperationException: The requested operation is not supported.
        //     at com.strobel.util.ContractUtils.unsupported(ContractUtils.java:27)
        //     at com.strobel.assembler.metadata.TypeReference.getRawType(TypeReference.java:276)
        //     at com.strobel.assembler.metadata.TypeReference.getRawType(TypeReference.java:271)
        //     at com.strobel.assembler.metadata.TypeReference.makeGenericType(TypeReference.java:150)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:187)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:25)
        //     at com.strobel.assembler.metadata.ParameterizedType.accept(ParameterizedType.java:103)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visit(TypeSubstitutionVisitor.java:39)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitMethod(TypeSubstitutionVisitor.java:276)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferCall(TypeAnalysis.java:2591)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1029)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:803)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferCall(TypeAnalysis.java:2695)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1029)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:803)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:770)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:766)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferCall(TypeAnalysis.java:2463)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1029)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:803)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:770)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:766)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferCall(TypeAnalysis.java:2463)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1029)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:803)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:770)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:766)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferCall(TypeAnalysis.java:2463)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1029)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:803)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:770)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:766)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferCall(TypeAnalysis.java:2463)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1029)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:803)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:770)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:766)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferCall(TypeAnalysis.java:2463)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1029)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:803)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:770)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:766)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferCall(TypeAnalysis.java:2463)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1029)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:803)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:770)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:766)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferCall(TypeAnalysis.java:2463)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1029)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:803)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:770)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:766)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferCall(TypeAnalysis.java:2463)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1029)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:803)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferCall(TypeAnalysis.java:2695)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1029)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:803)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:770)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:766)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferCall(TypeAnalysis.java:2515)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1029)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:803)
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:672)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypesForVariables(TypeAnalysis.java:586)
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:397)
        //     at com.strobel.decompiler.ast.TypeAnalysis.run(TypeAnalysis.java:96)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:109)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:214)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:99)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:782)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:675)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:552)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:519)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:161)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:150)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:125)
        //     at cuchaz.enigma.SourceProvider.getSources(SourceProvider.java:66)
        //     at cuchaz.enigma.Deobfuscator.decompileClass(Deobfuscator.java:269)
        //     at cuchaz.enigma.Deobfuscator.lambda$decompileClasses$7(Deobfuscator.java:262)
        //     at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:184)
        //     at java.util.ArrayList$ArrayListSpliterator.forEachRemaining(ArrayList.java:1374)
        //     at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:481)
        //     at java.util.stream.ForEachOps$ForEachTask.compute(ForEachOps.java:291)
        //     at java.util.concurrent.CountedCompleter.exec(CountedCompleter.java:731)
        //     at java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:289)
        //     at java.util.concurrent.ForkJoinPool$WorkQueue.execLocalTasks(ForkJoinPool.java:1040)
        //     at java.util.concurrent.ForkJoinPool$WorkQueue.runTask(ForkJoinPool.java:1058)
        //     at java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1692)
        //     at java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:157)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    static {
        a = LocationCheckLootCondition.builder(new LocationPredicate.Builder().biome(Biomes.w));
        b = LocationCheckLootCondition.builder(new LocationPredicate.Builder().biome(Biomes.x));
        c = LocationCheckLootCondition.builder(new LocationPredicate.Builder().biome(Biomes.y));
        d = LocationCheckLootCondition.builder(new LocationPredicate.Builder().biome(Biomes.aw));
        e = LocationCheckLootCondition.builder(new LocationPredicate.Builder().biome(Biomes.ai));
        f = LocationCheckLootCondition.builder(new LocationPredicate.Builder().biome(Biomes.aj));
        g = LocationCheckLootCondition.builder(new LocationPredicate.Builder().biome(Biomes.ax));
    }
}

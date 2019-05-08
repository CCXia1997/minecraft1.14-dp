package net.minecraft.data.server;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.loot.LootSupplier;
import net.minecraft.util.Identifier;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class GiftLootTableGenerator implements Consumer<BiConsumer<Identifier, LootSupplier.Builder>>
{
    public void a(final BiConsumer<Identifier, LootSupplier.Builder> biConsumer) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: getstatic       net/minecraft/world/loot/LootTables.GAMEPLAY_CAT_MORNING_GIFT:Lnet/minecraft/util/Identifier;
        //     4: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //     7: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //    10: iconst_1       
        //    11: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //    14: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //    17: getstatic       net/minecraft/item/Items.ok:Lnet/minecraft/item/Item;
        //    20: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //    23: bipush          10
        //    25: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.setWeight:(I)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //    28: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //    31: getstatic       net/minecraft/item/Items.oj:Lnet/minecraft/item/Item;
        //    34: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //    37: bipush          10
        //    39: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.setWeight:(I)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //    42: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //    45: getstatic       net/minecraft/item/Items.md:Lnet/minecraft/item/Item;
        //    48: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //    51: bipush          10
        //    53: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.setWeight:(I)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //    56: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //    59: getstatic       net/minecraft/item/Items.jH:Lnet/minecraft/item/Item;
        //    62: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //    65: bipush          10
        //    67: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.setWeight:(I)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //    70: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //    73: getstatic       net/minecraft/item/Items.mf:Lnet/minecraft/item/Item;
        //    76: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //    79: bipush          10
        //    81: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.setWeight:(I)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //    84: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //    87: getstatic       net/minecraft/item/Items.jG:Lnet/minecraft/item/Item;
        //    90: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //    93: bipush          10
        //    95: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.setWeight:(I)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //    98: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   101: getstatic       net/minecraft/item/Items.pv:Lnet/minecraft/item/Item;
        //   104: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   107: iconst_2       
        //   108: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.setWeight:(I)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   111: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   114: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //   117: invokeinterface java/util/function/BiConsumer.accept:(Ljava/lang/Object;Ljava/lang/Object;)V
        //   122: aload_1         /* biConsumer */
        //   123: getstatic       net/minecraft/world/loot/LootTables.ag:Lnet/minecraft/util/Identifier;
        //   126: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //   129: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //   132: iconst_1       
        //   133: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //   136: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   139: getstatic       net/minecraft/item/Items.jV:Lnet/minecraft/item/Item;
        //   142: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   145: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   148: getstatic       net/minecraft/item/Items.jW:Lnet/minecraft/item/Item;
        //   151: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   154: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   157: getstatic       net/minecraft/item/Items.jX:Lnet/minecraft/item/Item;
        //   160: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   163: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   166: getstatic       net/minecraft/item/Items.jY:Lnet/minecraft/item/Item;
        //   169: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   172: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   175: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //   178: invokeinterface java/util/function/BiConsumer.accept:(Ljava/lang/Object;Ljava/lang/Object;)V
        //   183: aload_1         /* biConsumer */
        //   184: getstatic       net/minecraft/world/loot/LootTables.ah:Lnet/minecraft/util/Identifier;
        //   187: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //   190: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //   193: iconst_1       
        //   194: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //   197: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   200: getstatic       net/minecraft/item/Items.oh:Lnet/minecraft/item/Item;
        //   203: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   206: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   209: getstatic       net/minecraft/item/Items.me:Lnet/minecraft/item/Item;
        //   212: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   215: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   218: getstatic       net/minecraft/item/Items.kn:Lnet/minecraft/item/Item;
        //   221: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   224: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   227: getstatic       net/minecraft/item/Items.mc:Lnet/minecraft/item/Item;
        //   230: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   233: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   236: getstatic       net/minecraft/item/Items.ou:Lnet/minecraft/item/Item;
        //   239: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   242: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   245: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //   248: invokeinterface java/util/function/BiConsumer.accept:(Ljava/lang/Object;Ljava/lang/Object;)V
        //   253: aload_1         /* biConsumer */
        //   254: getstatic       net/minecraft/world/loot/LootTables.ai:Lnet/minecraft/util/Identifier;
        //   257: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //   260: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //   263: iconst_1       
        //   264: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //   267: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   270: getstatic       net/minecraft/item/Items.nM:Lnet/minecraft/item/Item;
        //   273: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   276: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   279: getstatic       net/minecraft/item/Items.kR:Lnet/minecraft/item/Item;
        //   282: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   285: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   288: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //   291: invokeinterface java/util/function/BiConsumer.accept:(Ljava/lang/Object;Ljava/lang/Object;)V
        //   296: aload_1         /* biConsumer */
        //   297: getstatic       net/minecraft/world/loot/LootTables.aj:Lnet/minecraft/util/Identifier;
        //   300: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //   303: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //   306: iconst_1       
        //   307: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //   310: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   313: getstatic       net/minecraft/item/Items.kC:Lnet/minecraft/item/Item;
        //   316: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   319: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   322: getstatic       net/minecraft/item/Items.ll:Lnet/minecraft/item/Item;
        //   325: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   328: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   331: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //   334: invokeinterface java/util/function/BiConsumer.accept:(Ljava/lang/Object;Ljava/lang/Object;)V
        //   339: aload_1         /* biConsumer */
        //   340: getstatic       net/minecraft/world/loot/LootTables.ak:Lnet/minecraft/util/Identifier;
        //   343: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //   346: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //   349: iconst_1       
        //   350: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //   353: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   356: getstatic       net/minecraft/item/Items.jQ:Lnet/minecraft/item/Item;
        //   359: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   362: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   365: getstatic       net/minecraft/item/Items.nW:Lnet/minecraft/item/Item;
        //   368: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   371: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   374: getstatic       net/minecraft/item/Items.lU:Lnet/minecraft/item/Item;
        //   377: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   380: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   383: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //   386: invokeinterface java/util/function/BiConsumer.accept:(Ljava/lang/Object;Ljava/lang/Object;)V
        //   391: aload_1         /* biConsumer */
        //   392: getstatic       net/minecraft/world/loot/LootTables.al:Lnet/minecraft/util/Identifier;
        //   395: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //   398: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //   401: iconst_1       
        //   402: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //   405: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   408: getstatic       net/minecraft/item/Items.lb:Lnet/minecraft/item/Item;
        //   411: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   414: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   417: getstatic       net/minecraft/item/Items.lc:Lnet/minecraft/item/Item;
        //   420: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   423: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   426: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //   429: invokeinterface java/util/function/BiConsumer.accept:(Ljava/lang/Object;Ljava/lang/Object;)V
        //   434: aload_1         /* biConsumer */
        //   435: getstatic       net/minecraft/world/loot/LootTables.am:Lnet/minecraft/util/Identifier;
        //   438: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //   441: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //   444: iconst_1       
        //   445: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //   448: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   451: getstatic       net/minecraft/item/Items.jg:Lnet/minecraft/item/Item;
        //   454: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   457: bipush          26
        //   459: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.setWeight:(I)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   462: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   465: getstatic       net/minecraft/item/Items.oU:Lnet/minecraft/item/Item;
        //   468: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   471: fconst_0       
        //   472: fconst_1       
        //   473: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //   476: invokestatic    net/minecraft/world/loot/function/SetCountLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //   479: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   482: new             Lnet/minecraft/nbt/CompoundTag;
        //   485: dup            
        //   486: invokespecial   net/minecraft/nbt/CompoundTag.<init>:()V
        //   489: invokedynamic   BootstrapMethod #0, accept:()Ljava/util/function/Consumer;
        //   494: invokestatic    net/minecraft/util/SystemUtil.consume:(Ljava/lang/Object;Ljava/util/function/Consumer;)Ljava/lang/Object;
        //   497: checkcast       Lnet/minecraft/nbt/CompoundTag;
        //   500: invokestatic    net/minecraft/world/loot/function/SetNbtLootFunction.builder:(Lnet/minecraft/nbt/CompoundTag;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //   503: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   506: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   509: getstatic       net/minecraft/item/Items.oU:Lnet/minecraft/item/Item;
        //   512: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   515: fconst_0       
        //   516: fconst_1       
        //   517: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //   520: invokestatic    net/minecraft/world/loot/function/SetCountLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //   523: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   526: new             Lnet/minecraft/nbt/CompoundTag;
        //   529: dup            
        //   530: invokespecial   net/minecraft/nbt/CompoundTag.<init>:()V
        //   533: invokedynamic   BootstrapMethod #1, accept:()Ljava/util/function/Consumer;
        //   538: invokestatic    net/minecraft/util/SystemUtil.consume:(Ljava/lang/Object;Ljava/util/function/Consumer;)Ljava/lang/Object;
        //   541: checkcast       Lnet/minecraft/nbt/CompoundTag;
        //   544: invokestatic    net/minecraft/world/loot/function/SetNbtLootFunction.builder:(Lnet/minecraft/nbt/CompoundTag;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //   547: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   550: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   553: getstatic       net/minecraft/item/Items.oU:Lnet/minecraft/item/Item;
        //   556: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   559: fconst_0       
        //   560: fconst_1       
        //   561: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //   564: invokestatic    net/minecraft/world/loot/function/SetCountLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //   567: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   570: new             Lnet/minecraft/nbt/CompoundTag;
        //   573: dup            
        //   574: invokespecial   net/minecraft/nbt/CompoundTag.<init>:()V
        //   577: invokedynamic   BootstrapMethod #2, accept:()Ljava/util/function/Consumer;
        //   582: invokestatic    net/minecraft/util/SystemUtil.consume:(Ljava/lang/Object;Ljava/util/function/Consumer;)Ljava/lang/Object;
        //   585: checkcast       Lnet/minecraft/nbt/CompoundTag;
        //   588: invokestatic    net/minecraft/world/loot/function/SetNbtLootFunction.builder:(Lnet/minecraft/nbt/CompoundTag;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //   591: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   594: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   597: getstatic       net/minecraft/item/Items.oU:Lnet/minecraft/item/Item;
        //   600: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   603: fconst_0       
        //   604: fconst_1       
        //   605: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //   608: invokestatic    net/minecraft/world/loot/function/SetCountLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //   611: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   614: new             Lnet/minecraft/nbt/CompoundTag;
        //   617: dup            
        //   618: invokespecial   net/minecraft/nbt/CompoundTag.<init>:()V
        //   621: invokedynamic   BootstrapMethod #3, accept:()Ljava/util/function/Consumer;
        //   626: invokestatic    net/minecraft/util/SystemUtil.consume:(Ljava/lang/Object;Ljava/util/function/Consumer;)Ljava/lang/Object;
        //   629: checkcast       Lnet/minecraft/nbt/CompoundTag;
        //   632: invokestatic    net/minecraft/world/loot/function/SetNbtLootFunction.builder:(Lnet/minecraft/nbt/CompoundTag;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //   635: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   638: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   641: getstatic       net/minecraft/item/Items.oU:Lnet/minecraft/item/Item;
        //   644: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   647: fconst_0       
        //   648: fconst_1       
        //   649: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //   652: invokestatic    net/minecraft/world/loot/function/SetCountLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //   655: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   658: new             Lnet/minecraft/nbt/CompoundTag;
        //   661: dup            
        //   662: invokespecial   net/minecraft/nbt/CompoundTag.<init>:()V
        //   665: invokedynamic   BootstrapMethod #4, accept:()Ljava/util/function/Consumer;
        //   670: invokestatic    net/minecraft/util/SystemUtil.consume:(Ljava/lang/Object;Ljava/util/function/Consumer;)Ljava/lang/Object;
        //   673: checkcast       Lnet/minecraft/nbt/CompoundTag;
        //   676: invokestatic    net/minecraft/world/loot/function/SetNbtLootFunction.builder:(Lnet/minecraft/nbt/CompoundTag;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //   679: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   682: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   685: getstatic       net/minecraft/item/Items.oU:Lnet/minecraft/item/Item;
        //   688: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   691: fconst_0       
        //   692: fconst_1       
        //   693: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //   696: invokestatic    net/minecraft/world/loot/function/SetCountLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //   699: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   702: new             Lnet/minecraft/nbt/CompoundTag;
        //   705: dup            
        //   706: invokespecial   net/minecraft/nbt/CompoundTag.<init>:()V
        //   709: invokedynamic   BootstrapMethod #5, accept:()Ljava/util/function/Consumer;
        //   714: invokestatic    net/minecraft/util/SystemUtil.consume:(Ljava/lang/Object;Ljava/util/function/Consumer;)Ljava/lang/Object;
        //   717: checkcast       Lnet/minecraft/nbt/CompoundTag;
        //   720: invokestatic    net/minecraft/world/loot/function/SetNbtLootFunction.builder:(Lnet/minecraft/nbt/CompoundTag;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //   723: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   726: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   729: getstatic       net/minecraft/item/Items.oU:Lnet/minecraft/item/Item;
        //   732: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   735: fconst_0       
        //   736: fconst_1       
        //   737: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //   740: invokestatic    net/minecraft/world/loot/function/SetCountLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //   743: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   746: new             Lnet/minecraft/nbt/CompoundTag;
        //   749: dup            
        //   750: invokespecial   net/minecraft/nbt/CompoundTag.<init>:()V
        //   753: invokedynamic   BootstrapMethod #6, accept:()Ljava/util/function/Consumer;
        //   758: invokestatic    net/minecraft/util/SystemUtil.consume:(Ljava/lang/Object;Ljava/util/function/Consumer;)Ljava/lang/Object;
        //   761: checkcast       Lnet/minecraft/nbt/CompoundTag;
        //   764: invokestatic    net/minecraft/world/loot/function/SetNbtLootFunction.builder:(Lnet/minecraft/nbt/CompoundTag;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //   767: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   770: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   773: getstatic       net/minecraft/item/Items.oU:Lnet/minecraft/item/Item;
        //   776: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   779: fconst_0       
        //   780: fconst_1       
        //   781: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //   784: invokestatic    net/minecraft/world/loot/function/SetCountLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //   787: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   790: new             Lnet/minecraft/nbt/CompoundTag;
        //   793: dup            
        //   794: invokespecial   net/minecraft/nbt/CompoundTag.<init>:()V
        //   797: invokedynamic   BootstrapMethod #7, accept:()Ljava/util/function/Consumer;
        //   802: invokestatic    net/minecraft/util/SystemUtil.consume:(Ljava/lang/Object;Ljava/util/function/Consumer;)Ljava/lang/Object;
        //   805: checkcast       Lnet/minecraft/nbt/CompoundTag;
        //   808: invokestatic    net/minecraft/world/loot/function/SetNbtLootFunction.builder:(Lnet/minecraft/nbt/CompoundTag;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //   811: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   814: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   817: getstatic       net/minecraft/item/Items.oU:Lnet/minecraft/item/Item;
        //   820: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   823: fconst_0       
        //   824: fconst_1       
        //   825: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //   828: invokestatic    net/minecraft/world/loot/function/SetCountLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //   831: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   834: new             Lnet/minecraft/nbt/CompoundTag;
        //   837: dup            
        //   838: invokespecial   net/minecraft/nbt/CompoundTag.<init>:()V
        //   841: invokedynamic   BootstrapMethod #8, accept:()Ljava/util/function/Consumer;
        //   846: invokestatic    net/minecraft/util/SystemUtil.consume:(Ljava/lang/Object;Ljava/util/function/Consumer;)Ljava/lang/Object;
        //   849: checkcast       Lnet/minecraft/nbt/CompoundTag;
        //   852: invokestatic    net/minecraft/world/loot/function/SetNbtLootFunction.builder:(Lnet/minecraft/nbt/CompoundTag;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //   855: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   858: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   861: getstatic       net/minecraft/item/Items.oU:Lnet/minecraft/item/Item;
        //   864: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   867: fconst_0       
        //   868: fconst_1       
        //   869: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //   872: invokestatic    net/minecraft/world/loot/function/SetCountLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //   875: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   878: new             Lnet/minecraft/nbt/CompoundTag;
        //   881: dup            
        //   882: invokespecial   net/minecraft/nbt/CompoundTag.<init>:()V
        //   885: invokedynamic   BootstrapMethod #9, accept:()Ljava/util/function/Consumer;
        //   890: invokestatic    net/minecraft/util/SystemUtil.consume:(Ljava/lang/Object;Ljava/util/function/Consumer;)Ljava/lang/Object;
        //   893: checkcast       Lnet/minecraft/nbt/CompoundTag;
        //   896: invokestatic    net/minecraft/world/loot/function/SetNbtLootFunction.builder:(Lnet/minecraft/nbt/CompoundTag;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //   899: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   902: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   905: getstatic       net/minecraft/item/Items.oU:Lnet/minecraft/item/Item;
        //   908: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   911: fconst_0       
        //   912: fconst_1       
        //   913: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //   916: invokestatic    net/minecraft/world/loot/function/SetCountLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //   919: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   922: new             Lnet/minecraft/nbt/CompoundTag;
        //   925: dup            
        //   926: invokespecial   net/minecraft/nbt/CompoundTag.<init>:()V
        //   929: invokedynamic   BootstrapMethod #10, accept:()Ljava/util/function/Consumer;
        //   934: invokestatic    net/minecraft/util/SystemUtil.consume:(Ljava/lang/Object;Ljava/util/function/Consumer;)Ljava/lang/Object;
        //   937: checkcast       Lnet/minecraft/nbt/CompoundTag;
        //   940: invokestatic    net/minecraft/world/loot/function/SetNbtLootFunction.builder:(Lnet/minecraft/nbt/CompoundTag;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //   943: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   946: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   949: getstatic       net/minecraft/item/Items.oU:Lnet/minecraft/item/Item;
        //   952: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   955: fconst_0       
        //   956: fconst_1       
        //   957: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //   960: invokestatic    net/minecraft/world/loot/function/SetCountLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //   963: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   966: new             Lnet/minecraft/nbt/CompoundTag;
        //   969: dup            
        //   970: invokespecial   net/minecraft/nbt/CompoundTag.<init>:()V
        //   973: invokedynamic   BootstrapMethod #11, accept:()Ljava/util/function/Consumer;
        //   978: invokestatic    net/minecraft/util/SystemUtil.consume:(Ljava/lang/Object;Ljava/util/function/Consumer;)Ljava/lang/Object;
        //   981: checkcast       Lnet/minecraft/nbt/CompoundTag;
        //   984: invokestatic    net/minecraft/world/loot/function/SetNbtLootFunction.builder:(Lnet/minecraft/nbt/CompoundTag;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //   987: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   990: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   993: getstatic       net/minecraft/item/Items.oU:Lnet/minecraft/item/Item;
        //   996: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   999: fconst_0       
        //  1000: fconst_1       
        //  1001: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  1004: invokestatic    net/minecraft/world/loot/function/SetCountLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //  1007: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  1010: new             Lnet/minecraft/nbt/CompoundTag;
        //  1013: dup            
        //  1014: invokespecial   net/minecraft/nbt/CompoundTag.<init>:()V
        //  1017: invokedynamic   BootstrapMethod #12, accept:()Ljava/util/function/Consumer;
        //  1022: invokestatic    net/minecraft/util/SystemUtil.consume:(Ljava/lang/Object;Ljava/util/function/Consumer;)Ljava/lang/Object;
        //  1025: checkcast       Lnet/minecraft/nbt/CompoundTag;
        //  1028: invokestatic    net/minecraft/world/loot/function/SetNbtLootFunction.builder:(Lnet/minecraft/nbt/CompoundTag;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //  1031: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  1034: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  1037: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  1040: invokeinterface java/util/function/BiConsumer.accept:(Ljava/lang/Object;Ljava/lang/Object;)V
        //  1045: aload_1         /* biConsumer */
        //  1046: getstatic       net/minecraft/world/loot/LootTables.an:Lnet/minecraft/util/Identifier;
        //  1049: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  1052: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //  1055: iconst_1       
        //  1056: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //  1059: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  1062: getstatic       net/minecraft/item/Items.kF:Lnet/minecraft/item/Item;
        //  1065: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  1068: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  1071: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  1074: invokeinterface java/util/function/BiConsumer.accept:(Ljava/lang/Object;Ljava/lang/Object;)V
        //  1079: aload_1         /* biConsumer */
        //  1080: getstatic       net/minecraft/world/loot/LootTables.ao:Lnet/minecraft/util/Identifier;
        //  1083: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  1086: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //  1089: iconst_1       
        //  1090: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //  1093: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  1096: getstatic       net/minecraft/item/Items.kS:Lnet/minecraft/item/Item;
        //  1099: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  1102: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  1105: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  1108: invokeinterface java/util/function/BiConsumer.accept:(Ljava/lang/Object;Ljava/lang/Object;)V
        //  1113: aload_1         /* biConsumer */
        //  1114: getstatic       net/minecraft/world/loot/LootTables.ap:Lnet/minecraft/util/Identifier;
        //  1117: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  1120: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //  1123: iconst_1       
        //  1124: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //  1127: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  1130: getstatic       net/minecraft/item/Items.CLAY:Lnet/minecraft/item/Item;
        //  1133: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  1136: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  1139: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  1142: invokeinterface java/util/function/BiConsumer.accept:(Ljava/lang/Object;Ljava/lang/Object;)V
        //  1147: aload_1         /* biConsumer */
        //  1148: getstatic       net/minecraft/world/loot/LootTables.aq:Lnet/minecraft/util/Identifier;
        //  1151: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  1154: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //  1157: iconst_1       
        //  1158: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //  1161: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  1164: getstatic       net/minecraft/item/Items.WHITE_WOOL:Lnet/minecraft/item/Item;
        //  1167: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  1170: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  1173: getstatic       net/minecraft/item/Items.ORANGE_WOOL:Lnet/minecraft/item/Item;
        //  1176: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  1179: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  1182: getstatic       net/minecraft/item/Items.MAGENTA_WOOL:Lnet/minecraft/item/Item;
        //  1185: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  1188: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  1191: getstatic       net/minecraft/item/Items.LIGHT_BLUE_WOOL:Lnet/minecraft/item/Item;
        //  1194: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  1197: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  1200: getstatic       net/minecraft/item/Items.YELLOW_WOOL:Lnet/minecraft/item/Item;
        //  1203: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  1206: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  1209: getstatic       net/minecraft/item/Items.LIME_WOOL:Lnet/minecraft/item/Item;
        //  1212: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  1215: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  1218: getstatic       net/minecraft/item/Items.PINK_WOOL:Lnet/minecraft/item/Item;
        //  1221: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  1224: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  1227: getstatic       net/minecraft/item/Items.GRAY_WOOL:Lnet/minecraft/item/Item;
        //  1230: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  1233: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  1236: getstatic       net/minecraft/item/Items.LIGHT_GRAY_WOOL:Lnet/minecraft/item/Item;
        //  1239: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  1242: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  1245: getstatic       net/minecraft/item/Items.CYAN_WOOL:Lnet/minecraft/item/Item;
        //  1248: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  1251: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  1254: getstatic       net/minecraft/item/Items.PURPLE_WOOL:Lnet/minecraft/item/Item;
        //  1257: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  1260: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  1263: getstatic       net/minecraft/item/Items.BLUE_WOOL:Lnet/minecraft/item/Item;
        //  1266: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  1269: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  1272: getstatic       net/minecraft/item/Items.BROWN_WOOL:Lnet/minecraft/item/Item;
        //  1275: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  1278: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  1281: getstatic       net/minecraft/item/Items.GREEN_WOOL:Lnet/minecraft/item/Item;
        //  1284: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  1287: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  1290: getstatic       net/minecraft/item/Items.RED_WOOL:Lnet/minecraft/item/Item;
        //  1293: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  1296: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  1299: getstatic       net/minecraft/item/Items.BLACK_WOOL:Lnet/minecraft/item/Item;
        //  1302: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  1305: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  1308: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  1311: invokeinterface java/util/function/BiConsumer.accept:(Ljava/lang/Object;Ljava/lang/Object;)V
        //  1316: aload_1         /* biConsumer */
        //  1317: getstatic       net/minecraft/world/loot/LootTables.ar:Lnet/minecraft/util/Identifier;
        //  1320: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  1323: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //  1326: iconst_1       
        //  1327: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //  1330: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  1333: getstatic       net/minecraft/item/Items.jt:Lnet/minecraft/item/Item;
        //  1336: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  1339: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  1342: getstatic       net/minecraft/item/Items.ju:Lnet/minecraft/item/Item;
        //  1345: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  1348: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  1351: getstatic       net/minecraft/item/Items.jK:Lnet/minecraft/item/Item;
        //  1354: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  1357: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  1360: getstatic       net/minecraft/item/Items.js:Lnet/minecraft/item/Item;
        //  1363: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  1366: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  1369: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  1372: invokeinterface java/util/function/BiConsumer.accept:(Ljava/lang/Object;Ljava/lang/Object;)V
        //  1377: aload_1         /* biConsumer */
        //  1378: getstatic       net/minecraft/world/loot/LootTables.as:Lnet/minecraft/util/Identifier;
        //  1381: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  1384: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //  1387: iconst_1       
        //  1388: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //  1391: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  1394: getstatic       net/minecraft/item/Items.ju:Lnet/minecraft/item/Item;
        //  1397: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  1400: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  1403: getstatic       net/minecraft/item/Items.jF:Lnet/minecraft/item/Item;
        //  1406: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  1409: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  1412: getstatic       net/minecraft/item/Items.jc:Lnet/minecraft/item/Item;
        //  1415: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  1418: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  1421: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  1424: invokeinterface java/util/function/BiConsumer.accept:(Ljava/lang/Object;Ljava/lang/Object;)V
        //  1429: return         
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
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:173)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:25)
        //     at com.strobel.assembler.metadata.ParameterizedType.accept(ParameterizedType.java:103)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visit(TypeSubstitutionVisitor.java:39)
        //     at com.strobel.assembler.metadata.MetadataHelper.substituteGenericArguments(MetadataHelper.java:1100)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferCall(TypeAnalysis.java:2676)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1029)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:803)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferCall(TypeAnalysis.java:2695)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1029)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:803)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferCall(TypeAnalysis.java:2695)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1029)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:803)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferCall(TypeAnalysis.java:2695)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1029)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:803)
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:672)
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:655)
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:365)
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
}

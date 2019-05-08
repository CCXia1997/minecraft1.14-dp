package net.minecraft.data.server;

import net.minecraft.predicate.entity.EntityFlagsPredicate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.loot.entry.LootTableEntry;
import net.minecraft.entity.EntityType;
import net.minecraft.world.loot.entry.LootEntry;
import net.minecraft.world.loot.entry.ItemEntry;
import net.minecraft.world.loot.LootTableRange;
import net.minecraft.world.loot.ConstantLootTableRange;
import net.minecraft.world.loot.LootPool;
import net.minecraft.item.ItemProvider;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.world.loot.LootSupplier;
import net.minecraft.util.Identifier;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class EntityLootTableGenerator implements Consumer<BiConsumer<Identifier, LootSupplier.Builder>>
{
    private static final EntityPredicate.Builder a;
    private final Map<Identifier, LootSupplier.Builder> b;
    
    public EntityLootTableGenerator() {
        this.b = Maps.newHashMap();
    }
    
    private static LootSupplier.Builder a(final ItemProvider itemProvider) {
        return LootSupplier.builder().withPool(LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry(ItemEntry.builder(itemProvider))).withPool(LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry(LootTableEntry.builder(EntityType.SHEEP.getLootTableId())));
    }
    
    public void a(final BiConsumer<Identifier, LootSupplier.Builder> biConsumer) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: getstatic       net/minecraft/entity/EntityType.ARMOR_STAND:Lnet/minecraft/entity/EntityType;
        //     4: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //     7: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //    10: aload_0         /* this */
        //    11: getstatic       net/minecraft/entity/EntityType.BAT:Lnet/minecraft/entity/EntityType;
        //    14: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //    17: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //    20: aload_0         /* this */
        //    21: getstatic       net/minecraft/entity/EntityType.BLAZE:Lnet/minecraft/entity/EntityType;
        //    24: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //    27: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //    30: iconst_1       
        //    31: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //    34: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //    37: getstatic       net/minecraft/item/Items.mh:Lnet/minecraft/item/Item;
        //    40: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //    43: fconst_0       
        //    44: fconst_1       
        //    45: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //    48: invokestatic    net/minecraft/world/loot/function/SetCountLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //    51: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //    54: fconst_0       
        //    55: fconst_1       
        //    56: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //    59: invokestatic    net/minecraft/world/loot/function/LootingEnchantLootFunction.builder:(Lnet/minecraft/world/loot/UniformLootTableRange;)Lnet/minecraft/world/loot/function/LootingEnchantLootFunction$Builder;
        //    62: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //    65: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //    68: invokestatic    net/minecraft/world/loot/condition/KilledByPlayerLootCondition.builder:()Lnet/minecraft/world/loot/condition/LootCondition$Builder;
        //    71: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withCondition:(Lnet/minecraft/world/loot/condition/LootCondition$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //    74: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //    77: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //    80: aload_0         /* this */
        //    81: getstatic       net/minecraft/entity/EntityType.CAT:Lnet/minecraft/entity/EntityType;
        //    84: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //    87: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //    90: iconst_1       
        //    91: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //    94: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //    97: getstatic       net/minecraft/item/Items.jG:Lnet/minecraft/item/Item;
        //   100: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   103: fconst_0       
        //   104: fconst_2       
        //   105: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //   108: invokestatic    net/minecraft/world/loot/function/SetCountLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //   111: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   114: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   117: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //   120: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //   123: aload_0         /* this */
        //   124: getstatic       net/minecraft/entity/EntityType.CAVE_SPIDER:Lnet/minecraft/entity/EntityType;
        //   127: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //   130: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //   133: iconst_1       
        //   134: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //   137: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   140: getstatic       net/minecraft/item/Items.jG:Lnet/minecraft/item/Item;
        //   143: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   146: fconst_0       
        //   147: fconst_2       
        //   148: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //   151: invokestatic    net/minecraft/world/loot/function/SetCountLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //   154: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   157: fconst_0       
        //   158: fconst_1       
        //   159: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //   162: invokestatic    net/minecraft/world/loot/function/LootingEnchantLootFunction.builder:(Lnet/minecraft/world/loot/UniformLootTableRange;)Lnet/minecraft/world/loot/function/LootingEnchantLootFunction$Builder;
        //   165: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   168: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   171: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //   174: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //   177: iconst_1       
        //   178: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //   181: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   184: getstatic       net/minecraft/item/Items.mn:Lnet/minecraft/item/Item;
        //   187: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   190: ldc             -1.0
        //   192: fconst_1       
        //   193: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //   196: invokestatic    net/minecraft/world/loot/function/SetCountLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //   199: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   202: fconst_0       
        //   203: fconst_1       
        //   204: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //   207: invokestatic    net/minecraft/world/loot/function/LootingEnchantLootFunction.builder:(Lnet/minecraft/world/loot/UniformLootTableRange;)Lnet/minecraft/world/loot/function/LootingEnchantLootFunction$Builder;
        //   210: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   213: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   216: invokestatic    net/minecraft/world/loot/condition/KilledByPlayerLootCondition.builder:()Lnet/minecraft/world/loot/condition/LootCondition$Builder;
        //   219: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withCondition:(Lnet/minecraft/world/loot/condition/LootCondition$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   222: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //   225: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //   228: aload_0         /* this */
        //   229: getstatic       net/minecraft/entity/EntityType.CHICKEN:Lnet/minecraft/entity/EntityType;
        //   232: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //   235: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //   238: iconst_1       
        //   239: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //   242: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   245: getstatic       net/minecraft/item/Items.jH:Lnet/minecraft/item/Item;
        //   248: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   251: fconst_0       
        //   252: fconst_2       
        //   253: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //   256: invokestatic    net/minecraft/world/loot/function/SetCountLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //   259: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   262: fconst_0       
        //   263: fconst_1       
        //   264: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //   267: invokestatic    net/minecraft/world/loot/function/LootingEnchantLootFunction.builder:(Lnet/minecraft/world/loot/UniformLootTableRange;)Lnet/minecraft/world/loot/function/LootingEnchantLootFunction$Builder;
        //   270: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   273: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   276: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //   279: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //   282: iconst_1       
        //   283: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //   286: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   289: getstatic       net/minecraft/item/Items.md:Lnet/minecraft/item/Item;
        //   292: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   295: invokestatic    net/minecraft/world/loot/function/FurnaceSmeltLootFunction.builder:()Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //   298: getstatic       net/minecraft/world/loot/context/LootContext$EntityTarget.THIS:Lnet/minecraft/world/loot/context/LootContext$EntityTarget;
        //   301: getstatic       net/minecraft/data/server/EntityLootTableGenerator.a:Lnet/minecraft/predicate/entity/EntityPredicate$Builder;
        //   304: invokestatic    net/minecraft/world/loot/condition/EntityPropertiesLootCondition.builder:(Lnet/minecraft/world/loot/context/LootContext$EntityTarget;Lnet/minecraft/predicate/entity/EntityPredicate$Builder;)Lnet/minecraft/world/loot/condition/LootCondition$Builder;
        //   307: invokevirtual   net/minecraft/world/loot/function/ConditionalLootFunction$Builder.withCondition:(Lnet/minecraft/world/loot/condition/LootCondition$Builder;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //   310: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   313: fconst_0       
        //   314: fconst_1       
        //   315: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //   318: invokestatic    net/minecraft/world/loot/function/LootingEnchantLootFunction.builder:(Lnet/minecraft/world/loot/UniformLootTableRange;)Lnet/minecraft/world/loot/function/LootingEnchantLootFunction$Builder;
        //   321: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   324: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   327: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //   330: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //   333: aload_0         /* this */
        //   334: getstatic       net/minecraft/entity/EntityType.COD:Lnet/minecraft/entity/EntityType;
        //   337: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //   340: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //   343: iconst_1       
        //   344: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //   347: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   350: getstatic       net/minecraft/item/Items.lb:Lnet/minecraft/item/Item;
        //   353: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   356: invokestatic    net/minecraft/world/loot/function/FurnaceSmeltLootFunction.builder:()Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //   359: getstatic       net/minecraft/world/loot/context/LootContext$EntityTarget.THIS:Lnet/minecraft/world/loot/context/LootContext$EntityTarget;
        //   362: getstatic       net/minecraft/data/server/EntityLootTableGenerator.a:Lnet/minecraft/predicate/entity/EntityPredicate$Builder;
        //   365: invokestatic    net/minecraft/world/loot/condition/EntityPropertiesLootCondition.builder:(Lnet/minecraft/world/loot/context/LootContext$EntityTarget;Lnet/minecraft/predicate/entity/EntityPredicate$Builder;)Lnet/minecraft/world/loot/condition/LootCondition$Builder;
        //   368: invokevirtual   net/minecraft/world/loot/function/ConditionalLootFunction$Builder.withCondition:(Lnet/minecraft/world/loot/condition/LootCondition$Builder;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //   371: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   374: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   377: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //   380: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //   383: iconst_1       
        //   384: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //   387: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   390: getstatic       net/minecraft/item/Items.lw:Lnet/minecraft/item/Item;
        //   393: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   396: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   399: ldc             0.05
        //   401: invokestatic    net/minecraft/world/loot/condition/RandomChanceLootCondition.builder:(F)Lnet/minecraft/world/loot/condition/LootCondition$Builder;
        //   404: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withCondition:(Lnet/minecraft/world/loot/condition/LootCondition$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   407: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //   410: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //   413: aload_0         /* this */
        //   414: getstatic       net/minecraft/entity/EntityType.COW:Lnet/minecraft/entity/EntityType;
        //   417: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //   420: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //   423: iconst_1       
        //   424: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //   427: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   430: getstatic       net/minecraft/item/Items.kF:Lnet/minecraft/item/Item;
        //   433: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   436: fconst_0       
        //   437: fconst_2       
        //   438: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //   441: invokestatic    net/minecraft/world/loot/function/SetCountLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //   444: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   447: fconst_0       
        //   448: fconst_1       
        //   449: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //   452: invokestatic    net/minecraft/world/loot/function/LootingEnchantLootFunction.builder:(Lnet/minecraft/world/loot/UniformLootTableRange;)Lnet/minecraft/world/loot/function/LootingEnchantLootFunction$Builder;
        //   455: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   458: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   461: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //   464: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //   467: iconst_1       
        //   468: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //   471: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   474: getstatic       net/minecraft/item/Items.mb:Lnet/minecraft/item/Item;
        //   477: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   480: fconst_1       
        //   481: ldc             3.0
        //   483: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //   486: invokestatic    net/minecraft/world/loot/function/SetCountLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //   489: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   492: invokestatic    net/minecraft/world/loot/function/FurnaceSmeltLootFunction.builder:()Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //   495: getstatic       net/minecraft/world/loot/context/LootContext$EntityTarget.THIS:Lnet/minecraft/world/loot/context/LootContext$EntityTarget;
        //   498: getstatic       net/minecraft/data/server/EntityLootTableGenerator.a:Lnet/minecraft/predicate/entity/EntityPredicate$Builder;
        //   501: invokestatic    net/minecraft/world/loot/condition/EntityPropertiesLootCondition.builder:(Lnet/minecraft/world/loot/context/LootContext$EntityTarget;Lnet/minecraft/predicate/entity/EntityPredicate$Builder;)Lnet/minecraft/world/loot/condition/LootCondition$Builder;
        //   504: invokevirtual   net/minecraft/world/loot/function/ConditionalLootFunction$Builder.withCondition:(Lnet/minecraft/world/loot/condition/LootCondition$Builder;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //   507: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   510: fconst_0       
        //   511: fconst_1       
        //   512: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //   515: invokestatic    net/minecraft/world/loot/function/LootingEnchantLootFunction.builder:(Lnet/minecraft/world/loot/UniformLootTableRange;)Lnet/minecraft/world/loot/function/LootingEnchantLootFunction$Builder;
        //   518: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   521: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   524: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //   527: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //   530: aload_0         /* this */
        //   531: getstatic       net/minecraft/entity/EntityType.CREEPER:Lnet/minecraft/entity/EntityType;
        //   534: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //   537: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //   540: iconst_1       
        //   541: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //   544: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   547: getstatic       net/minecraft/item/Items.jI:Lnet/minecraft/item/Item;
        //   550: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   553: fconst_0       
        //   554: fconst_2       
        //   555: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //   558: invokestatic    net/minecraft/world/loot/function/SetCountLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //   561: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   564: fconst_0       
        //   565: fconst_1       
        //   566: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //   569: invokestatic    net/minecraft/world/loot/function/LootingEnchantLootFunction.builder:(Lnet/minecraft/world/loot/UniformLootTableRange;)Lnet/minecraft/world/loot/function/LootingEnchantLootFunction$Builder;
        //   572: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   575: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   578: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //   581: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //   584: getstatic       net/minecraft/tag/ItemTags.K:Lnet/minecraft/tag/Tag;
        //   587: invokestatic    net/minecraft/world/loot/entry/TagEntry.builder:(Lnet/minecraft/tag/Tag;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   590: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   593: getstatic       net/minecraft/world/loot/context/LootContext$EntityTarget.KILLER:Lnet/minecraft/world/loot/context/LootContext$EntityTarget;
        //   596: invokestatic    net/minecraft/predicate/entity/EntityPredicate$Builder.create:()Lnet/minecraft/predicate/entity/EntityPredicate$Builder;
        //   599: getstatic       net/minecraft/tag/EntityTags.a:Lnet/minecraft/tag/Tag;
        //   602: invokevirtual   net/minecraft/predicate/entity/EntityPredicate$Builder.type:(Lnet/minecraft/tag/Tag;)Lnet/minecraft/predicate/entity/EntityPredicate$Builder;
        //   605: invokestatic    net/minecraft/world/loot/condition/EntityPropertiesLootCondition.builder:(Lnet/minecraft/world/loot/context/LootContext$EntityTarget;Lnet/minecraft/predicate/entity/EntityPredicate$Builder;)Lnet/minecraft/world/loot/condition/LootCondition$Builder;
        //   608: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withCondition:(Lnet/minecraft/world/loot/condition/LootCondition$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   611: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //   614: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //   617: aload_0         /* this */
        //   618: getstatic       net/minecraft/entity/EntityType.DOLPHIN:Lnet/minecraft/entity/EntityType;
        //   621: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //   624: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //   627: iconst_1       
        //   628: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //   631: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   634: getstatic       net/minecraft/item/Items.lb:Lnet/minecraft/item/Item;
        //   637: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   640: fconst_0       
        //   641: fconst_1       
        //   642: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //   645: invokestatic    net/minecraft/world/loot/function/SetCountLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //   648: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   651: fconst_0       
        //   652: fconst_1       
        //   653: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //   656: invokestatic    net/minecraft/world/loot/function/LootingEnchantLootFunction.builder:(Lnet/minecraft/world/loot/UniformLootTableRange;)Lnet/minecraft/world/loot/function/LootingEnchantLootFunction$Builder;
        //   659: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   662: invokestatic    net/minecraft/world/loot/function/FurnaceSmeltLootFunction.builder:()Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //   665: getstatic       net/minecraft/world/loot/context/LootContext$EntityTarget.THIS:Lnet/minecraft/world/loot/context/LootContext$EntityTarget;
        //   668: getstatic       net/minecraft/data/server/EntityLootTableGenerator.a:Lnet/minecraft/predicate/entity/EntityPredicate$Builder;
        //   671: invokestatic    net/minecraft/world/loot/condition/EntityPropertiesLootCondition.builder:(Lnet/minecraft/world/loot/context/LootContext$EntityTarget;Lnet/minecraft/predicate/entity/EntityPredicate$Builder;)Lnet/minecraft/world/loot/condition/LootCondition$Builder;
        //   674: invokevirtual   net/minecraft/world/loot/function/ConditionalLootFunction$Builder.withCondition:(Lnet/minecraft/world/loot/condition/LootCondition$Builder;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //   677: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   680: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   683: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //   686: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //   689: aload_0         /* this */
        //   690: getstatic       net/minecraft/entity/EntityType.DONKEY:Lnet/minecraft/entity/EntityType;
        //   693: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //   696: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //   699: iconst_1       
        //   700: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //   703: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   706: getstatic       net/minecraft/item/Items.kF:Lnet/minecraft/item/Item;
        //   709: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   712: fconst_0       
        //   713: fconst_2       
        //   714: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //   717: invokestatic    net/minecraft/world/loot/function/SetCountLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //   720: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   723: fconst_0       
        //   724: fconst_1       
        //   725: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //   728: invokestatic    net/minecraft/world/loot/function/LootingEnchantLootFunction.builder:(Lnet/minecraft/world/loot/UniformLootTableRange;)Lnet/minecraft/world/loot/function/LootingEnchantLootFunction$Builder;
        //   731: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   734: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   737: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //   740: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //   743: aload_0         /* this */
        //   744: getstatic       net/minecraft/entity/EntityType.DROWNED:Lnet/minecraft/entity/EntityType;
        //   747: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //   750: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //   753: iconst_1       
        //   754: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //   757: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   760: getstatic       net/minecraft/item/Items.mf:Lnet/minecraft/item/Item;
        //   763: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   766: fconst_0       
        //   767: fconst_2       
        //   768: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //   771: invokestatic    net/minecraft/world/loot/function/SetCountLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //   774: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   777: fconst_0       
        //   778: fconst_1       
        //   779: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //   782: invokestatic    net/minecraft/world/loot/function/LootingEnchantLootFunction.builder:(Lnet/minecraft/world/loot/UniformLootTableRange;)Lnet/minecraft/world/loot/function/LootingEnchantLootFunction$Builder;
        //   785: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   788: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   791: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //   794: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //   797: iconst_1       
        //   798: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //   801: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   804: getstatic       net/minecraft/item/Items.jl:Lnet/minecraft/item/Item;
        //   807: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   810: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   813: invokestatic    net/minecraft/world/loot/condition/KilledByPlayerLootCondition.builder:()Lnet/minecraft/world/loot/condition/LootCondition$Builder;
        //   816: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withCondition:(Lnet/minecraft/world/loot/condition/LootCondition$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   819: ldc             0.05
        //   821: ldc_w           0.01
        //   824: invokestatic    net/minecraft/world/loot/condition/RandomChanceWithLootingLootCondition.builder:(FF)Lnet/minecraft/world/loot/condition/LootCondition$Builder;
        //   827: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withCondition:(Lnet/minecraft/world/loot/condition/LootCondition$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   830: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //   833: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //   836: aload_0         /* this */
        //   837: getstatic       net/minecraft/entity/EntityType.ELDER_GUARDIAN:Lnet/minecraft/entity/EntityType;
        //   840: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //   843: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //   846: iconst_1       
        //   847: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //   850: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   853: getstatic       net/minecraft/item/Items.oe:Lnet/minecraft/item/Item;
        //   856: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   859: fconst_0       
        //   860: fconst_2       
        //   861: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //   864: invokestatic    net/minecraft/world/loot/function/SetCountLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //   867: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   870: fconst_0       
        //   871: fconst_1       
        //   872: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //   875: invokestatic    net/minecraft/world/loot/function/LootingEnchantLootFunction.builder:(Lnet/minecraft/world/loot/UniformLootTableRange;)Lnet/minecraft/world/loot/function/LootingEnchantLootFunction$Builder;
        //   878: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   881: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   884: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //   887: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //   890: iconst_1       
        //   891: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //   894: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   897: getstatic       net/minecraft/item/Items.lb:Lnet/minecraft/item/Item;
        //   900: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   903: iconst_3       
        //   904: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.setWeight:(I)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   907: fconst_0       
        //   908: fconst_1       
        //   909: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //   912: invokestatic    net/minecraft/world/loot/function/LootingEnchantLootFunction.builder:(Lnet/minecraft/world/loot/UniformLootTableRange;)Lnet/minecraft/world/loot/function/LootingEnchantLootFunction$Builder;
        //   915: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   918: invokestatic    net/minecraft/world/loot/function/FurnaceSmeltLootFunction.builder:()Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //   921: getstatic       net/minecraft/world/loot/context/LootContext$EntityTarget.THIS:Lnet/minecraft/world/loot/context/LootContext$EntityTarget;
        //   924: getstatic       net/minecraft/data/server/EntityLootTableGenerator.a:Lnet/minecraft/predicate/entity/EntityPredicate$Builder;
        //   927: invokestatic    net/minecraft/world/loot/condition/EntityPropertiesLootCondition.builder:(Lnet/minecraft/world/loot/context/LootContext$EntityTarget;Lnet/minecraft/predicate/entity/EntityPredicate$Builder;)Lnet/minecraft/world/loot/condition/LootCondition$Builder;
        //   930: invokevirtual   net/minecraft/world/loot/function/ConditionalLootFunction$Builder.withCondition:(Lnet/minecraft/world/loot/condition/LootCondition$Builder;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //   933: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   936: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   939: getstatic       net/minecraft/item/Items.of:Lnet/minecraft/item/Item;
        //   942: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   945: iconst_2       
        //   946: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.setWeight:(I)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   949: fconst_0       
        //   950: fconst_1       
        //   951: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //   954: invokestatic    net/minecraft/world/loot/function/LootingEnchantLootFunction.builder:(Lnet/minecraft/world/loot/UniformLootTableRange;)Lnet/minecraft/world/loot/function/LootingEnchantLootFunction$Builder;
        //   957: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   960: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   963: invokestatic    net/minecraft/world/loot/entry/EmptyEntry.Serializer:()Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   966: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   969: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //   972: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //   975: iconst_1       
        //   976: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //   979: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   982: getstatic       net/minecraft/block/Blocks.an:Lnet/minecraft/block/Block;
        //   985: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //   988: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   991: invokestatic    net/minecraft/world/loot/condition/KilledByPlayerLootCondition.builder:()Lnet/minecraft/world/loot/condition/LootCondition$Builder;
        //   994: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withCondition:(Lnet/minecraft/world/loot/condition/LootCondition$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //   997: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  1000: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //  1003: iconst_1       
        //  1004: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //  1007: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  1010: getstatic       net/minecraft/world/loot/LootTables.GAMEPLAY_FISHING_FISH:Lnet/minecraft/util/Identifier;
        //  1013: invokestatic    net/minecraft/world/loot/entry/LootTableEntry.builder:(Lnet/minecraft/util/Identifier;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  1016: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  1019: invokestatic    net/minecraft/world/loot/condition/KilledByPlayerLootCondition.builder:()Lnet/minecraft/world/loot/condition/LootCondition$Builder;
        //  1022: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withCondition:(Lnet/minecraft/world/loot/condition/LootCondition$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  1025: ldc_w           0.025
        //  1028: ldc_w           0.01
        //  1031: invokestatic    net/minecraft/world/loot/condition/RandomChanceWithLootingLootCondition.builder:(FF)Lnet/minecraft/world/loot/condition/LootCondition$Builder;
        //  1034: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withCondition:(Lnet/minecraft/world/loot/condition/LootCondition$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  1037: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  1040: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //  1043: aload_0         /* this */
        //  1044: getstatic       net/minecraft/entity/EntityType.ENDER_DRAGON:Lnet/minecraft/entity/EntityType;
        //  1047: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  1050: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //  1053: aload_0         /* this */
        //  1054: getstatic       net/minecraft/entity/EntityType.ENDERMAN:Lnet/minecraft/entity/EntityType;
        //  1057: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  1060: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //  1063: iconst_1       
        //  1064: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //  1067: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  1070: getstatic       net/minecraft/item/Items.mg:Lnet/minecraft/item/Item;
        //  1073: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  1076: fconst_0       
        //  1077: fconst_1       
        //  1078: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  1081: invokestatic    net/minecraft/world/loot/function/SetCountLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //  1084: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  1087: fconst_0       
        //  1088: fconst_1       
        //  1089: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  1092: invokestatic    net/minecraft/world/loot/function/LootingEnchantLootFunction.builder:(Lnet/minecraft/world/loot/UniformLootTableRange;)Lnet/minecraft/world/loot/function/LootingEnchantLootFunction$Builder;
        //  1095: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  1098: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  1101: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  1104: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //  1107: aload_0         /* this */
        //  1108: getstatic       net/minecraft/entity/EntityType.ENDERMITE:Lnet/minecraft/entity/EntityType;
        //  1111: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  1114: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //  1117: aload_0         /* this */
        //  1118: getstatic       net/minecraft/entity/EntityType.EVOKER:Lnet/minecraft/entity/EntityType;
        //  1121: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  1124: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //  1127: iconst_1       
        //  1128: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //  1131: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  1134: getstatic       net/minecraft/item/Items.pd:Lnet/minecraft/item/Item;
        //  1137: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  1140: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  1143: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  1146: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //  1149: iconst_1       
        //  1150: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //  1153: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  1156: getstatic       net/minecraft/item/Items.nF:Lnet/minecraft/item/Item;
        //  1159: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  1162: fconst_0       
        //  1163: fconst_1       
        //  1164: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  1167: invokestatic    net/minecraft/world/loot/function/SetCountLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //  1170: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  1173: fconst_0       
        //  1174: fconst_1       
        //  1175: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  1178: invokestatic    net/minecraft/world/loot/function/LootingEnchantLootFunction.builder:(Lnet/minecraft/world/loot/UniformLootTableRange;)Lnet/minecraft/world/loot/function/LootingEnchantLootFunction$Builder;
        //  1181: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  1184: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  1187: invokestatic    net/minecraft/world/loot/condition/KilledByPlayerLootCondition.builder:()Lnet/minecraft/world/loot/condition/LootCondition$Builder;
        //  1190: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withCondition:(Lnet/minecraft/world/loot/condition/LootCondition$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  1193: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  1196: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //  1199: aload_0         /* this */
        //  1200: getstatic       net/minecraft/entity/EntityType.B:Lnet/minecraft/entity/EntityType;
        //  1203: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  1206: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //  1209: aload_0         /* this */
        //  1210: getstatic       net/minecraft/entity/EntityType.GHAST:Lnet/minecraft/entity/EntityType;
        //  1213: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  1216: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //  1219: iconst_1       
        //  1220: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //  1223: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  1226: getstatic       net/minecraft/item/Items.mi:Lnet/minecraft/item/Item;
        //  1229: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  1232: fconst_0       
        //  1233: fconst_1       
        //  1234: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  1237: invokestatic    net/minecraft/world/loot/function/SetCountLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //  1240: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  1243: fconst_0       
        //  1244: fconst_1       
        //  1245: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  1248: invokestatic    net/minecraft/world/loot/function/LootingEnchantLootFunction.builder:(Lnet/minecraft/world/loot/UniformLootTableRange;)Lnet/minecraft/world/loot/function/LootingEnchantLootFunction$Builder;
        //  1251: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  1254: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  1257: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  1260: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //  1263: iconst_1       
        //  1264: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //  1267: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  1270: getstatic       net/minecraft/item/Items.jI:Lnet/minecraft/item/Item;
        //  1273: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  1276: fconst_0       
        //  1277: fconst_2       
        //  1278: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  1281: invokestatic    net/minecraft/world/loot/function/SetCountLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //  1284: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  1287: fconst_0       
        //  1288: fconst_1       
        //  1289: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  1292: invokestatic    net/minecraft/world/loot/function/LootingEnchantLootFunction.builder:(Lnet/minecraft/world/loot/UniformLootTableRange;)Lnet/minecraft/world/loot/function/LootingEnchantLootFunction$Builder;
        //  1295: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  1298: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  1301: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  1304: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //  1307: aload_0         /* this */
        //  1308: getstatic       net/minecraft/entity/EntityType.GIANT:Lnet/minecraft/entity/EntityType;
        //  1311: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  1314: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //  1317: aload_0         /* this */
        //  1318: getstatic       net/minecraft/entity/EntityType.GUARDIAN:Lnet/minecraft/entity/EntityType;
        //  1321: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  1324: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //  1327: iconst_1       
        //  1328: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //  1331: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  1334: getstatic       net/minecraft/item/Items.oe:Lnet/minecraft/item/Item;
        //  1337: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  1340: fconst_0       
        //  1341: fconst_2       
        //  1342: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  1345: invokestatic    net/minecraft/world/loot/function/SetCountLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //  1348: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  1351: fconst_0       
        //  1352: fconst_1       
        //  1353: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  1356: invokestatic    net/minecraft/world/loot/function/LootingEnchantLootFunction.builder:(Lnet/minecraft/world/loot/UniformLootTableRange;)Lnet/minecraft/world/loot/function/LootingEnchantLootFunction$Builder;
        //  1359: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  1362: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  1365: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  1368: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //  1371: iconst_1       
        //  1372: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //  1375: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  1378: getstatic       net/minecraft/item/Items.lb:Lnet/minecraft/item/Item;
        //  1381: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  1384: iconst_2       
        //  1385: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.setWeight:(I)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  1388: fconst_0       
        //  1389: fconst_1       
        //  1390: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  1393: invokestatic    net/minecraft/world/loot/function/LootingEnchantLootFunction.builder:(Lnet/minecraft/world/loot/UniformLootTableRange;)Lnet/minecraft/world/loot/function/LootingEnchantLootFunction$Builder;
        //  1396: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  1399: invokestatic    net/minecraft/world/loot/function/FurnaceSmeltLootFunction.builder:()Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //  1402: getstatic       net/minecraft/world/loot/context/LootContext$EntityTarget.THIS:Lnet/minecraft/world/loot/context/LootContext$EntityTarget;
        //  1405: getstatic       net/minecraft/data/server/EntityLootTableGenerator.a:Lnet/minecraft/predicate/entity/EntityPredicate$Builder;
        //  1408: invokestatic    net/minecraft/world/loot/condition/EntityPropertiesLootCondition.builder:(Lnet/minecraft/world/loot/context/LootContext$EntityTarget;Lnet/minecraft/predicate/entity/EntityPredicate$Builder;)Lnet/minecraft/world/loot/condition/LootCondition$Builder;
        //  1411: invokevirtual   net/minecraft/world/loot/function/ConditionalLootFunction$Builder.withCondition:(Lnet/minecraft/world/loot/condition/LootCondition$Builder;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //  1414: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  1417: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  1420: getstatic       net/minecraft/item/Items.of:Lnet/minecraft/item/Item;
        //  1423: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  1426: iconst_2       
        //  1427: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.setWeight:(I)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  1430: fconst_0       
        //  1431: fconst_1       
        //  1432: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  1435: invokestatic    net/minecraft/world/loot/function/LootingEnchantLootFunction.builder:(Lnet/minecraft/world/loot/UniformLootTableRange;)Lnet/minecraft/world/loot/function/LootingEnchantLootFunction$Builder;
        //  1438: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  1441: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  1444: invokestatic    net/minecraft/world/loot/entry/EmptyEntry.Serializer:()Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  1447: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  1450: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  1453: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //  1456: iconst_1       
        //  1457: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //  1460: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  1463: getstatic       net/minecraft/world/loot/LootTables.GAMEPLAY_FISHING_FISH:Lnet/minecraft/util/Identifier;
        //  1466: invokestatic    net/minecraft/world/loot/entry/LootTableEntry.builder:(Lnet/minecraft/util/Identifier;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  1469: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  1472: invokestatic    net/minecraft/world/loot/condition/KilledByPlayerLootCondition.builder:()Lnet/minecraft/world/loot/condition/LootCondition$Builder;
        //  1475: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withCondition:(Lnet/minecraft/world/loot/condition/LootCondition$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  1478: ldc_w           0.025
        //  1481: ldc_w           0.01
        //  1484: invokestatic    net/minecraft/world/loot/condition/RandomChanceWithLootingLootCondition.builder:(FF)Lnet/minecraft/world/loot/condition/LootCondition$Builder;
        //  1487: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withCondition:(Lnet/minecraft/world/loot/condition/LootCondition$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  1490: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  1493: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //  1496: aload_0         /* this */
        //  1497: getstatic       net/minecraft/entity/EntityType.HORSE:Lnet/minecraft/entity/EntityType;
        //  1500: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  1503: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //  1506: iconst_1       
        //  1507: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //  1510: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  1513: getstatic       net/minecraft/item/Items.kF:Lnet/minecraft/item/Item;
        //  1516: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  1519: fconst_0       
        //  1520: fconst_2       
        //  1521: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  1524: invokestatic    net/minecraft/world/loot/function/SetCountLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //  1527: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  1530: fconst_0       
        //  1531: fconst_1       
        //  1532: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  1535: invokestatic    net/minecraft/world/loot/function/LootingEnchantLootFunction.builder:(Lnet/minecraft/world/loot/UniformLootTableRange;)Lnet/minecraft/world/loot/function/LootingEnchantLootFunction$Builder;
        //  1538: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  1541: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  1544: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  1547: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //  1550: aload_0         /* this */
        //  1551: getstatic       net/minecraft/entity/EntityType.HUSK:Lnet/minecraft/entity/EntityType;
        //  1554: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  1557: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //  1560: iconst_1       
        //  1561: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //  1564: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  1567: getstatic       net/minecraft/item/Items.mf:Lnet/minecraft/item/Item;
        //  1570: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  1573: fconst_0       
        //  1574: fconst_2       
        //  1575: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  1578: invokestatic    net/minecraft/world/loot/function/SetCountLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //  1581: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  1584: fconst_0       
        //  1585: fconst_1       
        //  1586: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  1589: invokestatic    net/minecraft/world/loot/function/LootingEnchantLootFunction.builder:(Lnet/minecraft/world/loot/UniformLootTableRange;)Lnet/minecraft/world/loot/function/LootingEnchantLootFunction$Builder;
        //  1592: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  1595: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  1598: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  1601: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //  1604: iconst_1       
        //  1605: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //  1608: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  1611: getstatic       net/minecraft/item/Items.jk:Lnet/minecraft/item/Item;
        //  1614: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  1617: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  1620: getstatic       net/minecraft/item/Items.nI:Lnet/minecraft/item/Item;
        //  1623: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  1626: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  1629: getstatic       net/minecraft/item/Items.nJ:Lnet/minecraft/item/Item;
        //  1632: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  1635: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  1638: invokestatic    net/minecraft/world/loot/condition/KilledByPlayerLootCondition.builder:()Lnet/minecraft/world/loot/condition/LootCondition$Builder;
        //  1641: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withCondition:(Lnet/minecraft/world/loot/condition/LootCondition$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  1644: ldc_w           0.025
        //  1647: ldc_w           0.01
        //  1650: invokestatic    net/minecraft/world/loot/condition/RandomChanceWithLootingLootCondition.builder:(FF)Lnet/minecraft/world/loot/condition/LootCondition$Builder;
        //  1653: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withCondition:(Lnet/minecraft/world/loot/condition/LootCondition$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  1656: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  1659: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //  1662: aload_0         /* this */
        //  1663: getstatic       net/minecraft/entity/EntityType.RAVAGER:Lnet/minecraft/entity/EntityType;
        //  1666: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  1669: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //  1672: iconst_1       
        //  1673: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //  1676: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  1679: getstatic       net/minecraft/item/Items.kB:Lnet/minecraft/item/Item;
        //  1682: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  1685: iconst_1       
        //  1686: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //  1689: invokestatic    net/minecraft/world/loot/function/SetCountLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //  1692: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  1695: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  1698: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  1701: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //  1704: aload_0         /* this */
        //  1705: getstatic       net/minecraft/entity/EntityType.ILLUSIONER:Lnet/minecraft/entity/EntityType;
        //  1708: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  1711: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //  1714: aload_0         /* this */
        //  1715: getstatic       net/minecraft/entity/EntityType.IRON_GOLEM:Lnet/minecraft/entity/EntityType;
        //  1718: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  1721: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //  1724: iconst_1       
        //  1725: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //  1728: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  1731: getstatic       net/minecraft/block/Blocks.bp:Lnet/minecraft/block/Block;
        //  1734: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  1737: fconst_0       
        //  1738: fconst_2       
        //  1739: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  1742: invokestatic    net/minecraft/world/loot/function/SetCountLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //  1745: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  1748: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  1751: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  1754: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //  1757: iconst_1       
        //  1758: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //  1761: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  1764: getstatic       net/minecraft/item/Items.jk:Lnet/minecraft/item/Item;
        //  1767: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  1770: ldc             3.0
        //  1772: ldc_w           5.0
        //  1775: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  1778: invokestatic    net/minecraft/world/loot/function/SetCountLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //  1781: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  1784: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  1787: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  1790: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //  1793: aload_0         /* this */
        //  1794: getstatic       net/minecraft/entity/EntityType.LLAMA:Lnet/minecraft/entity/EntityType;
        //  1797: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  1800: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //  1803: iconst_1       
        //  1804: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //  1807: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  1810: getstatic       net/minecraft/item/Items.kF:Lnet/minecraft/item/Item;
        //  1813: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  1816: fconst_0       
        //  1817: fconst_2       
        //  1818: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  1821: invokestatic    net/minecraft/world/loot/function/SetCountLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //  1824: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  1827: fconst_0       
        //  1828: fconst_1       
        //  1829: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  1832: invokestatic    net/minecraft/world/loot/function/LootingEnchantLootFunction.builder:(Lnet/minecraft/world/loot/UniformLootTableRange;)Lnet/minecraft/world/loot/function/LootingEnchantLootFunction$Builder;
        //  1835: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  1838: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  1841: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  1844: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //  1847: aload_0         /* this */
        //  1848: getstatic       net/minecraft/entity/EntityType.MAGMA_CUBE:Lnet/minecraft/entity/EntityType;
        //  1851: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  1854: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //  1857: iconst_1       
        //  1858: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //  1861: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  1864: getstatic       net/minecraft/item/Items.mq:Lnet/minecraft/item/Item;
        //  1867: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  1870: ldc_w           -2.0
        //  1873: fconst_1       
        //  1874: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  1877: invokestatic    net/minecraft/world/loot/function/SetCountLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //  1880: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  1883: fconst_0       
        //  1884: fconst_1       
        //  1885: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  1888: invokestatic    net/minecraft/world/loot/function/LootingEnchantLootFunction.builder:(Lnet/minecraft/world/loot/UniformLootTableRange;)Lnet/minecraft/world/loot/function/LootingEnchantLootFunction$Builder;
        //  1891: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  1894: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  1897: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  1900: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //  1903: aload_0         /* this */
        //  1904: getstatic       net/minecraft/entity/EntityType.MULE:Lnet/minecraft/entity/EntityType;
        //  1907: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  1910: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //  1913: iconst_1       
        //  1914: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //  1917: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  1920: getstatic       net/minecraft/item/Items.kF:Lnet/minecraft/item/Item;
        //  1923: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  1926: fconst_0       
        //  1927: fconst_2       
        //  1928: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  1931: invokestatic    net/minecraft/world/loot/function/SetCountLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //  1934: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  1937: fconst_0       
        //  1938: fconst_1       
        //  1939: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  1942: invokestatic    net/minecraft/world/loot/function/LootingEnchantLootFunction.builder:(Lnet/minecraft/world/loot/UniformLootTableRange;)Lnet/minecraft/world/loot/function/LootingEnchantLootFunction$Builder;
        //  1945: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  1948: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  1951: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  1954: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //  1957: aload_0         /* this */
        //  1958: getstatic       net/minecraft/entity/EntityType.MOOSHROOM:Lnet/minecraft/entity/EntityType;
        //  1961: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  1964: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //  1967: iconst_1       
        //  1968: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //  1971: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  1974: getstatic       net/minecraft/item/Items.kF:Lnet/minecraft/item/Item;
        //  1977: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  1980: fconst_0       
        //  1981: fconst_2       
        //  1982: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  1985: invokestatic    net/minecraft/world/loot/function/SetCountLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //  1988: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  1991: fconst_0       
        //  1992: fconst_1       
        //  1993: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  1996: invokestatic    net/minecraft/world/loot/function/LootingEnchantLootFunction.builder:(Lnet/minecraft/world/loot/UniformLootTableRange;)Lnet/minecraft/world/loot/function/LootingEnchantLootFunction$Builder;
        //  1999: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  2002: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  2005: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  2008: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //  2011: iconst_1       
        //  2012: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //  2015: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  2018: getstatic       net/minecraft/item/Items.mb:Lnet/minecraft/item/Item;
        //  2021: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  2024: fconst_1       
        //  2025: ldc             3.0
        //  2027: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  2030: invokestatic    net/minecraft/world/loot/function/SetCountLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //  2033: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  2036: invokestatic    net/minecraft/world/loot/function/FurnaceSmeltLootFunction.builder:()Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //  2039: getstatic       net/minecraft/world/loot/context/LootContext$EntityTarget.THIS:Lnet/minecraft/world/loot/context/LootContext$EntityTarget;
        //  2042: getstatic       net/minecraft/data/server/EntityLootTableGenerator.a:Lnet/minecraft/predicate/entity/EntityPredicate$Builder;
        //  2045: invokestatic    net/minecraft/world/loot/condition/EntityPropertiesLootCondition.builder:(Lnet/minecraft/world/loot/context/LootContext$EntityTarget;Lnet/minecraft/predicate/entity/EntityPredicate$Builder;)Lnet/minecraft/world/loot/condition/LootCondition$Builder;
        //  2048: invokevirtual   net/minecraft/world/loot/function/ConditionalLootFunction$Builder.withCondition:(Lnet/minecraft/world/loot/condition/LootCondition$Builder;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //  2051: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  2054: fconst_0       
        //  2055: fconst_1       
        //  2056: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  2059: invokestatic    net/minecraft/world/loot/function/LootingEnchantLootFunction.builder:(Lnet/minecraft/world/loot/UniformLootTableRange;)Lnet/minecraft/world/loot/function/LootingEnchantLootFunction$Builder;
        //  2062: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  2065: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  2068: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  2071: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //  2074: aload_0         /* this */
        //  2075: getstatic       net/minecraft/entity/EntityType.OCELOT:Lnet/minecraft/entity/EntityType;
        //  2078: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  2081: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //  2084: aload_0         /* this */
        //  2085: getstatic       net/minecraft/entity/EntityType.PANDA:Lnet/minecraft/entity/EntityType;
        //  2088: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  2091: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //  2094: iconst_1       
        //  2095: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //  2098: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  2101: getstatic       net/minecraft/block/Blocks.kQ:Lnet/minecraft/block/Block;
        //  2104: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  2107: iconst_1       
        //  2108: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //  2111: invokestatic    net/minecraft/world/loot/function/SetCountLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //  2114: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  2117: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  2120: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  2123: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //  2126: aload_0         /* this */
        //  2127: getstatic       net/minecraft/entity/EntityType.PARROT:Lnet/minecraft/entity/EntityType;
        //  2130: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  2133: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //  2136: iconst_1       
        //  2137: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //  2140: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  2143: getstatic       net/minecraft/item/Items.jH:Lnet/minecraft/item/Item;
        //  2146: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  2149: fconst_1       
        //  2150: fconst_2       
        //  2151: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  2154: invokestatic    net/minecraft/world/loot/function/SetCountLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //  2157: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  2160: fconst_0       
        //  2161: fconst_1       
        //  2162: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  2165: invokestatic    net/minecraft/world/loot/function/LootingEnchantLootFunction.builder:(Lnet/minecraft/world/loot/UniformLootTableRange;)Lnet/minecraft/world/loot/function/LootingEnchantLootFunction$Builder;
        //  2168: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  2171: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  2174: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  2177: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //  2180: aload_0         /* this */
        //  2181: getstatic       net/minecraft/entity/EntityType.PHANTOM:Lnet/minecraft/entity/EntityType;
        //  2184: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  2187: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //  2190: iconst_1       
        //  2191: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //  2194: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  2197: getstatic       net/minecraft/item/Items.pv:Lnet/minecraft/item/Item;
        //  2200: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  2203: fconst_0       
        //  2204: fconst_1       
        //  2205: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  2208: invokestatic    net/minecraft/world/loot/function/SetCountLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //  2211: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  2214: fconst_0       
        //  2215: fconst_1       
        //  2216: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  2219: invokestatic    net/minecraft/world/loot/function/LootingEnchantLootFunction.builder:(Lnet/minecraft/world/loot/UniformLootTableRange;)Lnet/minecraft/world/loot/function/LootingEnchantLootFunction$Builder;
        //  2222: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  2225: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  2228: invokestatic    net/minecraft/world/loot/condition/KilledByPlayerLootCondition.builder:()Lnet/minecraft/world/loot/condition/LootCondition$Builder;
        //  2231: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withCondition:(Lnet/minecraft/world/loot/condition/LootCondition$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  2234: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  2237: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //  2240: aload_0         /* this */
        //  2241: getstatic       net/minecraft/entity/EntityType.PIG:Lnet/minecraft/entity/EntityType;
        //  2244: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  2247: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //  2250: iconst_1       
        //  2251: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //  2254: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  2257: getstatic       net/minecraft/item/Items.km:Lnet/minecraft/item/Item;
        //  2260: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  2263: fconst_1       
        //  2264: ldc             3.0
        //  2266: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  2269: invokestatic    net/minecraft/world/loot/function/SetCountLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //  2272: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  2275: invokestatic    net/minecraft/world/loot/function/FurnaceSmeltLootFunction.builder:()Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //  2278: getstatic       net/minecraft/world/loot/context/LootContext$EntityTarget.THIS:Lnet/minecraft/world/loot/context/LootContext$EntityTarget;
        //  2281: getstatic       net/minecraft/data/server/EntityLootTableGenerator.a:Lnet/minecraft/predicate/entity/EntityPredicate$Builder;
        //  2284: invokestatic    net/minecraft/world/loot/condition/EntityPropertiesLootCondition.builder:(Lnet/minecraft/world/loot/context/LootContext$EntityTarget;Lnet/minecraft/predicate/entity/EntityPredicate$Builder;)Lnet/minecraft/world/loot/condition/LootCondition$Builder;
        //  2287: invokevirtual   net/minecraft/world/loot/function/ConditionalLootFunction$Builder.withCondition:(Lnet/minecraft/world/loot/condition/LootCondition$Builder;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //  2290: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  2293: fconst_0       
        //  2294: fconst_1       
        //  2295: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  2298: invokestatic    net/minecraft/world/loot/function/LootingEnchantLootFunction.builder:(Lnet/minecraft/world/loot/UniformLootTableRange;)Lnet/minecraft/world/loot/function/LootingEnchantLootFunction$Builder;
        //  2301: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  2304: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  2307: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  2310: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //  2313: aload_0         /* this */
        //  2314: getstatic       net/minecraft/entity/EntityType.PILLAGER:Lnet/minecraft/entity/EntityType;
        //  2317: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  2320: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //  2323: aload_0         /* this */
        //  2324: getstatic       net/minecraft/entity/EntityType.PLAYER:Lnet/minecraft/entity/EntityType;
        //  2327: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  2330: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //  2333: aload_0         /* this */
        //  2334: getstatic       net/minecraft/entity/EntityType.POLAR_BEAR:Lnet/minecraft/entity/EntityType;
        //  2337: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  2340: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //  2343: iconst_1       
        //  2344: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //  2347: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  2350: getstatic       net/minecraft/item/Items.lb:Lnet/minecraft/item/Item;
        //  2353: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  2356: iconst_3       
        //  2357: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.setWeight:(I)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  2360: fconst_0       
        //  2361: fconst_2       
        //  2362: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  2365: invokestatic    net/minecraft/world/loot/function/SetCountLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //  2368: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  2371: fconst_0       
        //  2372: fconst_1       
        //  2373: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  2376: invokestatic    net/minecraft/world/loot/function/LootingEnchantLootFunction.builder:(Lnet/minecraft/world/loot/UniformLootTableRange;)Lnet/minecraft/world/loot/function/LootingEnchantLootFunction$Builder;
        //  2379: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  2382: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  2385: getstatic       net/minecraft/item/Items.lc:Lnet/minecraft/item/Item;
        //  2388: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  2391: fconst_0       
        //  2392: fconst_2       
        //  2393: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  2396: invokestatic    net/minecraft/world/loot/function/SetCountLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //  2399: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  2402: fconst_0       
        //  2403: fconst_1       
        //  2404: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  2407: invokestatic    net/minecraft/world/loot/function/LootingEnchantLootFunction.builder:(Lnet/minecraft/world/loot/UniformLootTableRange;)Lnet/minecraft/world/loot/function/LootingEnchantLootFunction$Builder;
        //  2410: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  2413: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  2416: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  2419: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //  2422: aload_0         /* this */
        //  2423: getstatic       net/minecraft/entity/EntityType.PUFFERFISH:Lnet/minecraft/entity/EntityType;
        //  2426: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  2429: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //  2432: iconst_1       
        //  2433: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //  2436: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  2439: getstatic       net/minecraft/item/Items.le:Lnet/minecraft/item/Item;
        //  2442: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  2445: iconst_1       
        //  2446: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //  2449: invokestatic    net/minecraft/world/loot/function/SetCountLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //  2452: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  2455: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  2458: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  2461: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //  2464: iconst_1       
        //  2465: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //  2468: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  2471: getstatic       net/minecraft/item/Items.lw:Lnet/minecraft/item/Item;
        //  2474: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  2477: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  2480: ldc             0.05
        //  2482: invokestatic    net/minecraft/world/loot/condition/RandomChanceLootCondition.builder:(F)Lnet/minecraft/world/loot/condition/LootCondition$Builder;
        //  2485: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withCondition:(Lnet/minecraft/world/loot/condition/LootCondition$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  2488: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  2491: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //  2494: aload_0         /* this */
        //  2495: getstatic       net/minecraft/entity/EntityType.RABBIT:Lnet/minecraft/entity/EntityType;
        //  2498: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  2501: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //  2504: iconst_1       
        //  2505: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //  2508: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  2511: getstatic       net/minecraft/item/Items.ok:Lnet/minecraft/item/Item;
        //  2514: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  2517: fconst_0       
        //  2518: fconst_1       
        //  2519: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  2522: invokestatic    net/minecraft/world/loot/function/SetCountLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //  2525: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  2528: fconst_0       
        //  2529: fconst_1       
        //  2530: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  2533: invokestatic    net/minecraft/world/loot/function/LootingEnchantLootFunction.builder:(Lnet/minecraft/world/loot/UniformLootTableRange;)Lnet/minecraft/world/loot/function/LootingEnchantLootFunction$Builder;
        //  2536: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  2539: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  2542: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  2545: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //  2548: iconst_1       
        //  2549: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //  2552: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  2555: getstatic       net/minecraft/item/Items.og:Lnet/minecraft/item/Item;
        //  2558: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  2561: fconst_0       
        //  2562: fconst_1       
        //  2563: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  2566: invokestatic    net/minecraft/world/loot/function/SetCountLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //  2569: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  2572: invokestatic    net/minecraft/world/loot/function/FurnaceSmeltLootFunction.builder:()Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //  2575: getstatic       net/minecraft/world/loot/context/LootContext$EntityTarget.THIS:Lnet/minecraft/world/loot/context/LootContext$EntityTarget;
        //  2578: getstatic       net/minecraft/data/server/EntityLootTableGenerator.a:Lnet/minecraft/predicate/entity/EntityPredicate$Builder;
        //  2581: invokestatic    net/minecraft/world/loot/condition/EntityPropertiesLootCondition.builder:(Lnet/minecraft/world/loot/context/LootContext$EntityTarget;Lnet/minecraft/predicate/entity/EntityPredicate$Builder;)Lnet/minecraft/world/loot/condition/LootCondition$Builder;
        //  2584: invokevirtual   net/minecraft/world/loot/function/ConditionalLootFunction$Builder.withCondition:(Lnet/minecraft/world/loot/condition/LootCondition$Builder;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //  2587: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  2590: fconst_0       
        //  2591: fconst_1       
        //  2592: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  2595: invokestatic    net/minecraft/world/loot/function/LootingEnchantLootFunction.builder:(Lnet/minecraft/world/loot/UniformLootTableRange;)Lnet/minecraft/world/loot/function/LootingEnchantLootFunction$Builder;
        //  2598: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  2601: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  2604: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  2607: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //  2610: iconst_1       
        //  2611: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //  2614: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  2617: getstatic       net/minecraft/item/Items.oj:Lnet/minecraft/item/Item;
        //  2620: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  2623: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  2626: invokestatic    net/minecraft/world/loot/condition/KilledByPlayerLootCondition.builder:()Lnet/minecraft/world/loot/condition/LootCondition$Builder;
        //  2629: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withCondition:(Lnet/minecraft/world/loot/condition/LootCondition$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  2632: ldc_w           0.1
        //  2635: ldc_w           0.03
        //  2638: invokestatic    net/minecraft/world/loot/condition/RandomChanceWithLootingLootCondition.builder:(FF)Lnet/minecraft/world/loot/condition/LootCondition$Builder;
        //  2641: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withCondition:(Lnet/minecraft/world/loot/condition/LootCondition$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  2644: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  2647: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //  2650: aload_0         /* this */
        //  2651: getstatic       net/minecraft/entity/EntityType.SALMON:Lnet/minecraft/entity/EntityType;
        //  2654: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  2657: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //  2660: iconst_1       
        //  2661: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //  2664: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  2667: getstatic       net/minecraft/item/Items.lc:Lnet/minecraft/item/Item;
        //  2670: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  2673: invokestatic    net/minecraft/world/loot/function/FurnaceSmeltLootFunction.builder:()Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //  2676: getstatic       net/minecraft/world/loot/context/LootContext$EntityTarget.THIS:Lnet/minecraft/world/loot/context/LootContext$EntityTarget;
        //  2679: getstatic       net/minecraft/data/server/EntityLootTableGenerator.a:Lnet/minecraft/predicate/entity/EntityPredicate$Builder;
        //  2682: invokestatic    net/minecraft/world/loot/condition/EntityPropertiesLootCondition.builder:(Lnet/minecraft/world/loot/context/LootContext$EntityTarget;Lnet/minecraft/predicate/entity/EntityPredicate$Builder;)Lnet/minecraft/world/loot/condition/LootCondition$Builder;
        //  2685: invokevirtual   net/minecraft/world/loot/function/ConditionalLootFunction$Builder.withCondition:(Lnet/minecraft/world/loot/condition/LootCondition$Builder;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //  2688: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  2691: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  2694: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  2697: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //  2700: iconst_1       
        //  2701: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //  2704: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  2707: getstatic       net/minecraft/item/Items.lw:Lnet/minecraft/item/Item;
        //  2710: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  2713: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  2716: ldc             0.05
        //  2718: invokestatic    net/minecraft/world/loot/condition/RandomChanceLootCondition.builder:(F)Lnet/minecraft/world/loot/condition/LootCondition$Builder;
        //  2721: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withCondition:(Lnet/minecraft/world/loot/condition/LootCondition$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  2724: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  2727: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //  2730: aload_0         /* this */
        //  2731: getstatic       net/minecraft/entity/EntityType.SHEEP:Lnet/minecraft/entity/EntityType;
        //  2734: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  2737: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //  2740: iconst_1       
        //  2741: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //  2744: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  2747: getstatic       net/minecraft/item/Items.ot:Lnet/minecraft/item/Item;
        //  2750: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  2753: fconst_1       
        //  2754: fconst_2       
        //  2755: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  2758: invokestatic    net/minecraft/world/loot/function/SetCountLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //  2761: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  2764: invokestatic    net/minecraft/world/loot/function/FurnaceSmeltLootFunction.builder:()Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //  2767: getstatic       net/minecraft/world/loot/context/LootContext$EntityTarget.THIS:Lnet/minecraft/world/loot/context/LootContext$EntityTarget;
        //  2770: getstatic       net/minecraft/data/server/EntityLootTableGenerator.a:Lnet/minecraft/predicate/entity/EntityPredicate$Builder;
        //  2773: invokestatic    net/minecraft/world/loot/condition/EntityPropertiesLootCondition.builder:(Lnet/minecraft/world/loot/context/LootContext$EntityTarget;Lnet/minecraft/predicate/entity/EntityPredicate$Builder;)Lnet/minecraft/world/loot/condition/LootCondition$Builder;
        //  2776: invokevirtual   net/minecraft/world/loot/function/ConditionalLootFunction$Builder.withCondition:(Lnet/minecraft/world/loot/condition/LootCondition$Builder;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //  2779: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  2782: fconst_0       
        //  2783: fconst_1       
        //  2784: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  2787: invokestatic    net/minecraft/world/loot/function/LootingEnchantLootFunction.builder:(Lnet/minecraft/world/loot/UniformLootTableRange;)Lnet/minecraft/world/loot/function/LootingEnchantLootFunction$Builder;
        //  2790: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  2793: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  2796: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  2799: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //  2802: aload_0         /* this */
        //  2803: getstatic       net/minecraft/world/loot/LootTables.ENTITY_SHEEP_BLACK:Lnet/minecraft/util/Identifier;
        //  2806: getstatic       net/minecraft/block/Blocks.bm:Lnet/minecraft/block/Block;
        //  2809: invokestatic    net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  2812: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/util/Identifier;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //  2815: aload_0         /* this */
        //  2816: getstatic       net/minecraft/world/loot/LootTables.ENTITY_SHEEP_BLUE:Lnet/minecraft/util/Identifier;
        //  2819: getstatic       net/minecraft/block/Blocks.bi:Lnet/minecraft/block/Block;
        //  2822: invokestatic    net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  2825: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/util/Identifier;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //  2828: aload_0         /* this */
        //  2829: getstatic       net/minecraft/world/loot/LootTables.ENTITY_SHEEP_BROWN:Lnet/minecraft/util/Identifier;
        //  2832: getstatic       net/minecraft/block/Blocks.bj:Lnet/minecraft/block/Block;
        //  2835: invokestatic    net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  2838: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/util/Identifier;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //  2841: aload_0         /* this */
        //  2842: getstatic       net/minecraft/world/loot/LootTables.ENTITY_SHEEP_CYAN:Lnet/minecraft/util/Identifier;
        //  2845: getstatic       net/minecraft/block/Blocks.bg:Lnet/minecraft/block/Block;
        //  2848: invokestatic    net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  2851: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/util/Identifier;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //  2854: aload_0         /* this */
        //  2855: getstatic       net/minecraft/world/loot/LootTables.ENTITY_SHEEP_GRAY:Lnet/minecraft/util/Identifier;
        //  2858: getstatic       net/minecraft/block/Blocks.be:Lnet/minecraft/block/Block;
        //  2861: invokestatic    net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  2864: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/util/Identifier;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //  2867: aload_0         /* this */
        //  2868: getstatic       net/minecraft/world/loot/LootTables.ENTITY_SHEEP_GREEN:Lnet/minecraft/util/Identifier;
        //  2871: getstatic       net/minecraft/block/Blocks.bk:Lnet/minecraft/block/Block;
        //  2874: invokestatic    net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  2877: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/util/Identifier;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //  2880: aload_0         /* this */
        //  2881: getstatic       net/minecraft/world/loot/LootTables.ENTITY_SHEEP_LIGHT_BLUE:Lnet/minecraft/util/Identifier;
        //  2884: getstatic       net/minecraft/block/Blocks.ba:Lnet/minecraft/block/Block;
        //  2887: invokestatic    net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  2890: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/util/Identifier;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //  2893: aload_0         /* this */
        //  2894: getstatic       net/minecraft/world/loot/LootTables.ENTITY_SHEEP_LIGHT_GRAY:Lnet/minecraft/util/Identifier;
        //  2897: getstatic       net/minecraft/block/Blocks.bf:Lnet/minecraft/block/Block;
        //  2900: invokestatic    net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  2903: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/util/Identifier;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //  2906: aload_0         /* this */
        //  2907: getstatic       net/minecraft/world/loot/LootTables.ENTITY_SHEEP_LIME:Lnet/minecraft/util/Identifier;
        //  2910: getstatic       net/minecraft/block/Blocks.bc:Lnet/minecraft/block/Block;
        //  2913: invokestatic    net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  2916: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/util/Identifier;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //  2919: aload_0         /* this */
        //  2920: getstatic       net/minecraft/world/loot/LootTables.ENTITY_SHEEP_MAGENTA:Lnet/minecraft/util/Identifier;
        //  2923: getstatic       net/minecraft/block/Blocks.aZ:Lnet/minecraft/block/Block;
        //  2926: invokestatic    net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  2929: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/util/Identifier;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //  2932: aload_0         /* this */
        //  2933: getstatic       net/minecraft/world/loot/LootTables.ENTITY_SHEEP_ORANGE:Lnet/minecraft/util/Identifier;
        //  2936: getstatic       net/minecraft/block/Blocks.aY:Lnet/minecraft/block/Block;
        //  2939: invokestatic    net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  2942: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/util/Identifier;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //  2945: aload_0         /* this */
        //  2946: getstatic       net/minecraft/world/loot/LootTables.ENTITY_SHEEP_PINK:Lnet/minecraft/util/Identifier;
        //  2949: getstatic       net/minecraft/block/Blocks.bd:Lnet/minecraft/block/Block;
        //  2952: invokestatic    net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  2955: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/util/Identifier;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //  2958: aload_0         /* this */
        //  2959: getstatic       net/minecraft/world/loot/LootTables.ENTITY_SHEEP_PURPLE:Lnet/minecraft/util/Identifier;
        //  2962: getstatic       net/minecraft/block/Blocks.bh:Lnet/minecraft/block/Block;
        //  2965: invokestatic    net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  2968: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/util/Identifier;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //  2971: aload_0         /* this */
        //  2972: getstatic       net/minecraft/world/loot/LootTables.ENTITY_SHEEP_RED:Lnet/minecraft/util/Identifier;
        //  2975: getstatic       net/minecraft/block/Blocks.bl:Lnet/minecraft/block/Block;
        //  2978: invokestatic    net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  2981: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/util/Identifier;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //  2984: aload_0         /* this */
        //  2985: getstatic       net/minecraft/world/loot/LootTables.ENTITY_SHEEP_WHITE:Lnet/minecraft/util/Identifier;
        //  2988: getstatic       net/minecraft/block/Blocks.aX:Lnet/minecraft/block/Block;
        //  2991: invokestatic    net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  2994: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/util/Identifier;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //  2997: aload_0         /* this */
        //  2998: getstatic       net/minecraft/world/loot/LootTables.ENTITY_SHEEP_YELLOW:Lnet/minecraft/util/Identifier;
        //  3001: getstatic       net/minecraft/block/Blocks.bb:Lnet/minecraft/block/Block;
        //  3004: invokestatic    net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  3007: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/util/Identifier;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //  3010: aload_0         /* this */
        //  3011: getstatic       net/minecraft/entity/EntityType.SHULKER:Lnet/minecraft/entity/EntityType;
        //  3014: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  3017: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //  3020: iconst_1       
        //  3021: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //  3024: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  3027: getstatic       net/minecraft/item/Items.pe:Lnet/minecraft/item/Item;
        //  3030: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  3033: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  3036: ldc_w           0.5
        //  3039: ldc_w           0.0625
        //  3042: invokestatic    net/minecraft/world/loot/condition/RandomChanceWithLootingLootCondition.builder:(FF)Lnet/minecraft/world/loot/condition/LootCondition$Builder;
        //  3045: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withCondition:(Lnet/minecraft/world/loot/condition/LootCondition$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  3048: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  3051: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //  3054: aload_0         /* this */
        //  3055: getstatic       net/minecraft/entity/EntityType.SILVERFISH:Lnet/minecraft/entity/EntityType;
        //  3058: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  3061: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //  3064: aload_0         /* this */
        //  3065: getstatic       net/minecraft/entity/EntityType.SKELETON:Lnet/minecraft/entity/EntityType;
        //  3068: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  3071: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //  3074: iconst_1       
        //  3075: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //  3078: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  3081: getstatic       net/minecraft/item/Items.jg:Lnet/minecraft/item/Item;
        //  3084: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  3087: fconst_0       
        //  3088: fconst_2       
        //  3089: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  3092: invokestatic    net/minecraft/world/loot/function/SetCountLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //  3095: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  3098: fconst_0       
        //  3099: fconst_1       
        //  3100: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  3103: invokestatic    net/minecraft/world/loot/function/LootingEnchantLootFunction.builder:(Lnet/minecraft/world/loot/UniformLootTableRange;)Lnet/minecraft/world/loot/function/LootingEnchantLootFunction$Builder;
        //  3106: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  3109: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  3112: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  3115: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //  3118: iconst_1       
        //  3119: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //  3122: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  3125: getstatic       net/minecraft/item/Items.lB:Lnet/minecraft/item/Item;
        //  3128: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  3131: fconst_0       
        //  3132: fconst_2       
        //  3133: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  3136: invokestatic    net/minecraft/world/loot/function/SetCountLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //  3139: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  3142: fconst_0       
        //  3143: fconst_1       
        //  3144: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  3147: invokestatic    net/minecraft/world/loot/function/LootingEnchantLootFunction.builder:(Lnet/minecraft/world/loot/UniformLootTableRange;)Lnet/minecraft/world/loot/function/LootingEnchantLootFunction$Builder;
        //  3150: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  3153: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  3156: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  3159: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //  3162: aload_0         /* this */
        //  3163: getstatic       net/minecraft/entity/EntityType.SKELETON_HORSE:Lnet/minecraft/entity/EntityType;
        //  3166: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  3169: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //  3172: iconst_1       
        //  3173: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //  3176: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  3179: getstatic       net/minecraft/item/Items.lB:Lnet/minecraft/item/Item;
        //  3182: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  3185: fconst_0       
        //  3186: fconst_2       
        //  3187: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  3190: invokestatic    net/minecraft/world/loot/function/SetCountLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //  3193: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  3196: fconst_0       
        //  3197: fconst_1       
        //  3198: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  3201: invokestatic    net/minecraft/world/loot/function/LootingEnchantLootFunction.builder:(Lnet/minecraft/world/loot/UniformLootTableRange;)Lnet/minecraft/world/loot/function/LootingEnchantLootFunction$Builder;
        //  3204: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  3207: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  3210: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  3213: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //  3216: aload_0         /* this */
        //  3217: getstatic       net/minecraft/entity/EntityType.SLIME:Lnet/minecraft/entity/EntityType;
        //  3220: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  3223: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //  3226: iconst_1       
        //  3227: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //  3230: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  3233: getstatic       net/minecraft/item/Items.kT:Lnet/minecraft/item/Item;
        //  3236: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  3239: fconst_0       
        //  3240: fconst_2       
        //  3241: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  3244: invokestatic    net/minecraft/world/loot/function/SetCountLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //  3247: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  3250: fconst_0       
        //  3251: fconst_1       
        //  3252: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  3255: invokestatic    net/minecraft/world/loot/function/LootingEnchantLootFunction.builder:(Lnet/minecraft/world/loot/UniformLootTableRange;)Lnet/minecraft/world/loot/function/LootingEnchantLootFunction$Builder;
        //  3258: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  3261: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  3264: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  3267: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //  3270: aload_0         /* this */
        //  3271: getstatic       net/minecraft/entity/EntityType.SNOW_GOLEM:Lnet/minecraft/entity/EntityType;
        //  3274: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  3277: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //  3280: iconst_1       
        //  3281: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //  3284: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  3287: getstatic       net/minecraft/item/Items.kD:Lnet/minecraft/item/Item;
        //  3290: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  3293: fconst_0       
        //  3294: ldc_w           15.0
        //  3297: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  3300: invokestatic    net/minecraft/world/loot/function/SetCountLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //  3303: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  3306: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  3309: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  3312: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //  3315: aload_0         /* this */
        //  3316: getstatic       net/minecraft/entity/EntityType.SPIDER:Lnet/minecraft/entity/EntityType;
        //  3319: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  3322: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //  3325: iconst_1       
        //  3326: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //  3329: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  3332: getstatic       net/minecraft/item/Items.jG:Lnet/minecraft/item/Item;
        //  3335: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  3338: fconst_0       
        //  3339: fconst_2       
        //  3340: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  3343: invokestatic    net/minecraft/world/loot/function/SetCountLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //  3346: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  3349: fconst_0       
        //  3350: fconst_1       
        //  3351: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  3354: invokestatic    net/minecraft/world/loot/function/LootingEnchantLootFunction.builder:(Lnet/minecraft/world/loot/UniformLootTableRange;)Lnet/minecraft/world/loot/function/LootingEnchantLootFunction$Builder;
        //  3357: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  3360: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  3363: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  3366: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //  3369: iconst_1       
        //  3370: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //  3373: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  3376: getstatic       net/minecraft/item/Items.mn:Lnet/minecraft/item/Item;
        //  3379: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  3382: ldc             -1.0
        //  3384: fconst_1       
        //  3385: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  3388: invokestatic    net/minecraft/world/loot/function/SetCountLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //  3391: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  3394: fconst_0       
        //  3395: fconst_1       
        //  3396: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  3399: invokestatic    net/minecraft/world/loot/function/LootingEnchantLootFunction.builder:(Lnet/minecraft/world/loot/UniformLootTableRange;)Lnet/minecraft/world/loot/function/LootingEnchantLootFunction$Builder;
        //  3402: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  3405: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  3408: invokestatic    net/minecraft/world/loot/condition/KilledByPlayerLootCondition.builder:()Lnet/minecraft/world/loot/condition/LootCondition$Builder;
        //  3411: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withCondition:(Lnet/minecraft/world/loot/condition/LootCondition$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  3414: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  3417: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //  3420: aload_0         /* this */
        //  3421: getstatic       net/minecraft/entity/EntityType.SQUID:Lnet/minecraft/entity/EntityType;
        //  3424: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  3427: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //  3430: iconst_1       
        //  3431: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //  3434: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  3437: getstatic       net/minecraft/item/Items.lh:Lnet/minecraft/item/Item;
        //  3440: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  3443: fconst_1       
        //  3444: ldc             3.0
        //  3446: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  3449: invokestatic    net/minecraft/world/loot/function/SetCountLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //  3452: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  3455: fconst_0       
        //  3456: fconst_1       
        //  3457: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  3460: invokestatic    net/minecraft/world/loot/function/LootingEnchantLootFunction.builder:(Lnet/minecraft/world/loot/UniformLootTableRange;)Lnet/minecraft/world/loot/function/LootingEnchantLootFunction$Builder;
        //  3463: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  3466: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  3469: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  3472: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //  3475: aload_0         /* this */
        //  3476: getstatic       net/minecraft/entity/EntityType.STRAY:Lnet/minecraft/entity/EntityType;
        //  3479: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  3482: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //  3485: iconst_1       
        //  3486: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //  3489: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  3492: getstatic       net/minecraft/item/Items.jg:Lnet/minecraft/item/Item;
        //  3495: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  3498: fconst_0       
        //  3499: fconst_2       
        //  3500: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  3503: invokestatic    net/minecraft/world/loot/function/SetCountLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //  3506: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  3509: fconst_0       
        //  3510: fconst_1       
        //  3511: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  3514: invokestatic    net/minecraft/world/loot/function/LootingEnchantLootFunction.builder:(Lnet/minecraft/world/loot/UniformLootTableRange;)Lnet/minecraft/world/loot/function/LootingEnchantLootFunction$Builder;
        //  3517: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  3520: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  3523: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  3526: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //  3529: iconst_1       
        //  3530: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //  3533: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  3536: getstatic       net/minecraft/item/Items.lB:Lnet/minecraft/item/Item;
        //  3539: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  3542: fconst_0       
        //  3543: fconst_2       
        //  3544: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  3547: invokestatic    net/minecraft/world/loot/function/SetCountLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //  3550: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  3553: fconst_0       
        //  3554: fconst_1       
        //  3555: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  3558: invokestatic    net/minecraft/world/loot/function/LootingEnchantLootFunction.builder:(Lnet/minecraft/world/loot/UniformLootTableRange;)Lnet/minecraft/world/loot/function/LootingEnchantLootFunction$Builder;
        //  3561: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  3564: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  3567: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  3570: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //  3573: iconst_1       
        //  3574: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //  3577: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  3580: getstatic       net/minecraft/item/Items.oU:Lnet/minecraft/item/Item;
        //  3583: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  3586: fconst_0       
        //  3587: fconst_1       
        //  3588: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  3591: invokestatic    net/minecraft/world/loot/function/SetCountLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //  3594: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  3597: fconst_0       
        //  3598: fconst_1       
        //  3599: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  3602: invokestatic    net/minecraft/world/loot/function/LootingEnchantLootFunction.builder:(Lnet/minecraft/world/loot/UniformLootTableRange;)Lnet/minecraft/world/loot/function/LootingEnchantLootFunction$Builder;
        //  3605: iconst_1       
        //  3606: invokevirtual   net/minecraft/world/loot/function/LootingEnchantLootFunction$Builder.withLimit:(I)Lnet/minecraft/world/loot/function/LootingEnchantLootFunction$Builder;
        //  3609: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  3612: new             Lnet/minecraft/nbt/CompoundTag;
        //  3615: dup            
        //  3616: invokespecial   net/minecraft/nbt/CompoundTag.<init>:()V
        //  3619: invokedynamic   BootstrapMethod #0, accept:()Ljava/util/function/Consumer;
        //  3624: invokestatic    net/minecraft/util/SystemUtil.consume:(Ljava/lang/Object;Ljava/util/function/Consumer;)Ljava/lang/Object;
        //  3627: checkcast       Lnet/minecraft/nbt/CompoundTag;
        //  3630: invokestatic    net/minecraft/world/loot/function/SetNbtLootFunction.builder:(Lnet/minecraft/nbt/CompoundTag;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //  3633: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  3636: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  3639: invokestatic    net/minecraft/world/loot/condition/KilledByPlayerLootCondition.builder:()Lnet/minecraft/world/loot/condition/LootCondition$Builder;
        //  3642: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withCondition:(Lnet/minecraft/world/loot/condition/LootCondition$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  3645: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  3648: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //  3651: aload_0         /* this */
        //  3652: getstatic       net/minecraft/entity/EntityType.ax:Lnet/minecraft/entity/EntityType;
        //  3655: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  3658: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //  3661: iconst_1       
        //  3662: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //  3665: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  3668: getstatic       net/minecraft/item/Items.kF:Lnet/minecraft/item/Item;
        //  3671: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  3674: fconst_0       
        //  3675: fconst_2       
        //  3676: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  3679: invokestatic    net/minecraft/world/loot/function/SetCountLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //  3682: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  3685: fconst_0       
        //  3686: fconst_1       
        //  3687: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  3690: invokestatic    net/minecraft/world/loot/function/LootingEnchantLootFunction.builder:(Lnet/minecraft/world/loot/UniformLootTableRange;)Lnet/minecraft/world/loot/function/LootingEnchantLootFunction$Builder;
        //  3693: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  3696: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  3699: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  3702: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //  3705: aload_0         /* this */
        //  3706: getstatic       net/minecraft/entity/EntityType.TROPICAL_FISH:Lnet/minecraft/entity/EntityType;
        //  3709: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  3712: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //  3715: iconst_1       
        //  3716: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //  3719: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  3722: getstatic       net/minecraft/item/Items.ld:Lnet/minecraft/item/Item;
        //  3725: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  3728: iconst_1       
        //  3729: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //  3732: invokestatic    net/minecraft/world/loot/function/SetCountLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //  3735: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  3738: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  3741: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  3744: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //  3747: iconst_1       
        //  3748: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //  3751: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  3754: getstatic       net/minecraft/item/Items.lw:Lnet/minecraft/item/Item;
        //  3757: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  3760: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  3763: ldc             0.05
        //  3765: invokestatic    net/minecraft/world/loot/condition/RandomChanceLootCondition.builder:(F)Lnet/minecraft/world/loot/condition/LootCondition$Builder;
        //  3768: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withCondition:(Lnet/minecraft/world/loot/condition/LootCondition$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  3771: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  3774: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //  3777: aload_0         /* this */
        //  3778: getstatic       net/minecraft/entity/EntityType.TURTLE:Lnet/minecraft/entity/EntityType;
        //  3781: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  3784: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //  3787: iconst_1       
        //  3788: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //  3791: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  3794: getstatic       net/minecraft/block/Blocks.aT:Lnet/minecraft/block/Block;
        //  3797: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  3800: iconst_3       
        //  3801: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.setWeight:(I)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  3804: fconst_0       
        //  3805: fconst_2       
        //  3806: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  3809: invokestatic    net/minecraft/world/loot/function/SetCountLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //  3812: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  3815: fconst_0       
        //  3816: fconst_1       
        //  3817: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  3820: invokestatic    net/minecraft/world/loot/function/LootingEnchantLootFunction.builder:(Lnet/minecraft/world/loot/UniformLootTableRange;)Lnet/minecraft/world/loot/function/LootingEnchantLootFunction$Builder;
        //  3823: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  3826: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  3829: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  3832: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //  3835: iconst_1       
        //  3836: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //  3839: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  3842: getstatic       net/minecraft/item/Items.jA:Lnet/minecraft/item/Item;
        //  3845: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  3848: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  3851: invokestatic    net/minecraft/predicate/entity/DamageSourcePredicate$Builder.create:()Lnet/minecraft/predicate/entity/DamageSourcePredicate$Builder;
        //  3854: iconst_1       
        //  3855: invokestatic    java/lang/Boolean.valueOf:(Z)Ljava/lang/Boolean;
        //  3858: invokevirtual   net/minecraft/predicate/entity/DamageSourcePredicate$Builder.lightning:(Ljava/lang/Boolean;)Lnet/minecraft/predicate/entity/DamageSourcePredicate$Builder;
        //  3861: invokestatic    net/minecraft/world/loot/condition/DamageSourcePropertiesLootCondition.builder:(Lnet/minecraft/predicate/entity/DamageSourcePredicate$Builder;)Lnet/minecraft/world/loot/condition/LootCondition$Builder;
        //  3864: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withCondition:(Lnet/minecraft/world/loot/condition/LootCondition$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  3867: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  3870: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //  3873: aload_0         /* this */
        //  3874: getstatic       net/minecraft/entity/EntityType.VEX:Lnet/minecraft/entity/EntityType;
        //  3877: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  3880: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //  3883: aload_0         /* this */
        //  3884: getstatic       net/minecraft/entity/EntityType.VILLAGER:Lnet/minecraft/entity/EntityType;
        //  3887: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  3890: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //  3893: aload_0         /* this */
        //  3894: getstatic       net/minecraft/entity/EntityType.aK:Lnet/minecraft/entity/EntityType;
        //  3897: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  3900: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //  3903: aload_0         /* this */
        //  3904: getstatic       net/minecraft/entity/EntityType.VINDICATOR:Lnet/minecraft/entity/EntityType;
        //  3907: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  3910: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //  3913: iconst_1       
        //  3914: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //  3917: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  3920: getstatic       net/minecraft/item/Items.nF:Lnet/minecraft/item/Item;
        //  3923: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  3926: fconst_0       
        //  3927: fconst_1       
        //  3928: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  3931: invokestatic    net/minecraft/world/loot/function/SetCountLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //  3934: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  3937: fconst_0       
        //  3938: fconst_1       
        //  3939: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  3942: invokestatic    net/minecraft/world/loot/function/LootingEnchantLootFunction.builder:(Lnet/minecraft/world/loot/UniformLootTableRange;)Lnet/minecraft/world/loot/function/LootingEnchantLootFunction$Builder;
        //  3945: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  3948: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  3951: invokestatic    net/minecraft/world/loot/condition/KilledByPlayerLootCondition.builder:()Lnet/minecraft/world/loot/condition/LootCondition$Builder;
        //  3954: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withCondition:(Lnet/minecraft/world/loot/condition/LootCondition$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  3957: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  3960: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //  3963: aload_0         /* this */
        //  3964: getstatic       net/minecraft/entity/EntityType.WITCH:Lnet/minecraft/entity/EntityType;
        //  3967: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  3970: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //  3973: fconst_1       
        //  3974: ldc             3.0
        //  3976: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  3979: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  3982: getstatic       net/minecraft/item/Items.la:Lnet/minecraft/item/Item;
        //  3985: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  3988: fconst_0       
        //  3989: fconst_2       
        //  3990: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  3993: invokestatic    net/minecraft/world/loot/function/SetCountLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //  3996: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  3999: fconst_0       
        //  4000: fconst_1       
        //  4001: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  4004: invokestatic    net/minecraft/world/loot/function/LootingEnchantLootFunction.builder:(Lnet/minecraft/world/loot/UniformLootTableRange;)Lnet/minecraft/world/loot/function/LootingEnchantLootFunction$Builder;
        //  4007: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  4010: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  4013: getstatic       net/minecraft/item/Items.lC:Lnet/minecraft/item/Item;
        //  4016: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  4019: fconst_0       
        //  4020: fconst_2       
        //  4021: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  4024: invokestatic    net/minecraft/world/loot/function/SetCountLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //  4027: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  4030: fconst_0       
        //  4031: fconst_1       
        //  4032: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  4035: invokestatic    net/minecraft/world/loot/function/LootingEnchantLootFunction.builder:(Lnet/minecraft/world/loot/UniformLootTableRange;)Lnet/minecraft/world/loot/function/LootingEnchantLootFunction$Builder;
        //  4038: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  4041: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  4044: getstatic       net/minecraft/item/Items.kC:Lnet/minecraft/item/Item;
        //  4047: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  4050: fconst_0       
        //  4051: fconst_2       
        //  4052: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  4055: invokestatic    net/minecraft/world/loot/function/SetCountLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //  4058: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  4061: fconst_0       
        //  4062: fconst_1       
        //  4063: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  4066: invokestatic    net/minecraft/world/loot/function/LootingEnchantLootFunction.builder:(Lnet/minecraft/world/loot/UniformLootTableRange;)Lnet/minecraft/world/loot/function/LootingEnchantLootFunction$Builder;
        //  4069: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  4072: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  4075: getstatic       net/minecraft/item/Items.mn:Lnet/minecraft/item/Item;
        //  4078: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  4081: fconst_0       
        //  4082: fconst_2       
        //  4083: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  4086: invokestatic    net/minecraft/world/loot/function/SetCountLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //  4089: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  4092: fconst_0       
        //  4093: fconst_1       
        //  4094: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  4097: invokestatic    net/minecraft/world/loot/function/LootingEnchantLootFunction.builder:(Lnet/minecraft/world/loot/UniformLootTableRange;)Lnet/minecraft/world/loot/function/LootingEnchantLootFunction$Builder;
        //  4100: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  4103: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  4106: getstatic       net/minecraft/item/Items.mm:Lnet/minecraft/item/Item;
        //  4109: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  4112: fconst_0       
        //  4113: fconst_2       
        //  4114: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  4117: invokestatic    net/minecraft/world/loot/function/SetCountLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //  4120: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  4123: fconst_0       
        //  4124: fconst_1       
        //  4125: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  4128: invokestatic    net/minecraft/world/loot/function/LootingEnchantLootFunction.builder:(Lnet/minecraft/world/loot/UniformLootTableRange;)Lnet/minecraft/world/loot/function/LootingEnchantLootFunction$Builder;
        //  4131: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  4134: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  4137: getstatic       net/minecraft/item/Items.jI:Lnet/minecraft/item/Item;
        //  4140: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  4143: fconst_0       
        //  4144: fconst_2       
        //  4145: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  4148: invokestatic    net/minecraft/world/loot/function/SetCountLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //  4151: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  4154: fconst_0       
        //  4155: fconst_1       
        //  4156: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  4159: invokestatic    net/minecraft/world/loot/function/LootingEnchantLootFunction.builder:(Lnet/minecraft/world/loot/UniformLootTableRange;)Lnet/minecraft/world/loot/function/LootingEnchantLootFunction$Builder;
        //  4162: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  4165: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  4168: getstatic       net/minecraft/item/Items.jz:Lnet/minecraft/item/Item;
        //  4171: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  4174: iconst_2       
        //  4175: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.setWeight:(I)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  4178: fconst_0       
        //  4179: fconst_2       
        //  4180: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  4183: invokestatic    net/minecraft/world/loot/function/SetCountLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //  4186: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  4189: fconst_0       
        //  4190: fconst_1       
        //  4191: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  4194: invokestatic    net/minecraft/world/loot/function/LootingEnchantLootFunction.builder:(Lnet/minecraft/world/loot/UniformLootTableRange;)Lnet/minecraft/world/loot/function/LootingEnchantLootFunction$Builder;
        //  4197: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  4200: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  4203: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  4206: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //  4209: aload_0         /* this */
        //  4210: getstatic       net/minecraft/entity/EntityType.WITHER:Lnet/minecraft/entity/EntityType;
        //  4213: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  4216: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //  4219: aload_0         /* this */
        //  4220: getstatic       net/minecraft/entity/EntityType.WITHER_SKELETON:Lnet/minecraft/entity/EntityType;
        //  4223: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  4226: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //  4229: iconst_1       
        //  4230: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //  4233: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  4236: getstatic       net/minecraft/item/Items.jh:Lnet/minecraft/item/Item;
        //  4239: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  4242: ldc             -1.0
        //  4244: fconst_1       
        //  4245: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  4248: invokestatic    net/minecraft/world/loot/function/SetCountLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //  4251: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  4254: fconst_0       
        //  4255: fconst_1       
        //  4256: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  4259: invokestatic    net/minecraft/world/loot/function/LootingEnchantLootFunction.builder:(Lnet/minecraft/world/loot/UniformLootTableRange;)Lnet/minecraft/world/loot/function/LootingEnchantLootFunction$Builder;
        //  4262: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  4265: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  4268: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  4271: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //  4274: iconst_1       
        //  4275: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //  4278: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  4281: getstatic       net/minecraft/item/Items.lB:Lnet/minecraft/item/Item;
        //  4284: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  4287: fconst_0       
        //  4288: fconst_2       
        //  4289: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  4292: invokestatic    net/minecraft/world/loot/function/SetCountLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //  4295: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  4298: fconst_0       
        //  4299: fconst_1       
        //  4300: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  4303: invokestatic    net/minecraft/world/loot/function/LootingEnchantLootFunction.builder:(Lnet/minecraft/world/loot/UniformLootTableRange;)Lnet/minecraft/world/loot/function/LootingEnchantLootFunction$Builder;
        //  4306: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  4309: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  4312: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  4315: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //  4318: iconst_1       
        //  4319: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //  4322: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  4325: getstatic       net/minecraft/block/Blocks.eW:Lnet/minecraft/block/Block;
        //  4328: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  4331: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  4334: invokestatic    net/minecraft/world/loot/condition/KilledByPlayerLootCondition.builder:()Lnet/minecraft/world/loot/condition/LootCondition$Builder;
        //  4337: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withCondition:(Lnet/minecraft/world/loot/condition/LootCondition$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  4340: ldc_w           0.025
        //  4343: ldc_w           0.01
        //  4346: invokestatic    net/minecraft/world/loot/condition/RandomChanceWithLootingLootCondition.builder:(FF)Lnet/minecraft/world/loot/condition/LootCondition$Builder;
        //  4349: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withCondition:(Lnet/minecraft/world/loot/condition/LootCondition$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  4352: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  4355: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //  4358: aload_0         /* this */
        //  4359: getstatic       net/minecraft/entity/EntityType.WOLF:Lnet/minecraft/entity/EntityType;
        //  4362: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  4365: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //  4368: aload_0         /* this */
        //  4369: getstatic       net/minecraft/entity/EntityType.ZOMBIE:Lnet/minecraft/entity/EntityType;
        //  4372: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  4375: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //  4378: iconst_1       
        //  4379: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //  4382: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  4385: getstatic       net/minecraft/item/Items.mf:Lnet/minecraft/item/Item;
        //  4388: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  4391: fconst_0       
        //  4392: fconst_2       
        //  4393: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  4396: invokestatic    net/minecraft/world/loot/function/SetCountLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //  4399: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  4402: fconst_0       
        //  4403: fconst_1       
        //  4404: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  4407: invokestatic    net/minecraft/world/loot/function/LootingEnchantLootFunction.builder:(Lnet/minecraft/world/loot/UniformLootTableRange;)Lnet/minecraft/world/loot/function/LootingEnchantLootFunction$Builder;
        //  4410: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  4413: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  4416: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  4419: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //  4422: iconst_1       
        //  4423: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //  4426: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  4429: getstatic       net/minecraft/item/Items.jk:Lnet/minecraft/item/Item;
        //  4432: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  4435: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  4438: getstatic       net/minecraft/item/Items.nI:Lnet/minecraft/item/Item;
        //  4441: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  4444: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  4447: getstatic       net/minecraft/item/Items.nJ:Lnet/minecraft/item/Item;
        //  4450: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  4453: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  4456: invokestatic    net/minecraft/world/loot/condition/KilledByPlayerLootCondition.builder:()Lnet/minecraft/world/loot/condition/LootCondition$Builder;
        //  4459: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withCondition:(Lnet/minecraft/world/loot/condition/LootCondition$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  4462: ldc_w           0.025
        //  4465: ldc_w           0.01
        //  4468: invokestatic    net/minecraft/world/loot/condition/RandomChanceWithLootingLootCondition.builder:(FF)Lnet/minecraft/world/loot/condition/LootCondition$Builder;
        //  4471: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withCondition:(Lnet/minecraft/world/loot/condition/LootCondition$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  4474: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  4477: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //  4480: aload_0         /* this */
        //  4481: getstatic       net/minecraft/entity/EntityType.ZOMBIE_HORSE:Lnet/minecraft/entity/EntityType;
        //  4484: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  4487: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //  4490: iconst_1       
        //  4491: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //  4494: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  4497: getstatic       net/minecraft/item/Items.mf:Lnet/minecraft/item/Item;
        //  4500: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  4503: fconst_0       
        //  4504: fconst_2       
        //  4505: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  4508: invokestatic    net/minecraft/world/loot/function/SetCountLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //  4511: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  4514: fconst_0       
        //  4515: fconst_1       
        //  4516: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  4519: invokestatic    net/minecraft/world/loot/function/LootingEnchantLootFunction.builder:(Lnet/minecraft/world/loot/UniformLootTableRange;)Lnet/minecraft/world/loot/function/LootingEnchantLootFunction$Builder;
        //  4522: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  4525: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  4528: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  4531: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //  4534: aload_0         /* this */
        //  4535: getstatic       net/minecraft/entity/EntityType.ZOMBIE_PIGMAN:Lnet/minecraft/entity/EntityType;
        //  4538: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  4541: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //  4544: iconst_1       
        //  4545: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //  4548: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  4551: getstatic       net/minecraft/item/Items.mf:Lnet/minecraft/item/Item;
        //  4554: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  4557: fconst_0       
        //  4558: fconst_1       
        //  4559: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  4562: invokestatic    net/minecraft/world/loot/function/SetCountLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //  4565: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  4568: fconst_0       
        //  4569: fconst_1       
        //  4570: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  4573: invokestatic    net/minecraft/world/loot/function/LootingEnchantLootFunction.builder:(Lnet/minecraft/world/loot/UniformLootTableRange;)Lnet/minecraft/world/loot/function/LootingEnchantLootFunction$Builder;
        //  4576: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  4579: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  4582: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  4585: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //  4588: iconst_1       
        //  4589: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //  4592: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  4595: getstatic       net/minecraft/item/Items.mj:Lnet/minecraft/item/Item;
        //  4598: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  4601: fconst_0       
        //  4602: fconst_1       
        //  4603: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  4606: invokestatic    net/minecraft/world/loot/function/SetCountLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //  4609: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  4612: fconst_0       
        //  4613: fconst_1       
        //  4614: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  4617: invokestatic    net/minecraft/world/loot/function/LootingEnchantLootFunction.builder:(Lnet/minecraft/world/loot/UniformLootTableRange;)Lnet/minecraft/world/loot/function/LootingEnchantLootFunction$Builder;
        //  4620: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  4623: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  4626: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  4629: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //  4632: iconst_1       
        //  4633: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //  4636: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  4639: getstatic       net/minecraft/item/Items.jl:Lnet/minecraft/item/Item;
        //  4642: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  4645: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  4648: invokestatic    net/minecraft/world/loot/condition/KilledByPlayerLootCondition.builder:()Lnet/minecraft/world/loot/condition/LootCondition$Builder;
        //  4651: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withCondition:(Lnet/minecraft/world/loot/condition/LootCondition$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  4654: ldc_w           0.025
        //  4657: ldc_w           0.01
        //  4660: invokestatic    net/minecraft/world/loot/condition/RandomChanceWithLootingLootCondition.builder:(FF)Lnet/minecraft/world/loot/condition/LootCondition$Builder;
        //  4663: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withCondition:(Lnet/minecraft/world/loot/condition/LootCondition$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  4666: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  4669: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //  4672: aload_0         /* this */
        //  4673: getstatic       net/minecraft/entity/EntityType.ZOMBIE_VILLAGER:Lnet/minecraft/entity/EntityType;
        //  4676: invokestatic    net/minecraft/world/loot/LootSupplier.builder:()Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  4679: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //  4682: iconst_1       
        //  4683: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //  4686: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  4689: getstatic       net/minecraft/item/Items.mf:Lnet/minecraft/item/Item;
        //  4692: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  4695: fconst_0       
        //  4696: fconst_2       
        //  4697: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  4700: invokestatic    net/minecraft/world/loot/function/SetCountLootFunction.builder:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/function/ConditionalLootFunction$Builder;
        //  4703: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  4706: fconst_0       
        //  4707: fconst_1       
        //  4708: invokestatic    net/minecraft/world/loot/UniformLootTableRange.between:(FF)Lnet/minecraft/world/loot/UniformLootTableRange;
        //  4711: invokestatic    net/minecraft/world/loot/function/LootingEnchantLootFunction.builder:(Lnet/minecraft/world/loot/UniformLootTableRange;)Lnet/minecraft/world/loot/function/LootingEnchantLootFunction$Builder;
        //  4714: invokevirtual   net/minecraft/world/loot/entry/LeafEntry$Builder.withFunction:(Lnet/minecraft/world/loot/function/LootFunction$Builder;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  4717: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  4720: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  4723: invokestatic    net/minecraft/world/loot/LootPool.builder:()Lnet/minecraft/world/loot/LootPool$Builder;
        //  4726: iconst_1       
        //  4727: invokestatic    net/minecraft/world/loot/ConstantLootTableRange.create:(I)Lnet/minecraft/world/loot/ConstantLootTableRange;
        //  4730: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withRolls:(Lnet/minecraft/world/loot/LootTableRange;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  4733: getstatic       net/minecraft/item/Items.jk:Lnet/minecraft/item/Item;
        //  4736: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  4739: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  4742: getstatic       net/minecraft/item/Items.nI:Lnet/minecraft/item/Item;
        //  4745: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  4748: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  4751: getstatic       net/minecraft/item/Items.nJ:Lnet/minecraft/item/Item;
        //  4754: invokestatic    net/minecraft/world/loot/entry/ItemEntry.builder:(Lnet/minecraft/item/ItemProvider;)Lnet/minecraft/world/loot/entry/LeafEntry$Builder;
        //  4757: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withEntry:(Lnet/minecraft/world/loot/entry/LootEntry$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  4760: invokestatic    net/minecraft/world/loot/condition/KilledByPlayerLootCondition.builder:()Lnet/minecraft/world/loot/condition/LootCondition$Builder;
        //  4763: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withCondition:(Lnet/minecraft/world/loot/condition/LootCondition$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  4766: ldc_w           0.025
        //  4769: ldc_w           0.01
        //  4772: invokestatic    net/minecraft/world/loot/condition/RandomChanceWithLootingLootCondition.builder:(FF)Lnet/minecraft/world/loot/condition/LootCondition$Builder;
        //  4775: invokevirtual   net/minecraft/world/loot/LootPool$Builder.withCondition:(Lnet/minecraft/world/loot/condition/LootCondition$Builder;)Lnet/minecraft/world/loot/LootPool$Builder;
        //  4778: invokevirtual   net/minecraft/world/loot/LootSupplier$Builder.withPool:(Lnet/minecraft/world/loot/LootPool$Builder;)Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  4781: invokespecial   net/minecraft/data/server/EntityLootTableGenerator.a:(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/loot/LootSupplier$Builder;)V
        //  4784: invokestatic    com/google/common/collect/Sets.newHashSet:()Ljava/util/HashSet;
        //  4787: astore_2        /* set2 */
        //  4788: getstatic       net/minecraft/util/registry/Registry.ENTITY_TYPE:Lnet/minecraft/util/registry/DefaultedRegistry;
        //  4791: invokevirtual   net/minecraft/util/registry/DefaultedRegistry.iterator:()Ljava/util/Iterator;
        //  4794: astore_3       
        //  4795: aload_3        
        //  4796: invokeinterface java/util/Iterator.hasNext:()Z
        //  4801: ifeq            4995
        //  4804: aload_3        
        //  4805: invokeinterface java/util/Iterator.next:()Ljava/lang/Object;
        //  4810: checkcast       Lnet/minecraft/entity/EntityType;
        //  4813: astore          entityType4
        //  4815: aload           entityType4
        //  4817: invokevirtual   net/minecraft/entity/EntityType.getLootTableId:()Lnet/minecraft/util/Identifier;
        //  4820: astore          identifier5
        //  4822: aload           entityType4
        //  4824: getstatic       net/minecraft/entity/EntityType.PLAYER:Lnet/minecraft/entity/EntityType;
        //  4827: if_acmpeq       4849
        //  4830: aload           entityType4
        //  4832: getstatic       net/minecraft/entity/EntityType.ARMOR_STAND:Lnet/minecraft/entity/EntityType;
        //  4835: if_acmpeq       4849
        //  4838: aload           entityType4
        //  4840: invokevirtual   net/minecraft/entity/EntityType.getCategory:()Lnet/minecraft/entity/EntityCategory;
        //  4843: getstatic       net/minecraft/entity/EntityCategory.e:Lnet/minecraft/entity/EntityCategory;
        //  4846: if_acmpeq       4936
        //  4849: aload           identifier5
        //  4851: getstatic       net/minecraft/world/loot/LootTables.EMPTY:Lnet/minecraft/util/Identifier;
        //  4854: if_acmpeq       4992
        //  4857: aload_2         /* set2 */
        //  4858: aload           identifier5
        //  4860: invokeinterface java/util/Set.add:(Ljava/lang/Object;)Z
        //  4865: ifeq            4992
        //  4868: aload_0         /* this */
        //  4869: getfield        net/minecraft/data/server/EntityLootTableGenerator.b:Ljava/util/Map;
        //  4872: aload           identifier5
        //  4874: invokeinterface java/util/Map.remove:(Ljava/lang/Object;)Ljava/lang/Object;
        //  4879: checkcast       Lnet/minecraft/world/loot/LootSupplier$Builder;
        //  4882: astore          builder6
        //  4884: aload           builder6
        //  4886: ifnonnull       4923
        //  4889: new             Ljava/lang/IllegalStateException;
        //  4892: dup            
        //  4893: ldc_w           "Missing loottable '%s' for '%s'"
        //  4896: iconst_2       
        //  4897: anewarray       Ljava/lang/Object;
        //  4900: dup            
        //  4901: iconst_0       
        //  4902: aload           identifier5
        //  4904: aastore        
        //  4905: dup            
        //  4906: iconst_1       
        //  4907: getstatic       net/minecraft/util/registry/Registry.ENTITY_TYPE:Lnet/minecraft/util/registry/DefaultedRegistry;
        //  4910: aload           entityType4
        //  4912: invokevirtual   net/minecraft/util/registry/DefaultedRegistry.getId:(Ljava/lang/Object;)Lnet/minecraft/util/Identifier;
        //  4915: aastore        
        //  4916: invokestatic    java/lang/String.format:(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
        //  4919: invokespecial   java/lang/IllegalStateException.<init>:(Ljava/lang/String;)V
        //  4922: athrow         
        //  4923: aload_1         /* biConsumer */
        //  4924: aload           identifier5
        //  4926: aload           builder6
        //  4928: invokeinterface java/util/function/BiConsumer.accept:(Ljava/lang/Object;Ljava/lang/Object;)V
        //  4933: goto            4992
        //  4936: aload           identifier5
        //  4938: getstatic       net/minecraft/world/loot/LootTables.EMPTY:Lnet/minecraft/util/Identifier;
        //  4941: if_acmpeq       4992
        //  4944: aload_0         /* this */
        //  4945: getfield        net/minecraft/data/server/EntityLootTableGenerator.b:Ljava/util/Map;
        //  4948: aload           identifier5
        //  4950: invokeinterface java/util/Map.remove:(Ljava/lang/Object;)Ljava/lang/Object;
        //  4955: ifnull          4992
        //  4958: new             Ljava/lang/IllegalStateException;
        //  4961: dup            
        //  4962: ldc_w           "Weird loottable '%s' for '%s', not a LivingEntity so should not have loot"
        //  4965: iconst_2       
        //  4966: anewarray       Ljava/lang/Object;
        //  4969: dup            
        //  4970: iconst_0       
        //  4971: aload           identifier5
        //  4973: aastore        
        //  4974: dup            
        //  4975: iconst_1       
        //  4976: getstatic       net/minecraft/util/registry/Registry.ENTITY_TYPE:Lnet/minecraft/util/registry/DefaultedRegistry;
        //  4979: aload           entityType4
        //  4981: invokevirtual   net/minecraft/util/registry/DefaultedRegistry.getId:(Ljava/lang/Object;)Lnet/minecraft/util/Identifier;
        //  4984: aastore        
        //  4985: invokestatic    java/lang/String.format:(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
        //  4988: invokespecial   java/lang/IllegalStateException.<init>:(Ljava/lang/String;)V
        //  4991: athrow         
        //  4992: goto            4795
        //  4995: aload_0         /* this */
        //  4996: getfield        net/minecraft/data/server/EntityLootTableGenerator.b:Ljava/util/Map;
        //  4999: aload_1         /* biConsumer */
        //  5000: invokedynamic   BootstrapMethod #1, accept:(Ljava/util/function/BiConsumer;)Ljava/util/function/BiConsumer;
        //  5005: invokeinterface java/util/Map.forEach:(Ljava/util/function/BiConsumer;)V
        //  5010: return         
        //    Signature:
        //  (Ljava/util/function/BiConsumer<Lnet/minecraft/util/Identifier;Lnet/minecraft/world/loot/LootSupplier$Builder;>;)V
        //    StackMapTable: 00 06 FD 12 BB 07 03 08 07 03 0A FD 00 35 07 00 73 07 03 1E FF 00 49 00 07 07 00 02 07 03 40 07 03 08 07 03 0A 00 07 03 1E 07 00 0F 00 00 FF 00 0C 00 06 07 00 02 07 03 40 07 03 08 07 03 0A 07 00 73 07 03 1E 00 00 F9 00 37 F9 00 02
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
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferCall(TypeAnalysis.java:2695)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1029)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:803)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferCall(TypeAnalysis.java:2695)
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
        //     at java.util.concurrent.ForkJoinPool$WorkQueue.runTask(ForkJoinPool.java:1056)
        //     at java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1692)
        //     at java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:157)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    private void a(final EntityType<?> entityType, final LootSupplier.Builder builder) {
        this.a(entityType.getLootTableId(), builder);
    }
    
    private void a(final Identifier identifier, final LootSupplier.Builder builder) {
        this.b.put(identifier, builder);
    }
    
    static {
        a = EntityPredicate.Builder.create().flags(EntityFlagsPredicate.Builder.create().onFire(true).build());
    }
}

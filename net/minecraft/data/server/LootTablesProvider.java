package net.minecraft.data.server;

import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import java.util.Map;
import net.minecraft.world.loot.LootTableReporter;
import java.io.IOException;
import net.minecraft.world.loot.LootManager;
import java.nio.file.Path;
import net.minecraft.data.DataCache;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.loot.context.LootContextTypes;
import net.minecraft.world.loot.context.LootContextType;
import net.minecraft.world.loot.LootSupplier;
import net.minecraft.util.Identifier;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import net.minecraft.data.DataGenerator;
import com.google.gson.Gson;
import org.apache.logging.log4j.Logger;
import net.minecraft.data.DataProvider;

public class LootTablesProvider implements DataProvider
{
    private static final Logger LOGGER;
    private static final Gson GSON;
    private final DataGenerator root;
    private final List<Pair<Supplier<Consumer<BiConsumer<Identifier, LootSupplier.Builder>>>, LootContextType>> e;
    
    public LootTablesProvider(final DataGenerator dataGenerator) {
        this.e = ImmutableList.of(Pair.of(FishingLootTableGenerator::new, LootContextTypes.FISHING), Pair.of(ChestLootTableGenerator::new, LootContextTypes.CHEST), Pair.of(EntityLootTableGenerator::new, LootContextTypes.ENTITY), Pair.of(BlockLootTableGenerator::new, LootContextTypes.BLOCK), Pair.of(GiftLootTableGenerator::new, LootContextTypes.GIFT));
        this.root = dataGenerator;
    }
    
    @Override
    public void run(final DataCache dataCache) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: getfield        net/minecraft/data/server/LootTablesProvider.root:Lnet/minecraft/data/DataGenerator;
        //     4: invokevirtual   net/minecraft/data/DataGenerator.getOutput:()Ljava/nio/file/Path;
        //     7: astore_2        /* path2 */
        //     8: invokestatic    com/google/common/collect/Maps.newHashMap:()Ljava/util/HashMap;
        //    11: astore_3        /* map3 */
        //    12: aload_0         /* this */
        //    13: getfield        net/minecraft/data/server/LootTablesProvider.e:Ljava/util/List;
        //    16: aload_3         /* map3 */
        //    17: invokedynamic   BootstrapMethod #5, accept:(Ljava/util/Map;)Ljava/util/function/Consumer;
        //    22: invokeinterface java/util/List.forEach:(Ljava/util/function/Consumer;)V
        //    27: new             Lnet/minecraft/world/loot/LootTableReporter;
        //    30: dup            
        //    31: invokespecial   net/minecraft/world/loot/LootTableReporter.<init>:()V
        //    34: astore          lootTableReporter4
        //    36: invokestatic    net/minecraft/world/loot/LootTables.getAll:()Ljava/util/Set;
        //    39: aload_3         /* map3 */
        //    40: invokeinterface java/util/Map.keySet:()Ljava/util/Set;
        //    45: invokestatic    com/google/common/collect/Sets.difference:(Ljava/util/Set;Ljava/util/Set;)Lcom/google/common/collect/Sets$SetView;
        //    48: astore          set5
        //    50: aload           set5
        //    52: invokeinterface java/util/Set.iterator:()Ljava/util/Iterator;
        //    57: astore          6
        //    59: aload           6
        //    61: invokeinterface java/util/Iterator.hasNext:()Z
        //    66: ifeq            109
        //    69: aload           6
        //    71: invokeinterface java/util/Iterator.next:()Ljava/lang/Object;
        //    76: checkcast       Lnet/minecraft/util/Identifier;
        //    79: astore          identifier7
        //    81: aload           lootTableReporter4
        //    83: new             Ljava/lang/StringBuilder;
        //    86: dup            
        //    87: invokespecial   java/lang/StringBuilder.<init>:()V
        //    90: ldc             "Missing built-in table: "
        //    92: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //    95: aload           identifier7
        //    97: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //   100: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   103: invokevirtual   net/minecraft/world/loot/LootTableReporter.report:(Ljava/lang/String;)V
        //   106: goto            59
        //   109: aload_3         /* map3 */
        //   110: aload           lootTableReporter4
        //   112: aload_3         /* map3 */
        //   113: invokedynamic   BootstrapMethod #6, accept:(Lnet/minecraft/world/loot/LootTableReporter;Ljava/util/Map;)Ljava/util/function/BiConsumer;
        //   118: invokeinterface java/util/Map.forEach:(Ljava/util/function/BiConsumer;)V
        //   123: aload           lootTableReporter4
        //   125: invokevirtual   net/minecraft/world/loot/LootTableReporter.getMessages:()Lcom/google/common/collect/Multimap;
        //   128: astore          multimap6
        //   130: aload           multimap6
        //   132: invokeinterface com/google/common/collect/Multimap.isEmpty:()Z
        //   137: ifne            162
        //   140: aload           multimap6
        //   142: invokedynamic   BootstrapMethod #7, accept:()Ljava/util/function/BiConsumer;
        //   147: invokeinterface com/google/common/collect/Multimap.forEach:(Ljava/util/function/BiConsumer;)V
        //   152: new             Ljava/lang/IllegalStateException;
        //   155: dup            
        //   156: ldc             "Failed to validate loot tables, see logs"
        //   158: invokespecial   java/lang/IllegalStateException.<init>:(Ljava/lang/String;)V
        //   161: athrow         
        //   162: aload_3         /* map3 */
        //   163: aload_2         /* path2 */
        //   164: aload_1         /* dataCache */
        //   165: invokedynamic   BootstrapMethod #8, accept:(Ljava/nio/file/Path;Lnet/minecraft/data/DataCache;)Ljava/util/function/BiConsumer;
        //   170: invokeinterface java/util/Map.forEach:(Ljava/util/function/BiConsumer;)V
        //   175: return         
        //    StackMapTable: 00 03 FF 00 3B 00 07 00 07 00 AA 07 00 AC 07 00 AE 07 00 92 00 07 00 B0 00 00 F9 00 31 FA 00 34
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Could not infer any expression.
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:374)
        //     at com.strobel.decompiler.ast.TypeAnalysis.run(TypeAnalysis.java:96)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:344)
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
        //     at java.util.concurrent.ForkJoinPool.helpComplete(ForkJoinPool.java:1870)
        //     at java.util.concurrent.ForkJoinPool.externalHelpComplete(ForkJoinPool.java:2467)
        //     at java.util.concurrent.ForkJoinTask.externalAwaitDone(ForkJoinTask.java:324)
        //     at java.util.concurrent.ForkJoinTask.doInvoke(ForkJoinTask.java:405)
        //     at java.util.concurrent.ForkJoinTask.invoke(ForkJoinTask.java:734)
        //     at java.util.stream.ForEachOps$ForEachOp.evaluateParallel(ForEachOps.java:160)
        //     at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateParallel(ForEachOps.java:174)
        //     at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:233)
        //     at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:418)
        //     at java.util.stream.ReferencePipeline$Head.forEach(ReferencePipeline.java:583)
        //     at cuchaz.enigma.Deobfuscator.decompileClasses(Deobfuscator.java:257)
        //     at cuchaz.enigma.Deobfuscator.writeSources(Deobfuscator.java:197)
        //     at cuchaz.enigma.gui.GuiController.lambda$exportSource$2(GuiController.java:132)
        //     at cuchaz.enigma.gui.dialog.ProgressDialog.lambda$runInThread$0(ProgressDialog.java:64)
        //     at java.lang.Thread.run(Thread.java:745)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    private static Path getOutput(final Path rootOutput, final Identifier lootTableId) {
        return rootOutput.resolve("data/" + lootTableId.getNamespace() + "/loot_tables/" + lootTableId.getPath() + ".json");
    }
    
    @Override
    public String getName() {
        return "LootTables";
    }
    
    static {
        LOGGER = LogManager.getLogger();
        GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    }
}

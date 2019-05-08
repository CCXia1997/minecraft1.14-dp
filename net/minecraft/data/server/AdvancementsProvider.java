package net.minecraft.data.server;

import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import com.google.gson.JsonElement;
import java.util.Set;
import java.nio.file.Path;
import java.io.IOException;
import net.minecraft.data.DataCache;
import com.google.common.collect.ImmutableList;
import net.minecraft.advancement.Advancement;
import java.util.function.Consumer;
import java.util.List;
import net.minecraft.data.DataGenerator;
import com.google.gson.Gson;
import org.apache.logging.log4j.Logger;
import net.minecraft.data.DataProvider;

public class AdvancementsProvider implements DataProvider
{
    private static final Logger LOGGER;
    private static final Gson GSON;
    private final DataGenerator root;
    private final List<Consumer<Consumer<Advancement>>> tabGenerators;
    
    public AdvancementsProvider(final DataGenerator dataGenerator) {
        this.tabGenerators = ImmutableList.<Consumer<Consumer<Advancement>>>of(new EndTabAdvancementGenerator(), new HusbandryTabAdvancementGenerator(), new AdventureTabAdvancementGenerator(), new NetherTabAdvancementGenerator(), new StoryTabAdvancementGenerator());
        this.root = dataGenerator;
    }
    
    @Override
    public void run(final DataCache dataCache) throws IOException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: getfield        net/minecraft/data/server/AdvancementsProvider.root:Lnet/minecraft/data/DataGenerator;
        //     4: invokevirtual   net/minecraft/data/DataGenerator.getOutput:()Ljava/nio/file/Path;
        //     7: astore_2        /* path2 */
        //     8: invokestatic    com/google/common/collect/Sets.newHashSet:()Ljava/util/HashSet;
        //    11: astore_3        /* set3 */
        //    12: aload_3         /* set3 */
        //    13: aload_2         /* path2 */
        //    14: aload_1         /* dataCache */
        //    15: invokedynamic   BootstrapMethod #0, accept:(Ljava/util/Set;Ljava/nio/file/Path;Lnet/minecraft/data/DataCache;)Ljava/util/function/Consumer;
        //    20: astore          consumer4
        //    22: aload_0         /* this */
        //    23: getfield        net/minecraft/data/server/AdvancementsProvider.tabGenerators:Ljava/util/List;
        //    26: invokeinterface java/util/List.iterator:()Ljava/util/Iterator;
        //    31: astore          5
        //    33: aload           5
        //    35: invokeinterface java/util/Iterator.hasNext:()Z
        //    40: ifeq            67
        //    43: aload           5
        //    45: invokeinterface java/util/Iterator.next:()Ljava/lang/Object;
        //    50: checkcast       Ljava/util/function/Consumer;
        //    53: astore          consumer6
        //    55: aload           consumer6
        //    57: aload           consumer4
        //    59: invokeinterface java/util/function/Consumer.accept:(Ljava/lang/Object;)V
        //    64: goto            33
        //    67: return         
        //    Exceptions:
        //  throws java.io.IOException
        //    StackMapTable: 00 02 FF 00 21 00 06 00 00 00 00 07 00 67 07 00 69 00 00 FF 00 21 00 00 00 00
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
        //     at java.util.concurrent.ForkJoinPool$WorkQueue.runTask(ForkJoinPool.java:1056)
        //     at java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1692)
        //     at java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:157)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    private static Path getOutput(final Path rootOutput, final Advancement advancement) {
        return rootOutput.resolve("data/" + advancement.getId().getNamespace() + "/advancements/" + advancement.getId().getPath() + ".json");
    }
    
    @Override
    public String getName() {
        return "Advancements";
    }
    
    static {
        LOGGER = LogManager.getLogger();
        GSON = new GsonBuilder().setPrettyPrinting().create();
    }
}

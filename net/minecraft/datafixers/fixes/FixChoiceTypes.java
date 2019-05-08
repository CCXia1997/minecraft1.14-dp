package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.util.Pair;
import java.util.function.Function;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.types.templates.TaggedChoice;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;

public class FixChoiceTypes extends DataFix
{
    private final String name;
    private final DSL.TypeReference types;
    
    public FixChoiceTypes(final Schema ouputSchema, final String name, final DSL.TypeReference typeReference) {
        super(ouputSchema, true);
        this.name = name;
        this.types = typeReference;
    }
    
    public TypeRewriteRule makeRule() {
        final TaggedChoice.TaggedChoiceType<?> taggedChoiceType1 = this.getInputSchema().findChoiceType(this.types);
        final TaggedChoice.TaggedChoiceType<?> taggedChoiceType2 = this.getOutputSchema().findChoiceType(this.types);
        return this.a(this.name, taggedChoiceType1, taggedChoiceType2);
    }
    
    protected final <K> TypeRewriteRule a(final String string, final TaggedChoice.TaggedChoiceType<K> taggedChoiceType2, final TaggedChoice.TaggedChoiceType<?> taggedChoiceType3) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: invokevirtual   com/mojang/datafixers/types/templates/TaggedChoice$TaggedChoiceType.getKeyType:()Lcom/mojang/datafixers/types/Type;
        //     4: aload_3         /* taggedChoiceType3 */
        //     5: invokevirtual   com/mojang/datafixers/types/templates/TaggedChoice$TaggedChoiceType.getKeyType:()Lcom/mojang/datafixers/types/Type;
        //     8: if_acmpeq       21
        //    11: new             Ljava/lang/IllegalStateException;
        //    14: dup            
        //    15: ldc             "Could not inject: key type is not the same"
        //    17: invokespecial   java/lang/IllegalStateException.<init>:(Ljava/lang/String;)V
        //    20: athrow         
        //    21: aload_3         /* taggedChoiceType3 */
        //    22: astore          taggedChoiceType4
        //    24: aload_0         /* this */
        //    25: aload_1         /* string */
        //    26: aload_2         /* taggedChoiceType2 */
        //    27: aload           taggedChoiceType4
        //    29: aload_0         /* this */
        //    30: aload           taggedChoiceType4
        //    32: invokedynamic   BootstrapMethod #0, apply:(Lnet/minecraft/datafixers/fixes/FixChoiceTypes;Lcom/mojang/datafixers/types/templates/TaggedChoice$TaggedChoiceType;)Ljava/util/function/Function;
        //    37: invokevirtual   net/minecraft/datafixers/fixes/FixChoiceTypes.fixTypeEverywhere:(Ljava/lang/String;Lcom/mojang/datafixers/types/Type;Lcom/mojang/datafixers/types/Type;Ljava/util/function/Function;)Lcom/mojang/datafixers/TypeRewriteRule;
        //    40: areturn        
        //    Signature:
        //  <K:Ljava/lang/Object;>(Ljava/lang/String;Lcom/mojang/datafixers/types/templates/TaggedChoice$TaggedChoiceType<TK;>;Lcom/mojang/datafixers/types/templates/TaggedChoice$TaggedChoiceType<*>;)Lcom/mojang/datafixers/TypeRewriteRule;
        //    StackMapTable: 00 01 15
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
}

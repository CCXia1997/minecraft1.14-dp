package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TaggedChoice;
import com.mojang.datafixers.util.Pair;
import java.util.function.Function;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.DataFix;

public abstract class EntityRenameFix extends DataFix
{
    private final String name;
    
    public EntityRenameFix(final String name, final Schema oldSchema, final boolean boolean3) {
        super(oldSchema, boolean3);
        this.name = name;
    }
    
    public TypeRewriteRule makeRule() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: invokevirtual   net/minecraft/datafixers/fixes/EntityRenameFix.getInputSchema:()Lcom/mojang/datafixers/schemas/Schema;
        //     4: getstatic       net/minecraft/datafixers/TypeReferences.ENTITY:Lcom/mojang/datafixers/DSL$TypeReference;
        //     7: invokevirtual   com/mojang/datafixers/schemas/Schema.findChoiceType:(Lcom/mojang/datafixers/DSL$TypeReference;)Lcom/mojang/datafixers/types/templates/TaggedChoice$TaggedChoiceType;
        //    10: astore_1        /* taggedChoiceType1 */
        //    11: aload_0         /* this */
        //    12: invokevirtual   net/minecraft/datafixers/fixes/EntityRenameFix.getOutputSchema:()Lcom/mojang/datafixers/schemas/Schema;
        //    15: getstatic       net/minecraft/datafixers/TypeReferences.ENTITY:Lcom/mojang/datafixers/DSL$TypeReference;
        //    18: invokevirtual   com/mojang/datafixers/schemas/Schema.findChoiceType:(Lcom/mojang/datafixers/DSL$TypeReference;)Lcom/mojang/datafixers/types/templates/TaggedChoice$TaggedChoiceType;
        //    21: astore_2        /* taggedChoiceType2 */
        //    22: getstatic       net/minecraft/datafixers/TypeReferences.ENTITY_NAME:Lcom/mojang/datafixers/DSL$TypeReference;
        //    25: invokeinterface com/mojang/datafixers/DSL$TypeReference.typeName:()Ljava/lang/String;
        //    30: invokestatic    com/mojang/datafixers/DSL.namespacedString:()Lcom/mojang/datafixers/types/Type;
        //    33: invokestatic    com/mojang/datafixers/DSL.named:(Ljava/lang/String;Lcom/mojang/datafixers/types/Type;)Lcom/mojang/datafixers/types/Type;
        //    36: astore_3        /* type3 */
        //    37: aload_0         /* this */
        //    38: invokevirtual   net/minecraft/datafixers/fixes/EntityRenameFix.getOutputSchema:()Lcom/mojang/datafixers/schemas/Schema;
        //    41: getstatic       net/minecraft/datafixers/TypeReferences.ENTITY_NAME:Lcom/mojang/datafixers/DSL$TypeReference;
        //    44: invokevirtual   com/mojang/datafixers/schemas/Schema.getType:(Lcom/mojang/datafixers/DSL$TypeReference;)Lcom/mojang/datafixers/types/Type;
        //    47: aload_3         /* type3 */
        //    48: invokestatic    java/util/Objects.equals:(Ljava/lang/Object;Ljava/lang/Object;)Z
        //    51: ifne            64
        //    54: new             Ljava/lang/IllegalStateException;
        //    57: dup            
        //    58: ldc             "Entity name type is not what was expected."
        //    60: invokespecial   java/lang/IllegalStateException.<init>:(Ljava/lang/String;)V
        //    63: athrow         
        //    64: aload_0         /* this */
        //    65: aload_0         /* this */
        //    66: getfield        net/minecraft/datafixers/fixes/EntityRenameFix.name:Ljava/lang/String;
        //    69: aload_1         /* taggedChoiceType1 */
        //    70: aload_2         /* taggedChoiceType2 */
        //    71: aload_0         /* this */
        //    72: aload_1         /* taggedChoiceType1 */
        //    73: aload_2         /* taggedChoiceType2 */
        //    74: invokedynamic   BootstrapMethod #0, apply:(Lnet/minecraft/datafixers/fixes/EntityRenameFix;Lcom/mojang/datafixers/types/templates/TaggedChoice$TaggedChoiceType;Lcom/mojang/datafixers/types/templates/TaggedChoice$TaggedChoiceType;)Ljava/util/function/Function;
        //    79: invokevirtual   net/minecraft/datafixers/fixes/EntityRenameFix.fixTypeEverywhere:(Ljava/lang/String;Lcom/mojang/datafixers/types/Type;Lcom/mojang/datafixers/types/Type;Ljava/util/function/Function;)Lcom/mojang/datafixers/TypeRewriteRule;
        //    82: aload_0         /* this */
        //    83: new             Ljava/lang/StringBuilder;
        //    86: dup            
        //    87: invokespecial   java/lang/StringBuilder.<init>:()V
        //    90: aload_0         /* this */
        //    91: getfield        net/minecraft/datafixers/fixes/EntityRenameFix.name:Ljava/lang/String;
        //    94: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //    97: ldc             " for entity name"
        //    99: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   102: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   105: aload_3         /* type3 */
        //   106: aload_0         /* this */
        //   107: invokedynamic   BootstrapMethod #1, apply:(Lnet/minecraft/datafixers/fixes/EntityRenameFix;)Ljava/util/function/Function;
        //   112: invokevirtual   net/minecraft/datafixers/fixes/EntityRenameFix.fixTypeEverywhere:(Ljava/lang/String;Lcom/mojang/datafixers/types/Type;Ljava/util/function/Function;)Lcom/mojang/datafixers/TypeRewriteRule;
        //   115: invokestatic    com/mojang/datafixers/TypeRewriteRule.seq:(Lcom/mojang/datafixers/TypeRewriteRule;Lcom/mojang/datafixers/TypeRewriteRule;)Lcom/mojang/datafixers/TypeRewriteRule;
        //   118: areturn        
        //    StackMapTable: 00 01 FE 00 40 07 00 07 07 00 07 07 00 5A
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
        //     at java.util.concurrent.ForkJoinPool$WorkQueue.execLocalTasks(ForkJoinPool.java:1040)
        //     at java.util.concurrent.ForkJoinPool$WorkQueue.runTask(ForkJoinPool.java:1058)
        //     at java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1692)
        //     at java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:157)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    protected abstract String rename(final String arg1);
}

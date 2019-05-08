package net.minecraft.world.loot.condition;

import net.minecraft.util.JsonHelper;
import net.minecraft.state.StateFactory;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import java.util.Collection;
import com.google.common.collect.Sets;
import com.google.common.collect.Maps;
import net.minecraft.world.loot.context.LootContext;
import com.google.common.collect.ImmutableSet;
import net.minecraft.world.loot.context.LootContextParameters;
import net.minecraft.world.loot.context.LootContextParameter;
import java.util.Set;
import java.util.Iterator;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.BlockState;
import java.util.function.Predicate;
import net.minecraft.state.property.Property;
import java.util.Map;
import net.minecraft.block.Block;

public class BlockStatePropertyLootCondition implements LootCondition
{
    private final Block block;
    private final Map<Property<?>, Object> properties;
    private final Predicate<BlockState> predicate;
    
    private BlockStatePropertyLootCondition(final Block block, final Map<Property<?>, Object> properties) {
        this.block = block;
        this.properties = ImmutableMap.copyOf(properties);
        this.predicate = getBlockState(block, properties);
    }
    
    private static Predicate<BlockState> getBlockState(final Block block, final Map<Property<?>, Object> properties) {
        final int integer3 = properties.size();
        if (integer3 == 0) {
            return blockState -> blockState.getBlock() == block;
        }
        if (integer3 == 1) {
            final Map.Entry<Property<?>, Object> entry4 = properties.entrySet().iterator().next();
            final Property<?> property5 = entry4.getKey();
            final Object object6 = entry4.getValue();
            final Object o;
            final Property<Object> property7;
            return blockState -> blockState.getBlock() == block && o.equals(blockState.get(property7));
        }
        Predicate<BlockState> predicate4 = blockState -> blockState.getBlock() == block;
        for (final Map.Entry<Property<?>, Object> entry5 : properties.entrySet()) {
            final Property<?> property6 = entry5.getKey();
            final Object object7 = entry5.getValue();
            predicate4 = predicate4.and(blockState -> object7.equals(blockState.get(property6)));
        }
        return predicate4;
    }
    
    @Override
    public Set<LootContextParameter<?>> getRequiredParameters() {
        return ImmutableSet.of(LootContextParameters.g);
    }
    
    public boolean a(final LootContext context) {
        final BlockState blockState2 = context.<BlockState>get(LootContextParameters.g);
        return blockState2 != null && this.predicate.test(blockState2);
    }
    
    public static Builder builder(final Block block) {
        return new Builder(block);
    }
    
    public static class Builder implements LootCondition.Builder
    {
        private final Block block;
        private final Set<Property<?>> availableProperties;
        private final Map<Property<?>, Object> propertyValues;
        
        public Builder(final Block block) {
            this.propertyValues = Maps.newHashMap();
            this.block = block;
            (this.availableProperties = Sets.<Property<?>>newIdentityHashSet()).addAll(block.getStateFactory().getProperties());
        }
        
        public <T extends Comparable<T>> Builder withBlockStateProperty(final Property<T> property, final T value) {
            if (!this.availableProperties.contains(property)) {
                throw new IllegalArgumentException("Block " + Registry.BLOCK.getId(this.block) + " does not have property '" + property + "'");
            }
            if (!property.getValues().contains(value)) {
                throw new IllegalArgumentException("Block " + Registry.BLOCK.getId(this.block) + " property '" + property + "' does not have value '" + value + "'");
            }
            this.propertyValues.put(property, value);
            return this;
        }
        
        @Override
        public LootCondition build() {
            return new BlockStatePropertyLootCondition(this.block, this.propertyValues, null);
        }
    }
    
    public static class Factory extends LootCondition.Factory<BlockStatePropertyLootCondition>
    {
        private static <T extends Comparable<T>> String getPropertyValueString(final Property<T> property, final Object value) {
            return property.getValueAsString((T)value);
        }
        
        protected Factory() {
            super(new Identifier("block_state_property"), BlockStatePropertyLootCondition.class);
        }
        
        @Override
        public void toJson(final JsonObject json, final BlockStatePropertyLootCondition condition, final JsonSerializationContext context) {
            json.addProperty("block", Registry.BLOCK.getId(condition.block).toString());
            final JsonObject jsonObject4 = new JsonObject();
            condition.properties.forEach((property, value) -> jsonObject4.addProperty(property.getName(), Factory.<Comparable>getPropertyValueString(property, value)));
            json.add("properties", jsonObject4);
        }
        
        @Override
        public BlockStatePropertyLootCondition fromJson(final JsonObject json, final JsonDeserializationContext context) {
            // 
            // This method could not be decompiled.
            // 
            // Original Bytecode:
            // 
            //     3: dup            
            //     4: aload_1         /* json */
            //     5: ldc             "block"
            //     7: invokestatic    net/minecraft/util/JsonHelper.getString:(Lcom/google/gson/JsonObject;Ljava/lang/String;)Ljava/lang/String;
            //    10: invokespecial   net/minecraft/util/Identifier.<init>:(Ljava/lang/String;)V
            //    13: astore_3        /* identifier3 */
            //    14: getstatic       net/minecraft/util/registry/Registry.BLOCK:Lnet/minecraft/util/registry/DefaultedRegistry;
            //    17: aload_3         /* identifier3 */
            //    18: invokevirtual   net/minecraft/util/registry/DefaultedRegistry.getOrEmpty:(Lnet/minecraft/util/Identifier;)Ljava/util/Optional;
            //    21: aload_3         /* identifier3 */
            //    22: invokedynamic   BootstrapMethod #1, get:(Lnet/minecraft/util/Identifier;)Ljava/util/function/Supplier;
            //    27: invokevirtual   java/util/Optional.orElseThrow:(Ljava/util/function/Supplier;)Ljava/lang/Object;
            //    30: checkcast       Lnet/minecraft/block/Block;
            //    33: astore          block4
            //    35: aload           block4
            //    37: invokevirtual   net/minecraft/block/Block.getStateFactory:()Lnet/minecraft/state/StateFactory;
            //    40: astore          stateFactory5
            //    42: invokestatic    com/google/common/collect/Maps.newHashMap:()Ljava/util/HashMap;
            //    45: astore          map6
            //    47: aload_1         /* json */
            //    48: ldc             "properties"
            //    50: invokevirtual   com/google/gson/JsonObject.has:(Ljava/lang/String;)Z
            //    53: ifeq            85
            //    56: aload_1         /* json */
            //    57: ldc             "properties"
            //    59: invokestatic    net/minecraft/util/JsonHelper.getObject:(Lcom/google/gson/JsonObject;Ljava/lang/String;)Lcom/google/gson/JsonObject;
            //    62: astore          jsonObject7
            //    64: aload           jsonObject7
            //    66: invokevirtual   com/google/gson/JsonObject.entrySet:()Ljava/util/Set;
            //    69: aload           stateFactory5
            //    71: aload           block4
            //    73: aload           map6
            //    75: invokedynamic   BootstrapMethod #2, accept:(Lnet/minecraft/state/StateFactory;Lnet/minecraft/block/Block;Ljava/util/Map;)Ljava/util/function/Consumer;
            //    80: invokeinterface java/util/Set.forEach:(Ljava/util/function/Consumer;)V
            //    85: new             Lnet/minecraft/world/loot/condition/BlockStatePropertyLootCondition;
            //    88: dup            
            //    89: aload           block4
            //    91: aload           map6
            //    93: aconst_null    
            //    94: invokespecial   net/minecraft/world/loot/condition/BlockStatePropertyLootCondition.<init>:(Lnet/minecraft/block/Block;Ljava/util/Map;Lnet/minecraft/world/loot/condition/BlockStatePropertyLootCondition$1;)V
            //    97: areturn        
            //    StackMapTable: 00 01 FF 00 55 00 07 00 00 00 00 07 00 9D 00 07 00 C5 00 00
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
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:576)
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
}

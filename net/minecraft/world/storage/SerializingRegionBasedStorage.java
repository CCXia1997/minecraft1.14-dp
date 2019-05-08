package net.minecraft.world.storage;

import org.apache.logging.log4j.LogManager;
import com.google.common.collect.ImmutableMap;
import net.minecraft.SharedConstants;
import java.util.Map;
import com.google.common.collect.Maps;
import net.minecraft.nbt.Tag;
import java.io.IOException;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.datafixers.NbtOps;
import net.minecraft.world.World;
import javax.annotation.Nullable;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import java.util.function.BooleanSupplier;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.io.File;
import net.minecraft.datafixers.DataFixTypes;
import com.mojang.datafixers.DataFixer;
import java.util.function.Function;
import com.mojang.datafixers.Dynamic;
import java.util.function.BiFunction;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
import java.util.Optional;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import org.apache.logging.log4j.Logger;
import net.minecraft.util.DynamicSerializable;

public class SerializingRegionBasedStorage<R extends DynamicSerializable> extends RegionBasedStorage
{
    private static final Logger LOGGER;
    private final Long2ObjectMap<Optional<R>> loadedElements;
    private final LongLinkedOpenHashSet unsavedElements;
    private final BiFunction<Runnable, Dynamic<?>, R> deserializer;
    private final Function<Runnable, R> factory;
    private final DataFixer dataFixer;
    private final DataFixTypes dataFixType;
    
    public SerializingRegionBasedStorage(final File directory, final BiFunction<Runnable, Dynamic<?>, R> deserializer, final Function<Runnable, R> factory, final DataFixer dataFixer, final DataFixTypes dataFixType) {
        super(directory);
        this.loadedElements = (Long2ObjectMap<Optional<R>>)new Long2ObjectOpenHashMap();
        this.unsavedElements = new LongLinkedOpenHashSet();
        this.deserializer = deserializer;
        this.factory = factory;
        this.dataFixer = dataFixer;
        this.dataFixType = dataFixType;
    }
    
    protected void tick(final BooleanSupplier booleanSupplier) {
        while (!this.unsavedElements.isEmpty() && booleanSupplier.getAsBoolean()) {
            final ChunkPos chunkPos2 = ChunkSectionPos.from(this.unsavedElements.firstLong()).toChunkPos();
            this.c(chunkPos2);
        }
    }
    
    @Nullable
    protected Optional<R> getIfLoaded(final long pos) {
        return (Optional<R>)this.loadedElements.get(pos);
    }
    
    protected Optional<R> get(final long pos) {
        final ChunkSectionPos chunkSectionPos3 = ChunkSectionPos.from(pos);
        if (this.isPosInvalid(chunkSectionPos3)) {
            return Optional.<R>empty();
        }
        Optional<R> optional4 = this.getIfLoaded(pos);
        if (optional4 != null) {
            return optional4;
        }
        this.loadDataAt(chunkSectionPos3.toChunkPos());
        optional4 = this.getIfLoaded(pos);
        if (optional4 == null) {
            throw new IllegalStateException();
        }
        return optional4;
    }
    
    protected boolean isPosInvalid(final ChunkSectionPos pos) {
        return World.isHeightInvalid(ChunkSectionPos.fromChunkCoord(pos.getChunkY()));
    }
    
    protected R getOrCreate(final long pos) {
        final Optional<R> optional3 = this.get(pos);
        if (optional3.isPresent()) {
            return optional3.get();
        }
        final R dynamicSerializable4 = this.factory.apply(() -> this.onUpdate(pos));
        this.loadedElements.put(pos, Optional.<R>of(dynamicSerializable4));
        return dynamicSerializable4;
    }
    
    private void loadDataAt(final ChunkPos chunkPos) {
        try {
            final CompoundTag compoundTag2 = this.getTagAt(chunkPos);
            this.<CompoundTag>a(chunkPos, (com.mojang.datafixers.types.DynamicOps<CompoundTag>)NbtOps.INSTANCE, compoundTag2);
        }
        catch (IOException iOException2) {
            SerializingRegionBasedStorage.LOGGER.error("Error reading data from disk", (Throwable)iOException2);
        }
    }
    
    private <T> void a(final ChunkPos chunkPos, final DynamicOps<T> dynamicOps, @Nullable final T object) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: ifnonnull       45
        //     4: iconst_0       
        //     5: istore          integer4
        //     7: iload           integer4
        //     9: bipush          16
        //    11: if_icmpge       42
        //    14: aload_0         /* this */
        //    15: getfield        net/minecraft/world/storage/SerializingRegionBasedStorage.loadedElements:Lit/unimi/dsi/fastutil/longs/Long2ObjectMap;
        //    18: aload_1         /* chunkPos */
        //    19: iload           integer4
        //    21: invokestatic    net/minecraft/util/math/ChunkSectionPos.from:(Lnet/minecraft/world/chunk/ChunkPos;I)Lnet/minecraft/util/math/ChunkSectionPos;
        //    24: invokevirtual   net/minecraft/util/math/ChunkSectionPos.asLong:()J
        //    27: invokestatic    java/util/Optional.empty:()Ljava/util/Optional;
        //    30: invokeinterface it/unimi/dsi/fastutil/longs/Long2ObjectMap.put:(JLjava/lang/Object;)Ljava/lang/Object;
        //    35: pop            
        //    36: iinc            integer4, 1
        //    39: goto            7
        //    42: goto            203
        //    45: new             Lcom/mojang/datafixers/Dynamic;
        //    48: dup            
        //    49: aload_2         /* dynamicOps */
        //    50: aload_3         /* object */
        //    51: invokespecial   com/mojang/datafixers/Dynamic.<init>:(Lcom/mojang/datafixers/types/DynamicOps;Ljava/lang/Object;)V
        //    54: astore          dynamic4
        //    56: aload           dynamic4
        //    58: invokestatic    net/minecraft/world/storage/SerializingRegionBasedStorage.a:(Lcom/mojang/datafixers/Dynamic;)I
        //    61: istore          integer5
        //    63: invokestatic    net/minecraft/SharedConstants.getGameVersion:()Lcom/mojang/bridge/game/GameVersion;
        //    66: invokeinterface com/mojang/bridge/game/GameVersion.getWorldVersion:()I
        //    71: istore          integer6
        //    73: iload           integer5
        //    75: iload           integer6
        //    77: if_icmpeq       84
        //    80: iconst_1       
        //    81: goto            85
        //    84: iconst_0       
        //    85: istore          boolean7
        //    87: aload_0         /* this */
        //    88: getfield        net/minecraft/world/storage/SerializingRegionBasedStorage.dataFixer:Lcom/mojang/datafixers/DataFixer;
        //    91: aload_0         /* this */
        //    92: getfield        net/minecraft/world/storage/SerializingRegionBasedStorage.dataFixType:Lnet/minecraft/datafixers/DataFixTypes;
        //    95: invokevirtual   net/minecraft/datafixers/DataFixTypes.getTypeReference:()Lcom/mojang/datafixers/DSL$TypeReference;
        //    98: aload           dynamic4
        //   100: iload           integer5
        //   102: iload           integer6
        //   104: invokeinterface com/mojang/datafixers/DataFixer.update:(Lcom/mojang/datafixers/DSL$TypeReference;Lcom/mojang/datafixers/Dynamic;II)Lcom/mojang/datafixers/Dynamic;
        //   109: astore          dynamic8
        //   111: aload           dynamic8
        //   113: ldc_w           "Sections"
        //   116: invokevirtual   com/mojang/datafixers/Dynamic.get:(Ljava/lang/String;)Lcom/mojang/datafixers/OptionalDynamic;
        //   119: astore          optionalDynamic9
        //   121: iconst_0       
        //   122: istore          integer10
        //   124: iload           integer10
        //   126: bipush          16
        //   128: if_icmpge       203
        //   131: aload_1         /* chunkPos */
        //   132: iload           integer10
        //   134: invokestatic    net/minecraft/util/math/ChunkSectionPos.from:(Lnet/minecraft/world/chunk/ChunkPos;I)Lnet/minecraft/util/math/ChunkSectionPos;
        //   137: invokevirtual   net/minecraft/util/math/ChunkSectionPos.asLong:()J
        //   140: lstore          long11
        //   142: aload           optionalDynamic9
        //   144: iload           integer10
        //   146: invokestatic    java/lang/Integer.toString:(I)Ljava/lang/String;
        //   149: invokevirtual   com/mojang/datafixers/OptionalDynamic.get:(Ljava/lang/String;)Lcom/mojang/datafixers/OptionalDynamic;
        //   152: invokevirtual   com/mojang/datafixers/OptionalDynamic.get:()Ljava/util/Optional;
        //   155: aload_0         /* this */
        //   156: lload           long11
        //   158: invokedynamic   BootstrapMethod #1, apply:(Lnet/minecraft/world/storage/SerializingRegionBasedStorage;J)Ljava/util/function/Function;
        //   163: invokevirtual   java/util/Optional.map:(Ljava/util/function/Function;)Ljava/util/Optional;
        //   166: astore          optional13
        //   168: aload_0         /* this */
        //   169: getfield        net/minecraft/world/storage/SerializingRegionBasedStorage.loadedElements:Lit/unimi/dsi/fastutil/longs/Long2ObjectMap;
        //   172: lload           long11
        //   174: aload           optional13
        //   176: invokeinterface it/unimi/dsi/fastutil/longs/Long2ObjectMap.put:(JLjava/lang/Object;)Ljava/lang/Object;
        //   181: pop            
        //   182: aload           optional13
        //   184: aload_0         /* this */
        //   185: lload           long11
        //   187: iload           boolean7
        //   189: invokedynamic   BootstrapMethod #2, accept:(Lnet/minecraft/world/storage/SerializingRegionBasedStorage;JZ)Ljava/util/function/Consumer;
        //   194: invokevirtual   java/util/Optional.ifPresent:(Ljava/util/function/Consumer;)V
        //   197: iinc            integer10, 1
        //   200: goto            124
        //   203: return         
        //    Signature:
        //  <T:Ljava/lang/Object;>(Lnet/minecraft/world/chunk/ChunkPos;Lcom/mojang/datafixers/types/DynamicOps<TT;>;TT;)V
        //    StackMapTable: 00 07 FF 00 07 00 05 07 00 02 07 00 E4 00 00 01 00 00 FF 00 22 00 00 00 00 FF 00 02 00 04 07 00 02 07 00 E4 07 00 EC 07 00 EE 00 00 FF 00 26 00 07 07 00 02 07 00 E4 00 00 07 00 F0 01 01 00 00 40 01 FF 00 26 00 0B 07 00 02 07 00 E4 00 00 00 00 00 01 00 07 01 14 01 00 00 FF 00 4E 00 00 00 00
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        //     at com.strobel.decompiler.languages.java.ast.NameVariables.generateNameForVariable(NameVariables.java:264)
        //     at com.strobel.decompiler.languages.java.ast.NameVariables.assignNamesToVariables(NameVariables.java:198)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:276)
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
    
    private void c(final ChunkPos chunkPos) {
        final Dynamic<Tag> dynamic2 = this.<Tag>a(chunkPos, (com.mojang.datafixers.types.DynamicOps<Tag>)NbtOps.INSTANCE);
        final Tag tag3 = (Tag)dynamic2.getValue();
        if (tag3 instanceof CompoundTag) {
            try {
                this.setTagAt(chunkPos, (CompoundTag)tag3);
            }
            catch (IOException iOException4) {
                SerializingRegionBasedStorage.LOGGER.error("Error writing data to disk", (Throwable)iOException4);
            }
        }
        else {
            SerializingRegionBasedStorage.LOGGER.error("Expected compound tag, got {}", tag3);
        }
    }
    
    private <T> Dynamic<T> a(final ChunkPos chunkPos, final DynamicOps<T> dynamicOps) {
        final Map<T, T> map3 = Maps.newHashMap();
        for (int integer4 = 0; integer4 < 16; ++integer4) {
            final long long5 = ChunkSectionPos.from(chunkPos, integer4).asLong();
            this.unsavedElements.remove(long5);
            final Optional<R> optional7 = (Optional<R>)this.loadedElements.get(long5);
            if (optional7 != null) {
                if (optional7.isPresent()) {
                    map3.put((T)dynamicOps.createString(Integer.toString(integer4)), optional7.get().<T>serialize(dynamicOps));
                }
            }
        }
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of(dynamicOps.createString("Sections"), dynamicOps.createMap((Map)map3), dynamicOps.createString("DataVersion"), dynamicOps.createInt(SharedConstants.getGameVersion().getWorldVersion()))));
    }
    
    protected void onLoad(final long pos) {
    }
    
    protected void onUpdate(final long pos) {
        final Optional<R> optional3 = (Optional<R>)this.loadedElements.get(pos);
        if (optional3 == null || !optional3.isPresent()) {
            SerializingRegionBasedStorage.LOGGER.warn("No data for position: {}", ChunkSectionPos.from(pos));
            return;
        }
        this.unsavedElements.add(pos);
    }
    
    private static int a(final Dynamic<?> dynamic) {
        return dynamic.get("DataVersion").asNumber().orElse(1945).intValue();
    }
    
    public void a(final ChunkPos chunkPos) {
        if (!this.unsavedElements.isEmpty()) {
            for (int integer2 = 0; integer2 < 16; ++integer2) {
                final long long3 = ChunkSectionPos.from(chunkPos, integer2).asLong();
                if (this.unsavedElements.contains(long3)) {
                    this.c(chunkPos);
                    return;
                }
            }
        }
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}

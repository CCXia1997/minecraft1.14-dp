package net.minecraft.village;

import net.minecraft.sound.SoundEvents;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableList;
import net.minecraft.block.Blocks;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Function;
import net.minecraft.state.property.Property;
import net.minecraft.block.enums.BedPart;
import net.minecraft.block.BedBlock;
import java.util.stream.Stream;
import java.util.Optional;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import java.util.Collection;
import com.google.common.collect.ImmutableSet;
import net.minecraft.block.Block;
import javax.annotation.Nullable;
import net.minecraft.sound.SoundEvent;
import java.util.Map;
import net.minecraft.block.BlockState;
import java.util.Set;
import java.util.function.Predicate;

public class PointOfInterestType
{
    private static final Predicate<PointOfInterestType> IS_USED_BY_PROFESSION;
    public static final Predicate<PointOfInterestType> ALWAYS_TRUE;
    private static final Set<BlockState> BED_STATES;
    private static final Map<BlockState, PointOfInterestType> BLOCK_STATE_TO_POINT_OF_INTEREST_TYPE;
    public static final PointOfInterestType b;
    public static final PointOfInterestType c;
    public static final PointOfInterestType d;
    public static final PointOfInterestType e;
    public static final PointOfInterestType f;
    public static final PointOfInterestType g;
    public static final PointOfInterestType h;
    public static final PointOfInterestType i;
    public static final PointOfInterestType j;
    public static final PointOfInterestType k;
    public static final PointOfInterestType l;
    public static final PointOfInterestType m;
    public static final PointOfInterestType n;
    public static final PointOfInterestType o;
    public static final PointOfInterestType p;
    public static final PointOfInterestType q;
    public static final PointOfInterestType r;
    private final String id;
    private final Set<BlockState> workStationStates;
    private final int ticketCount;
    @Nullable
    private final SoundEvent sound;
    private final Predicate<PointOfInterestType> completionCondition;
    
    private static Set<BlockState> getAllStatesOf(final Block block) {
        return ImmutableSet.copyOf(block.getStateFactory().getStates());
    }
    
    private PointOfInterestType(final String string, final Set<BlockState> set, final int integer, @Nullable final SoundEvent soundEvent, final Predicate<PointOfInterestType> predicate) {
        this.id = string;
        this.workStationStates = ImmutableSet.copyOf(set);
        this.ticketCount = integer;
        this.sound = soundEvent;
        this.completionCondition = predicate;
    }
    
    private PointOfInterestType(final String string, final Set<BlockState> set, final int integer, @Nullable final SoundEvent soundEvent) {
        this.id = string;
        this.workStationStates = ImmutableSet.copyOf(set);
        this.ticketCount = integer;
        this.sound = soundEvent;
        this.completionCondition = (pointOfInterestType -> pointOfInterestType == this);
    }
    
    public int getTicketCount() {
        return this.ticketCount;
    }
    
    public Predicate<PointOfInterestType> getCompletionCondition() {
        return this.completionCondition;
    }
    
    @Override
    public String toString() {
        return this.id;
    }
    
    @Nullable
    public SoundEvent getSound() {
        return this.sound;
    }
    
    private static PointOfInterestType register(final String id, final Set<BlockState> set, final int integer, @Nullable final SoundEvent soundEvent) {
        return setup(Registry.POINT_OF_INTEREST_TYPE.<PointOfInterestType>add(new Identifier(id), new PointOfInterestType(id, set, integer, soundEvent)));
    }
    
    private static PointOfInterestType register(final String id, final Set<BlockState> set, final int integer, @Nullable final SoundEvent soundEvent, final Predicate<PointOfInterestType> predicate) {
        return setup(Registry.POINT_OF_INTEREST_TYPE.<PointOfInterestType>add(new Identifier(id), new PointOfInterestType(id, set, integer, soundEvent, predicate)));
    }
    
    private static PointOfInterestType setup(final PointOfInterestType pointOfInterestType) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: getfield        net/minecraft/village/PointOfInterestType.workStationStates:Ljava/util/Set;
        //     4: aload_0         /* pointOfInterestType */
        //     5: invokedynamic   BootstrapMethod #1, accept:(Lnet/minecraft/village/PointOfInterestType;)Ljava/util/function/Consumer;
        //    10: invokeinterface java/util/Set.forEach:(Ljava/util/function/Consumer;)V
        //    15: aload_0         /* pointOfInterestType */
        //    16: areturn        
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
    
    public static Optional<PointOfInterestType> from(final BlockState blockState) {
        return Optional.<PointOfInterestType>ofNullable(PointOfInterestType.BLOCK_STATE_TO_POINT_OF_INTEREST_TYPE.get(blockState));
    }
    
    public static Stream<BlockState> getAllAssociatedStates() {
        return PointOfInterestType.BLOCK_STATE_TO_POINT_OF_INTEREST_TYPE.keySet().stream();
    }
    
    static {
        IS_USED_BY_PROFESSION = (pointOfInterestType -> Registry.VILLAGER_PROFESSION.stream().map(VillagerProfession::getWorkStation).collect(Collectors.toSet()).contains(pointOfInterestType));
        ALWAYS_TRUE = (pointOfInterestType -> true);
        BED_STATES = ImmutableList.<Block>of(Blocks.aK, Blocks.aL, Blocks.aH, Blocks.aI, Blocks.aF, Blocks.aD, Blocks.aJ, Blocks.az, Blocks.aE, Blocks.aB, Blocks.ay, Blocks.ax, Blocks.aC, Blocks.aG, Blocks.aw, Blocks.aA).stream().flatMap(block -> block.getStateFactory().getStates().stream()).filter(blockState -> blockState.<BedPart>get(BedBlock.PART) == BedPart.a).collect(ImmutableSet.toImmutableSet());
        BLOCK_STATE_TO_POINT_OF_INTEREST_TYPE = Maps.newHashMap();
        b = register("unemployed", ImmutableSet.of(), 1, null, PointOfInterestType.IS_USED_BY_PROFESSION);
        c = register("armorer", getAllStatesOf(Blocks.lM), 1, SoundEvents.mB);
        d = register("butcher", getAllStatesOf(Blocks.lL), 1, SoundEvents.mC);
        e = register("cartographer", getAllStatesOf(Blocks.lN), 1, SoundEvents.mD);
        f = register("cleric", getAllStatesOf(Blocks.dS), 1, SoundEvents.mE);
        g = register("farmer", getAllStatesOf(Blocks.lZ), 1, SoundEvents.mF);
        h = register("fisherman", getAllStatesOf(Blocks.lK), 1, SoundEvents.mG);
        i = register("fletcher", getAllStatesOf(Blocks.lO), 1, SoundEvents.mH);
        j = register("leatherworker", getAllStatesOf(Blocks.dT), 1, SoundEvents.mI);
        k = register("librarian", getAllStatesOf(Blocks.lQ), 1, SoundEvents.mJ);
        l = register("mason", getAllStatesOf(Blocks.lS), 1, SoundEvents.mK);
        m = register("nitwit", ImmutableSet.of(), 1, null);
        n = register("shepherd", getAllStatesOf(Blocks.lJ), 1, SoundEvents.mL);
        o = register("toolsmith", getAllStatesOf(Blocks.lR), 1, SoundEvents.mM);
        p = register("weaponsmith", getAllStatesOf(Blocks.lP), 1, SoundEvents.mN);
        q = register("home", PointOfInterestType.BED_STATES, 1, null);
        r = register("meeting", getAllStatesOf(Blocks.lT), 32, null);
    }
}

package net.minecraft.util.registry;

import net.minecraft.sound.SoundEvents;
import net.minecraft.fluid.Fluids;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Items;
import net.minecraft.potion.Potions;
import net.minecraft.world.biome.Biomes;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.stat.Stats;
import net.minecraft.structure.StructureFeatures;
import org.apache.commons.lang3.Validate;
import net.minecraft.SharedConstants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.util.stream.StreamSupport;
import java.util.stream.Stream;
import java.util.Random;
import java.util.Set;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Schedule;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.village.VillagerProfession;
import net.minecraft.village.VillagerType;
import net.minecraft.stat.StatType;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.container.ContainerType;
import net.minecraft.structure.pool.StructurePoolElementType;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.entity.decoration.painting.PaintingMotive;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.world.biome.source.BiomeSourceType;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.carver.Carver;
import net.minecraft.potion.Potion;
import net.minecraft.item.Item;
import net.minecraft.entity.EntityType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.block.Block;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.fluid.Fluid;
import net.minecraft.sound.SoundEvent;
import java.util.function.Supplier;
import net.minecraft.util.Identifier;
import java.util.Map;
import org.apache.logging.log4j.Logger;
import net.minecraft.util.IndexedIterable;

public abstract class Registry<T> implements IndexedIterable<T>
{
    protected static final Logger LOGGER;
    private static final Map<Identifier, Supplier<?>> DEFAULT_ENTRIES;
    public static final MutableRegistry<MutableRegistry<?>> REGISTRIES;
    public static final Registry<SoundEvent> SOUND_EVENT;
    public static final DefaultedRegistry<Fluid> FLUID;
    public static final Registry<StatusEffect> STATUS_EFFECT;
    public static final DefaultedRegistry<Block> BLOCK;
    public static final Registry<Enchantment> ENCHANTMENT;
    public static final DefaultedRegistry<EntityType<?>> ENTITY_TYPE;
    public static final DefaultedRegistry<Item> ITEM;
    public static final DefaultedRegistry<Potion> POTION;
    public static final Registry<Carver<?>> CARVER;
    public static final Registry<SurfaceBuilder<?>> SURFACE_BUILDER;
    public static final Registry<Feature<?>> FEATURE;
    public static final Registry<Decorator<?>> DECORATOR;
    public static final Registry<Biome> BIOME;
    public static final Registry<ParticleType<? extends ParticleParameters>> PARTICLE_TYPE;
    public static final Registry<BiomeSourceType<?, ?>> BIOME_SOURCE_TYPE;
    public static final Registry<BlockEntityType<?>> BLOCK_ENTITY;
    public static final Registry<ChunkGeneratorType<?, ?>> CHUNK_GENERATOR_TYPE;
    public static final Registry<DimensionType> DIMENSION;
    public static final DefaultedRegistry<PaintingMotive> MOTIVE;
    public static final Registry<Identifier> CUSTOM_STAT;
    public static final DefaultedRegistry<ChunkStatus> CHUNK_STATUS;
    public static final Registry<StructureFeature<?>> STRUCTURE_FEATURE;
    public static final Registry<StructurePieceType> STRUCTURE_PIECE;
    public static final Registry<RuleTest> RULE_TEST;
    public static final Registry<StructureProcessorType> STRUCTURE_PROCESSOR;
    public static final Registry<StructurePoolElementType> STRUCTURE_POOL_ELEMENT;
    public static final Registry<ContainerType<?>> CONTAINER;
    public static final Registry<RecipeType<?>> RECIPE_TYPE;
    public static final Registry<RecipeSerializer<?>> RECIPE_SERIALIZER;
    public static final Registry<StatType<?>> STAT_TYPE;
    public static final DefaultedRegistry<VillagerType> VILLAGER_TYPE;
    public static final DefaultedRegistry<VillagerProfession> VILLAGER_PROFESSION;
    public static final DefaultedRegistry<PointOfInterestType> POINT_OF_INTEREST_TYPE;
    public static final DefaultedRegistry<MemoryModuleType<?>> MEMORY_MODULE_TYPE;
    public static final DefaultedRegistry<SensorType<?>> SENSOR_TYPE;
    public static final SimpleRegistry<Schedule> SCHEDULE;
    public static final SimpleRegistry<Activity> ACTIVITY;
    
    private static <T> void putDefaultEntry(final String id, final Supplier<T> defaultEntry) {
        Registry.DEFAULT_ENTRIES.put(new Identifier(id), defaultEntry);
    }
    
    private static <T, R extends MutableRegistry<T>> R create(final String id, final R registry, final Supplier<T> defaultEntry) {
        Registry.<T>putDefaultEntry(id, defaultEntry);
        Registry.create(id, (MutableRegistry<Object>)registry);
        return registry;
    }
    
    private static <T> Registry<T> create(final String id, final MutableRegistry<T> registry) {
        Registry.REGISTRIES.<MutableRegistry<T>>add(new Identifier(id), registry);
        return registry;
    }
    
    @Nullable
    public abstract Identifier getId(final T arg1);
    
    public abstract int getRawId(@Nullable final T arg1);
    
    @Nullable
    public abstract T get(@Nullable final Identifier arg1);
    
    public abstract Optional<T> getOrEmpty(@Nullable final Identifier arg1);
    
    public abstract Set<Identifier> getIds();
    
    @Nullable
    public abstract T getRandom(final Random arg1);
    
    public Stream<T> stream() {
        return StreamSupport.<T>stream(this.spliterator(), false);
    }
    
    @Environment(EnvType.CLIENT)
    public abstract boolean containsId(final Identifier arg1);
    
    public static <T> T register(final Registry<? super T> registry, final String id, final T entry) {
        return Registry.<T>register(registry, new Identifier(id), entry);
    }
    
    public static <T> T register(final Registry<? super T> registry, final Identifier id, final T entry) {
        return ((MutableRegistry)registry).<T>add(id, entry);
    }
    
    public static <T> T register(final Registry<? super T> registry, final int rawId, final String id, final T entry) {
        return ((MutableRegistry)registry).<T>set(rawId, new Identifier(id), entry);
    }
    
    static {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     3: putstatic       net/minecraft/util/registry/Registry.LOGGER:Lorg/apache/logging/log4j/Logger;
        //     6: invokestatic    com/google/common/collect/Maps.newLinkedHashMap:()Ljava/util/LinkedHashMap;
        //     9: putstatic       net/minecraft/util/registry/Registry.DEFAULT_ENTRIES:Ljava/util/Map;
        //    12: new             Lnet/minecraft/util/registry/SimpleRegistry;
        //    15: dup            
        //    16: invokespecial   net/minecraft/util/registry/SimpleRegistry.<init>:()V
        //    19: putstatic       net/minecraft/util/registry/Registry.REGISTRIES:Lnet/minecraft/util/registry/MutableRegistry;
        //    22: ldc_w           "sound_event"
        //    25: new             Lnet/minecraft/util/registry/SimpleRegistry;
        //    28: dup            
        //    29: invokespecial   net/minecraft/util/registry/SimpleRegistry.<init>:()V
        //    32: invokedynamic   BootstrapMethod #0, get:()Ljava/util/function/Supplier;
        //    37: invokestatic    net/minecraft/util/registry/Registry.create:(Ljava/lang/String;Lnet/minecraft/util/registry/MutableRegistry;Ljava/util/function/Supplier;)Lnet/minecraft/util/registry/MutableRegistry;
        //    40: putstatic       net/minecraft/util/registry/Registry.SOUND_EVENT:Lnet/minecraft/util/registry/Registry;
        //    43: ldc_w           "fluid"
        //    46: new             Lnet/minecraft/util/registry/DefaultedRegistry;
        //    49: dup            
        //    50: ldc_w           "empty"
        //    53: invokespecial   net/minecraft/util/registry/DefaultedRegistry.<init>:(Ljava/lang/String;)V
        //    56: invokedynamic   BootstrapMethod #1, get:()Ljava/util/function/Supplier;
        //    61: invokestatic    net/minecraft/util/registry/Registry.create:(Ljava/lang/String;Lnet/minecraft/util/registry/MutableRegistry;Ljava/util/function/Supplier;)Lnet/minecraft/util/registry/MutableRegistry;
        //    64: checkcast       Lnet/minecraft/util/registry/DefaultedRegistry;
        //    67: putstatic       net/minecraft/util/registry/Registry.FLUID:Lnet/minecraft/util/registry/DefaultedRegistry;
        //    70: ldc_w           "mob_effect"
        //    73: new             Lnet/minecraft/util/registry/SimpleRegistry;
        //    76: dup            
        //    77: invokespecial   net/minecraft/util/registry/SimpleRegistry.<init>:()V
        //    80: invokedynamic   BootstrapMethod #2, get:()Ljava/util/function/Supplier;
        //    85: invokestatic    net/minecraft/util/registry/Registry.create:(Ljava/lang/String;Lnet/minecraft/util/registry/MutableRegistry;Ljava/util/function/Supplier;)Lnet/minecraft/util/registry/MutableRegistry;
        //    88: putstatic       net/minecraft/util/registry/Registry.STATUS_EFFECT:Lnet/minecraft/util/registry/Registry;
        //    91: ldc_w           "block"
        //    94: new             Lnet/minecraft/util/registry/DefaultedRegistry;
        //    97: dup            
        //    98: ldc_w           "air"
        //   101: invokespecial   net/minecraft/util/registry/DefaultedRegistry.<init>:(Ljava/lang/String;)V
        //   104: invokedynamic   BootstrapMethod #3, get:()Ljava/util/function/Supplier;
        //   109: invokestatic    net/minecraft/util/registry/Registry.create:(Ljava/lang/String;Lnet/minecraft/util/registry/MutableRegistry;Ljava/util/function/Supplier;)Lnet/minecraft/util/registry/MutableRegistry;
        //   112: checkcast       Lnet/minecraft/util/registry/DefaultedRegistry;
        //   115: putstatic       net/minecraft/util/registry/Registry.BLOCK:Lnet/minecraft/util/registry/DefaultedRegistry;
        //   118: ldc_w           "enchantment"
        //   121: new             Lnet/minecraft/util/registry/SimpleRegistry;
        //   124: dup            
        //   125: invokespecial   net/minecraft/util/registry/SimpleRegistry.<init>:()V
        //   128: invokedynamic   BootstrapMethod #4, get:()Ljava/util/function/Supplier;
        //   133: invokestatic    net/minecraft/util/registry/Registry.create:(Ljava/lang/String;Lnet/minecraft/util/registry/MutableRegistry;Ljava/util/function/Supplier;)Lnet/minecraft/util/registry/MutableRegistry;
        //   136: putstatic       net/minecraft/util/registry/Registry.ENCHANTMENT:Lnet/minecraft/util/registry/Registry;
        //   139: ldc_w           "entity_type"
        //   142: new             Lnet/minecraft/util/registry/DefaultedRegistry;
        //   145: dup            
        //   146: ldc_w           "pig"
        //   149: invokespecial   net/minecraft/util/registry/DefaultedRegistry.<init>:(Ljava/lang/String;)V
        //   152: invokedynamic   BootstrapMethod #5, get:()Ljava/util/function/Supplier;
        //   157: invokestatic    net/minecraft/util/registry/Registry.create:(Ljava/lang/String;Lnet/minecraft/util/registry/MutableRegistry;Ljava/util/function/Supplier;)Lnet/minecraft/util/registry/MutableRegistry;
        //   160: checkcast       Lnet/minecraft/util/registry/DefaultedRegistry;
        //   163: putstatic       net/minecraft/util/registry/Registry.ENTITY_TYPE:Lnet/minecraft/util/registry/DefaultedRegistry;
        //   166: ldc_w           "item"
        //   169: new             Lnet/minecraft/util/registry/DefaultedRegistry;
        //   172: dup            
        //   173: ldc_w           "air"
        //   176: invokespecial   net/minecraft/util/registry/DefaultedRegistry.<init>:(Ljava/lang/String;)V
        //   179: invokedynamic   BootstrapMethod #6, get:()Ljava/util/function/Supplier;
        //   184: invokestatic    net/minecraft/util/registry/Registry.create:(Ljava/lang/String;Lnet/minecraft/util/registry/MutableRegistry;Ljava/util/function/Supplier;)Lnet/minecraft/util/registry/MutableRegistry;
        //   187: checkcast       Lnet/minecraft/util/registry/DefaultedRegistry;
        //   190: putstatic       net/minecraft/util/registry/Registry.ITEM:Lnet/minecraft/util/registry/DefaultedRegistry;
        //   193: ldc_w           "potion"
        //   196: new             Lnet/minecraft/util/registry/DefaultedRegistry;
        //   199: dup            
        //   200: ldc_w           "empty"
        //   203: invokespecial   net/minecraft/util/registry/DefaultedRegistry.<init>:(Ljava/lang/String;)V
        //   206: invokedynamic   BootstrapMethod #7, get:()Ljava/util/function/Supplier;
        //   211: invokestatic    net/minecraft/util/registry/Registry.create:(Ljava/lang/String;Lnet/minecraft/util/registry/MutableRegistry;Ljava/util/function/Supplier;)Lnet/minecraft/util/registry/MutableRegistry;
        //   214: checkcast       Lnet/minecraft/util/registry/DefaultedRegistry;
        //   217: putstatic       net/minecraft/util/registry/Registry.POTION:Lnet/minecraft/util/registry/DefaultedRegistry;
        //   220: ldc_w           "carver"
        //   223: new             Lnet/minecraft/util/registry/SimpleRegistry;
        //   226: dup            
        //   227: invokespecial   net/minecraft/util/registry/SimpleRegistry.<init>:()V
        //   230: invokedynamic   BootstrapMethod #8, get:()Ljava/util/function/Supplier;
        //   235: invokestatic    net/minecraft/util/registry/Registry.create:(Ljava/lang/String;Lnet/minecraft/util/registry/MutableRegistry;Ljava/util/function/Supplier;)Lnet/minecraft/util/registry/MutableRegistry;
        //   238: putstatic       net/minecraft/util/registry/Registry.CARVER:Lnet/minecraft/util/registry/Registry;
        //   241: ldc_w           "surface_builder"
        //   244: new             Lnet/minecraft/util/registry/SimpleRegistry;
        //   247: dup            
        //   248: invokespecial   net/minecraft/util/registry/SimpleRegistry.<init>:()V
        //   251: invokedynamic   BootstrapMethod #9, get:()Ljava/util/function/Supplier;
        //   256: invokestatic    net/minecraft/util/registry/Registry.create:(Ljava/lang/String;Lnet/minecraft/util/registry/MutableRegistry;Ljava/util/function/Supplier;)Lnet/minecraft/util/registry/MutableRegistry;
        //   259: putstatic       net/minecraft/util/registry/Registry.SURFACE_BUILDER:Lnet/minecraft/util/registry/Registry;
        //   262: ldc_w           "feature"
        //   265: new             Lnet/minecraft/util/registry/SimpleRegistry;
        //   268: dup            
        //   269: invokespecial   net/minecraft/util/registry/SimpleRegistry.<init>:()V
        //   272: invokedynamic   BootstrapMethod #10, get:()Ljava/util/function/Supplier;
        //   277: invokestatic    net/minecraft/util/registry/Registry.create:(Ljava/lang/String;Lnet/minecraft/util/registry/MutableRegistry;Ljava/util/function/Supplier;)Lnet/minecraft/util/registry/MutableRegistry;
        //   280: putstatic       net/minecraft/util/registry/Registry.FEATURE:Lnet/minecraft/util/registry/Registry;
        //   283: ldc_w           "decorator"
        //   286: new             Lnet/minecraft/util/registry/SimpleRegistry;
        //   289: dup            
        //   290: invokespecial   net/minecraft/util/registry/SimpleRegistry.<init>:()V
        //   293: invokedynamic   BootstrapMethod #11, get:()Ljava/util/function/Supplier;
        //   298: invokestatic    net/minecraft/util/registry/Registry.create:(Ljava/lang/String;Lnet/minecraft/util/registry/MutableRegistry;Ljava/util/function/Supplier;)Lnet/minecraft/util/registry/MutableRegistry;
        //   301: putstatic       net/minecraft/util/registry/Registry.DECORATOR:Lnet/minecraft/util/registry/Registry;
        //   304: ldc_w           "biome"
        //   307: new             Lnet/minecraft/util/registry/SimpleRegistry;
        //   310: dup            
        //   311: invokespecial   net/minecraft/util/registry/SimpleRegistry.<init>:()V
        //   314: invokedynamic   BootstrapMethod #12, get:()Ljava/util/function/Supplier;
        //   319: invokestatic    net/minecraft/util/registry/Registry.create:(Ljava/lang/String;Lnet/minecraft/util/registry/MutableRegistry;Ljava/util/function/Supplier;)Lnet/minecraft/util/registry/MutableRegistry;
        //   322: putstatic       net/minecraft/util/registry/Registry.BIOME:Lnet/minecraft/util/registry/Registry;
        //   325: ldc_w           "particle_type"
        //   328: new             Lnet/minecraft/util/registry/SimpleRegistry;
        //   331: dup            
        //   332: invokespecial   net/minecraft/util/registry/SimpleRegistry.<init>:()V
        //   335: invokedynamic   BootstrapMethod #13, get:()Ljava/util/function/Supplier;
        //   340: invokestatic    net/minecraft/util/registry/Registry.create:(Ljava/lang/String;Lnet/minecraft/util/registry/MutableRegistry;Ljava/util/function/Supplier;)Lnet/minecraft/util/registry/MutableRegistry;
        //   343: putstatic       net/minecraft/util/registry/Registry.PARTICLE_TYPE:Lnet/minecraft/util/registry/Registry;
        //   346: ldc_w           "biome_source_type"
        //   349: new             Lnet/minecraft/util/registry/SimpleRegistry;
        //   352: dup            
        //   353: invokespecial   net/minecraft/util/registry/SimpleRegistry.<init>:()V
        //   356: invokedynamic   BootstrapMethod #14, get:()Ljava/util/function/Supplier;
        //   361: invokestatic    net/minecraft/util/registry/Registry.create:(Ljava/lang/String;Lnet/minecraft/util/registry/MutableRegistry;Ljava/util/function/Supplier;)Lnet/minecraft/util/registry/MutableRegistry;
        //   364: putstatic       net/minecraft/util/registry/Registry.BIOME_SOURCE_TYPE:Lnet/minecraft/util/registry/Registry;
        //   367: ldc_w           "block_entity_type"
        //   370: new             Lnet/minecraft/util/registry/SimpleRegistry;
        //   373: dup            
        //   374: invokespecial   net/minecraft/util/registry/SimpleRegistry.<init>:()V
        //   377: invokedynamic   BootstrapMethod #15, get:()Ljava/util/function/Supplier;
        //   382: invokestatic    net/minecraft/util/registry/Registry.create:(Ljava/lang/String;Lnet/minecraft/util/registry/MutableRegistry;Ljava/util/function/Supplier;)Lnet/minecraft/util/registry/MutableRegistry;
        //   385: putstatic       net/minecraft/util/registry/Registry.BLOCK_ENTITY:Lnet/minecraft/util/registry/Registry;
        //   388: ldc_w           "chunk_generator_type"
        //   391: new             Lnet/minecraft/util/registry/SimpleRegistry;
        //   394: dup            
        //   395: invokespecial   net/minecraft/util/registry/SimpleRegistry.<init>:()V
        //   398: invokedynamic   BootstrapMethod #16, get:()Ljava/util/function/Supplier;
        //   403: invokestatic    net/minecraft/util/registry/Registry.create:(Ljava/lang/String;Lnet/minecraft/util/registry/MutableRegistry;Ljava/util/function/Supplier;)Lnet/minecraft/util/registry/MutableRegistry;
        //   406: putstatic       net/minecraft/util/registry/Registry.CHUNK_GENERATOR_TYPE:Lnet/minecraft/util/registry/Registry;
        //   409: ldc_w           "dimension_type"
        //   412: new             Lnet/minecraft/util/registry/SimpleRegistry;
        //   415: dup            
        //   416: invokespecial   net/minecraft/util/registry/SimpleRegistry.<init>:()V
        //   419: invokedynamic   BootstrapMethod #17, get:()Ljava/util/function/Supplier;
        //   424: invokestatic    net/minecraft/util/registry/Registry.create:(Ljava/lang/String;Lnet/minecraft/util/registry/MutableRegistry;Ljava/util/function/Supplier;)Lnet/minecraft/util/registry/MutableRegistry;
        //   427: putstatic       net/minecraft/util/registry/Registry.DIMENSION:Lnet/minecraft/util/registry/Registry;
        //   430: ldc_w           "motive"
        //   433: new             Lnet/minecraft/util/registry/DefaultedRegistry;
        //   436: dup            
        //   437: ldc_w           "kebab"
        //   440: invokespecial   net/minecraft/util/registry/DefaultedRegistry.<init>:(Ljava/lang/String;)V
        //   443: invokedynamic   BootstrapMethod #18, get:()Ljava/util/function/Supplier;
        //   448: invokestatic    net/minecraft/util/registry/Registry.create:(Ljava/lang/String;Lnet/minecraft/util/registry/MutableRegistry;Ljava/util/function/Supplier;)Lnet/minecraft/util/registry/MutableRegistry;
        //   451: checkcast       Lnet/minecraft/util/registry/DefaultedRegistry;
        //   454: putstatic       net/minecraft/util/registry/Registry.MOTIVE:Lnet/minecraft/util/registry/DefaultedRegistry;
        //   457: ldc_w           "custom_stat"
        //   460: new             Lnet/minecraft/util/registry/SimpleRegistry;
        //   463: dup            
        //   464: invokespecial   net/minecraft/util/registry/SimpleRegistry.<init>:()V
        //   467: invokedynamic   BootstrapMethod #19, get:()Ljava/util/function/Supplier;
        //   472: invokestatic    net/minecraft/util/registry/Registry.create:(Ljava/lang/String;Lnet/minecraft/util/registry/MutableRegistry;Ljava/util/function/Supplier;)Lnet/minecraft/util/registry/MutableRegistry;
        //   475: putstatic       net/minecraft/util/registry/Registry.CUSTOM_STAT:Lnet/minecraft/util/registry/Registry;
        //   478: ldc_w           "chunk_status"
        //   481: new             Lnet/minecraft/util/registry/DefaultedRegistry;
        //   484: dup            
        //   485: ldc_w           "empty"
        //   488: invokespecial   net/minecraft/util/registry/DefaultedRegistry.<init>:(Ljava/lang/String;)V
        //   491: invokedynamic   BootstrapMethod #20, get:()Ljava/util/function/Supplier;
        //   496: invokestatic    net/minecraft/util/registry/Registry.create:(Ljava/lang/String;Lnet/minecraft/util/registry/MutableRegistry;Ljava/util/function/Supplier;)Lnet/minecraft/util/registry/MutableRegistry;
        //   499: checkcast       Lnet/minecraft/util/registry/DefaultedRegistry;
        //   502: putstatic       net/minecraft/util/registry/Registry.CHUNK_STATUS:Lnet/minecraft/util/registry/DefaultedRegistry;
        //   505: ldc_w           "structure_feature"
        //   508: new             Lnet/minecraft/util/registry/SimpleRegistry;
        //   511: dup            
        //   512: invokespecial   net/minecraft/util/registry/SimpleRegistry.<init>:()V
        //   515: invokedynamic   BootstrapMethod #21, get:()Ljava/util/function/Supplier;
        //   520: invokestatic    net/minecraft/util/registry/Registry.create:(Ljava/lang/String;Lnet/minecraft/util/registry/MutableRegistry;Ljava/util/function/Supplier;)Lnet/minecraft/util/registry/MutableRegistry;
        //   523: putstatic       net/minecraft/util/registry/Registry.STRUCTURE_FEATURE:Lnet/minecraft/util/registry/Registry;
        //   526: ldc_w           "structure_piece"
        //   529: new             Lnet/minecraft/util/registry/SimpleRegistry;
        //   532: dup            
        //   533: invokespecial   net/minecraft/util/registry/SimpleRegistry.<init>:()V
        //   536: invokedynamic   BootstrapMethod #22, get:()Ljava/util/function/Supplier;
        //   541: invokestatic    net/minecraft/util/registry/Registry.create:(Ljava/lang/String;Lnet/minecraft/util/registry/MutableRegistry;Ljava/util/function/Supplier;)Lnet/minecraft/util/registry/MutableRegistry;
        //   544: putstatic       net/minecraft/util/registry/Registry.STRUCTURE_PIECE:Lnet/minecraft/util/registry/Registry;
        //   547: ldc_w           "rule_test"
        //   550: new             Lnet/minecraft/util/registry/SimpleRegistry;
        //   553: dup            
        //   554: invokespecial   net/minecraft/util/registry/SimpleRegistry.<init>:()V
        //   557: invokedynamic   BootstrapMethod #23, get:()Ljava/util/function/Supplier;
        //   562: invokestatic    net/minecraft/util/registry/Registry.create:(Ljava/lang/String;Lnet/minecraft/util/registry/MutableRegistry;Ljava/util/function/Supplier;)Lnet/minecraft/util/registry/MutableRegistry;
        //   565: putstatic       net/minecraft/util/registry/Registry.RULE_TEST:Lnet/minecraft/util/registry/Registry;
        //   568: ldc_w           "structure_processor"
        //   571: new             Lnet/minecraft/util/registry/SimpleRegistry;
        //   574: dup            
        //   575: invokespecial   net/minecraft/util/registry/SimpleRegistry.<init>:()V
        //   578: invokedynamic   BootstrapMethod #24, get:()Ljava/util/function/Supplier;
        //   583: invokestatic    net/minecraft/util/registry/Registry.create:(Ljava/lang/String;Lnet/minecraft/util/registry/MutableRegistry;Ljava/util/function/Supplier;)Lnet/minecraft/util/registry/MutableRegistry;
        //   586: putstatic       net/minecraft/util/registry/Registry.STRUCTURE_PROCESSOR:Lnet/minecraft/util/registry/Registry;
        //   589: ldc_w           "structure_pool_element"
        //   592: new             Lnet/minecraft/util/registry/SimpleRegistry;
        //   595: dup            
        //   596: invokespecial   net/minecraft/util/registry/SimpleRegistry.<init>:()V
        //   599: invokedynamic   BootstrapMethod #25, get:()Ljava/util/function/Supplier;
        //   604: invokestatic    net/minecraft/util/registry/Registry.create:(Ljava/lang/String;Lnet/minecraft/util/registry/MutableRegistry;Ljava/util/function/Supplier;)Lnet/minecraft/util/registry/MutableRegistry;
        //   607: putstatic       net/minecraft/util/registry/Registry.STRUCTURE_POOL_ELEMENT:Lnet/minecraft/util/registry/Registry;
        //   610: ldc_w           "menu"
        //   613: new             Lnet/minecraft/util/registry/SimpleRegistry;
        //   616: dup            
        //   617: invokespecial   net/minecraft/util/registry/SimpleRegistry.<init>:()V
        //   620: invokedynamic   BootstrapMethod #26, get:()Ljava/util/function/Supplier;
        //   625: invokestatic    net/minecraft/util/registry/Registry.create:(Ljava/lang/String;Lnet/minecraft/util/registry/MutableRegistry;Ljava/util/function/Supplier;)Lnet/minecraft/util/registry/MutableRegistry;
        //   628: putstatic       net/minecraft/util/registry/Registry.CONTAINER:Lnet/minecraft/util/registry/Registry;
        //   631: ldc_w           "recipe_type"
        //   634: new             Lnet/minecraft/util/registry/SimpleRegistry;
        //   637: dup            
        //   638: invokespecial   net/minecraft/util/registry/SimpleRegistry.<init>:()V
        //   641: invokedynamic   BootstrapMethod #27, get:()Ljava/util/function/Supplier;
        //   646: invokestatic    net/minecraft/util/registry/Registry.create:(Ljava/lang/String;Lnet/minecraft/util/registry/MutableRegistry;Ljava/util/function/Supplier;)Lnet/minecraft/util/registry/MutableRegistry;
        //   649: putstatic       net/minecraft/util/registry/Registry.RECIPE_TYPE:Lnet/minecraft/util/registry/Registry;
        //   652: ldc_w           "recipe_serializer"
        //   655: new             Lnet/minecraft/util/registry/SimpleRegistry;
        //   658: dup            
        //   659: invokespecial   net/minecraft/util/registry/SimpleRegistry.<init>:()V
        //   662: invokedynamic   BootstrapMethod #28, get:()Ljava/util/function/Supplier;
        //   667: invokestatic    net/minecraft/util/registry/Registry.create:(Ljava/lang/String;Lnet/minecraft/util/registry/MutableRegistry;Ljava/util/function/Supplier;)Lnet/minecraft/util/registry/MutableRegistry;
        //   670: putstatic       net/minecraft/util/registry/Registry.RECIPE_SERIALIZER:Lnet/minecraft/util/registry/Registry;
        //   673: ldc_w           "stat_type"
        //   676: new             Lnet/minecraft/util/registry/SimpleRegistry;
        //   679: dup            
        //   680: invokespecial   net/minecraft/util/registry/SimpleRegistry.<init>:()V
        //   683: invokestatic    net/minecraft/util/registry/Registry.create:(Ljava/lang/String;Lnet/minecraft/util/registry/MutableRegistry;)Lnet/minecraft/util/registry/Registry;
        //   686: putstatic       net/minecraft/util/registry/Registry.STAT_TYPE:Lnet/minecraft/util/registry/Registry;
        //   689: ldc_w           "villager_type"
        //   692: new             Lnet/minecraft/util/registry/DefaultedRegistry;
        //   695: dup            
        //   696: ldc_w           "plains"
        //   699: invokespecial   net/minecraft/util/registry/DefaultedRegistry.<init>:(Ljava/lang/String;)V
        //   702: invokedynamic   BootstrapMethod #29, get:()Ljava/util/function/Supplier;
        //   707: invokestatic    net/minecraft/util/registry/Registry.create:(Ljava/lang/String;Lnet/minecraft/util/registry/MutableRegistry;Ljava/util/function/Supplier;)Lnet/minecraft/util/registry/MutableRegistry;
        //   710: checkcast       Lnet/minecraft/util/registry/DefaultedRegistry;
        //   713: putstatic       net/minecraft/util/registry/Registry.VILLAGER_TYPE:Lnet/minecraft/util/registry/DefaultedRegistry;
        //   716: ldc_w           "villager_profession"
        //   719: new             Lnet/minecraft/util/registry/DefaultedRegistry;
        //   722: dup            
        //   723: ldc_w           "none"
        //   726: invokespecial   net/minecraft/util/registry/DefaultedRegistry.<init>:(Ljava/lang/String;)V
        //   729: invokedynamic   BootstrapMethod #30, get:()Ljava/util/function/Supplier;
        //   734: invokestatic    net/minecraft/util/registry/Registry.create:(Ljava/lang/String;Lnet/minecraft/util/registry/MutableRegistry;Ljava/util/function/Supplier;)Lnet/minecraft/util/registry/MutableRegistry;
        //   737: checkcast       Lnet/minecraft/util/registry/DefaultedRegistry;
        //   740: putstatic       net/minecraft/util/registry/Registry.VILLAGER_PROFESSION:Lnet/minecraft/util/registry/DefaultedRegistry;
        //   743: ldc_w           "point_of_interest_type"
        //   746: new             Lnet/minecraft/util/registry/DefaultedRegistry;
        //   749: dup            
        //   750: ldc_w           "unemployed"
        //   753: invokespecial   net/minecraft/util/registry/DefaultedRegistry.<init>:(Ljava/lang/String;)V
        //   756: invokedynamic   BootstrapMethod #31, get:()Ljava/util/function/Supplier;
        //   761: invokestatic    net/minecraft/util/registry/Registry.create:(Ljava/lang/String;Lnet/minecraft/util/registry/MutableRegistry;Ljava/util/function/Supplier;)Lnet/minecraft/util/registry/MutableRegistry;
        //   764: checkcast       Lnet/minecraft/util/registry/DefaultedRegistry;
        //   767: putstatic       net/minecraft/util/registry/Registry.POINT_OF_INTEREST_TYPE:Lnet/minecraft/util/registry/DefaultedRegistry;
        //   770: ldc_w           "memory_module_type"
        //   773: new             Lnet/minecraft/util/registry/DefaultedRegistry;
        //   776: dup            
        //   777: ldc_w           "dummy"
        //   780: invokespecial   net/minecraft/util/registry/DefaultedRegistry.<init>:(Ljava/lang/String;)V
        //   783: invokedynamic   BootstrapMethod #32, get:()Ljava/util/function/Supplier;
        //   788: invokestatic    net/minecraft/util/registry/Registry.create:(Ljava/lang/String;Lnet/minecraft/util/registry/MutableRegistry;Ljava/util/function/Supplier;)Lnet/minecraft/util/registry/MutableRegistry;
        //   791: checkcast       Lnet/minecraft/util/registry/DefaultedRegistry;
        //   794: putstatic       net/minecraft/util/registry/Registry.MEMORY_MODULE_TYPE:Lnet/minecraft/util/registry/DefaultedRegistry;
        //   797: ldc_w           "sensor_type"
        //   800: new             Lnet/minecraft/util/registry/DefaultedRegistry;
        //   803: dup            
        //   804: ldc_w           "dummy"
        //   807: invokespecial   net/minecraft/util/registry/DefaultedRegistry.<init>:(Ljava/lang/String;)V
        //   810: invokedynamic   BootstrapMethod #33, get:()Ljava/util/function/Supplier;
        //   815: invokestatic    net/minecraft/util/registry/Registry.create:(Ljava/lang/String;Lnet/minecraft/util/registry/MutableRegistry;Ljava/util/function/Supplier;)Lnet/minecraft/util/registry/MutableRegistry;
        //   818: checkcast       Lnet/minecraft/util/registry/DefaultedRegistry;
        //   821: putstatic       net/minecraft/util/registry/Registry.SENSOR_TYPE:Lnet/minecraft/util/registry/DefaultedRegistry;
        //   824: ldc_w           "schedule"
        //   827: new             Lnet/minecraft/util/registry/SimpleRegistry;
        //   830: dup            
        //   831: invokespecial   net/minecraft/util/registry/SimpleRegistry.<init>:()V
        //   834: invokedynamic   BootstrapMethod #34, get:()Ljava/util/function/Supplier;
        //   839: invokestatic    net/minecraft/util/registry/Registry.create:(Ljava/lang/String;Lnet/minecraft/util/registry/MutableRegistry;Ljava/util/function/Supplier;)Lnet/minecraft/util/registry/MutableRegistry;
        //   842: checkcast       Lnet/minecraft/util/registry/SimpleRegistry;
        //   845: putstatic       net/minecraft/util/registry/Registry.SCHEDULE:Lnet/minecraft/util/registry/SimpleRegistry;
        //   848: ldc_w           "activity"
        //   851: new             Lnet/minecraft/util/registry/SimpleRegistry;
        //   854: dup            
        //   855: invokespecial   net/minecraft/util/registry/SimpleRegistry.<init>:()V
        //   858: invokedynamic   BootstrapMethod #35, get:()Ljava/util/function/Supplier;
        //   863: invokestatic    net/minecraft/util/registry/Registry.create:(Ljava/lang/String;Lnet/minecraft/util/registry/MutableRegistry;Ljava/util/function/Supplier;)Lnet/minecraft/util/registry/MutableRegistry;
        //   866: checkcast       Lnet/minecraft/util/registry/SimpleRegistry;
        //   869: putstatic       net/minecraft/util/registry/Registry.ACTIVITY:Lnet/minecraft/util/registry/SimpleRegistry;
        //   872: getstatic       net/minecraft/util/registry/Registry.DEFAULT_ENTRIES:Ljava/util/Map;
        //   875: invokeinterface java/util/Map.entrySet:()Ljava/util/Set;
        //   880: invokedynamic   BootstrapMethod #36, accept:()Ljava/util/function/Consumer;
        //   885: invokeinterface java/util/Set.forEach:(Ljava/util/function/Consumer;)V
        //   890: getstatic       net/minecraft/util/registry/Registry.REGISTRIES:Lnet/minecraft/util/registry/MutableRegistry;
        //   893: invokedynamic   BootstrapMethod #37, accept:()Ljava/util/function/Consumer;
        //   898: invokevirtual   net/minecraft/util/registry/MutableRegistry.forEach:(Ljava/util/function/Consumer;)V
        //   901: return         
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
}

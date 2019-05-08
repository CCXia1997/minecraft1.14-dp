package net.minecraft.client.render.model;

import java.util.stream.Stream;
import java.io.InputStream;
import net.minecraft.client.render.model.json.WeightedUnbakedModel;
import com.mojang.datafixers.util.Pair;
import com.google.common.collect.ImmutableList;
import net.minecraft.resource.Resource;
import java.io.Reader;
import java.io.Closeable;
import org.apache.commons.io.IOUtils;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.io.StringReader;
import java.io.FileNotFoundException;
import javax.annotation.Nullable;
import java.util.Objects;
import net.minecraft.state.property.Property;
import java.util.function.Predicate;
import java.util.Iterator;
import java.util.Collection;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import net.minecraft.util.registry.Registry;
import net.minecraft.client.render.block.BlockModels;
import java.io.IOException;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.util.profiler.Profiler;
import org.apache.commons.lang3.tuple.Triple;
import net.minecraft.client.render.model.json.ModelVariantMap;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.resource.ResourceManager;
import net.minecraft.client.render.model.json.ItemModelGenerator;
import net.minecraft.block.BlockState;
import net.minecraft.block.Block;
import net.minecraft.state.StateFactory;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import com.google.common.base.Splitter;
import java.util.Map;
import com.google.common.annotations.VisibleForTesting;
import net.minecraft.client.util.ModelIdentifier;
import org.apache.logging.log4j.Logger;
import java.util.Set;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ModelLoader
{
    public static final Identifier FIRE_0;
    public static final Identifier FIRE_1;
    public static final Identifier LAVA_FLOW;
    public static final Identifier WATER_FLOW;
    public static final Identifier WATER_OVERLAY;
    public static final Identifier DESTROY_STAGE_0;
    public static final Identifier DESTROY_STAGE_1;
    public static final Identifier DESTROY_STAGE_2;
    public static final Identifier DESTROY_STAGE_3;
    public static final Identifier DESTROY_STAGE_4;
    public static final Identifier DESTROY_STAGE_5;
    public static final Identifier DESTROY_STAGE_6;
    public static final Identifier DESTROY_STAGE_7;
    public static final Identifier DESTROY_STAGE_8;
    public static final Identifier DESTROY_STAGE_9;
    private static final Set<Identifier> DEFAULT_TEXTURES;
    private static final Logger LOGGER;
    public static final ModelIdentifier MISSING;
    @VisibleForTesting
    public static final String MISSING_DEFINITION;
    private static final Map<String, String> BUILTIN_MODEL_DEFINITIONS;
    private static final Splitter COMMA_SPLITTER;
    private static final Splitter KEY_VALUE_SPLITTER;
    public static final JsonUnbakedModel GENERATION_MARKER;
    public static final JsonUnbakedModel BLOCK_ENTITY_MARKER;
    private static final StateFactory<Block, BlockState> ITEM_FRAME_STATE_FACTORY;
    private static final ItemModelGenerator ITEM_MODEL_GENERATOR;
    private static final Map<Identifier, StateFactory<Block, BlockState>> STATIC_DEFINITIONS;
    private final ResourceManager resourceManager;
    private final SpriteAtlasTexture spriteAtlas;
    private final Set<Identifier> modelsToLoad;
    private final ModelVariantMap.DeserializationContext variantMapDeserializationContext;
    private final Map<Identifier, UnbakedModel> unbakedModels;
    private final Map<Triple<Identifier, ModelRotation, Boolean>, BakedModel> bakedModelCache;
    private final Map<Identifier, UnbakedModel> modelsToBake;
    private final Map<Identifier, BakedModel> bakedModels;
    private final SpriteAtlasTexture.Data spriteAtlasData;
    
    public ModelLoader(final ResourceManager resourceManager, final SpriteAtlasTexture spriteAtlas, final Profiler profiler) {
        this.modelsToLoad = Sets.newHashSet();
        this.variantMapDeserializationContext = new ModelVariantMap.DeserializationContext();
        this.unbakedModels = Maps.newHashMap();
        this.bakedModelCache = Maps.newHashMap();
        this.modelsToBake = Maps.newHashMap();
        this.bakedModels = Maps.newHashMap();
        this.resourceManager = resourceManager;
        this.spriteAtlas = spriteAtlas;
        profiler.push("missing_model");
        try {
            this.unbakedModels.put(ModelLoader.MISSING, this.loadModelFromJson(ModelLoader.MISSING));
            this.addModel(ModelLoader.MISSING);
        }
        catch (IOException iOException4) {
            ModelLoader.LOGGER.error("Error loading missing model, should never happen :(", (Throwable)iOException4);
            throw new RuntimeException(iOException4);
        }
        profiler.swap("static_definitions");
        ModelLoader.STATIC_DEFINITIONS.forEach((identifier, stateFactory) -> stateFactory.getStates().forEach(blockState -> this.addModel(BlockModels.getModelId(identifier, blockState))));
        profiler.swap("blocks");
        for (final Block block5 : Registry.BLOCK) {
            block5.getStateFactory().getStates().forEach(blockState -> this.addModel(BlockModels.getModelId(blockState)));
        }
        profiler.swap("items");
        for (final Identifier identifier2 : Registry.ITEM.getIds()) {
            this.addModel(new ModelIdentifier(identifier2, "inventory"));
        }
        profiler.swap("special");
        this.addModel(new ModelIdentifier("minecraft:trident_in_hand#inventory"));
        profiler.swap("textures");
        final Set<String> set4 = Sets.newLinkedHashSet();
        final Set<Identifier> set5 = this.modelsToBake.values().stream().flatMap(unbakedModel -> unbakedModel.getTextureDependencies(this::getOrLoadModel, set4).stream()).collect(Collectors.toSet());
        set5.addAll(ModelLoader.DEFAULT_TEXTURES);
        set4.forEach(string -> ModelLoader.LOGGER.warn("Unable to resolve texture reference: {}", string));
        profiler.swap("stitching");
        this.spriteAtlasData = this.spriteAtlas.stitch(this.resourceManager, set5, profiler);
        profiler.pop();
    }
    
    public void upload(final Profiler profiler) {
        profiler.push("atlas");
        this.spriteAtlas.upload(this.spriteAtlasData);
        profiler.swap("baking");
        BakedModel bakedModel2;
        this.modelsToBake.keySet().forEach(identifier -> {
            bakedModel2 = null;
            try {
                bakedModel2 = this.bake(identifier, ModelRotation.X0_Y0);
            }
            catch (Exception exception3) {
                ModelLoader.LOGGER.warn("Unable to bake model: '{}': {}", identifier, exception3);
            }
            if (bakedModel2 != null) {
                this.bakedModels.put(identifier, bakedModel2);
            }
            return;
        });
        profiler.pop();
    }
    
    private static Predicate<BlockState> stateKeyToPredicate(final StateFactory<Block, BlockState> stateFactory, final String key) {
        final Map<Property<?>, Comparable<?>> map3 = Maps.newHashMap();
        for (final String string5 : ModelLoader.COMMA_SPLITTER.split(key)) {
            final Iterator<String> iterator6 = ModelLoader.KEY_VALUE_SPLITTER.split(string5).iterator();
            if (iterator6.hasNext()) {
                final String string6 = iterator6.next();
                final Property<?> property8 = stateFactory.getProperty(string6);
                if (property8 != null && iterator6.hasNext()) {
                    final String string7 = iterator6.next();
                    final Comparable<?> comparable10 = ModelLoader.<Comparable<?>>getPropertyValue(property8, string7);
                    if (comparable10 == null) {
                        throw new RuntimeException("Unknown value: '" + string7 + "' for blockstate property: '" + string6 + "' " + property8.getValues());
                    }
                    map3.put(property8, comparable10);
                }
                else {
                    if (!string6.isEmpty()) {
                        throw new RuntimeException("Unknown blockstate property: '" + string6 + "'");
                    }
                    continue;
                }
            }
        }
        final Block block4 = stateFactory.getBaseObject();
        final Block block5;
        final Map<Property<?>, Comparable<?>> map4;
        final Iterator<Map.Entry<Property<?>, Comparable<?>>> iterator8;
        Map.Entry<Property<?>, Comparable<?>> entry5;
        return blockState -> {
            if (blockState == null || block5 != blockState.getBlock()) {
                return false;
            }
            else {
                map4.entrySet().iterator();
                while (iterator8.hasNext()) {
                    entry5 = iterator8.next();
                    if (!Objects.equals(blockState.get(entry5.getKey()), entry5.getValue())) {
                        return false;
                    }
                }
                return true;
            }
        };
    }
    
    @Nullable
    static <T extends Comparable<T>> T getPropertyValue(final Property<T> property, final String string) {
        return property.getValue(string).orElse(null);
    }
    
    public UnbakedModel getOrLoadModel(final Identifier id) {
        if (this.unbakedModels.containsKey(id)) {
            return this.unbakedModels.get(id);
        }
        if (this.modelsToLoad.contains(id)) {
            throw new IllegalStateException("Circular reference while loading " + id);
        }
        this.modelsToLoad.add(id);
        final UnbakedModel unbakedModel2 = this.unbakedModels.get(ModelLoader.MISSING);
        while (!this.modelsToLoad.isEmpty()) {
            final Identifier identifier3 = this.modelsToLoad.iterator().next();
            try {
                if (!this.unbakedModels.containsKey(identifier3)) {
                    this.loadModel(identifier3);
                }
            }
            catch (ModelLoaderException modelLoaderException4) {
                ModelLoader.LOGGER.warn(modelLoaderException4.getMessage());
                this.unbakedModels.put(identifier3, unbakedModel2);
            }
            catch (Exception exception4) {
                ModelLoader.LOGGER.warn("Unable to load model: '{}' referenced from: {}: {}", identifier3, id, exception4);
                this.unbakedModels.put(identifier3, unbakedModel2);
            }
            finally {
                this.modelsToLoad.remove(identifier3);
            }
        }
        return this.unbakedModels.getOrDefault(id, unbakedModel2);
    }
    
    private void loadModel(final Identifier id) throws Exception {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: instanceof      Lnet/minecraft/client/util/ModelIdentifier;
        //     4: ifne            18
        //     7: aload_0         /* this */
        //     8: aload_1         /* id */
        //     9: aload_0         /* this */
        //    10: aload_1         /* id */
        //    11: invokespecial   net/minecraft/client/render/model/ModelLoader.loadModelFromJson:(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/model/json/JsonUnbakedModel;
        //    14: invokespecial   net/minecraft/client/render/model/ModelLoader.putModel:(Lnet/minecraft/util/Identifier;Lnet/minecraft/client/render/model/UnbakedModel;)V
        //    17: return         
        //    18: aload_1         /* id */
        //    19: checkcast       Lnet/minecraft/client/util/ModelIdentifier;
        //    22: astore_2        /* modelIdentifier2 */
        //    23: aload_2         /* modelIdentifier2 */
        //    24: invokevirtual   net/minecraft/client/util/ModelIdentifier.getVariant:()Ljava/lang/String;
        //    27: ldc_w           "inventory"
        //    30: invokestatic    java/util/Objects.equals:(Ljava/lang/Object;Ljava/lang/Object;)Z
        //    33: ifeq            101
        //    36: new             Lnet/minecraft/util/Identifier;
        //    39: dup            
        //    40: aload_1         /* id */
        //    41: invokevirtual   net/minecraft/util/Identifier.getNamespace:()Ljava/lang/String;
        //    44: new             Ljava/lang/StringBuilder;
        //    47: dup            
        //    48: invokespecial   java/lang/StringBuilder.<init>:()V
        //    51: ldc_w           "item/"
        //    54: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //    57: aload_1         /* id */
        //    58: invokevirtual   net/minecraft/util/Identifier.getPath:()Ljava/lang/String;
        //    61: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //    64: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //    67: invokespecial   net/minecraft/util/Identifier.<init>:(Ljava/lang/String;Ljava/lang/String;)V
        //    70: astore_3        /* identifier3 */
        //    71: aload_0         /* this */
        //    72: aload_3         /* identifier3 */
        //    73: invokespecial   net/minecraft/client/render/model/ModelLoader.loadModelFromJson:(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/model/json/JsonUnbakedModel;
        //    76: astore          jsonUnbakedModel4
        //    78: aload_0         /* this */
        //    79: aload_2         /* modelIdentifier2 */
        //    80: aload           jsonUnbakedModel4
        //    82: invokespecial   net/minecraft/client/render/model/ModelLoader.putModel:(Lnet/minecraft/util/Identifier;Lnet/minecraft/client/render/model/UnbakedModel;)V
        //    85: aload_0         /* this */
        //    86: getfield        net/minecraft/client/render/model/ModelLoader.unbakedModels:Ljava/util/Map;
        //    89: aload_3         /* identifier3 */
        //    90: aload           jsonUnbakedModel4
        //    92: invokeinterface java/util/Map.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //    97: pop            
        //    98: goto            799
        //   101: new             Lnet/minecraft/util/Identifier;
        //   104: dup            
        //   105: aload_1         /* id */
        //   106: invokevirtual   net/minecraft/util/Identifier.getNamespace:()Ljava/lang/String;
        //   109: aload_1         /* id */
        //   110: invokevirtual   net/minecraft/util/Identifier.getPath:()Ljava/lang/String;
        //   113: invokespecial   net/minecraft/util/Identifier.<init>:(Ljava/lang/String;Ljava/lang/String;)V
        //   116: astore_3        /* identifier3 */
        //   117: getstatic       net/minecraft/client/render/model/ModelLoader.STATIC_DEFINITIONS:Ljava/util/Map;
        //   120: aload_3         /* identifier3 */
        //   121: invokeinterface java/util/Map.get:(Ljava/lang/Object;)Ljava/lang/Object;
        //   126: invokestatic    java/util/Optional.ofNullable:(Ljava/lang/Object;)Ljava/util/Optional;
        //   129: aload_3         /* identifier3 */
        //   130: invokedynamic   BootstrapMethod #6, get:(Lnet/minecraft/util/Identifier;)Ljava/util/function/Supplier;
        //   135: invokevirtual   java/util/Optional.orElseGet:(Ljava/util/function/Supplier;)Ljava/lang/Object;
        //   138: checkcast       Lnet/minecraft/state/StateFactory;
        //   141: astore          stateFactory4
        //   143: aload_0         /* this */
        //   144: getfield        net/minecraft/client/render/model/ModelLoader.variantMapDeserializationContext:Lnet/minecraft/client/render/model/json/ModelVariantMap$DeserializationContext;
        //   147: aload           stateFactory4
        //   149: invokevirtual   net/minecraft/client/render/model/json/ModelVariantMap$DeserializationContext.setStateFactory:(Lnet/minecraft/state/StateFactory;)V
        //   152: aload           stateFactory4
        //   154: invokevirtual   net/minecraft/state/StateFactory.getStates:()Lcom/google/common/collect/ImmutableList;
        //   157: astore          immutableList5
        //   159: invokestatic    com/google/common/collect/Maps.newHashMap:()Ljava/util/HashMap;
        //   162: astore          map6
        //   164: aload           immutableList5
        //   166: aload           map6
        //   168: aload_3         /* identifier3 */
        //   169: invokedynamic   BootstrapMethod #7, accept:(Ljava/util/Map;Lnet/minecraft/util/Identifier;)Ljava/util/function/Consumer;
        //   174: invokevirtual   com/google/common/collect/ImmutableList.forEach:(Ljava/util/function/Consumer;)V
        //   177: invokestatic    com/google/common/collect/Maps.newHashMap:()Ljava/util/HashMap;
        //   180: astore          map7
        //   182: new             Lnet/minecraft/util/Identifier;
        //   185: dup            
        //   186: aload_1         /* id */
        //   187: invokevirtual   net/minecraft/util/Identifier.getNamespace:()Ljava/lang/String;
        //   190: new             Ljava/lang/StringBuilder;
        //   193: dup            
        //   194: invokespecial   java/lang/StringBuilder.<init>:()V
        //   197: ldc_w           "blockstates/"
        //   200: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   203: aload_1         /* id */
        //   204: invokevirtual   net/minecraft/util/Identifier.getPath:()Ljava/lang/String;
        //   207: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   210: ldc_w           ".json"
        //   213: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   216: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   219: invokespecial   net/minecraft/util/Identifier.<init>:(Ljava/lang/String;Ljava/lang/String;)V
        //   222: astore          identifier8
        //   224: aload_0         /* this */
        //   225: getfield        net/minecraft/client/render/model/ModelLoader.resourceManager:Lnet/minecraft/resource/ResourceManager;
        //   228: aload           identifier8
        //   230: invokeinterface net/minecraft/resource/ResourceManager.getAllResources:(Lnet/minecraft/util/Identifier;)Ljava/util/List;
        //   235: invokeinterface java/util/List.stream:()Ljava/util/stream/Stream;
        //   240: aload_0         /* this */
        //   241: invokedynamic   BootstrapMethod #8, apply:(Lnet/minecraft/client/render/model/ModelLoader;)Ljava/util/function/Function;
        //   246: invokeinterface java/util/stream/Stream.map:(Ljava/util/function/Function;)Ljava/util/stream/Stream;
        //   251: invokestatic    java/util/stream/Collectors.toList:()Ljava/util/stream/Collector;
        //   254: invokeinterface java/util/stream/Stream.collect:(Ljava/util/stream/Collector;)Ljava/lang/Object;
        //   259: checkcast       Ljava/util/List;
        //   262: astore          list9
        //   264: goto            401
        //   267: astore          iOException10
        //   269: getstatic       net/minecraft/client/render/model/ModelLoader.LOGGER:Lorg/apache/logging/log4j/Logger;
        //   272: ldc_w           "Exception loading blockstate definition: {}: {}"
        //   275: aload           identifier8
        //   277: aload           iOException10
        //   279: invokeinterface org/apache/logging/log4j/Logger.warn:(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
        //   284: aload           map6
        //   286: invokeinterface java/util/Map.entrySet:()Ljava/util/Set;
        //   291: invokeinterface java/util/Set.iterator:()Ljava/util/Iterator;
        //   296: astore          11
        //   298: aload           11
        //   300: invokeinterface java/util/Iterator.hasNext:()Z
        //   305: ifeq            400
        //   308: aload           11
        //   310: invokeinterface java/util/Iterator.next:()Ljava/lang/Object;
        //   315: checkcast       Ljava/util/Map$Entry;
        //   318: astore          entry12
        //   320: aload           map7
        //   322: aload           entry12
        //   324: invokeinterface java/util/Map$Entry.getValue:()Ljava/lang/Object;
        //   329: invokeinterface java/util/Map.get:(Ljava/lang/Object;)Ljava/lang/Object;
        //   334: checkcast       Lnet/minecraft/client/render/model/UnbakedModel;
        //   337: astore          unbakedModel13
        //   339: aload           unbakedModel13
        //   341: ifnonnull       381
        //   344: getstatic       net/minecraft/client/render/model/ModelLoader.LOGGER:Lorg/apache/logging/log4j/Logger;
        //   347: ldc_w           "Exception loading blockstate definition: '{}' missing model for variant: '{}'"
        //   350: aload           identifier8
        //   352: aload           entry12
        //   354: invokeinterface java/util/Map$Entry.getKey:()Ljava/lang/Object;
        //   359: invokeinterface org/apache/logging/log4j/Logger.warn:(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
        //   364: aload_0         /* this */
        //   365: getfield        net/minecraft/client/render/model/ModelLoader.unbakedModels:Ljava/util/Map;
        //   368: getstatic       net/minecraft/client/render/model/ModelLoader.MISSING:Lnet/minecraft/client/util/ModelIdentifier;
        //   371: invokeinterface java/util/Map.get:(Ljava/lang/Object;)Ljava/lang/Object;
        //   376: checkcast       Lnet/minecraft/client/render/model/UnbakedModel;
        //   379: astore          unbakedModel13
        //   381: aload_0         /* this */
        //   382: aload           entry12
        //   384: invokeinterface java/util/Map$Entry.getKey:()Ljava/lang/Object;
        //   389: checkcast       Lnet/minecraft/util/Identifier;
        //   392: aload           unbakedModel13
        //   394: invokespecial   net/minecraft/client/render/model/ModelLoader.putModel:(Lnet/minecraft/util/Identifier;Lnet/minecraft/client/render/model/UnbakedModel;)V
        //   397: goto            298
        //   400: return         
        //   401: aload           list9
        //   403: invokeinterface java/util/List.iterator:()Ljava/util/Iterator;
        //   408: astore          10
        //   410: aload           10
        //   412: invokeinterface java/util/Iterator.hasNext:()Z
        //   417: ifeq            524
        //   420: aload           10
        //   422: invokeinterface java/util/Iterator.next:()Ljava/lang/Object;
        //   427: checkcast       Lcom/mojang/datafixers/util/Pair;
        //   430: astore          pair11
        //   432: aload           pair11
        //   434: invokevirtual   com/mojang/datafixers/util/Pair.getSecond:()Ljava/lang/Object;
        //   437: checkcast       Lnet/minecraft/client/render/model/json/ModelVariantMap;
        //   440: astore          modelVariantMap12
        //   442: invokestatic    com/google/common/collect/Maps.newIdentityHashMap:()Ljava/util/IdentityHashMap;
        //   445: astore          map13
        //   447: aload           modelVariantMap12
        //   449: invokevirtual   net/minecraft/client/render/model/json/ModelVariantMap.hasMultipartModel:()Z
        //   452: ifeq            479
        //   455: aload           modelVariantMap12
        //   457: invokevirtual   net/minecraft/client/render/model/json/ModelVariantMap.getMultipartMdoel:()Lnet/minecraft/client/render/model/MultipartUnbakedModel;
        //   460: astore          unbakedModel14
        //   462: aload           immutableList5
        //   464: aload           map13
        //   466: aload           unbakedModel14
        //   468: invokedynamic   BootstrapMethod #9, accept:(Ljava/util/Map;Lnet/minecraft/client/render/model/UnbakedModel;)Ljava/util/function/Consumer;
        //   473: invokevirtual   com/google/common/collect/ImmutableList.forEach:(Ljava/util/function/Consumer;)V
        //   476: goto            482
        //   479: aconst_null    
        //   480: astore          unbakedModel14
        //   482: aload           modelVariantMap12
        //   484: invokevirtual   net/minecraft/client/render/model/json/ModelVariantMap.getVariantMap:()Ljava/util/Map;
        //   487: aload_0         /* this */
        //   488: aload           immutableList5
        //   490: aload           stateFactory4
        //   492: aload           map13
        //   494: aload           unbakedModel14
        //   496: aload           modelVariantMap12
        //   498: aload           identifier8
        //   500: aload           pair11
        //   502: invokedynamic   BootstrapMethod #10, accept:(Lnet/minecraft/client/render/model/ModelLoader;Lcom/google/common/collect/ImmutableList;Lnet/minecraft/state/StateFactory;Ljava/util/Map;Lnet/minecraft/client/render/model/UnbakedModel;Lnet/minecraft/client/render/model/json/ModelVariantMap;Lnet/minecraft/util/Identifier;Lcom/mojang/datafixers/util/Pair;)Ljava/util/function/BiConsumer;
        //   507: invokeinterface java/util/Map.forEach:(Ljava/util/function/BiConsumer;)V
        //   512: aload           map7
        //   514: aload           map13
        //   516: invokeinterface java/util/Map.putAll:(Ljava/util/Map;)V
        //   521: goto            410
        //   524: aload           map6
        //   526: invokeinterface java/util/Map.entrySet:()Ljava/util/Set;
        //   531: invokeinterface java/util/Set.iterator:()Ljava/util/Iterator;
        //   536: astore          9
        //   538: aload           9
        //   540: invokeinterface java/util/Iterator.hasNext:()Z
        //   545: ifeq            640
        //   548: aload           9
        //   550: invokeinterface java/util/Iterator.next:()Ljava/lang/Object;
        //   555: checkcast       Ljava/util/Map$Entry;
        //   558: astore          entry10
        //   560: aload           map7
        //   562: aload           entry10
        //   564: invokeinterface java/util/Map$Entry.getValue:()Ljava/lang/Object;
        //   569: invokeinterface java/util/Map.get:(Ljava/lang/Object;)Ljava/lang/Object;
        //   574: checkcast       Lnet/minecraft/client/render/model/UnbakedModel;
        //   577: astore          unbakedModel11
        //   579: aload           unbakedModel11
        //   581: ifnonnull       621
        //   584: getstatic       net/minecraft/client/render/model/ModelLoader.LOGGER:Lorg/apache/logging/log4j/Logger;
        //   587: ldc_w           "Exception loading blockstate definition: '{}' missing model for variant: '{}'"
        //   590: aload           identifier8
        //   592: aload           entry10
        //   594: invokeinterface java/util/Map$Entry.getKey:()Ljava/lang/Object;
        //   599: invokeinterface org/apache/logging/log4j/Logger.warn:(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
        //   604: aload_0         /* this */
        //   605: getfield        net/minecraft/client/render/model/ModelLoader.unbakedModels:Ljava/util/Map;
        //   608: getstatic       net/minecraft/client/render/model/ModelLoader.MISSING:Lnet/minecraft/client/util/ModelIdentifier;
        //   611: invokeinterface java/util/Map.get:(Ljava/lang/Object;)Ljava/lang/Object;
        //   616: checkcast       Lnet/minecraft/client/render/model/UnbakedModel;
        //   619: astore          unbakedModel11
        //   621: aload_0         /* this */
        //   622: aload           entry10
        //   624: invokeinterface java/util/Map$Entry.getKey:()Ljava/lang/Object;
        //   629: checkcast       Lnet/minecraft/util/Identifier;
        //   632: aload           unbakedModel11
        //   634: invokespecial   net/minecraft/client/render/model/ModelLoader.putModel:(Lnet/minecraft/util/Identifier;Lnet/minecraft/client/render/model/UnbakedModel;)V
        //   637: goto            538
        //   640: goto            799
        //   643: astore          modelLoaderException9
        //   645: aload           modelLoaderException9
        //   647: athrow         
        //   648: astore          exception9
        //   650: new             Lnet/minecraft/client/render/model/ModelLoader$ModelLoaderException;
        //   653: dup            
        //   654: ldc_w           "Exception loading blockstate definition: '%s': %s"
        //   657: iconst_2       
        //   658: anewarray       Ljava/lang/Object;
        //   661: dup            
        //   662: iconst_0       
        //   663: aload           identifier8
        //   665: aastore        
        //   666: dup            
        //   667: iconst_1       
        //   668: aload           exception9
        //   670: aastore        
        //   671: invokestatic    java/lang/String.format:(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
        //   674: invokespecial   net/minecraft/client/render/model/ModelLoader$ModelLoaderException.<init>:(Ljava/lang/String;)V
        //   677: athrow         
        //   678: astore          15
        //   680: aload           map6
        //   682: invokeinterface java/util/Map.entrySet:()Ljava/util/Set;
        //   687: invokeinterface java/util/Set.iterator:()Ljava/util/Iterator;
        //   692: astore          16
        //   694: aload           16
        //   696: invokeinterface java/util/Iterator.hasNext:()Z
        //   701: ifeq            796
        //   704: aload           16
        //   706: invokeinterface java/util/Iterator.next:()Ljava/lang/Object;
        //   711: checkcast       Ljava/util/Map$Entry;
        //   714: astore          entry17
        //   716: aload           map7
        //   718: aload           entry17
        //   720: invokeinterface java/util/Map$Entry.getValue:()Ljava/lang/Object;
        //   725: invokeinterface java/util/Map.get:(Ljava/lang/Object;)Ljava/lang/Object;
        //   730: checkcast       Lnet/minecraft/client/render/model/UnbakedModel;
        //   733: astore          unbakedModel18
        //   735: aload           unbakedModel18
        //   737: ifnonnull       777
        //   740: getstatic       net/minecraft/client/render/model/ModelLoader.LOGGER:Lorg/apache/logging/log4j/Logger;
        //   743: ldc_w           "Exception loading blockstate definition: '{}' missing model for variant: '{}'"
        //   746: aload           identifier8
        //   748: aload           entry17
        //   750: invokeinterface java/util/Map$Entry.getKey:()Ljava/lang/Object;
        //   755: invokeinterface org/apache/logging/log4j/Logger.warn:(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
        //   760: aload_0         /* this */
        //   761: getfield        net/minecraft/client/render/model/ModelLoader.unbakedModels:Ljava/util/Map;
        //   764: getstatic       net/minecraft/client/render/model/ModelLoader.MISSING:Lnet/minecraft/client/util/ModelIdentifier;
        //   767: invokeinterface java/util/Map.get:(Ljava/lang/Object;)Ljava/lang/Object;
        //   772: checkcast       Lnet/minecraft/client/render/model/UnbakedModel;
        //   775: astore          unbakedModel18
        //   777: aload_0         /* this */
        //   778: aload           entry17
        //   780: invokeinterface java/util/Map$Entry.getKey:()Ljava/lang/Object;
        //   785: checkcast       Lnet/minecraft/util/Identifier;
        //   788: aload           unbakedModel18
        //   790: invokespecial   net/minecraft/client/render/model/ModelLoader.putModel:(Lnet/minecraft/util/Identifier;Lnet/minecraft/client/render/model/UnbakedModel;)V
        //   793: goto            694
        //   796: aload           15
        //   798: athrow         
        //   799: return         
        //    Exceptions:
        //  throws java.lang.Exception
        //    StackMapTable: 00 15 12 FB 00 52 FF 00 A5 00 09 07 00 02 00 00 00 00 00 07 01 89 07 01 89 07 01 08 00 01 07 00 66 FF 00 1E 00 0C 07 00 02 00 00 00 00 00 00 07 01 89 07 01 08 00 00 07 00 D9 00 00 FD 00 52 07 00 18 07 01 F2 FF 00 12 00 00 00 00 FF 00 00 00 0A 07 00 02 00 00 00 07 00 1F 07 00 F7 07 01 89 07 01 89 07 01 08 07 02 5D 00 00 FF 00 08 00 0B 07 00 02 00 00 00 07 00 1F 07 00 F7 07 01 89 07 01 89 07 01 08 00 07 00 D9 00 00 FE 00 44 07 02 7E 07 00 10 07 02 95 FC 00 02 07 02 97 FF 00 29 00 09 07 00 02 00 00 00 00 00 07 01 89 07 01 89 07 01 08 00 00 FF 00 0D 00 0A 07 00 02 00 00 00 00 00 00 07 01 89 07 01 08 07 00 D9 00 00 FD 00 52 07 00 18 07 01 F2 FF 00 12 00 00 00 00 FF 00 02 00 09 07 00 02 00 00 00 00 00 07 01 89 07 01 89 07 01 08 00 01 07 00 0B 44 07 01 EA 5D 07 02 11 FF 00 0F 00 11 07 00 02 00 00 00 00 00 00 07 01 89 07 01 08 00 00 00 00 00 00 07 02 11 07 00 D9 00 00 FD 00 52 07 00 18 07 01 F2 FF 00 12 00 10 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 07 02 11 00 00 FF 00 02 00 00 00 00
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                                                                
        //  -----  -----  -----  -----  --------------------------------------------------------------------
        //  224    264    267    401    Ljava/io/IOException;
        //  224    284    643    648    Lnet/minecraft/client/render/model/ModelLoader$ModelLoaderException;
        //  401    524    643    648    Lnet/minecraft/client/render/model/ModelLoader$ModelLoaderException;
        //  224    284    648    678    Ljava/lang/Exception;
        //  401    524    648    678    Ljava/lang/Exception;
        //  224    284    678    799    Any
        //  401    524    678    799    Any
        //  643    680    678    799    Any
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
    
    private void putModel(final Identifier id, final UnbakedModel unbakedModel) {
        this.unbakedModels.put(id, unbakedModel);
        this.modelsToLoad.addAll(unbakedModel.getModelDependencies());
    }
    
    private void addModel(final ModelIdentifier modelId) {
        final UnbakedModel unbakedModel2 = this.getOrLoadModel(modelId);
        this.unbakedModels.put(modelId, unbakedModel2);
        this.modelsToBake.put(modelId, unbakedModel2);
    }
    
    @Nullable
    public BakedModel bake(final Identifier id, final ModelBakeSettings settings) {
        final Triple<Identifier, ModelRotation, Boolean> triple3 = (Triple<Identifier, ModelRotation, Boolean>)Triple.of(id, settings.getRotation(), settings.isUvLocked());
        if (this.bakedModelCache.containsKey(triple3)) {
            return this.bakedModelCache.get(triple3);
        }
        final UnbakedModel unbakedModel4 = this.getOrLoadModel(id);
        if (unbakedModel4 instanceof JsonUnbakedModel) {
            final JsonUnbakedModel jsonUnbakedModel5 = (JsonUnbakedModel)unbakedModel4;
            if (jsonUnbakedModel5.getRootModel() == ModelLoader.GENERATION_MARKER) {
                return ModelLoader.ITEM_MODEL_GENERATOR.create(this.spriteAtlas::getSprite, jsonUnbakedModel5).bake(this, jsonUnbakedModel5, this.spriteAtlas::getSprite, settings);
            }
        }
        final BakedModel bakedModel5 = unbakedModel4.bake(this, this.spriteAtlas::getSprite, settings);
        this.bakedModelCache.put(triple3, bakedModel5);
        return bakedModel5;
    }
    
    private JsonUnbakedModel loadModelFromJson(final Identifier id) throws IOException {
        Reader reader2 = null;
        Resource resource3 = null;
        try {
            final String string4 = id.getPath();
            if ("builtin/generated".equals(string4)) {
                return ModelLoader.GENERATION_MARKER;
            }
            if ("builtin/entity".equals(string4)) {
                return ModelLoader.BLOCK_ENTITY_MARKER;
            }
            if (string4.startsWith("builtin/")) {
                final String string5 = string4.substring("builtin/".length());
                final String string6 = ModelLoader.BUILTIN_MODEL_DEFINITIONS.get(string5);
                if (string6 == null) {
                    throw new FileNotFoundException(id.toString());
                }
                reader2 = new StringReader(string6);
            }
            else {
                resource3 = this.resourceManager.getResource(new Identifier(id.getNamespace(), "models/" + id.getPath() + ".json"));
                reader2 = new InputStreamReader(resource3.getInputStream(), StandardCharsets.UTF_8);
            }
            final JsonUnbakedModel jsonUnbakedModel5 = JsonUnbakedModel.deserialize(reader2);
            jsonUnbakedModel5.id = id.toString();
            return jsonUnbakedModel5;
        }
        finally {
            IOUtils.closeQuietly(reader2);
            IOUtils.closeQuietly((Closeable)resource3);
        }
    }
    
    public Map<Identifier, BakedModel> getBakedModelMap() {
        return this.bakedModels;
    }
    
    static {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     3: dup            
        //     4: ldc_w           "block/fire_0"
        //     7: invokespecial   net/minecraft/util/Identifier.<init>:(Ljava/lang/String;)V
        //    10: putstatic       net/minecraft/client/render/model/ModelLoader.FIRE_0:Lnet/minecraft/util/Identifier;
        //    13: new             Lnet/minecraft/util/Identifier;
        //    16: dup            
        //    17: ldc_w           "block/fire_1"
        //    20: invokespecial   net/minecraft/util/Identifier.<init>:(Ljava/lang/String;)V
        //    23: putstatic       net/minecraft/client/render/model/ModelLoader.FIRE_1:Lnet/minecraft/util/Identifier;
        //    26: new             Lnet/minecraft/util/Identifier;
        //    29: dup            
        //    30: ldc_w           "block/lava_flow"
        //    33: invokespecial   net/minecraft/util/Identifier.<init>:(Ljava/lang/String;)V
        //    36: putstatic       net/minecraft/client/render/model/ModelLoader.LAVA_FLOW:Lnet/minecraft/util/Identifier;
        //    39: new             Lnet/minecraft/util/Identifier;
        //    42: dup            
        //    43: ldc_w           "block/water_flow"
        //    46: invokespecial   net/minecraft/util/Identifier.<init>:(Ljava/lang/String;)V
        //    49: putstatic       net/minecraft/client/render/model/ModelLoader.WATER_FLOW:Lnet/minecraft/util/Identifier;
        //    52: new             Lnet/minecraft/util/Identifier;
        //    55: dup            
        //    56: ldc_w           "block/water_overlay"
        //    59: invokespecial   net/minecraft/util/Identifier.<init>:(Ljava/lang/String;)V
        //    62: putstatic       net/minecraft/client/render/model/ModelLoader.WATER_OVERLAY:Lnet/minecraft/util/Identifier;
        //    65: new             Lnet/minecraft/util/Identifier;
        //    68: dup            
        //    69: ldc_w           "block/destroy_stage_0"
        //    72: invokespecial   net/minecraft/util/Identifier.<init>:(Ljava/lang/String;)V
        //    75: putstatic       net/minecraft/client/render/model/ModelLoader.DESTROY_STAGE_0:Lnet/minecraft/util/Identifier;
        //    78: new             Lnet/minecraft/util/Identifier;
        //    81: dup            
        //    82: ldc_w           "block/destroy_stage_1"
        //    85: invokespecial   net/minecraft/util/Identifier.<init>:(Ljava/lang/String;)V
        //    88: putstatic       net/minecraft/client/render/model/ModelLoader.DESTROY_STAGE_1:Lnet/minecraft/util/Identifier;
        //    91: new             Lnet/minecraft/util/Identifier;
        //    94: dup            
        //    95: ldc_w           "block/destroy_stage_2"
        //    98: invokespecial   net/minecraft/util/Identifier.<init>:(Ljava/lang/String;)V
        //   101: putstatic       net/minecraft/client/render/model/ModelLoader.DESTROY_STAGE_2:Lnet/minecraft/util/Identifier;
        //   104: new             Lnet/minecraft/util/Identifier;
        //   107: dup            
        //   108: ldc_w           "block/destroy_stage_3"
        //   111: invokespecial   net/minecraft/util/Identifier.<init>:(Ljava/lang/String;)V
        //   114: putstatic       net/minecraft/client/render/model/ModelLoader.DESTROY_STAGE_3:Lnet/minecraft/util/Identifier;
        //   117: new             Lnet/minecraft/util/Identifier;
        //   120: dup            
        //   121: ldc_w           "block/destroy_stage_4"
        //   124: invokespecial   net/minecraft/util/Identifier.<init>:(Ljava/lang/String;)V
        //   127: putstatic       net/minecraft/client/render/model/ModelLoader.DESTROY_STAGE_4:Lnet/minecraft/util/Identifier;
        //   130: new             Lnet/minecraft/util/Identifier;
        //   133: dup            
        //   134: ldc_w           "block/destroy_stage_5"
        //   137: invokespecial   net/minecraft/util/Identifier.<init>:(Ljava/lang/String;)V
        //   140: putstatic       net/minecraft/client/render/model/ModelLoader.DESTROY_STAGE_5:Lnet/minecraft/util/Identifier;
        //   143: new             Lnet/minecraft/util/Identifier;
        //   146: dup            
        //   147: ldc_w           "block/destroy_stage_6"
        //   150: invokespecial   net/minecraft/util/Identifier.<init>:(Ljava/lang/String;)V
        //   153: putstatic       net/minecraft/client/render/model/ModelLoader.DESTROY_STAGE_6:Lnet/minecraft/util/Identifier;
        //   156: new             Lnet/minecraft/util/Identifier;
        //   159: dup            
        //   160: ldc_w           "block/destroy_stage_7"
        //   163: invokespecial   net/minecraft/util/Identifier.<init>:(Ljava/lang/String;)V
        //   166: putstatic       net/minecraft/client/render/model/ModelLoader.DESTROY_STAGE_7:Lnet/minecraft/util/Identifier;
        //   169: new             Lnet/minecraft/util/Identifier;
        //   172: dup            
        //   173: ldc_w           "block/destroy_stage_8"
        //   176: invokespecial   net/minecraft/util/Identifier.<init>:(Ljava/lang/String;)V
        //   179: putstatic       net/minecraft/client/render/model/ModelLoader.DESTROY_STAGE_8:Lnet/minecraft/util/Identifier;
        //   182: new             Lnet/minecraft/util/Identifier;
        //   185: dup            
        //   186: ldc_w           "block/destroy_stage_9"
        //   189: invokespecial   net/minecraft/util/Identifier.<init>:(Ljava/lang/String;)V
        //   192: putstatic       net/minecraft/client/render/model/ModelLoader.DESTROY_STAGE_9:Lnet/minecraft/util/Identifier;
        //   195: bipush          20
        //   197: anewarray       Lnet/minecraft/util/Identifier;
        //   200: dup            
        //   201: iconst_0       
        //   202: getstatic       net/minecraft/client/render/model/ModelLoader.WATER_FLOW:Lnet/minecraft/util/Identifier;
        //   205: aastore        
        //   206: dup            
        //   207: iconst_1       
        //   208: getstatic       net/minecraft/client/render/model/ModelLoader.LAVA_FLOW:Lnet/minecraft/util/Identifier;
        //   211: aastore        
        //   212: dup            
        //   213: iconst_2       
        //   214: getstatic       net/minecraft/client/render/model/ModelLoader.WATER_OVERLAY:Lnet/minecraft/util/Identifier;
        //   217: aastore        
        //   218: dup            
        //   219: iconst_3       
        //   220: getstatic       net/minecraft/client/render/model/ModelLoader.FIRE_0:Lnet/minecraft/util/Identifier;
        //   223: aastore        
        //   224: dup            
        //   225: iconst_4       
        //   226: getstatic       net/minecraft/client/render/model/ModelLoader.FIRE_1:Lnet/minecraft/util/Identifier;
        //   229: aastore        
        //   230: dup            
        //   231: iconst_5       
        //   232: getstatic       net/minecraft/client/render/model/ModelLoader.DESTROY_STAGE_0:Lnet/minecraft/util/Identifier;
        //   235: aastore        
        //   236: dup            
        //   237: bipush          6
        //   239: getstatic       net/minecraft/client/render/model/ModelLoader.DESTROY_STAGE_1:Lnet/minecraft/util/Identifier;
        //   242: aastore        
        //   243: dup            
        //   244: bipush          7
        //   246: getstatic       net/minecraft/client/render/model/ModelLoader.DESTROY_STAGE_2:Lnet/minecraft/util/Identifier;
        //   249: aastore        
        //   250: dup            
        //   251: bipush          8
        //   253: getstatic       net/minecraft/client/render/model/ModelLoader.DESTROY_STAGE_3:Lnet/minecraft/util/Identifier;
        //   256: aastore        
        //   257: dup            
        //   258: bipush          9
        //   260: getstatic       net/minecraft/client/render/model/ModelLoader.DESTROY_STAGE_4:Lnet/minecraft/util/Identifier;
        //   263: aastore        
        //   264: dup            
        //   265: bipush          10
        //   267: getstatic       net/minecraft/client/render/model/ModelLoader.DESTROY_STAGE_5:Lnet/minecraft/util/Identifier;
        //   270: aastore        
        //   271: dup            
        //   272: bipush          11
        //   274: getstatic       net/minecraft/client/render/model/ModelLoader.DESTROY_STAGE_6:Lnet/minecraft/util/Identifier;
        //   277: aastore        
        //   278: dup            
        //   279: bipush          12
        //   281: getstatic       net/minecraft/client/render/model/ModelLoader.DESTROY_STAGE_7:Lnet/minecraft/util/Identifier;
        //   284: aastore        
        //   285: dup            
        //   286: bipush          13
        //   288: getstatic       net/minecraft/client/render/model/ModelLoader.DESTROY_STAGE_8:Lnet/minecraft/util/Identifier;
        //   291: aastore        
        //   292: dup            
        //   293: bipush          14
        //   295: getstatic       net/minecraft/client/render/model/ModelLoader.DESTROY_STAGE_9:Lnet/minecraft/util/Identifier;
        //   298: aastore        
        //   299: dup            
        //   300: bipush          15
        //   302: new             Lnet/minecraft/util/Identifier;
        //   305: dup            
        //   306: ldc_w           "item/empty_armor_slot_helmet"
        //   309: invokespecial   net/minecraft/util/Identifier.<init>:(Ljava/lang/String;)V
        //   312: aastore        
        //   313: dup            
        //   314: bipush          16
        //   316: new             Lnet/minecraft/util/Identifier;
        //   319: dup            
        //   320: ldc_w           "item/empty_armor_slot_chestplate"
        //   323: invokespecial   net/minecraft/util/Identifier.<init>:(Ljava/lang/String;)V
        //   326: aastore        
        //   327: dup            
        //   328: bipush          17
        //   330: new             Lnet/minecraft/util/Identifier;
        //   333: dup            
        //   334: ldc_w           "item/empty_armor_slot_leggings"
        //   337: invokespecial   net/minecraft/util/Identifier.<init>:(Ljava/lang/String;)V
        //   340: aastore        
        //   341: dup            
        //   342: bipush          18
        //   344: new             Lnet/minecraft/util/Identifier;
        //   347: dup            
        //   348: ldc_w           "item/empty_armor_slot_boots"
        //   351: invokespecial   net/minecraft/util/Identifier.<init>:(Ljava/lang/String;)V
        //   354: aastore        
        //   355: dup            
        //   356: bipush          19
        //   358: new             Lnet/minecraft/util/Identifier;
        //   361: dup            
        //   362: ldc_w           "item/empty_armor_slot_shield"
        //   365: invokespecial   net/minecraft/util/Identifier.<init>:(Ljava/lang/String;)V
        //   368: aastore        
        //   369: invokestatic    com/google/common/collect/Sets.newHashSet:([Ljava/lang/Object;)Ljava/util/HashSet;
        //   372: putstatic       net/minecraft/client/render/model/ModelLoader.DEFAULT_TEXTURES:Ljava/util/Set;
        //   375: invokestatic    org/apache/logging/log4j/LogManager.getLogger:()Lorg/apache/logging/log4j/Logger;
        //   378: putstatic       net/minecraft/client/render/model/ModelLoader.LOGGER:Lorg/apache/logging/log4j/Logger;
        //   381: new             Lnet/minecraft/client/util/ModelIdentifier;
        //   384: dup            
        //   385: ldc_w           "builtin/missing"
        //   388: ldc_w           "missing"
        //   391: invokespecial   net/minecraft/client/util/ModelIdentifier.<init>:(Ljava/lang/String;Ljava/lang/String;)V
        //   394: putstatic       net/minecraft/client/render/model/ModelLoader.MISSING:Lnet/minecraft/client/util/ModelIdentifier;
        //   397: new             Ljava/lang/StringBuilder;
        //   400: dup            
        //   401: invokespecial   java/lang/StringBuilder.<init>:()V
        //   404: ldc_w           "{    'textures': {       'particle': '"
        //   407: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   410: invokestatic    net/minecraft/client/texture/MissingSprite.getMissingSpriteId:()Lnet/minecraft/util/Identifier;
        //   413: invokevirtual   net/minecraft/util/Identifier.getPath:()Ljava/lang/String;
        //   416: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   419: ldc_w           "',       'missingno': '"
        //   422: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   425: invokestatic    net/minecraft/client/texture/MissingSprite.getMissingSpriteId:()Lnet/minecraft/util/Identifier;
        //   428: invokevirtual   net/minecraft/util/Identifier.getPath:()Ljava/lang/String;
        //   431: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   434: ldc_w           "'    },    'elements': [         {  'from': [ 0, 0, 0 ],            'to': [ 16, 16, 16 ],            'faces': {                'down':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'down',  'texture': '#missingno' },                'up':    { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'up',    'texture': '#missingno' },                'north': { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'north', 'texture': '#missingno' },                'south': { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'south', 'texture': '#missingno' },                'west':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'west',  'texture': '#missingno' },                'east':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'east',  'texture': '#missingno' }            }        }    ]}"
        //   437: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   440: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   443: bipush          39
        //   445: bipush          34
        //   447: invokevirtual   java/lang/String.replace:(CC)Ljava/lang/String;
        //   450: putstatic       net/minecraft/client/render/model/ModelLoader.MISSING_DEFINITION:Ljava/lang/String;
        //   453: ldc_w           "missing"
        //   456: getstatic       net/minecraft/client/render/model/ModelLoader.MISSING_DEFINITION:Ljava/lang/String;
        //   459: invokestatic    com/google/common/collect/ImmutableMap.of:(Ljava/lang/Object;Ljava/lang/Object;)Lcom/google/common/collect/ImmutableMap;
        //   462: invokestatic    com/google/common/collect/Maps.newHashMap:(Ljava/util/Map;)Ljava/util/HashMap;
        //   465: putstatic       net/minecraft/client/render/model/ModelLoader.BUILTIN_MODEL_DEFINITIONS:Ljava/util/Map;
        //   468: bipush          44
        //   470: invokestatic    com/google/common/base/Splitter.on:(C)Lcom/google/common/base/Splitter;
        //   473: putstatic       net/minecraft/client/render/model/ModelLoader.COMMA_SPLITTER:Lcom/google/common/base/Splitter;
        //   476: bipush          61
        //   478: invokestatic    com/google/common/base/Splitter.on:(C)Lcom/google/common/base/Splitter;
        //   481: iconst_2       
        //   482: invokevirtual   com/google/common/base/Splitter.limit:(I)Lcom/google/common/base/Splitter;
        //   485: putstatic       net/minecraft/client/render/model/ModelLoader.KEY_VALUE_SPLITTER:Lcom/google/common/base/Splitter;
        //   488: ldc_w           "{}"
        //   491: invokestatic    net/minecraft/client/render/model/json/JsonUnbakedModel.deserialize:(Ljava/lang/String;)Lnet/minecraft/client/render/model/json/JsonUnbakedModel;
        //   494: invokedynamic   BootstrapMethod #16, accept:()Ljava/util/function/Consumer;
        //   499: invokestatic    net/minecraft/util/SystemUtil.consume:(Ljava/lang/Object;Ljava/util/function/Consumer;)Ljava/lang/Object;
        //   502: checkcast       Lnet/minecraft/client/render/model/json/JsonUnbakedModel;
        //   505: putstatic       net/minecraft/client/render/model/ModelLoader.GENERATION_MARKER:Lnet/minecraft/client/render/model/json/JsonUnbakedModel;
        //   508: ldc_w           "{}"
        //   511: invokestatic    net/minecraft/client/render/model/json/JsonUnbakedModel.deserialize:(Ljava/lang/String;)Lnet/minecraft/client/render/model/json/JsonUnbakedModel;
        //   514: invokedynamic   BootstrapMethod #17, accept:()Ljava/util/function/Consumer;
        //   519: invokestatic    net/minecraft/util/SystemUtil.consume:(Ljava/lang/Object;Ljava/util/function/Consumer;)Ljava/lang/Object;
        //   522: checkcast       Lnet/minecraft/client/render/model/json/JsonUnbakedModel;
        //   525: putstatic       net/minecraft/client/render/model/ModelLoader.BLOCK_ENTITY_MARKER:Lnet/minecraft/client/render/model/json/JsonUnbakedModel;
        //   528: new             Lnet/minecraft/state/StateFactory$Builder;
        //   531: dup            
        //   532: getstatic       net/minecraft/block/Blocks.AIR:Lnet/minecraft/block/Block;
        //   535: invokespecial   net/minecraft/state/StateFactory$Builder.<init>:(Ljava/lang/Object;)V
        //   538: iconst_1       
        //   539: anewarray       Lnet/minecraft/state/property/Property;
        //   542: dup            
        //   543: iconst_0       
        //   544: ldc_w           "map"
        //   547: invokestatic    net/minecraft/state/property/BooleanProperty.create:(Ljava/lang/String;)Lnet/minecraft/state/property/BooleanProperty;
        //   550: aastore        
        //   551: invokevirtual   net/minecraft/state/StateFactory$Builder.add:([Lnet/minecraft/state/property/Property;)Lnet/minecraft/state/StateFactory$Builder;
        //   554: invokedynamic   BootstrapMethod #18, create:()Lnet/minecraft/state/StateFactory$Factory;
        //   559: invokevirtual   net/minecraft/state/StateFactory$Builder.build:(Lnet/minecraft/state/StateFactory$Factory;)Lnet/minecraft/state/StateFactory;
        //   562: putstatic       net/minecraft/client/render/model/ModelLoader.ITEM_FRAME_STATE_FACTORY:Lnet/minecraft/state/StateFactory;
        //   565: new             Lnet/minecraft/client/render/model/json/ItemModelGenerator;
        //   568: dup            
        //   569: invokespecial   net/minecraft/client/render/model/json/ItemModelGenerator.<init>:()V
        //   572: putstatic       net/minecraft/client/render/model/ModelLoader.ITEM_MODEL_GENERATOR:Lnet/minecraft/client/render/model/json/ItemModelGenerator;
        //   575: new             Lnet/minecraft/util/Identifier;
        //   578: dup            
        //   579: ldc_w           "item_frame"
        //   582: invokespecial   net/minecraft/util/Identifier.<init>:(Ljava/lang/String;)V
        //   585: getstatic       net/minecraft/client/render/model/ModelLoader.ITEM_FRAME_STATE_FACTORY:Lnet/minecraft/state/StateFactory;
        //   588: invokestatic    com/google/common/collect/ImmutableMap.of:(Ljava/lang/Object;Ljava/lang/Object;)Lcom/google/common/collect/ImmutableMap;
        //   591: putstatic       net/minecraft/client/render/model/ModelLoader.STATIC_DEFINITIONS:Ljava/util/Map;
        //   594: return         
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
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:778)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferCall(TypeAnalysis.java:2669)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1029)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:803)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:770)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:766)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1072)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:803)
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:672)
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:655)
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:365)
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
    
    @Environment(EnvType.CLIENT)
    static class ModelLoaderException extends RuntimeException
    {
        public ModelLoaderException(final String string) {
            super(string);
        }
    }
}

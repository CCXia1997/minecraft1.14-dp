package net.minecraft.client.render.model.json;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.common.collect.Lists;
import com.google.gson.JsonParseException;
import com.google.gson.JsonObject;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonDeserializer;
import java.lang.reflect.Type;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.util.math.Direction;
import net.minecraft.client.render.model.BasicBakedModel;
import net.minecraft.client.render.model.BuiltinBakedModel;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.texture.Sprite;
import java.util.Objects;
import net.minecraft.client.texture.MissingSprite;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Function;
import java.util.Iterator;
import java.util.Set;
import com.google.common.collect.Sets;
import java.util.Collection;
import net.minecraft.client.render.model.ModelLoader;
import java.io.StringReader;
import net.minecraft.util.JsonHelper;
import java.io.Reader;
import net.minecraft.util.Identifier;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.List;
import com.google.common.annotations.VisibleForTesting;
import com.google.gson.Gson;
import net.minecraft.client.render.model.BakedQuadFactory;
import org.apache.logging.log4j.Logger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.UnbakedModel;

@Environment(EnvType.CLIENT)
public class JsonUnbakedModel implements UnbakedModel
{
    private static final Logger LOGGER;
    private static final BakedQuadFactory QUAD_FACTORY;
    @VisibleForTesting
    static final Gson GSON;
    private final List<ModelElement> elements;
    private final boolean depthInGui;
    private final boolean ambientOcclusion;
    private final ModelTransformation transformations;
    private final List<ModelItemOverride> overrides;
    public String id;
    @VisibleForTesting
    protected final Map<String, String> textureMap;
    @Nullable
    protected JsonUnbakedModel parent;
    @Nullable
    protected Identifier parentId;
    
    public static JsonUnbakedModel deserialize(final Reader reader) {
        return JsonHelper.<JsonUnbakedModel>deserialize(JsonUnbakedModel.GSON, reader, JsonUnbakedModel.class);
    }
    
    public static JsonUnbakedModel deserialize(final String string) {
        return deserialize(new StringReader(string));
    }
    
    public JsonUnbakedModel(@Nullable final Identifier parentId, final List<ModelElement> elements, final Map<String, String> textureMap, final boolean ambientOcclusion, final boolean depthInGui, final ModelTransformation transformations, final List<ModelItemOverride> overrides) {
        this.id = "";
        this.elements = elements;
        this.ambientOcclusion = ambientOcclusion;
        this.depthInGui = depthInGui;
        this.textureMap = textureMap;
        this.parentId = parentId;
        this.transformations = transformations;
        this.overrides = overrides;
    }
    
    public List<ModelElement> getElements() {
        if (this.elements.isEmpty() && this.parent != null) {
            return this.parent.getElements();
        }
        return this.elements;
    }
    
    public boolean useAmbientOcclusion() {
        if (this.parent != null) {
            return this.parent.useAmbientOcclusion();
        }
        return this.ambientOcclusion;
    }
    
    public boolean hasDepthInGui() {
        return this.depthInGui;
    }
    
    public List<ModelItemOverride> getOverrides() {
        return this.overrides;
    }
    
    private ModelItemPropertyOverrideList compileOverrides(final ModelLoader modelLoader, final JsonUnbakedModel parent) {
        if (this.overrides.isEmpty()) {
            return ModelItemPropertyOverrideList.EMPTY;
        }
        return new ModelItemPropertyOverrideList(modelLoader, parent, modelLoader::getOrLoadModel, this.overrides);
    }
    
    @Override
    public Collection<Identifier> getModelDependencies() {
        final Set<Identifier> set1 = Sets.newHashSet();
        for (final ModelItemOverride modelItemOverride3 : this.overrides) {
            set1.add(modelItemOverride3.getModelId());
        }
        if (this.parentId != null) {
            set1.add(this.parentId);
        }
        return set1;
    }
    
    @Override
    public Collection<Identifier> getTextureDependencies(final Function<Identifier, UnbakedModel> unbakedModelGetter, final Set<String> unresolvedTextureReferences) {
        final Set<UnbakedModel> set3 = Sets.newLinkedHashSet();
        for (JsonUnbakedModel jsonUnbakedModel4 = this; jsonUnbakedModel4.parentId != null && jsonUnbakedModel4.parent == null; jsonUnbakedModel4 = jsonUnbakedModel4.parent) {
            set3.add(jsonUnbakedModel4);
            UnbakedModel unbakedModel5 = unbakedModelGetter.apply(jsonUnbakedModel4.parentId);
            if (unbakedModel5 == null) {
                JsonUnbakedModel.LOGGER.warn("No parent '{}' while loading model '{}'", this.parentId, jsonUnbakedModel4);
            }
            if (set3.contains(unbakedModel5)) {
                JsonUnbakedModel.LOGGER.warn("Found 'parent' loop while loading model '{}' in chain: {} -> {}", jsonUnbakedModel4, set3.stream().map(Object::toString).collect(Collectors.joining(" -> ")), this.parentId);
                unbakedModel5 = null;
            }
            if (unbakedModel5 == null) {
                jsonUnbakedModel4.parentId = ModelLoader.MISSING;
                unbakedModel5 = unbakedModelGetter.apply(jsonUnbakedModel4.parentId);
            }
            if (!(unbakedModel5 instanceof JsonUnbakedModel)) {
                throw new IllegalStateException("BlockModel parent has to be a block model.");
            }
            jsonUnbakedModel4.parent = (JsonUnbakedModel)unbakedModel5;
        }
        final Set<Identifier> set4 = Sets.<Identifier>newHashSet(new Identifier(this.resolveTexture("particle")));
        for (final ModelElement modelElement7 : this.getElements()) {
            for (final ModelElementFace modelElementFace9 : modelElement7.faces.values()) {
                final String string2 = this.resolveTexture(modelElementFace9.textureId);
                if (Objects.equals(string2, MissingSprite.getMissingSpriteId().toString())) {
                    unresolvedTextureReferences.add(String.format("%s in %s", modelElementFace9.textureId, this.id));
                }
                set4.add(new Identifier(string2));
            }
        }
        final UnbakedModel unbakedModel6;
        final Set set5;
        this.overrides.forEach(modelItemOverride -> {
            unbakedModel6 = unbakedModelGetter.apply(modelItemOverride.getModelId());
            if (Objects.equals(unbakedModel6, this)) {
                return;
            }
            else {
                set5.addAll(unbakedModel6.getTextureDependencies(unbakedModelGetter, unresolvedTextureReferences));
                return;
            }
        });
        if (this.getRootModel() == ModelLoader.GENERATION_MARKER) {
            ItemModelGenerator.LAYERS.forEach(string -> set4.add(new Identifier(this.resolveTexture(string))));
        }
        return set4;
    }
    
    @Override
    public BakedModel bake(final ModelLoader loader, final Function<Identifier, Sprite> textureGetter, final ModelBakeSettings rotationContainer) {
        return this.bake(loader, this, textureGetter, rotationContainer);
    }
    
    public BakedModel bake(final ModelLoader loader, final JsonUnbakedModel parent, final Function<Identifier, Sprite> textureGetter, final ModelBakeSettings settings) {
        final Sprite sprite5 = textureGetter.apply(new Identifier(this.resolveTexture("particle")));
        if (this.getRootModel() == ModelLoader.BLOCK_ENTITY_MARKER) {
            return new BuiltinBakedModel(this.getTransformations(), this.compileOverrides(loader, parent), sprite5);
        }
        final BasicBakedModel.Builder builder6 = new BasicBakedModel.Builder(this, this.compileOverrides(loader, parent)).setParticle(sprite5);
        for (final ModelElement modelElement8 : this.getElements()) {
            for (final Direction direction10 : modelElement8.faces.keySet()) {
                final ModelElementFace modelElementFace11 = modelElement8.faces.get(direction10);
                final Sprite sprite6 = textureGetter.apply(new Identifier(this.resolveTexture(modelElementFace11.textureId)));
                if (modelElementFace11.cullFace == null) {
                    builder6.addQuad(createQuad(modelElement8, modelElementFace11, sprite6, direction10, settings));
                }
                else {
                    builder6.addQuad(settings.getRotation().apply(modelElementFace11.cullFace), createQuad(modelElement8, modelElementFace11, sprite6, direction10, settings));
                }
            }
        }
        return builder6.build();
    }
    
    private static BakedQuad createQuad(final ModelElement element, final ModelElementFace elementFace, final Sprite sprite, final Direction side, final ModelBakeSettings settings) {
        return JsonUnbakedModel.QUAD_FACTORY.bake(element.from, element.to, elementFace, sprite, side, settings, element.rotation, element.shade);
    }
    
    public boolean textureExists(final String name) {
        return !MissingSprite.getMissingSpriteId().toString().equals(this.resolveTexture(name));
    }
    
    public String resolveTexture(String name) {
        if (!this.isTextureReference(name)) {
            name = '#' + name;
        }
        return this.resolveTexture(name, new TextureResolutionContext(this));
    }
    
    private String resolveTexture(final String name, final TextureResolutionContext context) {
        if (!this.isTextureReference(name)) {
            return name;
        }
        if (this == context.current) {
            JsonUnbakedModel.LOGGER.warn("Unable to resolve texture due to upward reference: {} in {}", name, this.id);
            return MissingSprite.getMissingSpriteId().toString();
        }
        String string3 = this.textureMap.get(name.substring(1));
        if (string3 == null && this.parent != null) {
            string3 = this.parent.resolveTexture(name, context);
        }
        context.current = this;
        if (string3 != null && this.isTextureReference(string3)) {
            string3 = context.root.resolveTexture(string3, context);
        }
        if (string3 == null || this.isTextureReference(string3)) {
            return MissingSprite.getMissingSpriteId().toString();
        }
        return string3;
    }
    
    private boolean isTextureReference(final String name) {
        return name.charAt(0) == '#';
    }
    
    public JsonUnbakedModel getRootModel() {
        return (this.parent == null) ? this : this.parent.getRootModel();
    }
    
    public ModelTransformation getTransformations() {
        final Transformation transformation1 = this.getTransformation(ModelTransformation.Type.b);
        final Transformation transformation2 = this.getTransformation(ModelTransformation.Type.c);
        final Transformation transformation3 = this.getTransformation(ModelTransformation.Type.d);
        final Transformation transformation4 = this.getTransformation(ModelTransformation.Type.e);
        final Transformation transformation5 = this.getTransformation(ModelTransformation.Type.f);
        final Transformation transformation6 = this.getTransformation(ModelTransformation.Type.g);
        final Transformation transformation7 = this.getTransformation(ModelTransformation.Type.h);
        final Transformation transformation8 = this.getTransformation(ModelTransformation.Type.FIXED);
        return new ModelTransformation(transformation1, transformation2, transformation3, transformation4, transformation5, transformation6, transformation7, transformation8);
    }
    
    private Transformation getTransformation(final ModelTransformation.Type type) {
        if (this.parent != null && !this.transformations.isTransformationDefined(type)) {
            return this.parent.getTransformation(type);
        }
        return this.transformations.getTransformation(type);
    }
    
    @Override
    public String toString() {
        return this.id;
    }
    
    static {
        LOGGER = LogManager.getLogger();
        QUAD_FACTORY = new BakedQuadFactory();
        GSON = new GsonBuilder().registerTypeAdapter(JsonUnbakedModel.class, new Deserializer()).registerTypeAdapter(ModelElement.class, new ModelElement.Deserializer()).registerTypeAdapter(ModelElementFace.class, new ModelElementFace.Deserializer()).registerTypeAdapter(ModelElementTexture.class, new ModelElementTexture.Deserializer()).registerTypeAdapter(Transformation.class, new Transformation.Deserializer()).registerTypeAdapter(ModelTransformation.class, new ModelTransformation.Deserializer()).registerTypeAdapter(ModelItemOverride.class, new ModelItemOverride.Deserializer()).create();
    }
    
    @Environment(EnvType.CLIENT)
    static final class TextureResolutionContext
    {
        public final JsonUnbakedModel root;
        public JsonUnbakedModel current;
        
        private TextureResolutionContext(final JsonUnbakedModel root) {
            this.root = root;
        }
    }
    
    @Environment(EnvType.CLIENT)
    public static class Deserializer implements JsonDeserializer<JsonUnbakedModel>
    {
        public JsonUnbakedModel a(final JsonElement jsonElement, final Type type, final JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            final JsonObject jsonObject4 = jsonElement.getAsJsonObject();
            final List<ModelElement> list5 = this.deserializeElements(jsonDeserializationContext, jsonObject4);
            final String string6 = this.deserializeParent(jsonObject4);
            final Map<String, String> map7 = this.deserializeTextures(jsonObject4);
            final boolean boolean8 = this.deserializeAmbientOcclusion(jsonObject4);
            ModelTransformation modelTransformation9 = ModelTransformation.NONE;
            if (jsonObject4.has("display")) {
                final JsonObject jsonObject5 = JsonHelper.getObject(jsonObject4, "display");
                modelTransformation9 = jsonDeserializationContext.<ModelTransformation>deserialize(jsonObject5, ModelTransformation.class);
            }
            final List<ModelItemOverride> list6 = this.deserializeOverrides(jsonDeserializationContext, jsonObject4);
            final Identifier identifier11 = string6.isEmpty() ? null : new Identifier(string6);
            return new JsonUnbakedModel(identifier11, list5, map7, boolean8, true, modelTransformation9, list6);
        }
        
        protected List<ModelItemOverride> deserializeOverrides(final JsonDeserializationContext context, final JsonObject object) {
            final List<ModelItemOverride> list3 = Lists.newArrayList();
            if (object.has("overrides")) {
                final JsonArray jsonArray4 = JsonHelper.getArray(object, "overrides");
                for (final JsonElement jsonElement6 : jsonArray4) {
                    list3.add(context.<ModelItemOverride>deserialize(jsonElement6, ModelItemOverride.class));
                }
            }
            return list3;
        }
        
        private Map<String, String> deserializeTextures(final JsonObject object) {
            final Map<String, String> map2 = Maps.newHashMap();
            if (object.has("textures")) {
                final JsonObject jsonObject3 = object.getAsJsonObject("textures");
                for (final Map.Entry<String, JsonElement> entry5 : jsonObject3.entrySet()) {
                    map2.put(entry5.getKey(), entry5.getValue().getAsString());
                }
            }
            return map2;
        }
        
        private String deserializeParent(final JsonObject object) {
            return JsonHelper.getString(object, "parent", "");
        }
        
        protected boolean deserializeAmbientOcclusion(final JsonObject object) {
            return JsonHelper.getBoolean(object, "ambientocclusion", true);
        }
        
        protected List<ModelElement> deserializeElements(final JsonDeserializationContext context, final JsonObject object) {
            final List<ModelElement> list3 = Lists.newArrayList();
            if (object.has("elements")) {
                for (final JsonElement jsonElement5 : JsonHelper.getArray(object, "elements")) {
                    list3.add(context.<ModelElement>deserialize(jsonElement5, ModelElement.class));
                }
            }
            return list3;
        }
    }
}

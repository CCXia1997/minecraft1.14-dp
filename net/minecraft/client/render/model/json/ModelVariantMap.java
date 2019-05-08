package net.minecraft.client.render.model.json;

import javax.annotation.Nullable;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonDeserializer;
import java.lang.reflect.Type;
import com.google.gson.GsonBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Block;
import net.minecraft.state.StateFactory;
import com.google.gson.Gson;
import java.util.Iterator;
import java.util.List;
import com.google.common.collect.Maps;
import net.minecraft.util.JsonHelper;
import java.io.Reader;
import net.minecraft.client.render.model.MultipartUnbakedModel;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ModelVariantMap
{
    private final Map<String, WeightedUnbakedModel> variantMap;
    private MultipartUnbakedModel multipartModel;
    
    public static ModelVariantMap deserialize(final DeserializationContext context, final Reader reader) {
        return JsonHelper.<ModelVariantMap>deserialize(context.gson, reader, ModelVariantMap.class);
    }
    
    public ModelVariantMap(final Map<String, WeightedUnbakedModel> variantMap, final MultipartUnbakedModel multipartModel) {
        this.variantMap = Maps.newLinkedHashMap();
        this.multipartModel = multipartModel;
        this.variantMap.putAll(variantMap);
    }
    
    public ModelVariantMap(final List<ModelVariantMap> variantMapList) {
        this.variantMap = Maps.newLinkedHashMap();
        ModelVariantMap modelVariantMap2 = null;
        for (final ModelVariantMap modelVariantMap3 : variantMapList) {
            if (modelVariantMap3.hasMultipartModel()) {
                this.variantMap.clear();
                modelVariantMap2 = modelVariantMap3;
            }
            this.variantMap.putAll(modelVariantMap3.variantMap);
        }
        if (modelVariantMap2 != null) {
            this.multipartModel = modelVariantMap2.multipartModel;
        }
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof ModelVariantMap) {
            final ModelVariantMap modelVariantMap2 = (ModelVariantMap)o;
            if (this.variantMap.equals(modelVariantMap2.variantMap)) {
                return this.hasMultipartModel() ? this.multipartModel.equals(modelVariantMap2.multipartModel) : (!modelVariantMap2.hasMultipartModel());
            }
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return 31 * this.variantMap.hashCode() + (this.hasMultipartModel() ? this.multipartModel.hashCode() : 0);
    }
    
    public Map<String, WeightedUnbakedModel> getVariantMap() {
        return this.variantMap;
    }
    
    public boolean hasMultipartModel() {
        return this.multipartModel != null;
    }
    
    public MultipartUnbakedModel getMultipartMdoel() {
        return this.multipartModel;
    }
    
    @Environment(EnvType.CLIENT)
    public static final class DeserializationContext
    {
        protected final Gson gson;
        private StateFactory<Block, BlockState> stateFactory;
        
        public DeserializationContext() {
            this.gson = new GsonBuilder().registerTypeAdapter(ModelVariantMap.class, new Deserializer()).registerTypeAdapter(ModelVariant.class, new ModelVariant.Deserializer()).registerTypeAdapter(WeightedUnbakedModel.class, new WeightedUnbakedModel.Deserializer()).registerTypeAdapter(MultipartUnbakedModel.class, new MultipartUnbakedModel.Deserializer(this)).registerTypeAdapter(MultipartModelComponent.class, new MultipartModelComponent.Deserializer()).create();
        }
        
        public StateFactory<Block, BlockState> getStateFactory() {
            return this.stateFactory;
        }
        
        public void setStateFactory(final StateFactory<Block, BlockState> stateFactory) {
            this.stateFactory = stateFactory;
        }
    }
    
    @Environment(EnvType.CLIENT)
    public static class Deserializer implements JsonDeserializer<ModelVariantMap>
    {
        public ModelVariantMap a(final JsonElement functionJson, final Type unused, final JsonDeserializationContext context) throws JsonParseException {
            final JsonObject jsonObject4 = functionJson.getAsJsonObject();
            final Map<String, WeightedUnbakedModel> map5 = this.deserializeVariants(context, jsonObject4);
            final MultipartUnbakedModel multipartUnbakedModel6 = this.deserializeMultipart(context, jsonObject4);
            if (map5.isEmpty() && (multipartUnbakedModel6 == null || multipartUnbakedModel6.getModels().isEmpty())) {
                throw new JsonParseException("Neither 'variants' nor 'multipart' found");
            }
            return new ModelVariantMap(map5, multipartUnbakedModel6);
        }
        
        protected Map<String, WeightedUnbakedModel> deserializeVariants(final JsonDeserializationContext context, final JsonObject object) {
            final Map<String, WeightedUnbakedModel> map3 = Maps.newHashMap();
            if (object.has("variants")) {
                final JsonObject jsonObject4 = JsonHelper.getObject(object, "variants");
                for (final Map.Entry<String, JsonElement> entry6 : jsonObject4.entrySet()) {
                    map3.put(entry6.getKey(), context.<WeightedUnbakedModel>deserialize(entry6.getValue(), WeightedUnbakedModel.class));
                }
            }
            return map3;
        }
        
        @Nullable
        protected MultipartUnbakedModel deserializeMultipart(final JsonDeserializationContext context, final JsonObject object) {
            if (!object.has("multipart")) {
                return null;
            }
            final JsonArray jsonArray3 = JsonHelper.getArray(object, "multipart");
            return context.<MultipartUnbakedModel>deserialize(jsonArray3, MultipartUnbakedModel.class);
        }
    }
}

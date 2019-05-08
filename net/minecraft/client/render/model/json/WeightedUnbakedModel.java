package net.minecraft.client.render.model.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonParseException;
import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import java.lang.reflect.Type;
import com.google.gson.JsonElement;
import com.google.gson.JsonDeserializer;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import java.util.Iterator;
import net.minecraft.client.render.model.WeightedBakedModel;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.render.model.ModelLoader;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Function;
import net.minecraft.util.Identifier;
import java.util.Collection;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.UnbakedModel;

@Environment(EnvType.CLIENT)
public class WeightedUnbakedModel implements UnbakedModel
{
    private final List<ModelVariant> variants;
    
    public WeightedUnbakedModel(final List<ModelVariant> variants) {
        this.variants = variants;
    }
    
    public List<ModelVariant> getVariants() {
        return this.variants;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof WeightedUnbakedModel) {
            final WeightedUnbakedModel weightedUnbakedModel2 = (WeightedUnbakedModel)o;
            return this.variants.equals(weightedUnbakedModel2.variants);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.variants.hashCode();
    }
    
    @Override
    public Collection<Identifier> getModelDependencies() {
        return this.getVariants().stream().map(ModelVariant::getLocation).collect(Collectors.toSet());
    }
    
    @Override
    public Collection<Identifier> getTextureDependencies(final Function<Identifier, UnbakedModel> unbakedModelGetter, final Set<String> unresolvedTextureReferences) {
        return this.getVariants().stream().map(ModelVariant::getLocation).distinct().flatMap(identifier -> unbakedModelGetter.apply(identifier).getTextureDependencies(unbakedModelGetter, unresolvedTextureReferences).stream()).collect(Collectors.toSet());
    }
    
    @Nullable
    @Override
    public BakedModel bake(final ModelLoader loader, final Function<Identifier, Sprite> textureGetter, final ModelBakeSettings rotationContainer) {
        if (this.getVariants().isEmpty()) {
            return null;
        }
        final WeightedBakedModel.Builder builder4 = new WeightedBakedModel.Builder();
        for (final ModelVariant modelVariant6 : this.getVariants()) {
            final BakedModel bakedModel7 = loader.bake(modelVariant6.getLocation(), modelVariant6);
            builder4.add(bakedModel7, modelVariant6.getWeight());
        }
        return builder4.getFirst();
    }
    
    @Environment(EnvType.CLIENT)
    public static class Deserializer implements JsonDeserializer<WeightedUnbakedModel>
    {
        public WeightedUnbakedModel a(final JsonElement functionJson, final Type unused, final JsonDeserializationContext context) throws JsonParseException {
            final List<ModelVariant> list4 = Lists.newArrayList();
            if (functionJson.isJsonArray()) {
                final JsonArray jsonArray5 = functionJson.getAsJsonArray();
                if (jsonArray5.size() == 0) {
                    throw new JsonParseException("Empty variant array");
                }
                for (final JsonElement jsonElement7 : jsonArray5) {
                    list4.add(context.<ModelVariant>deserialize(jsonElement7, ModelVariant.class));
                }
            }
            else {
                list4.add(context.<ModelVariant>deserialize(functionJson, ModelVariant.class));
            }
            return new WeightedUnbakedModel(list4);
        }
    }
}

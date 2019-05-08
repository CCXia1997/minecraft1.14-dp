package net.minecraft.client.render.model;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonParseException;
import com.google.gson.JsonDeserializationContext;
import java.lang.reflect.Type;
import com.google.gson.JsonElement;
import net.minecraft.client.render.model.json.ModelVariantMap;
import com.google.gson.JsonDeserializer;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.client.texture.Sprite;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import net.minecraft.util.Identifier;
import java.util.Collection;
import java.util.Objects;
import java.util.Iterator;
import com.google.common.collect.Sets;
import net.minecraft.client.render.model.json.WeightedUnbakedModel;
import java.util.Set;
import net.minecraft.client.render.model.json.MultipartModelComponent;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.block.Block;
import net.minecraft.state.StateFactory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class MultipartUnbakedModel implements UnbakedModel
{
    private final StateFactory<Block, BlockState> stateFactory;
    private final List<MultipartModelComponent> components;
    
    public MultipartUnbakedModel(final StateFactory<Block, BlockState> stateFactory, final List<MultipartModelComponent> components) {
        this.stateFactory = stateFactory;
        this.components = components;
    }
    
    public List<MultipartModelComponent> getComponents() {
        return this.components;
    }
    
    public Set<WeightedUnbakedModel> getModels() {
        final Set<WeightedUnbakedModel> set1 = Sets.newHashSet();
        for (final MultipartModelComponent multipartModelComponent3 : this.components) {
            set1.add(multipartModelComponent3.getModel());
        }
        return set1;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof MultipartUnbakedModel) {
            final MultipartUnbakedModel multipartUnbakedModel2 = (MultipartUnbakedModel)o;
            return Objects.equals(this.stateFactory, multipartUnbakedModel2.stateFactory) && Objects.equals(this.components, multipartUnbakedModel2.components);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.stateFactory, this.components);
    }
    
    @Override
    public Collection<Identifier> getModelDependencies() {
        return this.getComponents().stream().flatMap(multipartModelComponent -> multipartModelComponent.getModel().getModelDependencies().stream()).collect(Collectors.toSet());
    }
    
    @Override
    public Collection<Identifier> getTextureDependencies(final Function<Identifier, UnbakedModel> unbakedModelGetter, final Set<String> unresolvedTextureReferences) {
        return this.getComponents().stream().flatMap(multipartModelComponent -> multipartModelComponent.getModel().getTextureDependencies(unbakedModelGetter, unresolvedTextureReferences).stream()).collect(Collectors.toSet());
    }
    
    @Nullable
    @Override
    public BakedModel bake(final ModelLoader loader, final Function<Identifier, Sprite> textureGetter, final ModelBakeSettings rotationContainer) {
        final MultipartBakedModel.Builder builder4 = new MultipartBakedModel.Builder();
        for (final MultipartModelComponent multipartModelComponent6 : this.getComponents()) {
            final BakedModel bakedModel7 = multipartModelComponent6.getModel().bake(loader, textureGetter, rotationContainer);
            if (bakedModel7 != null) {
                builder4.addComponent(multipartModelComponent6.getPredicate(this.stateFactory), bakedModel7);
            }
        }
        return builder4.build();
    }
    
    @Environment(EnvType.CLIENT)
    public static class Deserializer implements JsonDeserializer<MultipartUnbakedModel>
    {
        private final ModelVariantMap.DeserializationContext context;
        
        public Deserializer(final ModelVariantMap.DeserializationContext context) {
            this.context = context;
        }
        
        public MultipartUnbakedModel a(final JsonElement functionJson, final Type unused, final JsonDeserializationContext context) throws JsonParseException {
            return new MultipartUnbakedModel(this.context.getStateFactory(), this.deserializeComponents(context, functionJson.getAsJsonArray()));
        }
        
        private List<MultipartModelComponent> deserializeComponents(final JsonDeserializationContext context, final JsonArray array) {
            final List<MultipartModelComponent> list3 = Lists.newArrayList();
            for (final JsonElement jsonElement5 : array) {
                list3.add(context.<MultipartModelComponent>deserialize(jsonElement5, MultipartModelComponent.class));
            }
            return list3;
        }
    }
}

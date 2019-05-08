package net.minecraft.client.render.model.json;

import com.google.common.annotations.VisibleForTesting;
import java.util.Set;
import java.util.Map;
import com.google.common.collect.Streams;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Function;
import java.util.List;
import net.minecraft.util.JsonHelper;
import com.google.gson.JsonParseException;
import com.google.gson.JsonObject;
import com.google.gson.JsonDeserializationContext;
import java.lang.reflect.Type;
import com.google.gson.JsonElement;
import com.google.gson.JsonDeserializer;
import java.util.function.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.block.Block;
import net.minecraft.state.StateFactory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class MultipartModelComponent
{
    private final MultipartModelSelector selector;
    private final WeightedUnbakedModel model;
    
    public MultipartModelComponent(final MultipartModelSelector selector, final WeightedUnbakedModel model) {
        if (selector == null) {
            throw new IllegalArgumentException("Missing condition for selector");
        }
        if (model == null) {
            throw new IllegalArgumentException("Missing variant for selector");
        }
        this.selector = selector;
        this.model = model;
    }
    
    public WeightedUnbakedModel getModel() {
        return this.model;
    }
    
    public Predicate<BlockState> getPredicate(final StateFactory<Block, BlockState> stateFactory) {
        return this.selector.getPredicate(stateFactory);
    }
    
    @Override
    public boolean equals(final Object o) {
        return this == o;
    }
    
    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }
    
    @Environment(EnvType.CLIENT)
    public static class Deserializer implements JsonDeserializer<MultipartModelComponent>
    {
        public MultipartModelComponent a(final JsonElement jsonElement, final Type type, final JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            final JsonObject jsonObject4 = jsonElement.getAsJsonObject();
            return new MultipartModelComponent(this.deserializeSelectorOrDefault(jsonObject4), jsonDeserializationContext.<WeightedUnbakedModel>deserialize(jsonObject4.get("apply"), WeightedUnbakedModel.class));
        }
        
        private MultipartModelSelector deserializeSelectorOrDefault(final JsonObject object) {
            if (object.has("when")) {
                return deserializeSelector(JsonHelper.getObject(object, "when"));
            }
            return MultipartModelSelector.TRUE;
        }
        
        @VisibleForTesting
        static MultipartModelSelector deserializeSelector(final JsonObject object) {
            final Set<Map.Entry<String, JsonElement>> set2 = object.entrySet();
            if (set2.isEmpty()) {
                throw new JsonParseException("No elements found in selector");
            }
            if (set2.size() != 1) {
                return new AndMultipartModelSelector(set2.stream().map(Deserializer::createStatePropertySelector).collect(Collectors.toList()));
            }
            if (object.has("OR")) {
                final List<MultipartModelSelector> list3 = Streams.stream((Iterable<Object>)JsonHelper.getArray(object, "OR")).map(jsonElement -> deserializeSelector(jsonElement.getAsJsonObject())).collect(Collectors.toList());
                return new OrMultipartModelSelector(list3);
            }
            if (object.has("AND")) {
                final List<MultipartModelSelector> list3 = Streams.stream((Iterable<Object>)JsonHelper.getArray(object, "AND")).map(jsonElement -> deserializeSelector(jsonElement.getAsJsonObject())).collect(Collectors.toList());
                return new AndMultipartModelSelector(list3);
            }
            return createStatePropertySelector(set2.iterator().next());
        }
        
        private static MultipartModelSelector createStatePropertySelector(final Map.Entry<String, JsonElement> entry) {
            return new SimpleMultipartModelSelector(entry.getKey(), entry.getValue().getAsString());
        }
    }
}

package net.minecraft.client.render.model.json;

import com.google.common.collect.Maps;
import com.google.gson.JsonParseException;
import com.google.gson.JsonObject;
import net.minecraft.util.JsonHelper;
import com.google.gson.JsonDeserializationContext;
import java.lang.reflect.Type;
import com.google.gson.JsonElement;
import com.google.gson.JsonDeserializer;
import net.minecraft.item.ItemPropertyGetter;
import java.util.Iterator;
import net.minecraft.item.Item;
import net.minecraft.entity.LivingEntity;
import javax.annotation.Nullable;
import net.minecraft.world.World;
import net.minecraft.item.ItemStack;
import java.util.Map;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ModelItemOverride
{
    private final Identifier modelId;
    private final Map<Identifier, Float> minPropertyValues;
    
    public ModelItemOverride(final Identifier modelId, final Map<Identifier, Float> minPropertyValues) {
        this.modelId = modelId;
        this.minPropertyValues = minPropertyValues;
    }
    
    public Identifier getModelId() {
        return this.modelId;
    }
    
    boolean matches(final ItemStack stack, @Nullable final World world, @Nullable final LivingEntity entity) {
        final Item item4 = stack.getItem();
        for (final Map.Entry<Identifier, Float> entry6 : this.minPropertyValues.entrySet()) {
            final ItemPropertyGetter itemPropertyGetter7 = item4.getProperty(entry6.getKey());
            if (itemPropertyGetter7 == null || itemPropertyGetter7.call(stack, world, entity) < entry6.getValue()) {
                return false;
            }
        }
        return true;
    }
    
    @Environment(EnvType.CLIENT)
    public static class Deserializer implements JsonDeserializer<ModelItemOverride>
    {
        protected Deserializer() {
        }
        
        public ModelItemOverride a(final JsonElement functionJson, final Type unused, final JsonDeserializationContext context) throws JsonParseException {
            final JsonObject jsonObject4 = functionJson.getAsJsonObject();
            final Identifier identifier5 = new Identifier(JsonHelper.getString(jsonObject4, "model"));
            final Map<Identifier, Float> map6 = this.deserializeMinPropertyValues(jsonObject4);
            return new ModelItemOverride(identifier5, map6);
        }
        
        protected Map<Identifier, Float> deserializeMinPropertyValues(final JsonObject object) {
            final Map<Identifier, Float> map2 = Maps.newLinkedHashMap();
            final JsonObject jsonObject3 = JsonHelper.getObject(object, "predicate");
            for (final Map.Entry<String, JsonElement> entry5 : jsonObject3.entrySet()) {
                map2.put(new Identifier(entry5.getKey()), JsonHelper.asFloat(entry5.getValue(), entry5.getKey()));
            }
            return map2;
        }
    }
}

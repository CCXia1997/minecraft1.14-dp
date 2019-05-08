package net.minecraft.client.render.model.json;

import net.minecraft.util.JsonHelper;
import com.google.gson.JsonParseException;
import com.google.gson.JsonObject;
import com.google.gson.JsonDeserializationContext;
import java.lang.reflect.Type;
import com.google.gson.JsonElement;
import com.google.gson.JsonDeserializer;
import net.minecraft.client.render.model.ModelRotation;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.ModelBakeSettings;

@Environment(EnvType.CLIENT)
public class ModelVariant implements ModelBakeSettings
{
    private final Identifier location;
    private final ModelRotation rotation;
    private final boolean uvLock;
    private final int weight;
    
    public ModelVariant(final Identifier location, final ModelRotation rotation, final boolean uvLock, final int weight) {
        this.location = location;
        this.rotation = rotation;
        this.uvLock = uvLock;
        this.weight = weight;
    }
    
    public Identifier getLocation() {
        return this.location;
    }
    
    @Override
    public ModelRotation getRotation() {
        return this.rotation;
    }
    
    @Override
    public boolean isUvLocked() {
        return this.uvLock;
    }
    
    public int getWeight() {
        return this.weight;
    }
    
    @Override
    public String toString() {
        return "Variant{modelLocation=" + this.location + ", rotation=" + this.rotation + ", uvLock=" + this.uvLock + ", weight=" + this.weight + '}';
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof ModelVariant) {
            final ModelVariant modelVariant2 = (ModelVariant)o;
            return this.location.equals(modelVariant2.location) && this.rotation == modelVariant2.rotation && this.uvLock == modelVariant2.uvLock && this.weight == modelVariant2.weight;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int integer1 = this.location.hashCode();
        integer1 = 31 * integer1 + this.rotation.hashCode();
        integer1 = 31 * integer1 + Boolean.valueOf(this.uvLock).hashCode();
        integer1 = 31 * integer1 + this.weight;
        return integer1;
    }
    
    @Environment(EnvType.CLIENT)
    public static class Deserializer implements JsonDeserializer<ModelVariant>
    {
        public ModelVariant a(final JsonElement functionJson, final Type unused, final JsonDeserializationContext context) throws JsonParseException {
            final JsonObject jsonObject4 = functionJson.getAsJsonObject();
            final Identifier identifier5 = this.deserializeModel(jsonObject4);
            final ModelRotation modelRotation6 = this.deserializeRotation(jsonObject4);
            final boolean boolean7 = this.deserializeUvLock(jsonObject4);
            final int integer8 = this.deserializeWeight(jsonObject4);
            return new ModelVariant(identifier5, modelRotation6, boolean7, integer8);
        }
        
        private boolean deserializeUvLock(final JsonObject object) {
            return JsonHelper.getBoolean(object, "uvlock", false);
        }
        
        protected ModelRotation deserializeRotation(final JsonObject object) {
            final int integer2 = JsonHelper.getInt(object, "x", 0);
            final int integer3 = JsonHelper.getInt(object, "y", 0);
            final ModelRotation modelRotation4 = ModelRotation.get(integer2, integer3);
            if (modelRotation4 == null) {
                throw new JsonParseException("Invalid BlockModelRotation x: " + integer2 + ", y: " + integer3);
            }
            return modelRotation4;
        }
        
        protected Identifier deserializeModel(final JsonObject object) {
            return new Identifier(JsonHelper.getString(object, "model"));
        }
        
        protected int deserializeWeight(final JsonObject object) {
            final int integer2 = JsonHelper.getInt(object, "weight", 1);
            if (integer2 < 1) {
                throw new JsonParseException("Invalid weight " + integer2 + " found, expected integer >= 1");
            }
            return integer2;
        }
    }
}

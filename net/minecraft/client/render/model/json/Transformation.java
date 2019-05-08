package net.minecraft.client.render.model.json;

import com.google.gson.JsonArray;
import net.minecraft.util.JsonHelper;
import com.google.gson.JsonParseException;
import com.google.gson.JsonObject;
import com.google.gson.JsonDeserializationContext;
import java.lang.reflect.Type;
import com.google.gson.JsonElement;
import com.google.gson.JsonDeserializer;
import net.minecraft.client.util.math.Vector3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class Transformation
{
    public static final Transformation NONE;
    public final Vector3f rotation;
    public final Vector3f translation;
    public final Vector3f scale;
    
    public Transformation(final Vector3f rotation, final Vector3f translation, final Vector3f scale) {
        this.rotation = new Vector3f(rotation);
        this.translation = new Vector3f(translation);
        this.scale = new Vector3f(scale);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (this.getClass() == o.getClass()) {
            final Transformation transformation2 = (Transformation)o;
            return this.rotation.equals(transformation2.rotation) && this.scale.equals(transformation2.scale) && this.translation.equals(transformation2.translation);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int integer1 = this.rotation.hashCode();
        integer1 = 31 * integer1 + this.translation.hashCode();
        integer1 = 31 * integer1 + this.scale.hashCode();
        return integer1;
    }
    
    static {
        NONE = new Transformation(new Vector3f(), new Vector3f(), new Vector3f(1.0f, 1.0f, 1.0f));
    }
    
    @Environment(EnvType.CLIENT)
    public static class Deserializer implements JsonDeserializer<Transformation>
    {
        private static final Vector3f DEFAULT_ROATATION;
        private static final Vector3f DEFAULT_TRANSLATION;
        private static final Vector3f DEFAULT_SCALE;
        
        protected Deserializer() {
        }
        
        public Transformation a(final JsonElement functionJson, final Type unused, final JsonDeserializationContext context) throws JsonParseException {
            final JsonObject jsonObject4 = functionJson.getAsJsonObject();
            final Vector3f vector3f5 = this.parseVector3f(jsonObject4, "rotation", Deserializer.DEFAULT_ROATATION);
            final Vector3f vector3f6 = this.parseVector3f(jsonObject4, "translation", Deserializer.DEFAULT_TRANSLATION);
            vector3f6.scale(0.0625f);
            vector3f6.clamp(-5.0f, 5.0f);
            final Vector3f vector3f7 = this.parseVector3f(jsonObject4, "scale", Deserializer.DEFAULT_SCALE);
            vector3f7.clamp(-4.0f, 4.0f);
            return new Transformation(vector3f5, vector3f6, vector3f7);
        }
        
        private Vector3f parseVector3f(final JsonObject json, final String key, final Vector3f default_) {
            if (!json.has(key)) {
                return default_;
            }
            final JsonArray jsonArray4 = JsonHelper.getArray(json, key);
            if (jsonArray4.size() != 3) {
                throw new JsonParseException("Expected 3 " + key + " values, found: " + jsonArray4.size());
            }
            final float[] arr5 = new float[3];
            for (int integer6 = 0; integer6 < arr5.length; ++integer6) {
                arr5[integer6] = JsonHelper.asFloat(jsonArray4.get(integer6), key + "[" + integer6 + "]");
            }
            return new Vector3f(arr5[0], arr5[1], arr5[2]);
        }
        
        static {
            DEFAULT_ROATATION = new Vector3f(0.0f, 0.0f, 0.0f);
            DEFAULT_TRANSLATION = new Vector3f(0.0f, 0.0f, 0.0f);
            DEFAULT_SCALE = new Vector3f(1.0f, 1.0f, 1.0f);
        }
    }
}

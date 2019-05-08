package net.minecraft.client.render.model.json;

import com.google.gson.JsonArray;
import net.minecraft.util.JsonHelper;
import com.google.gson.JsonParseException;
import com.google.gson.JsonObject;
import com.google.gson.JsonDeserializationContext;
import java.lang.reflect.Type;
import com.google.gson.JsonElement;
import com.google.gson.JsonDeserializer;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ModelElementTexture
{
    public float[] uvs;
    public final int rotation;
    
    public ModelElementTexture(@Nullable final float[] uvs, final int rotation) {
        this.uvs = uvs;
        this.rotation = rotation;
    }
    
    public float getU(final int rotation) {
        if (this.uvs == null) {
            throw new NullPointerException("uvs");
        }
        final int integer2 = this.getRotatedUVIndex(rotation);
        return this.uvs[(integer2 == 0 || integer2 == 1) ? 0 : 2];
    }
    
    public float getV(final int rotation) {
        if (this.uvs == null) {
            throw new NullPointerException("uvs");
        }
        final int integer2 = this.getRotatedUVIndex(rotation);
        return this.uvs[(integer2 == 0 || integer2 == 3) ? 1 : 3];
    }
    
    private int getRotatedUVIndex(final int rotation) {
        return (rotation + this.rotation / 90) % 4;
    }
    
    public int c(final int integer) {
        return (integer + 4 - this.rotation / 90) % 4;
    }
    
    public void setUvs(final float[] uvs) {
        if (this.uvs == null) {
            this.uvs = uvs;
        }
    }
    
    @Environment(EnvType.CLIENT)
    public static class Deserializer implements JsonDeserializer<ModelElementTexture>
    {
        protected Deserializer() {
        }
        
        public ModelElementTexture a(final JsonElement functionJson, final Type unused, final JsonDeserializationContext context) throws JsonParseException {
            final JsonObject jsonObject4 = functionJson.getAsJsonObject();
            final float[] arr5 = this.deserializeUVs(jsonObject4);
            final int integer6 = this.deserializeRotation(jsonObject4);
            return new ModelElementTexture(arr5, integer6);
        }
        
        protected int deserializeRotation(final JsonObject object) {
            final int integer2 = JsonHelper.getInt(object, "rotation", 0);
            if (integer2 < 0 || integer2 % 90 != 0 || integer2 / 90 > 3) {
                throw new JsonParseException("Invalid rotation " + integer2 + " found, only 0/90/180/270 allowed");
            }
            return integer2;
        }
        
        @Nullable
        private float[] deserializeUVs(final JsonObject object) {
            if (!object.has("uv")) {
                return null;
            }
            final JsonArray jsonArray2 = JsonHelper.getArray(object, "uv");
            if (jsonArray2.size() != 4) {
                throw new JsonParseException("Expected 4 uv values, found: " + jsonArray2.size());
            }
            final float[] arr3 = new float[4];
            for (int integer4 = 0; integer4 < arr3.length; ++integer4) {
                arr3[integer4] = JsonHelper.asFloat(jsonArray2.get(integer4), "uv[" + integer4 + "]");
            }
            return arr3;
        }
    }
}

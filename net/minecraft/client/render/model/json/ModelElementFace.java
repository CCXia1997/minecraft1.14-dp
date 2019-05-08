package net.minecraft.client.render.model.json;

import net.minecraft.util.JsonHelper;
import com.google.gson.JsonParseException;
import com.google.gson.JsonObject;
import com.google.gson.JsonDeserializationContext;
import java.lang.reflect.Type;
import com.google.gson.JsonElement;
import com.google.gson.JsonDeserializer;
import javax.annotation.Nullable;
import net.minecraft.util.math.Direction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ModelElementFace
{
    public final Direction cullFace;
    public final int tintIndex;
    public final String textureId;
    public final ModelElementTexture textureData;
    
    public ModelElementFace(@Nullable final Direction cullFace, final int tintIndex, final String textureId, final ModelElementTexture textureData) {
        this.cullFace = cullFace;
        this.tintIndex = tintIndex;
        this.textureId = textureId;
        this.textureData = textureData;
    }
    
    @Environment(EnvType.CLIENT)
    public static class Deserializer implements JsonDeserializer<ModelElementFace>
    {
        protected Deserializer() {
        }
        
        public ModelElementFace a(final JsonElement functionJson, final Type unused, final JsonDeserializationContext context) throws JsonParseException {
            final JsonObject jsonObject4 = functionJson.getAsJsonObject();
            final Direction direction5 = this.deserializeCullFace(jsonObject4);
            final int integer6 = this.deserializeTintIndex(jsonObject4);
            final String string7 = this.deserializeTexture(jsonObject4);
            final ModelElementTexture modelElementTexture8 = context.<ModelElementTexture>deserialize(jsonObject4, ModelElementTexture.class);
            return new ModelElementFace(direction5, integer6, string7, modelElementTexture8);
        }
        
        protected int deserializeTintIndex(final JsonObject object) {
            return JsonHelper.getInt(object, "tintindex", -1);
        }
        
        private String deserializeTexture(final JsonObject object) {
            return JsonHelper.getString(object, "texture");
        }
        
        @Nullable
        private Direction deserializeCullFace(final JsonObject object) {
            final String string2 = JsonHelper.getString(object, "cullface", "");
            return Direction.byName(string2);
        }
    }
}

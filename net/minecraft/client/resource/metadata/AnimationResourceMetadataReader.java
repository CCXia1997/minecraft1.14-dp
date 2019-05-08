package net.minecraft.client.resource.metadata;

import com.google.gson.JsonElement;
import com.google.gson.JsonArray;
import java.util.List;
import com.google.gson.JsonParseException;
import org.apache.commons.lang3.Validate;
import net.minecraft.util.JsonHelper;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.metadata.ResourceMetadataReader;

@Environment(EnvType.CLIENT)
public class AnimationResourceMetadataReader implements ResourceMetadataReader<AnimationResourceMetadata>
{
    @Override
    public AnimationResourceMetadata fromJson(final JsonObject jsonObject) {
        final List<AnimationFrameResourceMetadata> list2 = Lists.newArrayList();
        final int integer3 = JsonHelper.getInt(jsonObject, "frametime", 1);
        if (integer3 != 1) {
            Validate.inclusiveBetween(1L, 2147483647L, (long)integer3, "Invalid default frame time");
        }
        if (jsonObject.has("frames")) {
            try {
                final JsonArray jsonArray4 = JsonHelper.getArray(jsonObject, "frames");
                for (int integer4 = 0; integer4 < jsonArray4.size(); ++integer4) {
                    final JsonElement jsonElement6 = jsonArray4.get(integer4);
                    final AnimationFrameResourceMetadata animationFrameResourceMetadata7 = this.readFrameMetadata(integer4, jsonElement6);
                    if (animationFrameResourceMetadata7 != null) {
                        list2.add(animationFrameResourceMetadata7);
                    }
                }
            }
            catch (ClassCastException classCastException4) {
                throw new JsonParseException("Invalid animation->frames: expected array, was " + jsonObject.get("frames"), classCastException4);
            }
        }
        final int integer5 = JsonHelper.getInt(jsonObject, "width", -1);
        int integer4 = JsonHelper.getInt(jsonObject, "height", -1);
        if (integer5 != -1) {
            Validate.inclusiveBetween(1L, 2147483647L, (long)integer5, "Invalid width");
        }
        if (integer4 != -1) {
            Validate.inclusiveBetween(1L, 2147483647L, (long)integer4, "Invalid height");
        }
        final boolean boolean6 = JsonHelper.getBoolean(jsonObject, "interpolate", false);
        return new AnimationResourceMetadata(list2, integer5, integer4, integer3, boolean6);
    }
    
    private AnimationFrameResourceMetadata readFrameMetadata(final int frame, final JsonElement json) {
        if (json.isJsonPrimitive()) {
            return new AnimationFrameResourceMetadata(JsonHelper.asInt(json, "frames[" + frame + "]"));
        }
        if (json.isJsonObject()) {
            final JsonObject jsonObject3 = JsonHelper.asObject(json, "frames[" + frame + "]");
            final int integer4 = JsonHelper.getInt(jsonObject3, "time", -1);
            if (jsonObject3.has("time")) {
                Validate.inclusiveBetween(1L, 2147483647L, (long)integer4, "Invalid frame time");
            }
            final int integer5 = JsonHelper.getInt(jsonObject3, "index");
            Validate.inclusiveBetween(0L, 2147483647L, (long)integer5, "Invalid frame index");
            return new AnimationFrameResourceMetadata(integer5, integer4);
        }
        return null;
    }
    
    @Override
    public String getKey() {
        return "animation";
    }
}

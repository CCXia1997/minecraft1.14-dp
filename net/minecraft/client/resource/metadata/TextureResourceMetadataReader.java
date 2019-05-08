package net.minecraft.client.resource.metadata;

import net.minecraft.util.JsonHelper;
import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.metadata.ResourceMetadataReader;

@Environment(EnvType.CLIENT)
public class TextureResourceMetadataReader implements ResourceMetadataReader<TextureResourceMetadata>
{
    @Override
    public TextureResourceMetadata fromJson(final JsonObject jsonObject) {
        final boolean boolean2 = JsonHelper.getBoolean(jsonObject, "blur", false);
        final boolean boolean3 = JsonHelper.getBoolean(jsonObject, "clamp", false);
        return new TextureResourceMetadata(boolean2, boolean3);
    }
    
    @Override
    public String getKey() {
        return "texture";
    }
}

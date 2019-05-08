package net.minecraft.resource.metadata;

import net.minecraft.util.JsonHelper;
import com.google.gson.JsonParseException;
import net.minecraft.text.TextComponent;
import com.google.gson.JsonObject;

public class PackResourceMetadataReader implements ResourceMetadataReader<PackResourceMetadata>
{
    @Override
    public PackResourceMetadata fromJson(final JsonObject jsonObject) {
        final TextComponent textComponent2 = TextComponent.Serializer.fromJson(jsonObject.get("description"));
        if (textComponent2 == null) {
            throw new JsonParseException("Invalid/missing description!");
        }
        final int integer3 = JsonHelper.getInt(jsonObject, "pack_format");
        return new PackResourceMetadata(textComponent2, integer3);
    }
    
    @Override
    public String getKey() {
        return "pack";
    }
}

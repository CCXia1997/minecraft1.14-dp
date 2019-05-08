package net.minecraft.client.resource.metadata;

import java.util.Iterator;
import java.util.Set;
import java.util.Collection;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.util.JsonHelper;
import com.google.gson.JsonParseException;
import com.google.gson.JsonElement;
import java.util.Map;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.metadata.ResourceMetadataReader;

@Environment(EnvType.CLIENT)
public class LanguageResourceMetadataReader implements ResourceMetadataReader<LanguageResourceMetadata>
{
    @Override
    public LanguageResourceMetadata fromJson(final JsonObject jsonObject) {
        final Set<LanguageDefinition> set2 = Sets.newHashSet();
        for (final Map.Entry<String, JsonElement> entry4 : jsonObject.entrySet()) {
            final String string5 = entry4.getKey();
            if (string5.length() > 16) {
                throw new JsonParseException("Invalid language->'" + string5 + "': language code must not be more than " + 16 + " characters long");
            }
            final JsonObject jsonObject2 = JsonHelper.asObject(entry4.getValue(), "language");
            final String string6 = JsonHelper.getString(jsonObject2, "region");
            final String string7 = JsonHelper.getString(jsonObject2, "name");
            final boolean boolean9 = JsonHelper.getBoolean(jsonObject2, "bidirectional", false);
            if (string6.isEmpty()) {
                throw new JsonParseException("Invalid language->'" + string5 + "'->region: empty value");
            }
            if (string7.isEmpty()) {
                throw new JsonParseException("Invalid language->'" + string5 + "'->name: empty value");
            }
            if (!set2.add(new LanguageDefinition(string5, string6, string7, boolean9))) {
                throw new JsonParseException("Duplicate language->'" + string5 + "' defined");
            }
        }
        return new LanguageResourceMetadata(set2);
    }
    
    @Override
    public String getKey() {
        return "language";
    }
}

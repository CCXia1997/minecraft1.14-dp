package net.minecraft.client.render.entity.feature;

import net.minecraft.util.JsonHelper;
import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.metadata.ResourceMetadataReader;

@Environment(EnvType.CLIENT)
public class VillagerResourceMetadataReader implements ResourceMetadataReader<VillagerResourceMetadata>
{
    @Override
    public VillagerResourceMetadata fromJson(final JsonObject jsonObject) {
        return new VillagerResourceMetadata(VillagerResourceMetadata.HatType.from(JsonHelper.getString(jsonObject, "hat", "none")));
    }
    
    @Override
    public String getKey() {
        return "villager";
    }
}

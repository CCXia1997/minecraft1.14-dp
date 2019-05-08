package net.minecraft.client.particle;

import com.google.gson.JsonElement;
import java.util.stream.Collector;
import com.google.common.collect.ImmutableList;
import java.util.function.Function;
import com.google.common.collect.Streams;
import com.google.gson.JsonArray;
import net.minecraft.util.JsonHelper;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.util.Identifier;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ParticleTextureData
{
    @Nullable
    private final List<Identifier> textureList;
    
    private ParticleTextureData(@Nullable final List<Identifier> list) {
        this.textureList = list;
    }
    
    @Nullable
    public List<Identifier> getTextureList() {
        return this.textureList;
    }
    
    public static ParticleTextureData load(final JsonObject jsonObject) {
        final JsonArray jsonArray2 = JsonHelper.getArray(jsonObject, "textures", null);
        List<Identifier> list3;
        if (jsonArray2 != null) {
            list3 = Streams.stream((Iterable<Object>)jsonArray2).map(jsonElement -> JsonHelper.asString(jsonElement, "texture")).map(Identifier::new).collect(ImmutableList.toImmutableList());
        }
        else {
            list3 = null;
        }
        return new ParticleTextureData(list3);
    }
}

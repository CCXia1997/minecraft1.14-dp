package net.minecraft.client.font;

import org.apache.logging.log4j.LogManager;
import javax.annotation.Nullable;
import java.nio.ByteBuffer;
import net.minecraft.resource.Resource;
import java.io.IOException;
import com.mojang.blaze3d.platform.TextureUtil;
import net.minecraft.resource.ResourceManager;
import com.google.gson.JsonElement;
import com.google.gson.JsonArray;
import net.minecraft.util.JsonHelper;
import com.google.gson.JsonParseException;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Logger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class TrueTypeFontLoader implements FontLoader
{
    private static final Logger LOGGER;
    private final Identifier filename;
    private final float size;
    private final float oversample;
    private final float shiftX;
    private final float shiftY;
    private final String excludedCharacters;
    
    public TrueTypeFontLoader(final Identifier filename, final float size, final float oversample, final float shiftX, final float shiftY, final String excludedCharacters) {
        this.filename = filename;
        this.size = size;
        this.oversample = oversample;
        this.shiftX = shiftX;
        this.shiftY = shiftY;
        this.excludedCharacters = excludedCharacters;
    }
    
    public static FontLoader fromJson(final JsonObject jsonObject) {
        float float2 = 0.0f;
        float float3 = 0.0f;
        if (jsonObject.has("shift")) {
            final JsonArray jsonArray4 = jsonObject.getAsJsonArray("shift");
            if (jsonArray4.size() != 2) {
                throw new JsonParseException("Expected 2 elements in 'shift', found " + jsonArray4.size());
            }
            float2 = JsonHelper.asFloat(jsonArray4.get(0), "shift[0]");
            float3 = JsonHelper.asFloat(jsonArray4.get(1), "shift[1]");
        }
        final StringBuilder stringBuilder4 = new StringBuilder();
        if (jsonObject.has("skip")) {
            final JsonElement jsonElement5 = jsonObject.get("skip");
            if (jsonElement5.isJsonArray()) {
                final JsonArray jsonArray5 = JsonHelper.asArray(jsonElement5, "skip");
                for (int integer7 = 0; integer7 < jsonArray5.size(); ++integer7) {
                    stringBuilder4.append(JsonHelper.asString(jsonArray5.get(integer7), "skip[" + integer7 + "]"));
                }
            }
            else {
                stringBuilder4.append(JsonHelper.asString(jsonElement5, "skip"));
            }
        }
        return new TrueTypeFontLoader(new Identifier(JsonHelper.getString(jsonObject, "file")), JsonHelper.getFloat(jsonObject, "size", 11.0f), JsonHelper.getFloat(jsonObject, "oversample", 1.0f), float2, float3, stringBuilder4.toString());
    }
    
    @Nullable
    @Override
    public Font load(final ResourceManager resourceManager) {
        try (final Resource resource2 = resourceManager.getResource(new Identifier(this.filename.getNamespace(), "font/" + this.filename.getPath()))) {
            TrueTypeFontLoader.LOGGER.info("Loading font");
            final ByteBuffer byteBuffer4 = TextureUtil.readResource(resource2.getInputStream());
            byteBuffer4.flip();
            TrueTypeFontLoader.LOGGER.info("Reading font");
            return new TrueTypeFont(TrueTypeFont.getSTBTTFontInfo(byteBuffer4), this.size, this.oversample, this.shiftX, this.shiftY, this.excludedCharacters);
        }
        catch (IOException iOException2) {
            TrueTypeFontLoader.LOGGER.error("Couldn't load truetype font {}", this.filename, iOException2);
            return null;
        }
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}

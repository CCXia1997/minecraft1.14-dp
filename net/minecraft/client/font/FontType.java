package net.minecraft.client.font;

import net.minecraft.util.SystemUtil;
import com.google.common.collect.Maps;
import java.util.HashMap;
import com.google.gson.JsonObject;
import java.util.function.Function;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public enum FontType
{
    BITMAP("bitmap", (Function<JsonObject, FontLoader>)TextureFont.Loader::fromJson), 
    TTF("ttf", TrueTypeFontLoader::fromJson), 
    LEGACY_UNICODE("legacy_unicode", UnicodeTextureFont.Loader::fromJson);
    
    private static final Map<String, FontType> REGISTRY;
    private final String id;
    private final Function<JsonObject, FontLoader> loaderFactory;
    
    private FontType(final String id, final Function<JsonObject, FontLoader> factory) {
        this.id = id;
        this.loaderFactory = factory;
    }
    
    public static FontType byId(final String id) {
        final FontType fontType2 = FontType.REGISTRY.get(id);
        if (fontType2 == null) {
            throw new IllegalArgumentException("Invalid type: " + id);
        }
        return fontType2;
    }
    
    public FontLoader createLoader(final JsonObject jsonObject) {
        return this.loaderFactory.apply(jsonObject);
    }
    
    static {
        final FontType[] array;
        int length;
        int i = 0;
        FontType fontType5;
        REGISTRY = SystemUtil.<Map<String, FontType>>consume(Maps.newHashMap(), hashMap -> {
            values();
            for (length = array.length; i < length; ++i) {
                fontType5 = array[i];
                hashMap.put(fontType5.id, fontType5);
            }
        });
    }
}

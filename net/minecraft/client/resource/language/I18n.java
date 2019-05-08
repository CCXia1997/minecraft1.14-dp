package net.minecraft.client.resource.language;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class I18n
{
    private static TranslationStorage storage;
    
    static void setLanguage(final TranslationStorage storage) {
        I18n.storage = storage;
    }
    
    public static String translate(final String key, final Object... args) {
        return I18n.storage.translate(key, args);
    }
    
    public static boolean hasTranslation(final String key) {
        return I18n.storage.containsKey(key);
    }
}

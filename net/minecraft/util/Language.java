package net.minecraft.util;

import org.apache.logging.log4j.LogManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.util.Iterator;
import com.google.gson.JsonObject;
import java.io.InputStream;
import com.google.gson.JsonParseException;
import java.io.Reader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.logging.log4j.Logger;

public class Language
{
    private static final Logger LOGGER;
    private static final Pattern b;
    private static final Language INSTANCE;
    private final Map<String, String> translations;
    private long timeLoaded;
    
    public Language() {
        this.translations = Maps.newHashMap();
        try {
            final InputStream inputStream1 = Language.class.getResourceAsStream("/assets/minecraft/lang/en_us.json");
            final JsonElement jsonElement2 = new Gson().<JsonElement>fromJson(new InputStreamReader(inputStream1, StandardCharsets.UTF_8), JsonElement.class);
            final JsonObject jsonObject3 = JsonHelper.asObject(jsonElement2, "strings");
            for (final Map.Entry<String, JsonElement> entry5 : jsonObject3.entrySet()) {
                final String string6 = Language.b.matcher(JsonHelper.asString(entry5.getValue(), entry5.getKey())).replaceAll("%$1s");
                this.translations.put(entry5.getKey(), string6);
            }
            this.timeLoaded = SystemUtil.getMeasuringTimeMs();
        }
        catch (JsonParseException jsonParseException1) {
            Language.LOGGER.error("Couldn't read strings from /assets/minecraft/lang/en_us.json", (Throwable)jsonParseException1);
        }
    }
    
    public static Language getInstance() {
        return Language.INSTANCE;
    }
    
    @Environment(EnvType.CLIENT)
    public static synchronized void load(final Map<String, String> map) {
        Language.INSTANCE.translations.clear();
        Language.INSTANCE.translations.putAll(map);
        Language.INSTANCE.timeLoaded = SystemUtil.getMeasuringTimeMs();
    }
    
    public synchronized String translate(final String string) {
        return this.getTranslation(string);
    }
    
    private String getTranslation(final String string) {
        final String string2 = this.translations.get(string);
        return (string2 == null) ? string : string2;
    }
    
    public synchronized boolean hasTranslation(final String string) {
        return this.translations.containsKey(string);
    }
    
    public long getTimeLoaded() {
        return this.timeLoaded;
    }
    
    static {
        LOGGER = LogManager.getLogger();
        b = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");
        INSTANCE = new Language();
    }
}

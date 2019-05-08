package net.minecraft.client.resource.language;

import org.apache.logging.log4j.LogManager;
import java.util.IllegalFormatException;
import com.google.gson.JsonObject;
import net.minecraft.util.JsonHelper;
import java.io.Reader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import com.google.gson.JsonElement;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import net.minecraft.resource.Resource;
import java.util.Iterator;
import java.io.FileNotFoundException;
import net.minecraft.util.Identifier;
import java.util.List;
import net.minecraft.resource.ResourceManager;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.logging.log4j.Logger;
import com.google.gson.Gson;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class TranslationStorage
{
    private static final Gson GSON;
    private static final Logger LOGGER;
    private static final Pattern PARAM_PATTERN;
    protected final Map<String, String> translations;
    
    public TranslationStorage() {
        this.translations = Maps.newHashMap();
    }
    
    public synchronized void load(final ResourceManager container, final List<String> list) {
        this.translations.clear();
        for (final String string4 : list) {
            final String string5 = String.format("lang/%s.json", string4);
            for (final String string6 : container.getAllNamespaces()) {
                try {
                    final Identifier identifier8 = new Identifier(string6, string5);
                    this.load(container.getAllResources(identifier8));
                }
                catch (FileNotFoundException ex) {}
                catch (Exception exception8) {
                    TranslationStorage.LOGGER.warn("Skipped language file: {}:{} ({})", string6, string5, exception8.toString());
                }
            }
        }
    }
    
    private void load(final List<Resource> list) {
        for (final Resource resource3 : list) {
            final InputStream inputStream4 = resource3.getInputStream();
            try {
                this.load(inputStream4);
            }
            finally {
                IOUtils.closeQuietly(inputStream4);
            }
        }
    }
    
    private void load(final InputStream inputStream) {
        final JsonElement jsonElement2 = TranslationStorage.GSON.<JsonElement>fromJson(new InputStreamReader(inputStream, StandardCharsets.UTF_8), JsonElement.class);
        final JsonObject jsonObject3 = JsonHelper.asObject(jsonElement2, "strings");
        for (final Map.Entry<String, JsonElement> entry5 : jsonObject3.entrySet()) {
            final String string6 = TranslationStorage.PARAM_PATTERN.matcher(JsonHelper.asString(entry5.getValue(), entry5.getKey())).replaceAll("%$1s");
            this.translations.put(entry5.getKey(), string6);
        }
    }
    
    private String get(final String string) {
        final String string2 = this.translations.get(string);
        return (string2 == null) ? string : string2;
    }
    
    public String translate(final String key, final Object[] arr) {
        final String string3 = this.get(key);
        try {
            return String.format(string3, arr);
        }
        catch (IllegalFormatException illegalFormatException4) {
            return "Format error: " + string3;
        }
    }
    
    public boolean containsKey(final String string) {
        return this.translations.containsKey(string);
    }
    
    static {
        GSON = new Gson();
        LOGGER = LogManager.getLogger();
        PARAM_PATTERN = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");
    }
}

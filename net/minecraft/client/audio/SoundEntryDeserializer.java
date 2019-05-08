package net.minecraft.client.audio;

import org.apache.commons.lang3.Validate;
import com.google.gson.JsonArray;
import com.google.common.collect.Lists;
import com.google.gson.JsonParseException;
import java.util.List;
import com.google.gson.JsonObject;
import net.minecraft.util.JsonHelper;
import com.google.gson.JsonDeserializationContext;
import java.lang.reflect.Type;
import com.google.gson.JsonElement;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import com.google.gson.JsonDeserializer;

@Environment(EnvType.CLIENT)
public class SoundEntryDeserializer implements JsonDeserializer<SoundEntry>
{
    public SoundEntry a(final JsonElement functionJson, final Type unused, final JsonDeserializationContext context) throws JsonParseException {
        final JsonObject jsonObject4 = JsonHelper.asObject(functionJson, "entry");
        final boolean boolean5 = JsonHelper.getBoolean(jsonObject4, "replace", false);
        final String string6 = JsonHelper.getString(jsonObject4, "subtitle", null);
        final List<Sound> list7 = this.deserializeSounds(jsonObject4);
        return new SoundEntry(list7, boolean5, string6);
    }
    
    private List<Sound> deserializeSounds(final JsonObject json) {
        final List<Sound> list2 = Lists.newArrayList();
        if (json.has("sounds")) {
            final JsonArray jsonArray3 = JsonHelper.getArray(json, "sounds");
            for (int integer4 = 0; integer4 < jsonArray3.size(); ++integer4) {
                final JsonElement jsonElement5 = jsonArray3.get(integer4);
                if (JsonHelper.isString(jsonElement5)) {
                    final String string6 = JsonHelper.asString(jsonElement5, "sound");
                    list2.add(new Sound(string6, 1.0f, 1.0f, 1, Sound.RegistrationType.FILE, false, false, 16));
                }
                else {
                    list2.add(this.deserializeSound(JsonHelper.asObject(jsonElement5, "sound")));
                }
            }
        }
        return list2;
    }
    
    private Sound deserializeSound(final JsonObject json) {
        final String string2 = JsonHelper.getString(json, "name");
        final Sound.RegistrationType registrationType3 = this.deserializeType(json, Sound.RegistrationType.FILE);
        final float float4 = JsonHelper.getFloat(json, "volume", 1.0f);
        Validate.isTrue(float4 > 0.0f, "Invalid volume", new Object[0]);
        final float float5 = JsonHelper.getFloat(json, "pitch", 1.0f);
        Validate.isTrue(float5 > 0.0f, "Invalid pitch", new Object[0]);
        final int integer6 = JsonHelper.getInt(json, "weight", 1);
        Validate.isTrue(integer6 > 0, "Invalid weight", new Object[0]);
        final boolean boolean7 = JsonHelper.getBoolean(json, "preload", false);
        final boolean boolean8 = JsonHelper.getBoolean(json, "stream", false);
        final int integer7 = JsonHelper.getInt(json, "attenuation_distance", 16);
        return new Sound(string2, float4, float5, integer6, registrationType3, boolean8, boolean7, integer7);
    }
    
    private Sound.RegistrationType deserializeType(final JsonObject json, final Sound.RegistrationType fallback) {
        Sound.RegistrationType registrationType3 = fallback;
        if (json.has("type")) {
            registrationType3 = Sound.RegistrationType.getByName(JsonHelper.getString(json, "type"));
            Validate.notNull(registrationType3, "Invalid type", new Object[0]);
        }
        return registrationType3;
    }
}

package net.minecraft.util;

import java.util.Locale;
import javax.annotation.Nullable;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonReader;
import java.io.IOException;
import com.google.gson.stream.JsonWriter;
import java.util.Map;
import com.google.common.collect.Maps;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.TypeAdapterFactory;

public class LowercaseEnumTypeAdapterFactory implements TypeAdapterFactory
{
    @Nullable
    @Override
    public <T> TypeAdapter<T> create(final Gson gson, final TypeToken<T> typeToken) {
        final Class<T> class3 = (Class<T>)typeToken.getRawType();
        if (!class3.isEnum()) {
            return null;
        }
        final Map<String, T> map4 = Maps.newHashMap();
        for (final T object8 : class3.getEnumConstants()) {
            map4.put(this.toString(object8), object8);
        }
        return new TypeAdapter<T>() {
            @Override
            public void write(final JsonWriter jsonWriter, final T object) throws IOException {
                if (object == null) {
                    jsonWriter.nullValue();
                }
                else {
                    jsonWriter.value(LowercaseEnumTypeAdapterFactory.this.toString(object));
                }
            }
            
            @Nullable
            @Override
            public T read(final JsonReader jsonReader) throws IOException {
                if (jsonReader.peek() == JsonToken.NULL) {
                    jsonReader.nextNull();
                    return null;
                }
                return map4.get(jsonReader.nextString());
            }
        };
    }
    
    private String toString(final Object o) {
        if (o instanceof Enum) {
            return ((Enum)o).name().toLowerCase(Locale.ROOT);
        }
        return o.toString().toLowerCase(Locale.ROOT);
    }
}

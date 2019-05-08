package net.minecraft.server;

import com.google.gson.JsonParseException;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonDeserializer;
import org.apache.logging.log4j.LogManager;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.lang.reflect.Type;
import java.io.Reader;
import net.minecraft.util.JsonHelper;
import java.io.BufferedWriter;
import java.io.Writer;
import org.apache.commons.io.IOUtils;
import com.google.common.io.Files;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import com.google.gson.JsonObject;
import java.util.Iterator;
import java.util.List;
import com.google.common.collect.Lists;
import javax.annotation.Nullable;
import java.io.IOException;
import com.google.gson.GsonBuilder;
import com.google.common.collect.Maps;
import java.lang.reflect.ParameterizedType;
import java.util.Map;
import java.io.File;
import com.google.gson.Gson;
import org.apache.logging.log4j.Logger;

public class ServerConfigList<K, V extends ServerConfigEntry<K>>
{
    protected static final Logger LOGGER;
    protected final Gson GSON;
    private final File file;
    private final Map<String, V> map;
    private boolean enabled;
    private static final ParameterizedType f;
    
    public ServerConfigList(final File file) {
        this.map = Maps.newHashMap();
        this.enabled = true;
        this.file = file;
        final GsonBuilder gsonBuilder2 = new GsonBuilder().setPrettyPrinting();
        gsonBuilder2.registerTypeHierarchyAdapter(ServerConfigEntry.class, new DeSerializer());
        this.GSON = gsonBuilder2.create();
    }
    
    public boolean isEnabled() {
        return this.enabled;
    }
    
    public void setEnabled(final boolean boolean1) {
        this.enabled = boolean1;
    }
    
    public File getFile() {
        return this.file;
    }
    
    public void add(final V serverConfigEntry) {
        this.map.put(this.toString(serverConfigEntry.getKey()), serverConfigEntry);
        try {
            this.save();
        }
        catch (IOException iOException2) {
            ServerConfigList.LOGGER.warn("Could not save the list after adding a user.", (Throwable)iOException2);
        }
    }
    
    @Nullable
    public V get(final K object) {
        this.removeInvalidEntries();
        return this.map.get(this.toString(object));
    }
    
    public void remove(final K object) {
        this.map.remove(this.toString(object));
        try {
            this.save();
        }
        catch (IOException iOException2) {
            ServerConfigList.LOGGER.warn("Could not save the list after removing a user.", (Throwable)iOException2);
        }
    }
    
    public void removeEntry(final ServerConfigEntry<K> serverConfigEntry) {
        this.remove(serverConfigEntry.getKey());
    }
    
    public String[] getNames() {
        return this.map.keySet().<String>toArray(new String[this.map.size()]);
    }
    
    public boolean isEmpty() {
        return this.map.size() < 1;
    }
    
    protected String toString(final K object) {
        return object.toString();
    }
    
    protected boolean contains(final K object) {
        return this.map.containsKey(this.toString(object));
    }
    
    private void removeInvalidEntries() {
        final List<K> list1 = Lists.newArrayList();
        for (final V serverConfigEntry3 : this.map.values()) {
            if (serverConfigEntry3.isInvalid()) {
                list1.add(serverConfigEntry3.getKey());
            }
        }
        for (final K object3 : list1) {
            this.map.remove(this.toString(object3));
        }
    }
    
    protected ServerConfigEntry<K> fromJson(final JsonObject jsonObject) {
        return new ServerConfigEntry<K>(null, jsonObject);
    }
    
    public Collection<V> values() {
        return this.map.values();
    }
    
    public void save() throws IOException {
        final Collection<V> collection1 = this.map.values();
        final String string2 = this.GSON.toJson(collection1);
        BufferedWriter bufferedWriter3 = null;
        try {
            bufferedWriter3 = Files.newWriter(this.file, StandardCharsets.UTF_8);
            bufferedWriter3.write(string2);
        }
        finally {
            IOUtils.closeQuietly((Writer)bufferedWriter3);
        }
    }
    
    public void load() throws FileNotFoundException {
        if (!this.file.exists()) {
            return;
        }
        BufferedReader bufferedReader1 = null;
        try {
            bufferedReader1 = Files.newReader(this.file, StandardCharsets.UTF_8);
            final Collection<ServerConfigEntry<K>> collection2 = JsonHelper.<Collection<ServerConfigEntry<K>>>deserialize(this.GSON, bufferedReader1, ServerConfigList.f);
            if (collection2 != null) {
                this.map.clear();
                for (final ServerConfigEntry<K> serverConfigEntry4 : collection2) {
                    if (serverConfigEntry4.getKey() != null) {
                        this.map.put(this.toString(serverConfigEntry4.getKey()), (V)serverConfigEntry4);
                    }
                }
            }
        }
        finally {
            IOUtils.closeQuietly((Reader)bufferedReader1);
        }
    }
    
    static {
        LOGGER = LogManager.getLogger();
        f = new ParameterizedType() {
            @Override
            public Type[] getActualTypeArguments() {
                return new Type[] { ServerConfigEntry.class };
            }
            
            @Override
            public Type getRawType() {
                return List.class;
            }
            
            @Override
            public Type getOwnerType() {
                return null;
            }
        };
    }
    
    class DeSerializer implements JsonDeserializer<ServerConfigEntry<K>>, JsonSerializer<ServerConfigEntry<K>>
    {
        private DeSerializer() {
        }
        
        public JsonElement a(final ServerConfigEntry<K> entry, final Type unused, final JsonSerializationContext context) {
            final JsonObject jsonObject4 = new JsonObject();
            entry.serialize(jsonObject4);
            return jsonObject4;
        }
        
        public ServerConfigEntry<K> a(final JsonElement functionJson, final Type unused, final JsonDeserializationContext context) throws JsonParseException {
            if (functionJson.isJsonObject()) {
                final JsonObject jsonObject4 = functionJson.getAsJsonObject();
                return ServerConfigList.this.fromJson(jsonObject4);
            }
            return null;
        }
    }
}

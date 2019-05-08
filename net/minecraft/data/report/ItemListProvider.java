package net.minecraft.data.report;

import com.google.gson.GsonBuilder;
import java.util.Iterator;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.DefaultedRegistry;
import java.io.IOException;
import java.nio.file.Path;
import com.google.gson.JsonElement;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import com.google.gson.JsonObject;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataGenerator;
import com.google.gson.Gson;
import net.minecraft.data.DataProvider;

public class ItemListProvider implements DataProvider
{
    private static final Gson b;
    private final DataGenerator root;
    
    public ItemListProvider(final DataGenerator dataGenerator) {
        this.root = dataGenerator;
    }
    
    @Override
    public void run(final DataCache dataCache) throws IOException {
        final JsonObject jsonObject2 = new JsonObject();
        Registry.REGISTRIES.getIds().forEach(identifier -> jsonObject2.add(identifier.toString(), ItemListProvider.a(Registry.REGISTRIES.get(identifier))));
        final Path path3 = this.root.getOutput().resolve("reports/registries.json");
        DataProvider.writeToPath(ItemListProvider.b, dataCache, jsonObject2, path3);
    }
    
    private static <T> JsonElement a(final MutableRegistry<T> mutableRegistry) {
        final JsonObject jsonObject2 = new JsonObject();
        if (mutableRegistry instanceof DefaultedRegistry) {
            final Identifier identifier3 = ((DefaultedRegistry)mutableRegistry).getDefaultId();
            jsonObject2.addProperty("default", identifier3.toString());
        }
        final int integer3 = Registry.REGISTRIES.getRawId(mutableRegistry);
        jsonObject2.addProperty("protocol_id", integer3);
        final JsonObject jsonObject3 = new JsonObject();
        for (final Identifier identifier4 : mutableRegistry.getIds()) {
            final T object7 = mutableRegistry.get(identifier4);
            final int integer4 = mutableRegistry.getRawId(object7);
            final JsonObject jsonObject4 = new JsonObject();
            jsonObject4.addProperty("protocol_id", integer4);
            jsonObject3.add(identifier4.toString(), jsonObject4);
        }
        jsonObject2.add("entries", jsonObject3);
        return jsonObject2;
    }
    
    @Override
    public String getName() {
        return "Registry Dump";
    }
    
    static {
        b = new GsonBuilder().setPrettyPrinting().create();
    }
}

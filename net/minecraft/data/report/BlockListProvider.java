package net.minecraft.data.report;

import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.nio.file.Path;
import com.google.common.collect.UnmodifiableIterator;
import net.minecraft.state.StateFactory;
import net.minecraft.util.Identifier;
import java.util.Iterator;
import net.minecraft.block.BlockState;
import com.google.gson.JsonElement;
import net.minecraft.util.SystemUtil;
import com.google.gson.JsonArray;
import net.minecraft.state.property.Property;
import net.minecraft.block.Block;
import net.minecraft.util.registry.Registry;
import com.google.gson.JsonObject;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataGenerator;
import com.google.gson.Gson;
import net.minecraft.data.DataProvider;

public class BlockListProvider implements DataProvider
{
    private static final Gson b;
    private final DataGenerator root;
    
    public BlockListProvider(final DataGenerator dataGenerator) {
        this.root = dataGenerator;
    }
    
    @Override
    public void run(final DataCache dataCache) throws IOException {
        final JsonObject jsonObject2 = new JsonObject();
        for (final Block block4 : Registry.BLOCK) {
            final Identifier identifier5 = Registry.BLOCK.getId(block4);
            final JsonObject jsonObject3 = new JsonObject();
            final StateFactory<Block, BlockState> stateFactory7 = block4.getStateFactory();
            if (!stateFactory7.getProperties().isEmpty()) {
                final JsonObject jsonObject4 = new JsonObject();
                for (final Property<?> property10 : stateFactory7.getProperties()) {
                    final JsonArray jsonArray11 = new JsonArray();
                    for (final Comparable<?> comparable13 : property10.getValues()) {
                        jsonArray11.add(SystemUtil.getValueAsString(property10, comparable13));
                    }
                    jsonObject4.add(property10.getName(), jsonArray11);
                }
                jsonObject3.add("properties", jsonObject4);
            }
            final JsonArray jsonArray12 = new JsonArray();
            for (final BlockState blockState10 : stateFactory7.getStates()) {
                final JsonObject jsonObject5 = new JsonObject();
                final JsonObject jsonObject6 = new JsonObject();
                for (final Property<?> property11 : stateFactory7.getProperties()) {
                    jsonObject6.addProperty(property11.getName(), SystemUtil.getValueAsString(property11, blockState10.get(property11)));
                }
                if (jsonObject6.size() > 0) {
                    jsonObject5.add("properties", jsonObject6);
                }
                jsonObject5.addProperty("id", Block.getRawIdFromState(blockState10));
                if (blockState10 == block4.getDefaultState()) {
                    jsonObject5.addProperty("default", true);
                }
                jsonArray12.add(jsonObject5);
            }
            jsonObject3.add("states", jsonArray12);
            jsonObject2.add(identifier5.toString(), jsonObject3);
        }
        final Path path3 = this.root.getOutput().resolve("reports/blocks.json");
        DataProvider.writeToPath(BlockListProvider.b, dataCache, jsonObject2, path3);
    }
    
    @Override
    public String getName() {
        return "Block List";
    }
    
    static {
        b = new GsonBuilder().setPrettyPrinting().create();
    }
}

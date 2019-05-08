package net.minecraft.world.loot;

import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.condition.LootConditions;
import net.minecraft.world.loot.condition.LootCondition;
import net.minecraft.world.loot.function.LootFunctions;
import net.minecraft.world.loot.function.LootFunction;
import net.minecraft.world.loot.entry.LootEntries;
import net.minecraft.world.loot.entry.LootEntry;
import net.minecraft.util.BoundedIntUnaryOperator;
import java.lang.reflect.Type;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import com.google.gson.JsonElement;
import com.google.common.collect.ImmutableSet;
import java.util.function.Function;
import net.minecraft.resource.Resource;
import java.util.Iterator;
import net.minecraft.util.JsonHelper;
import org.apache.commons.io.IOUtils;
import java.nio.charset.StandardCharsets;
import net.minecraft.resource.ResourceManager;
import java.util.Collections;
import com.google.common.collect.Maps;
import java.util.Set;
import net.minecraft.util.Identifier;
import java.util.Map;
import com.google.gson.Gson;
import org.apache.logging.log4j.Logger;
import net.minecraft.resource.SynchronousResourceReloadListener;

public class LootManager implements SynchronousResourceReloadListener
{
    private static final Logger LOGGER;
    private static final Gson gson;
    private final Map<Identifier, LootSupplier> suppliers;
    private final Set<Identifier> supplierNames;
    public static final int lootTablesLength;
    public static final int jsonLength;
    
    public LootManager() {
        this.suppliers = Maps.newHashMap();
        this.supplierNames = Collections.<Identifier>unmodifiableSet(this.suppliers.keySet());
    }
    
    public LootSupplier getSupplier(final Identifier id) {
        return this.suppliers.getOrDefault(id, LootSupplier.EMPTY);
    }
    
    @Override
    public void apply(final ResourceManager manager) {
        this.suppliers.clear();
        for (final Identifier identifier3 : manager.findResources("loot_tables", ressourcePath -> ressourcePath.endsWith(".json"))) {
            final String string4 = identifier3.getPath();
            final Identifier identifier4 = new Identifier(identifier3.getNamespace(), string4.substring(LootManager.lootTablesLength, string4.length() - LootManager.jsonLength));
            try (final Resource resource6 = manager.getResource(identifier3)) {
                final LootSupplier lootSupplier8 = JsonHelper.<LootSupplier>deserialize(LootManager.gson, IOUtils.toString(resource6.getInputStream(), StandardCharsets.UTF_8), LootSupplier.class);
                if (lootSupplier8 != null) {
                    this.suppliers.put(identifier4, lootSupplier8);
                }
            }
            catch (Throwable throwable6) {
                LootManager.LOGGER.error("Couldn't read loot table {} from {}", identifier4, identifier3, throwable6);
            }
        }
        this.suppliers.put(LootTables.EMPTY, LootSupplier.EMPTY);
        final LootTableReporter lootTableReporter2 = new LootTableReporter();
        this.suppliers.forEach((id, supplier) -> check(lootTableReporter2, id, supplier, this.suppliers::get));
        lootTableReporter2.getMessages().forEach((key, value) -> LootManager.LOGGER.warn("Found validation problem in " + key + ": " + value));
    }
    
    public static void check(final LootTableReporter reporter, final Identifier id, final LootSupplier supplier, final Function<Identifier, LootSupplier> supplierGetter) {
        final Set<Identifier> set5 = ImmutableSet.<Identifier>of(id);
        supplier.check(reporter.makeChild("{" + id.toString() + "}"), supplierGetter, set5, supplier.getType());
    }
    
    public static JsonElement toJson(final LootSupplier supplier) {
        return LootManager.gson.toJsonTree(supplier);
    }
    
    public Set<Identifier> getSupplierNames() {
        return this.supplierNames;
    }
    
    static {
        LOGGER = LogManager.getLogger();
        gson = new GsonBuilder().registerTypeAdapter(UniformLootTableRange.class, new UniformLootTableRange.Serializer()).registerTypeAdapter(BinomialLootTableRange.class, new BinomialLootTableRange.Serializer()).registerTypeAdapter(ConstantLootTableRange.class, new ConstantLootTableRange.Serializer()).registerTypeAdapter(BoundedIntUnaryOperator.class, new BoundedIntUnaryOperator.Serializer()).registerTypeAdapter(LootPool.class, new LootPool.Serializer()).registerTypeAdapter(LootSupplier.class, new LootSupplier.Serializer()).registerTypeHierarchyAdapter(LootEntry.class, new LootEntries.Serializer()).registerTypeHierarchyAdapter(LootFunction.class, new LootFunctions.Factory()).registerTypeHierarchyAdapter(LootCondition.class, new LootConditions.Factory()).registerTypeHierarchyAdapter(LootContext.EntityTarget.class, new LootContext.EntityTarget.Serializer()).create();
        lootTablesLength = "loot_tables/".length();
        jsonLength = ".json".length();
    }
}

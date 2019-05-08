package net.minecraft.recipe;

import org.apache.logging.log4j.LogManager;
import com.google.gson.JsonSyntaxException;
import net.minecraft.util.registry.Registry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.util.stream.Stream;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.Objects;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import net.minecraft.inventory.Inventory;
import net.minecraft.world.World;
import net.minecraft.resource.Resource;
import java.util.Iterator;
import com.google.gson.Gson;
import java.io.IOException;
import com.google.gson.JsonParseException;
import net.minecraft.util.JsonHelper;
import org.apache.commons.io.IOUtils;
import java.nio.charset.StandardCharsets;
import com.google.gson.JsonObject;
import com.google.gson.GsonBuilder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.SystemUtil;
import com.google.common.collect.Maps;
import net.minecraft.util.Identifier;
import java.util.Map;
import org.apache.logging.log4j.Logger;
import net.minecraft.resource.SynchronousResourceReloadListener;

public class RecipeManager implements SynchronousResourceReloadListener
{
    private static final Logger LOGGER;
    public static final int PREFIX_LENGTH;
    public static final int SUFFIX_LENGTH;
    private final Map<RecipeType<?>, Map<Identifier, Recipe<?>>> recipeMap;
    private boolean hadErrors;
    
    public RecipeManager() {
        this.recipeMap = SystemUtil.<Map<RecipeType<?>, Map<Identifier, Recipe<?>>>>consume(Maps.newHashMap(), RecipeManager::clear);
    }
    
    @Override
    public void apply(final ResourceManager manager) {
        final Gson gson2 = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        this.hadErrors = false;
        clear(this.recipeMap);
        for (final Identifier identifier4 : manager.findResources("recipes", string -> string.endsWith(".json"))) {
            final String string2 = identifier4.getPath();
            final Identifier identifier5 = new Identifier(identifier4.getNamespace(), string2.substring(RecipeManager.PREFIX_LENGTH, string2.length() - RecipeManager.SUFFIX_LENGTH));
            try (final Resource resource7 = manager.getResource(identifier4)) {
                final JsonObject jsonObject9 = JsonHelper.<JsonObject>deserialize(gson2, IOUtils.toString(resource7.getInputStream(), StandardCharsets.UTF_8), JsonObject.class);
                if (jsonObject9 == null) {
                    RecipeManager.LOGGER.error("Couldn't load recipe {} as it's null or empty", identifier5);
                }
                else {
                    this.add(deserialize(identifier5, jsonObject9));
                }
            }
            catch (JsonParseException | IllegalArgumentException ex2) {
                final RuntimeException ex;
                final RuntimeException runtimeException7 = ex;
                RecipeManager.LOGGER.error("Parsing error loading recipe {}", identifier5, runtimeException7);
                this.hadErrors = true;
            }
            catch (IOException iOException7) {
                RecipeManager.LOGGER.error("Couldn't read custom advancement {} from {}", identifier5, identifier4, iOException7);
                this.hadErrors = true;
            }
        }
        RecipeManager.LOGGER.info("Loaded {} recipes", this.recipeMap.size());
    }
    
    public void add(final Recipe<?> recipe) {
        final Map<Identifier, Recipe<?>> map2 = this.recipeMap.get(recipe.getType());
        if (map2.containsKey(recipe.getId())) {
            throw new IllegalStateException("Duplicate recipe ignored with ID " + recipe.getId());
        }
        map2.put(recipe.getId(), recipe);
    }
    
    public <C extends Inventory, T extends Recipe<C>> Optional<T> getFirstMatch(final RecipeType<T> recipeType, final C inventory, final World world) {
        return this.<Inventory, T>getAllForType(recipeType).values().stream().<T>flatMap(recipe -> SystemUtil.stream(recipeType.<C>get(recipe, world, inventory))).findFirst();
    }
    
    public <C extends Inventory, T extends Recipe<C>> List<T> getAllMatches(final RecipeType<T> recipeType, final C inventory, final World world) {
        return this.<Inventory, T>getAllForType(recipeType).values().stream().flatMap(recipe -> SystemUtil.stream(recipeType.<C>get(recipe, world, inventory))).sorted(Comparator.comparing(recipe -> recipe.getOutput().getTranslationKey())).collect(Collectors.toList());
    }
    
    private <C extends Inventory, T extends Recipe<C>> Map<Identifier, Recipe<C>> getAllForType(final RecipeType<T> recipeType) {
        return (Map<Identifier, Recipe<C>>)this.recipeMap.getOrDefault(recipeType, Maps.newHashMap());
    }
    
    public <C extends Inventory, T extends Recipe<C>> DefaultedList<ItemStack> getRemainingStacks(final RecipeType<T> recipeType, final C inventory, final World world) {
        final Optional<T> optional4 = this.<C, T>getFirstMatch(recipeType, inventory, world);
        if (optional4.isPresent()) {
            return optional4.get().getRemainingStacks(inventory);
        }
        final DefaultedList<ItemStack> defaultedList5 = DefaultedList.<ItemStack>create(inventory.getInvSize(), ItemStack.EMPTY);
        for (int integer6 = 0; integer6 < defaultedList5.size(); ++integer6) {
            defaultedList5.set(integer6, inventory.getInvStack(integer6));
        }
        return defaultedList5;
    }
    
    public Optional<? extends Recipe<?>> get(final Identifier identifier) {
        return this.recipeMap.values().stream().map(map -> map.get(identifier)).filter(Objects::nonNull).findFirst();
    }
    
    public Collection<Recipe<?>> values() {
        return this.recipeMap.values().stream().flatMap(map -> map.values().stream()).collect(Collectors.toSet());
    }
    
    public Stream<Identifier> keys() {
        return this.recipeMap.values().stream().<Identifier>flatMap(map -> map.keySet().stream());
    }
    
    @Environment(EnvType.CLIENT)
    public void clear() {
        clear(this.recipeMap);
    }
    
    public static Recipe<?> deserialize(final Identifier id, final JsonObject json) {
        final String string3 = JsonHelper.getString(json, "type");
        final Object o;
        final String s;
        return Registry.RECIPE_SERIALIZER.getOrEmpty(new Identifier(string3)).<Throwable>orElseThrow(() -> {
            new JsonSyntaxException("Invalid or unsupported recipe type '" + s + "'");
            return o;
        }).read(id, json);
    }
    
    private static void clear(final Map<RecipeType<?>, Map<Identifier, Recipe<?>>> map) {
        map.clear();
        for (final RecipeType<?> recipeType3 : Registry.RECIPE_TYPE) {
            map.put(recipeType3, Maps.newHashMap());
        }
    }
    
    static {
        LOGGER = LogManager.getLogger();
        PREFIX_LENGTH = "recipes/".length();
        SUFFIX_LENGTH = ".json".length();
    }
}

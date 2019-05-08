package net.minecraft.server;

import com.google.gson.TypeAdapterFactory;
import net.minecraft.util.LowercaseEnumTypeAdapterFactory;
import net.minecraft.text.Style;
import net.minecraft.text.TextComponent;
import net.minecraft.advancement.AdvancementRewards;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import com.google.gson.JsonObject;
import com.google.gson.JsonDeserializationContext;
import java.lang.reflect.Type;
import com.google.gson.JsonElement;
import net.minecraft.advancement.AdvancementPositioner;
import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.resource.Resource;
import java.util.Iterator;
import java.io.IOException;
import net.minecraft.util.InvalidIdentifierException;
import com.google.gson.JsonParseException;
import net.minecraft.util.JsonHelper;
import org.apache.commons.io.IOUtils;
import java.nio.charset.StandardCharsets;
import com.google.common.collect.Maps;
import net.minecraft.advancement.Advancement;
import net.minecraft.util.Identifier;
import java.util.Map;
import net.minecraft.resource.ResourceManager;
import net.minecraft.advancement.AdvancementManager;
import com.google.gson.Gson;
import org.apache.logging.log4j.Logger;
import net.minecraft.resource.SynchronousResourceReloadListener;

public class ServerAdvancementLoader implements SynchronousResourceReloadListener
{
    private static final Logger LOGGER;
    private static final Gson GSON;
    private static final AdvancementManager MANAGER;
    public static final int PATH_PREFIX_LENGTH;
    public static final int FILE_EXTENSION_LENGTH;
    private boolean errored;
    
    private Map<Identifier, Advancement.Task> scanAdvancements(final ResourceManager resourceManager) {
        final Map<Identifier, Advancement.Task> map2 = Maps.newHashMap();
        for (final Identifier identifier4 : resourceManager.findResources("advancements", string -> string.endsWith(".json"))) {
            final String string2 = identifier4.getPath();
            final Identifier identifier5 = new Identifier(identifier4.getNamespace(), string2.substring(ServerAdvancementLoader.PATH_PREFIX_LENGTH, string2.length() - ServerAdvancementLoader.FILE_EXTENSION_LENGTH));
            try (final Resource resource7 = resourceManager.getResource(identifier4)) {
                final Advancement.Task task9 = JsonHelper.<Advancement.Task>deserialize(ServerAdvancementLoader.GSON, IOUtils.toString(resource7.getInputStream(), StandardCharsets.UTF_8), Advancement.Task.class);
                if (task9 == null) {
                    ServerAdvancementLoader.LOGGER.error("Couldn't load custom advancement {} from {} as it's empty or null", identifier5, identifier4);
                }
                else {
                    map2.put(identifier5, task9);
                }
            }
            catch (JsonParseException | IllegalArgumentException | InvalidIdentifierException ex2) {
                final RuntimeException ex;
                final RuntimeException runtimeException7 = ex;
                ServerAdvancementLoader.LOGGER.error("Parsing error loading custom advancement {}: {}", identifier5, runtimeException7.getMessage());
                this.errored = true;
            }
            catch (IOException iOException7) {
                ServerAdvancementLoader.LOGGER.error("Couldn't read custom advancement {} from {}", identifier5, identifier4, iOException7);
                this.errored = true;
            }
        }
        return map2;
    }
    
    @Nullable
    public Advancement get(final Identifier identifier) {
        return ServerAdvancementLoader.MANAGER.get(identifier);
    }
    
    public Collection<Advancement> getAdvancements() {
        return ServerAdvancementLoader.MANAGER.getAdvancements();
    }
    
    @Override
    public void apply(final ResourceManager manager) {
        this.errored = false;
        ServerAdvancementLoader.MANAGER.clear();
        final Map<Identifier, Advancement.Task> map2 = this.scanAdvancements(manager);
        ServerAdvancementLoader.MANAGER.load(map2);
        for (final Advancement advancement4 : ServerAdvancementLoader.MANAGER.getRoots()) {
            if (advancement4.getDisplay() != null) {
                AdvancementPositioner.arrangeForTree(advancement4);
            }
        }
    }
    
    static {
        LOGGER = LogManager.getLogger();
        final JsonObject jsonObject4;
        GSON = new GsonBuilder().registerTypeHierarchyAdapter(Advancement.Task.class, (jsonElement, type, jsonDeserializationContext) -> {
            jsonObject4 = JsonHelper.asObject(jsonElement, "advancement");
            return Advancement.Task.fromJson(jsonObject4, jsonDeserializationContext);
        }).registerTypeAdapter(AdvancementRewards.class, new AdvancementRewards.Deserializer()).registerTypeHierarchyAdapter(TextComponent.class, new TextComponent.Serializer()).registerTypeHierarchyAdapter(Style.class, new Style.Serializer()).registerTypeAdapterFactory(new LowercaseEnumTypeAdapterFactory()).create();
        MANAGER = new AdvancementManager();
        PATH_PREFIX_LENGTH = "advancements/".length();
        FILE_EXTENSION_LENGTH = ".json".length();
    }
}

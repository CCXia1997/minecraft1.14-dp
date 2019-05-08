package net.minecraft.client.resource;

import org.apache.logging.log4j.LogManager;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Function;
import java.util.Collection;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.util.Identifier;
import java.util.Iterator;
import java.io.BufferedReader;
import org.apache.commons.io.IOUtils;
import java.io.FileNotFoundException;
import com.google.gson.JsonParseException;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.Reader;
import net.minecraft.util.JsonHelper;
import com.google.common.io.Files;
import java.nio.charset.StandardCharsets;
import com.google.common.collect.Maps;
import java.io.File;
import java.util.Map;
import org.apache.logging.log4j.Logger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ResourceIndex
{
    protected static final Logger LOGGER;
    private final Map<String, File> index;
    
    protected ResourceIndex() {
        this.index = Maps.newHashMap();
    }
    
    public ResourceIndex(final File directory, final String indexName) {
        this.index = Maps.newHashMap();
        final File file3 = new File(directory, "objects");
        final File file4 = new File(directory, "indexes/" + indexName + ".json");
        BufferedReader bufferedReader5 = null;
        try {
            bufferedReader5 = Files.newReader(file4, StandardCharsets.UTF_8);
            final JsonObject jsonObject6 = JsonHelper.deserialize(bufferedReader5);
            final JsonObject jsonObject7 = JsonHelper.getObject(jsonObject6, "objects", null);
            if (jsonObject7 != null) {
                for (final Map.Entry<String, JsonElement> entry9 : jsonObject7.entrySet()) {
                    final JsonObject jsonObject8 = entry9.getValue();
                    final String string11 = entry9.getKey();
                    final String[] arr12 = string11.split("/", 2);
                    final String string12 = (arr12.length == 1) ? arr12[0] : (arr12[0] + ":" + arr12[1]);
                    final String string13 = JsonHelper.getString(jsonObject8, "hash");
                    final File file5 = new File(file3, string13.substring(0, 2) + "/" + string13);
                    this.index.put(string12, file5);
                }
            }
        }
        catch (JsonParseException jsonParseException6) {
            ResourceIndex.LOGGER.error("Unable to parse resource index file: {}", file4);
        }
        catch (FileNotFoundException fileNotFoundException6) {
            ResourceIndex.LOGGER.error("Can't find the resource index file: {}", file4);
        }
        finally {
            IOUtils.closeQuietly((Reader)bufferedReader5);
        }
    }
    
    @Nullable
    public File getResource(final Identifier identifier) {
        return this.findFile(identifier.toString());
    }
    
    @Nullable
    public File findFile(final String path) {
        return this.index.get(path);
    }
    
    public Collection<String> getFilesRecursively(final String dir, final int unused, final Predicate<String> filter) {
        return this.index.keySet().stream().filter(string -> !string.endsWith(".mcmeta")).map(Identifier::new).map(Identifier::getPath).filter(string2 -> string2.startsWith(dir + "/")).filter(filter).collect(Collectors.toList());
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}

package net.minecraft.data.server;

import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import java.io.BufferedWriter;
import java.nio.file.Path;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;
import java.util.Iterator;
import java.io.IOException;
import java.nio.file.OpenOption;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.util.Objects;
import com.google.gson.JsonElement;
import net.minecraft.tag.TagContainer;
import java.util.Optional;
import net.minecraft.data.DataCache;
import com.google.common.collect.Maps;
import net.minecraft.tag.Tag;
import java.util.Map;
import net.minecraft.util.registry.Registry;
import net.minecraft.data.DataGenerator;
import com.google.gson.Gson;
import org.apache.logging.log4j.Logger;
import net.minecraft.data.DataProvider;

public abstract class AbstractTagProvider<T> implements DataProvider
{
    private static final Logger LOGGER;
    private static final Gson GSON;
    protected final DataGenerator root;
    protected final Registry<T> registry;
    protected final Map<Tag<T>, Tag.Builder<T>> d;
    
    protected AbstractTagProvider(final DataGenerator root, final Registry<T> registry) {
        this.d = Maps.newLinkedHashMap();
        this.root = root;
        this.registry = registry;
    }
    
    protected abstract void configure();
    
    @Override
    public void run(final DataCache dataCache) throws IOException {
        this.d.clear();
        this.configure();
        final TagContainer<T> tagContainer2 = new TagContainer<T>(identifier -> Optional.empty(), "", false, "generated");
        for (final Map.Entry<Tag<T>, Tag.Builder<T>> entry4 : this.d.entrySet()) {
            final Identifier identifier5 = entry4.getKey().getId();
            if (!entry4.getValue().applyTagGetter(tagContainer2::get)) {
                throw new UnsupportedOperationException("Unsupported referencing of tags!");
            }
            final Tag<T> tag6 = entry4.getValue().build(identifier5);
            final JsonObject jsonObject7 = tag6.toJson(this.registry::getId);
            final Path path8 = this.getOutput(identifier5);
            tagContainer2.add(tag6);
            this.a(tagContainer2);
            try {
                final String string9 = AbstractTagProvider.GSON.toJson(jsonObject7);
                final String string10 = AbstractTagProvider.SHA1.hashUnencodedChars(string9).toString();
                if (!Objects.equals(dataCache.getOldSha1(path8), string10) || !Files.exists(path8)) {
                    Files.createDirectories(path8.getParent(), new FileAttribute[0]);
                    try (final BufferedWriter bufferedWriter11 = Files.newBufferedWriter(path8)) {
                        bufferedWriter11.write(string9);
                    }
                }
                dataCache.updateSha1(path8, string10);
            }
            catch (IOException iOException9) {
                AbstractTagProvider.LOGGER.error("Couldn't save tags to {}", path8, iOException9);
            }
        }
    }
    
    protected abstract void a(final TagContainer<T> arg1);
    
    protected abstract Path getOutput(final Identifier arg1);
    
    protected Tag.Builder<T> a(final Tag<T> tag) {
        return this.d.computeIfAbsent(tag, tag -> Tag.Builder.create());
    }
    
    static {
        LOGGER = LogManager.getLogger();
        GSON = new GsonBuilder().setPrettyPrinting().create();
    }
}

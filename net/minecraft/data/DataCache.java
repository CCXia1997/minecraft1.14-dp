package net.minecraft.data;

import org.apache.logging.log4j.LogManager;
import java.nio.file.LinkOption;
import java.nio.file.FileVisitOption;
import java.util.stream.Stream;
import java.util.Objects;
import javax.annotation.Nullable;
import java.io.Writer;
import java.util.Collection;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.List;
import java.io.IOException;
import org.apache.commons.io.IOUtils;
import com.google.common.base.Charsets;
import java.nio.file.OpenOption;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import com.google.common.collect.Sets;
import com.google.common.collect.Maps;
import java.util.Set;
import java.util.Map;
import java.nio.file.Path;
import org.apache.logging.log4j.Logger;

public class DataCache
{
    private static final Logger LOGGER;
    private final Path root;
    private final Path recordFile;
    private int unchanged;
    private final Map<Path, String> oldSha1;
    private final Map<Path, String> newSha1;
    private final Set<Path> ignores;
    
    public DataCache(final Path path, final String string) throws IOException {
        this.oldSha1 = Maps.newHashMap();
        this.newSha1 = Maps.newHashMap();
        this.ignores = Sets.newHashSet();
        this.root = path;
        final Path path2 = path.resolve(".cache");
        Files.createDirectories(path2, new FileAttribute[0]);
        this.recordFile = path2.resolve(string);
        final String s;
        this.files().forEach(path -> s = this.oldSha1.put(path, ""));
        if (Files.isReadable(this.recordFile)) {
            final int integer3;
            IOUtils.readLines(Files.newInputStream(this.recordFile), Charsets.UTF_8).forEach(string -> {
                integer3 = string.indexOf(32);
                this.oldSha1.put(path.resolve(string.substring(integer3 + 1)), string.substring(0, integer3));
            });
        }
    }
    
    public void write() throws IOException {
        this.deleteAll();
        Writer writer1;
        try {
            writer1 = Files.newBufferedWriter(this.recordFile);
        }
        catch (IOException iOException2) {
            DataCache.LOGGER.warn("Unable write cachefile {}: {}", this.recordFile, iOException2.toString());
            return;
        }
        IOUtils.writeLines((Collection)this.newSha1.entrySet().stream().map(entry -> entry.getValue() + ' ' + this.root.relativize((Path)entry.getKey())).collect(Collectors.toList()), System.lineSeparator(), writer1);
        writer1.close();
        DataCache.LOGGER.debug("Caching: cache hits: {}, created: {} removed: {}", this.unchanged, (this.newSha1.size() - this.unchanged), this.oldSha1.size());
    }
    
    @Nullable
    public String getOldSha1(final Path path) {
        return this.oldSha1.get(path);
    }
    
    public void updateSha1(final Path path, final String string) {
        this.newSha1.put(path, string);
        if (Objects.equals(this.oldSha1.remove(path), string)) {
            ++this.unchanged;
        }
    }
    
    public boolean contains(final Path path) {
        return this.oldSha1.containsKey(path);
    }
    
    public void ignore(final Path path) {
        this.ignores.add(path);
    }
    
    private void deleteAll() throws IOException {
        this.files().forEach(path -> {
            if (this.contains(path) && !this.ignores.contains(path)) {
                try {
                    Files.delete(path);
                }
                catch (IOException iOException2) {
                    DataCache.LOGGER.debug("Unable to delete: {} ({})", path, iOException2.toString());
                }
            }
        });
    }
    
    private Stream<Path> files() throws IOException {
        return Files.walk(this.root).filter(path -> !Objects.equals(this.recordFile, path) && !Files.isDirectory(path));
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}

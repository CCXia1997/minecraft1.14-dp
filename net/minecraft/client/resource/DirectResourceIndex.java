package net.minecraft.client.resource;

import java.util.stream.Stream;
import java.util.Collections;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.nio.file.Path;
import java.util.function.Function;
import java.nio.file.LinkOption;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.FileVisitOption;
import java.util.Collection;
import java.util.function.Predicate;
import net.minecraft.util.Identifier;
import java.io.File;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class DirectResourceIndex extends ResourceIndex
{
    private final File assetDir;
    
    public DirectResourceIndex(final File assetDir) {
        this.assetDir = assetDir;
    }
    
    @Override
    public File getResource(final Identifier identifier) {
        return new File(this.assetDir, identifier.toString().replace(':', '/'));
    }
    
    @Override
    public File findFile(final String path) {
        return new File(this.assetDir, path);
    }
    
    @Override
    public Collection<String> getFilesRecursively(final String dir, final int unused, final Predicate<String> filter) {
        final Path path2 = this.assetDir.toPath().resolve("minecraft/");
        try (final Stream<Path> stream5 = Files.walk(path2.resolve(dir), unused)) {
            return stream5.filter(path -> Files.isRegularFile(path)).filter(path -> !path.endsWith(".mcmeta")).map(path2::relativize).map(Object::toString).map(string -> string.replaceAll("\\\\", "/")).filter(filter).collect(Collectors.toList());
        }
        catch (NoSuchFileException ex) {}
        catch (IOException iOException5) {
            DirectResourceIndex.LOGGER.warn("Unable to getFiles on {}", dir, iOException5);
        }
        return Collections.emptyList();
    }
}

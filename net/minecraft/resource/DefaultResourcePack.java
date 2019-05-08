package net.minecraft.resource;

import net.minecraft.util.SystemUtil;
import com.google.common.collect.Maps;
import org.apache.logging.log4j.LogManager;
import java.nio.file.FileSystemNotFoundException;
import java.util.Collections;
import java.nio.file.FileSystems;
import java.util.HashMap;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import org.apache.commons.io.IOUtils;
import javax.annotation.Nullable;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.nio.file.FileVisitOption;
import com.google.common.collect.Lists;
import java.net.URI;
import java.util.Enumeration;
import java.nio.file.NoSuchFileException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.net.URL;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.function.Predicate;
import java.io.FileNotFoundException;
import net.minecraft.util.Identifier;
import java.io.IOException;
import java.nio.file.OpenOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.io.InputStream;
import com.google.common.collect.ImmutableSet;
import java.util.Set;
import java.nio.file.FileSystem;
import java.util.Map;
import org.apache.logging.log4j.Logger;
import java.nio.file.Path;

public class DefaultResourcePack implements ResourcePack
{
    public static Path RESOURCE_PATH;
    private static final Logger LOGGER;
    public static Class<?> RESOURCE_CLASS;
    private static final Map<ResourceType, FileSystem> e;
    public final Set<String> namespaces;
    
    public DefaultResourcePack(final String... arr) {
        this.namespaces = ImmutableSet.<String>copyOf(arr);
    }
    
    @Override
    public InputStream openRoot(final String string) throws IOException {
        if (string.contains("/") || string.contains("\\")) {
            throw new IllegalArgumentException("Root resources can only be filenames, not paths (no / allowed!)");
        }
        if (DefaultResourcePack.RESOURCE_PATH != null) {
            final Path path2 = DefaultResourcePack.RESOURCE_PATH.resolve(string);
            if (Files.exists(path2)) {
                return Files.newInputStream(path2);
            }
        }
        return this.getInputStream(string);
    }
    
    @Override
    public InputStream open(final ResourceType type, final Identifier identifier) throws IOException {
        final InputStream inputStream3 = this.findInputStream(type, identifier);
        if (inputStream3 != null) {
            return inputStream3;
        }
        throw new FileNotFoundException(identifier.getPath());
    }
    
    @Override
    public Collection<Identifier> findResources(final ResourceType type, final String namespace, final int integer, final Predicate<String> predicate) {
        final Set<Identifier> set5 = Sets.newHashSet();
        if (DefaultResourcePack.RESOURCE_PATH != null) {
            try {
                set5.addAll(this.getIdentifiers(integer, "minecraft", DefaultResourcePack.RESOURCE_PATH.resolve(type.getName()).resolve("minecraft"), namespace, predicate));
            }
            catch (IOException ex2) {}
            if (type == ResourceType.ASSETS) {
                Enumeration<URL> enumeration6 = null;
                try {
                    enumeration6 = DefaultResourcePack.RESOURCE_CLASS.getClassLoader().getResources(type.getName() + "/minecraft");
                }
                catch (IOException ex3) {}
                while (enumeration6 != null && enumeration6.hasMoreElements()) {
                    try {
                        final URI uRI7 = enumeration6.nextElement().toURI();
                        if (!"file".equals(uRI7.getScheme())) {
                            continue;
                        }
                        set5.addAll(this.getIdentifiers(integer, "minecraft", Paths.get(uRI7), namespace, predicate));
                    }
                    catch (URISyntaxException | IOException ex4) {}
                }
            }
        }
        try {
            final URL uRL6 = DefaultResourcePack.class.getResource("/" + type.getName() + "/.mcassetsroot");
            if (uRL6 == null) {
                DefaultResourcePack.LOGGER.error("Couldn't find .mcassetsroot, cannot load vanilla resources");
                return set5;
            }
            final URI uRI7 = uRL6.toURI();
            if ("file".equals(uRI7.getScheme())) {
                final URL uRL7 = new URL(uRL6.toString().substring(0, uRL6.toString().length() - ".mcassetsroot".length()) + "minecraft");
                if (uRL7 == null) {
                    return set5;
                }
                final Path path9 = Paths.get(uRL7.toURI());
                set5.addAll(this.getIdentifiers(integer, "minecraft", path9, namespace, predicate));
            }
            else if ("jar".equals(uRI7.getScheme())) {
                final Path path10 = DefaultResourcePack.e.get(type).getPath("/" + type.getName() + "/minecraft");
                set5.addAll(this.getIdentifiers(integer, "minecraft", path10, namespace, predicate));
            }
            else {
                DefaultResourcePack.LOGGER.error("Unsupported scheme {} trying to list vanilla resources (NYI?)", uRI7);
            }
        }
        catch (FileNotFoundException | NoSuchFileException ex5) {}
        catch (URISyntaxException | IOException ex6) {
            final Exception ex;
            final Exception exception6 = ex;
            DefaultResourcePack.LOGGER.error("Couldn't get a list of all vanilla resources", (Throwable)exception6);
        }
        return set5;
    }
    
    private Collection<Identifier> getIdentifiers(final int maxDepth, final String namespace, final Path path, final String searchLocation, final Predicate<String> filter) throws IOException {
        final List<Identifier> list6 = Lists.newArrayList();
        for (final Path path2 : Files.walk(path.resolve(searchLocation), maxDepth)) {
            if (!path2.endsWith(".mcmeta") && Files.isRegularFile(path2) && filter.test(path2.getFileName().toString())) {
                list6.add(new Identifier(namespace, path.relativize(path2).toString().replaceAll("\\\\", "/")));
            }
        }
        return list6;
    }
    
    @Nullable
    protected InputStream findInputStream(final ResourceType type, final Identifier id) {
        final String string3 = "/" + type.getName() + "/" + id.getNamespace() + "/" + id.getPath();
        if (DefaultResourcePack.RESOURCE_PATH != null) {
            final Path path4 = DefaultResourcePack.RESOURCE_PATH.resolve(type.getName() + "/" + id.getNamespace() + "/" + id.getPath());
            if (Files.exists(path4)) {
                try {
                    return Files.newInputStream(path4);
                }
                catch (IOException ex) {}
            }
        }
        try {
            final URL uRL4 = DefaultResourcePack.class.getResource(string3);
            if (uRL4 != null && DirectoryResourcePack.isValidPath(new File(uRL4.getFile()), string3)) {
                return DefaultResourcePack.class.getResourceAsStream(string3);
            }
        }
        catch (IOException iOException4) {
            return DefaultResourcePack.class.getResourceAsStream(string3);
        }
        return null;
    }
    
    @Nullable
    protected InputStream getInputStream(final String path) {
        return DefaultResourcePack.class.getResourceAsStream("/" + path);
    }
    
    @Override
    public boolean contains(final ResourceType type, final Identifier identifier) {
        final InputStream inputStream3 = this.findInputStream(type, identifier);
        final boolean boolean4 = inputStream3 != null;
        IOUtils.closeQuietly(inputStream3);
        return boolean4;
    }
    
    @Override
    public Set<String> getNamespaces(final ResourceType resourceType) {
        return this.namespaces;
    }
    
    @Nullable
    @Override
    public <T> T parseMetadata(final ResourceMetadataReader<T> resourceMetadataReader) throws IOException {
        try (final InputStream inputStream2 = this.openRoot("pack.mcmeta")) {
            return AbstractFilenameResourcePack.<T>parseMetadata(resourceMetadataReader, inputStream2);
        }
        catch (RuntimeException | FileNotFoundException ex2) {
            final Exception ex;
            final Exception exception2 = ex;
            return null;
        }
    }
    
    @Override
    public String getName() {
        return "Default";
    }
    
    @Override
    public void close() {
    }
    
    static {
        LOGGER = LogManager.getLogger();
        final ResourceType[] array;
        int length;
        int i = 0;
        ResourceType resourceType6;
        URL uRL7;
        URI uRI8;
        FileSystem fileSystem9;
        final Exception ex;
        Exception exception8;
        e = SystemUtil.<Map<ResourceType, FileSystem>>consume(Maps.newHashMap(), hashMap -> {
            synchronized (DefaultResourcePack.class) {
                ResourceType.values();
                for (length = array.length; i < length; ++i) {
                    resourceType6 = array[i];
                    uRL7 = DefaultResourcePack.class.getResource("/" + resourceType6.getName() + "/.mcassetsroot");
                    try {
                        uRI8 = uRL7.toURI();
                        if ("jar".equals(uRI8.getScheme())) {
                            try {
                                fileSystem9 = FileSystems.getFileSystem(uRI8);
                            }
                            catch (FileSystemNotFoundException fileSystemNotFoundException10) {
                                fileSystem9 = FileSystems.newFileSystem(uRI8, Collections.emptyMap());
                            }
                            hashMap.put(resourceType6, fileSystem9);
                        }
                    }
                    catch (URISyntaxException | IOException ex2) {
                        exception8 = ex;
                        DefaultResourcePack.LOGGER.error("Couldn't get a list of all vanilla resources", (Throwable)exception8);
                    }
                }
            }
        });
    }
}

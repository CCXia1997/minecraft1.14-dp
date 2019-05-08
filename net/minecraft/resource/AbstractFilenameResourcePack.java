package net.minecraft.resource;

import org.apache.logging.log4j.LogManager;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.util.JsonHelper;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import javax.annotation.Nullable;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.IOException;
import java.io.InputStream;
import net.minecraft.util.Identifier;
import java.io.File;
import org.apache.logging.log4j.Logger;

public abstract class AbstractFilenameResourcePack implements ResourcePack
{
    private static final Logger LOGGER;
    protected final File base;
    
    public AbstractFilenameResourcePack(final File file) {
        this.base = file;
    }
    
    private static String getFilename(final ResourceType type, final Identifier id) {
        return String.format("%s/%s/%s", type.getName(), id.getNamespace(), id.getPath());
    }
    
    protected static String relativize(final File file1, final File file2) {
        return file1.toURI().relativize(file2.toURI()).getPath();
    }
    
    @Override
    public InputStream open(final ResourceType type, final Identifier identifier) throws IOException {
        return this.openFilename(getFilename(type, identifier));
    }
    
    @Override
    public boolean contains(final ResourceType type, final Identifier identifier) {
        return this.containsFilename(getFilename(type, identifier));
    }
    
    protected abstract InputStream openFilename(final String arg1) throws IOException;
    
    @Environment(EnvType.CLIENT)
    @Override
    public InputStream openRoot(final String string) throws IOException {
        if (string.contains("/") || string.contains("\\")) {
            throw new IllegalArgumentException("Root resources can only be filenames, not paths (no / allowed!)");
        }
        return this.openFilename(string);
    }
    
    protected abstract boolean containsFilename(final String arg1);
    
    protected void warnNonLowercaseNamespace(final String string) {
        AbstractFilenameResourcePack.LOGGER.warn("ResourcePack: ignored non-lowercase namespace: {} in {}", string, this.base);
    }
    
    @Nullable
    @Override
    public <T> T parseMetadata(final ResourceMetadataReader<T> resourceMetadataReader) throws IOException {
        try (final InputStream inputStream2 = this.openFilename("pack.mcmeta")) {
            return AbstractFilenameResourcePack.<T>parseMetadata(resourceMetadataReader, inputStream2);
        }
    }
    
    @Nullable
    public static <T> T parseMetadata(final ResourceMetadataReader<T> resourceMetadataReader, final InputStream inputStream) {
        JsonObject jsonObject3;
        try (final BufferedReader bufferedReader4 = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            jsonObject3 = JsonHelper.deserialize(bufferedReader4);
        }
        catch (IOException | JsonParseException ex2) {
            final Exception ex;
            final Exception exception4 = ex;
            AbstractFilenameResourcePack.LOGGER.error("Couldn't load {} metadata", resourceMetadataReader.getKey(), exception4);
            return null;
        }
        if (!jsonObject3.has(resourceMetadataReader.getKey())) {
            return null;
        }
        try {
            return resourceMetadataReader.fromJson(JsonHelper.getObject(jsonObject3, resourceMetadataReader.getKey()));
        }
        catch (JsonParseException jsonParseException4) {
            AbstractFilenameResourcePack.LOGGER.error("Couldn't load {} metadata", resourceMetadataReader.getKey(), jsonParseException4);
            return null;
        }
    }
    
    @Override
    public String getName() {
        return this.base.getName();
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}

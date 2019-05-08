package net.minecraft.resource;

import java.util.concurrent.Executors;
import net.minecraft.util.UncaughtExceptionLogger;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.logging.log4j.LogManager;
import java.io.IOException;
import org.apache.commons.io.IOUtils;
import net.minecraft.util.JsonHelper;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import javax.annotation.Nullable;
import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.InputStream;
import net.minecraft.util.Identifier;
import java.util.concurrent.Executor;
import org.apache.logging.log4j.Logger;

public class ResourceImpl implements Resource
{
    private static final Logger LOGGER;
    public static final Executor RESOURCE_IO_EXECUTOR;
    private final String packName;
    private final Identifier id;
    private final InputStream inputStream;
    private final InputStream metadataInputStream;
    @Environment(EnvType.CLIENT)
    private boolean readMetadata;
    @Environment(EnvType.CLIENT)
    private JsonObject metadata;
    
    public ResourceImpl(final String string, final Identifier identifier, final InputStream inputStream3, @Nullable final InputStream inputStream4) {
        this.packName = string;
        this.id = identifier;
        this.inputStream = inputStream3;
        this.metadataInputStream = inputStream4;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public Identifier getId() {
        return this.id;
    }
    
    @Override
    public InputStream getInputStream() {
        return this.inputStream;
    }
    
    @Environment(EnvType.CLIENT)
    public boolean hasMetadata() {
        return this.metadataInputStream != null;
    }
    
    @Nullable
    @Environment(EnvType.CLIENT)
    @Override
    public <T> T getMetadata(final ResourceMetadataReader<T> resourceMetadataReader) {
        if (!this.hasMetadata()) {
            return null;
        }
        if (this.metadata == null && !this.readMetadata) {
            this.readMetadata = true;
            BufferedReader bufferedReader2 = null;
            try {
                bufferedReader2 = new BufferedReader(new InputStreamReader(this.metadataInputStream, StandardCharsets.UTF_8));
                this.metadata = JsonHelper.deserialize(bufferedReader2);
            }
            finally {
                IOUtils.closeQuietly((Reader)bufferedReader2);
            }
        }
        if (this.metadata == null) {
            return null;
        }
        final String string2 = resourceMetadataReader.getKey();
        return this.metadata.has(string2) ? resourceMetadataReader.fromJson(JsonHelper.getObject(this.metadata, string2)) : null;
    }
    
    @Override
    public String getResourcePackName() {
        return this.packName;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ResourceImpl)) {
            return false;
        }
        final ResourceImpl resourceImpl2 = (ResourceImpl)o;
        Label_0054: {
            if (this.id != null) {
                if (this.id.equals(resourceImpl2.id)) {
                    break Label_0054;
                }
            }
            else if (resourceImpl2.id == null) {
                break Label_0054;
            }
            return false;
        }
        if (this.packName != null) {
            if (this.packName.equals(resourceImpl2.packName)) {
                return true;
            }
        }
        else if (resourceImpl2.packName == null) {
            return true;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int integer1 = (this.packName != null) ? this.packName.hashCode() : 0;
        integer1 = 31 * integer1 + ((this.id != null) ? this.id.hashCode() : 0);
        return integer1;
    }
    
    @Override
    public void close() throws IOException {
        this.inputStream.close();
        if (this.metadataInputStream != null) {
            this.metadataInputStream.close();
        }
    }
    
    static {
        LOGGER = LogManager.getLogger();
        RESOURCE_IO_EXECUTOR = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setDaemon(true).setNameFormat("Resource IO {0}").setUncaughtExceptionHandler(new UncaughtExceptionLogger(ResourceImpl.LOGGER)).build());
    }
}

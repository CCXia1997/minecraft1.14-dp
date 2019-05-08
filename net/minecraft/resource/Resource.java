package net.minecraft.resource;

import javax.annotation.Nullable;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import java.io.InputStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import java.io.Closeable;

public interface Resource extends Closeable
{
    @Environment(EnvType.CLIENT)
    Identifier getId();
    
    InputStream getInputStream();
    
    @Nullable
    @Environment(EnvType.CLIENT)
     <T> T getMetadata(final ResourceMetadataReader<T> arg1);
    
    String getResourcePackName();
}

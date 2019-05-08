package net.minecraft.resource;

import javax.annotation.Nullable;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import java.util.Set;
import java.util.Collection;
import java.util.function.Predicate;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.IOException;
import java.io.InputStream;
import java.io.Closeable;

public interface ResourcePack extends Closeable
{
    @Environment(EnvType.CLIENT)
    InputStream openRoot(final String arg1) throws IOException;
    
    InputStream open(final ResourceType arg1, final Identifier arg2) throws IOException;
    
    Collection<Identifier> findResources(final ResourceType arg1, final String arg2, final int arg3, final Predicate<String> arg4);
    
    boolean contains(final ResourceType arg1, final Identifier arg2);
    
    Set<String> getNamespaces(final ResourceType arg1);
    
    @Nullable
     <T> T parseMetadata(final ResourceMetadataReader<T> arg1) throws IOException;
    
    String getName();
}

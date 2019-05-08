package net.minecraft.resource;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.List;
import java.io.IOException;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.util.Set;

public interface ResourceManager
{
    @Environment(EnvType.CLIENT)
    Set<String> getAllNamespaces();
    
    Resource getResource(final Identifier arg1) throws IOException;
    
    @Environment(EnvType.CLIENT)
    boolean containsResource(final Identifier arg1);
    
    List<Resource> getAllResources(final Identifier arg1) throws IOException;
    
    Collection<Identifier> findResources(final String arg1, final Predicate<String> arg2);
    
    @Environment(EnvType.CLIENT)
    void addPack(final ResourcePack arg1);
}

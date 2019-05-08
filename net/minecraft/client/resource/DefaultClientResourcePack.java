package net.minecraft.client.resource;

import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Function;
import java.util.Collection;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.InputStream;
import net.minecraft.util.Identifier;
import net.minecraft.resource.ResourceType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.DefaultResourcePack;

@Environment(EnvType.CLIENT)
public class DefaultClientResourcePack extends DefaultResourcePack
{
    private final ResourceIndex index;
    
    public DefaultClientResourcePack(final ResourceIndex resourceIndex) {
        super("minecraft", "realms");
        this.index = resourceIndex;
    }
    
    @Nullable
    @Override
    protected InputStream findInputStream(final ResourceType type, final Identifier id) {
        if (type == ResourceType.ASSETS) {
            final File file3 = this.index.getResource(id);
            if (file3 != null && file3.exists()) {
                try {
                    return new FileInputStream(file3);
                }
                catch (FileNotFoundException ex) {}
            }
        }
        return super.findInputStream(type, id);
    }
    
    @Nullable
    @Override
    protected InputStream getInputStream(final String path) {
        final File file2 = this.index.findFile(path);
        if (file2 != null && file2.exists()) {
            try {
                return new FileInputStream(file2);
            }
            catch (FileNotFoundException ex) {}
        }
        return super.getInputStream(path);
    }
    
    @Override
    public Collection<Identifier> findResources(final ResourceType type, final String namespace, final int integer, final Predicate<String> predicate) {
        final Collection<Identifier> collection5 = super.findResources(type, namespace, integer, predicate);
        collection5.addAll(this.index.getFilesRecursively(namespace, integer, predicate).stream().map(Identifier::new).collect(Collectors.toList()));
        return collection5;
    }
}

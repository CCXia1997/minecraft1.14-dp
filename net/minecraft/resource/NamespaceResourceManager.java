package net.minecraft.resource;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.ByteArrayOutputStream;
import org.apache.logging.log4j.LogManager;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.Iterator;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileNotFoundException;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.util.Collections;
import java.util.Set;
import com.google.common.collect.Lists;
import java.util.List;
import org.apache.logging.log4j.Logger;

public class NamespaceResourceManager implements ResourceManager
{
    private static final Logger LOGGER;
    protected final List<ResourcePack> packList;
    private final ResourceType type;
    
    public NamespaceResourceManager(final ResourceType resourceType) {
        this.packList = Lists.newArrayList();
        this.type = resourceType;
    }
    
    @Override
    public void addPack(final ResourcePack resourcePack) {
        this.packList.add(resourcePack);
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public Set<String> getAllNamespaces() {
        return Collections.<String>emptySet();
    }
    
    @Override
    public Resource getResource(final Identifier identifier) throws IOException {
        this.validate(identifier);
        ResourcePack resourcePack2 = null;
        final Identifier identifier2 = getMetadataPath(identifier);
        for (int integer4 = this.packList.size() - 1; integer4 >= 0; --integer4) {
            final ResourcePack resourcePack3 = this.packList.get(integer4);
            if (resourcePack2 == null && resourcePack3.contains(this.type, identifier2)) {
                resourcePack2 = resourcePack3;
            }
            if (resourcePack3.contains(this.type, identifier)) {
                InputStream inputStream6 = null;
                if (resourcePack2 != null) {
                    inputStream6 = this.open(identifier2, resourcePack2);
                }
                return new ResourceImpl(resourcePack3.getName(), identifier, this.open(identifier, resourcePack3), inputStream6);
            }
        }
        throw new FileNotFoundException(identifier.toString());
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public boolean containsResource(final Identifier identifier) {
        if (!this.isPathAbsolute(identifier)) {
            return false;
        }
        for (int integer2 = this.packList.size() - 1; integer2 >= 0; --integer2) {
            final ResourcePack resourcePack3 = this.packList.get(integer2);
            if (resourcePack3.contains(this.type, identifier)) {
                return true;
            }
        }
        return false;
    }
    
    protected InputStream open(final Identifier identifier, final ResourcePack resourcePack) throws IOException {
        final InputStream inputStream3 = resourcePack.open(this.type, identifier);
        return NamespaceResourceManager.LOGGER.isDebugEnabled() ? new DebugInputStream(inputStream3, identifier, resourcePack.getName()) : inputStream3;
    }
    
    private void validate(final Identifier identifier) throws IOException {
        if (!this.isPathAbsolute(identifier)) {
            throw new IOException("Invalid relative path to resource: " + identifier);
        }
    }
    
    private boolean isPathAbsolute(final Identifier identifier) {
        return !identifier.getPath().contains("..");
    }
    
    @Override
    public List<Resource> getAllResources(final Identifier identifier) throws IOException {
        this.validate(identifier);
        final List<Resource> list2 = Lists.newArrayList();
        final Identifier identifier2 = getMetadataPath(identifier);
        for (final ResourcePack resourcePack5 : this.packList) {
            if (resourcePack5.contains(this.type, identifier)) {
                final InputStream inputStream6 = resourcePack5.contains(this.type, identifier2) ? this.open(identifier2, resourcePack5) : null;
                list2.add(new ResourceImpl(resourcePack5.getName(), identifier, this.open(identifier, resourcePack5), inputStream6));
            }
        }
        if (list2.isEmpty()) {
            throw new FileNotFoundException(identifier.toString());
        }
        return list2;
    }
    
    @Override
    public Collection<Identifier> findResources(final String namespace, final Predicate<String> predicate) {
        final List<Identifier> list3 = Lists.newArrayList();
        for (final ResourcePack resourcePack5 : this.packList) {
            list3.addAll(resourcePack5.findResources(this.type, namespace, Integer.MAX_VALUE, predicate));
        }
        Collections.<Identifier>sort(list3);
        return list3;
    }
    
    static Identifier getMetadataPath(final Identifier identifier) {
        return new Identifier(identifier.getNamespace(), identifier.getPath() + ".mcmeta");
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
    
    static class DebugInputStream extends InputStream
    {
        private final InputStream parent;
        private final String leakMessage;
        private boolean closed;
        
        public DebugInputStream(final InputStream inputStream, final Identifier identifier, final String string) {
            this.parent = inputStream;
            final ByteArrayOutputStream byteArrayOutputStream4 = new ByteArrayOutputStream();
            new Exception().printStackTrace(new PrintStream(byteArrayOutputStream4));
            this.leakMessage = "Leaked resource: '" + identifier + "' loaded from pack: '" + string + "'\n" + byteArrayOutputStream4;
        }
        
        @Override
        public void close() throws IOException {
            this.parent.close();
            this.closed = true;
        }
        
        @Override
        protected void finalize() throws Throwable {
            if (!this.closed) {
                NamespaceResourceManager.LOGGER.warn(this.leakMessage);
            }
            super.finalize();
        }
        
        @Override
        public int read() throws IOException {
            return this.parent.read();
        }
    }
}

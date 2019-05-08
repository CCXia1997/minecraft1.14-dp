package net.minecraft.resource;

import net.minecraft.util.Identifier;
import java.util.Collection;
import java.util.function.Predicate;
import java.io.Closeable;
import org.apache.commons.io.IOUtils;
import java.util.List;
import java.util.Enumeration;
import java.util.Locale;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.io.InputStream;
import java.io.IOException;
import java.io.File;
import java.util.zip.ZipFile;
import com.google.common.base.Splitter;

public class ZipResourcePack extends AbstractFilenameResourcePack
{
    public static final Splitter TYPE_NAMESPACE_SPLITTER;
    private ZipFile file;
    
    public ZipResourcePack(final File file) {
        super(file);
    }
    
    private ZipFile getZipFile() throws IOException {
        if (this.file == null) {
            this.file = new ZipFile(this.base);
        }
        return this.file;
    }
    
    @Override
    protected InputStream openFilename(final String string) throws IOException {
        final ZipFile zipFile2 = this.getZipFile();
        final ZipEntry zipEntry3 = zipFile2.getEntry(string);
        if (zipEntry3 == null) {
            throw new ResourceNotFoundException(this.base, string);
        }
        return zipFile2.getInputStream(zipEntry3);
    }
    
    public boolean containsFilename(final String string) {
        try {
            return this.getZipFile().getEntry(string) != null;
        }
        catch (IOException iOException2) {
            return false;
        }
    }
    
    @Override
    public Set<String> getNamespaces(final ResourceType resourceType) {
        ZipFile zipFile2;
        try {
            zipFile2 = this.getZipFile();
        }
        catch (IOException iOException3) {
            return Collections.<String>emptySet();
        }
        final Enumeration<? extends ZipEntry> enumeration3 = zipFile2.entries();
        final Set<String> set4 = Sets.newHashSet();
        while (enumeration3.hasMoreElements()) {
            final ZipEntry zipEntry5 = (ZipEntry)enumeration3.nextElement();
            final String string6 = zipEntry5.getName();
            if (string6.startsWith(resourceType.getName() + "/")) {
                final List<String> list7 = Lists.newArrayList(ZipResourcePack.TYPE_NAMESPACE_SPLITTER.split(string6));
                if (list7.size() <= 1) {
                    continue;
                }
                final String string7 = list7.get(1);
                if (string7.equals(string7.toLowerCase(Locale.ROOT))) {
                    set4.add(string7);
                }
                else {
                    this.warnNonLowercaseNamespace(string7);
                }
            }
        }
        return set4;
    }
    
    @Override
    protected void finalize() throws Throwable {
        this.close();
        super.finalize();
    }
    
    @Override
    public void close() {
        if (this.file != null) {
            IOUtils.closeQuietly((Closeable)this.file);
            this.file = null;
        }
    }
    
    @Override
    public Collection<Identifier> findResources(final ResourceType type, final String namespace, final int integer, final Predicate<String> predicate) {
        ZipFile zipFile5;
        try {
            zipFile5 = this.getZipFile();
        }
        catch (IOException iOException6) {
            return Collections.emptySet();
        }
        final Enumeration<? extends ZipEntry> enumeration6 = zipFile5.entries();
        final List<Identifier> list7 = Lists.newArrayList();
        final String string8 = type.getName() + "/";
        while (enumeration6.hasMoreElements()) {
            final ZipEntry zipEntry9 = (ZipEntry)enumeration6.nextElement();
            if (!zipEntry9.isDirectory()) {
                if (!zipEntry9.getName().startsWith(string8)) {
                    continue;
                }
                final String string9 = zipEntry9.getName().substring(string8.length());
                if (string9.endsWith(".mcmeta")) {
                    continue;
                }
                final int integer2 = string9.indexOf(47);
                if (integer2 < 0) {
                    continue;
                }
                final String string10 = string9.substring(integer2 + 1);
                if (!string10.startsWith(namespace + "/")) {
                    continue;
                }
                final String[] arr13 = string10.substring(namespace.length() + 2).split("/");
                if (arr13.length < integer + 1 || !predicate.test(string10)) {
                    continue;
                }
                final String string11 = string9.substring(0, integer2);
                list7.add(new Identifier(string11, string10));
            }
        }
        return list7;
    }
    
    static {
        TYPE_NAMESPACE_SPLITTER = Splitter.on('/').omitEmptyStrings().limit(3);
    }
}

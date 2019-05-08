package net.minecraft.resource;

import net.minecraft.util.SystemUtil;
import org.apache.logging.log4j.LogManager;
import net.minecraft.util.InvalidIdentifierException;
import java.util.Iterator;
import java.util.List;
import com.google.common.collect.Lists;
import net.minecraft.util.Identifier;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.Locale;
import java.io.FileFilter;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import com.google.common.collect.Sets;
import java.util.Set;
import javax.annotation.Nullable;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.File;
import com.google.common.base.CharMatcher;
import org.apache.logging.log4j.Logger;

public class DirectoryResourcePack extends AbstractFilenameResourcePack
{
    private static final Logger b;
    private static final boolean IS_WINDOWS;
    private static final CharMatcher BACKSLASH_MATCHER;
    
    public DirectoryResourcePack(final File file) {
        super(file);
    }
    
    public static boolean isValidPath(final File file, final String filename) throws IOException {
        String string3 = file.getCanonicalPath();
        if (DirectoryResourcePack.IS_WINDOWS) {
            string3 = DirectoryResourcePack.BACKSLASH_MATCHER.replaceFrom(string3, '/');
        }
        return string3.endsWith(filename);
    }
    
    @Override
    protected InputStream openFilename(final String string) throws IOException {
        final File file2 = this.getFile(string);
        if (file2 == null) {
            throw new ResourceNotFoundException(this.base, string);
        }
        return new FileInputStream(file2);
    }
    
    @Override
    protected boolean containsFilename(final String string) {
        return this.getFile(string) != null;
    }
    
    @Nullable
    private File getFile(final String string) {
        try {
            final File file2 = new File(this.base, string);
            if (file2.isFile() && isValidPath(file2, string)) {
                return file2;
            }
        }
        catch (IOException ex) {}
        return null;
    }
    
    @Override
    public Set<String> getNamespaces(final ResourceType resourceType) {
        final Set<String> set2 = Sets.newHashSet();
        final File file3 = new File(this.base, resourceType.getName());
        final File[] arr4 = file3.listFiles((FileFilter)DirectoryFileFilter.DIRECTORY);
        if (arr4 != null) {
            for (final File file4 : arr4) {
                final String string9 = AbstractFilenameResourcePack.relativize(file3, file4);
                if (string9.equals(string9.toLowerCase(Locale.ROOT))) {
                    set2.add(string9.substring(0, string9.length() - 1));
                }
                else {
                    this.warnNonLowercaseNamespace(string9);
                }
            }
        }
        return set2;
    }
    
    @Override
    public void close() throws IOException {
    }
    
    @Override
    public Collection<Identifier> findResources(final ResourceType type, final String namespace, final int integer, final Predicate<String> predicate) {
        final File file5 = new File(this.base, type.getName());
        final List<Identifier> list6 = Lists.newArrayList();
        for (final String string8 : this.getNamespaces(type)) {
            this.findFiles(new File(new File(file5, string8), namespace), integer, string8, list6, namespace + "/", predicate);
        }
        return list6;
    }
    
    private void findFiles(final File file, final int integer, final String string3, final List<Identifier> list, final String string5, final Predicate<String> predicate) {
        final File[] arr7 = file.listFiles();
        if (arr7 != null) {
            for (final File file2 : arr7) {
                if (file2.isDirectory()) {
                    if (integer > 0) {
                        this.findFiles(file2, integer - 1, string3, list, string5 + file2.getName() + "/", predicate);
                    }
                }
                else if (!file2.getName().endsWith(".mcmeta") && predicate.test(file2.getName())) {
                    try {
                        list.add(new Identifier(string3, string5 + file2.getName()));
                    }
                    catch (InvalidIdentifierException invalidIdentifierException12) {
                        DirectoryResourcePack.b.error(invalidIdentifierException12.getMessage());
                    }
                }
            }
        }
    }
    
    static {
        b = LogManager.getLogger();
        IS_WINDOWS = (SystemUtil.getOperatingSystem() == SystemUtil.OperatingSystem.WINDOWS);
        BACKSLASH_MATCHER = CharMatcher.is('\\');
    }
}

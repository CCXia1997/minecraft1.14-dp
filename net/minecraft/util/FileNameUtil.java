package net.minecraft.util;

import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.Iterator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.IOException;
import java.util.regex.Matcher;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import net.minecraft.SharedConstants;
import java.nio.file.Path;
import java.util.regex.Pattern;

public class FileNameUtil
{
    private static final Pattern a;
    private static final Pattern RESERVED_WINDOWS_NAMES;
    
    @Environment(EnvType.CLIENT)
    public static String getNextUniqueName(final Path path, String name, final String extension) throws IOException {
        for (final char character7 : SharedConstants.INVALID_CHARS_LEVEL_NAME) {
            name = name.replace(character7, '_');
        }
        name = name.replaceAll("[./\"]", "_");
        if (FileNameUtil.RESERVED_WINDOWS_NAMES.matcher(name).matches()) {
            name = "_" + name + "_";
        }
        final Matcher matcher4 = FileNameUtil.a.matcher(name);
        int integer5 = 0;
        if (matcher4.matches()) {
            name = matcher4.group("name");
            integer5 = Integer.parseInt(matcher4.group("count"));
        }
        if (name.length() > 255 - extension.length()) {
            name = name.substring(0, 255 - extension.length());
        }
        while (true) {
            String string6 = name;
            if (integer5 != 0) {
                final String string7 = " (" + integer5 + ")";
                final int integer6 = 255 - string7.length();
                if (string6.length() > integer6) {
                    string6 = string6.substring(0, integer6);
                }
                string6 += string7;
            }
            string6 += extension;
            final Path path2 = path.resolve(string6);
            try {
                final Path path3 = Files.createDirectory(path2, new FileAttribute[0]);
                Files.deleteIfExists(path3);
                return path.relativize(path3).toString();
            }
            catch (FileAlreadyExistsException fileAlreadyExistsException8) {
                ++integer5;
            }
        }
    }
    
    public static boolean isNormal(final Path path) {
        final Path path2 = path.normalize();
        return path2.equals(path);
    }
    
    public static boolean isAllowedName(final Path path) {
        for (final Path path2 : path) {
            if (FileNameUtil.RESERVED_WINDOWS_NAMES.matcher(path2.toString()).matches()) {
                return false;
            }
        }
        return true;
    }
    
    public static Path b(final Path path, final String string2, final String string3) {
        final String string4 = string2 + string3;
        final Path path2 = Paths.get(string4);
        if (path2.endsWith(string3)) {
            throw new InvalidPathException(string4, "empty resource name");
        }
        return path.resolve(path2);
    }
    
    static {
        a = Pattern.compile("(<name>.*) \\((<count>\\d*)\\)", 66);
        RESERVED_WINDOWS_NAMES = Pattern.compile(".*\\.|(?:COM|CLOCK\\$|CON|PRN|AUX|NUL|COM[1-9]|LPT[1-9])(?:\\..*)?", 2);
    }
}

package net.minecraft.data;

import org.apache.logging.log4j.LogManager;
import java.io.OutputStream;
import java.io.BufferedReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.StringNbtReader;
import java.nio.file.OpenOption;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.LinkOption;
import java.util.Objects;
import java.io.Reader;
import org.apache.commons.io.IOUtils;
import java.io.IOException;
import java.util.Iterator;
import java.nio.file.Files;
import java.nio.file.FileVisitOption;
import java.nio.file.Path;
import org.apache.logging.log4j.Logger;

public class SnbtProvider implements DataProvider
{
    private static final Logger LOGGER;
    private final DataGenerator root;
    
    public SnbtProvider(final DataGenerator dataGenerator) {
        this.root = dataGenerator;
    }
    
    @Override
    public void run(final DataCache dataCache) throws IOException {
        final Path path5 = this.root.getOutput();
        for (final Path path6 : this.root.getInputs()) {
            Files.walk(path6).filter(path -> path.toString().endsWith(".snbt")).forEach(path4 -> this.a(dataCache, path4, this.a(path6, path4), path5));
        }
    }
    
    @Override
    public String getName() {
        return "SNBT -> NBT";
    }
    
    private String a(final Path path1, final Path path2) {
        final String string3 = path1.relativize(path2).toString().replaceAll("\\\\", "/");
        return string3.substring(0, string3.length() - ".snbt".length());
    }
    
    private void a(final DataCache dataCache, final Path path2, final String string, final Path path4) {
        try {
            final Path path5 = path4.resolve(string + ".nbt");
            try (final BufferedReader bufferedReader6 = Files.newBufferedReader(path2)) {
                final String string2 = IOUtils.toString((Reader)bufferedReader6);
                final String string3 = SnbtProvider.SHA1.hashUnencodedChars(string2).toString();
                if (!Objects.equals(dataCache.getOldSha1(path5), string3) || !Files.exists(path5)) {
                    Files.createDirectories(path5.getParent(), new FileAttribute[0]);
                    try (final OutputStream outputStream10 = Files.newOutputStream(path5)) {
                        NbtIo.writeCompressed(StringNbtReader.parse(string2), outputStream10);
                    }
                }
                dataCache.updateSha1(path5, string3);
            }
        }
        catch (CommandSyntaxException commandSyntaxException5) {
            SnbtProvider.LOGGER.error("Couldn't convert {} from SNBT to NBT at {} as it's invalid SNBT", string, path2, commandSyntaxException5);
        }
        catch (IOException iOException5) {
            SnbtProvider.LOGGER.error("Couldn't convert {} from SNBT to NBT at {}", string, path2, iOException5);
        }
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}

package net.minecraft.data.dev;

import org.apache.logging.log4j.LogManager;
import java.io.BufferedWriter;
import net.minecraft.text.TextComponent;
import net.minecraft.nbt.CompoundTag;
import java.nio.file.attribute.FileAttribute;
import net.minecraft.nbt.NbtIo;
import java.nio.file.OpenOption;
import java.io.IOException;
import java.util.Iterator;
import java.nio.file.Files;
import java.nio.file.FileVisitOption;
import java.nio.file.Path;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataGenerator;
import org.apache.logging.log4j.Logger;
import net.minecraft.data.DataProvider;

public class NbtProvider implements DataProvider
{
    private static final Logger LOGGER;
    private final DataGenerator root;
    
    public NbtProvider(final DataGenerator dataGenerator) {
        this.root = dataGenerator;
    }
    
    @Override
    public void run(final DataCache dataCache) throws IOException {
        final Path path4 = this.root.getOutput();
        for (final Path path5 : this.root.getInputs()) {
            Files.walk(path5).filter(path -> path.toString().endsWith(".nbt")).forEach(path3 -> this.a(path3, this.a(path5, path3), path4));
        }
    }
    
    @Override
    public String getName() {
        return "NBT to SNBT";
    }
    
    private String a(final Path path1, final Path path2) {
        final String string3 = path1.relativize(path2).toString().replaceAll("\\\\", "/");
        return string3.substring(0, string3.length() - ".nbt".length());
    }
    
    private void a(final Path path1, final String string, final Path path3) {
        try {
            final CompoundTag compoundTag4 = NbtIo.readCompressed(Files.newInputStream(path1));
            final TextComponent textComponent5 = compoundTag4.toTextComponent("    ", 0);
            final String string2 = textComponent5.getString() + "\n";
            final Path path4 = path3.resolve(string + ".snbt");
            Files.createDirectories(path4.getParent(), new FileAttribute[0]);
            try (final BufferedWriter bufferedWriter8 = Files.newBufferedWriter(path4)) {
                bufferedWriter8.write(string2);
            }
            NbtProvider.LOGGER.info("Converted {} from NBT to SNBT", string);
        }
        catch (IOException iOException4) {
            NbtProvider.LOGGER.error("Couldn't convert {} from NBT to SNBT at {}", string, path1, iOException4);
        }
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}

package net.minecraft.data.validate;

import net.minecraft.util.TagHelper;
import net.minecraft.datafixers.DataFixTypes;
import net.minecraft.structure.Structure;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.CompoundTag;
import java.io.InputStream;
import net.minecraft.nbt.StringNbtReader;
import org.apache.commons.io.IOUtils;
import com.google.common.base.Charsets;
import java.nio.file.OpenOption;
import java.util.stream.Stream;
import java.nio.file.FileVisitOption;
import com.mojang.datafixers.DataFixer;
import java.io.IOException;
import java.util.Iterator;
import net.minecraft.datafixers.Schemas;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;

public class StructureValidatorProvider implements DataProvider
{
    private final DataGenerator generator;
    
    public StructureValidatorProvider(final DataGenerator dataGenerator) {
        this.generator = dataGenerator;
    }
    
    @Override
    public void run(final DataCache dataCache) throws IOException {
        for (final Path path3 : this.generator.getInputs()) {
            final Path path4 = path3.resolve("data/minecraft/structures/");
            if (Files.isDirectory(path4)) {
                a(Schemas.getFixer(), path4);
            }
        }
    }
    
    @Override
    public String getName() {
        return "Structure validator";
    }
    
    private static void a(final DataFixer dataFixer, final Path path) throws IOException {
        try (final Stream<Path> stream3 = Files.walk(path)) {
            stream3.forEach(path -> {
                if (Files.isRegularFile(path)) {
                    b(dataFixer, path);
                }
                return;
            });
        }
    }
    
    private static void b(final DataFixer dataFixer, final Path path) {
        try {
            final String string3 = path.getFileName().toString();
            if (string3.endsWith(".snbt")) {
                c(dataFixer, path);
            }
            else {
                if (!string3.endsWith(".nbt")) {
                    throw new IllegalArgumentException("Unrecognized format of file");
                }
                d(dataFixer, path);
            }
        }
        catch (Exception exception3) {
            throw new a(path, exception3);
        }
    }
    
    private static void c(final DataFixer dataFixer, final Path path) throws Exception {
        CompoundTag compoundTag3;
        try (final InputStream inputStream4 = Files.newInputStream(path)) {
            final String string6 = IOUtils.toString(inputStream4, Charsets.UTF_8);
            compoundTag3 = StringNbtReader.parse(string6);
        }
        a(dataFixer, a(compoundTag3));
    }
    
    private static void d(final DataFixer dataFixer, final Path path) throws Exception {
        CompoundTag compoundTag3;
        try (final InputStream inputStream4 = Files.newInputStream(path)) {
            compoundTag3 = NbtIo.readCompressed(inputStream4);
        }
        a(dataFixer, a(compoundTag3));
    }
    
    private static CompoundTag a(final CompoundTag compoundTag) {
        if (!compoundTag.containsKey("DataVersion", 99)) {
            compoundTag.putInt("DataVersion", 500);
        }
        return compoundTag;
    }
    
    private static CompoundTag a(final DataFixer dataFixer, final CompoundTag compoundTag) {
        final Structure structure3 = new Structure();
        structure3.fromTag(TagHelper.update(dataFixer, DataFixTypes.f, compoundTag, compoundTag.getInt("DataVersion")));
        return structure3.toTag(new CompoundTag());
    }
    
    static class a extends RuntimeException
    {
        public a(final Path path, final Throwable throwable) {
            super("Failed to process file: " + path.toAbsolutePath().toString(), throwable);
        }
    }
}

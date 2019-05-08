package net.minecraft.structure;

import org.apache.logging.log4j.LogManager;
import java.nio.file.InvalidPathException;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.FileNameUtil;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.TagHelper;
import net.minecraft.datafixers.DataFixTypes;
import net.minecraft.nbt.NbtIo;
import java.io.InputStream;
import java.io.IOException;
import java.io.FileInputStream;
import net.minecraft.resource.Resource;
import java.io.FileNotFoundException;
import net.minecraft.resource.ResourceManager;
import javax.annotation.Nullable;
import net.minecraft.resource.ResourceReloadListener;
import com.google.common.collect.Maps;
import java.io.File;
import java.nio.file.Path;
import net.minecraft.server.MinecraftServer;
import com.mojang.datafixers.DataFixer;
import net.minecraft.util.Identifier;
import java.util.Map;
import org.apache.logging.log4j.Logger;
import net.minecraft.resource.SynchronousResourceReloadListener;

public class StructureManager implements SynchronousResourceReloadListener
{
    private static final Logger LOGGER;
    private final Map<Identifier, Structure> structures;
    private final DataFixer dataFixer;
    private final MinecraftServer server;
    private final Path generatedPath;
    
    public StructureManager(final MinecraftServer server, final File worldDir, final DataFixer dataFixer) {
        this.structures = Maps.newHashMap();
        this.server = server;
        this.dataFixer = dataFixer;
        this.generatedPath = worldDir.toPath().resolve("generated").normalize();
        server.getDataManager().registerListener(this);
    }
    
    public Structure getStructureOrBlank(final Identifier id) {
        Structure structure2 = this.getStructure(id);
        if (structure2 == null) {
            structure2 = new Structure();
            this.structures.put(id, structure2);
        }
        return structure2;
    }
    
    @Nullable
    public Structure getStructure(final Identifier identifier) {
        final Structure structure2;
        return this.structures.computeIfAbsent(identifier, identifier -> {
            structure2 = this.loadStructureFromFile(identifier);
            return (structure2 != null) ? structure2 : this.loadStructureFromResource(identifier);
        });
    }
    
    @Override
    public void apply(final ResourceManager manager) {
        this.structures.clear();
    }
    
    @Nullable
    private Structure loadStructureFromResource(final Identifier id) {
        final Identifier identifier2 = new Identifier(id.getNamespace(), "structures/" + id.getPath() + ".nbt");
        try (final Resource resource3 = this.server.getDataManager().getResource(identifier2)) {
            return this.readStructure(resource3.getInputStream());
        }
        catch (FileNotFoundException fileNotFoundException3) {
            return null;
        }
        catch (Throwable throwable3) {
            StructureManager.LOGGER.error("Couldn't load structure {}: {}", id, throwable3.toString());
            return null;
        }
    }
    
    @Nullable
    private Structure loadStructureFromFile(final Identifier id) {
        if (!this.generatedPath.toFile().isDirectory()) {
            return null;
        }
        final Path path2 = this.getAndCheckStructurePath(id, ".nbt");
        try (final InputStream inputStream3 = new FileInputStream(path2.toFile())) {
            return this.readStructure(inputStream3);
        }
        catch (FileNotFoundException fileNotFoundException3) {
            return null;
        }
        catch (IOException iOException3) {
            StructureManager.LOGGER.error("Couldn't load structure from {}", path2, iOException3);
            return null;
        }
    }
    
    private Structure readStructure(final InputStream structureInputStream) throws IOException {
        final CompoundTag compoundTag2 = NbtIo.readCompressed(structureInputStream);
        if (!compoundTag2.containsKey("DataVersion", 99)) {
            compoundTag2.putInt("DataVersion", 500);
        }
        final Structure structure3 = new Structure();
        structure3.fromTag(TagHelper.update(this.dataFixer, DataFixTypes.f, compoundTag2, compoundTag2.getInt("DataVersion")));
        return structure3;
    }
    
    public boolean saveStructure(final Identifier id) {
        final Structure structure2 = this.structures.get(id);
        if (structure2 == null) {
            return false;
        }
        final Path path3 = this.getAndCheckStructurePath(id, ".nbt");
        final Path path4 = path3.getParent();
        if (path4 == null) {
            return false;
        }
        try {
            Files.createDirectories(Files.exists(path4) ? path4.toRealPath() : path4, new FileAttribute[0]);
        }
        catch (IOException iOException5) {
            StructureManager.LOGGER.error("Failed to create parent directory: {}", path4);
            return false;
        }
        final CompoundTag compoundTag5 = structure2.toTag(new CompoundTag());
        try (final OutputStream outputStream6 = new FileOutputStream(path3.toFile())) {
            NbtIo.writeCompressed(compoundTag5, outputStream6);
        }
        catch (Throwable throwable6) {
            return false;
        }
        return true;
    }
    
    private Path getStructurePath(final Identifier id, final String string) {
        try {
            final Path path3 = this.generatedPath.resolve(id.getNamespace());
            final Path path4 = path3.resolve("structures");
            return FileNameUtil.b(path4, id.getPath(), string);
        }
        catch (InvalidPathException invalidPathException3) {
            throw new InvalidIdentifierException("Invalid resource path: " + id, invalidPathException3);
        }
    }
    
    private Path getAndCheckStructurePath(final Identifier id, final String string) {
        if (id.getPath().contains("//")) {
            throw new InvalidIdentifierException("Invalid resource path: " + id);
        }
        final Path path3 = this.getStructurePath(id, string);
        if (!path3.startsWith(this.generatedPath) || !FileNameUtil.isNormal(path3) || !FileNameUtil.isAllowedName(path3)) {
            throw new InvalidIdentifierException("Invalid resource path: " + path3);
        }
        return path3;
    }
    
    public void unloadStructure(final Identifier id) {
        this.structures.remove(id);
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}

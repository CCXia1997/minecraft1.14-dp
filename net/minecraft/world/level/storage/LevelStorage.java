package net.minecraft.world.level.storage;

import java.time.temporal.TemporalField;
import java.time.format.SignStyle;
import java.time.temporal.ChronoField;
import java.time.format.DateTimeFormatterBuilder;
import org.apache.logging.log4j.LogManager;
import java.nio.file.FileVisitor;
import java.util.zip.ZipEntry;
import java.nio.file.FileVisitResult;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.Paths;
import java.util.zip.ZipOutputStream;
import java.io.BufferedOutputStream;
import java.nio.file.OpenOption;
import net.minecraft.util.FileNameUtil;
import java.time.LocalDateTime;
import java.io.OutputStream;
import java.io.FileOutputStream;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.TagHelper;
import net.minecraft.datafixers.DataFixTypes;
import java.io.InputStream;
import net.minecraft.nbt.NbtIo;
import java.io.FileInputStream;
import net.minecraft.util.ProgressListener;
import net.minecraft.world.WorldSaveHandler;
import javax.annotation.Nullable;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.LevelProperties;
import java.io.File;
import org.apache.commons.lang3.StringUtils;
import com.google.common.collect.Lists;
import net.minecraft.text.TranslatableTextComponent;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.IOException;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import com.mojang.datafixers.DataFixer;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import org.apache.logging.log4j.Logger;

public class LevelStorage
{
    private static final Logger LOGGER;
    private static final DateTimeFormatter TIME_FORMATTER;
    private final Path savesDirectory;
    private final Path backupsDirectory;
    private final DataFixer dataFixer;
    
    public LevelStorage(final Path savesDirectory, final Path backupsDirectory, final DataFixer dataFixer) {
        this.dataFixer = dataFixer;
        try {
            Files.createDirectories(Files.exists(savesDirectory) ? savesDirectory.toRealPath() : savesDirectory, new FileAttribute[0]);
        }
        catch (IOException iOException4) {
            throw new RuntimeException(iOException4);
        }
        this.savesDirectory = savesDirectory;
        this.backupsDirectory = backupsDirectory;
    }
    
    @Environment(EnvType.CLIENT)
    public String getName() {
        return "Anvil";
    }
    
    @Environment(EnvType.CLIENT)
    public List<LevelSummary> getLevelList() throws LevelStorageException {
        if (!Files.isDirectory(this.savesDirectory)) {
            throw new LevelStorageException(new TranslatableTextComponent("selectWorld.load_folder_access", new Object[0]).getString());
        }
        final List<LevelSummary> list1 = Lists.newArrayList();
        final File[] listFiles;
        final File[] arr2 = listFiles = this.savesDirectory.toFile().listFiles();
        for (final File file6 : listFiles) {
            if (file6.isDirectory()) {
                final String string7 = file6.getName();
                final LevelProperties levelProperties8 = this.getLevelProperties(string7);
                if (levelProperties8 != null && (levelProperties8.getVersion() == 19132 || levelProperties8.getVersion() == 19133)) {
                    final boolean boolean9 = levelProperties8.getVersion() != this.getCurrentVersion();
                    String string8 = levelProperties8.getLevelName();
                    if (StringUtils.isEmpty((CharSequence)string8)) {
                        string8 = string7;
                    }
                    final long long11 = 0L;
                    list1.add(new LevelSummary(levelProperties8, string7, string8, 0L, boolean9));
                }
            }
        }
        return list1;
    }
    
    private int getCurrentVersion() {
        return 19133;
    }
    
    public WorldSaveHandler createSaveHandler(final String name, @Nullable final MinecraftServer server) {
        return createSaveHandler(this.savesDirectory, this.dataFixer, name, server);
    }
    
    protected static WorldSaveHandler createSaveHandler(final Path savesDirectory, final DataFixer dataFixer, final String name, @Nullable final MinecraftServer server) {
        return new WorldSaveHandler(savesDirectory.toFile(), name, server, dataFixer);
    }
    
    public boolean requiresConversion(final String name) {
        final LevelProperties levelProperties2 = this.getLevelProperties(name);
        return levelProperties2 != null && levelProperties2.getVersion() != this.getCurrentVersion();
    }
    
    public boolean convertLevel(final String name, final ProgressListener progressListener) {
        return AnvilLevelStorage.convertLevel(this.savesDirectory, this.dataFixer, name, progressListener);
    }
    
    @Nullable
    public LevelProperties getLevelProperties(final String string) {
        return getLevelProperties(this.savesDirectory, this.dataFixer, string);
    }
    
    @Nullable
    protected static LevelProperties getLevelProperties(final Path savesDirectory, final DataFixer dataFixer, final String name) {
        final File file4 = new File(savesDirectory.toFile(), name);
        if (!file4.exists()) {
            return null;
        }
        File file5 = new File(file4, "level.dat");
        if (file5.exists()) {
            final LevelProperties levelProperties6 = readLevelProperties(file5, dataFixer);
            if (levelProperties6 != null) {
                return levelProperties6;
            }
        }
        file5 = new File(file4, "level.dat_old");
        if (file5.exists()) {
            return readLevelProperties(file5, dataFixer);
        }
        return null;
    }
    
    @Nullable
    public static LevelProperties readLevelProperties(final File file, final DataFixer dataFixer) {
        try {
            final CompoundTag compoundTag3 = NbtIo.readCompressed(new FileInputStream(file));
            final CompoundTag compoundTag4 = compoundTag3.getCompound("Data");
            final CompoundTag compoundTag5 = compoundTag4.containsKey("Player", 10) ? compoundTag4.getCompound("Player") : null;
            compoundTag4.remove("Player");
            final int integer6 = compoundTag4.containsKey("DataVersion", 99) ? compoundTag4.getInt("DataVersion") : -1;
            return new LevelProperties(TagHelper.update(dataFixer, DataFixTypes.a, compoundTag4, integer6), dataFixer, integer6, compoundTag5);
        }
        catch (Exception exception3) {
            LevelStorage.LOGGER.error("Exception reading {}", file, exception3);
            return null;
        }
    }
    
    @Environment(EnvType.CLIENT)
    public void renameLevel(final String name, final String newName) {
        final File file3 = new File(this.savesDirectory.toFile(), name);
        if (!file3.exists()) {
            return;
        }
        final File file4 = new File(file3, "level.dat");
        if (file4.exists()) {
            try {
                final CompoundTag compoundTag5 = NbtIo.readCompressed(new FileInputStream(file4));
                final CompoundTag compoundTag6 = compoundTag5.getCompound("Data");
                compoundTag6.putString("LevelName", newName);
                NbtIo.writeCompressed(compoundTag5, new FileOutputStream(file4));
            }
            catch (Exception exception5) {
                exception5.printStackTrace();
            }
        }
    }
    
    @Environment(EnvType.CLIENT)
    public boolean isLevelNameValid(final String string) {
        try {
            final Path path2 = this.savesDirectory.resolve(string);
            Files.createDirectory(path2, new FileAttribute[0]);
            Files.deleteIfExists(path2);
            return true;
        }
        catch (IOException iOException2) {
            return false;
        }
    }
    
    @Environment(EnvType.CLIENT)
    public boolean deleteLevel(final String name) {
        final File file2 = new File(this.savesDirectory.toFile(), name);
        if (!file2.exists()) {
            return true;
        }
        LevelStorage.LOGGER.info("Deleting level {}", name);
        for (int integer3 = 1; integer3 <= 5; ++integer3) {
            LevelStorage.LOGGER.info("Attempt {}...", integer3);
            if (deleteFilesRecursively(file2.listFiles())) {
                break;
            }
            LevelStorage.LOGGER.warn("Unsuccessful in deleting contents.");
            if (integer3 < 5) {
                try {
                    Thread.sleep(500L);
                }
                catch (InterruptedException ex) {}
            }
        }
        return file2.delete();
    }
    
    @Environment(EnvType.CLIENT)
    private static boolean deleteFilesRecursively(final File[] files) {
        for (final File file5 : files) {
            LevelStorage.LOGGER.debug("Deleting {}", file5);
            if (file5.isDirectory() && !deleteFilesRecursively(file5.listFiles())) {
                LevelStorage.LOGGER.warn("Couldn't delete directory {}", file5);
                return false;
            }
            if (!file5.delete()) {
                LevelStorage.LOGGER.warn("Couldn't delete file {}", file5);
                return false;
            }
        }
        return true;
    }
    
    @Environment(EnvType.CLIENT)
    public boolean levelExists(final String name) {
        return Files.isDirectory(this.savesDirectory.resolve(name));
    }
    
    @Environment(EnvType.CLIENT)
    public Path getSavesDirectory() {
        return this.savesDirectory;
    }
    
    public File resolveFile(final String string1, final String string2) {
        return this.savesDirectory.resolve(string1).resolve(string2).toFile();
    }
    
    @Environment(EnvType.CLIENT)
    private Path resolvePath(final String string) {
        return this.savesDirectory.resolve(string);
    }
    
    @Environment(EnvType.CLIENT)
    public Path getBackupsDirectory() {
        return this.backupsDirectory;
    }
    
    @Environment(EnvType.CLIENT)
    public long backupLevel(final String name) throws IOException {
        final Path path2 = this.resolvePath(name);
        final String string3 = LocalDateTime.now().format(LevelStorage.TIME_FORMATTER) + "_" + name;
        final Path path3 = this.getBackupsDirectory();
        try {
            Files.createDirectories(Files.exists(path3) ? path3.toRealPath() : path3, new FileAttribute[0]);
        }
        catch (IOException iOException5) {
            throw new RuntimeException(iOException5);
        }
        final Path path4 = path3.resolve(FileNameUtil.getNextUniqueName(path3, string3, ".zip"));
        try (final ZipOutputStream zipOutputStream6 = new ZipOutputStream(new BufferedOutputStream(Files.newOutputStream(path4)))) {
            final Path path5 = Paths.get(name);
            Files.walkFileTree(path2, new SimpleFileVisitor<Path>() {
                public FileVisitResult a(final Path path, final BasicFileAttributes basicFileAttributes) throws IOException {
                    final String string3 = path5.resolve(path2.relativize(path)).toString().replace('\\', '/');
                    final ZipEntry zipEntry4 = new ZipEntry(string3);
                    zipOutputStream6.putNextEntry(zipEntry4);
                    com.google.common.io.Files.asByteSource(path.toFile()).copyTo(zipOutputStream6);
                    zipOutputStream6.closeEntry();
                    return FileVisitResult.CONTINUE;
                }
            });
        }
        return Files.size(path4);
    }
    
    static {
        LOGGER = LogManager.getLogger();
        TIME_FORMATTER = new DateTimeFormatterBuilder().appendValue(ChronoField.YEAR, 4, 10, SignStyle.EXCEEDS_PAD).appendLiteral('-').appendValue(ChronoField.MONTH_OF_YEAR, 2).appendLiteral('-').appendValue(ChronoField.DAY_OF_MONTH, 2).appendLiteral('_').appendValue(ChronoField.HOUR_OF_DAY, 2).appendLiteral('-').appendValue(ChronoField.MINUTE_OF_HOUR, 2).appendLiteral('-').appendValue(ChronoField.SECOND_OF_MINUTE, 2).toFormatter();
    }
}

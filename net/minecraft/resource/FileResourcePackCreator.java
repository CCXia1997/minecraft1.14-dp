package net.minecraft.resource;

import java.util.function.Supplier;
import java.util.Map;
import java.io.File;
import java.io.FileFilter;

public class FileResourcePackCreator implements ResourcePackCreator
{
    private static final FileFilter POSSIBLE_PACK;
    private final File packsFolder;
    
    public FileResourcePackCreator(final File packsFolder) {
        this.packsFolder = packsFolder;
    }
    
    @Override
    public <T extends ResourcePackContainer> void registerContainer(final Map<String, T> registry, final ResourcePackContainer.Factory<T> factory) {
        if (!this.packsFolder.isDirectory()) {
            this.packsFolder.mkdirs();
        }
        final File[] arr3 = this.packsFolder.listFiles(FileResourcePackCreator.POSSIBLE_PACK);
        if (arr3 == null) {
            return;
        }
        for (final File file7 : arr3) {
            final String string8 = "file/" + file7.getName();
            final T resourcePackContainer9 = ResourcePackContainer.<T>of(string8, false, this.createResourcePack(file7), factory, ResourcePackContainer.SortingDirection.a);
            if (resourcePackContainer9 != null) {
                registry.put(string8, resourcePackContainer9);
            }
        }
    }
    
    private Supplier<ResourcePack> createResourcePack(final File file) {
        if (file.isDirectory()) {
            return (Supplier<ResourcePack>)(() -> new DirectoryResourcePack(file));
        }
        return (Supplier<ResourcePack>)(() -> new ZipResourcePack(file));
    }
    
    static {
        final boolean boolean2;
        final boolean b;
        final boolean boolean3;
        POSSIBLE_PACK = (file -> {
            boolean2 = (file.isFile() && file.getName().endsWith(".zip"));
            Label_0053_1: {
                if (file.isDirectory()) {
                    if (new File(file, "pack.mcmeta").isFile()) {
                        break Label_0053_1;
                    }
                }
            }
            boolean3 = b;
            return boolean2 || boolean3;
        });
    }
}

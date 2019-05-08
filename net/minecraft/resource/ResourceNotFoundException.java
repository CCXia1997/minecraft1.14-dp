package net.minecraft.resource;

import java.io.File;
import java.io.FileNotFoundException;

public class ResourceNotFoundException extends FileNotFoundException
{
    public ResourceNotFoundException(final File packSource, final String string) {
        super(String.format("'%s' in ResourcePack '%s'", string, packSource));
    }
}

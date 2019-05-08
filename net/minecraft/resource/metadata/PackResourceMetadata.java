package net.minecraft.resource.metadata;

import net.minecraft.text.TextComponent;

public class PackResourceMetadata
{
    public static final PackResourceMetadataReader READER;
    private final TextComponent description;
    private final int packFormat;
    
    public PackResourceMetadata(final TextComponent textComponent, final int integer) {
        this.description = textComponent;
        this.packFormat = integer;
    }
    
    public TextComponent getDescription() {
        return this.description;
    }
    
    public int getPackFormat() {
        return this.packFormat;
    }
    
    static {
        READER = new PackResourceMetadataReader();
    }
}

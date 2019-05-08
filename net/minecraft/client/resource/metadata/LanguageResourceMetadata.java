package net.minecraft.client.resource.metadata;

import net.minecraft.client.resource.language.LanguageDefinition;
import java.util.Collection;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class LanguageResourceMetadata
{
    public static final LanguageResourceMetadataReader READER;
    private final Collection<LanguageDefinition> definitions;
    
    public LanguageResourceMetadata(final Collection<LanguageDefinition> collection) {
        this.definitions = collection;
    }
    
    public Collection<LanguageDefinition> getLanguageDefinitions() {
        return this.definitions;
    }
    
    static {
        READER = new LanguageResourceMetadataReader();
    }
}

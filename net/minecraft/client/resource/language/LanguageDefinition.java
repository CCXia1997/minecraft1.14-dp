package net.minecraft.client.resource.language;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import com.mojang.bridge.game.Language;

@Environment(EnvType.CLIENT)
public class LanguageDefinition implements Language, Comparable<LanguageDefinition>
{
    private final String code;
    private final String name;
    private final String region;
    private final boolean rightToLeft;
    
    public LanguageDefinition(final String code, final String name, final String region, final boolean boolean4) {
        this.code = code;
        this.name = name;
        this.region = region;
        this.rightToLeft = boolean4;
    }
    
    public String getCode() {
        return this.code;
    }
    
    public String getName() {
        return this.region;
    }
    
    public String getRegion() {
        return this.name;
    }
    
    public boolean isRightToLeft() {
        return this.rightToLeft;
    }
    
    @Override
    public String toString() {
        return String.format("%s (%s)", this.region, this.name);
    }
    
    @Override
    public boolean equals(final Object o) {
        return this == o || (o instanceof LanguageDefinition && this.code.equals(((LanguageDefinition)o).code));
    }
    
    @Override
    public int hashCode() {
        return this.code.hashCode();
    }
    
    public int a(final LanguageDefinition languageDefinition) {
        return this.code.compareTo(languageDefinition.code);
    }
}

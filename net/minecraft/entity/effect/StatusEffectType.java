package net.minecraft.entity.effect;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.TextFormat;

public enum StatusEffectType
{
    a(TextFormat.j), 
    b(TextFormat.m), 
    c(TextFormat.j);
    
    private final TextFormat formatting;
    
    private StatusEffectType(final TextFormat format) {
        this.formatting = format;
    }
    
    @Environment(EnvType.CLIENT)
    public TextFormat getFormatting() {
        return this.formatting;
    }
}

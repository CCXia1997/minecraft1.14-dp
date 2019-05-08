package net.minecraft.util;

import net.minecraft.text.TextFormat;

public enum Rarity
{
    a(TextFormat.p), 
    b(TextFormat.o), 
    c(TextFormat.l), 
    d(TextFormat.n);
    
    public final TextFormat formatting;
    
    private Rarity(final TextFormat textFormat) {
        this.formatting = textFormat;
    }
}

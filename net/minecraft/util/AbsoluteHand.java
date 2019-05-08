package net.minecraft.util;

import net.minecraft.text.TranslatableTextComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.TextComponent;

public enum AbsoluteHand
{
    a((TextComponent)new TranslatableTextComponent("options.mainHand.left", new Object[0])), 
    b((TextComponent)new TranslatableTextComponent("options.mainHand.right", new Object[0]));
    
    private final TextComponent name;
    
    private AbsoluteHand(final TextComponent textComponent) {
        this.name = textComponent;
    }
    
    @Environment(EnvType.CLIENT)
    public AbsoluteHand getOpposite() {
        if (this == AbsoluteHand.a) {
            return AbsoluteHand.b;
        }
        return AbsoluteHand.a;
    }
    
    @Override
    public String toString() {
        return this.name.getString();
    }
}

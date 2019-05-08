package net.minecraft.resource;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.text.TextComponent;

public enum ResourcePackCompatibility
{
    a("old"), 
    b("new"), 
    c("compatible");
    
    private final TextComponent notification;
    private final TextComponent confirmMessage;
    
    private ResourcePackCompatibility(final String string1) {
        this.notification = new TranslatableTextComponent("resourcePack.incompatible." + string1, new Object[0]);
        this.confirmMessage = new TranslatableTextComponent("resourcePack.incompatible.confirm." + string1, new Object[0]);
    }
    
    public boolean isCompatible() {
        return this == ResourcePackCompatibility.c;
    }
    
    public static ResourcePackCompatibility from(final int integer) {
        if (integer < SharedConstants.getGameVersion().getPackVersion()) {
            return ResourcePackCompatibility.a;
        }
        if (integer > SharedConstants.getGameVersion().getPackVersion()) {
            return ResourcePackCompatibility.b;
        }
        return ResourcePackCompatibility.c;
    }
    
    @Environment(EnvType.CLIENT)
    public TextComponent getNotification() {
        return this.notification;
    }
    
    @Environment(EnvType.CLIENT)
    public TextComponent getConfirmMessage() {
        return this.confirmMessage;
    }
}

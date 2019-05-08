package net.minecraft.client.options;

import java.util.Comparator;
import java.util.Arrays;
import net.minecraft.util.math.MathHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public enum ChatVisibility
{
    FULL(0, "options.chat.visibility.full"), 
    COMMANDS(1, "options.chat.visibility.system"), 
    HIDDEN(2, "options.chat.visibility.hidden");
    
    private static final ChatVisibility[] d;
    private final int id;
    private final String key;
    
    private ChatVisibility(final int integer1, final String string2) {
        this.id = integer1;
        this.key = string2;
    }
    
    public int getId() {
        return this.id;
    }
    
    @Environment(EnvType.CLIENT)
    public String getTranslationKey() {
        return this.key;
    }
    
    @Environment(EnvType.CLIENT)
    public static ChatVisibility byId(final int integer) {
        return ChatVisibility.d[MathHelper.floorMod(integer, ChatVisibility.d.length)];
    }
    
    static {
        d = Arrays.<ChatVisibility>stream(values()).sorted(Comparator.comparingInt(ChatVisibility::getId)).<ChatVisibility>toArray(ChatVisibility[]::new);
    }
}

package net.minecraft.advancement;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.TextFormat;

public enum AdvancementFrame
{
    TASK("task", 0, TextFormat.k), 
    CHALLENGE("challenge", 26, TextFormat.f), 
    GOAL("goal", 52, TextFormat.k);
    
    private final String id;
    private final int texV;
    private final TextFormat titleFormat;
    
    private AdvancementFrame(final String id, final int texV, final TextFormat textFormat) {
        this.id = id;
        this.texV = texV;
        this.titleFormat = textFormat;
    }
    
    public String getId() {
        return this.id;
    }
    
    @Environment(EnvType.CLIENT)
    public int texV() {
        return this.texV;
    }
    
    public static AdvancementFrame forName(final String name) {
        for (final AdvancementFrame advancementFrame5 : values()) {
            if (advancementFrame5.id.equals(name)) {
                return advancementFrame5;
            }
        }
        throw new IllegalArgumentException("Unknown frame type '" + name + "'");
    }
    
    public TextFormat getTitleFormat() {
        return this.titleFormat;
    }
}

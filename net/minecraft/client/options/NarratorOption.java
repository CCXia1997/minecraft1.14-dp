package net.minecraft.client.options;

import java.util.Comparator;
import java.util.Arrays;
import net.minecraft.util.math.MathHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public enum NarratorOption
{
    a(0, "options.narrator.off"), 
    b(1, "options.narrator.all"), 
    c(2, "options.narrator.chat"), 
    d(3, "options.narrator.system");
    
    private static final NarratorOption[] VALUES;
    private final int id;
    private final String translationKey;
    
    private NarratorOption(final int id, final String translationKey) {
        this.id = id;
        this.translationKey = translationKey;
    }
    
    public int getId() {
        return this.id;
    }
    
    public String getTranslationKey() {
        return this.translationKey;
    }
    
    public static NarratorOption byId(final int id) {
        return NarratorOption.VALUES[MathHelper.floorMod(id, NarratorOption.VALUES.length)];
    }
    
    static {
        VALUES = Arrays.<NarratorOption>stream(values()).sorted(Comparator.comparingInt(NarratorOption::getId)).<NarratorOption>toArray(NarratorOption[]::new);
    }
}

package net.minecraft.client.options;

import java.util.Comparator;
import java.util.Arrays;
import net.minecraft.util.math.MathHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public enum AoOption
{
    a(0, "options.ao.off"), 
    b(1, "options.ao.min"), 
    c(2, "options.ao.max");
    
    private static final AoOption[] OPTIONS;
    private final int value;
    private final String translationKey;
    
    private AoOption(final int integer1, final String string2) {
        this.value = integer1;
        this.translationKey = string2;
    }
    
    public int getValue() {
        return this.value;
    }
    
    public String getTranslationKey() {
        return this.translationKey;
    }
    
    public static AoOption getOption(final int integer) {
        return AoOption.OPTIONS[MathHelper.floorMod(integer, AoOption.OPTIONS.length)];
    }
    
    static {
        OPTIONS = Arrays.<AoOption>stream(values()).sorted(Comparator.comparingInt(AoOption::getValue)).<AoOption>toArray(AoOption[]::new);
    }
}

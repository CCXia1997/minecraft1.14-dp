package net.minecraft.client.options;

import java.util.Comparator;
import java.util.Arrays;
import net.minecraft.util.math.MathHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public enum CloudRenderMode
{
    a(0, "options.off"), 
    b(1, "options.clouds.fast"), 
    c(2, "options.clouds.fancy");
    
    private static final CloudRenderMode[] RENDER_MODES;
    private final int value;
    private final String translationKey;
    
    private CloudRenderMode(final int integer1, final String string2) {
        this.value = integer1;
        this.translationKey = string2;
    }
    
    public int getValue() {
        return this.value;
    }
    
    public String getTranslationKey() {
        return this.translationKey;
    }
    
    public static CloudRenderMode getOption(final int id) {
        return CloudRenderMode.RENDER_MODES[MathHelper.floorMod(id, CloudRenderMode.RENDER_MODES.length)];
    }
    
    static {
        RENDER_MODES = Arrays.<CloudRenderMode>stream(values()).sorted(Comparator.comparingInt(CloudRenderMode::getValue)).<CloudRenderMode>toArray(CloudRenderMode[]::new);
    }
}

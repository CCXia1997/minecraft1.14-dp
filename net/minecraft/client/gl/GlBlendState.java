package net.minecraft.client.gl;

import java.util.Locale;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class GlBlendState
{
    private static GlBlendState activeBlendState;
    private final int srcRgb;
    private final int srcAlpha;
    private final int dstRgb;
    private final int dstAlpha;
    private final int func;
    private final boolean separateBlend;
    private final boolean blendDisabled;
    
    private GlBlendState(final boolean separateBlend, final boolean blendDisabled, final int srcRgb, final int dstRgb, final int srcAlpha, final int dstAlpha, final int func) {
        this.separateBlend = separateBlend;
        this.srcRgb = srcRgb;
        this.dstRgb = dstRgb;
        this.srcAlpha = srcAlpha;
        this.dstAlpha = dstAlpha;
        this.blendDisabled = blendDisabled;
        this.func = func;
    }
    
    public GlBlendState() {
        this(false, true, 1, 0, 1, 0, 32774);
    }
    
    public GlBlendState(final int srcRgb, final int dstRgb, final int func) {
        this(false, false, srcRgb, dstRgb, srcRgb, dstRgb, func);
    }
    
    public GlBlendState(final int srcRgb, final int dstRgb, final int srcAlpha, final int dstAlpha, final int func) {
        this(true, false, srcRgb, dstRgb, srcAlpha, dstAlpha, func);
    }
    
    public void enable() {
        if (this.equals(GlBlendState.activeBlendState)) {
            return;
        }
        if (GlBlendState.activeBlendState == null || this.blendDisabled != GlBlendState.activeBlendState.isBlendDisabled()) {
            GlBlendState.activeBlendState = this;
            if (this.blendDisabled) {
                GlStateManager.disableBlend();
                return;
            }
            GlStateManager.enableBlend();
        }
        GlStateManager.blendEquation(this.func);
        if (this.separateBlend) {
            GlStateManager.blendFuncSeparate(this.srcRgb, this.dstRgb, this.srcAlpha, this.dstAlpha);
        }
        else {
            GlStateManager.blendFunc(this.srcRgb, this.dstRgb);
        }
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GlBlendState)) {
            return false;
        }
        final GlBlendState glBlendState2 = (GlBlendState)o;
        return this.func == glBlendState2.func && this.dstAlpha == glBlendState2.dstAlpha && this.dstRgb == glBlendState2.dstRgb && this.blendDisabled == glBlendState2.blendDisabled && this.separateBlend == glBlendState2.separateBlend && this.srcAlpha == glBlendState2.srcAlpha && this.srcRgb == glBlendState2.srcRgb;
    }
    
    @Override
    public int hashCode() {
        int integer1 = this.srcRgb;
        integer1 = 31 * integer1 + this.srcAlpha;
        integer1 = 31 * integer1 + this.dstRgb;
        integer1 = 31 * integer1 + this.dstAlpha;
        integer1 = 31 * integer1 + this.func;
        integer1 = 31 * integer1 + (this.separateBlend ? 1 : 0);
        integer1 = 31 * integer1 + (this.blendDisabled ? 1 : 0);
        return integer1;
    }
    
    public boolean isBlendDisabled() {
        return this.blendDisabled;
    }
    
    public static int getFuncFromString(final String string) {
        final String string2 = string.trim().toLowerCase(Locale.ROOT);
        if ("add".equals(string2)) {
            return 32774;
        }
        if ("subtract".equals(string2)) {
            return 32778;
        }
        if ("reversesubtract".equals(string2)) {
            return 32779;
        }
        if ("reverse_subtract".equals(string2)) {
            return 32779;
        }
        if ("min".equals(string2)) {
            return 32775;
        }
        if ("max".equals(string2)) {
            return 32776;
        }
        return 32774;
    }
    
    public static int getComponentFromString(final String string) {
        String string2 = string.trim().toLowerCase(Locale.ROOT);
        string2 = string2.replaceAll("_", "");
        string2 = string2.replaceAll("one", "1");
        string2 = string2.replaceAll("zero", "0");
        string2 = string2.replaceAll("minus", "-");
        if ("0".equals(string2)) {
            return 0;
        }
        if ("1".equals(string2)) {
            return 1;
        }
        if ("srccolor".equals(string2)) {
            return 768;
        }
        if ("1-srccolor".equals(string2)) {
            return 769;
        }
        if ("dstcolor".equals(string2)) {
            return 774;
        }
        if ("1-dstcolor".equals(string2)) {
            return 775;
        }
        if ("srcalpha".equals(string2)) {
            return 770;
        }
        if ("1-srcalpha".equals(string2)) {
            return 771;
        }
        if ("dstalpha".equals(string2)) {
            return 772;
        }
        if ("1-dstalpha".equals(string2)) {
            return 773;
        }
        return -1;
    }
}

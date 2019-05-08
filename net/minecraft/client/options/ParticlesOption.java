package net.minecraft.client.options;

import java.util.Comparator;
import java.util.Arrays;
import net.minecraft.util.math.MathHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public enum ParticlesOption
{
    a(0, "options.particles.all"), 
    b(1, "options.particles.decreased"), 
    c(2, "options.particles.minimal");
    
    private static final ParticlesOption[] VALUES;
    private final int id;
    private final String translationKey;
    
    private ParticlesOption(final int id, final String translationKey) {
        this.id = id;
        this.translationKey = translationKey;
    }
    
    public String getTranslationKey() {
        return this.translationKey;
    }
    
    public int getId() {
        return this.id;
    }
    
    public static ParticlesOption byId(final int id) {
        return ParticlesOption.VALUES[MathHelper.floorMod(id, ParticlesOption.VALUES.length)];
    }
    
    static {
        VALUES = Arrays.<ParticlesOption>stream(values()).sorted(Comparator.comparingInt(ParticlesOption::getId)).<ParticlesOption>toArray(ParticlesOption[]::new);
    }
}

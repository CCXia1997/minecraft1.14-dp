package net.minecraft.client.options;

import java.util.Comparator;
import java.util.Arrays;
import net.minecraft.util.math.MathHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public enum AttackIndicator
{
    a(0, "options.off"), 
    b(1, "options.attack.crosshair"), 
    c(2, "options.attack.hotbar");
    
    private static final AttackIndicator[] VALUES;
    private final int id;
    private final String translationKey;
    
    private AttackIndicator(final int id, final String translationKey) {
        this.id = id;
        this.translationKey = translationKey;
    }
    
    public int getId() {
        return this.id;
    }
    
    public String getTranslationKey() {
        return this.translationKey;
    }
    
    public static AttackIndicator byId(final int id) {
        return AttackIndicator.VALUES[MathHelper.floorMod(id, AttackIndicator.VALUES.length)];
    }
    
    static {
        VALUES = Arrays.<AttackIndicator>stream(values()).sorted(Comparator.comparingInt(AttackIndicator::getId)).<AttackIndicator>toArray(AttackIndicator[]::new);
    }
}

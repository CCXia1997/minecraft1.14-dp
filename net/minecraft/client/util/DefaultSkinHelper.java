package net.minecraft.client.util;

import java.util.UUID;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class DefaultSkinHelper
{
    private static final Identifier STEVE_SKIN;
    private static final Identifier ALEX_SKIN;
    
    public static Identifier getTexture() {
        return DefaultSkinHelper.STEVE_SKIN;
    }
    
    public static Identifier getTexture(final UUID uuid) {
        if (shouldUseSlimModel(uuid)) {
            return DefaultSkinHelper.ALEX_SKIN;
        }
        return DefaultSkinHelper.STEVE_SKIN;
    }
    
    public static String getModel(final UUID uuid) {
        if (shouldUseSlimModel(uuid)) {
            return "slim";
        }
        return "default";
    }
    
    private static boolean shouldUseSlimModel(final UUID uuid) {
        return (uuid.hashCode() & 0x1) == 0x1;
    }
    
    static {
        STEVE_SKIN = new Identifier("textures/entity/steve.png");
        ALEX_SKIN = new Identifier("textures/entity/alex.png");
    }
}

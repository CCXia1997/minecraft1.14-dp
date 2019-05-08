package net.minecraft.client.particle;

import java.util.Random;
import net.minecraft.client.texture.Sprite;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface SpriteProvider
{
    Sprite getSprite(final int arg1, final int arg2);
    
    Sprite getSprite(final Random arg1);
}

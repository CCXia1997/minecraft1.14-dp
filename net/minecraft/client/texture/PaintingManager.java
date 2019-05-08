package net.minecraft.client.texture;

import net.minecraft.entity.decoration.painting.PaintingMotive;
import com.google.common.collect.Iterables;
import java.util.Collections;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class PaintingManager extends SpriteAtlasHolder
{
    private static final Identifier PAINTING_BACK_ID;
    
    public PaintingManager(final TextureManager textureManager) {
        super(textureManager, SpriteAtlasTexture.PAINTING_ATLAS_TEX, "textures/painting");
    }
    
    @Override
    protected Iterable<Identifier> getSprites() {
        return Iterables.<Identifier>concat(Registry.MOTIVE.getIds(), Collections.<Identifier>singleton(PaintingManager.PAINTING_BACK_ID));
    }
    
    public Sprite getPaintingSprite(final PaintingMotive motive) {
        return this.getSprite(Registry.MOTIVE.getId(motive));
    }
    
    public Sprite getBackSprite() {
        return this.getSprite(PaintingManager.PAINTING_BACK_ID);
    }
    
    static {
        PAINTING_BACK_ID = new Identifier("back");
    }
}

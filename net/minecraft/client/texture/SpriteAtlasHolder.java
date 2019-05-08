package net.minecraft.client.texture;

import net.minecraft.util.profiler.Profiler;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.SupplyingResourceReloadListener;

@Environment(EnvType.CLIENT)
public abstract class SpriteAtlasHolder extends SupplyingResourceReloadListener<SpriteAtlasTexture.Data> implements AutoCloseable
{
    private final SpriteAtlasTexture atlas;
    
    public SpriteAtlasHolder(final TextureManager textureManager, final Identifier identifier, final String string) {
        textureManager.registerTextureUpdateable(identifier, this.atlas = new SpriteAtlasTexture(string));
    }
    
    protected abstract Iterable<Identifier> getSprites();
    
    protected Sprite getSprite(final Identifier identifier) {
        return this.atlas.getSprite(identifier);
    }
    
    @Override
    protected SpriteAtlasTexture.Data load(final ResourceManager resourceManager, final Profiler profiler) {
        profiler.startTick();
        profiler.push("stitching");
        final SpriteAtlasTexture.Data data3 = this.atlas.stitch(resourceManager, this.getSprites(), profiler);
        profiler.pop();
        profiler.endTick();
        return data3;
    }
    
    @Override
    protected void apply(final SpriteAtlasTexture.Data result, final ResourceManager resourceManager, final Profiler profiler) {
        profiler.startTick();
        profiler.push("upload");
        this.atlas.upload(result);
        profiler.pop();
        profiler.endTick();
    }
    
    @Override
    public void close() {
        this.atlas.clear();
    }
}

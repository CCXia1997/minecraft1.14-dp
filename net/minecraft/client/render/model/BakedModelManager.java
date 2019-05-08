package net.minecraft.client.render.model;

import net.minecraft.util.profiler.Profiler;
import net.minecraft.resource.ResourceManager;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.SupplyingResourceReloadListener;

@Environment(EnvType.CLIENT)
public class BakedModelManager extends SupplyingResourceReloadListener<ModelLoader>
{
    private Map<Identifier, BakedModel> modelMap;
    private final SpriteAtlasTexture spriteAtlas;
    private final BlockModels blockStateMaps;
    private BakedModel missingModel;
    
    public BakedModelManager(final SpriteAtlasTexture spriteAtlas) {
        this.spriteAtlas = spriteAtlas;
        this.blockStateMaps = new BlockModels(this);
    }
    
    public BakedModel getModel(final ModelIdentifier id) {
        return this.modelMap.getOrDefault(id, this.missingModel);
    }
    
    public BakedModel getMissingModel() {
        return this.missingModel;
    }
    
    public BlockModels getBlockStateMaps() {
        return this.blockStateMaps;
    }
    
    @Override
    protected ModelLoader load(final ResourceManager resourceManager, final Profiler profiler) {
        profiler.startTick();
        final ModelLoader modelLoader3 = new ModelLoader(resourceManager, this.spriteAtlas, profiler);
        profiler.endTick();
        return modelLoader3;
    }
    
    @Override
    protected void apply(final ModelLoader result, final ResourceManager resourceManager, final Profiler profiler) {
        profiler.startTick();
        profiler.push("upload");
        result.upload(profiler);
        this.modelMap = result.getBakedModelMap();
        this.missingModel = this.modelMap.get(ModelLoader.MISSING);
        profiler.swap("cache");
        this.blockStateMaps.reload();
        profiler.pop();
        profiler.endTick();
    }
}

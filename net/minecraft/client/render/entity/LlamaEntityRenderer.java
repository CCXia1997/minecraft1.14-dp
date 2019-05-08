package net.minecraft.client.render.entity;

import net.minecraft.entity.Entity;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.LlamaDecorFeatureRenderer;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.LlamaEntityModel;
import net.minecraft.entity.passive.LlamaEntity;

@Environment(EnvType.CLIENT)
public class LlamaEntityRenderer extends MobEntityRenderer<LlamaEntity, LlamaEntityModel<LlamaEntity>>
{
    private static final Identifier[] TEXTURES;
    
    public LlamaEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new LlamaEntityModel(0.0f), 0.7f);
        this.addFeature(new LlamaDecorFeatureRenderer(this));
    }
    
    protected Identifier getTexture(final LlamaEntity llamaEntity) {
        return LlamaEntityRenderer.TEXTURES[llamaEntity.getVariant()];
    }
    
    static {
        TEXTURES = new Identifier[] { new Identifier("textures/entity/llama/creamy.png"), new Identifier("textures/entity/llama/white.png"), new Identifier("textures/entity/llama/brown.png"), new Identifier("textures/entity/llama/gray.png") };
    }
}

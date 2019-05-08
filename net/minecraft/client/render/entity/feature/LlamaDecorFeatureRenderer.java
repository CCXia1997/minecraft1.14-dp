package net.minecraft.client.render.entity.feature;

import net.minecraft.entity.Entity;
import net.minecraft.util.DyeColor;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.LlamaEntityModel;
import net.minecraft.entity.passive.LlamaEntity;

@Environment(EnvType.CLIENT)
public class LlamaDecorFeatureRenderer extends FeatureRenderer<LlamaEntity, LlamaEntityModel<LlamaEntity>>
{
    private static final Identifier[] LLAMA_DECOR;
    private static final Identifier TRADER_LLAMA_DECOR;
    private final LlamaEntityModel<LlamaEntity> model;
    
    public LlamaDecorFeatureRenderer(final FeatureRendererContext<LlamaEntity, LlamaEntityModel<LlamaEntity>> context) {
        super(context);
        this.model = new LlamaEntityModel<LlamaEntity>(0.5f);
    }
    
    @Override
    public void render(final LlamaEntity entity, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
        final DyeColor dyeColor9 = entity.getCarpetColor();
        if (dyeColor9 != null) {
            this.bindTexture(LlamaDecorFeatureRenderer.LLAMA_DECOR[dyeColor9.getId()]);
        }
        else {
            if (!entity.isTrader()) {
                return;
            }
            this.bindTexture(LlamaDecorFeatureRenderer.TRADER_LLAMA_DECOR);
        }
        ((FeatureRenderer<T, LlamaEntityModel<LlamaEntity>>)this).getModel().copyStateTo(this.model);
        this.model.render(entity, float2, float3, float5, float6, float7, float8);
    }
    
    @Override
    public boolean hasHurtOverlay() {
        return false;
    }
    
    static {
        LLAMA_DECOR = new Identifier[] { new Identifier("textures/entity/llama/decor/white.png"), new Identifier("textures/entity/llama/decor/orange.png"), new Identifier("textures/entity/llama/decor/magenta.png"), new Identifier("textures/entity/llama/decor/light_blue.png"), new Identifier("textures/entity/llama/decor/yellow.png"), new Identifier("textures/entity/llama/decor/lime.png"), new Identifier("textures/entity/llama/decor/pink.png"), new Identifier("textures/entity/llama/decor/gray.png"), new Identifier("textures/entity/llama/decor/light_gray.png"), new Identifier("textures/entity/llama/decor/cyan.png"), new Identifier("textures/entity/llama/decor/purple.png"), new Identifier("textures/entity/llama/decor/blue.png"), new Identifier("textures/entity/llama/decor/brown.png"), new Identifier("textures/entity/llama/decor/green.png"), new Identifier("textures/entity/llama/decor/red.png"), new Identifier("textures/entity/llama/decor/black.png") };
        TRADER_LLAMA_DECOR = new Identifier("textures/entity/llama/decor/trader_llama.png");
    }
}

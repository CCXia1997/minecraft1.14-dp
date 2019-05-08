package net.minecraft.client.render.model;

import com.google.common.collect.Lists;
import net.minecraft.client.render.model.json.ModelItemPropertyOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import java.util.Random;
import net.minecraft.util.math.Direction;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.util.WeightedPicker;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class WeightedBakedModel implements BakedModel
{
    private final int totalWeight;
    private final List<ModelEntry> models;
    private final BakedModel defaultModel;
    
    public WeightedBakedModel(final List<ModelEntry> models) {
        this.models = models;
        this.totalWeight = WeightedPicker.getWeightSum(models);
        this.defaultModel = models.get(0).model;
    }
    
    @Override
    public List<BakedQuad> getQuads(@Nullable final BlockState state, @Nullable final Direction face, final Random random) {
        return WeightedPicker.<ModelEntry>getAt(this.models, Math.abs((int)random.nextLong()) % this.totalWeight).model.getQuads(state, face, random);
    }
    
    @Override
    public boolean useAmbientOcclusion() {
        return this.defaultModel.useAmbientOcclusion();
    }
    
    @Override
    public boolean hasDepthInGui() {
        return this.defaultModel.hasDepthInGui();
    }
    
    @Override
    public boolean isBuiltin() {
        return this.defaultModel.isBuiltin();
    }
    
    @Override
    public Sprite getSprite() {
        return this.defaultModel.getSprite();
    }
    
    @Override
    public ModelTransformation getTransformation() {
        return this.defaultModel.getTransformation();
    }
    
    @Override
    public ModelItemPropertyOverrideList getItemPropertyOverrides() {
        return this.defaultModel.getItemPropertyOverrides();
    }
    
    @Environment(EnvType.CLIENT)
    public static class Builder
    {
        private final List<ModelEntry> models;
        
        public Builder() {
            this.models = Lists.newArrayList();
        }
        
        public Builder add(@Nullable final BakedModel model, final int weight) {
            if (model != null) {
                this.models.add(new ModelEntry(model, weight));
            }
            return this;
        }
        
        @Nullable
        public BakedModel getFirst() {
            if (this.models.isEmpty()) {
                return null;
            }
            if (this.models.size() == 1) {
                return this.models.get(0).model;
            }
            return new WeightedBakedModel(this.models);
        }
    }
    
    @Environment(EnvType.CLIENT)
    static class ModelEntry extends WeightedPicker.Entry
    {
        protected final BakedModel model;
        
        public ModelEntry(final BakedModel model, final int weight) {
            super(weight);
            this.model = model;
        }
    }
}

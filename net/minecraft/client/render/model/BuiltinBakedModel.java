package net.minecraft.client.render.model;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.util.math.Direction;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.render.model.json.ModelItemPropertyOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class BuiltinBakedModel implements BakedModel
{
    private final ModelTransformation transformation;
    private final ModelItemPropertyOverrideList itemPropertyOverrides;
    private final Sprite sprite;
    
    public BuiltinBakedModel(final ModelTransformation transformation, final ModelItemPropertyOverrideList itemPropertyOverrides, final Sprite sprite) {
        this.transformation = transformation;
        this.itemPropertyOverrides = itemPropertyOverrides;
        this.sprite = sprite;
    }
    
    @Override
    public List<BakedQuad> getQuads(@Nullable final BlockState state, @Nullable final Direction face, final Random random) {
        return Collections.<BakedQuad>emptyList();
    }
    
    @Override
    public boolean useAmbientOcclusion() {
        return false;
    }
    
    @Override
    public boolean hasDepthInGui() {
        return true;
    }
    
    @Override
    public boolean isBuiltin() {
        return true;
    }
    
    @Override
    public Sprite getSprite() {
        return this.sprite;
    }
    
    @Override
    public ModelTransformation getTransformation() {
        return this.transformation;
    }
    
    @Override
    public ModelItemPropertyOverrideList getItemPropertyOverrides() {
        return this.itemPropertyOverrides;
    }
}

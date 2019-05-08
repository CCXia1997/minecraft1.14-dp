package net.minecraft.client.render.model;

import com.google.common.collect.Maps;
import com.google.common.collect.Lists;
import java.util.Iterator;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.json.ModelItemPropertyOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;
import java.util.Map;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class BasicBakedModel implements BakedModel
{
    protected final List<BakedQuad> quads;
    protected final Map<Direction, List<BakedQuad>> faceQuads;
    protected final boolean usesAo;
    protected final boolean depthInGui;
    protected final Sprite sprite;
    protected final ModelTransformation transformation;
    protected final ModelItemPropertyOverrideList itemPropertyOverrides;
    
    public BasicBakedModel(final List<BakedQuad> quads, final Map<Direction, List<BakedQuad>> faceQuads, final boolean usesAo, final boolean is3dInGui, final Sprite sprite, final ModelTransformation transformation, final ModelItemPropertyOverrideList itemPropertyOverrides) {
        this.quads = quads;
        this.faceQuads = faceQuads;
        this.usesAo = usesAo;
        this.depthInGui = is3dInGui;
        this.sprite = sprite;
        this.transformation = transformation;
        this.itemPropertyOverrides = itemPropertyOverrides;
    }
    
    @Override
    public List<BakedQuad> getQuads(@Nullable final BlockState state, @Nullable final Direction face, final Random random) {
        return (face == null) ? this.quads : this.faceQuads.get(face);
    }
    
    @Override
    public boolean useAmbientOcclusion() {
        return this.usesAo;
    }
    
    @Override
    public boolean hasDepthInGui() {
        return this.depthInGui;
    }
    
    @Override
    public boolean isBuiltin() {
        return false;
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
    
    @Environment(EnvType.CLIENT)
    public static class Builder
    {
        private final List<BakedQuad> quads;
        private final Map<Direction, List<BakedQuad>> faceQuads;
        private final ModelItemPropertyOverrideList itemPropertyOverrides;
        private final boolean usesAo;
        private Sprite particleTexture;
        private final boolean depthInGui;
        private final ModelTransformation transformation;
        
        public Builder(final JsonUnbakedModel unbakedModel, final ModelItemPropertyOverrideList itemPropertyOverrides) {
            this(unbakedModel.useAmbientOcclusion(), unbakedModel.hasDepthInGui(), unbakedModel.getTransformations(), itemPropertyOverrides);
        }
        
        public Builder(final BlockState state, final BakedModel bakedModel, final Sprite sprite, final Random random, final long randomSeed) {
            this(bakedModel.useAmbientOcclusion(), bakedModel.hasDepthInGui(), bakedModel.getTransformation(), bakedModel.getItemPropertyOverrides());
            this.particleTexture = bakedModel.getSprite();
            for (final Direction direction10 : Direction.values()) {
                random.setSeed(randomSeed);
                for (final BakedQuad bakedQuad12 : bakedModel.getQuads(state, direction10, random)) {
                    this.addQuad(direction10, new RetexturedBakedQuad(bakedQuad12, sprite));
                }
            }
            random.setSeed(randomSeed);
            for (final BakedQuad bakedQuad13 : bakedModel.getQuads(state, null, random)) {
                this.addQuad(new RetexturedBakedQuad(bakedQuad13, sprite));
            }
        }
        
        private Builder(final boolean usesAo, final boolean depthInGui, final ModelTransformation transformation, final ModelItemPropertyOverrideList itemPropertyOverrides) {
            this.quads = Lists.newArrayList();
            this.faceQuads = Maps.newEnumMap(Direction.class);
            for (final Direction direction8 : Direction.values()) {
                this.faceQuads.put(direction8, Lists.newArrayList());
            }
            this.itemPropertyOverrides = itemPropertyOverrides;
            this.usesAo = usesAo;
            this.depthInGui = depthInGui;
            this.transformation = transformation;
        }
        
        public Builder addQuad(final Direction side, final BakedQuad quad) {
            this.faceQuads.get(side).add(quad);
            return this;
        }
        
        public Builder addQuad(final BakedQuad quad) {
            this.quads.add(quad);
            return this;
        }
        
        public Builder setParticle(final Sprite sprite) {
            this.particleTexture = sprite;
            return this;
        }
        
        public BakedModel build() {
            if (this.particleTexture == null) {
                throw new RuntimeException("Missing particle!");
            }
            return new BasicBakedModel(this.quads, this.faceQuads, this.usesAo, this.depthInGui, this.particleTexture, this.transformation, this.itemPropertyOverrides);
        }
    }
}

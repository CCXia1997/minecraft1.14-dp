package net.minecraft.client.render.model;

import java.util.Collection;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.Random;
import net.minecraft.util.math.Direction;
import javax.annotation.Nullable;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenCustomHashMap;
import net.minecraft.util.SystemUtil;
import java.util.BitSet;
import java.util.Map;
import net.minecraft.client.render.model.json.ModelItemPropertyOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.block.BlockState;
import java.util.function.Predicate;
import org.apache.commons.lang3.tuple.Pair;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class MultipartBakedModel implements BakedModel
{
    private final List<Pair<Predicate<BlockState>, BakedModel>> components;
    protected final boolean ambientOcclusion;
    protected final boolean depthGui;
    protected final Sprite sprite;
    protected final ModelTransformation transformations;
    protected final ModelItemPropertyOverrideList itemPropertyOverrides;
    private final Map<BlockState, BitSet> g;
    
    public MultipartBakedModel(final List<Pair<Predicate<BlockState>, BakedModel>> components) {
        this.g = (Map<BlockState, BitSet>)new Object2ObjectOpenCustomHashMap((Hash.Strategy)SystemUtil.identityHashStrategy());
        this.components = components;
        final BakedModel bakedModel2 = (BakedModel)components.iterator().next().getRight();
        this.ambientOcclusion = bakedModel2.useAmbientOcclusion();
        this.depthGui = bakedModel2.hasDepthInGui();
        this.sprite = bakedModel2.getSprite();
        this.transformations = bakedModel2.getTransformation();
        this.itemPropertyOverrides = bakedModel2.getItemPropertyOverrides();
    }
    
    @Override
    public List<BakedQuad> getQuads(@Nullable final BlockState state, @Nullable final Direction face, final Random random) {
        if (state == null) {
            return Collections.<BakedQuad>emptyList();
        }
        BitSet bitSet4 = this.g.get(state);
        if (bitSet4 == null) {
            bitSet4 = new BitSet();
            for (int integer5 = 0; integer5 < this.components.size(); ++integer5) {
                final Pair<Predicate<BlockState>, BakedModel> pair6 = this.components.get(integer5);
                if (((Predicate)pair6.getLeft()).test(state)) {
                    bitSet4.set(integer5);
                }
            }
            this.g.put(state, bitSet4);
        }
        final List<BakedQuad> list5 = Lists.newArrayList();
        final long long6 = random.nextLong();
        for (int integer6 = 0; integer6 < bitSet4.length(); ++integer6) {
            if (bitSet4.get(integer6)) {
                list5.addAll(((BakedModel)this.components.get(integer6).getRight()).getQuads(state, face, new Random(long6)));
            }
        }
        return list5;
    }
    
    @Override
    public boolean useAmbientOcclusion() {
        return this.ambientOcclusion;
    }
    
    @Override
    public boolean hasDepthInGui() {
        return this.depthGui;
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
        return this.transformations;
    }
    
    @Override
    public ModelItemPropertyOverrideList getItemPropertyOverrides() {
        return this.itemPropertyOverrides;
    }
    
    @Environment(EnvType.CLIENT)
    public static class Builder
    {
        private final List<Pair<Predicate<BlockState>, BakedModel>> components;
        
        public Builder() {
            this.components = Lists.newArrayList();
        }
        
        public void addComponent(final Predicate<BlockState> predicate, final BakedModel model) {
            this.components.add((Pair<Predicate<BlockState>, BakedModel>)Pair.of(predicate, model));
        }
        
        public BakedModel build() {
            return new MultipartBakedModel(this.components);
        }
    }
}

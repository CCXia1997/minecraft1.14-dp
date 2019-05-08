package net.minecraft.client.render.model.json;

import net.minecraft.entity.LivingEntity;
import javax.annotation.Nullable;
import net.minecraft.world.World;
import net.minecraft.item.ItemStack;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelRotation;
import java.util.Objects;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.util.Identifier;
import java.util.function.Function;
import net.minecraft.client.render.model.ModelLoader;
import java.util.Collections;
import com.google.common.collect.Lists;
import net.minecraft.client.render.model.BakedModel;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ModelItemPropertyOverrideList
{
    public static final ModelItemPropertyOverrideList EMPTY;
    private final List<ModelItemOverride> overrides;
    private final List<BakedModel> models;
    
    private ModelItemPropertyOverrideList() {
        this.overrides = Lists.newArrayList();
        this.models = Collections.<BakedModel>emptyList();
    }
    
    public ModelItemPropertyOverrideList(final ModelLoader modelLoader, final JsonUnbakedModel unbakedModel, final Function<Identifier, UnbakedModel> unbakedModelGetter, final List<ModelItemOverride> overrides) {
        this.overrides = Lists.newArrayList();
        final UnbakedModel unbakedModel2;
        Collections.reverse(this.models = overrides.stream().map(modelItemOverride -> {
            unbakedModel2 = unbakedModelGetter.apply(modelItemOverride.getModelId());
            if (Objects.equals(unbakedModel2, unbakedModel)) {
                return null;
            }
            else {
                return modelLoader.bake(modelItemOverride.getModelId(), ModelRotation.X0_Y0);
            }
        }).collect(Collectors.toList()));
        for (int integer5 = overrides.size() - 1; integer5 >= 0; --integer5) {
            this.overrides.add(overrides.get(integer5));
        }
    }
    
    @Nullable
    public BakedModel apply(final BakedModel model, final ItemStack stack, @Nullable final World world, @Nullable final LivingEntity entity) {
        if (!this.overrides.isEmpty()) {
            int integer5 = 0;
            while (integer5 < this.overrides.size()) {
                final ModelItemOverride modelItemOverride6 = this.overrides.get(integer5);
                if (modelItemOverride6.matches(stack, world, entity)) {
                    final BakedModel bakedModel7 = this.models.get(integer5);
                    if (bakedModel7 == null) {
                        return model;
                    }
                    return bakedModel7;
                }
                else {
                    ++integer5;
                }
            }
        }
        return model;
    }
    
    static {
        EMPTY = new ModelItemPropertyOverrideList();
    }
}

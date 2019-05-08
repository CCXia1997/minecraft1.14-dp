package net.minecraft.client.render.block;

import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.client.util.ModelIdentifier;
import java.util.Iterator;
import net.minecraft.block.Block;
import net.minecraft.util.registry.Registry;
import net.minecraft.client.texture.Sprite;
import com.google.common.collect.Maps;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.block.BlockState;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class BlockModels
{
    private final Map<BlockState, BakedModel> models;
    private final BakedModelManager modelManager;
    
    public BlockModels(final BakedModelManager bakedModelManager) {
        this.models = Maps.newIdentityHashMap();
        this.modelManager = bakedModelManager;
    }
    
    public Sprite getSprite(final BlockState blockState) {
        return this.getModel(blockState).getSprite();
    }
    
    public BakedModel getModel(final BlockState blockState) {
        BakedModel bakedModel2 = this.models.get(blockState);
        if (bakedModel2 == null) {
            bakedModel2 = this.modelManager.getMissingModel();
        }
        return bakedModel2;
    }
    
    public BakedModelManager getModelManager() {
        return this.modelManager;
    }
    
    public void reload() {
        this.models.clear();
        for (final Block block2 : Registry.BLOCK) {
            final BakedModel bakedModel;
            block2.getStateFactory().getStates().forEach(blockState -> bakedModel = this.models.put(blockState, this.modelManager.getModel(getModelId(blockState))));
        }
    }
    
    public static ModelIdentifier getModelId(final BlockState blockState) {
        return getModelId(Registry.BLOCK.getId(blockState.getBlock()), blockState);
    }
    
    public static ModelIdentifier getModelId(final Identifier identifier, final BlockState blockState) {
        return new ModelIdentifier(identifier, propertyMapToString(blockState.getEntries()));
    }
    
    public static String propertyMapToString(final Map<Property<?>, Comparable<?>> map) {
        final StringBuilder stringBuilder2 = new StringBuilder();
        for (final Map.Entry<Property<?>, Comparable<?>> entry4 : map.entrySet()) {
            if (stringBuilder2.length() != 0) {
                stringBuilder2.append(',');
            }
            final Property<?> property5 = entry4.getKey();
            stringBuilder2.append(property5.getName());
            stringBuilder2.append('=');
            stringBuilder2.append(BlockModels.propertyValueToString(property5, entry4.getValue()));
        }
        return stringBuilder2.toString();
    }
    
    private static <T extends Comparable<T>> String propertyValueToString(final Property<T> property, final Comparable<?> comparable) {
        return property.getValueAsString((T)comparable);
    }
}

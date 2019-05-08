package net.minecraft.client.render.item;

import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.item.Item;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.client.texture.Sprite;
import net.minecraft.item.ItemProvider;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.ModelIdentifier;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ItemModels
{
    public final Int2ObjectMap<ModelIdentifier> modelIds;
    private final Int2ObjectMap<BakedModel> models;
    private final BakedModelManager modelManager;
    
    public ItemModels(final BakedModelManager bakedModelManager) {
        this.modelIds = (Int2ObjectMap<ModelIdentifier>)new Int2ObjectOpenHashMap(256);
        this.models = (Int2ObjectMap<BakedModel>)new Int2ObjectOpenHashMap(256);
        this.modelManager = bakedModelManager;
    }
    
    public Sprite getSprite(final ItemProvider itemProvider) {
        return this.getSprite(new ItemStack(itemProvider));
    }
    
    public Sprite getSprite(final ItemStack itemStack) {
        final BakedModel bakedModel2 = this.getModel(itemStack);
        if ((bakedModel2 == this.modelManager.getMissingModel() || bakedModel2.isBuiltin()) && itemStack.getItem() instanceof BlockItem) {
            return this.modelManager.getBlockStateMaps().getSprite(((BlockItem)itemStack.getItem()).getBlock().getDefaultState());
        }
        return bakedModel2.getSprite();
    }
    
    public BakedModel getModel(final ItemStack itemStack) {
        final BakedModel bakedModel2 = this.getModel(itemStack.getItem());
        return (bakedModel2 == null) ? this.modelManager.getMissingModel() : bakedModel2;
    }
    
    @Nullable
    public BakedModel getModel(final Item item) {
        return (BakedModel)this.models.get(getModelId(item));
    }
    
    private static int getModelId(final Item item) {
        return Item.getRawIdByItem(item);
    }
    
    public void putModel(final Item item, final ModelIdentifier modelId) {
        this.modelIds.put(getModelId(item), modelId);
    }
    
    public BakedModelManager getModelManager() {
        return this.modelManager;
    }
    
    public void reloadModels() {
        this.models.clear();
        for (final Map.Entry<Integer, ModelIdentifier> entry2 : this.modelIds.entrySet()) {
            this.models.put(Integer.valueOf(entry2.getKey()), this.modelManager.getModel(entry2.getValue()));
        }
    }
}

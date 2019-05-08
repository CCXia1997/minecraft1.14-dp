package net.minecraft.client.search;

import java.util.Iterator;
import net.minecraft.resource.ResourceManager;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.recipe.book.RecipeResultCollection;
import net.minecraft.item.ItemStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.SynchronousResourceReloadListener;

@Environment(EnvType.CLIENT)
public class SearchManager implements SynchronousResourceReloadListener
{
    public static final Key<ItemStack> ITEM_TOOLTIP;
    public static final Key<ItemStack> ITEM_TAG;
    public static final Key<RecipeResultCollection> RECIPE_OUTPUT;
    private final Map<Key<?>, SearchableContainer<?>> instances;
    
    public SearchManager() {
        this.instances = Maps.newHashMap();
    }
    
    @Override
    public void apply(final ResourceManager manager) {
        for (final SearchableContainer<?> searchableContainer3 : this.instances.values()) {
            searchableContainer3.reload();
        }
    }
    
    public <T> void put(final Key<T> key, final SearchableContainer<T> value) {
        this.instances.put(key, value);
    }
    
    public <T> SearchableContainer<T> get(final Key<T> key) {
        return (SearchableContainer<T>)this.instances.get(key);
    }
    
    static {
        ITEM_TOOLTIP = new Key<ItemStack>();
        ITEM_TAG = new Key<ItemStack>();
        RECIPE_OUTPUT = new Key<RecipeResultCollection>();
    }
    
    @Environment(EnvType.CLIENT)
    public static class Key<T>
    {
    }
}

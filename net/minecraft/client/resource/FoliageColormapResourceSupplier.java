package net.minecraft.client.resource;

import net.minecraft.client.render.block.FoliageColorHandler;
import java.io.IOException;
import net.minecraft.client.util.RawTextureDataLoader;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.SupplyingResourceReloadListener;

@Environment(EnvType.CLIENT)
public class FoliageColormapResourceSupplier extends SupplyingResourceReloadListener<int[]>
{
    private static final Identifier FOLIAGE_COLORMAP_LOC;
    
    protected int[] b(final ResourceManager resourceManager, final Profiler profiler) {
        try {
            return RawTextureDataLoader.loadRawTextureData(resourceManager, FoliageColormapResourceSupplier.FOLIAGE_COLORMAP_LOC);
        }
        catch (IOException iOException3) {
            throw new IllegalStateException("Failed to load foliage color texture", iOException3);
        }
    }
    
    @Override
    protected void apply(final int[] result, final ResourceManager resourceManager, final Profiler profiler) {
        FoliageColorHandler.setColorMap(result);
    }
    
    static {
        FOLIAGE_COLORMAP_LOC = new Identifier("textures/colormap/foliage.png");
    }
}

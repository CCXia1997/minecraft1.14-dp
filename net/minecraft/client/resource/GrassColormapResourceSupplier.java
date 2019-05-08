package net.minecraft.client.resource;

import net.minecraft.client.render.block.GrassColorHandler;
import java.io.IOException;
import net.minecraft.client.util.RawTextureDataLoader;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.SupplyingResourceReloadListener;

@Environment(EnvType.CLIENT)
public class GrassColormapResourceSupplier extends SupplyingResourceReloadListener<int[]>
{
    private static final Identifier GRASS_COLORMAP_LOC;
    
    protected int[] b(final ResourceManager resourceManager, final Profiler profiler) {
        try {
            return RawTextureDataLoader.loadRawTextureData(resourceManager, GrassColormapResourceSupplier.GRASS_COLORMAP_LOC);
        }
        catch (IOException iOException3) {
            throw new IllegalStateException("Failed to load grass color texture", iOException3);
        }
    }
    
    @Override
    protected void apply(final int[] result, final ResourceManager resourceManager, final Profiler profiler) {
        GrassColorHandler.setColorMap(result);
    }
    
    static {
        GRASS_COLORMAP_LOC = new Identifier("textures/colormap/grass.png");
    }
}

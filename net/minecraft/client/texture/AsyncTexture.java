package net.minecraft.client.texture;

import net.minecraft.util.SystemUtil;
import java.util.concurrent.Executor;
import net.minecraft.util.Identifier;
import net.minecraft.resource.ResourceManager;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class AsyncTexture extends ResourceTexture
{
    private CompletableFuture<TextureData> future;
    
    public AsyncTexture(final ResourceManager resourceManager, final Identifier identifier, final Executor executor) {
        super(identifier);
        this.future = CompletableFuture.<TextureData>supplyAsync(() -> TextureData.load(resourceManager, identifier), executor);
    }
    
    @Override
    protected TextureData loadTextureData(final ResourceManager resourceManager) {
        if (this.future != null) {
            final TextureData textureData2 = this.future.join();
            this.future = null;
            return textureData2;
        }
        return TextureData.load(resourceManager, this.location);
    }
    
    public CompletableFuture<Void> getLoadCompleteFuture() {
        return (this.future == null) ? CompletableFuture.<Void>completedFuture((Void)null) : this.future.<Void>thenApply(textureData -> null);
    }
    
    @Override
    public void registerTexture(final TextureManager textureManager, final ResourceManager resourceManager, final Identifier identifier, final Executor executor) {
        (this.future = CompletableFuture.<TextureData>supplyAsync(() -> TextureData.load(resourceManager, this.location), SystemUtil.getServerWorkerExecutor())).thenRunAsync(() -> textureManager.registerTexture(this.location, this), executor);
    }
}

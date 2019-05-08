package net.minecraft.client.resource;

import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.ResourcePackCompatibility;
import net.minecraft.text.TextComponent;
import java.io.InputStream;
import java.io.IOException;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.resource.ResourcePack;
import java.util.function.Supplier;
import net.minecraft.util.Identifier;
import javax.annotation.Nullable;
import net.minecraft.client.texture.NativeImage;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.ResourcePackContainer;

@Environment(EnvType.CLIENT)
public class ClientResourcePackContainer extends ResourcePackContainer
{
    @Nullable
    private NativeImage icon;
    @Nullable
    private Identifier iconId;
    
    public ClientResourcePackContainer(final String name, final boolean notSorting, final Supplier<ResourcePack> packCreator, final ResourcePack pack, final PackResourceMetadata metadata, final SortingDirection direction) {
        super(name, notSorting, packCreator, pack, metadata, direction);
        NativeImage nativeImage7 = null;
        try (final InputStream inputStream8 = pack.openRoot("pack.png")) {
            nativeImage7 = NativeImage.fromInputStream(inputStream8);
        }
        catch (IOException ex) {}
        catch (IllegalArgumentException ex2) {}
        this.icon = nativeImage7;
    }
    
    public ClientResourcePackContainer(final String name, final boolean boolean2, final Supplier<ResourcePack> supplier, final TextComponent textComponent4, final TextComponent textComponent5, final ResourcePackCompatibility resourcePackCompatibility, final SortingDirection sortingDirection, final boolean boolean8, @Nullable final NativeImage nativeImage) {
        super(name, boolean2, supplier, textComponent4, textComponent5, resourcePackCompatibility, sortingDirection, boolean8);
        this.icon = nativeImage;
    }
    
    public void drawIcon(final TextureManager textureManager) {
        if (this.iconId == null) {
            if (this.icon == null) {
                this.iconId = new Identifier("textures/misc/unknown_pack.png");
            }
            else {
                this.iconId = textureManager.registerDynamicTexture("texturepackicon", new NativeImageBackedTexture(this.icon));
            }
        }
        textureManager.bindTexture(this.iconId);
    }
    
    @Override
    public void close() {
        super.close();
        if (this.icon != null) {
            this.icon.close();
            this.icon = null;
        }
    }
}

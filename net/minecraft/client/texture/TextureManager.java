package net.minecraft.client.texture;

import org.apache.logging.log4j.LogManager;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.MainMenuScreen;
import net.minecraft.util.profiler.Profiler;
import com.mojang.blaze3d.platform.TextureUtil;
import java.util.Iterator;
import net.minecraft.client.MinecraftClient;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import java.io.IOException;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.resource.ResourceManager;
import java.util.List;
import java.util.Map;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Logger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.ResourceReloadListener;

@Environment(EnvType.CLIENT)
public class TextureManager implements TextureTickListener, ResourceReloadListener
{
    private static final Logger LOGGER;
    public static final Identifier a;
    private final Map<Identifier, Texture> textures;
    private final List<TextureTickListener> tickListeners;
    private final Map<String, Integer> dynamicIdCounters;
    private final ResourceManager resourceContainer;
    
    public TextureManager(final ResourceManager resourceManager) {
        this.textures = Maps.newHashMap();
        this.tickListeners = Lists.newArrayList();
        this.dynamicIdCounters = Maps.newHashMap();
        this.resourceContainer = resourceManager;
    }
    
    public void bindTexture(final Identifier identifier) {
        Texture texture2 = this.textures.get(identifier);
        if (texture2 == null) {
            texture2 = new ResourceTexture(identifier);
            this.registerTexture(identifier, texture2);
        }
        texture2.bindTexture();
    }
    
    public boolean registerTextureUpdateable(final Identifier id, final TickableTexture tickableTexture) {
        if (this.registerTexture(id, tickableTexture)) {
            this.tickListeners.add(tickableTexture);
            return true;
        }
        return false;
    }
    
    public boolean registerTexture(final Identifier id, Texture texture) {
        boolean boolean3 = true;
        try {
            texture.load(this.resourceContainer);
        }
        catch (IOException iOException4) {
            if (id != TextureManager.a) {
                TextureManager.LOGGER.warn("Failed to load texture: {}", id, iOException4);
            }
            texture = MissingSprite.getMissingSpriteTexture();
            this.textures.put(id, texture);
            boolean3 = false;
        }
        catch (Throwable throwable4) {
            final CrashReport crashReport5 = CrashReport.create(throwable4, "Registering texture");
            final CrashReportSection crashReportSection6 = crashReport5.addElement("Resource location being registered");
            final Texture texture2 = texture;
            crashReportSection6.add("Resource location", id);
            crashReportSection6.add("Texture object class", () -> texture2.getClass().getName());
            throw new CrashException(crashReport5);
        }
        this.textures.put(id, texture);
        return boolean3;
    }
    
    public Texture getTexture(final Identifier identifier) {
        return this.textures.get(identifier);
    }
    
    public Identifier registerDynamicTexture(final String prefix, final NativeImageBackedTexture nativeImageBackedTexture) {
        Integer integer3 = this.dynamicIdCounters.get(prefix);
        if (integer3 == null) {
            integer3 = 1;
        }
        else {
            ++integer3;
        }
        this.dynamicIdCounters.put(prefix, integer3);
        final Identifier identifier4 = new Identifier(String.format("dynamic/%s_%d", prefix, integer3));
        this.registerTexture(identifier4, nativeImageBackedTexture);
        return identifier4;
    }
    
    public CompletableFuture<Void> loadTextureAsync(final Identifier identifier, final Executor executor) {
        if (!this.textures.containsKey(identifier)) {
            final AsyncTexture asyncTexture3 = new AsyncTexture(this.resourceContainer, identifier, executor);
            this.textures.put(identifier, asyncTexture3);
            return asyncTexture3.getLoadCompleteFuture().thenRunAsync(() -> this.registerTexture(identifier, asyncTexture3), (Executor)MinecraftClient.getInstance());
        }
        return CompletableFuture.<Void>completedFuture((Void)null);
    }
    
    @Override
    public void tick() {
        for (final TextureTickListener textureTickListener2 : this.tickListeners) {
            textureTickListener2.tick();
        }
    }
    
    public void destroyTexture(final Identifier identifier) {
        final Texture texture2 = this.getTexture(identifier);
        if (texture2 != null) {
            TextureUtil.releaseTextureId(texture2.getGlId());
        }
    }
    
    @Override
    public CompletableFuture<Void> a(final Helper helper, final ResourceManager resourceManager, final Profiler prepareProfiler, final Profiler applyProfiler, final Executor prepareExecutor, final Executor applyExecutor) {
        final Iterator<Map.Entry<Identifier, Texture>> iterator4;
        Map.Entry<Identifier, Texture> entry5;
        Identifier identifier6;
        Texture texture7;
        return CompletableFuture.allOf(MainMenuScreen.a(this, prepareExecutor), this.loadTextureAsync(AbstractButtonWidget.WIDGETS_LOCATION, prepareExecutor)).thenCompose(helper::waitForAll).thenAcceptAsync(void3 -> {
            MissingSprite.getMissingSpriteTexture();
            iterator4 = this.textures.entrySet().iterator();
            while (iterator4.hasNext()) {
                entry5 = iterator4.next();
                identifier6 = entry5.getKey();
                texture7 = entry5.getValue();
                if (texture7 == MissingSprite.getMissingSpriteTexture() && !identifier6.equals(MissingSprite.getMissingSpriteId())) {
                    iterator4.remove();
                }
                else {
                    texture7.registerTexture(this, resourceManager, identifier6, applyExecutor);
                }
            }
        }, applyExecutor);
    }
    
    static {
        LOGGER = LogManager.getLogger();
        a = new Identifier("");
    }
}

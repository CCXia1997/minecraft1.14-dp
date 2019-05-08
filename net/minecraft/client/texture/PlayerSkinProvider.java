package net.minecraft.client.texture;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.HashMap;
import com.google.common.collect.Multimap;
import com.mojang.authlib.minecraft.InsecureTextureException;
import net.minecraft.client.util.DefaultSkinHelper;
import com.google.common.hash.Hashing;
import javax.annotation.Nullable;
import net.minecraft.util.Identifier;
import com.google.common.collect.Maps;
import net.minecraft.client.MinecraftClient;
import com.google.common.cache.CacheLoader;
import java.util.concurrent.TimeUnit;
import com.google.common.cache.CacheBuilder;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import java.util.Map;
import com.mojang.authlib.GameProfile;
import com.google.common.cache.LoadingCache;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import java.io.File;
import java.util.concurrent.ExecutorService;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class PlayerSkinProvider
{
    private static final ExecutorService EXECUTOR_SERVICE;
    private final TextureManager textureManager;
    private final File skinCacheDir;
    private final MinecraftSessionService sessionService;
    private final LoadingCache<GameProfile, Map<MinecraftProfileTexture.Type, MinecraftProfileTexture>> skinCache;
    
    public PlayerSkinProvider(final TextureManager textureManager, final File file, final MinecraftSessionService minecraftSessionService) {
        this.textureManager = textureManager;
        this.skinCacheDir = file;
        this.sessionService = minecraftSessionService;
        this.skinCache = CacheBuilder.newBuilder().expireAfterAccess(15L, TimeUnit.SECONDS).<GameProfile, Map<MinecraftProfileTexture.Type, MinecraftProfileTexture>>build(new CacheLoader<GameProfile, Map<MinecraftProfileTexture.Type, MinecraftProfileTexture>>() {
            public Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> a(final GameProfile gameProfile) throws Exception {
                try {
                    return (Map<MinecraftProfileTexture.Type, MinecraftProfileTexture>)MinecraftClient.getInstance().getSessionService().getTextures(gameProfile, false);
                }
                catch (Throwable throwable2) {
                    return Maps.newHashMap();
                }
            }
        });
    }
    
    public Identifier loadSkin(final MinecraftProfileTexture profileTexture, final MinecraftProfileTexture.Type type) {
        return this.loadSkin(profileTexture, type, null);
    }
    
    public Identifier loadSkin(final MinecraftProfileTexture profileTexture, final MinecraftProfileTexture.Type type, @Nullable final SkinTextureAvailableCallback callback) {
        final String string4 = Hashing.sha1().hashUnencodedChars(profileTexture.getHash()).toString();
        final Identifier identifier5 = new Identifier("skins/" + string4);
        final Texture texture6 = this.textureManager.getTexture(identifier5);
        if (texture6 != null) {
            if (callback != null) {
                callback.onSkinTextureAvailable(type, identifier5, profileTexture);
            }
        }
        else {
            final File file7 = new File(this.skinCacheDir, (string4.length() > 2) ? string4.substring(0, 2) : "xx");
            final File file8 = new File(file7, string4);
            final ImageFilter imageFilter9 = (type == MinecraftProfileTexture.Type.SKIN) ? new SkinRemappingImageFilter() : null;
            final PlayerSkinTexture playerSkinTexture10 = new PlayerSkinTexture(file8, profileTexture.getUrl(), DefaultSkinHelper.getTexture(), new ImageFilter() {
                @Override
                public NativeImage filterImage(final NativeImage nativeImage) {
                    if (imageFilter9 != null) {
                        return imageFilter9.filterImage(nativeImage);
                    }
                    return nativeImage;
                }
                
                @Override
                public void a() {
                    if (imageFilter9 != null) {
                        imageFilter9.a();
                    }
                    if (callback != null) {
                        callback.onSkinTextureAvailable(type, identifier5, profileTexture);
                    }
                }
            });
            this.textureManager.registerTexture(identifier5, playerSkinTexture10);
        }
        return identifier5;
    }
    
    public void loadSkin(final GameProfile profile, final SkinTextureAvailableCallback callback, final boolean requireSecure) {
        final HashMap<Object, Object> map4;
        final Map<K, MinecraftProfileTexture> map5;
        PlayerSkinProvider.EXECUTOR_SERVICE.submit(() -> {
            map4 = Maps.newHashMap();
            try {
                map4.putAll(this.sessionService.getTextures(profile, requireSecure));
            }
            catch (InsecureTextureException ex) {}
            if (map4.isEmpty()) {
                profile.getProperties().clear();
                if (profile.getId().equals(MinecraftClient.getInstance().getSession().getProfile().getId())) {
                    profile.getProperties().putAll((Multimap)MinecraftClient.getInstance().getSessionProperties());
                    map4.putAll(this.sessionService.getTextures(profile, false));
                }
                else {
                    this.sessionService.fillProfileProperties(profile, requireSecure);
                    try {
                        map4.putAll(this.sessionService.getTextures(profile, requireSecure));
                    }
                    catch (InsecureTextureException ex2) {}
                }
            }
            MinecraftClient.getInstance().execute(() -> {
                if (map5.containsKey(MinecraftProfileTexture.Type.SKIN)) {
                    this.loadSkin(map5.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN, callback);
                }
                if (map5.containsKey(MinecraftProfileTexture.Type.CAPE)) {
                    this.loadSkin(map5.get(MinecraftProfileTexture.Type.CAPE), MinecraftProfileTexture.Type.CAPE, callback);
                }
            });
        });
    }
    
    public Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> getTextures(final GameProfile gameProfile) {
        return this.skinCache.getUnchecked(gameProfile);
    }
    
    static {
        EXECUTOR_SERVICE = new ThreadPoolExecutor(0, 2, 1L, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>());
    }
    
    @Environment(EnvType.CLIENT)
    public interface SkinTextureAvailableCallback
    {
        void onSkinTextureAvailable(final MinecraftProfileTexture.Type arg1, final Identifier arg2, final MinecraftProfileTexture arg3);
    }
}

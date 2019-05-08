package net.minecraft.client.texture;

import org.apache.logging.log4j.LogManager;
import net.minecraft.util.UncaughtExceptionLogger;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.FileUtils;
import net.minecraft.client.MinecraftClient;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import net.minecraft.resource.ResourceManager;
import com.mojang.blaze3d.platform.TextureUtil;
import net.minecraft.util.Identifier;
import javax.annotation.Nullable;
import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.logging.log4j.Logger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class PlayerSkinTexture extends ResourceTexture
{
    private static final Logger LOGGER;
    private static final AtomicInteger DOWNLOAD_THREAD_COUNTER;
    @Nullable
    private final File cacheFile;
    private final String url;
    @Nullable
    private final ImageFilter filter;
    @Nullable
    private Thread downloadThread;
    private volatile boolean m;
    
    public PlayerSkinTexture(@Nullable final File cacheFile, final String url, final Identifier fallbackSkin, @Nullable final ImageFilter imageFilter) {
        super(fallbackSkin);
        this.cacheFile = cacheFile;
        this.url = url;
        this.filter = imageFilter;
    }
    
    private void b(final NativeImage nativeImage) {
        TextureUtil.prepareImage(this.getGlId(), nativeImage.getWidth(), nativeImage.getHeight());
        nativeImage.upload(0, 0, 0, false);
    }
    
    public void a(final NativeImage nativeImage) {
        if (this.filter != null) {
            this.filter.a();
        }
        synchronized (this) {
            this.b(nativeImage);
            this.m = true;
        }
    }
    
    @Override
    public void load(final ResourceManager resourceManager) throws IOException {
        if (!this.m) {
            synchronized (this) {
                super.load(resourceManager);
                this.m = true;
            }
        }
        if (this.downloadThread == null) {
            if (this.cacheFile != null && this.cacheFile.isFile()) {
                PlayerSkinTexture.LOGGER.debug("Loading http texture from local cache ({})", this.cacheFile);
                NativeImage nativeImage2 = null;
                try {
                    nativeImage2 = NativeImage.fromInputStream(new FileInputStream(this.cacheFile));
                    if (this.filter != null) {
                        nativeImage2 = this.filter.filterImage(nativeImage2);
                    }
                    this.a(nativeImage2);
                }
                catch (IOException iOException3) {
                    PlayerSkinTexture.LOGGER.error("Couldn't load skin {}", this.cacheFile, iOException3);
                    this.startTextureDownload();
                }
                finally {
                    if (nativeImage2 != null) {
                        nativeImage2.close();
                    }
                }
            }
            else {
                this.startTextureDownload();
            }
        }
    }
    
    protected void startTextureDownload() {
        (this.downloadThread = new Thread("Texture Downloader #" + PlayerSkinTexture.DOWNLOAD_THREAD_COUNTER.incrementAndGet()) {
            @Override
            public void run() {
                HttpURLConnection httpURLConnection1 = null;
                PlayerSkinTexture.LOGGER.debug("Downloading http texture from {} to {}", PlayerSkinTexture.this.url, PlayerSkinTexture.this.cacheFile);
                try {
                    httpURLConnection1 = (HttpURLConnection)new URL(PlayerSkinTexture.this.url).openConnection(MinecraftClient.getInstance().getNetworkProxy());
                    httpURLConnection1.setDoInput(true);
                    httpURLConnection1.setDoOutput(false);
                    httpURLConnection1.connect();
                    if (httpURLConnection1.getResponseCode() / 100 != 2) {
                        return;
                    }
                    if (PlayerSkinTexture.this.cacheFile != null) {
                        FileUtils.copyInputStreamToFile(httpURLConnection1.getInputStream(), PlayerSkinTexture.this.cacheFile);
                        final InputStream inputStream2 = new FileInputStream(PlayerSkinTexture.this.cacheFile);
                    }
                    else {
                        final InputStream inputStream2 = httpURLConnection1.getInputStream();
                    }
                    NativeImage nativeImage2;
                    final InputStream inputStream3;
                    MinecraftClient.getInstance().execute(() -> {
                        nativeImage2 = null;
                        try {
                            nativeImage2 = NativeImage.fromInputStream(inputStream3);
                            if (PlayerSkinTexture.this.filter != null) {
                                nativeImage2 = PlayerSkinTexture.this.filter.filterImage(nativeImage2);
                            }
                            PlayerSkinTexture.this.a(nativeImage2);
                        }
                        catch (IOException iOException3) {
                            PlayerSkinTexture.LOGGER.warn("Error while loading the skin texture", (Throwable)iOException3);
                        }
                        finally {
                            if (nativeImage2 != null) {
                                nativeImage2.close();
                            }
                            IOUtils.closeQuietly(inputStream3);
                        }
                    });
                }
                catch (Exception exception2) {
                    PlayerSkinTexture.LOGGER.error("Couldn't download http texture", (Throwable)exception2);
                }
                finally {
                    if (httpURLConnection1 != null) {
                        httpURLConnection1.disconnect();
                    }
                }
            }
        }).setDaemon(true);
        this.downloadThread.setUncaughtExceptionHandler(new UncaughtExceptionLogger(PlayerSkinTexture.LOGGER));
        this.downloadThread.start();
    }
    
    static {
        LOGGER = LogManager.getLogger();
        DOWNLOAD_THREAD_COUNTER = new AtomicInteger(0);
    }
}

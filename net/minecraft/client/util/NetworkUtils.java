package net.minecraft.client.util;

import com.google.common.util.concurrent.MoreExecutors;
import java.util.concurrent.Executors;
import net.minecraft.util.UncaughtExceptionLogger;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.logging.log4j.LogManager;
import java.net.ServerSocket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.util.Iterator;
import java.io.InputStream;
import java.util.concurrent.Executor;
import org.apache.commons.io.IOUtils;
import java.io.IOException;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.DataOutputStream;
import org.apache.commons.io.FileUtils;
import java.util.Locale;
import java.net.HttpURLConnection;
import java.net.URL;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import java.util.concurrent.CompletableFuture;
import java.net.Proxy;
import javax.annotation.Nullable;
import net.minecraft.util.ProgressListener;
import java.util.Map;
import java.io.File;
import com.google.common.util.concurrent.ListeningExecutorService;
import org.apache.logging.log4j.Logger;

public class NetworkUtils
{
    private static final Logger LOGGER;
    public static final ListeningExecutorService downloadExecutor;
    
    @Environment(EnvType.CLIENT)
    public static CompletableFuture<?> download(final File file, final String string, final Map<String, String> map, final int integer, @Nullable final ProgressListener progressListener, final Proxy proxy) {
        HttpURLConnection httpURLConnection7;
        InputStream inputStream8;
        OutputStream outputStream9;
        byte[] arr10;
        URL uRL11;
        float float12;
        float float13;
        final Iterator<Map.Entry<String, String>> iterator;
        Map.Entry<String, String> entry15;
        final float n;
        float float14;
        int integer2;
        final TranslatableTextComponent arg1;
        long long15;
        final DataOutputStream dataOutputStream;
        final IOException ex;
        int integer3;
        final IOException ex2;
        InputStream inputStream9;
        return CompletableFuture.supplyAsync(() -> {
            httpURLConnection7 = null;
            inputStream8 = null;
            outputStream9 = null;
            if (progressListener != null) {
                progressListener.b(new TranslatableTextComponent("resourcepack.downloading", new Object[0]));
                progressListener.c(new TranslatableTextComponent("resourcepack.requesting", new Object[0]));
            }
            try {
                arr10 = new byte[4096];
                uRL11 = new URL(string);
                httpURLConnection7 = (HttpURLConnection)uRL11.openConnection(proxy);
                httpURLConnection7.setInstanceFollowRedirects(true);
                float12 = 0.0f;
                float13 = (float)map.entrySet().size();
                map.entrySet().iterator();
                while (iterator.hasNext()) {
                    entry15 = iterator.next();
                    httpURLConnection7.setRequestProperty(entry15.getKey(), entry15.getValue());
                    if (progressListener != null) {
                        ++float12;
                        progressListener.progressStagePercentage((int)(n / float13 * 100.0f));
                    }
                }
                inputStream8 = httpURLConnection7.getInputStream();
                float14 = (float)httpURLConnection7.getContentLength();
                integer2 = httpURLConnection7.getContentLength();
                if (progressListener != null) {
                    new TranslatableTextComponent("resourcepack.progress", new Object[] { String.format(Locale.ROOT, "%.2f", float14 / 1000.0f / 1000.0f) });
                    progressListener.c(arg1);
                }
                if (file.exists()) {
                    long15 = file.length();
                    if (long15 == integer2) {
                        if (progressListener != null) {
                            progressListener.setDone();
                        }
                        return null;
                    }
                    else {
                        NetworkUtils.LOGGER.warn("Deleting {} as it does not match what we currently have ({} vs our {}).", file, integer2, long15);
                        FileUtils.deleteQuietly(file);
                    }
                }
                else if (file.getParentFile() != null) {
                    file.getParentFile().mkdirs();
                }
                new DataOutputStream(new FileOutputStream(file));
                outputStream9 = dataOutputStream;
                if (integer > 0 && float14 > integer) {
                    if (progressListener != null) {
                        progressListener.setDone();
                    }
                    new IOException("Filesize is bigger than maximum allowed (file is " + float12 + ", limit is " + integer + ")");
                    throw ex;
                }
                else {
                    while ((integer3 = inputStream8.read(arr10)) >= 0) {
                        float12 += integer3;
                        if (progressListener != null) {
                            progressListener.progressStagePercentage((int)(float12 / float14 * 100.0f));
                        }
                        if (integer > 0 && float12 > integer) {
                            if (progressListener != null) {
                                progressListener.setDone();
                            }
                            new IOException("Filesize was bigger than maximum allowed (got >= " + float12 + ", limit was " + integer + ")");
                            throw ex2;
                        }
                        else if (Thread.interrupted()) {
                            NetworkUtils.LOGGER.error("INTERRUPTED");
                            if (progressListener != null) {
                                progressListener.setDone();
                            }
                            return null;
                        }
                        else {
                            outputStream9.write(arr10, 0, integer3);
                        }
                    }
                    if (progressListener != null) {
                        progressListener.setDone();
                    }
                }
            }
            catch (Throwable throwable10) {
                throwable10.printStackTrace();
                if (httpURLConnection7 != null) {
                    inputStream9 = httpURLConnection7.getErrorStream();
                    try {
                        NetworkUtils.LOGGER.error(IOUtils.toString(inputStream9));
                    }
                    catch (IOException iOException12) {
                        iOException12.printStackTrace();
                    }
                }
                if (progressListener != null) {
                    progressListener.setDone();
                }
            }
            finally {
                IOUtils.closeQuietly(inputStream8);
                IOUtils.closeQuietly(outputStream9);
            }
            return null;
        }, NetworkUtils.downloadExecutor);
    }
    
    public static int findLocalPort() {
        try (final ServerSocket serverSocket1 = new ServerSocket(0)) {
            return serverSocket1.getLocalPort();
        }
        catch (IOException iOException1) {
            return 25564;
        }
    }
    
    static {
        LOGGER = LogManager.getLogger();
        downloadExecutor = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool(new ThreadFactoryBuilder().setDaemon(true).setUncaughtExceptionHandler(new UncaughtExceptionLogger(NetworkUtils.LOGGER)).setNameFormat("Downloader %d").build()));
    }
}

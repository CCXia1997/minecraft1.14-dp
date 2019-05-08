package net.minecraft.util;

import java.net.URISyntaxException;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.io.IOException;
import java.security.PrivilegedActionException;
import org.apache.commons.io.IOUtils;
import java.security.AccessController;
import java.net.URL;
import org.apache.logging.log4j.LogManager;
import java.util.UUID;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.DataFixUtils;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import com.google.common.collect.Lists;
import java.util.List;
import it.unimi.dsi.fastutil.Hash;
import java.util.function.Consumer;
import java.util.function.Supplier;
import com.google.common.collect.Iterators;
import java.util.Iterator;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ManagementFactory;
import java.util.stream.Stream;
import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executor;
import net.minecraft.Bootstrap;
import net.minecraft.util.crash.CrashException;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import com.google.common.util.concurrent.MoreExecutors;
import net.minecraft.util.math.MathHelper;
import java.time.Instant;
import javax.annotation.Nullable;
import net.minecraft.state.property.Property;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.stream.Collector;
import org.apache.logging.log4j.Logger;
import java.util.function.LongSupplier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

public class SystemUtil
{
    private static final AtomicInteger b;
    private static final ExecutorService SERVER_WORKER_EXECUTOR;
    public static LongSupplier nanoTimeSupplier;
    private static final Logger LOGGER;
    
    public static <K, V> Collector<Map.Entry<? extends K, ? extends V>, ?, Map<K, V>> toMap() {
        return Collectors.<Map.Entry<? extends K, ? extends V>, K, V>toMap(Map.Entry::getKey, Map.Entry::getValue);
    }
    
    public static <T extends Comparable<T>> String getValueAsString(final Property<T> property, final Object object) {
        return property.getValueAsString((T)object);
    }
    
    public static String createTranslationKey(final String type, @Nullable final Identifier id) {
        if (id == null) {
            return type + ".unregistered_sadface";
        }
        return type + '.' + id.getNamespace() + '.' + id.getPath().replace('/', '.');
    }
    
    public static long getMeasuringTimeMs() {
        return getMeasuringTimeNano() / 1000000L;
    }
    
    public static long getMeasuringTimeNano() {
        return SystemUtil.nanoTimeSupplier.getAsLong();
    }
    
    public static long getEpochTimeMs() {
        return Instant.now().toEpochMilli();
    }
    
    private static ExecutorService createServerWorkerExecutor() {
        final int integer1 = MathHelper.clamp(Runtime.getRuntime().availableProcessors() - 1, 1, 7);
        ExecutorService executorService2;
        if (integer1 <= 0) {
            executorService2 = MoreExecutors.newDirectExecutorService();
        }
        else {
            final ForkJoinWorkerThread forkJoinWorkerThread2;
            executorService2 = new ForkJoinPool(integer1, forkJoinPool -> {
                forkJoinWorkerThread2 = new ForkJoinWorkerThread(forkJoinPool) {};
                forkJoinWorkerThread2.setName("Server-Worker-" + SystemUtil.b.getAndIncrement());
                return forkJoinWorkerThread2;
            }, (thread, throwable) -> {
                if (throwable instanceof CompletionException) {
                    throwable = (CrashException)throwable.getCause();
                }
                if (throwable instanceof CrashException) {
                    Bootstrap.println(throwable.getReport().asString());
                    System.exit(-1);
                }
                SystemUtil.LOGGER.error(String.format("Caught exception in thread %s", thread), (Throwable)throwable);
                return;
            }, true);
        }
        return executorService2;
    }
    
    public static Executor getServerWorkerExecutor() {
        return SystemUtil.SERVER_WORKER_EXECUTOR;
    }
    
    public static void shutdownServerWorkerExecutor() {
        SystemUtil.SERVER_WORKER_EXECUTOR.shutdown();
        boolean boolean1;
        try {
            boolean1 = SystemUtil.SERVER_WORKER_EXECUTOR.awaitTermination(3L, TimeUnit.SECONDS);
        }
        catch (InterruptedException interruptedException2) {
            boolean1 = false;
        }
        if (!boolean1) {
            SystemUtil.SERVER_WORKER_EXECUTOR.shutdownNow();
        }
    }
    
    @Environment(EnvType.CLIENT)
    public static <T> CompletableFuture<T> completeExceptionally(final Throwable throwable) {
        final CompletableFuture<T> completableFuture2 = new CompletableFuture<T>();
        completableFuture2.completeExceptionally(throwable);
        return completableFuture2;
    }
    
    public static OperatingSystem getOperatingSystem() {
        final String string1 = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        if (string1.contains("win")) {
            return OperatingSystem.WINDOWS;
        }
        if (string1.contains("mac")) {
            return OperatingSystem.MAC;
        }
        if (string1.contains("solaris")) {
            return OperatingSystem.SOLARIS;
        }
        if (string1.contains("sunos")) {
            return OperatingSystem.SOLARIS;
        }
        if (string1.contains("linux")) {
            return OperatingSystem.UNIX;
        }
        if (string1.contains("unix")) {
            return OperatingSystem.UNIX;
        }
        return OperatingSystem.UNKNOWN;
    }
    
    public static Stream<String> getJVMFlags() {
        final RuntimeMXBean runtimeMXBean1 = ManagementFactory.getRuntimeMXBean();
        return runtimeMXBean1.getInputArguments().stream().filter(string -> string.startsWith("-X"));
    }
    
    public static <T> T next(final Iterable<T> iterable, @Nullable final T object) {
        final Iterator<T> iterator3 = iterable.iterator();
        final T object2 = iterator3.next();
        if (object != null) {
            for (T object3 = object2; object3 != object; object3 = iterator3.next()) {
                if (iterator3.hasNext()) {}
            }
            if (iterator3.hasNext()) {
                return iterator3.next();
            }
        }
        return object2;
    }
    
    public static <T> T previous(final Iterable<T> iterable, @Nullable final T object) {
        final Iterator<T> iterator3 = iterable.iterator();
        T object2 = null;
        while (iterator3.hasNext()) {
            final T object3 = iterator3.next();
            if (object3 == object) {
                if (object2 == null) {
                    object2 = (iterator3.hasNext() ? Iterators.<T>getLast(iterator3) : object);
                    break;
                }
                break;
            }
            else {
                object2 = object3;
            }
        }
        return object2;
    }
    
    public static <T> T get(final Supplier<T> supplier) {
        return supplier.get();
    }
    
    public static <T> T consume(final T object, final Consumer<T> consumer) {
        consumer.accept(object);
        return object;
    }
    
    public static <K> Hash.Strategy<K> identityHashStrategy() {
        return (Hash.Strategy<K>)IdentityHashStrategy.INSTANCE;
    }
    
    public static <V> CompletableFuture<List<V>> thenCombine(final List<? extends CompletableFuture<? extends V>> futures) {
        final List<V> list2 = Lists.newArrayListWithCapacity(futures.size());
        final CompletableFuture<?>[] arr3 = new CompletableFuture[futures.size()];
        final CompletableFuture<Void> completableFuture5 = new CompletableFuture<Void>();
        final List<Object> list3;
        final int integer5;
        final Object o;
        final CompletableFuture completableFuture6;
        final List<Object> list4;
        final int n;
        futures.forEach(completableFuture4 -> {
            integer5 = list3.size();
            list3.add(null);
            o[integer5] = completableFuture4.whenComplete((object, throwable) -> {
                if (throwable != null) {
                    completableFuture6.completeExceptionally(throwable);
                }
                else {
                    list4.set(n, object);
                }
            });
            return;
        });
        return CompletableFuture.allOf(arr3).<List<V>>applyToEither(completableFuture5, void2 -> list2);
    }
    
    public static <T> Stream<T> stream(final Optional<? extends T> optional) {
        return (Stream<T>)DataFixUtils.orElseGet((Optional)optional.map(Stream::of), (Supplier)Stream::empty);
    }
    
    public static <T> Optional<T> ifPresentOrElse(final Optional<T> optional, final Consumer<T> consumer, final Runnable runnable) {
        if (optional.isPresent()) {
            consumer.accept(optional.get());
        }
        else {
            runnable.run();
        }
        return optional;
    }
    
    public static Runnable debugRunnable(final Runnable runnable, final Supplier<String> messageSupplier) {
        return runnable;
    }
    
    public static Optional<UUID> readUuid(final String name, final Dynamic<?> dynamic) {
        return dynamic.get(name + "Most").asNumber().<UUID>flatMap(number -> dynamic.get(name + "Least").asNumber().map(number2 -> new UUID(number.longValue(), number2.longValue())));
    }
    
    public static <T> Dynamic<T> writeUuid(final String name, final UUID uuid, final Dynamic<T> dynamic) {
        return (Dynamic<T>)dynamic.set(name + "Most", dynamic.createLong(uuid.getMostSignificantBits())).set(name + "Least", dynamic.createLong(uuid.getLeastSignificantBits()));
    }
    
    @Environment(EnvType.CLIENT)
    static /* synthetic */ Logger getLogger() {
        return SystemUtil.LOGGER;
    }
    
    static {
        b = new AtomicInteger(1);
        SERVER_WORKER_EXECUTOR = createServerWorkerExecutor();
        SystemUtil.nanoTimeSupplier = System::nanoTime;
        LOGGER = LogManager.getLogger();
    }
    
    public enum OperatingSystem
    {
        UNIX, 
        SOLARIS, 
        WINDOWS {
            @Environment(EnvType.CLIENT)
            @Override
            protected String[] getURLOpenCommand(final URL uRL) {
                return new String[] { "rundll32", "url.dll,FileProtocolHandler", uRL.toString() };
            }
        }, 
        MAC {
            @Environment(EnvType.CLIENT)
            @Override
            protected String[] getURLOpenCommand(final URL uRL) {
                return new String[] { "open", uRL.toString() };
            }
        }, 
        UNKNOWN;
        
        @Environment(EnvType.CLIENT)
        public void open(final URL uRL) {
            try {
                final Process process2 = AccessController.<Process>doPrivileged(() -> Runtime.getRuntime().exec(this.getURLOpenCommand(uRL)));
                for (final String string4 : IOUtils.readLines(process2.getErrorStream())) {
                    SystemUtil.getLogger().error(string4);
                }
                process2.getInputStream().close();
                process2.getErrorStream().close();
                process2.getOutputStream().close();
            }
            catch (PrivilegedActionException | IOException ex2) {
                final Exception ex;
                final Exception exception2 = ex;
                SystemUtil.getLogger().error("Couldn't open url '{}'", uRL, exception2);
            }
        }
        
        @Environment(EnvType.CLIENT)
        public void open(final URI uRI) {
            try {
                this.open(uRI.toURL());
            }
            catch (MalformedURLException malformedURLException2) {
                SystemUtil.getLogger().error("Couldn't open uri '{}'", uRI, malformedURLException2);
            }
        }
        
        @Environment(EnvType.CLIENT)
        public void open(final File file) {
            try {
                this.open(file.toURI().toURL());
            }
            catch (MalformedURLException malformedURLException2) {
                SystemUtil.getLogger().error("Couldn't open file '{}'", file, malformedURLException2);
            }
        }
        
        @Environment(EnvType.CLIENT)
        protected String[] getURLOpenCommand(final URL uRL) {
            String string2 = uRL.toString();
            if ("file".equals(uRL.getProtocol())) {
                string2 = string2.replace("file:", "file://");
            }
            return new String[] { "xdg-open", string2 };
        }
        
        @Environment(EnvType.CLIENT)
        public void open(final String string) {
            try {
                this.open(new URI(string).toURL());
            }
            catch (URISyntaxException | MalformedURLException | IllegalArgumentException ex2) {
                final Exception ex;
                final Exception exception2 = ex;
                SystemUtil.getLogger().error("Couldn't open uri '{}'", string, exception2);
            }
        }
    }
    
    enum IdentityHashStrategy implements Hash.Strategy<Object>
    {
        INSTANCE;
        
        public int hashCode(final Object object) {
            return System.identityHashCode(object);
        }
        
        public boolean equals(final Object object1, final Object object2) {
            return object1 == object2;
        }
    }
}

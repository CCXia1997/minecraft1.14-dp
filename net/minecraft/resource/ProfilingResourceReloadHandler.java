package net.minecraft.resource;

import org.apache.logging.log4j.LogManager;
import net.minecraft.util.profiler.ProfileResult;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.ProfilerSystem;
import net.minecraft.util.SystemUtil;
import java.util.concurrent.atomic.AtomicLong;
import net.minecraft.util.Void;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.List;
import com.google.common.base.Stopwatch;
import org.apache.logging.log4j.Logger;

public class ProfilingResourceReloadHandler extends ResourceReloadHandler<ProfilingInformation>
{
    private static final Logger LOGGER;
    private final Stopwatch reloadTimer;
    
    public ProfilingResourceReloadHandler(final ResourceManager resourceManager, final List<ResourceReloadListener> list, final Executor executor3, final Executor executor4, final CompletableFuture<Void> completableFuture) {
        final AtomicLong atomicLong7;
        final AtomicLong atomicLong8;
        final ProfilerSystem profilerSystem9;
        final ProfilerSystem profilerSystem10;
        final long long3;
        final AtomicLong atomicLong9;
        final long long4;
        final AtomicLong atomicLong10;
        final CompletableFuture<java.lang.Void> completableFuture2;
        super(executor3, executor4, resourceManager, list, (helper, resourceManager, resourceReloadListener, executor5, executor6) -> {
            atomicLong7 = new AtomicLong();
            atomicLong8 = new AtomicLong();
            profilerSystem9 = new ProfilerSystem(SystemUtil.getMeasuringTimeNano(), () -> 0);
            profilerSystem10 = new ProfilerSystem(SystemUtil.getMeasuringTimeNano(), () -> 0);
            completableFuture2 = resourceReloadListener.a(helper, resourceManager, profilerSystem9, profilerSystem10, runnable -> executor5.execute(() -> {
                long3 = SystemUtil.getMeasuringTimeNano();
                runnable.run();
                atomicLong9.addAndGet(SystemUtil.getMeasuringTimeNano() - long3);
            }), runnable -> executor6.execute(() -> {
                long4 = SystemUtil.getMeasuringTimeNano();
                runnable.run();
                atomicLong10.addAndGet(SystemUtil.getMeasuringTimeNano() - long4);
            }));
            return completableFuture2.thenApplyAsync(void6 -> new ProfilingInformation(resourceReloadListener.getClass().getSimpleName(), profilerSystem9.getResults(), profilerSystem10.getResults(), atomicLong7, atomicLong8), executor4);
        }, completableFuture);
        (this.reloadTimer = Stopwatch.createUnstarted()).start();
        this.applyStageFuture.thenAcceptAsync(this::finish, executor4);
    }
    
    private void finish(final List<ProfilingInformation> list) {
        this.reloadTimer.stop();
        int integer2 = 0;
        ProfilingResourceReloadHandler.LOGGER.info("Resource reload finished after " + this.reloadTimer.elapsed(TimeUnit.MILLISECONDS) + " ms");
        for (final ProfilingInformation profilingInformation4 : list) {
            final ProfileResult profileResult5 = profilingInformation4.prepareProfile;
            final ProfileResult profileResult6 = profilingInformation4.applyProfile;
            final int integer3 = (int)(profilingInformation4.prepareTimeMs.get() / 1000000.0);
            final int integer4 = (int)(profilingInformation4.applyTimeMs.get() / 1000000.0);
            final int integer5 = integer3 + integer4;
            final String string10 = profilingInformation4.name;
            ProfilingResourceReloadHandler.LOGGER.info(string10 + " took approximately " + integer5 + " ms (" + integer3 + " ms preparing, " + integer4 + " ms applying)");
            final String string11 = profileResult5.getTimingTreeString();
            if (string11.length() > 0) {
                ProfilingResourceReloadHandler.LOGGER.debug(string10 + " preparations:\n" + string11);
            }
            final String string12 = profileResult6.getTimingTreeString();
            if (string12.length() > 0) {
                ProfilingResourceReloadHandler.LOGGER.debug(string10 + " reload:\n" + string12);
            }
            ProfilingResourceReloadHandler.LOGGER.info("----------");
            integer2 += integer4;
        }
        ProfilingResourceReloadHandler.LOGGER.info("Total blocking time: " + integer2 + " ms");
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
    
    public static class ProfilingInformation
    {
        private final String name;
        private final ProfileResult prepareProfile;
        private final ProfileResult applyProfile;
        private final AtomicLong prepareTimeMs;
        private final AtomicLong applyTimeMs;
        
        private ProfilingInformation(final String string, final ProfileResult profileResult2, final ProfileResult profileResult3, final AtomicLong atomicLong4, final AtomicLong atomicLong5) {
            this.name = string;
            this.prepareProfile = profileResult2;
            this.applyProfile = profileResult3;
            this.prepareTimeMs = atomicLong4;
            this.applyTimeMs = atomicLong5;
        }
    }
}

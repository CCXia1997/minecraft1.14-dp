package net.minecraft.util.profiler;

import org.apache.logging.log4j.LogManager;
import java.time.Duration;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.util.function.Supplier;
import net.minecraft.util.SystemUtil;
import com.google.common.collect.Maps;
import com.google.common.collect.Lists;
import java.util.function.IntSupplier;
import java.util.Map;
import java.util.List;
import org.apache.logging.log4j.Logger;

public class ProfilerSystem implements ReadableProfiler
{
    private static final long TIMEOUT_NANOSECONDS;
    private static final Logger LOGGER;
    private final List<String> nameList;
    private final List<Long> timeList;
    private final Map<String, Long> nameDurationMap;
    private final IntSupplier f;
    private final long g;
    private final int h;
    private String location;
    private boolean tickStarted;
    
    public ProfilerSystem(final long long1, final IntSupplier intSupplier3) {
        this.nameList = Lists.newArrayList();
        this.timeList = Lists.newArrayList();
        this.nameDurationMap = Maps.newHashMap();
        this.location = "";
        this.g = long1;
        this.h = intSupplier3.getAsInt();
        this.f = intSupplier3;
    }
    
    @Override
    public void startTick() {
        if (this.tickStarted) {
            ProfilerSystem.LOGGER.error("Profiler tick already started - missing endTick()?");
            return;
        }
        this.tickStarted = true;
        this.location = "";
        this.nameList.clear();
        this.push("root");
    }
    
    @Override
    public void endTick() {
        if (!this.tickStarted) {
            ProfilerSystem.LOGGER.error("Profiler tick already ended - missing startTick()?");
            return;
        }
        this.pop();
        this.tickStarted = false;
        if (!this.location.isEmpty()) {
            ProfilerSystem.LOGGER.error("Profiler tick ended before path was fully popped (remainder: '{}'). Mismatched push/pop?", this.location);
        }
    }
    
    @Override
    public void push(final String string) {
        if (!this.tickStarted) {
            ProfilerSystem.LOGGER.error("Cannot push '{}' to profiler if profiler tick hasn't started - missing startTick()?", string);
            return;
        }
        if (!this.location.isEmpty()) {
            this.location += ".";
        }
        this.location += string;
        this.nameList.add(this.location);
        this.timeList.add(SystemUtil.getMeasuringTimeNano());
    }
    
    @Override
    public void push(final Supplier<String> supplier) {
        this.push(supplier.get());
    }
    
    @Override
    public void pop() {
        if (!this.tickStarted) {
            ProfilerSystem.LOGGER.error("Cannot pop from profiler if profiler tick hasn't started - missing startTick()?");
            return;
        }
        if (this.timeList.isEmpty()) {
            ProfilerSystem.LOGGER.error("Tried to pop one too many times! Mismatched push() and pop()?");
            return;
        }
        final long long1 = SystemUtil.getMeasuringTimeNano();
        final long long2 = this.timeList.remove(this.timeList.size() - 1);
        this.nameList.remove(this.nameList.size() - 1);
        final long long3 = long1 - long2;
        if (this.nameDurationMap.containsKey(this.location)) {
            this.nameDurationMap.put(this.location, this.nameDurationMap.get(this.location) + long3);
        }
        else {
            this.nameDurationMap.put(this.location, long3);
        }
        if (long3 > ProfilerSystem.TIMEOUT_NANOSECONDS) {
            ProfilerSystem.LOGGER.warn("Something's taking too long! '{}' took aprox {} ms", this.location, (long3 / 1000000.0));
        }
        this.location = (this.nameList.isEmpty() ? "" : this.nameList.get(this.nameList.size() - 1));
    }
    
    @Override
    public void swap(final String string) {
        this.pop();
        this.push(string);
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void swap(final Supplier<String> supplier) {
        this.pop();
        this.push(supplier);
    }
    
    @Override
    public ProfileResult getResults() {
        return new ProfileResultImpl(this.nameDurationMap, this.g, this.h, SystemUtil.getMeasuringTimeNano(), this.f.getAsInt());
    }
    
    static {
        TIMEOUT_NANOSECONDS = Duration.ofMillis(100L).toNanos();
        LOGGER = LogManager.getLogger();
    }
}

package net.minecraft.util.profiler;

import net.minecraft.util.SystemUtil;
import java.time.Duration;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.util.function.Supplier;
import java.util.function.IntSupplier;

public class DisableableProfiler implements Profiler
{
    private static final long a;
    private final IntSupplier tickSupplier;
    private final ProfilerControllerImpl controller;
    private final ProfilerControllerImpl d;
    
    public DisableableProfiler(final IntSupplier tickSupplier) {
        this.controller = new ProfilerControllerImpl();
        this.d = new ProfilerControllerImpl();
        this.tickSupplier = tickSupplier;
    }
    
    public ProfilerController getController() {
        return this.controller;
    }
    
    @Override
    public void startTick() {
        this.controller.profiler.startTick();
        this.d.profiler.startTick();
    }
    
    @Override
    public void endTick() {
        this.controller.profiler.endTick();
        this.d.profiler.endTick();
    }
    
    @Override
    public void push(final String string) {
        this.controller.profiler.push(string);
        this.d.profiler.push(string);
    }
    
    @Override
    public void push(final Supplier<String> supplier) {
        this.controller.profiler.push(supplier);
        this.d.profiler.push(supplier);
    }
    
    @Override
    public void pop() {
        this.controller.profiler.pop();
        this.d.profiler.pop();
    }
    
    @Override
    public void swap(final String string) {
        this.controller.profiler.swap(string);
        this.d.profiler.swap(string);
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void swap(final Supplier<String> supplier) {
        this.controller.profiler.swap(supplier);
        this.d.profiler.swap(supplier);
    }
    
    static {
        a = Duration.ofMillis(300L).toNanos();
    }
    
    class ProfilerControllerImpl implements ProfilerController
    {
        protected ReadableProfiler profiler;
        
        private ProfilerControllerImpl() {
            this.profiler = DummyProfiler.INSTANCE;
        }
        
        @Override
        public boolean isEnabled() {
            return this.profiler != DummyProfiler.INSTANCE;
        }
        
        @Override
        public ProfileResult disable() {
            final ProfileResult profileResult1 = this.profiler.getResults();
            this.profiler = DummyProfiler.INSTANCE;
            return profileResult1;
        }
        
        @Environment(EnvType.CLIENT)
        @Override
        public ProfileResult getResults() {
            return this.profiler.getResults();
        }
        
        @Override
        public void enable() {
            if (this.profiler == DummyProfiler.INSTANCE) {
                this.profiler = new ProfilerSystem(SystemUtil.getMeasuringTimeNano(), DisableableProfiler.this.tickSupplier);
            }
        }
    }
    
    public interface ProfilerController
    {
        boolean isEnabled();
        
        ProfileResult disable();
        
        @Environment(EnvType.CLIENT)
        ProfileResult getResults();
        
        void enable();
    }
}

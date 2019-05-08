package net.minecraft.util.profiler;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.util.function.Supplier;

public class DummyProfiler implements ReadableProfiler
{
    public static final DummyProfiler INSTANCE;
    
    private DummyProfiler() {
    }
    
    @Override
    public void startTick() {
    }
    
    @Override
    public void endTick() {
    }
    
    @Override
    public void push(final String string) {
    }
    
    @Override
    public void push(final Supplier<String> supplier) {
    }
    
    @Override
    public void pop() {
    }
    
    @Override
    public void swap(final String string) {
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void swap(final Supplier<String> supplier) {
    }
    
    @Override
    public ProfileResult getResults() {
        return EmptyProfileResult.INSTANCE;
    }
    
    static {
        INSTANCE = new DummyProfiler();
    }
}

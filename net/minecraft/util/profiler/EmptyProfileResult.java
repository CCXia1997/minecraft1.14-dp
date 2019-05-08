package net.minecraft.util.profiler;

import java.io.File;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.util.Collections;
import java.util.List;

public class EmptyProfileResult implements ProfileResult
{
    public static final EmptyProfileResult INSTANCE;
    
    private EmptyProfileResult() {
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public List<ProfilerTiming> getTimings(final String parentTiming) {
        return Collections.<ProfilerTiming>emptyList();
    }
    
    @Override
    public boolean saveToFile(final File file) {
        return false;
    }
    
    @Override
    public long getStartTime() {
        return 0L;
    }
    
    @Override
    public int getStartTick() {
        return 0;
    }
    
    @Override
    public long getEndTime() {
        return 0L;
    }
    
    @Override
    public int getEndTick() {
        return 0;
    }
    
    @Override
    public String getTimingTreeString() {
        return "";
    }
    
    static {
        INSTANCE = new EmptyProfileResult();
    }
}

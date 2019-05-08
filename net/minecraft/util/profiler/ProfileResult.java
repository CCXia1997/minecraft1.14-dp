package net.minecraft.util.profiler;

import java.io.File;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.util.List;

public interface ProfileResult
{
    @Environment(EnvType.CLIENT)
    List<ProfilerTiming> getTimings(final String arg1);
    
    boolean saveToFile(final File arg1);
    
    long getStartTime();
    
    int getStartTick();
    
    long getEndTime();
    
    int getEndTick();
    
    default long getTimeSpan() {
        return this.getEndTime() - this.getStartTime();
    }
    
    default int getTickSpan() {
        return this.getEndTick() - this.getStartTick();
    }
    
    String getTimingTreeString();
}

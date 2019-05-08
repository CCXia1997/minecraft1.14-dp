package net.minecraft.util.profiler;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public final class ProfilerTiming implements Comparable<ProfilerTiming>
{
    public final double parentSectionUsagePercentage;
    public final double totalUsagePercentage;
    public final String name;
    
    public ProfilerTiming(final String name, final double double2, final double double4) {
        this.name = name;
        this.parentSectionUsagePercentage = double2;
        this.totalUsagePercentage = double4;
    }
    
    public int a(final ProfilerTiming profilerTiming) {
        if (profilerTiming.parentSectionUsagePercentage < this.parentSectionUsagePercentage) {
            return -1;
        }
        if (profilerTiming.parentSectionUsagePercentage > this.parentSectionUsagePercentage) {
            return 1;
        }
        return profilerTiming.name.compareTo(this.name);
    }
    
    @Environment(EnvType.CLIENT)
    public int getColor() {
        return (this.name.hashCode() & 0xAAAAAA) + 4473924;
    }
}

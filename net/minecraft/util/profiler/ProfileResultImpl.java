package net.minecraft.util.profiler;

import org.apache.logging.log4j.LogManager;
import net.minecraft.util.SystemUtil;
import java.util.Locale;
import java.io.Writer;
import org.apache.commons.io.IOUtils;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.io.FileOutputStream;
import java.io.File;
import java.util.Iterator;
import java.util.Collections;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.Logger;

public class ProfileResultImpl implements ProfileResult
{
    private static final Logger LOGGER;
    private final Map<String, Long> timings;
    private final long startTime;
    private final int startTick;
    private final long endTime;
    private final int endTick;
    
    public ProfileResultImpl(final Map<String, Long> timings, final long startTime, final int startTick, final long endTime, final int endTick) {
        this.timings = timings;
        this.startTime = startTime;
        this.startTick = startTick;
        this.endTime = endTime;
        this.endTick = endTick;
    }
    
    @Override
    public List<ProfilerTiming> getTimings(String parentTiming) {
        final String string2 = parentTiming;
        long long3 = this.timings.containsKey("root") ? this.timings.get("root") : 0L;
        final long long4 = this.timings.containsKey(parentTiming) ? this.timings.get(parentTiming) : -1L;
        final List<ProfilerTiming> list7 = Lists.newArrayList();
        if (!parentTiming.isEmpty()) {
            parentTiming += ".";
        }
        long long5 = 0L;
        for (final String string3 : this.timings.keySet()) {
            if (string3.length() > parentTiming.length() && string3.startsWith(parentTiming) && string3.indexOf(".", parentTiming.length() + 1) < 0) {
                long5 += this.timings.get(string3);
            }
        }
        final float float10 = (float)long5;
        if (long5 < long4) {
            long5 = long4;
        }
        if (long3 < long5) {
            long3 = long5;
        }
        for (final String string4 : this.timings.keySet()) {
            if (string4.length() > parentTiming.length() && string4.startsWith(parentTiming) && string4.indexOf(".", parentTiming.length() + 1) < 0) {
                final long long6 = this.timings.get(string4);
                final double double15 = long6 * 100.0 / long5;
                final double double16 = long6 * 100.0 / long3;
                final String string5 = string4.substring(parentTiming.length());
                list7.add(new ProfilerTiming(string5, double15, double16));
            }
        }
        for (final String string4 : this.timings.keySet()) {
            this.timings.put(string4, this.timings.get(string4) * 999L / 1000L);
        }
        if (long5 > float10) {
            list7.add(new ProfilerTiming("unspecified", (long5 - float10) * 100.0 / long5, (long5 - float10) * 100.0 / long3));
        }
        Collections.<ProfilerTiming>sort(list7);
        list7.add(0, new ProfilerTiming(string2, 100.0, long5 * 100.0 / long3));
        return list7;
    }
    
    @Override
    public long getStartTime() {
        return this.startTime;
    }
    
    @Override
    public int getStartTick() {
        return this.startTick;
    }
    
    @Override
    public long getEndTime() {
        return this.endTime;
    }
    
    @Override
    public int getEndTick() {
        return this.endTick;
    }
    
    @Override
    public boolean saveToFile(final File file) {
        file.getParentFile().mkdirs();
        Writer writer2 = null;
        try {
            writer2 = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
            writer2.write(this.asString(this.getTimeSpan(), this.getTickSpan()));
            return true;
        }
        catch (Throwable throwable3) {
            ProfileResultImpl.LOGGER.error("Could not save profiler results to {}", file, throwable3);
            return false;
        }
        finally {
            IOUtils.closeQuietly(writer2);
        }
    }
    
    protected String asString(final long timeSpan, final int tickSpan) {
        final StringBuilder stringBuilder4 = new StringBuilder();
        stringBuilder4.append("---- Minecraft Profiler Results ----\n");
        stringBuilder4.append("// ");
        stringBuilder4.append(generateWittyComment());
        stringBuilder4.append("\n\n");
        stringBuilder4.append("Time span: ").append(timeSpan / 1000000L).append(" ms\n");
        stringBuilder4.append("Tick span: ").append(tickSpan).append(" ticks\n");
        stringBuilder4.append("// This is approximately ").append(String.format(Locale.ROOT, "%.2f", tickSpan / (timeSpan / 1.0E9f))).append(" ticks per second. It should be ").append(20).append(" ticks per second\n\n");
        stringBuilder4.append("--- BEGIN PROFILE DUMP ---\n\n");
        this.appendTiming(0, "root", stringBuilder4);
        stringBuilder4.append("--- END PROFILE DUMP ---\n\n");
        return stringBuilder4.toString();
    }
    
    @Override
    public String getTimingTreeString() {
        final StringBuilder stringBuilder1 = new StringBuilder();
        this.appendTiming(0, "root", stringBuilder1);
        return stringBuilder1.toString();
    }
    
    private void appendTiming(final int level, final String name, final StringBuilder sb) {
        final List<ProfilerTiming> list4 = this.getTimings(name);
        if (list4.size() < 3) {
            return;
        }
        for (int integer5 = 1; integer5 < list4.size(); ++integer5) {
            final ProfilerTiming profilerTiming6 = list4.get(integer5);
            sb.append(String.format("[%02d] ", level));
            for (int integer6 = 0; integer6 < level; ++integer6) {
                sb.append("|   ");
            }
            sb.append(profilerTiming6.name).append(" - ").append(String.format(Locale.ROOT, "%.2f", profilerTiming6.parentSectionUsagePercentage)).append("%/").append(String.format(Locale.ROOT, "%.2f", profilerTiming6.totalUsagePercentage)).append("%\n");
            if (!"unspecified".equals(profilerTiming6.name)) {
                try {
                    this.appendTiming(level + 1, name + "." + profilerTiming6.name, sb);
                }
                catch (Exception exception7) {
                    sb.append("[[ EXCEPTION ").append(exception7).append(" ]]");
                }
            }
        }
    }
    
    private static String generateWittyComment() {
        final String[] arr1 = { "Shiny numbers!", "Am I not running fast enough? :(", "I'm working as hard as I can!", "Will I ever be good enough for you? :(", "Speedy. Zoooooom!", "Hello world", "40% better than a crash report.", "Now with extra numbers", "Now with less numbers", "Now with the same numbers", "You should add flames to things, it makes them go faster!", "Do you feel the need for... optimization?", "*cracks redstone whip*", "Maybe if you treated it better then it'll have more motivation to work faster! Poor server." };
        try {
            return arr1[(int)(SystemUtil.getMeasuringTimeNano() % arr1.length)];
        }
        catch (Throwable throwable2) {
            return "Witty comment unavailable :(";
        }
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}

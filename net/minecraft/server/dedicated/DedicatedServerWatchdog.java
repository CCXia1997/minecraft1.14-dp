package net.minecraft.server.dedicated;

import org.apache.logging.log4j.LogManager;
import java.util.TimerTask;
import java.util.Timer;
import net.minecraft.util.crash.CrashReportSection;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.io.File;
import net.minecraft.util.crash.CrashReport;
import java.lang.management.ManagementFactory;
import java.util.Locale;
import net.minecraft.util.SystemUtil;
import org.apache.logging.log4j.Logger;

public class DedicatedServerWatchdog implements Runnable
{
    private static final Logger LOGGER;
    private final MinecraftDedicatedServer server;
    private final long maxTickTime;
    
    public DedicatedServerWatchdog(final MinecraftDedicatedServer minecraftDedicatedServer) {
        this.server = minecraftDedicatedServer;
        this.maxTickTime = minecraftDedicatedServer.getMaxTickTime();
    }
    
    @Override
    public void run() {
        while (this.server.isRunning()) {
            final long long1 = this.server.getServerStartTime();
            final long long2 = SystemUtil.getMeasuringTimeMs();
            final long long3 = long2 - long1;
            if (long3 > this.maxTickTime) {
                DedicatedServerWatchdog.LOGGER.fatal("A single server tick took {} seconds (should be max {})", String.format(Locale.ROOT, "%.2f", long3 / 1000.0f), String.format(Locale.ROOT, "%.2f", 0.05f));
                DedicatedServerWatchdog.LOGGER.fatal("Considering it to be crashed, server will forcibly shutdown.");
                final ThreadMXBean threadMXBean7 = ManagementFactory.getThreadMXBean();
                final ThreadInfo[] arr8 = threadMXBean7.dumpAllThreads(true, true);
                final StringBuilder stringBuilder9 = new StringBuilder();
                final Error error10 = new Error();
                for (final ThreadInfo threadInfo14 : arr8) {
                    if (threadInfo14.getThreadId() == this.server.getThread().getId()) {
                        error10.setStackTrace(threadInfo14.getStackTrace());
                    }
                    stringBuilder9.append(threadInfo14);
                    stringBuilder9.append("\n");
                }
                final CrashReport crashReport11 = new CrashReport("Watching Server", error10);
                this.server.populateCrashReport(crashReport11);
                final CrashReportSection crashReportSection12 = crashReport11.addElement("Thread Dump");
                crashReportSection12.add("Threads", stringBuilder9);
                final File file13 = new File(new File(this.server.getRunDirectory(), "crash-reports"), "crash-" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + "-server.txt");
                if (crashReport11.writeToFile(file13)) {
                    DedicatedServerWatchdog.LOGGER.error("This crash report has been saved to: {}", file13.getAbsolutePath());
                }
                else {
                    DedicatedServerWatchdog.LOGGER.error("We were unable to save this crash report to disk.");
                }
                this.shutdown();
            }
            try {
                Thread.sleep(long1 + this.maxTickTime - long2);
            }
            catch (InterruptedException ex) {}
        }
    }
    
    private void shutdown() {
        try {
            final Timer timer1 = new Timer();
            timer1.schedule(new TimerTask() {
                @Override
                public void run() {
                    Runtime.getRuntime().halt(1);
                }
            }, 10000L);
            System.exit(1);
        }
        catch (Throwable throwable1) {
            Runtime.getRuntime().halt(1);
        }
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}

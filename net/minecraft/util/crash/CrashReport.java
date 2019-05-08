package net.minecraft.util.crash;

import org.apache.logging.log4j.LogManager;
import java.util.concurrent.CompletionException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.io.FileOutputStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.util.Date;
import java.text.SimpleDateFormat;
import org.apache.commons.io.IOUtils;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import org.apache.commons.lang3.ArrayUtils;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import net.minecraft.util.SystemUtil;
import net.minecraft.SharedConstants;
import com.google.common.collect.Lists;
import java.io.File;
import java.util.List;
import org.apache.logging.log4j.Logger;

public class CrashReport
{
    private static final Logger LOGGER;
    private final String message;
    private final Throwable cause;
    private final CrashReportSection systemDetailsSection;
    private final List<CrashReportSection> otherSections;
    private File file;
    private boolean hasStackTrace;
    private StackTraceElement[] stackTrace;
    
    public CrashReport(final String message, final Throwable throwable) {
        this.systemDetailsSection = new CrashReportSection(this, "System Details");
        this.otherSections = Lists.newArrayList();
        this.hasStackTrace = true;
        this.stackTrace = new StackTraceElement[0];
        this.message = message;
        this.cause = throwable;
        this.fillSystemDetails();
    }
    
    private void fillSystemDetails() {
        this.systemDetailsSection.add("Minecraft Version", () -> SharedConstants.getGameVersion().getName());
        this.systemDetailsSection.add("Operating System", () -> System.getProperty("os.name") + " (" + System.getProperty("os.arch") + ") version " + System.getProperty("os.version"));
        this.systemDetailsSection.add("Java Version", () -> System.getProperty("java.version") + ", " + System.getProperty("java.vendor"));
        this.systemDetailsSection.add("Java VM Version", () -> System.getProperty("java.vm.name") + " (" + System.getProperty("java.vm.info") + "), " + System.getProperty("java.vm.vendor"));
        final Runtime runtime1;
        final long long2;
        final long long3;
        final long long4;
        final long long5;
        final long long6;
        final long long7;
        this.systemDetailsSection.add("Memory", () -> {
            runtime1 = Runtime.getRuntime();
            long2 = runtime1.maxMemory();
            long3 = runtime1.totalMemory();
            long4 = runtime1.freeMemory();
            long5 = long2 / 1024L / 1024L;
            long6 = long3 / 1024L / 1024L;
            long7 = long4 / 1024L / 1024L;
            return long4 + " bytes (" + long7 + " MB) / " + long3 + " bytes (" + long6 + " MB) up to " + long2 + " bytes (" + long5 + " MB)";
        });
        final List<String> list1;
        this.systemDetailsSection.add("JVM Flags", () -> {
            list1 = SystemUtil.getJVMFlags().collect(Collectors.toList());
            return String.format("%d total; %s", list1.size(), list1.stream().collect(Collectors.joining(" ")));
        });
    }
    
    public String getMessage() {
        return this.message;
    }
    
    public Throwable getCause() {
        return this.cause;
    }
    
    public void addStackTrace(final StringBuilder stringBuilder) {
        if ((this.stackTrace == null || this.stackTrace.length <= 0) && !this.otherSections.isEmpty()) {
            this.stackTrace = (StackTraceElement[])ArrayUtils.subarray((Object[])this.otherSections.get(0).getStackTrace(), 0, 1);
        }
        if (this.stackTrace != null && this.stackTrace.length > 0) {
            stringBuilder.append("-- Head --\n");
            stringBuilder.append("Thread: ").append(Thread.currentThread().getName()).append("\n");
            stringBuilder.append("Stacktrace:\n");
            for (final StackTraceElement stackTraceElement5 : this.stackTrace) {
                stringBuilder.append("\t").append("at ").append(stackTraceElement5);
                stringBuilder.append("\n");
            }
            stringBuilder.append("\n");
        }
        for (final CrashReportSection crashReportSection3 : this.otherSections) {
            crashReportSection3.addStackTrace(stringBuilder);
            stringBuilder.append("\n\n");
        }
        this.systemDetailsSection.addStackTrace(stringBuilder);
    }
    
    public String getCauseAsString() {
        StringWriter stringWriter1 = null;
        PrintWriter printWriter2 = null;
        Throwable throwable3 = this.cause;
        if (throwable3.getMessage() == null) {
            if (throwable3 instanceof NullPointerException) {
                throwable3 = new NullPointerException(this.message);
            }
            else if (throwable3 instanceof StackOverflowError) {
                throwable3 = new StackOverflowError(this.message);
            }
            else if (throwable3 instanceof OutOfMemoryError) {
                throwable3 = new OutOfMemoryError(this.message);
            }
            throwable3.setStackTrace(this.cause.getStackTrace());
        }
        try {
            stringWriter1 = new StringWriter();
            printWriter2 = new PrintWriter(stringWriter1);
            throwable3.printStackTrace(printWriter2);
            return stringWriter1.toString();
        }
        finally {
            IOUtils.closeQuietly((Writer)stringWriter1);
            IOUtils.closeQuietly((Writer)printWriter2);
        }
    }
    
    public String asString() {
        final StringBuilder stringBuilder1 = new StringBuilder();
        stringBuilder1.append("---- Minecraft Crash Report ----\n");
        stringBuilder1.append("// ");
        stringBuilder1.append(generateWittyComment());
        stringBuilder1.append("\n\n");
        stringBuilder1.append("Time: ");
        stringBuilder1.append(new SimpleDateFormat().format(new Date()));
        stringBuilder1.append("\n");
        stringBuilder1.append("Description: ");
        stringBuilder1.append(this.message);
        stringBuilder1.append("\n\n");
        stringBuilder1.append(this.getCauseAsString());
        stringBuilder1.append("\n\nA detailed walkthrough of the error, its code path and all known details is as follows:\n");
        for (int integer2 = 0; integer2 < 87; ++integer2) {
            stringBuilder1.append("-");
        }
        stringBuilder1.append("\n\n");
        this.addStackTrace(stringBuilder1);
        return stringBuilder1.toString();
    }
    
    @Environment(EnvType.CLIENT)
    public File getFile() {
        return this.file;
    }
    
    public boolean writeToFile(final File file) {
        if (this.file != null) {
            return false;
        }
        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }
        Writer writer2 = null;
        try {
            writer2 = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
            writer2.write(this.asString());
            this.file = file;
            return true;
        }
        catch (Throwable throwable3) {
            CrashReport.LOGGER.error("Could not save crash report to {}", file, throwable3);
            return false;
        }
        finally {
            IOUtils.closeQuietly(writer2);
        }
    }
    
    public CrashReportSection getSystemDetailsSection() {
        return this.systemDetailsSection;
    }
    
    public CrashReportSection addElement(final String string) {
        return this.addElement(string, 1);
    }
    
    public CrashReportSection addElement(final String name, final int integer) {
        final CrashReportSection crashReportSection3 = new CrashReportSection(this, name);
        if (this.hasStackTrace) {
            final int integer2 = crashReportSection3.trimStackTrace(integer);
            final StackTraceElement[] arr5 = this.cause.getStackTrace();
            StackTraceElement stackTraceElement6 = null;
            StackTraceElement stackTraceElement7 = null;
            final int integer3 = arr5.length - integer2;
            if (integer3 < 0) {
                System.out.println("Negative index in crash report handler (" + arr5.length + "/" + integer2 + ")");
            }
            if (arr5 != null && 0 <= integer3 && integer3 < arr5.length) {
                stackTraceElement6 = arr5[integer3];
                if (arr5.length + 1 - integer2 < arr5.length) {
                    stackTraceElement7 = arr5[arr5.length + 1 - integer2];
                }
            }
            this.hasStackTrace = crashReportSection3.a(stackTraceElement6, stackTraceElement7);
            if (integer2 > 0 && !this.otherSections.isEmpty()) {
                final CrashReportSection crashReportSection4 = this.otherSections.get(this.otherSections.size() - 1);
                crashReportSection4.b(integer2);
            }
            else if (arr5 != null && arr5.length >= integer2 && 0 <= integer3 && integer3 < arr5.length) {
                System.arraycopy(arr5, 0, this.stackTrace = new StackTraceElement[integer3], 0, this.stackTrace.length);
            }
            else {
                this.hasStackTrace = false;
            }
        }
        this.otherSections.add(crashReportSection3);
        return crashReportSection3;
    }
    
    private static String generateWittyComment() {
        final String[] arr1 = { "Who set us up the TNT?", "Everything's going to plan. No, really, that was supposed to happen.", "Uh... Did I do that?", "Oops.", "Why did you do that?", "I feel sad now :(", "My bad.", "I'm sorry, Dave.", "I let you down. Sorry :(", "On the bright side, I bought you a teddy bear!", "Daisy, daisy...", "Oh - I know what I did wrong!", "Hey, that tickles! Hehehe!", "I blame Dinnerbone.", "You should try our sister game, Minceraft!", "Don't be sad. I'll do better next time, I promise!", "Don't be sad, have a hug! <3", "I just don't know what went wrong :(", "Shall we play a game?", "Quite honestly, I wouldn't worry myself about that.", "I bet Cylons wouldn't have this problem.", "Sorry :(", "Surprise! Haha. Well, this is awkward.", "Would you like a cupcake?", "Hi. I'm Minecraft, and I'm a crashaholic.", "Ooh. Shiny.", "This doesn't make any sense!", "Why is it breaking :(", "Don't do that.", "Ouch. That hurt :(", "You're mean.", "This is a token for 1 free hug. Redeem at your nearest Mojangsta: [~~HUG~~]", "There are four lights!", "But it works on my machine." };
        try {
            return arr1[(int)(SystemUtil.getMeasuringTimeNano() % arr1.length)];
        }
        catch (Throwable throwable2) {
            return "Witty comment unavailable :(";
        }
    }
    
    public static CrashReport create(Throwable cause, final String title) {
        while (cause instanceof CompletionException && cause.getCause() != null) {
            cause = cause.getCause();
        }
        CrashReport crashReport3;
        if (cause instanceof CrashException) {
            crashReport3 = ((CrashException)cause).getReport();
        }
        else {
            crashReport3 = new CrashReport(title, cause);
        }
        return crashReport3;
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}

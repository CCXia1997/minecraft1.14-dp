package net.minecraft.util.crash;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import java.util.Iterator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;
import java.util.Locale;
import com.google.common.collect.Lists;
import java.util.List;

public class CrashReportSection
{
    private final CrashReport report;
    private final String title;
    private final List<Element> elements;
    private StackTraceElement[] stackTrace;
    
    public CrashReportSection(final CrashReport report, final String string) {
        this.elements = Lists.newArrayList();
        this.stackTrace = new StackTraceElement[0];
        this.report = report;
        this.title = string;
    }
    
    @Environment(EnvType.CLIENT)
    public static String createPositionString(final double x, final double y, final double z) {
        return String.format(Locale.ROOT, "%.2f,%.2f,%.2f - %s", x, y, z, createPositionString(new BlockPos(x, y, z)));
    }
    
    public static String createPositionString(final BlockPos pos) {
        return createPositionString(pos.getX(), pos.getY(), pos.getZ());
    }
    
    public static String createPositionString(final int x, final int y, final int z) {
        final StringBuilder stringBuilder4 = new StringBuilder();
        try {
            stringBuilder4.append(String.format("World: (%d,%d,%d)", x, y, z));
        }
        catch (Throwable throwable5) {
            stringBuilder4.append("(Error finding world loc)");
        }
        stringBuilder4.append(", ");
        try {
            final int integer5 = x >> 4;
            final int integer6 = z >> 4;
            final int integer7 = x & 0xF;
            final int integer8 = y >> 4;
            final int integer9 = z & 0xF;
            final int integer10 = integer5 << 4;
            final int integer11 = integer6 << 4;
            final int integer12 = (integer5 + 1 << 4) - 1;
            final int integer13 = (integer6 + 1 << 4) - 1;
            stringBuilder4.append(String.format("Chunk: (at %d,%d,%d in %d,%d; contains blocks %d,0,%d to %d,255,%d)", integer7, integer8, integer9, integer5, integer6, integer10, integer11, integer12, integer13));
        }
        catch (Throwable throwable5) {
            stringBuilder4.append("(Error finding chunk loc)");
        }
        stringBuilder4.append(", ");
        try {
            final int integer5 = x >> 9;
            final int integer6 = z >> 9;
            final int integer7 = integer5 << 5;
            final int integer8 = integer6 << 5;
            final int integer9 = (integer5 + 1 << 5) - 1;
            final int integer10 = (integer6 + 1 << 5) - 1;
            final int integer11 = integer5 << 9;
            final int integer12 = integer6 << 9;
            final int integer13 = (integer5 + 1 << 9) - 1;
            final int integer14 = (integer6 + 1 << 9) - 1;
            stringBuilder4.append(String.format("Region: (%d,%d; contains chunks %d,%d to %d,%d, blocks %d,0,%d to %d,255,%d)", integer5, integer6, integer7, integer8, integer9, integer10, integer11, integer12, integer13, integer14));
        }
        catch (Throwable throwable5) {
            stringBuilder4.append("(Error finding world loc)");
        }
        return stringBuilder4.toString();
    }
    
    public void add(final String name, final ICrashCallable<String> iCrashCallable) {
        try {
            this.add(name, iCrashCallable.call());
        }
        catch (Throwable throwable3) {
            this.add(name, throwable3);
        }
    }
    
    public void add(final String name, final Object object) {
        this.elements.add(new Element(name, object));
    }
    
    public void add(final String name, final Throwable throwable) {
        this.add(name, throwable);
    }
    
    public int trimStackTrace(final int integer) {
        final StackTraceElement[] arr2 = Thread.currentThread().getStackTrace();
        if (arr2.length <= 0) {
            return 0;
        }
        System.arraycopy(arr2, 3 + integer, this.stackTrace = new StackTraceElement[arr2.length - 3 - integer], 0, this.stackTrace.length);
        return this.stackTrace.length;
    }
    
    public boolean a(final StackTraceElement stackTraceElement1, final StackTraceElement stackTraceElement2) {
        if (this.stackTrace.length == 0 || stackTraceElement1 == null) {
            return false;
        }
        final StackTraceElement stackTraceElement3 = this.stackTrace[0];
        if (stackTraceElement3.isNativeMethod() != stackTraceElement1.isNativeMethod() || !stackTraceElement3.getClassName().equals(stackTraceElement1.getClassName()) || !stackTraceElement3.getFileName().equals(stackTraceElement1.getFileName()) || !stackTraceElement3.getMethodName().equals(stackTraceElement1.getMethodName())) {
            return false;
        }
        if (stackTraceElement2 != null != this.stackTrace.length > 1) {
            return false;
        }
        if (stackTraceElement2 != null && !this.stackTrace[1].equals(stackTraceElement2)) {
            return false;
        }
        this.stackTrace[0] = stackTraceElement1;
        return true;
    }
    
    public void b(final int integer) {
        final StackTraceElement[] arr2 = new StackTraceElement[this.stackTrace.length - integer];
        System.arraycopy(this.stackTrace, 0, arr2, 0, arr2.length);
        this.stackTrace = arr2;
    }
    
    public void addStackTrace(final StringBuilder stringBuilder) {
        stringBuilder.append("-- ").append(this.title).append(" --\n");
        stringBuilder.append("Details:");
        for (final Element element3 : this.elements) {
            stringBuilder.append("\n\t");
            stringBuilder.append(element3.getName());
            stringBuilder.append(": ");
            stringBuilder.append(element3.getDetail());
        }
        if (this.stackTrace != null && this.stackTrace.length > 0) {
            stringBuilder.append("\nStacktrace:");
            for (final StackTraceElement stackTraceElement5 : this.stackTrace) {
                stringBuilder.append("\n\tat ");
                stringBuilder.append(stackTraceElement5);
            }
        }
    }
    
    public StackTraceElement[] getStackTrace() {
        return this.stackTrace;
    }
    
    public static void addBlockInfo(final CrashReportSection element, final BlockPos pos, @Nullable final BlockState state) {
        if (state != null) {
            element.add("Block", state::toString);
        }
        element.add("Block location", () -> createPositionString(pos));
    }
    
    static class Element
    {
        private final String name;
        private final String detail;
        
        public Element(final String string, final Object object) {
            this.name = string;
            if (object == null) {
                this.detail = "~~NULL~~";
            }
            else if (object instanceof Throwable) {
                final Throwable throwable3 = (Throwable)object;
                this.detail = "~~ERROR~~ " + throwable3.getClass().getSimpleName() + ": " + throwable3.getMessage();
            }
            else {
                this.detail = object.toString();
            }
        }
        
        public String getName() {
            return this.name;
        }
        
        public String getDetail() {
            return this.detail;
        }
    }
}

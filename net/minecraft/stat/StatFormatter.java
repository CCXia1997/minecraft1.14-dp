package net.minecraft.stat;

import java.text.NumberFormat;
import net.minecraft.util.SystemUtil;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.text.DecimalFormat;

public interface StatFormatter
{
    public static final DecimalFormat DECIMAL_FORMAT = SystemUtil.<DecimalFormat>consume(new DecimalFormat("########0.00"), decimalFormat -> decimalFormat.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT)));
    public static final StatFormatter DEFAULT = NumberFormat.getIntegerInstance(Locale.US)::format;
    public static final StatFormatter DIVIDE_BY_TEN = integer -> StatFormatter.DECIMAL_FORMAT.format(integer * 0.1);
    public static final StatFormatter DISTANCE = integer -> {
        double2 = integer / 100.0;
        double3 = double2 / 1000.0;
        if (double3 > 0.5) {
            return StatFormatter.DECIMAL_FORMAT.format(double3) + " km";
        }
        else if (double2 > 0.5) {
            return StatFormatter.DECIMAL_FORMAT.format(double2) + " m";
        }
        else {
            return integer + " cm";
        }
    };
    public static final StatFormatter TIME = integer -> {
        double4 = integer / 20.0;
        double5 = double4 / 60.0;
        double6 = double5 / 60.0;
        double7 = double6 / 24.0;
        double8 = double7 / 365.0;
        if (double8 > 0.5) {
            return StatFormatter.DECIMAL_FORMAT.format(double8) + " y";
        }
        else if (double7 > 0.5) {
            return StatFormatter.DECIMAL_FORMAT.format(double7) + " d";
        }
        else if (double6 > 0.5) {
            return StatFormatter.DECIMAL_FORMAT.format(double6) + " h";
        }
        else if (double5 > 0.5) {
            return StatFormatter.DECIMAL_FORMAT.format(double5) + " m";
        }
        else {
            return double4 + " s";
        }
    };
    
    @Environment(EnvType.CLIENT)
    String format(final int arg1);
    
    default static {
        final double double2;
        final double double3;
        final double double4;
        final double double5;
        final double double6;
        final double double7;
        final double double8;
    }
}

package net.minecraft.util;

import org.apache.commons.lang3.StringUtils;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.util.regex.Pattern;

public class ChatUtil
{
    private static final Pattern PATTERN;
    
    @Environment(EnvType.CLIENT)
    public static String ticksToString(final int integer) {
        int integer2 = integer / 20;
        final int integer3 = integer2 / 60;
        integer2 %= 60;
        if (integer2 < 10) {
            return integer3 + ":0" + integer2;
        }
        return integer3 + ":" + integer2;
    }
    
    @Environment(EnvType.CLIENT)
    public static String stripTextFormat(final String text) {
        return ChatUtil.PATTERN.matcher(text).replaceAll("");
    }
    
    public static boolean isEmpty(@Nullable final String string) {
        return StringUtils.isEmpty((CharSequence)string);
    }
    
    static {
        PATTERN = Pattern.compile("(?i)\\u00A7[0-9A-FK-OR]");
    }
}

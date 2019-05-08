package net.minecraft.text;

import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.List;
import com.google.common.collect.Lists;
import java.util.Collection;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.util.Locale;
import javax.annotation.Nullable;
import java.util.regex.Pattern;
import java.util.Map;

public enum TextFormat
{
    BLACK("BLACK", '0', 0, Integer.valueOf(0)), 
    b("DARK_BLUE", '1', 1, Integer.valueOf(170)), 
    c("DARK_GREEN", '2', 2, Integer.valueOf(43520)), 
    d("DARK_AQUA", '3', 3, Integer.valueOf(43690)), 
    e("DARK_RED", '4', 4, Integer.valueOf(11141120)), 
    f("DARK_PURPLE", '5', 5, Integer.valueOf(11141290)), 
    g("GOLD", '6', 6, Integer.valueOf(16755200)), 
    h("GRAY", '7', 7, Integer.valueOf(11184810)), 
    i("DARK_GRAY", '8', 8, Integer.valueOf(5592405)), 
    j("BLUE", '9', 9, Integer.valueOf(5592575)), 
    k("GREEN", 'a', 10, Integer.valueOf(5635925)), 
    l("AQUA", 'b', 11, Integer.valueOf(5636095)), 
    m("RED", 'c', 12, Integer.valueOf(16733525)), 
    n("LIGHT_PURPLE", 'd', 13, Integer.valueOf(16733695)), 
    o("YELLOW", 'e', 14, Integer.valueOf(16777045)), 
    p("WHITE", 'f', 15, Integer.valueOf(16777215)), 
    q("OBFUSCATED", 'k', true), 
    r("BOLD", 'l', true), 
    s("STRIKETHROUGH", 'm', true), 
    t("UNDERLINE", 'n', true), 
    u("ITALIC", 'o', true), 
    RESET("RESET", 'r', -1, (Integer)null);
    
    private static final Map<String, TextFormat> w;
    private static final Pattern FORMAT_PATTERN;
    private final String y;
    private final char sectionSignCode;
    private final boolean modifier;
    private final String code;
    private final int id;
    @Nullable
    private final Integer color;
    
    private static String sanitizeName(final String name) {
        return name.toLowerCase(Locale.ROOT).replaceAll("[^a-z]", "");
    }
    
    private TextFormat(final String string1, final char character, final int integer3, @Nullable final Integer integer) {
        this(string1, character, false, integer3, integer);
    }
    
    private TextFormat(final String string1, final char character, final boolean boolean3) {
        this(string1, character, boolean3, -1, null);
    }
    
    private TextFormat(final String string1, final char character, final boolean boolean3, final int integer4, @Nullable final Integer integer) {
        this.y = string1;
        this.sectionSignCode = character;
        this.modifier = boolean3;
        this.id = integer4;
        this.color = integer;
        this.code = "§" + character;
    }
    
    @Environment(EnvType.CLIENT)
    public static String getFormatAtEnd(final String string) {
        final StringBuilder stringBuilder2 = new StringBuilder();
        int integer3 = -1;
        final int integer4 = string.length();
        while ((integer3 = string.indexOf(167, integer3 + 1)) != -1) {
            if (integer3 < integer4 - 1) {
                final TextFormat textFormat5 = bySectionSignCode(string.charAt(integer3 + 1));
                if (textFormat5 == null) {
                    continue;
                }
                if (textFormat5.affectsGlyphWidth()) {
                    stringBuilder2.setLength(0);
                }
                if (textFormat5 == TextFormat.RESET) {
                    continue;
                }
                stringBuilder2.append(textFormat5);
            }
        }
        return stringBuilder2.toString();
    }
    
    public int getId() {
        return this.id;
    }
    
    public boolean isModifier() {
        return this.modifier;
    }
    
    public boolean isColor() {
        return !this.modifier && this != TextFormat.RESET;
    }
    
    @Nullable
    @Environment(EnvType.CLIENT)
    public Integer getColor() {
        return this.color;
    }
    
    @Environment(EnvType.CLIENT)
    public boolean affectsGlyphWidth() {
        return !this.modifier;
    }
    
    public String getName() {
        return this.name().toLowerCase(Locale.ROOT);
    }
    
    @Override
    public String toString() {
        return this.code;
    }
    
    @Nullable
    public static String stripFormatting(@Nullable final String str) {
        return (str == null) ? null : TextFormat.FORMAT_PATTERN.matcher(str).replaceAll("");
    }
    
    @Nullable
    public static TextFormat getFormatByName(@Nullable final String name) {
        if (name == null) {
            return null;
        }
        return TextFormat.w.get(sanitizeName(name));
    }
    
    @Nullable
    public static TextFormat byId(final int id) {
        if (id < 0) {
            return TextFormat.RESET;
        }
        for (final TextFormat textFormat5 : values()) {
            if (textFormat5.getId() == id) {
                return textFormat5;
            }
        }
        return null;
    }
    
    @Nullable
    @Environment(EnvType.CLIENT)
    public static TextFormat bySectionSignCode(final char code) {
        final char character2 = Character.toString(code).toLowerCase(Locale.ROOT).charAt(0);
        for (final TextFormat textFormat6 : values()) {
            if (textFormat6.sectionSignCode == character2) {
                return textFormat6;
            }
        }
        return null;
    }
    
    public static Collection<String> getNames(final boolean colors, final boolean modifiers) {
        final List<String> list3 = Lists.newArrayList();
        for (final TextFormat textFormat7 : values()) {
            if (!textFormat7.isColor() || colors) {
                if (!textFormat7.isModifier() || modifiers) {
                    list3.add(textFormat7.getName());
                }
            }
        }
        return list3;
    }
    
    static {
        w = Arrays.<TextFormat>stream(values()).collect(Collectors.toMap(textFormat -> sanitizeName(textFormat.y), textFormat -> textFormat));
        FORMAT_PATTERN = Pattern.compile("(?i)§[0-9A-FK-OR]");
    }
}

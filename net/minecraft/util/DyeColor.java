package net.minecraft.util;

import java.util.stream.Collectors;
import java.util.Map;
import java.util.Comparator;
import java.util.Arrays;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.MaterialColor;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

public enum DyeColor implements StringRepresentable
{
    a(0, "white", 16383998, MaterialColor.WHITE, 15790320, 16777215), 
    b(1, "orange", 16351261, MaterialColor.ORANGE, 15435844, 16738335), 
    c(2, "magenta", 13061821, MaterialColor.MAGENTA, 12801229, 16711935), 
    d(3, "light_blue", 3847130, MaterialColor.LIGHT_BLUE, 6719955, 10141901), 
    e(4, "yellow", 16701501, MaterialColor.YELLOW, 14602026, 16776960), 
    f(5, "lime", 8439583, MaterialColor.LIME, 4312372, 12582656), 
    g(6, "pink", 15961002, MaterialColor.PINK, 14188952, 16738740), 
    h(7, "gray", 4673362, MaterialColor.GRAY, 4408131, 8421504), 
    i(8, "light_gray", 10329495, MaterialColor.LIGHT_GRAY, 11250603, 13882323), 
    j(9, "cyan", 1481884, MaterialColor.CYAN, 2651799, 65535), 
    k(10, "purple", 8991416, MaterialColor.PURPLE, 8073150, 10494192), 
    l(11, "blue", 3949738, MaterialColor.BLUE, 2437522, 255), 
    m(12, "brown", 8606770, MaterialColor.BROWN, 5320730, 9127187), 
    n(13, "green", 6192150, MaterialColor.GREEN, 3887386, 65280), 
    o(14, "red", 11546150, MaterialColor.RED, 11743532, 16711680), 
    BLACK(15, "black", 1908001, MaterialColor.BLACK, 1973019, 0);
    
    private static final DyeColor[] VALUES;
    private static final Int2ObjectOpenHashMap<DyeColor> BY_FIREWORK_COLOR;
    private final int id;
    private final String name;
    private final MaterialColor materialColor;
    private final int color;
    private final int colorSwapped;
    private final float[] colorComponents;
    private final int fireworkColor;
    private final int signColor;
    
    private DyeColor(final int woolId, final String name, final int color, final MaterialColor materialColor, final int integer5, final int integer6) {
        this.id = woolId;
        this.name = name;
        this.color = color;
        this.materialColor = materialColor;
        this.signColor = integer6;
        final int integer7 = (color & 0xFF0000) >> 16;
        final int integer8 = (color & 0xFF00) >> 8;
        final int integer9 = (color & 0xFF) >> 0;
        this.colorSwapped = (integer9 << 16 | integer8 << 8 | integer7 << 0);
        this.colorComponents = new float[] { integer7 / 255.0f, integer8 / 255.0f, integer9 / 255.0f };
        this.fireworkColor = integer5;
    }
    
    public int getId() {
        return this.id;
    }
    
    public String getName() {
        return this.name;
    }
    
    @Environment(EnvType.CLIENT)
    public int getColorSwapped() {
        return this.colorSwapped;
    }
    
    public float[] getColorComponents() {
        return this.colorComponents;
    }
    
    public MaterialColor getMaterialColor() {
        return this.materialColor;
    }
    
    public int getFireworkColor() {
        return this.fireworkColor;
    }
    
    @Environment(EnvType.CLIENT)
    public int getSignColor() {
        return this.signColor;
    }
    
    public static DyeColor byId(int id) {
        if (id < 0 || id >= DyeColor.VALUES.length) {
            id = 0;
        }
        return DyeColor.VALUES[id];
    }
    
    public static DyeColor byName(final String string, final DyeColor dyeColor) {
        for (final DyeColor dyeColor2 : values()) {
            if (dyeColor2.name.equals(string)) {
                return dyeColor2;
            }
        }
        return dyeColor;
    }
    
    @Nullable
    @Environment(EnvType.CLIENT)
    public static DyeColor byFireworkColor(final int integer) {
        return (DyeColor)DyeColor.BY_FIREWORK_COLOR.get(integer);
    }
    
    @Override
    public String toString() {
        return this.name;
    }
    
    @Override
    public String asString() {
        return this.name;
    }
    
    static {
        VALUES = Arrays.<DyeColor>stream(values()).sorted(Comparator.comparingInt(DyeColor::getId)).<DyeColor>toArray(DyeColor[]::new);
        BY_FIREWORK_COLOR = new Int2ObjectOpenHashMap((Map)Arrays.<DyeColor>stream(values()).collect(Collectors.toMap(dyeColor -> dyeColor.fireworkColor, dyeColor -> dyeColor)));
    }
}

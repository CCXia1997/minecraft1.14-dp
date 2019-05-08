package net.minecraft.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class MaterialColor
{
    public static final MaterialColor[] COLORS;
    public static final MaterialColor AIR;
    public static final MaterialColor GRASS;
    public static final MaterialColor SAND;
    public static final MaterialColor WEB;
    public static final MaterialColor LAVA;
    public static final MaterialColor ICE;
    public static final MaterialColor IRON;
    public static final MaterialColor FOLIAGE;
    public static final MaterialColor WHITE;
    public static final MaterialColor CLAY;
    public static final MaterialColor DIRT;
    public static final MaterialColor STONE;
    public static final MaterialColor WATER;
    public static final MaterialColor WOOD;
    public static final MaterialColor QUARTZ;
    public static final MaterialColor ORANGE;
    public static final MaterialColor MAGENTA;
    public static final MaterialColor LIGHT_BLUE;
    public static final MaterialColor YELLOW;
    public static final MaterialColor LIME;
    public static final MaterialColor PINK;
    public static final MaterialColor GRAY;
    public static final MaterialColor LIGHT_GRAY;
    public static final MaterialColor CYAN;
    public static final MaterialColor PURPLE;
    public static final MaterialColor BLUE;
    public static final MaterialColor BROWN;
    public static final MaterialColor GREEN;
    public static final MaterialColor RED;
    public static final MaterialColor BLACK;
    public static final MaterialColor GOLD;
    public static final MaterialColor DIAMOND;
    public static final MaterialColor LAPIS;
    public static final MaterialColor EMERALD;
    public static final MaterialColor SPRUCE;
    public static final MaterialColor NETHER;
    public static final MaterialColor WHITE_TERRACOTTA;
    public static final MaterialColor ORANGE_TERRACOTTA;
    public static final MaterialColor MAGENTA_TERRACOTTA;
    public static final MaterialColor LIGHT_BLUE_TERRACOTTA;
    public static final MaterialColor YELLOW_TERRACOTTA;
    public static final MaterialColor LIME_TERRACOTTA;
    public static final MaterialColor PINK_TERRACOTTA;
    public static final MaterialColor GRAY_TERRACOTTA;
    public static final MaterialColor LIGHT_GRAY_TERRACOTTA;
    public static final MaterialColor CYAN_TERRACOTTA;
    public static final MaterialColor PURPLE_TERRACOTTA;
    public static final MaterialColor BLUE_TERRACOTTA;
    public static final MaterialColor BROWN_TERRACOTTA;
    public static final MaterialColor GREEN_TERRACOTTA;
    public static final MaterialColor RED_TERRACOTTA;
    public static final MaterialColor BLACK_TERRACOTTA;
    public final int color;
    public final int id;
    
    private MaterialColor(final int id, final int integer2) {
        if (id < 0 || id > 63) {
            throw new IndexOutOfBoundsException("Map colour ID must be between 0 and 63 (inclusive)");
        }
        this.id = id;
        this.color = integer2;
        MaterialColor.COLORS[id] = this;
    }
    
    @Environment(EnvType.CLIENT)
    public int getRenderColor(final int shade) {
        int integer2 = 220;
        if (shade == 3) {
            integer2 = 135;
        }
        if (shade == 2) {
            integer2 = 255;
        }
        if (shade == 1) {
            integer2 = 220;
        }
        if (shade == 0) {
            integer2 = 180;
        }
        final int integer3 = (this.color >> 16 & 0xFF) * integer2 / 255;
        final int integer4 = (this.color >> 8 & 0xFF) * integer2 / 255;
        final int integer5 = (this.color & 0xFF) * integer2 / 255;
        return 0xFF000000 | integer5 << 16 | integer4 << 8 | integer3;
    }
    
    static {
        COLORS = new MaterialColor[64];
        AIR = new MaterialColor(0, 0);
        GRASS = new MaterialColor(1, 8368696);
        SAND = new MaterialColor(2, 16247203);
        WEB = new MaterialColor(3, 13092807);
        LAVA = new MaterialColor(4, 16711680);
        ICE = new MaterialColor(5, 10526975);
        IRON = new MaterialColor(6, 10987431);
        FOLIAGE = new MaterialColor(7, 31744);
        WHITE = new MaterialColor(8, 16777215);
        CLAY = new MaterialColor(9, 10791096);
        DIRT = new MaterialColor(10, 9923917);
        STONE = new MaterialColor(11, 7368816);
        WATER = new MaterialColor(12, 4210943);
        WOOD = new MaterialColor(13, 9402184);
        QUARTZ = new MaterialColor(14, 16776437);
        ORANGE = new MaterialColor(15, 14188339);
        MAGENTA = new MaterialColor(16, 11685080);
        LIGHT_BLUE = new MaterialColor(17, 6724056);
        YELLOW = new MaterialColor(18, 15066419);
        LIME = new MaterialColor(19, 8375321);
        PINK = new MaterialColor(20, 15892389);
        GRAY = new MaterialColor(21, 5000268);
        LIGHT_GRAY = new MaterialColor(22, 10066329);
        CYAN = new MaterialColor(23, 5013401);
        PURPLE = new MaterialColor(24, 8339378);
        BLUE = new MaterialColor(25, 3361970);
        BROWN = new MaterialColor(26, 6704179);
        GREEN = new MaterialColor(27, 6717235);
        RED = new MaterialColor(28, 10040115);
        BLACK = new MaterialColor(29, 1644825);
        GOLD = new MaterialColor(30, 16445005);
        DIAMOND = new MaterialColor(31, 6085589);
        LAPIS = new MaterialColor(32, 4882687);
        EMERALD = new MaterialColor(33, 55610);
        SPRUCE = new MaterialColor(34, 8476209);
        NETHER = new MaterialColor(35, 7340544);
        WHITE_TERRACOTTA = new MaterialColor(36, 13742497);
        ORANGE_TERRACOTTA = new MaterialColor(37, 10441252);
        MAGENTA_TERRACOTTA = new MaterialColor(38, 9787244);
        LIGHT_BLUE_TERRACOTTA = new MaterialColor(39, 7367818);
        YELLOW_TERRACOTTA = new MaterialColor(40, 12223780);
        LIME_TERRACOTTA = new MaterialColor(41, 6780213);
        PINK_TERRACOTTA = new MaterialColor(42, 10505550);
        GRAY_TERRACOTTA = new MaterialColor(43, 3746083);
        LIGHT_GRAY_TERRACOTTA = new MaterialColor(44, 8874850);
        CYAN_TERRACOTTA = new MaterialColor(45, 5725276);
        PURPLE_TERRACOTTA = new MaterialColor(46, 8014168);
        BLUE_TERRACOTTA = new MaterialColor(47, 4996700);
        BROWN_TERRACOTTA = new MaterialColor(48, 4993571);
        GREEN_TERRACOTTA = new MaterialColor(49, 5001770);
        RED_TERRACOTTA = new MaterialColor(50, 9321518);
        BLACK_TERRACOTTA = new MaterialColor(51, 2430480);
    }
}

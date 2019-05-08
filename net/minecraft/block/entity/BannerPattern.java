package net.minecraft.block.entity;

import java.util.AbstractList;
import java.util.Iterator;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import com.google.common.collect.Lists;
import net.minecraft.util.DyeColor;
import org.apache.commons.lang3.tuple.Pair;
import java.util.List;
import net.minecraft.item.Items;
import net.minecraft.item.ItemProvider;
import net.minecraft.block.Blocks;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;

public enum BannerPattern
{
    BASE("base", "b"), 
    SQUARE_BOTTOM_LEFT("square_bottom_left", "bl", "   ", "   ", "#  "), 
    SQUARE_BOTTOM_RIGHT("square_bottom_right", "br", "   ", "   ", "  #"), 
    SQUARE_TOP_LEFT("square_top_left", "tl", "#  ", "   ", "   "), 
    SQUARE_TOP_RIGHT("square_top_right", "tr", "  #", "   ", "   "), 
    STRIPE_BOTTOM("stripe_bottom", "bs", "   ", "   ", "###"), 
    STRIPE_TOP("stripe_top", "ts", "###", "   ", "   "), 
    STRIPE_LEFT("stripe_left", "ls", "#  ", "#  ", "#  "), 
    STRIPE_RIGHT("stripe_right", "rs", "  #", "  #", "  #"), 
    STRIPE_CENTER("stripe_center", "cs", " # ", " # ", " # "), 
    STRIPE_MIDDLE("stripe_middle", "ms", "   ", "###", "   "), 
    STRIPE_DOWNRIGHT("stripe_downright", "drs", "#  ", " # ", "  #"), 
    STRIPE_DOWNLEFT("stripe_downleft", "dls", "  #", " # ", "#  "), 
    SMALL_STRIPES("small_stripes", "ss", "# #", "# #", "   "), 
    CROSS("cross", "cr", "# #", " # ", "# #"), 
    STRAIGHT_CROSS("straight_cross", "sc", " # ", "###", " # "), 
    TRIANGLE_BOTTOM("triangle_bottom", "bt", "   ", " # ", "# #"), 
    TRIANGLE_TOP("triangle_top", "tt", "# #", " # ", "   "), 
    TRIANGLES_BOTTOM("triangles_bottom", "bts", "   ", "# #", " # "), 
    TRIANGLES_TOP("triangles_top", "tts", " # ", "# #", "   "), 
    DIAGONAL_DOWN_LEFT("diagonal_left", "ld", "## ", "#  ", "   "), 
    DIAGONAL_UP_RIGHT("diagonal_up_right", "rd", "   ", "  #", " ##"), 
    DIAGONAL_UP_LEFT("diagonal_up_left", "lud", "   ", "#  ", "## "), 
    DIAGONAL_DOWN_RIGHT("diagonal_right", "rud", " ##", "  #", "   "), 
    CIRCLE("circle", "mc", "   ", " # ", "   "), 
    RHOMBUS("rhombus", "mr", " # ", "# #", " # "), 
    HALF_VERTICAL_LEFT("half_vertical", "vh", "## ", "## ", "## "), 
    HALF_HORIZONTAL_TOP("half_horizontal", "hh", "###", "###", "   "), 
    HALF_VERTICAL_RIGHT("half_vertical_right", "vhr", " ##", " ##", " ##"), 
    HALF_HORIZONTAL_BOTTOM("half_horizontal_bottom", "hhb", "   ", "###", "###"), 
    BORDER("border", "bo", "###", "# #", "###"), 
    CURLY_BORDER("curly_border", "cbo", new ItemStack(Blocks.dH)), 
    GRADIENT_DOWN("gradient", "gra", "# #", " # ", " # "), 
    GRADIENT_UP("gradient_up", "gru", " # ", " # ", "# #"), 
    BRICKS("bricks", "bri", new ItemStack(Blocks.bF)), 
    GLOBE("globe", "glb"), 
    CREEPER("creeper", "cre", new ItemStack(Items.CREEPER_HEAD)), 
    SKULL("skull", "sku", new ItemStack(Items.WITHER_SKELETON_SKULL)), 
    FLOWER("flower", "flo", new ItemStack(Blocks.bx)), 
    MOJANG("mojang", "moj", new ItemStack(Items.kq));
    
    public static final int COUNT;
    public static final int P;
    private final String name;
    private final String id;
    private final String[] recipePattern;
    private ItemStack baseStack;
    
    private BannerPattern(final String name, final String string2) {
        this.recipePattern = new String[3];
        this.baseStack = ItemStack.EMPTY;
        this.name = name;
        this.id = string2;
    }
    
    private BannerPattern(final String name, final String id, final ItemStack itemStack) {
        this(name, id);
        this.baseStack = itemStack;
    }
    
    private BannerPattern(final String name, final String id, final String recipePattern0, final String recipePattern1, final String string5) {
        this(name, id);
        this.recipePattern[0] = recipePattern0;
        this.recipePattern[1] = recipePattern1;
        this.recipePattern[2] = string5;
    }
    
    @Environment(EnvType.CLIENT)
    public String getName() {
        return this.name;
    }
    
    public String getId() {
        return this.id;
    }
    
    @Nullable
    @Environment(EnvType.CLIENT)
    public static BannerPattern byId(final String id) {
        for (final BannerPattern bannerPattern5 : values()) {
            if (bannerPattern5.id.equals(id)) {
                return bannerPattern5;
            }
        }
        return null;
    }
    
    static {
        COUNT = values().length;
        P = BannerPattern.COUNT - 5 - 1;
    }
    
    public static class Builder
    {
        private final List<Pair<BannerPattern, DyeColor>> patterns;
        
        public Builder() {
            this.patterns = Lists.newArrayList();
        }
        
        public Builder with(final BannerPattern pattern, final DyeColor dyeColor) {
            this.patterns.add((Pair<BannerPattern, DyeColor>)Pair.of(pattern, dyeColor));
            return this;
        }
        
        public ListTag build() {
            final ListTag listTag1 = new ListTag();
            for (final Pair<BannerPattern, DyeColor> pair3 : this.patterns) {
                final CompoundTag compoundTag4 = new CompoundTag();
                compoundTag4.putString("Pattern", ((BannerPattern)pair3.getLeft()).id);
                compoundTag4.putInt("Color", ((DyeColor)pair3.getRight()).getId());
                ((AbstractList<CompoundTag>)listTag1).add(compoundTag4);
            }
            return listTag1;
        }
    }
}

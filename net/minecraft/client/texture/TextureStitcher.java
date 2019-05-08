package net.minecraft.client.texture;

import java.util.function.Consumer;
import net.minecraft.util.Identifier;
import java.util.Iterator;
import net.minecraft.util.math.MathHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import java.util.Comparator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class TextureStitcher
{
    private static final Comparator<Holder> comparator;
    private final int mipLevel;
    private final Set<Holder> holders;
    private final List<Slot> slots;
    private int width;
    private int height;
    private final int maxWidth;
    private final int maxHeight;
    
    public TextureStitcher(final int maxWidth, final int maxHeight, final int integer3) {
        this.holders = Sets.newHashSetWithExpectedSize(256);
        this.slots = Lists.newArrayListWithCapacity(256);
        this.mipLevel = integer3;
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
    }
    
    public int getWidth() {
        return this.width;
    }
    
    public int getHeight() {
        return this.height;
    }
    
    public void add(final Sprite sprite) {
        final Holder holder2 = new Holder(sprite, this.mipLevel);
        this.holders.add(holder2);
    }
    
    public void stitch() {
        final List<Holder> list1 = Lists.newArrayList(this.holders);
        list1.sort(TextureStitcher.comparator);
        for (final Holder holder3 : list1) {
            if (!this.tryFit(holder3)) {
                throw new TextureStitcherCannotFitException(holder3.sprite);
            }
        }
        this.width = MathHelper.smallestEncompassingPowerOfTwo(this.width);
        this.height = MathHelper.smallestEncompassingPowerOfTwo(this.height);
    }
    
    public List<Sprite> getStitchedSprites() {
        final List<Sprite> list1 = Lists.newArrayList();
        for (final Slot slot2 : this.slots) {
            final Holder holder3;
            final Sprite sprite4;
            final List<Sprite> list2;
            slot2.addAllFilledSlots(slot -> {
                holder3 = slot.getTexture();
                sprite4 = holder3.sprite;
                sprite4.init(this.width, this.height, slot.getX(), slot.getY());
                list2.add(sprite4);
                return;
            });
        }
        return list1;
    }
    
    private static int applyMipLevel(final int size, final int mipLevel) {
        return (size >> mipLevel) + (((size & (1 << mipLevel) - 1) != 0x0) ? 1 : 0) << mipLevel;
    }
    
    private boolean tryFit(final Holder holder) {
        for (final Slot slot3 : this.slots) {
            if (slot3.tryFit(holder)) {
                return true;
            }
        }
        return this.b(holder);
    }
    
    private boolean b(final Holder holder) {
        final int integer3 = MathHelper.smallestEncompassingPowerOfTwo(this.width);
        final int integer4 = MathHelper.smallestEncompassingPowerOfTwo(this.height);
        final int integer5 = MathHelper.smallestEncompassingPowerOfTwo(this.width + holder.width);
        final int integer6 = MathHelper.smallestEncompassingPowerOfTwo(this.height + holder.height);
        final boolean boolean7 = integer5 <= this.maxWidth;
        final boolean boolean8 = integer6 <= this.maxHeight;
        if (!boolean7 && !boolean8) {
            return false;
        }
        final boolean boolean9 = boolean7 && integer3 != integer5;
        final boolean boolean10 = boolean8 && integer4 != integer6;
        boolean boolean11;
        if (boolean9 ^ boolean10) {
            boolean11 = boolean9;
        }
        else {
            boolean11 = (boolean7 && integer3 <= integer4);
        }
        Slot slot11;
        if (boolean11) {
            if (this.height == 0) {
                this.height = holder.height;
            }
            slot11 = new Slot(this.width, 0, holder.width, this.height);
            this.width += holder.width;
        }
        else {
            slot11 = new Slot(0, this.height, this.width, holder.height);
            this.height += holder.height;
        }
        slot11.tryFit(holder);
        this.slots.add(slot11);
        return true;
    }
    
    static {
        comparator = Comparator.<Holder, Comparable>comparing(holder -> -holder.height).<Comparable>thenComparing(holder -> -holder.width).<Comparable>thenComparing(holder -> holder.sprite.getId());
    }
    
    @Environment(EnvType.CLIENT)
    static class Holder
    {
        public final Sprite sprite;
        public final int width;
        public final int height;
        
        public Holder(final Sprite sprite, final int mipLevel) {
            this.sprite = sprite;
            this.width = applyMipLevel(sprite.getWidth(), mipLevel);
            this.height = applyMipLevel(sprite.getHeight(), mipLevel);
        }
        
        @Override
        public String toString() {
            return "Holder{width=" + this.width + ", height=" + this.height + '}';
        }
    }
    
    @Environment(EnvType.CLIENT)
    public static class Slot
    {
        private final int x;
        private final int y;
        private final int width;
        private final int height;
        private List<Slot> subSlots;
        private Holder texture;
        
        public Slot(final int integer1, final int integer2, final int integer3, final int integer4) {
            this.x = integer1;
            this.y = integer2;
            this.width = integer3;
            this.height = integer4;
        }
        
        public Holder getTexture() {
            return this.texture;
        }
        
        public int getX() {
            return this.x;
        }
        
        public int getY() {
            return this.y;
        }
        
        public boolean tryFit(final Holder holder) {
            if (this.texture != null) {
                return false;
            }
            final int integer2 = holder.width;
            final int integer3 = holder.height;
            if (integer2 > this.width || integer3 > this.height) {
                return false;
            }
            if (integer2 == this.width && integer3 == this.height) {
                this.texture = holder;
                return true;
            }
            if (this.subSlots == null) {
                (this.subSlots = Lists.newArrayListWithCapacity(1)).add(new Slot(this.x, this.y, integer2, integer3));
                final int integer4 = this.width - integer2;
                final int integer5 = this.height - integer3;
                if (integer5 > 0 && integer4 > 0) {
                    final int integer6 = Math.max(this.height, integer4);
                    final int integer7 = Math.max(this.width, integer5);
                    if (integer6 >= integer7) {
                        this.subSlots.add(new Slot(this.x, this.y + integer3, integer2, integer5));
                        this.subSlots.add(new Slot(this.x + integer2, this.y, integer4, this.height));
                    }
                    else {
                        this.subSlots.add(new Slot(this.x + integer2, this.y, integer4, integer3));
                        this.subSlots.add(new Slot(this.x, this.y + integer3, this.width, integer5));
                    }
                }
                else if (integer4 == 0) {
                    this.subSlots.add(new Slot(this.x, this.y + integer3, integer2, integer5));
                }
                else if (integer5 == 0) {
                    this.subSlots.add(new Slot(this.x + integer2, this.y, integer4, integer3));
                }
            }
            for (final Slot slot5 : this.subSlots) {
                if (slot5.tryFit(holder)) {
                    return true;
                }
            }
            return false;
        }
        
        public void addAllFilledSlots(final Consumer<Slot> consumer) {
            if (this.texture != null) {
                consumer.accept(this);
            }
            else if (this.subSlots != null) {
                for (final Slot slot3 : this.subSlots) {
                    slot3.addAllFilledSlots(consumer);
                }
            }
        }
        
        @Override
        public String toString() {
            return "Slot{originX=" + this.x + ", originY=" + this.y + ", width=" + this.width + ", height=" + this.height + ", texture=" + this.texture + ", subSlots=" + this.subSlots + '}';
        }
    }
}

package net.minecraft.client.gui.widget;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
enum AdvancementTabType
{
    a(0, 0, 28, 32, 8), 
    b(84, 0, 28, 32, 8), 
    c(0, 64, 32, 28, 5), 
    d(96, 64, 32, 28, 5);
    
    private final int e;
    private final int f;
    private final int g;
    private final int h;
    private final int i;
    
    private AdvancementTabType(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5) {
        this.e = integer1;
        this.f = integer2;
        this.g = integer3;
        this.h = integer4;
        this.i = integer5;
    }
    
    public int a() {
        return this.i;
    }
    
    public void drawBackground(final DrawableHelper drawable, final int x, final int y, final boolean selected, final int integer5) {
        int integer6 = this.e;
        if (integer5 > 0) {
            integer6 += this.g;
        }
        if (integer5 == this.i - 1) {
            integer6 += this.g;
        }
        final int integer7 = selected ? (this.f + this.h) : this.f;
        drawable.blit(x + this.a(integer5), y + this.b(integer5), integer6, integer7, this.g, this.h);
    }
    
    public void drawIcon(final int x, final int y, final int integer3, final ItemRenderer itemRenderer, final ItemStack itemStack) {
        int integer4 = x + this.a(integer3);
        int integer5 = y + this.b(integer3);
        switch (this) {
            case a: {
                integer4 += 6;
                integer5 += 9;
                break;
            }
            case b: {
                integer4 += 6;
                integer5 += 6;
                break;
            }
            case c: {
                integer4 += 10;
                integer5 += 5;
                break;
            }
            case d: {
                integer4 += 6;
                integer5 += 5;
                break;
            }
        }
        itemRenderer.renderGuiItem(null, itemStack, integer4, integer5);
    }
    
    public int a(final int integer) {
        switch (this) {
            case a: {
                return (this.g + 4) * integer;
            }
            case b: {
                return (this.g + 4) * integer;
            }
            case c: {
                return -this.g + 4;
            }
            case d: {
                return 248;
            }
            default: {
                throw new UnsupportedOperationException("Don't know what this tab type is!" + this);
            }
        }
    }
    
    public int b(final int integer) {
        switch (this) {
            case a: {
                return -this.h + 4;
            }
            case b: {
                return 136;
            }
            case c: {
                return this.h * integer;
            }
            case d: {
                return this.h * integer;
            }
            default: {
                throw new UnsupportedOperationException("Don't know what this tab type is!" + this);
            }
        }
    }
    
    public boolean a(final int integer1, final int integer2, final int integer3, final double double4, final double double6) {
        final int integer4 = integer1 + this.a(integer3);
        final int integer5 = integer2 + this.b(integer3);
        return double4 > integer4 && double4 < integer4 + this.g && double6 > integer5 && double6 < integer5 + this.h;
    }
}

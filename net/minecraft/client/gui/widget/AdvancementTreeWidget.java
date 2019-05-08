package net.minecraft.client.gui.widget;

import javax.annotation.Nullable;
import java.util.Iterator;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.texture.TextureManager;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.item.ItemRenderer;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.item.ItemStack;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.advancement.Advancement;
import net.minecraft.client.gui.menu.AdvancementsScreen;
import net.minecraft.client.MinecraftClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;

@Environment(EnvType.CLIENT)
public class AdvancementTreeWidget extends DrawableHelper
{
    private final MinecraftClient client;
    private final AdvancementsScreen b;
    private final AdvancementTabType tabType;
    private final int d;
    private final Advancement rootAdvancement;
    private final AdvancementDisplay f;
    private final ItemStack g;
    private final String h;
    private final AdvancementWidget i;
    private final Map<Advancement, AdvancementWidget> widgets;
    private double k;
    private double l;
    private int m;
    private int n;
    private int o;
    private int p;
    private float q;
    private boolean r;
    
    public AdvancementTreeWidget(final MinecraftClient client, final AdvancementsScreen advancementsScreen, final AdvancementTabType advancementTabType, final int integer, final Advancement rootAdvancement, final AdvancementDisplay advancementDisplay) {
        this.widgets = Maps.newLinkedHashMap();
        this.m = Integer.MAX_VALUE;
        this.n = Integer.MAX_VALUE;
        this.o = Integer.MIN_VALUE;
        this.p = Integer.MIN_VALUE;
        this.client = client;
        this.b = advancementsScreen;
        this.tabType = advancementTabType;
        this.d = integer;
        this.rootAdvancement = rootAdvancement;
        this.f = advancementDisplay;
        this.g = advancementDisplay.getIcon();
        this.h = advancementDisplay.getTitle().getFormattedText();
        this.a(this.i = new AdvancementWidget(this, client, rootAdvancement, advancementDisplay), rootAdvancement);
    }
    
    public Advancement c() {
        return this.rootAdvancement;
    }
    
    public String d() {
        return this.h;
    }
    
    public void drawBackground(final int x, final int y, final boolean boolean3) {
        this.tabType.drawBackground(this, x, y, boolean3, this.d);
    }
    
    public void drawIcon(final int x, final int y, final ItemRenderer itemRenderer) {
        this.tabType.drawIcon(x, y, this.d, itemRenderer, this.g);
    }
    
    public void f() {
        if (!this.r) {
            this.k = 117 - (this.o + this.m) / 2;
            this.l = 56 - (this.p + this.n) / 2;
            this.r = true;
        }
        GlStateManager.depthFunc(518);
        DrawableHelper.fill(0, 0, 234, 113, -16777216);
        GlStateManager.depthFunc(515);
        final Identifier identifier1 = this.f.getBackground();
        if (identifier1 != null) {
            this.client.getTextureManager().bindTexture(identifier1);
        }
        else {
            this.client.getTextureManager().bindTexture(TextureManager.a);
        }
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        final int integer2 = MathHelper.floor(this.k);
        final int integer3 = MathHelper.floor(this.l);
        final int integer4 = integer2 % 16;
        final int integer5 = integer3 % 16;
        for (int integer6 = -1; integer6 <= 15; ++integer6) {
            for (int integer7 = -1; integer7 <= 8; ++integer7) {
                DrawableHelper.blit(integer4 + 16 * integer6, integer5 + 16 * integer7, 0.0f, 0.0f, 16, 16, 16, 16);
            }
        }
        this.i.a(integer2, integer3, true);
        this.i.a(integer2, integer3, false);
        this.i.a(integer2, integer3);
    }
    
    public void a(final int integer1, final int integer2, final int integer3, final int integer4) {
        GlStateManager.pushMatrix();
        GlStateManager.translatef(0.0f, 0.0f, 200.0f);
        DrawableHelper.fill(0, 0, 234, 113, MathHelper.floor(this.q * 255.0f) << 24);
        boolean boolean5 = false;
        final int integer5 = MathHelper.floor(this.k);
        final int integer6 = MathHelper.floor(this.l);
        if (integer1 > 0 && integer1 < 234 && integer2 > 0 && integer2 < 113) {
            for (final AdvancementWidget advancementWidget9 : this.widgets.values()) {
                if (advancementWidget9.a(integer5, integer6, integer1, integer2)) {
                    boolean5 = true;
                    advancementWidget9.a(integer5, integer6, this.q, integer3, integer4);
                    break;
                }
            }
        }
        GlStateManager.popMatrix();
        if (boolean5) {
            this.q = MathHelper.clamp(this.q + 0.02f, 0.0f, 0.3f);
        }
        else {
            this.q = MathHelper.clamp(this.q - 0.04f, 0.0f, 1.0f);
        }
    }
    
    public boolean a(final int integer1, final int integer2, final double double3, final double double5) {
        return this.tabType.a(integer1, integer2, this.d, double3, double5);
    }
    
    @Nullable
    public static AdvancementTreeWidget create(final MinecraftClient minecraft, final AdvancementsScreen advancementsScreen, int integer, final Advancement advancement) {
        if (advancement.getDisplay() == null) {
            return null;
        }
        for (final AdvancementTabType advancementTabType8 : AdvancementTabType.values()) {
            if (integer < advancementTabType8.a()) {
                return new AdvancementTreeWidget(minecraft, advancementsScreen, advancementTabType8, integer, advancement, advancement.getDisplay());
            }
            integer -= advancementTabType8.a();
        }
        return null;
    }
    
    public void a(final double double1, final double double3) {
        if (this.o - this.m > 234) {
            this.k = MathHelper.clamp(this.k + double1, -(this.o - 234), 0.0);
        }
        if (this.p - this.n > 113) {
            this.l = MathHelper.clamp(this.l + double3, -(this.p - 113), 0.0);
        }
    }
    
    public void a(final Advancement advancement) {
        if (advancement.getDisplay() == null) {
            return;
        }
        final AdvancementWidget advancementWidget2 = new AdvancementWidget(this, this.client, advancement, advancement.getDisplay());
        this.a(advancementWidget2, advancement);
    }
    
    private void a(final AdvancementWidget advancementWidget, final Advancement advancement) {
        this.widgets.put(advancement, advancementWidget);
        final int integer3 = advancementWidget.d();
        final int integer4 = integer3 + 28;
        final int integer5 = advancementWidget.c();
        final int integer6 = integer5 + 27;
        this.m = Math.min(this.m, integer3);
        this.o = Math.max(this.o, integer4);
        this.n = Math.min(this.n, integer5);
        this.p = Math.max(this.p, integer6);
        for (final AdvancementWidget advancementWidget2 : this.widgets.values()) {
            advancementWidget2.b();
        }
    }
    
    @Nullable
    public AdvancementWidget getWidgetForAdvancement(final Advancement advancement) {
        return this.widgets.get(advancement);
    }
    
    public AdvancementsScreen g() {
        return this.b;
    }
}

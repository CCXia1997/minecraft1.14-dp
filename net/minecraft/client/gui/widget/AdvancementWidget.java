package net.minecraft.client.gui.widget;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.client.render.GuiLighting;
import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import java.util.regex.Matcher;
import java.util.Collections;
import java.util.Iterator;
import net.minecraft.util.math.MathHelper;
import com.google.common.collect.Lists;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.client.MinecraftClient;
import java.util.List;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.advancement.Advancement;
import java.util.regex.Pattern;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;

@Environment(EnvType.CLIENT)
public class AdvancementWidget extends DrawableHelper
{
    private static final Identifier WIDGETS_TEX;
    private static final Pattern b;
    private final AdvancementTreeWidget tree;
    private final Advancement advancement;
    private final AdvancementDisplay display;
    private final String f;
    private final int g;
    private final List<String> h;
    private final MinecraftClient client;
    private AdvancementWidget j;
    private final List<AdvancementWidget> children;
    private AdvancementProgress l;
    private final int xPos;
    private final int yPos;
    
    public AdvancementWidget(final AdvancementTreeWidget tree, final MinecraftClient client, final Advancement advancement, final AdvancementDisplay advancementDisplay) {
        this.children = Lists.newArrayList();
        this.tree = tree;
        this.advancement = advancement;
        this.display = advancementDisplay;
        this.client = client;
        this.f = client.textRenderer.trimToWidth(advancementDisplay.getTitle().getFormattedText(), 163);
        this.xPos = MathHelper.floor(advancementDisplay.getX() * 28.0f);
        this.yPos = MathHelper.floor(advancementDisplay.getY() * 27.0f);
        final int integer5 = advancement.getRequirementCount();
        final int integer6 = String.valueOf(integer5).length();
        final int integer7 = (integer5 > 1) ? (client.textRenderer.getStringWidth("  ") + client.textRenderer.getStringWidth("0") * integer6 * 2 + client.textRenderer.getStringWidth("/")) : 0;
        int integer8 = 29 + client.textRenderer.getStringWidth(this.f) + integer7;
        final String string9 = advancementDisplay.getDescription().getFormattedText();
        this.h = this.a(string9, integer8);
        for (final String string10 : this.h) {
            integer8 = Math.max(integer8, client.textRenderer.getStringWidth(string10));
        }
        this.g = integer8 + 3 + 5;
    }
    
    private List<String> a(final String string, final int integer) {
        if (string.isEmpty()) {
            return Collections.<String>emptyList();
        }
        final List<String> list3 = this.client.textRenderer.wrapStringToWidthAsList(string, integer);
        if (list3.size() < 2) {
            return list3;
        }
        final String string2 = list3.get(0);
        final String string3 = list3.get(1);
        final int integer2 = this.client.textRenderer.getStringWidth(string2 + ' ' + string3.split(" ")[0]);
        if (integer2 - integer <= 10) {
            return this.client.textRenderer.wrapStringToWidthAsList(string, integer2);
        }
        final Matcher matcher7 = AdvancementWidget.b.matcher(string2);
        if (matcher7.matches()) {
            final int integer3 = this.client.textRenderer.getStringWidth(matcher7.group(1));
            if (integer - integer3 <= 10) {
                return this.client.textRenderer.wrapStringToWidthAsList(string, integer3);
            }
        }
        return list3;
    }
    
    @Nullable
    private AdvancementWidget getRootWidget(Advancement advancement) {
        do {
            advancement = advancement.getParent();
        } while (advancement != null && advancement.getDisplay() == null);
        if (advancement == null || advancement.getDisplay() == null) {
            return null;
        }
        return this.tree.getWidgetForAdvancement(advancement);
    }
    
    public void a(final int integer1, final int integer2, final boolean boolean3) {
        if (this.j != null) {
            final int integer3 = integer1 + this.j.xPos + 13;
            final int integer4 = integer1 + this.j.xPos + 26 + 4;
            final int integer5 = integer2 + this.j.yPos + 13;
            final int integer6 = integer1 + this.xPos + 13;
            final int integer7 = integer2 + this.yPos + 13;
            final int integer8 = boolean3 ? -16777216 : -1;
            if (boolean3) {
                this.hLine(integer4, integer3, integer5 - 1, integer8);
                this.hLine(integer4 + 1, integer3, integer5, integer8);
                this.hLine(integer4, integer3, integer5 + 1, integer8);
                this.hLine(integer6, integer4 - 1, integer7 - 1, integer8);
                this.hLine(integer6, integer4 - 1, integer7, integer8);
                this.hLine(integer6, integer4 - 1, integer7 + 1, integer8);
                this.vLine(integer4 - 1, integer7, integer5, integer8);
                this.vLine(integer4 + 1, integer7, integer5, integer8);
            }
            else {
                this.hLine(integer4, integer3, integer5, integer8);
                this.hLine(integer6, integer4, integer7, integer8);
                this.vLine(integer4, integer7, integer5, integer8);
            }
        }
        for (final AdvancementWidget advancementWidget5 : this.children) {
            advancementWidget5.a(integer1, integer2, boolean3);
        }
    }
    
    public void a(final int integer1, final int integer2) {
        if (!this.display.isHidden() || (this.l != null && this.l.isDone())) {
            final float float3 = (this.l == null) ? 0.0f : this.l.getProgressBarPercentage();
            AchievementObtainedStatus achievementObtainedStatus4;
            if (float3 >= 1.0f) {
                achievementObtainedStatus4 = AchievementObtainedStatus.a;
            }
            else {
                achievementObtainedStatus4 = AchievementObtainedStatus.b;
            }
            this.client.getTextureManager().bindTexture(AdvancementWidget.WIDGETS_TEX);
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.enableBlend();
            this.blit(integer1 + this.xPos + 3, integer2 + this.yPos, this.display.getFrame().texV(), 128 + achievementObtainedStatus4.getSpriteIndex() * 26, 26, 26);
            GuiLighting.enableForItems();
            this.client.getItemRenderer().renderGuiItem(null, this.display.getIcon(), integer1 + this.xPos + 8, integer2 + this.yPos + 5);
        }
        for (final AdvancementWidget advancementWidget4 : this.children) {
            advancementWidget4.a(integer1, integer2);
        }
    }
    
    public void setProgress(final AdvancementProgress advancementProgress) {
        this.l = advancementProgress;
    }
    
    public void a(final AdvancementWidget advancementWidget) {
        this.children.add(advancementWidget);
    }
    
    public void a(final int integer1, final int integer2, final float float3, final int integer4, final int integer5) {
        final boolean boolean6 = integer4 + integer1 + this.xPos + this.g + 26 >= this.tree.g().width;
        final String string7 = (this.l == null) ? null : this.l.getProgressBarFraction();
        final int integer6 = (string7 == null) ? 0 : this.client.textRenderer.getStringWidth(string7);
        final int n = 113 - integer2 - this.yPos - 26;
        final int n2 = 6;
        final int size = this.h.size();
        this.client.textRenderer.getClass();
        final boolean boolean7 = n <= n2 + size * 9;
        final float float4 = (this.l == null) ? 0.0f : this.l.getProgressBarPercentage();
        int integer7 = MathHelper.floor(float4 * this.g);
        AchievementObtainedStatus achievementObtainedStatus11;
        AchievementObtainedStatus achievementObtainedStatus12;
        AchievementObtainedStatus achievementObtainedStatus13;
        if (float4 >= 1.0f) {
            integer7 = this.g / 2;
            achievementObtainedStatus11 = AchievementObtainedStatus.a;
            achievementObtainedStatus12 = AchievementObtainedStatus.a;
            achievementObtainedStatus13 = AchievementObtainedStatus.a;
        }
        else if (integer7 < 2) {
            integer7 = this.g / 2;
            achievementObtainedStatus11 = AchievementObtainedStatus.b;
            achievementObtainedStatus12 = AchievementObtainedStatus.b;
            achievementObtainedStatus13 = AchievementObtainedStatus.b;
        }
        else if (integer7 > this.g - 2) {
            integer7 = this.g / 2;
            achievementObtainedStatus11 = AchievementObtainedStatus.a;
            achievementObtainedStatus12 = AchievementObtainedStatus.a;
            achievementObtainedStatus13 = AchievementObtainedStatus.b;
        }
        else {
            achievementObtainedStatus11 = AchievementObtainedStatus.a;
            achievementObtainedStatus12 = AchievementObtainedStatus.b;
            achievementObtainedStatus13 = AchievementObtainedStatus.b;
        }
        final int integer8 = this.g - integer7;
        this.client.getTextureManager().bindTexture(AdvancementWidget.WIDGETS_TEX);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.enableBlend();
        final int integer9 = integer2 + this.yPos;
        int integer10;
        if (boolean6) {
            integer10 = integer1 + this.xPos - this.g + 26 + 6;
        }
        else {
            integer10 = integer1 + this.xPos;
        }
        final int n3 = 32;
        final int size2 = this.h.size();
        this.client.textRenderer.getClass();
        final int integer11 = n3 + size2 * 9;
        if (!this.h.isEmpty()) {
            if (boolean7) {
                this.a(integer10, integer9 + 26 - integer11, this.g, integer11, 10, 200, 26, 0, 52);
            }
            else {
                this.a(integer10, integer9, this.g, integer11, 10, 200, 26, 0, 52);
            }
        }
        this.blit(integer10, integer9, 0, achievementObtainedStatus11.getSpriteIndex() * 26, integer7, 26);
        this.blit(integer10 + integer7, integer9, 200 - integer8, achievementObtainedStatus12.getSpriteIndex() * 26, integer8, 26);
        this.blit(integer1 + this.xPos + 3, integer2 + this.yPos, this.display.getFrame().texV(), 128 + achievementObtainedStatus13.getSpriteIndex() * 26, 26, 26);
        if (boolean6) {
            this.client.textRenderer.drawWithShadow(this.f, (float)(integer10 + 5), (float)(integer2 + this.yPos + 9), -1);
            if (string7 != null) {
                this.client.textRenderer.drawWithShadow(string7, (float)(integer1 + this.xPos - integer6), (float)(integer2 + this.yPos + 9), -1);
            }
        }
        else {
            this.client.textRenderer.drawWithShadow(this.f, (float)(integer1 + this.xPos + 32), (float)(integer2 + this.yPos + 9), -1);
            if (string7 != null) {
                this.client.textRenderer.drawWithShadow(string7, (float)(integer1 + this.xPos + this.g - integer6 - 5), (float)(integer2 + this.yPos + 9), -1);
            }
        }
        if (boolean7) {
            for (int integer12 = 0; integer12 < this.h.size(); ++integer12) {
                final TextRenderer textRenderer = this.client.textRenderer;
                final String string8 = this.h.get(integer12);
                final float x = (float)(integer10 + 5);
                final int n4 = integer9 + 26 - integer11 + 7;
                final int n5 = integer12;
                this.client.textRenderer.getClass();
                textRenderer.draw(string8, x, (float)(n4 + n5 * 9), -5592406);
            }
        }
        else {
            for (int integer12 = 0; integer12 < this.h.size(); ++integer12) {
                final TextRenderer textRenderer2 = this.client.textRenderer;
                final String string9 = this.h.get(integer12);
                final float x2 = (float)(integer10 + 5);
                final int n6 = integer2 + this.yPos + 9 + 17;
                final int n7 = integer12;
                this.client.textRenderer.getClass();
                textRenderer2.draw(string9, x2, (float)(n6 + n7 * 9), -5592406);
            }
        }
        GuiLighting.enableForItems();
        this.client.getItemRenderer().renderGuiItem(null, this.display.getIcon(), integer1 + this.xPos + 8, integer2 + this.yPos + 5);
    }
    
    protected void a(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6, final int integer7, final int integer8, final int integer9) {
        this.blit(integer1, integer2, integer8, integer9, integer5, integer5);
        this.a(integer1 + integer5, integer2, integer3 - integer5 - integer5, integer5, integer8 + integer5, integer9, integer6 - integer5 - integer5, integer7);
        this.blit(integer1 + integer3 - integer5, integer2, integer8 + integer6 - integer5, integer9, integer5, integer5);
        this.blit(integer1, integer2 + integer4 - integer5, integer8, integer9 + integer7 - integer5, integer5, integer5);
        this.a(integer1 + integer5, integer2 + integer4 - integer5, integer3 - integer5 - integer5, integer5, integer8 + integer5, integer9 + integer7 - integer5, integer6 - integer5 - integer5, integer7);
        this.blit(integer1 + integer3 - integer5, integer2 + integer4 - integer5, integer8 + integer6 - integer5, integer9 + integer7 - integer5, integer5, integer5);
        this.a(integer1, integer2 + integer5, integer5, integer4 - integer5 - integer5, integer8, integer9 + integer5, integer6, integer7 - integer5 - integer5);
        this.a(integer1 + integer5, integer2 + integer5, integer3 - integer5 - integer5, integer4 - integer5 - integer5, integer8 + integer5, integer9 + integer5, integer6 - integer5 - integer5, integer7 - integer5 - integer5);
        this.a(integer1 + integer3 - integer5, integer2 + integer5, integer5, integer4 - integer5 - integer5, integer8 + integer6 - integer5, integer9 + integer5, integer6, integer7 - integer5 - integer5);
    }
    
    protected void a(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6, final int integer7, final int integer8) {
        for (int integer9 = 0; integer9 < integer3; integer9 += integer7) {
            final int integer10 = integer1 + integer9;
            final int integer11 = Math.min(integer7, integer3 - integer9);
            for (int integer12 = 0; integer12 < integer4; integer12 += integer8) {
                final int integer13 = integer2 + integer12;
                final int integer14 = Math.min(integer8, integer4 - integer12);
                this.blit(integer10, integer13, integer5, integer6, integer11, integer14);
            }
        }
    }
    
    public boolean a(final int integer1, final int integer2, final int integer3, final int integer4) {
        if (this.display.isHidden() && (this.l == null || !this.l.isDone())) {
            return false;
        }
        final int integer5 = integer1 + this.xPos;
        final int integer6 = integer5 + 26;
        final int integer7 = integer2 + this.yPos;
        final int integer8 = integer7 + 26;
        return integer3 >= integer5 && integer3 <= integer6 && integer4 >= integer7 && integer4 <= integer8;
    }
    
    public void b() {
        if (this.j == null && this.advancement.getParent() != null) {
            this.j = this.getRootWidget(this.advancement);
            if (this.j != null) {
                this.j.a(this);
            }
        }
    }
    
    public int c() {
        return this.yPos;
    }
    
    public int d() {
        return this.xPos;
    }
    
    static {
        WIDGETS_TEX = new Identifier("textures/gui/advancements/widgets.png");
        b = Pattern.compile("(.+) \\S+");
    }
}

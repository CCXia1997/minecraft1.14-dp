package net.minecraft.client.gui.hud;

import org.apache.logging.log4j.LogManager;
import net.minecraft.client.gui.ingame.ChatScreen;
import javax.annotation.Nullable;
import net.minecraft.text.StringTextComponent;
import java.util.Iterator;
import net.minecraft.client.util.TextComponentUtil;
import net.minecraft.text.TextComponent;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.options.ChatVisibility;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import org.apache.logging.log4j.Logger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;

@Environment(EnvType.CLIENT)
public class ChatHud extends DrawableHelper
{
    private static final Logger LOGGER;
    private final MinecraftClient client;
    private final List<String> c;
    private final List<ChatHudLine> messages;
    private final List<ChatHudLine> visibleMessages;
    private int f;
    private boolean g;
    
    public ChatHud(final MinecraftClient client) {
        this.c = Lists.newArrayList();
        this.messages = Lists.newArrayList();
        this.visibleMessages = Lists.newArrayList();
        this.client = client;
    }
    
    public void draw(final int integer) {
        if (this.client.options.chatVisibility == ChatVisibility.HIDDEN) {
            return;
        }
        final int integer2 = this.getVisibleLineCount();
        final int integer3 = this.visibleMessages.size();
        if (integer3 <= 0) {
            return;
        }
        boolean boolean4 = false;
        if (this.isChatFocused()) {
            boolean4 = true;
        }
        final double double5 = this.getScale();
        final int integer4 = MathHelper.ceil(this.getWidth() / double5);
        GlStateManager.pushMatrix();
        GlStateManager.translatef(2.0f, 8.0f, 0.0f);
        GlStateManager.scaled(double5, double5, 1.0);
        final double double6 = this.client.options.chatOpacity * 0.8999999761581421 + 0.10000000149011612;
        final double double7 = this.client.options.textBackgroundOpacity;
        int integer5 = 0;
        for (int integer6 = 0; integer6 + this.f < this.visibleMessages.size() && integer6 < integer2; ++integer6) {
            final ChatHudLine chatHudLine14 = this.visibleMessages.get(integer6 + this.f);
            if (chatHudLine14 != null) {
                final int integer7 = integer - chatHudLine14.getTickCreated();
                if (integer7 < 200 || boolean4) {
                    final double double8 = boolean4 ? 1.0 : c(integer7);
                    final int integer8 = (int)(255.0 * double8 * double6);
                    final int integer9 = (int)(255.0 * double8 * double7);
                    ++integer5;
                    if (integer8 > 3) {
                        final int integer10 = 0;
                        final int integer11 = -integer6 * 9;
                        DrawableHelper.fill(-2, integer11 - 9, 0 + integer4 + 4, integer11, integer9 << 24);
                        final String string22 = chatHudLine14.getContents().getFormattedText();
                        GlStateManager.enableBlend();
                        this.client.textRenderer.drawWithShadow(string22, 0.0f, (float)(integer11 - 8), 16777215 + (integer8 << 24));
                        GlStateManager.disableAlphaTest();
                        GlStateManager.disableBlend();
                    }
                }
            }
        }
        if (boolean4) {
            this.client.textRenderer.getClass();
            final int integer6 = 9;
            GlStateManager.translatef(-3.0f, 0.0f, 0.0f);
            final int integer12 = integer3 * integer6 + integer3;
            final int integer7 = integer5 * integer6 + integer5;
            final int integer13 = this.f * integer7 / integer3;
            final int integer14 = integer7 * integer7 / integer12;
            if (integer12 != integer7) {
                final int integer8 = (integer13 > 0) ? 170 : 96;
                final int integer9 = this.g ? 13382451 : 3355562;
                DrawableHelper.fill(0, -integer13, 2, -integer13 - integer14, integer9 + (integer8 << 24));
                DrawableHelper.fill(2, -integer13, 1, -integer13 - integer14, 13421772 + (integer8 << 24));
            }
        }
        GlStateManager.popMatrix();
    }
    
    private static double c(final int integer) {
        double double2 = integer / 200.0;
        double2 = 1.0 - double2;
        double2 *= 10.0;
        double2 = MathHelper.clamp(double2, 0.0, 1.0);
        double2 *= double2;
        return double2;
    }
    
    public void clear(final boolean boolean1) {
        this.visibleMessages.clear();
        this.messages.clear();
        if (boolean1) {
            this.c.clear();
        }
    }
    
    public void addMessage(final TextComponent textComponent) {
        this.addMessage(textComponent, 0);
    }
    
    public void addMessage(final TextComponent message, final int integer) {
        this.addMessage(message, integer, this.client.inGameHud.getTicks(), false);
        ChatHud.LOGGER.info("[CHAT] {}", message.getString().replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n"));
    }
    
    private void addMessage(final TextComponent textComponent, final int integer2, final int integer3, final boolean boolean4) {
        if (integer2 != 0) {
            this.removeMessage(integer2);
        }
        final int integer4 = MathHelper.floor(this.getWidth() / this.getScale());
        final List<TextComponent> list6 = TextComponentUtil.wrapLines(textComponent, integer4, this.client.textRenderer, false, false);
        final boolean boolean5 = this.isChatFocused();
        for (final TextComponent textComponent2 : list6) {
            if (boolean5 && this.f > 0) {
                this.g = true;
                this.a(1.0);
            }
            this.visibleMessages.add(0, new ChatHudLine(integer3, textComponent2, integer2));
        }
        while (this.visibleMessages.size() > 100) {
            this.visibleMessages.remove(this.visibleMessages.size() - 1);
        }
        if (!boolean4) {
            this.messages.add(0, new ChatHudLine(integer3, textComponent, integer2));
            while (this.messages.size() > 100) {
                this.messages.remove(this.messages.size() - 1);
            }
        }
    }
    
    public void reset() {
        this.visibleMessages.clear();
        this.c();
        for (int integer1 = this.messages.size() - 1; integer1 >= 0; --integer1) {
            final ChatHudLine chatHudLine2 = this.messages.get(integer1);
            this.addMessage(chatHudLine2.getContents(), chatHudLine2.getId(), chatHudLine2.getTickCreated(), true);
        }
    }
    
    public List<String> b() {
        return this.c;
    }
    
    public void a(final String string) {
        if (this.c.isEmpty() || !this.c.get(this.c.size() - 1).equals(string)) {
            this.c.add(string);
        }
    }
    
    public void c() {
        this.f = 0;
        this.g = false;
    }
    
    public void a(final double double1) {
        this.f += (int)double1;
        final int integer3 = this.visibleMessages.size();
        if (this.f > integer3 - this.getVisibleLineCount()) {
            this.f = integer3 - this.getVisibleLineCount();
        }
        if (this.f <= 0) {
            this.f = 0;
            this.g = false;
        }
    }
    
    @Nullable
    public TextComponent getTextComponentAt(final double x, final double y) {
        if (!this.isChatFocused()) {
            return null;
        }
        final double double5 = this.getScale();
        double double6 = x - 2.0;
        double double7 = this.client.window.getScaledHeight() - y - 40.0;
        double6 = MathHelper.floor(double6 / double5);
        double7 = MathHelper.floor(double7 / double5);
        if (double6 < 0.0 || double7 < 0.0) {
            return null;
        }
        final int integer11 = Math.min(this.getVisibleLineCount(), this.visibleMessages.size());
        if (double6 <= MathHelper.floor(this.getWidth() / this.getScale())) {
            final double n = double7;
            this.client.textRenderer.getClass();
            if (n < 9 * integer11 + integer11) {
                final double n2 = double7;
                this.client.textRenderer.getClass();
                final int integer12 = (int)(n2 / 9.0 + this.f);
                if (integer12 >= 0 && integer12 < this.visibleMessages.size()) {
                    final ChatHudLine chatHudLine13 = this.visibleMessages.get(integer12);
                    int integer13 = 0;
                    for (final TextComponent textComponent16 : chatHudLine13.getContents()) {
                        if (textComponent16 instanceof StringTextComponent) {
                            integer13 += this.client.textRenderer.getStringWidth(TextComponentUtil.a(((StringTextComponent)textComponent16).getTextField(), false));
                            if (integer13 > double6) {
                                return textComponent16;
                            }
                            continue;
                        }
                    }
                }
                return null;
            }
        }
        return null;
    }
    
    public boolean isChatFocused() {
        return this.client.currentScreen instanceof ChatScreen;
    }
    
    public void removeMessage(final int integer) {
        Iterator<ChatHudLine> iterator2 = this.visibleMessages.iterator();
        while (iterator2.hasNext()) {
            final ChatHudLine chatHudLine3 = iterator2.next();
            if (chatHudLine3.getId() == integer) {
                iterator2.remove();
            }
        }
        iterator2 = this.messages.iterator();
        while (iterator2.hasNext()) {
            final ChatHudLine chatHudLine3 = iterator2.next();
            if (chatHudLine3.getId() == integer) {
                iterator2.remove();
                break;
            }
        }
    }
    
    public int getWidth() {
        return getWidth(this.client.options.chatWidth);
    }
    
    public int getHeight() {
        return getHeight(this.isChatFocused() ? this.client.options.chatHeightFocused : this.client.options.chatHeightUnfocused);
    }
    
    public double getScale() {
        return this.client.options.chatScale;
    }
    
    public static int getWidth(final double widthOption) {
        final int integer3 = 320;
        final int integer4 = 40;
        return MathHelper.floor(widthOption * 280.0 + 40.0);
    }
    
    public static int getHeight(final double heightOption) {
        final int integer3 = 180;
        final int integer4 = 20;
        return MathHelper.floor(heightOption * 160.0 + 20.0);
    }
    
    public int getVisibleLineCount() {
        return this.getHeight() / 9;
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}

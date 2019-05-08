package net.minecraft.realms;

import org.apache.logging.log4j.LogManager;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Element;
import java.util.Set;
import java.util.function.Predicate;
import com.google.common.collect.Sets;
import java.util.Iterator;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import com.google.common.collect.Lists;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.NarratorManager;
import org.apache.logging.log4j.Logger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;

@Environment(EnvType.CLIENT)
public class RealmsScreenProxy extends Screen
{
    private final RealmsScreen screen;
    private static final Logger LOGGER;
    
    public RealmsScreenProxy(final RealmsScreen realmsScreen) {
        super(NarratorManager.a);
        this.screen = realmsScreen;
    }
    
    public RealmsScreen getScreen() {
        return this.screen;
    }
    
    @Override
    public void init(final MinecraftClient client, final int width, final int height) {
        this.screen.init(client, width, height);
        super.init(client, width, height);
    }
    
    public void init() {
        this.screen.init();
        super.init();
    }
    
    public void drawCenteredString(final String text, final int x, final int y, final int integer4) {
        super.drawCenteredString(this.font, text, x, y, integer4);
    }
    
    public void drawString(final String text, final int x, final int y, final int color, final boolean boolean5) {
        if (boolean5) {
            super.drawString(this.font, text, x, y, color);
        }
        else {
            this.font.draw(text, (float)x, (float)y, color);
        }
    }
    
    @Override
    public void blit(final int x, final int y, final int u, final int v, final int width, final int height) {
        this.screen.blit(x, y, u, v, width, height);
        super.blit(x, y, u, v, width, height);
    }
    
    public static void blit(final int integer1, final int integer2, final float float3, final float float4, final int integer5, final int integer6, final int integer7, final int integer8, final int integer9, final int integer10) {
        DrawableHelper.blit(integer1, integer2, integer7, integer8, float3, float4, integer5, integer6, integer9, integer10);
    }
    
    public static void blit(final int x, final int y, final float u, final float v, final int width, final int height, final int texWidth, final int texHeight) {
        DrawableHelper.blit(x, y, u, v, width, height, texWidth, texHeight);
    }
    
    public void fillGradient(final int top, final int left, final int right, final int bottom, final int color1, final int color2) {
        super.fillGradient(top, left, right, bottom, color1, color2);
    }
    
    @Override
    public void renderBackground() {
        super.renderBackground();
    }
    
    @Override
    public boolean isPauseScreen() {
        return super.isPauseScreen();
    }
    
    @Override
    public void renderBackground(final int alpha) {
        super.renderBackground(alpha);
    }
    
    @Override
    public void render(final int mouseX, final int mouseY, final float delta) {
        this.screen.render(mouseX, mouseY, delta);
    }
    
    public void renderTooltip(final ItemStack stack, final int x, final int y) {
        super.renderTooltip(stack, x, y);
    }
    
    @Override
    public void renderTooltip(final String text, final int x, final int y) {
        super.renderTooltip(text, x, y);
    }
    
    @Override
    public void renderTooltip(final List<String> text, final int x, final int y) {
        super.renderTooltip(text, x, y);
    }
    
    @Override
    public void tick() {
        this.screen.tick();
        super.tick();
    }
    
    public int width() {
        return this.width;
    }
    
    public int height() {
        return this.height;
    }
    
    public int fontLineHeight() {
        this.font.getClass();
        return 9;
    }
    
    public int fontWidth(final String string) {
        return this.font.getStringWidth(string);
    }
    
    public void fontDrawShadow(final String text, final int x, final int y, final int integer4) {
        this.font.drawWithShadow(text, (float)x, (float)y, integer4);
    }
    
    public List<String> fontSplit(final String string, final int integer) {
        return this.font.wrapStringToWidthAsList(string, integer);
    }
    
    public void childrenClear() {
        this.children.clear();
    }
    
    public void addWidget(final RealmsGuiEventListener realmsGuiEventListener) {
        if (this.hasWidget(realmsGuiEventListener) || !this.children.add(realmsGuiEventListener.getProxy())) {
            RealmsScreenProxy.LOGGER.error("Tried to add the same widget multiple times: " + realmsGuiEventListener);
        }
    }
    
    public void narrateLabels() {
        final List<String> list1 = this.children.stream().filter(element -> element instanceof RealmsLabelProxy).map(element -> element.getLabel().getText()).collect(Collectors.toList());
        Realms.narrateNow(list1);
    }
    
    public void removeWidget(final RealmsGuiEventListener realmsGuiEventListener) {
        if (!this.hasWidget(realmsGuiEventListener) || !this.children.remove(realmsGuiEventListener.getProxy())) {
            RealmsScreenProxy.LOGGER.error("Tried to add the same widget multiple times: " + realmsGuiEventListener);
        }
    }
    
    public boolean hasWidget(final RealmsGuiEventListener realmsGuiEventListener) {
        return this.children.contains(realmsGuiEventListener.getProxy());
    }
    
    public void buttonsAdd(final AbstractRealmsButton<?> abstractRealmsButton) {
        this.addButton(abstractRealmsButton.getProxy());
    }
    
    public List<AbstractRealmsButton<?>> buttons() {
        final List<AbstractRealmsButton<?>> list1 = Lists.newArrayListWithExpectedSize(this.buttons.size());
        for (final AbstractButtonWidget abstractButtonWidget3 : this.buttons) {
            list1.add(((RealmsAbstractButtonProxy)abstractButtonWidget3).getButton());
        }
        return list1;
    }
    
    public void buttonsClear() {
        final Set<Element> set1 = Sets.newHashSet(this.buttons);
        this.children.removeIf(set1::contains);
        this.buttons.clear();
    }
    
    public void removeButton(final RealmsButton realmsButton) {
        this.children.remove(realmsButton.getProxy());
        this.buttons.remove(realmsButton.getProxy());
    }
    
    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        return this.screen.mouseClicked(mouseX, mouseY, button) || super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseReleased(final double mouseX, final double mouseY, final int button) {
        return this.screen.mouseReleased(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseDragged(final double mouseX, final double mouseY, final int button, final double deltaX, final double deltaY) {
        return this.screen.mouseDragged(mouseX, mouseY, button, deltaX, deltaY) || super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }
    
    @Override
    public boolean keyPressed(final int keyCode, final int scanCode, final int modifiers) {
        return this.screen.keyPressed(keyCode, scanCode, modifiers) || super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    @Override
    public boolean charTyped(final char chr, final int keyCode) {
        return this.screen.charTyped(chr, keyCode) || super.charTyped(chr, keyCode);
    }
    
    @Override
    public void removed() {
        this.screen.removed();
        super.removed();
    }
    
    public int draw(final String string, final int integer2, final int integer3, final int integer4, final boolean boolean5) {
        if (boolean5) {
            return this.font.drawWithShadow(string, (float)integer2, (float)integer3, integer4);
        }
        return this.font.draw(string, (float)integer2, (float)integer3, integer4);
    }
    
    public TextRenderer getFont() {
        return this.font;
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}

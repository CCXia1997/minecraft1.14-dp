package net.minecraft.client.gui.menu;

import javax.annotation.Nullable;
import net.minecraft.client.gui.widget.AdvancementWidget;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.font.TextRenderer;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.gui.DrawableHelper;
import java.util.Iterator;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.Packet;
import net.minecraft.server.network.packet.AdvancementTabC2SPacket;
import com.google.common.collect.Maps;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.gui.widget.AdvancementTreeWidget;
import net.minecraft.advancement.Advancement;
import java.util.Map;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientAdvancementManager;
import net.minecraft.client.gui.Screen;

@Environment(EnvType.CLIENT)
public class AdvancementsScreen extends Screen implements ClientAdvancementManager.Listener
{
    private static final Identifier WINDOW_TEXTURE;
    private static final Identifier TABS_TEXTURE;
    private final ClientAdvancementManager advancementHandler;
    private final Map<Advancement, AdvancementTreeWidget> widgetMap;
    private AdvancementTreeWidget selectedWidget;
    private boolean f;
    
    public AdvancementsScreen(final ClientAdvancementManager clientAdvancementManager) {
        super(NarratorManager.a);
        this.widgetMap = Maps.newLinkedHashMap();
        this.advancementHandler = clientAdvancementManager;
    }
    
    @Override
    protected void init() {
        this.widgetMap.clear();
        this.selectedWidget = null;
        this.advancementHandler.setListener(this);
        if (this.selectedWidget == null && !this.widgetMap.isEmpty()) {
            this.advancementHandler.selectTab(this.widgetMap.values().iterator().next().c(), true);
        }
        else {
            this.advancementHandler.selectTab((this.selectedWidget == null) ? null : this.selectedWidget.c(), true);
        }
    }
    
    @Override
    public void removed() {
        this.advancementHandler.setListener(null);
        final ClientPlayNetworkHandler clientPlayNetworkHandler1 = this.minecraft.getNetworkHandler();
        if (clientPlayNetworkHandler1 != null) {
            clientPlayNetworkHandler1.sendPacket(AdvancementTabC2SPacket.close());
        }
    }
    
    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        if (button == 0) {
            final int integer6 = (this.width - 252) / 2;
            final int integer7 = (this.height - 140) / 2;
            for (final AdvancementTreeWidget advancementTreeWidget9 : this.widgetMap.values()) {
                if (advancementTreeWidget9.a(integer6, integer7, mouseX, mouseY)) {
                    this.advancementHandler.selectTab(advancementTreeWidget9.c(), true);
                    break;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean keyPressed(final int keyCode, final int scanCode, final int modifiers) {
        if (this.minecraft.options.keyAdvancements.matchesKey(keyCode, scanCode)) {
            this.minecraft.openScreen(null);
            this.minecraft.mouse.lockCursor();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    @Override
    public void render(final int mouseX, final int mouseY, final float delta) {
        final int integer4 = (this.width - 252) / 2;
        final int integer5 = (this.height - 140) / 2;
        this.renderBackground();
        this.drawAdvancementTree(mouseX, mouseY, integer4, integer5);
        this.drawWidgets(integer4, integer5);
        this.drawWidgetTooltip(mouseX, mouseY, integer4, integer5);
    }
    
    @Override
    public boolean mouseDragged(final double mouseX, final double mouseY, final int button, final double deltaX, final double deltaY) {
        if (button != 0) {
            return this.f = false;
        }
        if (!this.f) {
            this.f = true;
        }
        else if (this.selectedWidget != null) {
            this.selectedWidget.a(deltaX, deltaY);
        }
        return true;
    }
    
    private void drawAdvancementTree(final int mouseX, final int mouseY, final int x, final int integer4) {
        final AdvancementTreeWidget advancementTreeWidget5 = this.selectedWidget;
        if (advancementTreeWidget5 == null) {
            DrawableHelper.fill(x + 9, integer4 + 18, x + 9 + 234, integer4 + 18 + 113, -16777216);
            final String string6 = I18n.translate("advancements.empty");
            final int integer5 = this.font.getStringWidth(string6);
            final TextRenderer font = this.font;
            final String string7 = string6;
            final float x2 = (float)(x + 9 + 117 - integer5 / 2);
            final int n = integer4 + 18 + 56;
            this.font.getClass();
            font.draw(string7, x2, (float)(n - 9 / 2), -1);
            final TextRenderer font2 = this.font;
            final String string8 = ":(";
            final float x3 = (float)(x + 9 + 117 - this.font.getStringWidth(":(") / 2);
            final int n2 = integer4 + 18 + 113;
            this.font.getClass();
            font2.draw(string8, x3, (float)(n2 - 9), -1);
            return;
        }
        GlStateManager.pushMatrix();
        GlStateManager.translatef((float)(x + 9), (float)(integer4 + 18), -400.0f);
        GlStateManager.enableDepthTest();
        advancementTreeWidget5.f();
        GlStateManager.popMatrix();
        GlStateManager.depthFunc(515);
        GlStateManager.disableDepthTest();
    }
    
    public void drawWidgets(final int x, final int integer2) {
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.enableBlend();
        GuiLighting.disable();
        this.minecraft.getTextureManager().bindTexture(AdvancementsScreen.WINDOW_TEXTURE);
        this.blit(x, integer2, 0, 0, 252, 140);
        if (this.widgetMap.size() > 1) {
            this.minecraft.getTextureManager().bindTexture(AdvancementsScreen.TABS_TEXTURE);
            for (final AdvancementTreeWidget advancementTreeWidget4 : this.widgetMap.values()) {
                advancementTreeWidget4.drawBackground(x, integer2, advancementTreeWidget4 == this.selectedWidget);
            }
            GlStateManager.enableRescaleNormal();
            GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GuiLighting.enableForItems();
            for (final AdvancementTreeWidget advancementTreeWidget4 : this.widgetMap.values()) {
                advancementTreeWidget4.drawIcon(x, integer2, this.itemRenderer);
            }
            GlStateManager.disableBlend();
        }
        this.font.draw(I18n.translate("gui.advancements"), (float)(x + 8), (float)(integer2 + 6), 4210752);
    }
    
    private void drawWidgetTooltip(final int mouseX, final int mouseY, final int x, final int integer4) {
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        if (this.selectedWidget != null) {
            GlStateManager.pushMatrix();
            GlStateManager.enableDepthTest();
            GlStateManager.translatef((float)(x + 9), (float)(integer4 + 18), 400.0f);
            this.selectedWidget.a(mouseX - x - 9, mouseY - integer4 - 18, x, integer4);
            GlStateManager.disableDepthTest();
            GlStateManager.popMatrix();
        }
        if (this.widgetMap.size() > 1) {
            for (final AdvancementTreeWidget advancementTreeWidget6 : this.widgetMap.values()) {
                if (advancementTreeWidget6.a(x, integer4, mouseX, (double)mouseY)) {
                    this.renderTooltip(advancementTreeWidget6.d(), mouseX, mouseY);
                }
            }
        }
    }
    
    @Override
    public void onRootAdded(final Advancement advancement) {
        final AdvancementTreeWidget advancementTreeWidget2 = AdvancementTreeWidget.create(this.minecraft, this, this.widgetMap.size(), advancement);
        if (advancementTreeWidget2 == null) {
            return;
        }
        this.widgetMap.put(advancement, advancementTreeWidget2);
    }
    
    @Override
    public void onRootRemoved(final Advancement advancement) {
    }
    
    @Override
    public void onDependentAdded(final Advancement advancement) {
        final AdvancementTreeWidget advancementTreeWidget2 = this.getAdvancementTreeWidget(advancement);
        if (advancementTreeWidget2 != null) {
            advancementTreeWidget2.a(advancement);
        }
    }
    
    @Override
    public void onDependentRemoved(final Advancement advancement) {
    }
    
    @Override
    public void setProgress(final Advancement advancement, final AdvancementProgress advancementProgress) {
        final AdvancementWidget advancementWidget3 = this.getAdvancementWidget(advancement);
        if (advancementWidget3 != null) {
            advancementWidget3.setProgress(advancementProgress);
        }
    }
    
    @Override
    public void selectTab(@Nullable final Advancement advancement) {
        this.selectedWidget = this.widgetMap.get(advancement);
    }
    
    @Override
    public void onClear() {
        this.widgetMap.clear();
        this.selectedWidget = null;
    }
    
    @Nullable
    public AdvancementWidget getAdvancementWidget(final Advancement advancement) {
        final AdvancementTreeWidget advancementTreeWidget2 = this.getAdvancementTreeWidget(advancement);
        return (advancementTreeWidget2 == null) ? null : advancementTreeWidget2.getWidgetForAdvancement(advancement);
    }
    
    @Nullable
    private AdvancementTreeWidget getAdvancementTreeWidget(Advancement advancement) {
        while (advancement.getParent() != null) {
            advancement = advancement.getParent();
        }
        return this.widgetMap.get(advancement);
    }
    
    static {
        WINDOW_TEXTURE = new Identifier("textures/gui/advancements/window.png");
        TABS_TEXTURE = new Identifier("textures/gui/advancements/tabs.png");
    }
}

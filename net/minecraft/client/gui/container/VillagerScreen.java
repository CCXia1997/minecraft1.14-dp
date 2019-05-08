package net.minecraft.client.gui.container;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.item.ItemStack;
import java.util.Iterator;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.util.math.MathHelper;
import net.minecraft.village.VillagerData;
import net.minecraft.village.TraderOfferList;
import net.minecraft.village.TradeOffer;
import net.minecraft.client.gui.DrawableHelper;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.network.Packet;
import net.minecraft.server.network.packet.SelectVillagerTradeC2SPacket;
import net.minecraft.text.TextComponent;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.container.MerchantContainer;
import net.minecraft.client.gui.ContainerScreen;

@Environment(EnvType.CLIENT)
public class VillagerScreen extends ContainerScreen<MerchantContainer>
{
    private static final Identifier TEXTURE;
    private int l;
    private final WidgetButtonPage[] m;
    private int n;
    private boolean o;
    
    public VillagerScreen(final MerchantContainer merchantContainer, final PlayerInventory playerInventory, final TextComponent textComponent) {
        super(merchantContainer, playerInventory, textComponent);
        this.m = new WidgetButtonPage[7];
        this.containerWidth = 276;
    }
    
    private void syncRecipeIndex() {
        ((MerchantContainer)this.container).setRecipeIndex(this.l);
        ((MerchantContainer)this.container).switchTo(this.l);
        this.minecraft.getNetworkHandler().sendPacket(new SelectVillagerTradeC2SPacket(this.l));
    }
    
    @Override
    protected void init() {
        super.init();
        final int integer1 = (this.width - this.containerWidth) / 2;
        final int integer2 = (this.height - this.containerHeight) / 2;
        int integer3 = integer2 + 16 + 2;
        for (int integer4 = 0; integer4 < 7; ++integer4) {
            this.m[integer4] = this.<WidgetButtonPage>addButton(new WidgetButtonPage(integer1 + 5, integer3, integer4, buttonWidget -> {
                if (buttonWidget instanceof WidgetButtonPage) {
                    this.l = buttonWidget.a() + this.n;
                    this.syncRecipeIndex();
                }
                return;
            }));
            integer3 += 20;
        }
    }
    
    @Override
    protected void drawForeground(final int mouseX, final int mouseY) {
        final int integer3 = ((MerchantContainer)this.container).getLevelProgress();
        final int integer4 = this.containerHeight - 94;
        if (integer3 > 0 && integer3 <= 5 && ((MerchantContainer)this.container).isLevelled()) {
            final String string5 = this.title.getFormattedText();
            final String string6 = "- " + I18n.translate("merchant.level." + integer3);
            final int integer5 = this.font.getStringWidth(string5);
            final int integer6 = this.font.getStringWidth(string6);
            final int integer7 = integer5 + integer6 + 3;
            final int integer8 = 49 + this.containerWidth / 2 - integer7 / 2;
            this.font.draw(string5, (float)integer8, 6.0f, 4210752);
            this.font.draw(this.playerInventory.getDisplayName().getFormattedText(), 107.0f, (float)integer4, 4210752);
            this.font.draw(string6, (float)(integer8 + integer5 + 3), 6.0f, 4210752);
        }
        else {
            final String string5 = this.title.getFormattedText();
            this.font.draw(string5, (float)(49 + this.containerWidth / 2 - this.font.getStringWidth(string5) / 2), 6.0f, 4210752);
            this.font.draw(this.playerInventory.getDisplayName().getFormattedText(), 107.0f, (float)integer4, 4210752);
        }
        final String string5 = I18n.translate("merchant.trades");
        final int integer9 = this.font.getStringWidth(string5);
        this.font.draw(string5, (float)(5 - integer9 / 2 + 48), 6.0f, 4210752);
    }
    
    @Override
    protected void drawBackground(final float delta, final int mouseX, final int mouseY) {
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bindTexture(VillagerScreen.TEXTURE);
        final int integer4 = (this.width - this.containerWidth) / 2;
        final int integer5 = (this.height - this.containerHeight) / 2;
        DrawableHelper.blit(integer4, integer5, this.blitOffset, 0.0f, 0.0f, this.containerWidth, this.containerHeight, 256, 512);
        final TraderOfferList traderOfferList6 = ((MerchantContainer)this.container).getRecipes();
        if (!traderOfferList6.isEmpty()) {
            final int integer6 = this.l;
            if (integer6 < 0 || integer6 >= traderOfferList6.size()) {
                return;
            }
            final TradeOffer tradeOffer8 = traderOfferList6.get(integer6);
            if (tradeOffer8.isDisabled()) {
                this.minecraft.getTextureManager().bindTexture(VillagerScreen.TEXTURE);
                GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                GlStateManager.disableLighting();
                DrawableHelper.blit(this.left + 83 + 99, this.top + 35, this.blitOffset, 311.0f, 0.0f, 28, 21, 256, 512);
            }
        }
    }
    
    private void a(final int integer1, final int integer2, final TradeOffer tradeOffer) {
        this.minecraft.getTextureManager().bindTexture(VillagerScreen.TEXTURE);
        final int integer3 = ((MerchantContainer)this.container).getLevelProgress();
        final int integer4 = ((MerchantContainer)this.container).getExperience();
        if (integer3 >= 5) {
            return;
        }
        DrawableHelper.blit(integer1 + 136, integer2 + 16, this.blitOffset, 0.0f, 186.0f, 102, 5, 256, 512);
        final int integer5 = VillagerData.getLowerLevelExperience(integer3);
        if (integer4 < integer5 || !VillagerData.canLevelUp(integer3)) {
            return;
        }
        final int integer6 = 100;
        final float float8 = (float)(100 / (VillagerData.getUpperLevelExperience(integer3) - integer5));
        final int integer7 = MathHelper.floor(float8 * (integer4 - integer5));
        DrawableHelper.blit(integer1 + 136, integer2 + 16, this.blitOffset, 0.0f, 191.0f, integer7 + 1, 5, 256, 512);
        final int integer8 = ((MerchantContainer)this.container).getTraderRewardedExperience();
        if (integer8 > 0) {
            final int integer9 = Math.min(MathHelper.floor(integer8 * float8), 100 - integer7);
            DrawableHelper.blit(integer1 + 136 + integer7 + 1, integer2 + 16 + 1, this.blitOffset, 2.0f, 182.0f, integer9, 3, 256, 512);
        }
    }
    
    private void a(final int integer1, final int integer2, final TraderOfferList traderOfferList) {
        GuiLighting.disable();
        final int integer3 = traderOfferList.size() + 1 - 7;
        if (integer3 > 1) {
            final int integer4 = 139 - (27 + (integer3 - 1) * 139 / integer3);
            final int integer5 = 1 + integer4 / integer3 + 139 / integer3;
            int integer6 = this.n * integer5;
            if (this.n == integer3 - 1) {
                integer6 = 113;
            }
            DrawableHelper.blit(integer1 + 94, integer2 + 18 + integer6, this.blitOffset, 0.0f, 199.0f, 6, 27, 256, 512);
        }
        else {
            DrawableHelper.blit(integer1 + 94, integer2 + 18, this.blitOffset, 6.0f, 199.0f, 6, 27, 256, 512);
        }
        GuiLighting.enableForItems();
    }
    
    @Override
    public void render(final int mouseX, final int mouseY, final float delta) {
        this.renderBackground();
        super.render(mouseX, mouseY, delta);
        final TraderOfferList traderOfferList4 = ((MerchantContainer)this.container).getRecipes();
        if (!traderOfferList4.isEmpty()) {
            final int integer5 = (this.width - this.containerWidth) / 2;
            final int integer6 = (this.height - this.containerHeight) / 2;
            int integer7 = integer6 + 16 + 1;
            final int integer8 = integer5 + 5 + 5;
            GlStateManager.pushMatrix();
            GuiLighting.enableForItems();
            GlStateManager.enableRescaleNormal();
            GlStateManager.enableColorMaterial();
            GlStateManager.enableLighting();
            this.minecraft.getTextureManager().bindTexture(VillagerScreen.TEXTURE);
            this.a(integer5, integer6, traderOfferList4);
            int integer9 = 0;
            for (final TradeOffer tradeOffer11 : traderOfferList4) {
                if (this.a(traderOfferList4.size()) && (integer9 < this.n || integer9 >= 7 + this.n)) {
                    ++integer9;
                }
                else {
                    final ItemStack itemStack12 = tradeOffer11.getOriginalFirstBuyItem();
                    final ItemStack itemStack13 = tradeOffer11.getAdjustedFirstBuyItem();
                    final ItemStack itemStack14 = tradeOffer11.getSecondBuyItem();
                    final ItemStack itemStack15 = tradeOffer11.getMutableSellItem();
                    this.itemRenderer.zOffset = 100.0f;
                    final int integer10 = integer7 + 2;
                    this.a(itemStack13, itemStack12, integer8, integer10);
                    if (!itemStack14.isEmpty()) {
                        this.itemRenderer.renderGuiItem(itemStack14, integer5 + 5 + 35, integer10);
                        this.itemRenderer.renderGuiItemOverlay(this.font, itemStack14, integer5 + 5 + 35, integer10);
                    }
                    this.a(tradeOffer11, integer5, integer10);
                    this.itemRenderer.renderGuiItem(itemStack15, integer5 + 5 + 68, integer10);
                    this.itemRenderer.renderGuiItemOverlay(this.font, itemStack15, integer5 + 5 + 68, integer10);
                    this.itemRenderer.zOffset = 0.0f;
                    integer7 += 20;
                    ++integer9;
                }
            }
            final int integer11 = this.l;
            TradeOffer tradeOffer11 = traderOfferList4.get(integer11);
            GlStateManager.disableLighting();
            if (((MerchantContainer)this.container).isLevelled()) {
                this.a(integer5, integer6, tradeOffer11);
            }
            if (tradeOffer11.isDisabled() && this.isPointWithinBounds(186, 35, 22, 21, mouseX, mouseY)) {
                this.renderTooltip(I18n.translate("merchant.deprecated"), mouseX, mouseY);
            }
            for (final WidgetButtonPage widgetButtonPage15 : this.m) {
                if (widgetButtonPage15.isHovered()) {
                    widgetButtonPage15.renderToolTip(mouseX, mouseY);
                }
                widgetButtonPage15.visible = (widgetButtonPage15.a < ((MerchantContainer)this.container).getRecipes().size());
            }
            GlStateManager.popMatrix();
            GlStateManager.enableLighting();
            GlStateManager.enableDepthTest();
            GuiLighting.enable();
        }
        this.drawMouseoverTooltip(mouseX, mouseY);
    }
    
    private void a(final TradeOffer tradeOffer, final int integer2, final int integer3) {
        GuiLighting.disable();
        GlStateManager.enableBlend();
        this.minecraft.getTextureManager().bindTexture(VillagerScreen.TEXTURE);
        if (tradeOffer.isDisabled()) {
            DrawableHelper.blit(integer2 + 5 + 35 + 20, integer3 + 3, this.blitOffset, 25.0f, 171.0f, 10, 9, 256, 512);
        }
        else {
            DrawableHelper.blit(integer2 + 5 + 35 + 20, integer3 + 3, this.blitOffset, 15.0f, 171.0f, 10, 9, 256, 512);
        }
        GuiLighting.enableForItems();
    }
    
    private void a(final ItemStack itemStack1, final ItemStack itemStack2, final int integer3, final int integer4) {
        this.itemRenderer.renderGuiItem(itemStack1, integer3, integer4);
        if (itemStack2.getAmount() == itemStack1.getAmount()) {
            this.itemRenderer.renderGuiItemOverlay(this.font, itemStack1, integer3, integer4);
        }
        else {
            this.itemRenderer.renderGuiItemOverlay(this.font, itemStack2, integer3, integer4, (itemStack2.getAmount() == 1) ? "1" : null);
            this.itemRenderer.renderGuiItemOverlay(this.font, itemStack1, integer3 + 14, integer4, (itemStack1.getAmount() == 1) ? "1" : null);
            this.minecraft.getTextureManager().bindTexture(VillagerScreen.TEXTURE);
            this.blitOffset += 300;
            GuiLighting.disable();
            DrawableHelper.blit(integer3 + 7, integer4 + 12, this.blitOffset, 0.0f, 176.0f, 9, 2, 256, 512);
            GuiLighting.enableForItems();
            this.blitOffset -= 300;
        }
    }
    
    private boolean a(final int integer) {
        return integer > 7;
    }
    
    @Override
    public boolean mouseScrolled(final double mouseX, final double mouseY, final double amount) {
        final int integer7 = ((MerchantContainer)this.container).getRecipes().size();
        if (this.a(integer7)) {
            final int integer8 = integer7 - 7;
            this.n -= (int)amount;
            this.n = MathHelper.clamp(this.n, 0, integer8);
        }
        return true;
    }
    
    @Override
    public boolean mouseDragged(final double mouseX, final double mouseY, final int button, final double deltaX, final double deltaY) {
        final int integer10 = ((MerchantContainer)this.container).getRecipes().size();
        if (this.o) {
            final int integer11 = this.top + 18;
            final int integer12 = integer11 + 139;
            final int integer13 = integer10 - 7;
            float float14 = ((float)mouseY - integer11 - 13.5f) / (integer12 - integer11 - 27.0f);
            float14 = float14 * integer13 + 0.5f;
            this.n = MathHelper.clamp((int)float14, 0, integer13);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }
    
    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        this.o = false;
        final int integer6 = (this.width - this.containerWidth) / 2;
        final int integer7 = (this.height - this.containerHeight) / 2;
        if (this.a(((MerchantContainer)this.container).getRecipes().size()) && mouseX > integer6 + 94 && mouseX < integer6 + 94 + 6 && mouseY > integer7 + 18 && mouseY <= integer7 + 18 + 139 + 1) {
            this.o = true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    static {
        TEXTURE = new Identifier("textures/gui/container/villager2.png");
    }
    
    @Environment(EnvType.CLIENT)
    class WidgetButtonPage extends ButtonWidget
    {
        final int a;
        
        public WidgetButtonPage(final int integer2, final int integer3, final int integer4, final PressAction pressAction) {
            super(integer2, integer3, 89, 20, "", pressAction);
            this.a = integer4;
            this.visible = false;
        }
        
        public int a() {
            return this.a;
        }
        
        @Override
        public void renderToolTip(final int mouseX, final int mouseY) {
            if (this.isHovered && ((MerchantContainer)VillagerScreen.this.container).getRecipes().size() > this.a + VillagerScreen.this.n) {
                if (mouseX < this.x + 20) {
                    final ItemStack itemStack3 = ((MerchantContainer)VillagerScreen.this.container).getRecipes().get(this.a + VillagerScreen.this.n).getAdjustedFirstBuyItem();
                    Screen.this.renderTooltip(itemStack3, mouseX, mouseY);
                }
                else if (mouseX < this.x + 50 && mouseX > this.x + 30) {
                    final ItemStack itemStack3 = ((MerchantContainer)VillagerScreen.this.container).getRecipes().get(this.a + VillagerScreen.this.n).getSecondBuyItem();
                    if (!itemStack3.isEmpty()) {
                        Screen.this.renderTooltip(itemStack3, mouseX, mouseY);
                    }
                }
                else if (mouseX > this.x + 65) {
                    final ItemStack itemStack3 = ((MerchantContainer)VillagerScreen.this.container).getRecipes().get(this.a + VillagerScreen.this.n).getMutableSellItem();
                    Screen.this.renderTooltip(itemStack3, mouseX, mouseY);
                }
            }
        }
    }
}

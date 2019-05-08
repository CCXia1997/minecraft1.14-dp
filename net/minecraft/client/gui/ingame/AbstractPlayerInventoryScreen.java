package net.minecraft.client.gui.ingame;

import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.client.texture.StatusEffectSpriteManager;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.texture.SpriteAtlasTexture;
import java.util.Iterator;
import java.util.Collection;
import net.minecraft.entity.effect.StatusEffectInstance;
import com.google.common.collect.Ordering;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.text.TextComponent;
import net.minecraft.entity.player.PlayerInventory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ContainerScreen;
import net.minecraft.container.Container;

@Environment(EnvType.CLIENT)
public abstract class AbstractPlayerInventoryScreen<T extends Container> extends ContainerScreen<T>
{
    protected boolean offsetGuiForEffects;
    
    public AbstractPlayerInventoryScreen(final T container, final PlayerInventory playerInventory, final TextComponent name) {
        super(container, playerInventory, name);
    }
    
    @Override
    protected void init() {
        super.init();
        this.b();
    }
    
    protected void b() {
        if (this.minecraft.player.getStatusEffects().isEmpty()) {
            this.left = (this.width - this.containerWidth) / 2;
            this.offsetGuiForEffects = false;
        }
        else {
            this.left = 160 + (this.width - this.containerWidth - 200) / 2;
            this.offsetGuiForEffects = true;
        }
    }
    
    @Override
    public void render(final int mouseX, final int mouseY, final float delta) {
        super.render(mouseX, mouseY, delta);
        if (this.offsetGuiForEffects) {
            this.drawPotionEffects();
        }
    }
    
    private void drawPotionEffects() {
        final int integer1 = this.left - 124;
        final Collection<StatusEffectInstance> collection2 = this.minecraft.player.getStatusEffects();
        if (collection2.isEmpty()) {
            return;
        }
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.disableLighting();
        int integer2 = 33;
        if (collection2.size() > 5) {
            integer2 = 132 / (collection2.size() - 1);
        }
        final Iterable<StatusEffectInstance> iterable4 = Ordering.<Comparable>natural().<StatusEffectInstance>sortedCopy(collection2);
        this.a(integer1, integer2, iterable4);
        this.b(integer1, integer2, iterable4);
        this.c(integer1, integer2, iterable4);
    }
    
    private void a(final int integer1, final int integer2, final Iterable<StatusEffectInstance> iterable) {
        this.minecraft.getTextureManager().bindTexture(AbstractPlayerInventoryScreen.BACKGROUND_TEXTURE);
        int integer3 = this.top;
        for (final StatusEffectInstance statusEffectInstance6 : iterable) {
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            this.blit(integer1, integer3, 0, 166, 140, 32);
            integer3 += integer2;
        }
    }
    
    private void b(final int integer1, final int integer2, final Iterable<StatusEffectInstance> iterable) {
        this.minecraft.getTextureManager().bindTexture(SpriteAtlasTexture.STATUS_EFFECT_ATLAS_TEX);
        final StatusEffectSpriteManager statusEffectSpriteManager4 = this.minecraft.getStatusEffectSpriteManager();
        int integer3 = this.top;
        for (final StatusEffectInstance statusEffectInstance7 : iterable) {
            final StatusEffect statusEffect8 = statusEffectInstance7.getEffectType();
            DrawableHelper.blit(integer1 + 6, integer3 + 7, this.blitOffset, 18, 18, statusEffectSpriteManager4.getSprite(statusEffect8));
            integer3 += integer2;
        }
    }
    
    private void c(final int integer1, final int integer2, final Iterable<StatusEffectInstance> iterable) {
        int integer3 = this.top;
        for (final StatusEffectInstance statusEffectInstance6 : iterable) {
            String string7 = I18n.translate(statusEffectInstance6.getEffectType().getTranslationKey());
            if (statusEffectInstance6.getAmplifier() >= 1 && statusEffectInstance6.getAmplifier() <= 9) {
                string7 = string7 + ' ' + I18n.translate("enchantment.level." + (statusEffectInstance6.getAmplifier() + 1));
            }
            this.font.drawWithShadow(string7, (float)(integer1 + 10 + 18), (float)(integer3 + 6), 16777215);
            final String string8 = StatusEffectUtil.durationToString(statusEffectInstance6, 1.0f);
            this.font.drawWithShadow(string8, (float)(integer1 + 10 + 18), (float)(integer3 + 6 + 10), 8355711);
            integer3 += integer2;
        }
    }
}

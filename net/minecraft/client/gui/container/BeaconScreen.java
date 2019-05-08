package net.minecraft.client.gui.container;

import net.minecraft.client.gui.Screen;
import net.minecraft.server.network.packet.GuiCloseC2SPacket;
import net.minecraft.network.Packet;
import net.minecraft.server.network.packet.UpdateBeaconC2SPacket;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.gui.widget.AbstractPressableButtonWidget;
import net.minecraft.client.MinecraftClient;
import java.util.List;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.Items;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Iterator;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;
import net.minecraft.container.Container;
import net.minecraft.container.ContainerListener;
import net.minecraft.text.TextComponent;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.container.BeaconContainer;
import net.minecraft.client.gui.ContainerScreen;

@Environment(EnvType.CLIENT)
public class BeaconScreen extends ContainerScreen<BeaconContainer>
{
    private static final Identifier BG_TEX;
    private WidgetButtonIconDone doneButton;
    private boolean canConsumeGem;
    private StatusEffect primaryEffect;
    private StatusEffect secondaryEffect;
    
    public BeaconScreen(final BeaconContainer beaconContainer, final PlayerInventory playerInventory, final TextComponent textComponent) {
        super(beaconContainer, playerInventory, textComponent);
        this.containerWidth = 230;
        this.containerHeight = 219;
        beaconContainer.addListener(new ContainerListener() {
            @Override
            public void onContainerRegistered(final Container container, final DefaultedList<ItemStack> defaultedList) {
            }
            
            @Override
            public void onContainerSlotUpdate(final Container container, final int slotId, final ItemStack itemStack) {
            }
            
            @Override
            public void onContainerPropertyUpdate(final Container container, final int propertyId, final int integer3) {
                BeaconScreen.this.primaryEffect = beaconContainer.getPrimaryEffect();
                BeaconScreen.this.secondaryEffect = beaconContainer.getSecondaryEffect();
                BeaconScreen.this.canConsumeGem = true;
            }
        });
    }
    
    @Override
    protected void init() {
        super.init();
        this.doneButton = this.<WidgetButtonIconDone>addButton(new WidgetButtonIconDone(this.left + 164, this.top + 107));
        this.<WidgetButtonIconCancel>addButton(new WidgetButtonIconCancel(this.left + 190, this.top + 107));
        this.canConsumeGem = true;
        this.doneButton.active = false;
    }
    
    @Override
    public void tick() {
        super.tick();
        final int integer1 = ((BeaconContainer)this.container).getProperties();
        if (this.canConsumeGem && integer1 >= 0) {
            this.canConsumeGem = false;
            for (int integer2 = 0; integer2 <= 2; ++integer2) {
                final int integer3 = BeaconBlockEntity.EFFECTS_BY_LEVEL[integer2].length;
                final int integer4 = integer3 * 22 + (integer3 - 1) * 2;
                for (int integer5 = 0; integer5 < integer3; ++integer5) {
                    final StatusEffect statusEffect6 = BeaconBlockEntity.EFFECTS_BY_LEVEL[integer2][integer5];
                    final WidgetButtonIconEffect widgetButtonIconEffect7 = new WidgetButtonIconEffect(this.left + 76 + integer5 * 24 - integer4 / 2, this.top + 22 + integer2 * 25, statusEffect6, true);
                    this.<WidgetButtonIconEffect>addButton(widgetButtonIconEffect7);
                    if (integer2 >= integer1) {
                        widgetButtonIconEffect7.active = false;
                    }
                    else if (statusEffect6 == this.primaryEffect) {
                        widgetButtonIconEffect7.setDisabled(true);
                    }
                }
            }
            int integer2 = 3;
            final int integer3 = BeaconBlockEntity.EFFECTS_BY_LEVEL[3].length + 1;
            final int integer4 = integer3 * 22 + (integer3 - 1) * 2;
            for (int integer5 = 0; integer5 < integer3 - 1; ++integer5) {
                final StatusEffect statusEffect6 = BeaconBlockEntity.EFFECTS_BY_LEVEL[3][integer5];
                final WidgetButtonIconEffect widgetButtonIconEffect7 = new WidgetButtonIconEffect(this.left + 167 + integer5 * 24 - integer4 / 2, this.top + 47, statusEffect6, false);
                this.<WidgetButtonIconEffect>addButton(widgetButtonIconEffect7);
                if (3 >= integer1) {
                    widgetButtonIconEffect7.active = false;
                }
                else if (statusEffect6 == this.secondaryEffect) {
                    widgetButtonIconEffect7.setDisabled(true);
                }
            }
            if (this.primaryEffect != null) {
                final WidgetButtonIconEffect widgetButtonIconEffect8 = new WidgetButtonIconEffect(this.left + 167 + (integer3 - 1) * 24 - integer4 / 2, this.top + 47, this.primaryEffect, false);
                this.<WidgetButtonIconEffect>addButton(widgetButtonIconEffect8);
                if (3 >= integer1) {
                    widgetButtonIconEffect8.active = false;
                }
                else if (this.primaryEffect == this.secondaryEffect) {
                    widgetButtonIconEffect8.setDisabled(true);
                }
            }
        }
        this.doneButton.active = (((BeaconContainer)this.container).hasPayment() && this.primaryEffect != null);
    }
    
    @Override
    protected void drawForeground(final int mouseX, final int mouseY) {
        GuiLighting.disable();
        this.drawCenteredString(this.font, I18n.translate("block.minecraft.beacon.primary"), 62, 10, 14737632);
        this.drawCenteredString(this.font, I18n.translate("block.minecraft.beacon.secondary"), 169, 10, 14737632);
        for (final AbstractButtonWidget abstractButtonWidget4 : this.buttons) {
            if (abstractButtonWidget4.isHovered()) {
                abstractButtonWidget4.renderToolTip(mouseX - this.left, mouseY - this.top);
                break;
            }
        }
        GuiLighting.enableForItems();
    }
    
    @Override
    protected void drawBackground(final float delta, final int mouseX, final int mouseY) {
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bindTexture(BeaconScreen.BG_TEX);
        final int integer4 = (this.width - this.containerWidth) / 2;
        final int integer5 = (this.height - this.containerHeight) / 2;
        this.blit(integer4, integer5, 0, 0, this.containerWidth, this.containerHeight);
        this.itemRenderer.zOffset = 100.0f;
        this.itemRenderer.renderGuiItem(new ItemStack(Items.nF), integer4 + 42, integer5 + 109);
        this.itemRenderer.renderGuiItem(new ItemStack(Items.jj), integer4 + 42 + 22, integer5 + 109);
        this.itemRenderer.renderGuiItem(new ItemStack(Items.jl), integer4 + 42 + 44, integer5 + 109);
        this.itemRenderer.renderGuiItem(new ItemStack(Items.jk), integer4 + 42 + 66, integer5 + 109);
        this.itemRenderer.zOffset = 0.0f;
    }
    
    @Override
    public void render(final int mouseX, final int mouseY, final float delta) {
        this.renderBackground();
        super.render(mouseX, mouseY, delta);
        this.drawMouseoverTooltip(mouseX, mouseY);
    }
    
    static {
        BG_TEX = new Identifier("textures/gui/container/beacon.png");
    }
    
    @Environment(EnvType.CLIENT)
    abstract static class WidgetButtonIcon extends AbstractPressableButtonWidget
    {
        private boolean disabled;
        
        protected WidgetButtonIcon(final int integer1, final int integer2) {
            super(integer1, integer2, 22, 22, "");
        }
        
        @Override
        public void renderButton(final int mouseX, final int mouseY, final float delta) {
            MinecraftClient.getInstance().getTextureManager().bindTexture(BeaconScreen.BG_TEX);
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            final int integer4 = 219;
            int integer5 = 0;
            if (!this.active) {
                integer5 += this.width * 2;
            }
            else if (this.disabled) {
                integer5 += this.width * 1;
            }
            else if (this.isHovered()) {
                integer5 += this.width * 3;
            }
            this.blit(this.x, this.y, integer5, 219, this.width, this.height);
            this.a();
        }
        
        protected abstract void a();
        
        public boolean isDisabled() {
            return this.disabled;
        }
        
        public void setDisabled(final boolean boolean1) {
            this.disabled = boolean1;
        }
    }
    
    @Environment(EnvType.CLIENT)
    class WidgetButtonIconEffect extends WidgetButtonIcon
    {
        private final StatusEffect effect;
        private final Sprite c;
        private final boolean primary;
        
        public WidgetButtonIconEffect(final int integer2, final int integer3, final StatusEffect statusEffect, final boolean boolean5) {
            super(integer2, integer3);
            this.effect = statusEffect;
            this.c = MinecraftClient.getInstance().getStatusEffectSpriteManager().getSprite(statusEffect);
            this.primary = boolean5;
        }
        
        @Override
        public void onPress() {
            if (this.isDisabled()) {
                return;
            }
            if (this.primary) {
                BeaconScreen.this.primaryEffect = this.effect;
            }
            else {
                BeaconScreen.this.secondaryEffect = this.effect;
            }
            BeaconScreen.this.buttons.clear();
            BeaconScreen.this.children.clear();
            BeaconScreen.this.init();
            BeaconScreen.this.tick();
        }
        
        @Override
        public void renderToolTip(final int mouseX, final int mouseY) {
            String string3 = I18n.translate(this.effect.getTranslationKey());
            if (!this.primary && this.effect != StatusEffects.j) {
                string3 += " II";
            }
            BeaconScreen.this.renderTooltip(string3, mouseX, mouseY);
        }
        
        @Override
        protected void a() {
            MinecraftClient.getInstance().getTextureManager().bindTexture(SpriteAtlasTexture.STATUS_EFFECT_ATLAS_TEX);
            DrawableHelper.blit(this.x + 2, this.y + 2, this.blitOffset, 18, 18, this.c);
        }
    }
    
    @Environment(EnvType.CLIENT)
    abstract static class e extends WidgetButtonIcon
    {
        private final int a;
        private final int b;
        
        protected e(final int integer1, final int integer2, final int integer3, final int integer4) {
            super(integer1, integer2);
            this.a = integer3;
            this.b = integer4;
        }
        
        @Override
        protected void a() {
            this.blit(this.x + 2, this.y + 2, this.a, this.b, 18, 18);
        }
    }
    
    @Environment(EnvType.CLIENT)
    class WidgetButtonIconDone extends e
    {
        public WidgetButtonIconDone(final int integer2, final int integer3) {
            super(integer2, integer3, 90, 220);
        }
        
        @Override
        public void onPress() {
            BeaconScreen.this.minecraft.getNetworkHandler().sendPacket(new UpdateBeaconC2SPacket(StatusEffect.getRawId(BeaconScreen.this.primaryEffect), StatusEffect.getRawId(BeaconScreen.this.secondaryEffect)));
            BeaconScreen.this.minecraft.player.networkHandler.sendPacket(new GuiCloseC2SPacket(BeaconScreen.this.minecraft.player.container.syncId));
            BeaconScreen.this.minecraft.openScreen(null);
        }
        
        @Override
        public void renderToolTip(final int mouseX, final int mouseY) {
            BeaconScreen.this.renderTooltip(I18n.translate("gui.done"), mouseX, mouseY);
        }
    }
    
    @Environment(EnvType.CLIENT)
    class WidgetButtonIconCancel extends e
    {
        public WidgetButtonIconCancel(final int integer2, final int integer3) {
            super(integer2, integer3, 112, 220);
        }
        
        @Override
        public void onPress() {
            BeaconScreen.this.minecraft.player.networkHandler.sendPacket(new GuiCloseC2SPacket(BeaconScreen.this.minecraft.player.container.syncId));
            BeaconScreen.this.minecraft.openScreen(null);
        }
        
        @Override
        public void renderToolTip(final int mouseX, final int mouseY) {
            BeaconScreen.this.renderTooltip(I18n.translate("gui.cancel"), mouseX, mouseY);
        }
    }
}

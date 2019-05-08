package net.minecraft.client.gui.container;

import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;
import net.minecraft.container.Container;
import net.minecraft.container.Slot;
import net.minecraft.network.Packet;
import net.minecraft.server.network.packet.RenameItemC2SPacket;
import net.minecraft.client.gui.DrawableHelper;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TextComponent;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.container.ContainerListener;
import net.minecraft.container.AnvilContainer;
import net.minecraft.client.gui.ContainerScreen;

@Environment(EnvType.CLIENT)
public class AnvilScreen extends ContainerScreen<AnvilContainer> implements ContainerListener
{
    private static final Identifier BG_TEX;
    private TextFieldWidget nameField;
    
    public AnvilScreen(final AnvilContainer anvilContainer, final PlayerInventory playerInventory, final TextComponent textComponent) {
        super(anvilContainer, playerInventory, textComponent);
    }
    
    @Override
    protected void init() {
        super.init();
        this.minecraft.keyboard.enableRepeatEvents(true);
        final int integer1 = (this.width - this.containerWidth) / 2;
        final int integer2 = (this.height - this.containerHeight) / 2;
        (this.nameField = new TextFieldWidget(this.font, integer1 + 62, integer2 + 24, 103, 12, I18n.translate("container.repair"))).d(false);
        this.nameField.changeFocus(true);
        this.nameField.h(-1);
        this.nameField.i(-1);
        this.nameField.setHasBorder(false);
        this.nameField.setMaxLength(35);
        this.nameField.setChangedListener(this::onRenamed);
        this.children.add(this.nameField);
        ((AnvilContainer)this.container).addListener(this);
        this.setInitialFocus(this.nameField);
    }
    
    @Override
    public void resize(final MinecraftClient client, final int width, final int height) {
        final String string4 = this.nameField.getText();
        this.init(client, width, height);
        this.nameField.setText(string4);
    }
    
    @Override
    public void removed() {
        super.removed();
        this.minecraft.keyboard.enableRepeatEvents(false);
        ((AnvilContainer)this.container).removeListener(this);
    }
    
    @Override
    public boolean keyPressed(final int keyCode, final int scanCode, final int modifiers) {
        if (keyCode == 256) {
            this.minecraft.player.closeContainer();
        }
        return this.nameField.keyPressed(keyCode, scanCode, modifiers) || this.nameField.f() || super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    @Override
    protected void drawForeground(final int mouseX, final int mouseY) {
        GlStateManager.disableLighting();
        GlStateManager.disableBlend();
        this.font.draw(this.title.getFormattedText(), 60.0f, 6.0f, 4210752);
        final int integer3 = ((AnvilContainer)this.container).getLevelCost();
        if (integer3 > 0) {
            int integer4 = 8453920;
            boolean boolean5 = true;
            String string6 = I18n.translate("container.repair.cost", integer3);
            if (integer3 >= 40 && !this.minecraft.player.abilities.creativeMode) {
                string6 = I18n.translate("container.repair.expensive");
                integer4 = 16736352;
            }
            else if (!((AnvilContainer)this.container).getSlot(2).hasStack()) {
                boolean5 = false;
            }
            else if (!((AnvilContainer)this.container).getSlot(2).canTakeItems(this.playerInventory.player)) {
                integer4 = 16736352;
            }
            if (boolean5) {
                final int integer5 = this.containerWidth - 8 - this.font.getStringWidth(string6) - 2;
                final int integer6 = 69;
                DrawableHelper.fill(integer5 - 2, 67, this.containerWidth - 8, 79, 1325400064);
                this.font.drawWithShadow(string6, (float)integer5, 69.0f, integer4);
            }
        }
        GlStateManager.enableLighting();
    }
    
    private void onRenamed(final String string) {
        if (string.isEmpty()) {
            return;
        }
        String string2 = string;
        final Slot slot3 = ((AnvilContainer)this.container).getSlot(0);
        if (slot3 != null && slot3.hasStack() && !slot3.getStack().hasDisplayName() && string2.equals(slot3.getStack().getDisplayName().getString())) {
            string2 = "";
        }
        ((AnvilContainer)this.container).setNewItemName(string2);
        this.minecraft.player.networkHandler.sendPacket(new RenameItemC2SPacket(string2));
    }
    
    @Override
    public void render(final int mouseX, final int mouseY, final float delta) {
        this.renderBackground();
        super.render(mouseX, mouseY, delta);
        this.drawMouseoverTooltip(mouseX, mouseY);
        GlStateManager.disableLighting();
        GlStateManager.disableBlend();
        this.nameField.render(mouseX, mouseY, delta);
    }
    
    @Override
    protected void drawBackground(final float delta, final int mouseX, final int mouseY) {
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bindTexture(AnvilScreen.BG_TEX);
        final int integer4 = (this.width - this.containerWidth) / 2;
        final int integer5 = (this.height - this.containerHeight) / 2;
        this.blit(integer4, integer5, 0, 0, this.containerWidth, this.containerHeight);
        this.blit(integer4 + 59, integer5 + 20, 0, this.containerHeight + (((AnvilContainer)this.container).getSlot(0).hasStack() ? 0 : 16), 110, 16);
        if ((((AnvilContainer)this.container).getSlot(0).hasStack() || ((AnvilContainer)this.container).getSlot(1).hasStack()) && !((AnvilContainer)this.container).getSlot(2).hasStack()) {
            this.blit(integer4 + 99, integer5 + 45, this.containerWidth, 0, 28, 21);
        }
    }
    
    @Override
    public void onContainerRegistered(final Container container, final DefaultedList<ItemStack> defaultedList) {
        this.onContainerSlotUpdate(container, 0, container.getSlot(0).getStack());
    }
    
    @Override
    public void onContainerSlotUpdate(final Container container, final int slotId, final ItemStack itemStack) {
        if (slotId == 0) {
            this.nameField.setText(itemStack.isEmpty() ? "" : itemStack.getDisplayName().getString());
            this.nameField.setIsEditable(!itemStack.isEmpty());
        }
    }
    
    @Override
    public void onContainerPropertyUpdate(final Container container, final int propertyId, final int integer3) {
    }
    
    static {
        BG_TEX = new Identifier("textures/gui/container/anvil.png");
    }
}

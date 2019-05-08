package net.minecraft.client.gui.container;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;
import net.minecraft.container.Container;
import net.minecraft.text.TextComponent;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.container.ContainerListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.container.LecternContainer;
import net.minecraft.client.gui.ContainerProvider;
import net.minecraft.client.gui.WrittenBookScreen;

@Environment(EnvType.CLIENT)
public class LecternScreen extends WrittenBookScreen implements ContainerProvider<LecternContainer>
{
    private final LecternContainer lecternContainer;
    private final ContainerListener listener;
    
    public LecternScreen(final LecternContainer lecternContainer, final PlayerInventory playerInventory, final TextComponent textComponent) {
        this.listener = new ContainerListener() {
            @Override
            public void onContainerRegistered(final Container container, final DefaultedList<ItemStack> defaultedList) {
                LecternScreen.this.updatePageProvider();
            }
            
            @Override
            public void onContainerSlotUpdate(final Container container, final int slotId, final ItemStack itemStack) {
                LecternScreen.this.updatePageProvider();
            }
            
            @Override
            public void onContainerPropertyUpdate(final Container container, final int propertyId, final int integer3) {
                if (propertyId == 0) {
                    LecternScreen.this.updatePage();
                }
            }
        };
        this.lecternContainer = lecternContainer;
    }
    
    @Override
    public LecternContainer getContainer() {
        return this.lecternContainer;
    }
    
    @Override
    protected void init() {
        super.init();
        this.lecternContainer.addListener(this.listener);
    }
    
    @Override
    public void onClose() {
        this.minecraft.player.closeContainer();
        super.onClose();
    }
    
    @Override
    public void removed() {
        super.removed();
        this.lecternContainer.removeListener(this.listener);
    }
    
    @Override
    protected void addCloseButton() {
        if (this.minecraft.player.canModifyWorld()) {
            this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 100, 196, 98, 20, I18n.translate("gui.done"), buttonWidget -> this.minecraft.openScreen(null)));
            this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 + 2, 196, 98, 20, I18n.translate("lectern.take_book"), buttonWidget -> this.sendButtonPressPacket(3)));
        }
        else {
            super.addCloseButton();
        }
    }
    
    @Override
    protected void goToPreviousPage() {
        this.sendButtonPressPacket(1);
    }
    
    @Override
    protected void goToNextPage() {
        this.sendButtonPressPacket(2);
    }
    
    @Override
    protected boolean jumpToPage(final int page) {
        if (page != this.lecternContainer.getPage()) {
            this.sendButtonPressPacket(100 + page);
            return true;
        }
        return false;
    }
    
    private void sendButtonPressPacket(final int id) {
        this.minecraft.interactionManager.clickButton(this.lecternContainer.syncId, id);
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }
    
    private void updatePageProvider() {
        final ItemStack itemStack1 = this.lecternContainer.getBookItem();
        this.setPageProvider(Contents.create(itemStack1));
    }
    
    private void updatePage() {
        this.setPage(this.lecternContainer.getPage());
    }
}

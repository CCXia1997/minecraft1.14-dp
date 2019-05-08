package net.minecraft.client.gui.ingame;

import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;
import net.minecraft.container.Container;
import net.minecraft.client.MinecraftClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.container.ContainerListener;

@Environment(EnvType.CLIENT)
public class CreativePlayerInventoryContainerListener implements ContainerListener
{
    private final MinecraftClient client;
    
    public CreativePlayerInventoryContainerListener(final MinecraftClient minecraftClient) {
        this.client = minecraftClient;
    }
    
    @Override
    public void onContainerRegistered(final Container container, final DefaultedList<ItemStack> defaultedList) {
    }
    
    @Override
    public void onContainerSlotUpdate(final Container container, final int slotId, final ItemStack itemStack) {
        this.client.interactionManager.clickCreativeStack(itemStack, slotId);
    }
    
    @Override
    public void onContainerPropertyUpdate(final Container container, final int propertyId, final int integer3) {
    }
}

package net.minecraft.client.network;

import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.container.ContainerProvider;
import net.minecraft.text.TextComponent;
import net.minecraft.container.NameableContainerProvider;

public final class ClientDummyContainerProvider implements NameableContainerProvider
{
    private final TextComponent name;
    private final ContainerProvider containerProvider;
    
    public ClientDummyContainerProvider(final ContainerProvider containerProvider, final TextComponent textComponent) {
        this.containerProvider = containerProvider;
        this.name = textComponent;
    }
    
    @Override
    public TextComponent getDisplayName() {
        return this.name;
    }
    
    @Override
    public Container createMenu(final int syncId, final PlayerInventory playerInventory, final PlayerEntity playerEntity) {
        return this.containerProvider.createMenu(syncId, playerInventory, playerEntity);
    }
}

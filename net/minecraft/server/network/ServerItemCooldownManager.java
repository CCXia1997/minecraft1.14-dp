package net.minecraft.server.network;

import net.minecraft.network.Packet;
import net.minecraft.client.network.packet.CooldownUpdateS2CPacket;
import net.minecraft.item.Item;
import net.minecraft.entity.player.ItemCooldownManager;

public class ServerItemCooldownManager extends ItemCooldownManager
{
    private final ServerPlayerEntity player;
    
    public ServerItemCooldownManager(final ServerPlayerEntity serverPlayerEntity) {
        this.player = serverPlayerEntity;
    }
    
    @Override
    protected void onCooldownUpdate(final Item item, final int integer) {
        super.onCooldownUpdate(item, integer);
        this.player.networkHandler.sendPacket(new CooldownUpdateS2CPacket(item, integer));
    }
    
    @Override
    protected void onCooldownUpdate(final Item item) {
        super.onCooldownUpdate(item);
        this.player.networkHandler.sendPacket(new CooldownUpdateS2CPacket(item, 0));
    }
}

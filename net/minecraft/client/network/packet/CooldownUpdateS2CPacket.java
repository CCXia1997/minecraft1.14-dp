package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.item.Item;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class CooldownUpdateS2CPacket implements Packet<ClientPlayPacketListener>
{
    private Item item;
    private int cooldown;
    
    public CooldownUpdateS2CPacket() {
    }
    
    public CooldownUpdateS2CPacket(final Item item, final int integer) {
        this.item = item;
        this.cooldown = integer;
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.item = Item.byRawId(buf.readVarInt());
        this.cooldown = buf.readVarInt();
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeVarInt(Item.getRawIdByItem(this.item));
        buf.writeVarInt(this.cooldown);
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onCooldownUpdate(this);
    }
    
    @Environment(EnvType.CLIENT)
    public Item getItem() {
        return this.item;
    }
    
    @Environment(EnvType.CLIENT)
    public int getCooldown() {
        return this.cooldown;
    }
}

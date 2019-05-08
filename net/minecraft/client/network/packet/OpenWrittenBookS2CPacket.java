package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.Hand;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class OpenWrittenBookS2CPacket implements Packet<ClientPlayPacketListener>
{
    private Hand hand;
    
    public OpenWrittenBookS2CPacket() {
    }
    
    public OpenWrittenBookS2CPacket(final Hand hand) {
        this.hand = hand;
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.hand = buf.<Hand>readEnumConstant(Hand.class);
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeEnumConstant(this.hand);
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onOpenWrittenBook(this);
    }
    
    @Environment(EnvType.CLIENT)
    public Hand getHand() {
        return this.hand;
    }
}

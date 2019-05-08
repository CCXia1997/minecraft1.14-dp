package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.text.TextComponent;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class PlayerListHeaderS2CPacket implements Packet<ClientPlayPacketListener>
{
    private TextComponent header;
    private TextComponent footer;
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.header = buf.readTextComponent();
        this.footer = buf.readTextComponent();
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeTextComponent(this.header);
        buf.writeTextComponent(this.footer);
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onPlayerListHeader(this);
    }
    
    @Environment(EnvType.CLIENT)
    public TextComponent getHeader() {
        return this.header;
    }
    
    @Environment(EnvType.CLIENT)
    public TextComponent getFooter() {
        return this.footer;
    }
}

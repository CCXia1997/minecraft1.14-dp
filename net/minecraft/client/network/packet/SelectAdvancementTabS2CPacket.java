package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import javax.annotation.Nullable;
import net.minecraft.util.Identifier;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class SelectAdvancementTabS2CPacket implements Packet<ClientPlayPacketListener>
{
    @Nullable
    private Identifier tabId;
    
    public SelectAdvancementTabS2CPacket() {
    }
    
    public SelectAdvancementTabS2CPacket(@Nullable final Identifier identifier) {
        this.tabId = identifier;
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onSelectAdvancementTab(this);
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        if (buf.readBoolean()) {
            this.tabId = buf.readIdentifier();
        }
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeBoolean(this.tabId != null);
        if (this.tabId != null) {
            buf.writeIdentifier(this.tabId);
        }
    }
    
    @Nullable
    @Environment(EnvType.CLIENT)
    public Identifier getTabId() {
        return this.tabId;
    }
}

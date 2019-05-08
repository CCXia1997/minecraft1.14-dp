package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.tag.TagManager;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class SynchronizeTagsS2CPacket implements Packet<ClientPlayPacketListener>
{
    private TagManager tagManager;
    
    public SynchronizeTagsS2CPacket() {
    }
    
    public SynchronizeTagsS2CPacket(final TagManager tagManager) {
        this.tagManager = tagManager;
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.tagManager = TagManager.fromPacket(buf);
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        this.tagManager.toPacket(buf);
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onSynchronizeTags(this);
    }
    
    @Environment(EnvType.CLIENT)
    public TagManager getTagManager() {
        return this.tagManager;
    }
}

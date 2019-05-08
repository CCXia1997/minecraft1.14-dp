package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.registry.Registry;
import net.minecraft.container.ContainerType;
import net.minecraft.text.TextComponent;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class OpenContainerPacket implements Packet<ClientPlayPacketListener>
{
    private int syncId;
    private int containerId;
    private TextComponent name;
    
    public OpenContainerPacket() {
    }
    
    public OpenContainerPacket(final int syncId, final ContainerType<?> type, final TextComponent name) {
        this.syncId = syncId;
        this.containerId = Registry.CONTAINER.getRawId(type);
        this.name = name;
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.syncId = buf.readVarInt();
        this.containerId = buf.readVarInt();
        this.name = buf.readTextComponent();
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeVarInt(this.syncId);
        buf.writeVarInt(this.containerId);
        buf.writeTextComponent(this.name);
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onOpenContainer(this);
    }
    
    @Environment(EnvType.CLIENT)
    public int getSyncId() {
        return this.syncId;
    }
    
    @Nullable
    @Environment(EnvType.CLIENT)
    public ContainerType<?> getContainerType() {
        return Registry.CONTAINER.get(this.containerId);
    }
    
    @Environment(EnvType.CLIENT)
    public TextComponent getName() {
        return this.name;
    }
}

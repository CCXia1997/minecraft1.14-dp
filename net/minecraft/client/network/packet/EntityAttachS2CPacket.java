package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class EntityAttachS2CPacket implements Packet<ClientPlayPacketListener>
{
    private int attachedId;
    private int holdingId;
    
    public EntityAttachS2CPacket() {
    }
    
    public EntityAttachS2CPacket(final Entity attachedEntity, @Nullable final Entity entity2) {
        this.attachedId = attachedEntity.getEntityId();
        this.holdingId = ((entity2 != null) ? entity2.getEntityId() : 0);
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.attachedId = buf.readInt();
        this.holdingId = buf.readInt();
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeInt(this.attachedId);
        buf.writeInt(this.holdingId);
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onEntityAttach(this);
    }
    
    @Environment(EnvType.CLIENT)
    public int getAttachedEntityId() {
        return this.attachedId;
    }
    
    @Environment(EnvType.CLIENT)
    public int getHoldingEntityId() {
        return this.holdingId;
    }
}

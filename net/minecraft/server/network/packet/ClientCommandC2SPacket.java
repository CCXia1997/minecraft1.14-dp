package net.minecraft.server.network.packet;

import net.minecraft.network.listener.PacketListener;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.Packet;

public class ClientCommandC2SPacket implements Packet<ServerPlayPacketListener>
{
    private int entityId;
    private Mode mode;
    private int mountJumpHeight;
    
    public ClientCommandC2SPacket() {
    }
    
    @Environment(EnvType.CLIENT)
    public ClientCommandC2SPacket(final Entity entity, final Mode mode) {
        this(entity, mode, 0);
    }
    
    @Environment(EnvType.CLIENT)
    public ClientCommandC2SPacket(final Entity entity, final Mode mode, final int integer) {
        this.entityId = entity.getEntityId();
        this.mode = mode;
        this.mountJumpHeight = integer;
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.entityId = buf.readVarInt();
        this.mode = buf.<Mode>readEnumConstant(Mode.class);
        this.mountJumpHeight = buf.readVarInt();
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeVarInt(this.entityId);
        buf.writeEnumConstant(this.mode);
        buf.writeVarInt(this.mountJumpHeight);
    }
    
    @Override
    public void apply(final ServerPlayPacketListener listener) {
        listener.onClientCommand(this);
    }
    
    public Mode getMode() {
        return this.mode;
    }
    
    public int getMountJumpHeight() {
        return this.mountJumpHeight;
    }
    
    public enum Mode
    {
        a, 
        b, 
        c, 
        d, 
        e, 
        f, 
        g, 
        h, 
        i;
    }
}

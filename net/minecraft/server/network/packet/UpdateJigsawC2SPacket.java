package net.minecraft.server.network.packet;

import net.minecraft.network.listener.PacketListener;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.Packet;

public class UpdateJigsawC2SPacket implements Packet<ServerPlayPacketListener>
{
    private BlockPos pos;
    private Identifier attachmentType;
    private Identifier targetPool;
    private String finalState;
    
    public UpdateJigsawC2SPacket() {
    }
    
    @Environment(EnvType.CLIENT)
    public UpdateJigsawC2SPacket(final BlockPos pos, final Identifier attachmentType, final Identifier targetPool, final String finalState) {
        this.pos = pos;
        this.attachmentType = attachmentType;
        this.targetPool = targetPool;
        this.finalState = finalState;
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.pos = buf.readBlockPos();
        this.attachmentType = buf.readIdentifier();
        this.targetPool = buf.readIdentifier();
        this.finalState = buf.readString(32767);
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeBlockPos(this.pos);
        buf.writeIdentifier(this.attachmentType);
        buf.writeIdentifier(this.targetPool);
        buf.writeString(this.finalState);
    }
    
    @Override
    public void apply(final ServerPlayPacketListener listener) {
        listener.onJigsawUpdate(this);
    }
    
    public BlockPos getPos() {
        return this.pos;
    }
    
    public Identifier getTargetPool() {
        return this.targetPool;
    }
    
    public Identifier getAttachmentType() {
        return this.attachmentType;
    }
    
    public String getFinalState() {
        return this.finalState;
    }
}

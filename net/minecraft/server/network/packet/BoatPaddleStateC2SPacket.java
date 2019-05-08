package net.minecraft.server.network.packet;

import net.minecraft.network.listener.PacketListener;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.Packet;

public class BoatPaddleStateC2SPacket implements Packet<ServerPlayPacketListener>
{
    private boolean leftPaddling;
    private boolean rightPaddling;
    
    public BoatPaddleStateC2SPacket() {
    }
    
    public BoatPaddleStateC2SPacket(final boolean boolean1, final boolean boolean2) {
        this.leftPaddling = boolean1;
        this.rightPaddling = boolean2;
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.leftPaddling = buf.readBoolean();
        this.rightPaddling = buf.readBoolean();
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeBoolean(this.leftPaddling);
        buf.writeBoolean(this.rightPaddling);
    }
    
    @Override
    public void apply(final ServerPlayPacketListener listener) {
        listener.onBoatPaddleState(this);
    }
    
    public boolean isLeftPaddling() {
        return this.leftPaddling;
    }
    
    public boolean isRightPaddling() {
        return this.rightPaddling;
    }
}

package net.minecraft.server.network.packet;

import net.minecraft.network.listener.PacketListener;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.Packet;

public class PlayerInputC2SPacket implements Packet<ServerPlayPacketListener>
{
    private float sideways;
    private float forward;
    private boolean jumping;
    private boolean sneaking;
    
    public PlayerInputC2SPacket() {
    }
    
    @Environment(EnvType.CLIENT)
    public PlayerInputC2SPacket(final float sideways, final float forward, final boolean jumping, final boolean sneaking) {
        this.sideways = sideways;
        this.forward = forward;
        this.jumping = jumping;
        this.sneaking = sneaking;
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.sideways = buf.readFloat();
        this.forward = buf.readFloat();
        final byte byte2 = buf.readByte();
        this.jumping = ((byte2 & 0x1) > 0);
        this.sneaking = ((byte2 & 0x2) > 0);
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeFloat(this.sideways);
        buf.writeFloat(this.forward);
        byte byte2 = 0;
        if (this.jumping) {
            byte2 |= 0x1;
        }
        if (this.sneaking) {
            byte2 |= 0x2;
        }
        buf.writeByte(byte2);
    }
    
    @Override
    public void apply(final ServerPlayPacketListener listener) {
        listener.onPlayerInput(this);
    }
    
    public float getSideways() {
        return this.sideways;
    }
    
    public float getForward() {
        return this.forward;
    }
    
    public boolean isJumping() {
        return this.jumping;
    }
    
    public boolean isSneaking() {
        return this.sneaking;
    }
}

package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class HealthUpdateS2CPacket implements Packet<ClientPlayPacketListener>
{
    private float health;
    private int food;
    private float saturation;
    
    public HealthUpdateS2CPacket() {
    }
    
    public HealthUpdateS2CPacket(final float health, final int food, final float float3) {
        this.health = health;
        this.food = food;
        this.saturation = float3;
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.health = buf.readFloat();
        this.food = buf.readVarInt();
        this.saturation = buf.readFloat();
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeFloat(this.health);
        buf.writeVarInt(this.food);
        buf.writeFloat(this.saturation);
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onHealthUpdate(this);
    }
    
    @Environment(EnvType.CLIENT)
    public float getHealth() {
        return this.health;
    }
    
    @Environment(EnvType.CLIENT)
    public int getFood() {
        return this.food;
    }
    
    @Environment(EnvType.CLIENT)
    public float getSaturation() {
        return this.saturation;
    }
}

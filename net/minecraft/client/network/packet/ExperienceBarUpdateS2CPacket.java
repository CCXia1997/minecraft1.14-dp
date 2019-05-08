package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class ExperienceBarUpdateS2CPacket implements Packet<ClientPlayPacketListener>
{
    private float barProgress;
    private int experienceLevel;
    private int experience;
    
    public ExperienceBarUpdateS2CPacket() {
    }
    
    public ExperienceBarUpdateS2CPacket(final float barProgress, final int experienceLevel, final int integer3) {
        this.barProgress = barProgress;
        this.experienceLevel = experienceLevel;
        this.experience = integer3;
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.barProgress = buf.readFloat();
        this.experience = buf.readVarInt();
        this.experienceLevel = buf.readVarInt();
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeFloat(this.barProgress);
        buf.writeVarInt(this.experience);
        buf.writeVarInt(this.experienceLevel);
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onExperienceBarUpdate(this);
    }
    
    @Environment(EnvType.CLIENT)
    public float getBarProgress() {
        return this.barProgress;
    }
    
    @Environment(EnvType.CLIENT)
    public int getExperienceLevel() {
        return this.experienceLevel;
    }
    
    @Environment(EnvType.CLIENT)
    public int getExperience() {
        return this.experience;
    }
}

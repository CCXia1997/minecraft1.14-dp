package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class EntityPotionEffectS2CPacket implements Packet<ClientPlayPacketListener>
{
    private int entityId;
    private byte effectId;
    private byte amplifier;
    private int duration;
    private byte flags;
    
    public EntityPotionEffectS2CPacket() {
    }
    
    public EntityPotionEffectS2CPacket(final int entityId, final StatusEffectInstance effect) {
        this.entityId = entityId;
        this.effectId = (byte)(StatusEffect.getRawId(effect.getEffectType()) & 0xFF);
        this.amplifier = (byte)(effect.getAmplifier() & 0xFF);
        if (effect.getDuration() > 32767) {
            this.duration = 32767;
        }
        else {
            this.duration = effect.getDuration();
        }
        this.flags = 0;
        if (effect.isAmbient()) {
            this.flags |= 0x1;
        }
        if (effect.shouldShowParticles()) {
            this.flags |= 0x2;
        }
        if (effect.shouldShowIcon()) {
            this.flags |= 0x4;
        }
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.entityId = buf.readVarInt();
        this.effectId = buf.readByte();
        this.amplifier = buf.readByte();
        this.duration = buf.readVarInt();
        this.flags = buf.readByte();
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeVarInt(this.entityId);
        buf.writeByte(this.effectId);
        buf.writeByte(this.amplifier);
        buf.writeVarInt(this.duration);
        buf.writeByte(this.flags);
    }
    
    @Environment(EnvType.CLIENT)
    public boolean isPermanent() {
        return this.duration == 32767;
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onEntityPotionEffect(this);
    }
    
    @Environment(EnvType.CLIENT)
    public int getEntityId() {
        return this.entityId;
    }
    
    @Environment(EnvType.CLIENT)
    public byte getEffectId() {
        return this.effectId;
    }
    
    @Environment(EnvType.CLIENT)
    public byte getAmplifier() {
        return this.amplifier;
    }
    
    @Environment(EnvType.CLIENT)
    public int getDuration() {
        return this.duration;
    }
    
    @Environment(EnvType.CLIENT)
    public boolean shouldShowParticles() {
        return (this.flags & 0x2) == 0x2;
    }
    
    @Environment(EnvType.CLIENT)
    public boolean isAmbient() {
        return (this.flags & 0x1) == 0x1;
    }
    
    @Environment(EnvType.CLIENT)
    public boolean shouldShowIcon() {
        return (this.flags & 0x4) == 0x4;
    }
}

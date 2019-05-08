package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class RemoveEntityEffectS2CPacket implements Packet<ClientPlayPacketListener>
{
    private int entityId;
    private StatusEffect effectType;
    
    public RemoveEntityEffectS2CPacket() {
    }
    
    public RemoveEntityEffectS2CPacket(final int entityId, final StatusEffect statusEffect) {
        this.entityId = entityId;
        this.effectType = statusEffect;
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.entityId = buf.readVarInt();
        this.effectType = StatusEffect.byRawId(buf.readUnsignedByte());
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeVarInt(this.entityId);
        buf.writeByte(StatusEffect.getRawId(this.effectType));
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onRemoveEntityEffect(this);
    }
    
    @Nullable
    @Environment(EnvType.CLIENT)
    public Entity getEntity(final World world) {
        return world.getEntityById(this.entityId);
    }
    
    @Nullable
    @Environment(EnvType.CLIENT)
    public StatusEffect getEffectType() {
        return this.effectType;
    }
}

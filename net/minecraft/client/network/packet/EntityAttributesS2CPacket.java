package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.IOException;
import java.util.UUID;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.util.PacketByteBuf;
import java.util.Iterator;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import java.util.Collection;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class EntityAttributesS2CPacket implements Packet<ClientPlayPacketListener>
{
    private int entityId;
    private final List<Entry> entries;
    
    public EntityAttributesS2CPacket() {
        this.entries = Lists.newArrayList();
    }
    
    public EntityAttributesS2CPacket(final int entityId, final Collection<EntityAttributeInstance> attributes) {
        this.entries = Lists.newArrayList();
        this.entityId = entityId;
        for (final EntityAttributeInstance entityAttributeInstance4 : attributes) {
            this.entries.add(new Entry(entityAttributeInstance4.getAttribute().getId(), entityAttributeInstance4.getBaseValue(), entityAttributeInstance4.getModifiers()));
        }
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.entityId = buf.readVarInt();
        for (int integer2 = buf.readInt(), integer3 = 0; integer3 < integer2; ++integer3) {
            final String string4 = buf.readString(64);
            final double double5 = buf.readDouble();
            final List<EntityAttributeModifier> list7 = Lists.newArrayList();
            for (int integer4 = buf.readVarInt(), integer5 = 0; integer5 < integer4; ++integer5) {
                final UUID uUID10 = buf.readUuid();
                list7.add(new EntityAttributeModifier(uUID10, "Unknown synced attribute modifier", buf.readDouble(), EntityAttributeModifier.Operation.fromId(buf.readByte())));
            }
            this.entries.add(new Entry(string4, double5, list7));
        }
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeVarInt(this.entityId);
        buf.writeInt(this.entries.size());
        for (final Entry entry3 : this.entries) {
            buf.writeString(entry3.getId());
            buf.writeDouble(entry3.getBaseValue());
            buf.writeVarInt(entry3.getModifiers().size());
            for (final EntityAttributeModifier entityAttributeModifier5 : entry3.getModifiers()) {
                buf.writeUuid(entityAttributeModifier5.getId());
                buf.writeDouble(entityAttributeModifier5.getAmount());
                buf.writeByte(entityAttributeModifier5.getOperation().getId());
            }
        }
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onEntityAttributes(this);
    }
    
    @Environment(EnvType.CLIENT)
    public int getEntityId() {
        return this.entityId;
    }
    
    @Environment(EnvType.CLIENT)
    public List<Entry> getEntries() {
        return this.entries;
    }
    
    public class Entry
    {
        private final String id;
        private final double baseValue;
        private final Collection<EntityAttributeModifier> modifiers;
        
        public Entry(final String baseValue, final double double3, final Collection<EntityAttributeModifier> collection5) {
            this.id = baseValue;
            this.baseValue = double3;
            this.modifiers = collection5;
        }
        
        public String getId() {
            return this.id;
        }
        
        public double getBaseValue() {
            return this.baseValue;
        }
        
        public Collection<EntityAttributeModifier> getModifiers() {
            return this.modifiers;
        }
    }
}

package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.IOException;
import com.google.common.collect.Sets;
import net.minecraft.util.PacketByteBuf;
import java.util.Iterator;
import com.google.common.collect.Maps;
import java.util.Collection;
import net.minecraft.advancement.AdvancementProgress;
import java.util.Set;
import net.minecraft.advancement.Advancement;
import net.minecraft.util.Identifier;
import java.util.Map;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class AdvancementUpdateS2CPacket implements Packet<ClientPlayPacketListener>
{
    private boolean clearCurrent;
    private Map<Identifier, Advancement.Task> toEarn;
    private Set<Identifier> toRemove;
    private Map<Identifier, AdvancementProgress> toSetProgress;
    
    public AdvancementUpdateS2CPacket() {
    }
    
    public AdvancementUpdateS2CPacket(final boolean boolean1, final Collection<Advancement> collection, final Set<Identifier> set, final Map<Identifier, AdvancementProgress> map) {
        this.clearCurrent = boolean1;
        this.toEarn = Maps.newHashMap();
        for (final Advancement advancement6 : collection) {
            this.toEarn.put(advancement6.getId(), advancement6.createTask());
        }
        this.toRemove = set;
        this.toSetProgress = Maps.newHashMap(map);
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onAdvancements(this);
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.clearCurrent = buf.readBoolean();
        this.toEarn = Maps.newHashMap();
        this.toRemove = Sets.newLinkedHashSet();
        this.toSetProgress = Maps.newHashMap();
        for (int integer2 = buf.readVarInt(), integer3 = 0; integer3 < integer2; ++integer3) {
            final Identifier identifier4 = buf.readIdentifier();
            final Advancement.Task task5 = Advancement.Task.fromPacket(buf);
            this.toEarn.put(identifier4, task5);
        }
        for (int integer2 = buf.readVarInt(), integer3 = 0; integer3 < integer2; ++integer3) {
            final Identifier identifier4 = buf.readIdentifier();
            this.toRemove.add(identifier4);
        }
        for (int integer2 = buf.readVarInt(), integer3 = 0; integer3 < integer2; ++integer3) {
            final Identifier identifier4 = buf.readIdentifier();
            this.toSetProgress.put(identifier4, AdvancementProgress.fromPacket(buf));
        }
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeBoolean(this.clearCurrent);
        buf.writeVarInt(this.toEarn.size());
        for (final Map.Entry<Identifier, Advancement.Task> entry3 : this.toEarn.entrySet()) {
            final Identifier identifier4 = entry3.getKey();
            final Advancement.Task task5 = entry3.getValue();
            buf.writeIdentifier(identifier4);
            task5.toPacket(buf);
        }
        buf.writeVarInt(this.toRemove.size());
        for (final Identifier identifier5 : this.toRemove) {
            buf.writeIdentifier(identifier5);
        }
        buf.writeVarInt(this.toSetProgress.size());
        for (final Map.Entry<Identifier, AdvancementProgress> entry4 : this.toSetProgress.entrySet()) {
            buf.writeIdentifier(entry4.getKey());
            entry4.getValue().toPacket(buf);
        }
    }
    
    @Environment(EnvType.CLIENT)
    public Map<Identifier, Advancement.Task> getAdvancementsToEarn() {
        return this.toEarn;
    }
    
    @Environment(EnvType.CLIENT)
    public Set<Identifier> getAdvancementIdsToRemove() {
        return this.toRemove;
    }
    
    @Environment(EnvType.CLIENT)
    public Map<Identifier, AdvancementProgress> getAdvancementsToProgress() {
        return this.toSetProgress;
    }
    
    @Environment(EnvType.CLIENT)
    public boolean shouldClearCurrent() {
        return this.clearCurrent;
    }
}

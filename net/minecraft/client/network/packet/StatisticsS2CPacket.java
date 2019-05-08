package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.util.Map;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.IOException;
import net.minecraft.util.registry.Registry;
import net.minecraft.stat.StatType;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.stat.Stat;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class StatisticsS2CPacket implements Packet<ClientPlayPacketListener>
{
    private Object2IntMap<Stat<?>> stats;
    
    public StatisticsS2CPacket() {
    }
    
    public StatisticsS2CPacket(final Object2IntMap<Stat<?>> object2IntMap) {
        this.stats = object2IntMap;
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onStatistics(this);
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        final int integer2 = buf.readVarInt();
        this.stats = (Object2IntMap<Stat<?>>)new Object2IntOpenHashMap(integer2);
        for (int integer3 = 0; integer3 < integer2; ++integer3) {
            this.readStat(Registry.STAT_TYPE.get(buf.readVarInt()), buf);
        }
    }
    
    private <T> void readStat(final StatType<T> type, final PacketByteBuf packetByteBuf) {
        final int integer3 = packetByteBuf.readVarInt();
        final int integer4 = packetByteBuf.readVarInt();
        this.stats.put(type.getOrCreateStat(type.getRegistry().get(integer3)), integer4);
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeVarInt(this.stats.size());
        for (final Object2IntMap.Entry<Stat<?>> entry3 : this.stats.object2IntEntrySet()) {
            final Stat<?> stat4 = entry3.getKey();
            buf.writeVarInt(Registry.STAT_TYPE.getRawId(stat4.getType()));
            buf.writeVarInt(this.getStatId(stat4));
            buf.writeVarInt(entry3.getIntValue());
        }
    }
    
    private <T> int getStatId(final Stat<T> stat) {
        return stat.getType().getRegistry().getRawId(stat.getValue());
    }
    
    @Environment(EnvType.CLIENT)
    public Map<Stat<?>, Integer> getStatMap() {
        return (Map<Stat<?>, Integer>)this.stats;
    }
}

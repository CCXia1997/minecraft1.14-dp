package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class CustomPayloadS2CPacket implements Packet<ClientPlayPacketListener>
{
    public static final Identifier BRAND;
    public static final Identifier DEBUG_PATH;
    public static final Identifier DEBUG_NEIGHBORS_UPDATE;
    public static final Identifier DEBUG_CAVES;
    public static final Identifier DEBUG_STRUCTURES;
    public static final Identifier DEBUG_WORLDGEN_ATTEMPT;
    public static final Identifier DEBUG_POI_TICKET_COUNT;
    public static final Identifier DEBUG_POI_ADDED;
    public static final Identifier DEBUG_POI_REMOVED;
    public static final Identifier DEBUG_VILLAGE_SECTIONS;
    public static final Identifier DEBUG_GOAL_SELECTOR;
    public static final Identifier DEBUG_BRAIN;
    private Identifier channel;
    private PacketByteBuf data;
    
    public CustomPayloadS2CPacket() {
    }
    
    public CustomPayloadS2CPacket(final Identifier channel, final PacketByteBuf packetByteBuf) {
        this.channel = channel;
        this.data = packetByteBuf;
        if (packetByteBuf.writerIndex() > 1048576) {
            throw new IllegalArgumentException("Payload may not be larger than 1048576 bytes");
        }
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.channel = buf.readIdentifier();
        final int integer2 = buf.readableBytes();
        if (integer2 < 0 || integer2 > 1048576) {
            throw new IOException("Payload may not be larger than 1048576 bytes");
        }
        this.data = new PacketByteBuf(buf.readBytes(integer2));
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeIdentifier(this.channel);
        buf.writeBytes(this.data.copy());
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onCustomPayload(this);
    }
    
    @Environment(EnvType.CLIENT)
    public Identifier getChannel() {
        return this.channel;
    }
    
    @Environment(EnvType.CLIENT)
    public PacketByteBuf getData() {
        return new PacketByteBuf(this.data.copy());
    }
    
    static {
        BRAND = new Identifier("brand");
        DEBUG_PATH = new Identifier("debug/path");
        DEBUG_NEIGHBORS_UPDATE = new Identifier("debug/neighbors_update");
        DEBUG_CAVES = new Identifier("debug/caves");
        DEBUG_STRUCTURES = new Identifier("debug/structures");
        DEBUG_WORLDGEN_ATTEMPT = new Identifier("debug/worldgen_attempt");
        DEBUG_POI_TICKET_COUNT = new Identifier("debug/poi_ticket_count");
        DEBUG_POI_ADDED = new Identifier("debug/poi_added");
        DEBUG_POI_REMOVED = new Identifier("debug/poi_removed");
        DEBUG_VILLAGE_SECTIONS = new Identifier("debug/village_sections");
        DEBUG_GOAL_SELECTOR = new Identifier("debug/goal_selector");
        DEBUG_BRAIN = new Identifier("debug/brain");
    }
}

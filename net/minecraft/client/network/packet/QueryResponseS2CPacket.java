package net.minecraft.client.network.packet;

import com.google.gson.TypeAdapterFactory;
import net.minecraft.util.LowercaseEnumTypeAdapterFactory;
import net.minecraft.text.Style;
import net.minecraft.text.TextComponent;
import java.lang.reflect.Type;
import com.google.gson.GsonBuilder;
import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.IOException;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.server.ServerMetadata;
import com.google.gson.Gson;
import net.minecraft.network.listener.ClientQueryPacketListener;
import net.minecraft.network.Packet;

public class QueryResponseS2CPacket implements Packet<ClientQueryPacketListener>
{
    private static final Gson GSON;
    private ServerMetadata metadata;
    
    public QueryResponseS2CPacket() {
    }
    
    public QueryResponseS2CPacket(final ServerMetadata serverMetadata) {
        this.metadata = serverMetadata;
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.metadata = JsonHelper.<ServerMetadata>deserialize(QueryResponseS2CPacket.GSON, buf.readString(32767), ServerMetadata.class);
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeString(QueryResponseS2CPacket.GSON.toJson(this.metadata));
    }
    
    @Override
    public void apply(final ClientQueryPacketListener listener) {
        listener.onResponse(this);
    }
    
    @Environment(EnvType.CLIENT)
    public ServerMetadata getServerMetadata() {
        return this.metadata;
    }
    
    static {
        GSON = new GsonBuilder().registerTypeAdapter(ServerMetadata.Version.class, new ServerMetadata.Version.Serializer()).registerTypeAdapter(ServerMetadata.Players.class, new ServerMetadata.Players.Deserializer()).registerTypeAdapter(ServerMetadata.class, new ServerMetadata.Deserializer()).registerTypeHierarchyAdapter(TextComponent.class, new TextComponent.Serializer()).registerTypeHierarchyAdapter(Style.class, new Style.Serializer()).registerTypeAdapterFactory(new LowercaseEnumTypeAdapterFactory()).create();
    }
}

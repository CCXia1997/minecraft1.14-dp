package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.minecraft.item.map.MapState;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import java.util.Collection;
import net.minecraft.item.map.MapIcon;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class MapUpdateS2CPacket implements Packet<ClientPlayPacketListener>
{
    private int id;
    private byte scale;
    private boolean showIcons;
    private boolean locked;
    private MapIcon[] icons;
    private int startX;
    private int startZ;
    private int width;
    private int height;
    private byte[] colors;
    
    public MapUpdateS2CPacket() {
    }
    
    public MapUpdateS2CPacket(final int id, final byte scale, final boolean showIcons, final boolean locked, final Collection<MapIcon> collection, final byte[] mapColors, final int startX, final int startZ, final int width, final int height) {
        this.id = id;
        this.scale = scale;
        this.showIcons = showIcons;
        this.locked = locked;
        this.icons = collection.<MapIcon>toArray(new MapIcon[collection.size()]);
        this.startX = startX;
        this.startZ = startZ;
        this.width = width;
        this.height = height;
        this.colors = new byte[width * height];
        for (int integer11 = 0; integer11 < width; ++integer11) {
            for (int integer12 = 0; integer12 < height; ++integer12) {
                this.colors[integer11 + integer12 * width] = mapColors[startX + integer11 + (startZ + integer12) * 128];
            }
        }
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.id = buf.readVarInt();
        this.scale = buf.readByte();
        this.showIcons = buf.readBoolean();
        this.locked = buf.readBoolean();
        this.icons = new MapIcon[buf.readVarInt()];
        for (int integer2 = 0; integer2 < this.icons.length; ++integer2) {
            final MapIcon.Type type3 = buf.<MapIcon.Type>readEnumConstant(MapIcon.Type.class);
            this.icons[integer2] = new MapIcon(type3, buf.readByte(), buf.readByte(), (byte)(buf.readByte() & 0xF), buf.readBoolean() ? buf.readTextComponent() : null);
        }
        this.width = buf.readUnsignedByte();
        if (this.width > 0) {
            this.height = buf.readUnsignedByte();
            this.startX = buf.readUnsignedByte();
            this.startZ = buf.readUnsignedByte();
            this.colors = buf.readByteArray();
        }
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeVarInt(this.id);
        buf.writeByte(this.scale);
        buf.writeBoolean(this.showIcons);
        buf.writeBoolean(this.locked);
        buf.writeVarInt(this.icons.length);
        for (final MapIcon mapIcon5 : this.icons) {
            buf.writeEnumConstant(mapIcon5.getType());
            buf.writeByte(mapIcon5.getX());
            buf.writeByte(mapIcon5.getZ());
            buf.writeByte(mapIcon5.getAngle() & 0xF);
            if (mapIcon5.getText() != null) {
                buf.writeBoolean(true);
                buf.writeTextComponent(mapIcon5.getText());
            }
            else {
                buf.writeBoolean(false);
            }
        }
        buf.writeByte(this.width);
        if (this.width > 0) {
            buf.writeByte(this.height);
            buf.writeByte(this.startX);
            buf.writeByte(this.startZ);
            buf.writeByteArray(this.colors);
        }
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onMapUpdate(this);
    }
    
    @Environment(EnvType.CLIENT)
    public int getId() {
        return this.id;
    }
    
    @Environment(EnvType.CLIENT)
    public void apply(final MapState mapState) {
        mapState.scale = this.scale;
        mapState.showIcons = this.showIcons;
        mapState.locked = this.locked;
        mapState.icons.clear();
        for (int integer2 = 0; integer2 < this.icons.length; ++integer2) {
            final MapIcon mapIcon3 = this.icons[integer2];
            mapState.icons.put("icon-" + integer2, mapIcon3);
        }
        for (int integer2 = 0; integer2 < this.width; ++integer2) {
            for (int integer3 = 0; integer3 < this.height; ++integer3) {
                mapState.colors[this.startX + integer2 + (this.startZ + integer3) * 128] = this.colors[integer2 + integer3 * this.width];
            }
        }
    }
}

package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.minecraft.entity.decoration.painting.PaintingMotive;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.registry.Registry;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.BlockPos;
import java.util.UUID;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class PaintingSpawnS2CPacket implements Packet<ClientPlayPacketListener>
{
    private int id;
    private UUID uuid;
    private BlockPos pos;
    private Direction facing;
    private int motive;
    
    public PaintingSpawnS2CPacket() {
    }
    
    public PaintingSpawnS2CPacket(final PaintingEntity entity) {
        this.id = entity.getEntityId();
        this.uuid = entity.getUuid();
        this.pos = entity.getDecorationBlockPos();
        this.facing = entity.facing;
        this.motive = Registry.MOTIVE.getRawId(entity.motive);
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.id = buf.readVarInt();
        this.uuid = buf.readUuid();
        this.motive = buf.readVarInt();
        this.pos = buf.readBlockPos();
        this.facing = Direction.fromHorizontal(buf.readUnsignedByte());
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeVarInt(this.id);
        buf.writeUuid(this.uuid);
        buf.writeVarInt(this.motive);
        buf.writeBlockPos(this.pos);
        buf.writeByte(this.facing.getHorizontal());
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onPaintingSpawn(this);
    }
    
    @Environment(EnvType.CLIENT)
    public int getId() {
        return this.id;
    }
    
    @Environment(EnvType.CLIENT)
    public UUID getPaintingUuid() {
        return this.uuid;
    }
    
    @Environment(EnvType.CLIENT)
    public BlockPos getPos() {
        return this.pos;
    }
    
    @Environment(EnvType.CLIENT)
    public Direction getFacing() {
        return this.facing;
    }
    
    @Environment(EnvType.CLIENT)
    public PaintingMotive getMotive() {
        return Registry.MOTIVE.get(this.motive);
    }
}

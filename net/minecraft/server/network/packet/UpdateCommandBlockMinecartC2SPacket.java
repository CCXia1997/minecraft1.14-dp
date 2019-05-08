package net.minecraft.server.network.packet;

import net.minecraft.network.listener.PacketListener;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.CommandBlockMinecartEntity;
import net.minecraft.world.CommandBlockExecutor;
import net.minecraft.world.World;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.Packet;

public class UpdateCommandBlockMinecartC2SPacket implements Packet<ServerPlayPacketListener>
{
    private int entityId;
    private String command;
    private boolean trackOutput;
    
    public UpdateCommandBlockMinecartC2SPacket() {
    }
    
    @Environment(EnvType.CLIENT)
    public UpdateCommandBlockMinecartC2SPacket(final int integer, final String string, final boolean boolean3) {
        this.entityId = integer;
        this.command = string;
        this.trackOutput = boolean3;
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.entityId = buf.readVarInt();
        this.command = buf.readString(32767);
        this.trackOutput = buf.readBoolean();
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeVarInt(this.entityId);
        buf.writeString(this.command);
        buf.writeBoolean(this.trackOutput);
    }
    
    @Override
    public void apply(final ServerPlayPacketListener listener) {
        listener.onUpdateCommandBlockMinecart(this);
    }
    
    @Nullable
    public CommandBlockExecutor getMinecartCommandExecutor(final World world) {
        final Entity entity2 = world.getEntityById(this.entityId);
        if (entity2 instanceof CommandBlockMinecartEntity) {
            return ((CommandBlockMinecartEntity)entity2).getCommandExecutor();
        }
        return null;
    }
    
    public String getCommand() {
        return this.command;
    }
    
    public boolean shouldTrackOutput() {
        return this.trackOutput;
    }
}

package net.minecraft.server.network.packet;

import net.minecraft.network.listener.PacketListener;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.Packet;

public class PlayerActionC2SPacket implements Packet<ServerPlayPacketListener>
{
    private BlockPos pos;
    private Direction direction;
    private Action action;
    
    public PlayerActionC2SPacket() {
    }
    
    @Environment(EnvType.CLIENT)
    public PlayerActionC2SPacket(final Action action, final BlockPos blockPos, final Direction direction) {
        this.action = action;
        this.pos = blockPos;
        this.direction = direction;
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.action = buf.<Action>readEnumConstant(Action.class);
        this.pos = buf.readBlockPos();
        this.direction = Direction.byId(buf.readUnsignedByte());
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeEnumConstant(this.action);
        buf.writeBlockPos(this.pos);
        buf.writeByte(this.direction.getId());
    }
    
    @Override
    public void apply(final ServerPlayPacketListener listener) {
        listener.onPlayerAction(this);
    }
    
    public BlockPos getPos() {
        return this.pos;
    }
    
    public Direction getDirection() {
        return this.direction;
    }
    
    public Action getAction() {
        return this.action;
    }
    
    public enum Action
    {
        a, 
        b, 
        c, 
        d, 
        e, 
        f, 
        g;
    }
}

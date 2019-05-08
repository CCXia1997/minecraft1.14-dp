package net.minecraft.server.network.packet;

import net.minecraft.network.listener.PacketListener;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.Packet;

public class UpdateCommandBlockC2SPacket implements Packet<ServerPlayPacketListener>
{
    private BlockPos pos;
    private String command;
    private boolean trackOutput;
    private boolean conditional;
    private boolean alwaysActive;
    private CommandBlockBlockEntity.Type type;
    
    public UpdateCommandBlockC2SPacket() {
    }
    
    @Environment(EnvType.CLIENT)
    public UpdateCommandBlockC2SPacket(final BlockPos blockPos, final String string, final CommandBlockBlockEntity.Type type, final boolean boolean4, final boolean boolean5, final boolean boolean6) {
        this.pos = blockPos;
        this.command = string;
        this.trackOutput = boolean4;
        this.conditional = boolean5;
        this.alwaysActive = boolean6;
        this.type = type;
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.pos = buf.readBlockPos();
        this.command = buf.readString(32767);
        this.type = buf.<CommandBlockBlockEntity.Type>readEnumConstant(CommandBlockBlockEntity.Type.class);
        final int integer2 = buf.readByte();
        this.trackOutput = ((integer2 & 0x1) != 0x0);
        this.conditional = ((integer2 & 0x2) != 0x0);
        this.alwaysActive = ((integer2 & 0x4) != 0x0);
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeBlockPos(this.pos);
        buf.writeString(this.command);
        buf.writeEnumConstant(this.type);
        int integer2 = 0;
        if (this.trackOutput) {
            integer2 |= 0x1;
        }
        if (this.conditional) {
            integer2 |= 0x2;
        }
        if (this.alwaysActive) {
            integer2 |= 0x4;
        }
        buf.writeByte(integer2);
    }
    
    @Override
    public void apply(final ServerPlayPacketListener listener) {
        listener.onUpdateCommandBlock(this);
    }
    
    public BlockPos getBlockPos() {
        return this.pos;
    }
    
    public String getCommand() {
        return this.command;
    }
    
    public boolean shouldTrackOutput() {
        return this.trackOutput;
    }
    
    public boolean isConditional() {
        return this.conditional;
    }
    
    public boolean isAlwaysActive() {
        return this.alwaysActive;
    }
    
    public CommandBlockBlockEntity.Type getType() {
        return this.type;
    }
}

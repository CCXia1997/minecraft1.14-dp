package net.minecraft.server.network.packet;

import net.minecraft.network.listener.PacketListener;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.TextComponent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.Packet;

public class UpdateSignC2SPacket implements Packet<ServerPlayPacketListener>
{
    private BlockPos pos;
    private String[] text;
    
    public UpdateSignC2SPacket() {
    }
    
    @Environment(EnvType.CLIENT)
    public UpdateSignC2SPacket(final BlockPos blockPos, final TextComponent textComponent2, final TextComponent textComponent3, final TextComponent textComponent4, final TextComponent textComponent5) {
        this.pos = blockPos;
        this.text = new String[] { textComponent2.getString(), textComponent3.getString(), textComponent4.getString(), textComponent5.getString() };
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.pos = buf.readBlockPos();
        this.text = new String[4];
        for (int integer2 = 0; integer2 < 4; ++integer2) {
            this.text[integer2] = buf.readString(384);
        }
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeBlockPos(this.pos);
        for (int integer2 = 0; integer2 < 4; ++integer2) {
            buf.writeString(this.text[integer2]);
        }
    }
    
    @Override
    public void apply(final ServerPlayPacketListener listener) {
        listener.onSignUpdate(this);
    }
    
    public BlockPos getPos() {
        return this.pos;
    }
    
    public String[] getText() {
        return this.text;
    }
}

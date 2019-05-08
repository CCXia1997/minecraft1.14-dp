package net.minecraft.server.network.packet;

import net.minecraft.network.listener.PacketListener;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.Packet;

public class PlayerInteractBlockC2SPacket implements Packet<ServerPlayPacketListener>
{
    private BlockHitResult a;
    private Hand hand;
    
    public PlayerInteractBlockC2SPacket() {
    }
    
    @Environment(EnvType.CLIENT)
    public PlayerInteractBlockC2SPacket(final Hand hand, final BlockHitResult blockHitResult) {
        this.hand = hand;
        this.a = blockHitResult;
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.hand = buf.<Hand>readEnumConstant(Hand.class);
        this.a = buf.readBlockHitResult();
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeEnumConstant(this.hand);
        buf.writeBlockHitResult(this.a);
    }
    
    @Override
    public void apply(final ServerPlayPacketListener listener) {
        listener.onPlayerInteractBlock(this);
    }
    
    public Hand getHand() {
        return this.hand;
    }
    
    public BlockHitResult getHitY() {
        return this.a;
    }
}

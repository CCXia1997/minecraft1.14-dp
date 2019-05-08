package net.minecraft.server.network.packet;

import net.minecraft.network.listener.PacketListener;
import java.io.IOException;
import java.util.UUID;
import net.minecraft.util.PacketByteBuf;
import com.mojang.authlib.GameProfile;
import net.minecraft.network.listener.ServerLoginPacketListener;
import net.minecraft.network.Packet;

public class LoginHelloC2SPacket implements Packet<ServerLoginPacketListener>
{
    private GameProfile profile;
    
    public LoginHelloC2SPacket() {
    }
    
    public LoginHelloC2SPacket(final GameProfile gameProfile) {
        this.profile = gameProfile;
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.profile = new GameProfile((UUID)null, buf.readString(16));
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeString(this.profile.getName());
    }
    
    @Override
    public void apply(final ServerLoginPacketListener listener) {
        listener.onHello(this);
    }
    
    public GameProfile getProfile() {
        return this.profile;
    }
}

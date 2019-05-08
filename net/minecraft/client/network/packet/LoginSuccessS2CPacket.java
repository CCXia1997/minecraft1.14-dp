package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.IOException;
import java.util.UUID;
import net.minecraft.util.PacketByteBuf;
import com.mojang.authlib.GameProfile;
import net.minecraft.network.listener.ClientLoginPacketListener;
import net.minecraft.network.Packet;

public class LoginSuccessS2CPacket implements Packet<ClientLoginPacketListener>
{
    private GameProfile profile;
    
    public LoginSuccessS2CPacket() {
    }
    
    public LoginSuccessS2CPacket(final GameProfile profile) {
        this.profile = profile;
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        final String string2 = buf.readString(36);
        final String string3 = buf.readString(16);
        final UUID uUID4 = UUID.fromString(string2);
        this.profile = new GameProfile(uUID4, string3);
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        final UUID uUID2 = this.profile.getId();
        buf.writeString((uUID2 == null) ? "" : uUID2.toString());
        buf.writeString(this.profile.getName());
    }
    
    @Override
    public void apply(final ClientLoginPacketListener listener) {
        listener.onLoginSuccess(this);
    }
    
    @Environment(EnvType.CLIENT)
    public GameProfile getProfile() {
        return this.profile;
    }
}

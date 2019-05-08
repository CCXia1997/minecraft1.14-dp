package net.minecraft.server.network.packet;

import net.minecraft.network.listener.PacketListener;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.AbsoluteHand;
import net.minecraft.client.options.ChatVisibility;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.Packet;

public class ClientSettingsC2SPacket implements Packet<ServerPlayPacketListener>
{
    private String language;
    private int viewDistance;
    private ChatVisibility chatVisibility;
    private boolean d;
    private int playerModelBitMask;
    private AbsoluteHand mainHand;
    
    public ClientSettingsC2SPacket() {
    }
    
    @Environment(EnvType.CLIENT)
    public ClientSettingsC2SPacket(final String language, final int viewDistance, final ChatVisibility chatVisibility, final boolean boolean4, final int modelBitMask, final AbsoluteHand mainHand) {
        this.language = language;
        this.viewDistance = viewDistance;
        this.chatVisibility = chatVisibility;
        this.d = boolean4;
        this.playerModelBitMask = modelBitMask;
        this.mainHand = mainHand;
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.language = buf.readString(16);
        this.viewDistance = buf.readByte();
        this.chatVisibility = buf.<ChatVisibility>readEnumConstant(ChatVisibility.class);
        this.d = buf.readBoolean();
        this.playerModelBitMask = buf.readUnsignedByte();
        this.mainHand = buf.<AbsoluteHand>readEnumConstant(AbsoluteHand.class);
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeString(this.language);
        buf.writeByte(this.viewDistance);
        buf.writeEnumConstant(this.chatVisibility);
        buf.writeBoolean(this.d);
        buf.writeByte(this.playerModelBitMask);
        buf.writeEnumConstant(this.mainHand);
    }
    
    @Override
    public void apply(final ServerPlayPacketListener listener) {
        listener.onClientSettings(this);
    }
    
    public String getLanguage() {
        return this.language;
    }
    
    public ChatVisibility getChatVisibility() {
        return this.chatVisibility;
    }
    
    public boolean e() {
        return this.d;
    }
    
    public int getPlayerModelBitMask() {
        return this.playerModelBitMask;
    }
    
    public AbsoluteHand getMainHand() {
        return this.mainHand;
    }
}

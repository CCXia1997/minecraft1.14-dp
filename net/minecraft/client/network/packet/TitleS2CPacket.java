package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import javax.annotation.Nullable;
import net.minecraft.text.TextComponent;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class TitleS2CPacket implements Packet<ClientPlayPacketListener>
{
    private Action action;
    private TextComponent text;
    private int fadeInTicks;
    private int stayTicks;
    private int fadeOutTicks;
    
    public TitleS2CPacket() {
    }
    
    public TitleS2CPacket(final Action action, final TextComponent textComponent) {
        this(action, textComponent, -1, -1, -1);
    }
    
    public TitleS2CPacket(final int fadeInTicks, final int stayTicks, final int fadeOutTicks) {
        this(Action.DISPLAY, null, fadeInTicks, stayTicks, fadeOutTicks);
    }
    
    public TitleS2CPacket(final Action action, @Nullable final TextComponent text, final int fadeInTicks, final int stayTicks, final int fadeOutTicks) {
        this.action = action;
        this.text = text;
        this.fadeInTicks = fadeInTicks;
        this.stayTicks = stayTicks;
        this.fadeOutTicks = fadeOutTicks;
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.action = buf.<Action>readEnumConstant(Action.class);
        if (this.action == Action.a || this.action == Action.b || this.action == Action.c) {
            this.text = buf.readTextComponent();
        }
        if (this.action == Action.DISPLAY) {
            this.fadeInTicks = buf.readInt();
            this.stayTicks = buf.readInt();
            this.fadeOutTicks = buf.readInt();
        }
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeEnumConstant(this.action);
        if (this.action == Action.a || this.action == Action.b || this.action == Action.c) {
            buf.writeTextComponent(this.text);
        }
        if (this.action == Action.DISPLAY) {
            buf.writeInt(this.fadeInTicks);
            buf.writeInt(this.stayTicks);
            buf.writeInt(this.fadeOutTicks);
        }
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onTitle(this);
    }
    
    @Environment(EnvType.CLIENT)
    public Action getAction() {
        return this.action;
    }
    
    @Environment(EnvType.CLIENT)
    public TextComponent getText() {
        return this.text;
    }
    
    @Environment(EnvType.CLIENT)
    public int getFadeInTicks() {
        return this.fadeInTicks;
    }
    
    @Environment(EnvType.CLIENT)
    public int getStayTicks() {
        return this.stayTicks;
    }
    
    @Environment(EnvType.CLIENT)
    public int getFadeOutTicks() {
        return this.fadeOutTicks;
    }
    
    public enum Action
    {
        a, 
        b, 
        c, 
        DISPLAY, 
        HIDE, 
        RESET;
    }
}

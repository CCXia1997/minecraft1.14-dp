package net.minecraft.server.network.packet;

import net.minecraft.network.listener.PacketListener;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.advancement.Advancement;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import javax.annotation.Nullable;
import net.minecraft.util.Identifier;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.Packet;

public class AdvancementTabC2SPacket implements Packet<ServerPlayPacketListener>
{
    private Action action;
    private Identifier tabToOpen;
    
    public AdvancementTabC2SPacket() {
    }
    
    @Environment(EnvType.CLIENT)
    public AdvancementTabC2SPacket(final Action action, @Nullable final Identifier tab) {
        this.action = action;
        this.tabToOpen = tab;
    }
    
    @Environment(EnvType.CLIENT)
    public static AdvancementTabC2SPacket open(final Advancement advancement) {
        return new AdvancementTabC2SPacket(Action.a, advancement.getId());
    }
    
    @Environment(EnvType.CLIENT)
    public static AdvancementTabC2SPacket close() {
        return new AdvancementTabC2SPacket(Action.b, null);
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.action = buf.<Action>readEnumConstant(Action.class);
        if (this.action == Action.a) {
            this.tabToOpen = buf.readIdentifier();
        }
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeEnumConstant(this.action);
        if (this.action == Action.a) {
            buf.writeIdentifier(this.tabToOpen);
        }
    }
    
    @Override
    public void apply(final ServerPlayPacketListener listener) {
        listener.onAdvancementTab(this);
    }
    
    public Action getAction() {
        return this.action;
    }
    
    public Identifier getTabToOpen() {
        return this.tabToOpen;
    }
    
    public enum Action
    {
        a, 
        b;
    }
}

package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.text.TextComponent;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class ScoreboardObjectiveUpdateS2CPacket implements Packet<ClientPlayPacketListener>
{
    private String name;
    private TextComponent displayName;
    private ScoreboardCriterion.RenderType type;
    private int mode;
    
    public ScoreboardObjectiveUpdateS2CPacket() {
    }
    
    public ScoreboardObjectiveUpdateS2CPacket(final ScoreboardObjective scoreboardObjective, final int integer) {
        this.name = scoreboardObjective.getName();
        this.displayName = scoreboardObjective.getDisplayName();
        this.type = scoreboardObjective.getRenderType();
        this.mode = integer;
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.name = buf.readString(16);
        this.mode = buf.readByte();
        if (this.mode == 0 || this.mode == 2) {
            this.displayName = buf.readTextComponent();
            this.type = buf.<ScoreboardCriterion.RenderType>readEnumConstant(ScoreboardCriterion.RenderType.class);
        }
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeString(this.name);
        buf.writeByte(this.mode);
        if (this.mode == 0 || this.mode == 2) {
            buf.writeTextComponent(this.displayName);
            buf.writeEnumConstant(this.type);
        }
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onScoreboardObjectiveUpdate(this);
    }
    
    @Environment(EnvType.CLIENT)
    public String getName() {
        return this.name;
    }
    
    @Environment(EnvType.CLIENT)
    public TextComponent getDisplayName() {
        return this.displayName;
    }
    
    @Environment(EnvType.CLIENT)
    public int getMode() {
        return this.mode;
    }
    
    @Environment(EnvType.CLIENT)
    public ScoreboardCriterion.RenderType getType() {
        return this.type;
    }
}

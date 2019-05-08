package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.world.level.LevelGeneratorType;
import net.minecraft.world.GameMode;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class PlayerRespawnS2CPacket implements Packet<ClientPlayPacketListener>
{
    private DimensionType dimension;
    private GameMode gameMode;
    private LevelGeneratorType generatorType;
    
    public PlayerRespawnS2CPacket() {
    }
    
    public PlayerRespawnS2CPacket(final DimensionType dimensionType, final LevelGeneratorType levelGeneratorType, final GameMode gameMode) {
        this.dimension = dimensionType;
        this.gameMode = gameMode;
        this.generatorType = levelGeneratorType;
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onPlayerRespawn(this);
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.dimension = DimensionType.byRawId(buf.readInt());
        this.gameMode = GameMode.byId(buf.readUnsignedByte());
        this.generatorType = LevelGeneratorType.getTypeFromName(buf.readString(16));
        if (this.generatorType == null) {
            this.generatorType = LevelGeneratorType.DEFAULT;
        }
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeInt(this.dimension.getRawId());
        buf.writeByte(this.gameMode.getId());
        buf.writeString(this.generatorType.getName());
    }
    
    @Environment(EnvType.CLIENT)
    public DimensionType getDimension() {
        return this.dimension;
    }
    
    @Environment(EnvType.CLIENT)
    public GameMode getGameMode() {
        return this.gameMode;
    }
    
    @Environment(EnvType.CLIENT)
    public LevelGeneratorType getGeneratorType() {
        return this.generatorType;
    }
}

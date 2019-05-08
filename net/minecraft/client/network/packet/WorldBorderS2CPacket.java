package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class WorldBorderS2CPacket implements Packet<ClientPlayPacketListener>
{
    private Type type;
    private int portalTeleportPosLimit;
    private double centerX;
    private double centerZ;
    private double size;
    private double oldSize;
    private long interpolationDuration;
    private int warningTime;
    private int warningBlocks;
    
    public WorldBorderS2CPacket() {
    }
    
    public WorldBorderS2CPacket(final WorldBorder border, final Type type) {
        this.type = type;
        this.centerX = border.getCenterX();
        this.centerZ = border.getCenterZ();
        this.oldSize = border.getSize();
        this.size = border.getTargetSize();
        this.interpolationDuration = border.getTargetRemainingTime();
        this.portalTeleportPosLimit = border.getMaxWorldBorderRadius();
        this.warningBlocks = border.getWarningBlocks();
        this.warningTime = border.getWarningTime();
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.type = buf.<Type>readEnumConstant(Type.class);
        switch (this.type) {
            case SET_SIZE: {
                this.size = buf.readDouble();
                break;
            }
            case INTERPOLATE_SIZE: {
                this.oldSize = buf.readDouble();
                this.size = buf.readDouble();
                this.interpolationDuration = buf.readVarLong();
                break;
            }
            case SET_CENTER: {
                this.centerX = buf.readDouble();
                this.centerZ = buf.readDouble();
                break;
            }
            case SET_WARNING_BLOCKS: {
                this.warningBlocks = buf.readVarInt();
                break;
            }
            case SET_WARNING_TIME: {
                this.warningTime = buf.readVarInt();
                break;
            }
            case INITIALIZE: {
                this.centerX = buf.readDouble();
                this.centerZ = buf.readDouble();
                this.oldSize = buf.readDouble();
                this.size = buf.readDouble();
                this.interpolationDuration = buf.readVarLong();
                this.portalTeleportPosLimit = buf.readVarInt();
                this.warningBlocks = buf.readVarInt();
                this.warningTime = buf.readVarInt();
                break;
            }
        }
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeEnumConstant(this.type);
        switch (this.type) {
            case SET_SIZE: {
                buf.writeDouble(this.size);
                break;
            }
            case INTERPOLATE_SIZE: {
                buf.writeDouble(this.oldSize);
                buf.writeDouble(this.size);
                buf.writeVarLong(this.interpolationDuration);
                break;
            }
            case SET_CENTER: {
                buf.writeDouble(this.centerX);
                buf.writeDouble(this.centerZ);
                break;
            }
            case SET_WARNING_TIME: {
                buf.writeVarInt(this.warningTime);
                break;
            }
            case SET_WARNING_BLOCKS: {
                buf.writeVarInt(this.warningBlocks);
                break;
            }
            case INITIALIZE: {
                buf.writeDouble(this.centerX);
                buf.writeDouble(this.centerZ);
                buf.writeDouble(this.oldSize);
                buf.writeDouble(this.size);
                buf.writeVarLong(this.interpolationDuration);
                buf.writeVarInt(this.portalTeleportPosLimit);
                buf.writeVarInt(this.warningBlocks);
                buf.writeVarInt(this.warningTime);
                break;
            }
        }
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onWorldBorder(this);
    }
    
    @Environment(EnvType.CLIENT)
    public void apply(final WorldBorder worldBorder) {
        switch (this.type) {
            case SET_SIZE: {
                worldBorder.setSize(this.size);
                break;
            }
            case INTERPOLATE_SIZE: {
                worldBorder.interpolateSize(this.oldSize, this.size, this.interpolationDuration);
                break;
            }
            case SET_CENTER: {
                worldBorder.setCenter(this.centerX, this.centerZ);
                break;
            }
            case INITIALIZE: {
                worldBorder.setCenter(this.centerX, this.centerZ);
                if (this.interpolationDuration > 0L) {
                    worldBorder.interpolateSize(this.oldSize, this.size, this.interpolationDuration);
                }
                else {
                    worldBorder.setSize(this.size);
                }
                worldBorder.setMaxWorldBorderRadius(this.portalTeleportPosLimit);
                worldBorder.setWarningBlocks(this.warningBlocks);
                worldBorder.setWarningTime(this.warningTime);
                break;
            }
            case SET_WARNING_TIME: {
                worldBorder.setWarningTime(this.warningTime);
                break;
            }
            case SET_WARNING_BLOCKS: {
                worldBorder.setWarningBlocks(this.warningBlocks);
                break;
            }
        }
    }
    
    public enum Type
    {
        SET_SIZE, 
        INTERPOLATE_SIZE, 
        SET_CENTER, 
        INITIALIZE, 
        SET_WARNING_TIME, 
        SET_WARNING_BLOCKS;
    }
}

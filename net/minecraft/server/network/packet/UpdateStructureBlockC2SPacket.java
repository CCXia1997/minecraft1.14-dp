package net.minecraft.server.network.packet;

import net.minecraft.network.listener.PacketListener;
import java.io.IOException;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.PacketByteBuf;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.BlockMirror;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.Packet;

public class UpdateStructureBlockC2SPacket implements Packet<ServerPlayPacketListener>
{
    private BlockPos pos;
    private StructureBlockBlockEntity.Action action;
    private StructureBlockMode mode;
    private String structureName;
    private BlockPos offset;
    private BlockPos size;
    private BlockMirror mirror;
    private BlockRotation rotation;
    private String metadata;
    private boolean ignoreEntities;
    private boolean showAir;
    private boolean showBoundingBox;
    private float integrity;
    private long seed;
    
    public UpdateStructureBlockC2SPacket() {
    }
    
    @Environment(EnvType.CLIENT)
    public UpdateStructureBlockC2SPacket(final BlockPos pos, final StructureBlockBlockEntity.Action action, final StructureBlockMode mode, final String structureName, final BlockPos offset, final BlockPos size, final BlockMirror mirror, final BlockRotation rotation, final String metadata, final boolean ignoreEntities, final boolean showAir, final boolean showBoundingBox, final float integrity, final long seed) {
        this.pos = pos;
        this.action = action;
        this.mode = mode;
        this.structureName = structureName;
        this.offset = offset;
        this.size = size;
        this.mirror = mirror;
        this.rotation = rotation;
        this.metadata = metadata;
        this.ignoreEntities = ignoreEntities;
        this.showAir = showAir;
        this.showBoundingBox = showBoundingBox;
        this.integrity = integrity;
        this.seed = seed;
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.pos = buf.readBlockPos();
        this.action = buf.<StructureBlockBlockEntity.Action>readEnumConstant(StructureBlockBlockEntity.Action.class);
        this.mode = buf.<StructureBlockMode>readEnumConstant(StructureBlockMode.class);
        this.structureName = buf.readString(32767);
        this.offset = new BlockPos(MathHelper.clamp(buf.readByte(), -32, 32), MathHelper.clamp(buf.readByte(), -32, 32), MathHelper.clamp(buf.readByte(), -32, 32));
        this.size = new BlockPos(MathHelper.clamp(buf.readByte(), 0, 32), MathHelper.clamp(buf.readByte(), 0, 32), MathHelper.clamp(buf.readByte(), 0, 32));
        this.mirror = buf.<BlockMirror>readEnumConstant(BlockMirror.class);
        this.rotation = buf.<BlockRotation>readEnumConstant(BlockRotation.class);
        this.metadata = buf.readString(12);
        this.integrity = MathHelper.clamp(buf.readFloat(), 0.0f, 1.0f);
        this.seed = buf.readVarLong();
        final int integer2 = buf.readByte();
        this.ignoreEntities = ((integer2 & 0x1) != 0x0);
        this.showAir = ((integer2 & 0x2) != 0x0);
        this.showBoundingBox = ((integer2 & 0x4) != 0x0);
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeBlockPos(this.pos);
        buf.writeEnumConstant(this.action);
        buf.writeEnumConstant(this.mode);
        buf.writeString(this.structureName);
        buf.writeByte(this.offset.getX());
        buf.writeByte(this.offset.getY());
        buf.writeByte(this.offset.getZ());
        buf.writeByte(this.size.getX());
        buf.writeByte(this.size.getY());
        buf.writeByte(this.size.getZ());
        buf.writeEnumConstant(this.mirror);
        buf.writeEnumConstant(this.rotation);
        buf.writeString(this.metadata);
        buf.writeFloat(this.integrity);
        buf.writeVarLong(this.seed);
        int integer2 = 0;
        if (this.ignoreEntities) {
            integer2 |= 0x1;
        }
        if (this.showAir) {
            integer2 |= 0x2;
        }
        if (this.showBoundingBox) {
            integer2 |= 0x4;
        }
        buf.writeByte(integer2);
    }
    
    @Override
    public void apply(final ServerPlayPacketListener listener) {
        listener.onStructureBlockUpdate(this);
    }
    
    public BlockPos getPos() {
        return this.pos;
    }
    
    public StructureBlockBlockEntity.Action getAction() {
        return this.action;
    }
    
    public StructureBlockMode getMode() {
        return this.mode;
    }
    
    public String getStructureName() {
        return this.structureName;
    }
    
    public BlockPos getOffset() {
        return this.offset;
    }
    
    public BlockPos getSize() {
        return this.size;
    }
    
    public BlockMirror getMirror() {
        return this.mirror;
    }
    
    public BlockRotation getRotation() {
        return this.rotation;
    }
    
    public String getMetadata() {
        return this.metadata;
    }
    
    public boolean getIgnoreEntities() {
        return this.ignoreEntities;
    }
    
    public boolean shouldShowAir() {
        return this.showAir;
    }
    
    public boolean shouldShowBoundingBox() {
        return this.showBoundingBox;
    }
    
    public float getIntegrity() {
        return this.integrity;
    }
    
    public long getSeed() {
        return this.seed;
    }
}

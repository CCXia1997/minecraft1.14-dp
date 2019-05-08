package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import javax.annotation.Nullable;
import net.minecraft.world.World;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.Entity;
import net.minecraft.command.arguments.EntityAnchorArgumentType;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class LookAtS2CPacket implements Packet<ClientPlayPacketListener>
{
    private double targetX;
    private double targetY;
    private double targetZ;
    private int entityId;
    private EntityAnchorArgumentType.EntityAnchor selfAnchor;
    private EntityAnchorArgumentType.EntityAnchor targetAnchor;
    private boolean lookAtEntity;
    
    public LookAtS2CPacket() {
    }
    
    public LookAtS2CPacket(final EntityAnchorArgumentType.EntityAnchor entityAnchor, final double double2, final double double4, final double double6) {
        this.selfAnchor = entityAnchor;
        this.targetX = double2;
        this.targetY = double4;
        this.targetZ = double6;
    }
    
    public LookAtS2CPacket(final EntityAnchorArgumentType.EntityAnchor selfAnchor, final Entity entity, final EntityAnchorArgumentType.EntityAnchor targetAnchor) {
        this.selfAnchor = selfAnchor;
        this.entityId = entity.getEntityId();
        this.targetAnchor = targetAnchor;
        final Vec3d vec3d4 = targetAnchor.positionAt(entity);
        this.targetX = vec3d4.x;
        this.targetY = vec3d4.y;
        this.targetZ = vec3d4.z;
        this.lookAtEntity = true;
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.selfAnchor = buf.<EntityAnchorArgumentType.EntityAnchor>readEnumConstant(EntityAnchorArgumentType.EntityAnchor.class);
        this.targetX = buf.readDouble();
        this.targetY = buf.readDouble();
        this.targetZ = buf.readDouble();
        if (buf.readBoolean()) {
            this.lookAtEntity = true;
            this.entityId = buf.readVarInt();
            this.targetAnchor = buf.<EntityAnchorArgumentType.EntityAnchor>readEnumConstant(EntityAnchorArgumentType.EntityAnchor.class);
        }
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeEnumConstant(this.selfAnchor);
        buf.writeDouble(this.targetX);
        buf.writeDouble(this.targetY);
        buf.writeDouble(this.targetZ);
        buf.writeBoolean(this.lookAtEntity);
        if (this.lookAtEntity) {
            buf.writeVarInt(this.entityId);
            buf.writeEnumConstant(this.targetAnchor);
        }
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onLookAt(this);
    }
    
    @Environment(EnvType.CLIENT)
    public EntityAnchorArgumentType.EntityAnchor getSelfAnchor() {
        return this.selfAnchor;
    }
    
    @Nullable
    @Environment(EnvType.CLIENT)
    public Vec3d getTargetPosition(final World world) {
        if (!this.lookAtEntity) {
            return new Vec3d(this.targetX, this.targetY, this.targetZ);
        }
        final Entity entity2 = world.getEntityById(this.entityId);
        if (entity2 == null) {
            return new Vec3d(this.targetX, this.targetY, this.targetZ);
        }
        return this.targetAnchor.positionAt(entity2);
    }
}

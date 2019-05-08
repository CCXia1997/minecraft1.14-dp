package net.minecraft.server.network.packet;

import net.minecraft.network.listener.PacketListener;
import javax.annotation.Nullable;
import net.minecraft.world.World;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.Packet;

public class PlayerInteractEntityC2SPacket implements Packet<ServerPlayPacketListener>
{
    private int entityId;
    private InteractionType type;
    private Vec3d hitPos;
    private Hand hand;
    
    public PlayerInteractEntityC2SPacket() {
    }
    
    public PlayerInteractEntityC2SPacket(final Entity entity) {
        this.entityId = entity.getEntityId();
        this.type = InteractionType.b;
    }
    
    @Environment(EnvType.CLIENT)
    public PlayerInteractEntityC2SPacket(final Entity entity, final Hand hand) {
        this.entityId = entity.getEntityId();
        this.type = InteractionType.a;
        this.hand = hand;
    }
    
    @Environment(EnvType.CLIENT)
    public PlayerInteractEntityC2SPacket(final Entity entity, final Hand hand, final Vec3d vec3d) {
        this.entityId = entity.getEntityId();
        this.type = InteractionType.c;
        this.hand = hand;
        this.hitPos = vec3d;
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.entityId = buf.readVarInt();
        this.type = buf.<InteractionType>readEnumConstant(InteractionType.class);
        if (this.type == InteractionType.c) {
            this.hitPos = new Vec3d(buf.readFloat(), buf.readFloat(), buf.readFloat());
        }
        if (this.type == InteractionType.a || this.type == InteractionType.c) {
            this.hand = buf.<Hand>readEnumConstant(Hand.class);
        }
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeVarInt(this.entityId);
        buf.writeEnumConstant(this.type);
        if (this.type == InteractionType.c) {
            buf.writeFloat((float)this.hitPos.x);
            buf.writeFloat((float)this.hitPos.y);
            buf.writeFloat((float)this.hitPos.z);
        }
        if (this.type == InteractionType.a || this.type == InteractionType.c) {
            buf.writeEnumConstant(this.hand);
        }
    }
    
    @Override
    public void apply(final ServerPlayPacketListener listener) {
        listener.onPlayerInteractEntity(this);
    }
    
    @Nullable
    public Entity getEntity(final World world) {
        return world.getEntityById(this.entityId);
    }
    
    public InteractionType getType() {
        return this.type;
    }
    
    public Hand getHand() {
        return this.hand;
    }
    
    public Vec3d getHitPosition() {
        return this.hitPos;
    }
    
    public enum InteractionType
    {
        a, 
        b, 
        c;
    }
}

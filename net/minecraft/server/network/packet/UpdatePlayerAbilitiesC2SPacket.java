package net.minecraft.server.network.packet;

import net.minecraft.network.listener.PacketListener;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.Packet;

public class UpdatePlayerAbilitiesC2SPacket implements Packet<ServerPlayPacketListener>
{
    private boolean invulnerable;
    private boolean flying;
    private boolean allowFlying;
    private boolean creativeMode;
    private float flySpeed;
    private float walkSpeed;
    
    public UpdatePlayerAbilitiesC2SPacket() {
    }
    
    public UpdatePlayerAbilitiesC2SPacket(final PlayerAbilities abilities) {
        this.setInvulnerable(abilities.invulnerable);
        this.setFlying(abilities.flying);
        this.setAllowFlying(abilities.allowFlying);
        this.setCreativeMode(abilities.creativeMode);
        this.setFlySpeed(abilities.getFlySpeed());
        this.setWalkSpeed(abilities.getWalkSpeed());
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        final byte byte2 = buf.readByte();
        this.setInvulnerable((byte2 & 0x1) > 0);
        this.setFlying((byte2 & 0x2) > 0);
        this.setAllowFlying((byte2 & 0x4) > 0);
        this.setCreativeMode((byte2 & 0x8) > 0);
        this.setFlySpeed(buf.readFloat());
        this.setWalkSpeed(buf.readFloat());
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        byte byte2 = 0;
        if (this.isInvulnerable()) {
            byte2 |= 0x1;
        }
        if (this.isFlying()) {
            byte2 |= 0x2;
        }
        if (this.isFlyingAllowed()) {
            byte2 |= 0x4;
        }
        if (this.isCreativeMode()) {
            byte2 |= 0x8;
        }
        buf.writeByte(byte2);
        buf.writeFloat(this.flySpeed);
        buf.writeFloat(this.walkSpeed);
    }
    
    @Override
    public void apply(final ServerPlayPacketListener listener) {
        listener.onPlayerAbilities(this);
    }
    
    public boolean isInvulnerable() {
        return this.invulnerable;
    }
    
    public void setInvulnerable(final boolean boolean1) {
        this.invulnerable = boolean1;
    }
    
    public boolean isFlying() {
        return this.flying;
    }
    
    public void setFlying(final boolean boolean1) {
        this.flying = boolean1;
    }
    
    public boolean isFlyingAllowed() {
        return this.allowFlying;
    }
    
    public void setAllowFlying(final boolean boolean1) {
        this.allowFlying = boolean1;
    }
    
    public boolean isCreativeMode() {
        return this.creativeMode;
    }
    
    public void setCreativeMode(final boolean boolean1) {
        this.creativeMode = boolean1;
    }
    
    public void setFlySpeed(final float float1) {
        this.flySpeed = float1;
    }
    
    public void setWalkSpeed(final float float1) {
        this.walkSpeed = float1;
    }
}

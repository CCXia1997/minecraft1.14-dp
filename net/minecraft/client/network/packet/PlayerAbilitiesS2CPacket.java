package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class PlayerAbilitiesS2CPacket implements Packet<ClientPlayPacketListener>
{
    private boolean invulnerable;
    private boolean flying;
    private boolean allowFlying;
    private boolean creativeMode;
    private float flySpeed;
    private float fovModifier;
    
    public PlayerAbilitiesS2CPacket() {
    }
    
    public PlayerAbilitiesS2CPacket(final PlayerAbilities playerAbilities) {
        this.setInvulnerable(playerAbilities.invulnerable);
        this.setFlying(playerAbilities.flying);
        this.setAllowFlying(playerAbilities.allowFlying);
        this.setCreativeMode(playerAbilities.creativeMode);
        this.setFlySpeed(playerAbilities.getFlySpeed());
        this.setFovModifier(playerAbilities.getWalkSpeed());
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        final byte byte2 = buf.readByte();
        this.setInvulnerable((byte2 & 0x1) > 0);
        this.setFlying((byte2 & 0x2) > 0);
        this.setAllowFlying((byte2 & 0x4) > 0);
        this.setCreativeMode((byte2 & 0x8) > 0);
        this.setFlySpeed(buf.readFloat());
        this.setFovModifier(buf.readFloat());
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
        if (this.allowFlying()) {
            byte2 |= 0x4;
        }
        if (this.isCreativeMode()) {
            byte2 |= 0x8;
        }
        buf.writeByte(byte2);
        buf.writeFloat(this.flySpeed);
        buf.writeFloat(this.fovModifier);
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
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
    
    public boolean allowFlying() {
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
    
    @Environment(EnvType.CLIENT)
    public float getFlySpeed() {
        return this.flySpeed;
    }
    
    public void setFlySpeed(final float float1) {
        this.flySpeed = float1;
    }
    
    @Environment(EnvType.CLIENT)
    public float getFovModifier() {
        return this.fovModifier;
    }
    
    public void setFovModifier(final float float1) {
        this.fovModifier = float1;
    }
}

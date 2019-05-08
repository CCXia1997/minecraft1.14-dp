package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.text.TextComponent;
import java.util.UUID;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class BossBarS2CPacket implements Packet<ClientPlayPacketListener>
{
    private UUID uuid;
    private Type type;
    private TextComponent name;
    private float percent;
    private BossBar.Color color;
    private BossBar.Style overlay;
    private boolean darkenSky;
    private boolean dragonMusic;
    private boolean thickenFog;
    
    public BossBarS2CPacket() {
    }
    
    public BossBarS2CPacket(final Type action, final BossBar bossBar) {
        this.type = action;
        this.uuid = bossBar.getUuid();
        this.name = bossBar.getName();
        this.percent = bossBar.getPercent();
        this.color = bossBar.getColor();
        this.overlay = bossBar.getOverlay();
        this.darkenSky = bossBar.getDarkenSky();
        this.dragonMusic = bossBar.hasDragonMusic();
        this.thickenFog = bossBar.getThickenFog();
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.uuid = buf.readUuid();
        this.type = buf.<Type>readEnumConstant(Type.class);
        switch (this.type) {
            case ADD: {
                this.name = buf.readTextComponent();
                this.percent = buf.readFloat();
                this.color = buf.<BossBar.Color>readEnumConstant(BossBar.Color.class);
                this.overlay = buf.<BossBar.Style>readEnumConstant(BossBar.Style.class);
                this.setFlagBitfield(buf.readUnsignedByte());
            }
            case UPDATE_PCT: {
                this.percent = buf.readFloat();
                break;
            }
            case UPDATE_TITLE: {
                this.name = buf.readTextComponent();
                break;
            }
            case UPDATE_STYLE: {
                this.color = buf.<BossBar.Color>readEnumConstant(BossBar.Color.class);
                this.overlay = buf.<BossBar.Style>readEnumConstant(BossBar.Style.class);
                break;
            }
            case UPDATE_FLAGS: {
                this.setFlagBitfield(buf.readUnsignedByte());
                break;
            }
        }
    }
    
    private void setFlagBitfield(final int integer) {
        this.darkenSky = ((integer & 0x1) > 0);
        this.dragonMusic = ((integer & 0x2) > 0);
        this.thickenFog = ((integer & 0x4) > 0);
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeUuid(this.uuid);
        buf.writeEnumConstant(this.type);
        switch (this.type) {
            case ADD: {
                buf.writeTextComponent(this.name);
                buf.writeFloat(this.percent);
                buf.writeEnumConstant(this.color);
                buf.writeEnumConstant(this.overlay);
                buf.writeByte(this.getFlagBitfield());
            }
            case UPDATE_PCT: {
                buf.writeFloat(this.percent);
                break;
            }
            case UPDATE_TITLE: {
                buf.writeTextComponent(this.name);
                break;
            }
            case UPDATE_STYLE: {
                buf.writeEnumConstant(this.color);
                buf.writeEnumConstant(this.overlay);
                break;
            }
            case UPDATE_FLAGS: {
                buf.writeByte(this.getFlagBitfield());
                break;
            }
        }
    }
    
    private int getFlagBitfield() {
        int integer1 = 0;
        if (this.darkenSky) {
            integer1 |= 0x1;
        }
        if (this.dragonMusic) {
            integer1 |= 0x2;
        }
        if (this.thickenFog) {
            integer1 |= 0x4;
        }
        return integer1;
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onBossBar(this);
    }
    
    @Environment(EnvType.CLIENT)
    public UUID getUuid() {
        return this.uuid;
    }
    
    @Environment(EnvType.CLIENT)
    public Type getType() {
        return this.type;
    }
    
    @Environment(EnvType.CLIENT)
    public TextComponent getName() {
        return this.name;
    }
    
    @Environment(EnvType.CLIENT)
    public float getPercent() {
        return this.percent;
    }
    
    @Environment(EnvType.CLIENT)
    public BossBar.Color getColor() {
        return this.color;
    }
    
    @Environment(EnvType.CLIENT)
    public BossBar.Style getOverlay() {
        return this.overlay;
    }
    
    @Environment(EnvType.CLIENT)
    public boolean shouldDarkenSky() {
        return this.darkenSky;
    }
    
    @Environment(EnvType.CLIENT)
    public boolean hasDragonMusic() {
        return this.dragonMusic;
    }
    
    @Environment(EnvType.CLIENT)
    public boolean shouldThickenFog() {
        return this.thickenFog;
    }
    
    public enum Type
    {
        ADD, 
        REMOVE, 
        UPDATE_PCT, 
        UPDATE_TITLE, 
        UPDATE_STYLE, 
        UPDATE_FLAGS;
    }
}

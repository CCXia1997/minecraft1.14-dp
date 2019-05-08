package net.minecraft.client.gui.hud;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.SystemUtil;
import net.minecraft.client.network.packet.BossBarS2CPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.boss.BossBar;

@Environment(EnvType.CLIENT)
public class ClientBossBar extends BossBar
{
    protected float healthLatest;
    protected long timeHealthSet;
    
    public ClientBossBar(final BossBarS2CPacket packet) {
        super(packet.getUuid(), packet.getName(), packet.getColor(), packet.getOverlay());
        this.healthLatest = packet.getPercent();
        this.percent = packet.getPercent();
        this.timeHealthSet = SystemUtil.getMeasuringTimeMs();
        this.setDarkenSky(packet.shouldDarkenSky());
        this.setDragonMusic(packet.hasDragonMusic());
        this.setThickenFog(packet.shouldThickenFog());
    }
    
    @Override
    public void setPercent(final float percentage) {
        this.percent = this.getPercent();
        this.healthLatest = percentage;
        this.timeHealthSet = SystemUtil.getMeasuringTimeMs();
    }
    
    @Override
    public float getPercent() {
        final long long1 = SystemUtil.getMeasuringTimeMs() - this.timeHealthSet;
        final float float3 = MathHelper.clamp(long1 / 100.0f, 0.0f, 1.0f);
        return MathHelper.lerp(float3, this.percent, this.healthLatest);
    }
    
    public void handlePacket(final BossBarS2CPacket packet) {
        switch (packet.getType()) {
            case UPDATE_TITLE: {
                this.setName(packet.getName());
                break;
            }
            case UPDATE_PCT: {
                this.setPercent(packet.getPercent());
                break;
            }
            case UPDATE_STYLE: {
                this.setColor(packet.getColor());
                this.setOverlay(packet.getOverlay());
                break;
            }
            case UPDATE_FLAGS: {
                this.setDarkenSky(packet.shouldDarkenSky());
                this.setDragonMusic(packet.hasDragonMusic());
                break;
            }
        }
    }
}

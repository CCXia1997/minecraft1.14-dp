package net.minecraft.client.gui.hud;

import net.minecraft.client.network.packet.BossBarS2CPacket;
import java.util.Iterator;
import net.minecraft.entity.boss.BossBar;
import com.mojang.blaze3d.platform.GlStateManager;
import com.google.common.collect.Maps;
import java.util.UUID;
import java.util.Map;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;

@Environment(EnvType.CLIENT)
public class BossBarHud extends DrawableHelper
{
    private static final Identifier BAR_TEX;
    private final MinecraftClient client;
    private final Map<UUID, ClientBossBar> bossBars;
    
    public BossBarHud(final MinecraftClient client) {
        this.bossBars = Maps.newLinkedHashMap();
        this.client = client;
    }
    
    public void draw() {
        if (this.bossBars.isEmpty()) {
            return;
        }
        final int integer1 = this.client.window.getScaledWidth();
        int integer2 = 12;
        for (final ClientBossBar clientBossBar4 : this.bossBars.values()) {
            final int integer3 = integer1 / 2 - 91;
            final int integer4 = integer2;
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            this.client.getTextureManager().bindTexture(BossBarHud.BAR_TEX);
            this.drawBossBar(integer3, integer4, clientBossBar4);
            final String string7 = clientBossBar4.getName().getFormattedText();
            final int integer5 = this.client.textRenderer.getStringWidth(string7);
            final int integer6 = integer1 / 2 - integer5 / 2;
            final int integer7 = integer4 - 9;
            this.client.textRenderer.drawWithShadow(string7, (float)integer6, (float)integer7, 16777215);
            final int n = integer2;
            final int n2 = 10;
            this.client.textRenderer.getClass();
            integer2 = n + (n2 + 9);
            if (integer2 >= this.client.window.getScaledHeight() / 3) {
                break;
            }
        }
    }
    
    private void drawBossBar(final int x, final int y, final BossBar bossBar) {
        this.blit(x, y, 0, bossBar.getColor().ordinal() * 5 * 2, 182, 5);
        if (bossBar.getOverlay() != BossBar.Style.a) {
            this.blit(x, y, 0, 80 + (bossBar.getOverlay().ordinal() - 1) * 5 * 2, 182, 5);
        }
        final int integer4 = (int)(bossBar.getPercent() * 183.0f);
        if (integer4 > 0) {
            this.blit(x, y, 0, bossBar.getColor().ordinal() * 5 * 2 + 5, integer4, 5);
            if (bossBar.getOverlay() != BossBar.Style.a) {
                this.blit(x, y, 0, 80 + (bossBar.getOverlay().ordinal() - 1) * 5 * 2 + 5, integer4, 5);
            }
        }
    }
    
    public void handlePacket(final BossBarS2CPacket packet) {
        if (packet.getType() == BossBarS2CPacket.Type.ADD) {
            this.bossBars.put(packet.getUuid(), new ClientBossBar(packet));
        }
        else if (packet.getType() == BossBarS2CPacket.Type.REMOVE) {
            this.bossBars.remove(packet.getUuid());
        }
        else {
            this.bossBars.get(packet.getUuid()).handlePacket(packet);
        }
    }
    
    public void clear() {
        this.bossBars.clear();
    }
    
    public boolean shouldPlayDragonMusic() {
        if (!this.bossBars.isEmpty()) {
            for (final BossBar bossBar2 : this.bossBars.values()) {
                if (bossBar2.hasDragonMusic()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean shouldDarkenSky() {
        if (!this.bossBars.isEmpty()) {
            for (final BossBar bossBar2 : this.bossBars.values()) {
                if (bossBar2.getDarkenSky()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean shouldThickenFog() {
        if (!this.bossBars.isEmpty()) {
            for (final BossBar bossBar2 : this.bossBars.values()) {
                if (bossBar2.getThickenFog()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    static {
        BAR_TEX = new Identifier("textures/gui/bars.png");
    }
}

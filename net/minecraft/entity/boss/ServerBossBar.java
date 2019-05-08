package net.minecraft.entity.boss;

import java.util.Collection;
import com.google.common.collect.Lists;
import java.util.Iterator;
import net.minecraft.network.Packet;
import com.google.common.base.Objects;
import net.minecraft.client.network.packet.BossBarS2CPacket;
import java.util.Collections;
import com.google.common.collect.Sets;
import net.minecraft.util.math.MathHelper;
import net.minecraft.text.TextComponent;
import net.minecraft.server.network.ServerPlayerEntity;
import java.util.Set;

public class ServerBossBar extends BossBar
{
    private final Set<ServerPlayerEntity> players;
    private final Set<ServerPlayerEntity> i;
    private boolean visible;
    
    public ServerBossBar(final TextComponent name, final Color color, final Style style) {
        super(MathHelper.randomUUID(), name, color, style);
        this.players = Sets.newHashSet();
        this.i = Collections.<ServerPlayerEntity>unmodifiableSet(this.players);
        this.visible = true;
    }
    
    @Override
    public void setPercent(final float percentage) {
        if (percentage != this.percent) {
            super.setPercent(percentage);
            this.sendPacket(BossBarS2CPacket.Type.UPDATE_PCT);
        }
    }
    
    @Override
    public void setColor(final Color color) {
        if (color != this.color) {
            super.setColor(color);
            this.sendPacket(BossBarS2CPacket.Type.UPDATE_STYLE);
        }
    }
    
    @Override
    public void setOverlay(final Style style) {
        if (style != this.style) {
            super.setOverlay(style);
            this.sendPacket(BossBarS2CPacket.Type.UPDATE_STYLE);
        }
    }
    
    @Override
    public BossBar setDarkenSky(final boolean darkenSky) {
        if (darkenSky != this.darkenSky) {
            super.setDarkenSky(darkenSky);
            this.sendPacket(BossBarS2CPacket.Type.UPDATE_FLAGS);
        }
        return this;
    }
    
    @Override
    public BossBar setDragonMusic(final boolean dragonMusic) {
        if (dragonMusic != this.dragonMusic) {
            super.setDragonMusic(dragonMusic);
            this.sendPacket(BossBarS2CPacket.Type.UPDATE_FLAGS);
        }
        return this;
    }
    
    @Override
    public BossBar setThickenFog(final boolean thickenFog) {
        if (thickenFog != this.thickenFog) {
            super.setThickenFog(thickenFog);
            this.sendPacket(BossBarS2CPacket.Type.UPDATE_FLAGS);
        }
        return this;
    }
    
    @Override
    public void setName(final TextComponent name) {
        if (!Objects.equal(name, this.name)) {
            super.setName(name);
            this.sendPacket(BossBarS2CPacket.Type.UPDATE_TITLE);
        }
    }
    
    private void sendPacket(final BossBarS2CPacket.Type type) {
        if (this.visible) {
            final BossBarS2CPacket bossBarS2CPacket2 = new BossBarS2CPacket(type, this);
            for (final ServerPlayerEntity serverPlayerEntity4 : this.players) {
                serverPlayerEntity4.networkHandler.sendPacket(bossBarS2CPacket2);
            }
        }
    }
    
    public void addPlayer(final ServerPlayerEntity serverPlayerEntity) {
        if (this.players.add(serverPlayerEntity) && this.visible) {
            serverPlayerEntity.networkHandler.sendPacket(new BossBarS2CPacket(BossBarS2CPacket.Type.ADD, this));
        }
    }
    
    public void removePlayer(final ServerPlayerEntity serverPlayerEntity) {
        if (this.players.remove(serverPlayerEntity) && this.visible) {
            serverPlayerEntity.networkHandler.sendPacket(new BossBarS2CPacket(BossBarS2CPacket.Type.REMOVE, this));
        }
    }
    
    public void clearPlayers() {
        if (!this.players.isEmpty()) {
            for (final ServerPlayerEntity serverPlayerEntity2 : Lists.<ServerPlayerEntity>newArrayList(this.players)) {
                this.removePlayer(serverPlayerEntity2);
            }
        }
    }
    
    public boolean isVisible() {
        return this.visible;
    }
    
    public void setVisible(final boolean visible) {
        if (visible != this.visible) {
            this.visible = visible;
            for (final ServerPlayerEntity serverPlayerEntity3 : this.players) {
                serverPlayerEntity3.networkHandler.sendPacket(new BossBarS2CPacket(visible ? BossBarS2CPacket.Type.ADD : BossBarS2CPacket.Type.REMOVE, this));
            }
        }
    }
    
    public Collection<ServerPlayerEntity> getPlayers() {
        return this.i;
    }
}

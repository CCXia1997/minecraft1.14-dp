package net.minecraft.client.gui.hud.spectator;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.util.math.MathHelper;
import java.util.Collection;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import java.util.Random;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.Identifier;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.SpectatorHud;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.text.TextComponent;
import java.util.Iterator;
import net.minecraft.scoreboard.Team;
import net.minecraft.client.MinecraftClient;
import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class TeamTeleportSpectatorMenu implements SpectatorMenuCommandGroup, SpectatorMenuCommand
{
    private final List<SpectatorMenuCommand> commands;
    
    public TeamTeleportSpectatorMenu() {
        this.commands = Lists.newArrayList();
        final MinecraftClient minecraftClient1 = MinecraftClient.getInstance();
        for (final Team team3 : minecraftClient1.world.getScoreboard().getTeams()) {
            this.commands.add(new TeleportToSpecificTeamCommand(team3));
        }
    }
    
    @Override
    public List<SpectatorMenuCommand> getCommands() {
        return this.commands;
    }
    
    @Override
    public TextComponent getPrompt() {
        return new TranslatableTextComponent("spectatorMenu.team_teleport.prompt", new Object[0]);
    }
    
    @Override
    public void use(final SpectatorMenu menu) {
        menu.selectElement(this);
    }
    
    @Override
    public TextComponent getName() {
        return new TranslatableTextComponent("spectatorMenu.team_teleport", new Object[0]);
    }
    
    @Override
    public void renderIcon(final float brightness, final int alpha) {
        MinecraftClient.getInstance().getTextureManager().bindTexture(SpectatorHud.SPECTATOR_TEX);
        DrawableHelper.blit(0, 0, 16.0f, 0.0f, 16, 16, 256, 256);
    }
    
    @Override
    public boolean enabled() {
        for (final SpectatorMenuCommand spectatorMenuCommand2 : this.commands) {
            if (spectatorMenuCommand2.enabled()) {
                return true;
            }
        }
        return false;
    }
    
    @Environment(EnvType.CLIENT)
    class TeleportToSpecificTeamCommand implements SpectatorMenuCommand
    {
        private final Team team;
        private final Identifier skinId;
        private final List<PlayerListEntry> scoreboardEntries;
        
        public TeleportToSpecificTeamCommand(final Team team) {
            this.team = team;
            this.scoreboardEntries = Lists.newArrayList();
            for (final String string4 : team.getPlayerList()) {
                final PlayerListEntry playerListEntry5 = MinecraftClient.getInstance().getNetworkHandler().getPlayerListEntry(string4);
                if (playerListEntry5 != null) {
                    this.scoreboardEntries.add(playerListEntry5);
                }
            }
            if (this.scoreboardEntries.isEmpty()) {
                this.skinId = DefaultSkinHelper.getTexture();
            }
            else {
                final String string5 = this.scoreboardEntries.get(new Random().nextInt(this.scoreboardEntries.size())).getProfile().getName();
                AbstractClientPlayerEntity.loadSkin(this.skinId = AbstractClientPlayerEntity.getSkinId(string5), string5);
            }
        }
        
        @Override
        public void use(final SpectatorMenu menu) {
            menu.selectElement(new TeleportSpectatorMenu(this.scoreboardEntries));
        }
        
        @Override
        public TextComponent getName() {
            return this.team.getDisplayName();
        }
        
        @Override
        public void renderIcon(final float brightness, final int alpha) {
            final Integer integer3 = this.team.getColor().getColor();
            if (integer3 != null) {
                final float float4 = (integer3 >> 16 & 0xFF) / 255.0f;
                final float float5 = (integer3 >> 8 & 0xFF) / 255.0f;
                final float float6 = (integer3 & 0xFF) / 255.0f;
                DrawableHelper.fill(1, 1, 15, 15, MathHelper.packRgb(float4 * brightness, float5 * brightness, float6 * brightness) | alpha << 24);
            }
            MinecraftClient.getInstance().getTextureManager().bindTexture(this.skinId);
            GlStateManager.color4f(brightness, brightness, brightness, alpha / 255.0f);
            DrawableHelper.blit(2, 2, 12, 12, 8.0f, 8.0f, 8, 8, 64, 64);
            DrawableHelper.blit(2, 2, 12, 12, 40.0f, 8.0f, 8, 8, 64, 64);
        }
        
        @Override
        public boolean enabled() {
            return !this.scoreboardEntries.isEmpty();
        }
    }
}

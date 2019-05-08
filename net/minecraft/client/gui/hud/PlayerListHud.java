package net.minecraft.client.gui.hud;

import com.google.common.collect.ComparisonChain;
import java.util.Comparator;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.player.PlayerEntity;
import com.mojang.authlib.GameProfile;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.text.TextFormat;
import net.minecraft.world.GameMode;
import net.minecraft.client.render.entity.PlayerModelPart;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.scoreboard.ScoreboardCriterion;
import javax.annotation.Nullable;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.SystemUtil;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import com.google.common.collect.Ordering;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;

@Environment(EnvType.CLIENT)
public class PlayerListHud extends DrawableHelper
{
    private static final Ordering<PlayerListEntry> ENTRY_ORDERING;
    private final MinecraftClient client;
    private final InGameHud inGameHud;
    private TextComponent footer;
    private TextComponent header;
    private long showTime;
    private boolean visible;
    
    public PlayerListHud(final MinecraftClient client, final InGameHud inGameHud) {
        this.client = client;
        this.inGameHud = inGameHud;
    }
    
    public TextComponent a(final PlayerListEntry playerListEntry) {
        if (playerListEntry.getDisplayName() != null) {
            return playerListEntry.getDisplayName();
        }
        return Team.modifyText(playerListEntry.getScoreboardTeam(), new StringTextComponent(playerListEntry.getProfile().getName()));
    }
    
    public void tick(final boolean visible) {
        if (visible && !this.visible) {
            this.showTime = SystemUtil.getMeasuringTimeMs();
        }
        this.visible = visible;
    }
    
    public void draw(final int integer, final Scoreboard scoreboard, @Nullable final ScoreboardObjective playerListScoreboardObjective) {
        final ClientPlayNetworkHandler clientPlayNetworkHandler4 = this.client.player.networkHandler;
        List<PlayerListEntry> list5 = PlayerListHud.ENTRY_ORDERING.<PlayerListEntry>sortedCopy(clientPlayNetworkHandler4.getPlayerList());
        int integer2 = 0;
        int integer3 = 0;
        for (final PlayerListEntry playerListEntry9 : list5) {
            int integer4 = this.client.textRenderer.getStringWidth(this.a(playerListEntry9).getFormattedText());
            integer2 = Math.max(integer2, integer4);
            if (playerListScoreboardObjective != null && playerListScoreboardObjective.getRenderType() != ScoreboardCriterion.RenderType.HEARTS) {
                integer4 = this.client.textRenderer.getStringWidth(" " + scoreboard.getPlayerScore(playerListEntry9.getProfile().getName(), playerListScoreboardObjective).getScore());
                integer3 = Math.max(integer3, integer4);
            }
        }
        list5 = list5.subList(0, Math.min(list5.size(), 80));
        int integer4;
        int integer6;
        int integer5;
        for (integer5 = (integer6 = list5.size()), integer4 = 1; integer6 > 20; integer6 = (integer5 + integer4 - 1) / integer4) {
            ++integer4;
        }
        final boolean boolean11 = this.client.isInSingleplayer() || this.client.getNetworkHandler().getClientConnection().isEncrypted();
        int integer7;
        if (playerListScoreboardObjective != null) {
            if (playerListScoreboardObjective.getRenderType() == ScoreboardCriterion.RenderType.HEARTS) {
                integer7 = 90;
            }
            else {
                integer7 = integer3;
            }
        }
        else {
            integer7 = 0;
        }
        final int integer8 = Math.min(integer4 * ((boolean11 ? 9 : 0) + integer2 + integer7 + 13), integer - 50) / integer4;
        final int integer9 = integer / 2 - (integer8 * integer4 + (integer4 - 1) * 5) / 2;
        int integer10 = 10;
        int integer11 = integer8 * integer4 + (integer4 - 1) * 5;
        List<String> list6 = null;
        if (this.header != null) {
            list6 = this.client.textRenderer.wrapStringToWidthAsList(this.header.getFormattedText(), integer - 50);
            for (final String string19 : list6) {
                integer11 = Math.max(integer11, this.client.textRenderer.getStringWidth(string19));
            }
        }
        List<String> list7 = null;
        if (this.footer != null) {
            list7 = this.client.textRenderer.wrapStringToWidthAsList(this.footer.getFormattedText(), integer - 50);
            for (final String string20 : list7) {
                integer11 = Math.max(integer11, this.client.textRenderer.getStringWidth(string20));
            }
        }
        if (list6 != null) {
            final int left = integer / 2 - integer11 / 2 - 1;
            final int top = integer10 - 1;
            final int right = integer / 2 + integer11 / 2 + 1;
            final int n = integer10;
            final int size = list6.size();
            this.client.textRenderer.getClass();
            DrawableHelper.fill(left, top, right, n + size * 9, Integer.MIN_VALUE);
            for (final String string20 : list6) {
                final int integer12 = this.client.textRenderer.getStringWidth(string20);
                this.client.textRenderer.drawWithShadow(string20, (float)(integer / 2 - integer12 / 2), (float)integer10, -1);
                final int n2 = integer10;
                this.client.textRenderer.getClass();
                integer10 = n2 + 9;
            }
            ++integer10;
        }
        DrawableHelper.fill(integer / 2 - integer11 / 2 - 1, integer10 - 1, integer / 2 + integer11 / 2 + 1, integer10 + integer6 * 9, Integer.MIN_VALUE);
        final int integer13 = this.client.options.getTextBackgroundColor(553648127);
        for (int integer14 = 0; integer14 < integer5; ++integer14) {
            final int integer12 = integer14 / integer6;
            final int integer15 = integer14 % integer6;
            int integer16 = integer9 + integer12 * integer8 + integer12 * 5;
            final int integer17 = integer10 + integer15 * 9;
            DrawableHelper.fill(integer16, integer17, integer16 + integer8, integer17 + 8, integer13);
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.enableAlphaTest();
            GlStateManager.enableBlend();
            GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            if (integer14 < list5.size()) {
                final PlayerListEntry playerListEntry10 = list5.get(integer14);
                final GameProfile gameProfile26 = playerListEntry10.getProfile();
                if (boolean11) {
                    final PlayerEntity playerEntity27 = this.client.world.getPlayerByUuid(gameProfile26.getId());
                    final boolean boolean12 = playerEntity27 != null && playerEntity27.isSkinOverlayVisible(PlayerModelPart.CAPE) && ("Dinnerbone".equals(gameProfile26.getName()) || "Grumm".equals(gameProfile26.getName()));
                    this.client.getTextureManager().bindTexture(playerListEntry10.getSkinTexture());
                    final int integer18 = 8 + (boolean12 ? 8 : 0);
                    final int integer19 = 8 * (boolean12 ? -1 : 1);
                    DrawableHelper.blit(integer16, integer17, 8, 8, 8.0f, (float)integer18, 8, integer19, 64, 64);
                    if (playerEntity27 != null && playerEntity27.isSkinOverlayVisible(PlayerModelPart.HEAD)) {
                        final int integer20 = 8 + (boolean12 ? 8 : 0);
                        final int integer21 = 8 * (boolean12 ? -1 : 1);
                        DrawableHelper.blit(integer16, integer17, 8, 8, 40.0f, (float)integer20, 8, integer21, 64, 64);
                    }
                    integer16 += 9;
                }
                final String string21 = this.a(playerListEntry10).getFormattedText();
                if (playerListEntry10.getGameMode() == GameMode.e) {
                    this.client.textRenderer.drawWithShadow(TextFormat.u + string21, (float)integer16, (float)integer17, -1862270977);
                }
                else {
                    this.client.textRenderer.drawWithShadow(string21, (float)integer16, (float)integer17, -1);
                }
                if (playerListScoreboardObjective != null && playerListEntry10.getGameMode() != GameMode.e) {
                    final int integer22 = integer16 + integer2 + 1;
                    final int integer18 = integer22 + integer7;
                    if (integer18 - integer22 > 5) {
                        this.a(playerListScoreboardObjective, integer17, gameProfile26.getName(), integer22, integer18, playerListEntry10);
                    }
                }
                this.a(integer8, integer16 - (boolean11 ? 9 : 0), integer17, playerListEntry10);
            }
        }
        if (list7 != null) {
            integer10 += integer6 * 9 + 1;
            final int left2 = integer / 2 - integer11 / 2 - 1;
            final int top2 = integer10 - 1;
            final int right2 = integer / 2 + integer11 / 2 + 1;
            final int n3 = integer10;
            final int size2 = list7.size();
            this.client.textRenderer.getClass();
            DrawableHelper.fill(left2, top2, right2, n3 + size2 * 9, Integer.MIN_VALUE);
            for (final String string22 : list7) {
                final int integer15 = this.client.textRenderer.getStringWidth(string22);
                this.client.textRenderer.drawWithShadow(string22, (float)(integer / 2 - integer15 / 2), (float)integer10, -1);
                final int n4 = integer10;
                this.client.textRenderer.getClass();
                integer10 = n4 + 9;
            }
        }
    }
    
    protected void a(final int integer1, final int integer2, final int integer3, final PlayerListEntry playerListEntry) {
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.client.getTextureManager().bindTexture(PlayerListHud.GUI_ICONS_LOCATION);
        final int integer4 = 0;
        int integer5;
        if (playerListEntry.getLatency() < 0) {
            integer5 = 5;
        }
        else if (playerListEntry.getLatency() < 150) {
            integer5 = 0;
        }
        else if (playerListEntry.getLatency() < 300) {
            integer5 = 1;
        }
        else if (playerListEntry.getLatency() < 600) {
            integer5 = 2;
        }
        else if (playerListEntry.getLatency() < 1000) {
            integer5 = 3;
        }
        else {
            integer5 = 4;
        }
        this.blitOffset += 100;
        this.blit(integer2 + integer1 - 11, integer3, 0, 176 + integer5 * 8, 10, 8);
        this.blitOffset -= 100;
    }
    
    private void a(final ScoreboardObjective scoreboardObjective, final int integer2, final String string, final int integer4, final int integer5, final PlayerListEntry playerListEntry) {
        final int integer6 = scoreboardObjective.getScoreboard().getPlayerScore(string, scoreboardObjective).getScore();
        if (scoreboardObjective.getRenderType() == ScoreboardCriterion.RenderType.HEARTS) {
            this.client.getTextureManager().bindTexture(PlayerListHud.GUI_ICONS_LOCATION);
            final long long8 = SystemUtil.getMeasuringTimeMs();
            if (this.showTime == playerListEntry.q()) {
                if (integer6 < playerListEntry.m()) {
                    playerListEntry.a(long8);
                    playerListEntry.b((long)(this.inGameHud.getTicks() + 20));
                }
                else if (integer6 > playerListEntry.m()) {
                    playerListEntry.a(long8);
                    playerListEntry.b((long)(this.inGameHud.getTicks() + 10));
                }
            }
            if (long8 - playerListEntry.o() > 1000L || this.showTime != playerListEntry.q()) {
                playerListEntry.b(integer6);
                playerListEntry.c(integer6);
                playerListEntry.a(long8);
            }
            playerListEntry.c(this.showTime);
            playerListEntry.b(integer6);
            final int integer7 = MathHelper.ceil(Math.max(integer6, playerListEntry.n()) / 2.0f);
            final int integer8 = Math.max(MathHelper.ceil((float)(integer6 / 2)), Math.max(MathHelper.ceil((float)(playerListEntry.n() / 2)), 10));
            final boolean boolean12 = playerListEntry.p() > this.inGameHud.getTicks() && (playerListEntry.p() - this.inGameHud.getTicks()) / 3L % 2L == 1L;
            if (integer7 > 0) {
                final int integer9 = MathHelper.floor(Math.min((integer5 - integer4 - 4) / (float)integer8, 9.0f));
                if (integer9 > 3) {
                    for (int integer10 = integer7; integer10 < integer8; ++integer10) {
                        this.blit(integer4 + integer10 * integer9, integer2, boolean12 ? 25 : 16, 0, 9, 9);
                    }
                    for (int integer10 = 0; integer10 < integer7; ++integer10) {
                        this.blit(integer4 + integer10 * integer9, integer2, boolean12 ? 25 : 16, 0, 9, 9);
                        if (boolean12) {
                            if (integer10 * 2 + 1 < playerListEntry.n()) {
                                this.blit(integer4 + integer10 * integer9, integer2, 70, 0, 9, 9);
                            }
                            if (integer10 * 2 + 1 == playerListEntry.n()) {
                                this.blit(integer4 + integer10 * integer9, integer2, 79, 0, 9, 9);
                            }
                        }
                        if (integer10 * 2 + 1 < integer6) {
                            this.blit(integer4 + integer10 * integer9, integer2, (integer10 >= 10) ? 160 : 52, 0, 9, 9);
                        }
                        if (integer10 * 2 + 1 == integer6) {
                            this.blit(integer4 + integer10 * integer9, integer2, (integer10 >= 10) ? 169 : 61, 0, 9, 9);
                        }
                    }
                }
                else {
                    final float float14 = MathHelper.clamp(integer6 / 20.0f, 0.0f, 1.0f);
                    final int integer11 = (int)((1.0f - float14) * 255.0f) << 16 | (int)(float14 * 255.0f) << 8;
                    String string2 = "" + integer6 / 2.0f;
                    if (integer5 - this.client.textRenderer.getStringWidth(string2 + "hp") >= integer4) {
                        string2 += "hp";
                    }
                    this.client.textRenderer.drawWithShadow(string2, (float)((integer5 + integer4) / 2 - this.client.textRenderer.getStringWidth(string2) / 2), (float)integer2, integer11);
                }
            }
        }
        else {
            final String string3 = TextFormat.o + "" + integer6;
            this.client.textRenderer.drawWithShadow(string3, (float)(integer5 - this.client.textRenderer.getStringWidth(string3)), (float)integer2, 16777215);
        }
    }
    
    public void setFooter(@Nullable final TextComponent footer) {
        this.footer = footer;
    }
    
    public void setHeader(@Nullable final TextComponent header) {
        this.header = header;
    }
    
    public void clear() {
        this.header = null;
        this.footer = null;
    }
    
    static {
        ENTRY_ORDERING = Ordering.<PlayerListEntry>from((Comparator<PlayerListEntry>)new EntryOrderComparator());
    }
    
    @Environment(EnvType.CLIENT)
    static class EntryOrderComparator implements Comparator<PlayerListEntry>
    {
        private EntryOrderComparator() {
        }
        
        public int a(final PlayerListEntry playerListEntry1, final PlayerListEntry playerListEntry2) {
            final Team team3 = playerListEntry1.getScoreboardTeam();
            final Team team4 = playerListEntry2.getScoreboardTeam();
            return ComparisonChain.start().compareTrueFirst(playerListEntry1.getGameMode() != GameMode.e, playerListEntry2.getGameMode() != GameMode.e).compare((team3 != null) ? team3.getName() : "", (team4 != null) ? team4.getName() : "").<String>compare(playerListEntry1.getProfile().getName(), playerListEntry2.getProfile().getName(), String::compareToIgnoreCase).result();
        }
    }
}

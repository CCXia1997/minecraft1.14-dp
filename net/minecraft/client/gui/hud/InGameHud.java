package net.minecraft.client.gui.hud;

import net.minecraft.world.border.WorldBorder;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.Tessellator;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.tag.FluidTags;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.SystemUtil;
import net.minecraft.entity.Entity;
import net.minecraft.scoreboard.AbstractTeam;
import com.google.common.collect.Iterables;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.util.ChatUtil;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.text.StringTextComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.util.AbsoluteHand;
import net.minecraft.client.texture.Sprite;
import net.minecraft.entity.effect.StatusEffect;
import java.util.Iterator;
import net.minecraft.client.texture.StatusEffectSpriteManager;
import java.util.Collection;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.effect.StatusEffectInstance;
import com.google.common.collect.Ordering;
import net.minecraft.client.gui.ContainerScreen;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.container.NameableContainerProvider;
import net.minecraft.util.hit.HitResult;
import net.minecraft.client.render.Camera;
import net.minecraft.client.options.GameOptions;
import net.minecraft.entity.LivingEntity;
import net.minecraft.client.options.AttackIndicator;
import com.mojang.blaze3d.platform.GLX;
import net.minecraft.scoreboard.Team;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.world.GameMode;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.block.Blocks;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.GameInfoChatListener;
import net.minecraft.client.util.NarratorManager;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.client.gui.ClientChatListener;
import java.util.List;
import net.minecraft.text.ChatMessageType;
import java.util.Map;
import net.minecraft.item.ItemStack;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.MinecraftClient;
import java.util.Random;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;

@Environment(EnvType.CLIENT)
public class InGameHud extends DrawableHelper
{
    private static final Identifier VIGNETTE_TEX;
    private static final Identifier WIDGETS_TEX;
    private static final Identifier PUMPKIN_BLUR;
    private final Random random;
    private final MinecraftClient client;
    private final ItemRenderer itemRenderer;
    private final ChatHud chatHud;
    private int ticks;
    private String overlayMessage;
    private int overlayRemaining;
    private boolean overlayTinted;
    public float a;
    private int heldItemTooltipFade;
    private ItemStack currentStack;
    private final DebugHud debugHud;
    private final SubtitlesHud subtitlesHud;
    private final SpectatorHud spectatorHud;
    private final PlayerListHud playerListHud;
    private final BossBarHud bossBarHud;
    private int titleTotalTicks;
    private String title;
    private String subtitle;
    private int titleFadeInTicks;
    private int titleRemainTicks;
    private int titleFadeOutTicks;
    private int z;
    private int A;
    private long B;
    private long C;
    private int scaledWidth;
    private int scaledHeight;
    private final Map<ChatMessageType, List<ClientChatListener>> listeners;
    
    public InGameHud(final MinecraftClient client) {
        this.random = new Random();
        this.overlayMessage = "";
        this.a = 1.0f;
        this.currentStack = ItemStack.EMPTY;
        this.title = "";
        this.subtitle = "";
        this.listeners = Maps.newHashMap();
        this.client = client;
        this.itemRenderer = client.getItemRenderer();
        this.debugHud = new DebugHud(client);
        this.spectatorHud = new SpectatorHud(client);
        this.chatHud = new ChatHud(client);
        this.playerListHud = new PlayerListHud(client, this);
        this.bossBarHud = new BossBarHud(client);
        this.subtitlesHud = new SubtitlesHud(client);
        for (final ChatMessageType chatMessageType5 : ChatMessageType.values()) {
            this.listeners.put(chatMessageType5, Lists.newArrayList());
        }
        final ClientChatListener clientChatListener2 = NarratorManager.INSTANCE;
        this.listeners.get(ChatMessageType.a).add(new ChatListenerHud(client));
        this.listeners.get(ChatMessageType.a).add(clientChatListener2);
        this.listeners.get(ChatMessageType.b).add(new ChatListenerHud(client));
        this.listeners.get(ChatMessageType.b).add(clientChatListener2);
        this.listeners.get(ChatMessageType.c).add(new GameInfoChatListener(client));
        this.setDefaultTitleFade();
    }
    
    public void setDefaultTitleFade() {
        this.titleFadeInTicks = 10;
        this.titleRemainTicks = 70;
        this.titleFadeOutTicks = 20;
    }
    
    public void draw(final float float1) {
        this.scaledWidth = this.client.window.getScaledWidth();
        this.scaledHeight = this.client.window.getScaledHeight();
        final TextRenderer textRenderer2 = this.getFontRenderer();
        GlStateManager.enableBlend();
        if (MinecraftClient.isFancyGraphicsEnabled()) {
            this.renderVignetteOverlay(this.client.getCameraEntity());
        }
        else {
            GlStateManager.enableDepthTest();
            GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        }
        final ItemStack itemStack3 = this.client.player.inventory.getArmorStack(3);
        if (this.client.options.perspective == 0 && itemStack3.getItem() == Blocks.cN.getItem()) {
            this.renderPumpkinOverlay();
        }
        if (!this.client.player.hasStatusEffect(StatusEffects.i)) {
            final float float2 = MathHelper.lerp(float1, this.client.player.lastNauseaStrength, this.client.player.nextNauseaStrength);
            if (float2 > 0.0f) {
                this.renderPortalOverlay(float2);
            }
        }
        if (this.client.interactionManager.getCurrentGameMode() == GameMode.e) {
            this.spectatorHud.draw(float1);
        }
        else if (!this.client.options.hudHidden) {
            this.renderHotbar(float1);
        }
        if (!this.client.options.hudHidden) {
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            this.client.getTextureManager().bindTexture(InGameHud.GUI_ICONS_LOCATION);
            GlStateManager.enableBlend();
            GlStateManager.enableAlphaTest();
            this.renderCrosshair();
            GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            this.client.getProfiler().push("bossHealth");
            this.bossBarHud.draw();
            this.client.getProfiler().pop();
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            this.client.getTextureManager().bindTexture(InGameHud.GUI_ICONS_LOCATION);
            if (this.client.interactionManager.hasStatusBars()) {
                this.renderStatusBars();
            }
            this.drawMountHealth();
            GlStateManager.disableBlend();
            final int integer4 = this.scaledWidth / 2 - 91;
            if (this.client.player.hasJumpingMount()) {
                this.renderMountJumpBar(integer4);
            }
            else if (this.client.interactionManager.hasExperienceBar()) {
                this.renderExperienceBar(integer4);
            }
            if (this.client.options.heldItemTooltips && this.client.interactionManager.getCurrentGameMode() != GameMode.e) {
                this.renderHeldItemTooltip();
            }
            else if (this.client.player.isSpectator()) {
                this.spectatorHud.draw();
            }
        }
        if (this.client.player.getSleepTimer() > 0) {
            this.client.getProfiler().push("sleep");
            GlStateManager.disableDepthTest();
            GlStateManager.disableAlphaTest();
            final float float2 = (float)this.client.player.getSleepTimer();
            float float3 = float2 / 100.0f;
            if (float3 > 1.0f) {
                float3 = 1.0f - (float2 - 100.0f) / 10.0f;
            }
            final int integer5 = (int)(220.0f * float3) << 24 | 0x101020;
            DrawableHelper.fill(0, 0, this.scaledWidth, this.scaledHeight, integer5);
            GlStateManager.enableAlphaTest();
            GlStateManager.enableDepthTest();
            this.client.getProfiler().pop();
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        }
        if (this.client.isDemo()) {
            this.renderDemoTimer();
        }
        this.renderStatusEffectOverlay();
        if (this.client.options.debugEnabled) {
            this.debugHud.draw();
        }
        if (!this.client.options.hudHidden) {
            if (this.overlayRemaining > 0) {
                this.client.getProfiler().push("overlayMessage");
                final float float2 = this.overlayRemaining - float1;
                int integer6 = (int)(float2 * 255.0f / 20.0f);
                if (integer6 > 255) {
                    integer6 = 255;
                }
                if (integer6 > 8) {
                    GlStateManager.pushMatrix();
                    GlStateManager.translatef((float)(this.scaledWidth / 2), (float)(this.scaledHeight - 68), 0.0f);
                    GlStateManager.enableBlend();
                    GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                    int integer5 = 16777215;
                    if (this.overlayTinted) {
                        integer5 = (MathHelper.hsvToRgb(float2 / 50.0f, 0.7f, 0.6f) & 0xFFFFFF);
                    }
                    final int integer7 = integer6 << 24 & 0xFF000000;
                    this.a(textRenderer2, -4, textRenderer2.getStringWidth(this.overlayMessage));
                    textRenderer2.draw(this.overlayMessage, (float)(-textRenderer2.getStringWidth(this.overlayMessage) / 2), -4.0f, integer5 | integer7);
                    GlStateManager.disableBlend();
                    GlStateManager.popMatrix();
                }
                this.client.getProfiler().pop();
            }
            if (this.titleTotalTicks > 0) {
                this.client.getProfiler().push("titleAndSubtitle");
                final float float2 = this.titleTotalTicks - float1;
                int integer6 = 255;
                if (this.titleTotalTicks > this.titleFadeOutTicks + this.titleRemainTicks) {
                    final float float4 = this.titleFadeInTicks + this.titleRemainTicks + this.titleFadeOutTicks - float2;
                    integer6 = (int)(float4 * 255.0f / this.titleFadeInTicks);
                }
                if (this.titleTotalTicks <= this.titleFadeOutTicks) {
                    integer6 = (int)(float2 * 255.0f / this.titleFadeOutTicks);
                }
                integer6 = MathHelper.clamp(integer6, 0, 255);
                if (integer6 > 8) {
                    GlStateManager.pushMatrix();
                    GlStateManager.translatef((float)(this.scaledWidth / 2), (float)(this.scaledHeight / 2), 0.0f);
                    GlStateManager.enableBlend();
                    GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                    GlStateManager.pushMatrix();
                    GlStateManager.scalef(4.0f, 4.0f, 4.0f);
                    final int integer5 = integer6 << 24 & 0xFF000000;
                    final int integer7 = textRenderer2.getStringWidth(this.title);
                    this.a(textRenderer2, -10, integer7);
                    textRenderer2.drawWithShadow(this.title, (float)(-integer7 / 2), -10.0f, 0xFFFFFF | integer5);
                    GlStateManager.popMatrix();
                    if (!this.subtitle.isEmpty()) {
                        GlStateManager.pushMatrix();
                        GlStateManager.scalef(2.0f, 2.0f, 2.0f);
                        final int integer8 = textRenderer2.getStringWidth(this.subtitle);
                        this.a(textRenderer2, 5, integer8);
                        textRenderer2.drawWithShadow(this.subtitle, (float)(-integer8 / 2), 5.0f, 0xFFFFFF | integer5);
                        GlStateManager.popMatrix();
                    }
                    GlStateManager.disableBlend();
                    GlStateManager.popMatrix();
                }
                this.client.getProfiler().pop();
            }
            this.subtitlesHud.draw();
            final Scoreboard scoreboard4 = this.client.world.getScoreboard();
            ScoreboardObjective scoreboardObjective5 = null;
            final Team team6 = scoreboard4.getPlayerTeam(this.client.player.getEntityName());
            if (team6 != null) {
                final int integer7 = team6.getColor().getId();
                if (integer7 >= 0) {
                    scoreboardObjective5 = scoreboard4.getObjectiveForSlot(3 + integer7);
                }
            }
            ScoreboardObjective scoreboardObjective6 = (scoreboardObjective5 != null) ? scoreboardObjective5 : scoreboard4.getObjectiveForSlot(1);
            if (scoreboardObjective6 != null) {
                this.renderScoreboardSidebar(scoreboardObjective6);
            }
            GlStateManager.enableBlend();
            GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.disableAlphaTest();
            GlStateManager.pushMatrix();
            GlStateManager.translatef(0.0f, (float)(this.scaledHeight - 48), 0.0f);
            this.client.getProfiler().push("chat");
            this.chatHud.draw(this.ticks);
            this.client.getProfiler().pop();
            GlStateManager.popMatrix();
            scoreboardObjective6 = scoreboard4.getObjectiveForSlot(0);
            if (this.client.options.keyPlayerList.isPressed() && (!this.client.isInSingleplayer() || this.client.player.networkHandler.getPlayerList().size() > 1 || scoreboardObjective6 != null)) {
                this.playerListHud.tick(true);
                this.playerListHud.draw(this.scaledWidth, scoreboard4, scoreboardObjective6);
            }
            else {
                this.playerListHud.tick(false);
            }
        }
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.disableLighting();
        GlStateManager.enableAlphaTest();
    }
    
    private void a(final TextRenderer textRenderer, final int integer2, final int integer3) {
        final int integer4 = this.client.options.getTextBackgroundColor(0.0f);
        if (integer4 != 0) {
            final int integer5 = -integer3 / 2;
            final int left = integer5 - 2;
            final int top = integer2 - 2;
            final int right = integer5 + integer3 + 2;
            textRenderer.getClass();
            DrawableHelper.fill(left, top, right, integer2 + 9 + 2, integer4);
        }
    }
    
    private void renderCrosshair() {
        final GameOptions gameOptions1 = this.client.options;
        if (gameOptions1.perspective != 0) {
            return;
        }
        if (this.client.interactionManager.getCurrentGameMode() == GameMode.e && !this.shouldRenderSpectatorCrosshair(this.client.hitResult)) {
            return;
        }
        if (gameOptions1.debugEnabled && !gameOptions1.hudHidden && !this.client.player.getReducedDebugInfo() && !gameOptions1.reducedDebugInfo) {
            GlStateManager.pushMatrix();
            GlStateManager.translatef((float)(this.scaledWidth / 2), (float)(this.scaledHeight / 2), (float)this.blitOffset);
            final Camera camera2 = this.client.gameRenderer.getCamera();
            GlStateManager.rotatef(camera2.getPitch(), -1.0f, 0.0f, 0.0f);
            GlStateManager.rotatef(camera2.getYaw(), 0.0f, 1.0f, 0.0f);
            GlStateManager.scalef(-1.0f, -1.0f, -1.0f);
            GLX.renderCrosshair(10);
            GlStateManager.popMatrix();
        }
        else {
            GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            final int integer2 = 15;
            this.blit((this.scaledWidth - 15) / 2, (this.scaledHeight - 15) / 2, 0, 0, 15, 15);
            if (this.client.options.attackIndicator == AttackIndicator.b) {
                final float float3 = this.client.player.s(0.0f);
                boolean boolean4 = false;
                if (this.client.targetedEntity != null && this.client.targetedEntity instanceof LivingEntity && float3 >= 1.0f) {
                    boolean4 = (this.client.player.dY() > 5.0f);
                    boolean4 &= this.client.targetedEntity.isAlive();
                }
                final int integer3 = this.scaledHeight / 2 - 7 + 16;
                final int integer4 = this.scaledWidth / 2 - 8;
                if (boolean4) {
                    this.blit(integer4, integer3, 68, 94, 16, 16);
                }
                else if (float3 < 1.0f) {
                    final int integer5 = (int)(float3 * 17.0f);
                    this.blit(integer4, integer3, 36, 94, 16, 4);
                    this.blit(integer4, integer3, 52, 94, integer5, 4);
                }
            }
        }
    }
    
    private boolean shouldRenderSpectatorCrosshair(final HitResult hitResult) {
        if (hitResult == null) {
            return false;
        }
        if (hitResult.getType() == HitResult.Type.ENTITY) {
            return ((EntityHitResult)hitResult).getEntity() instanceof NameableContainerProvider;
        }
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            final BlockPos blockPos2 = ((BlockHitResult)hitResult).getBlockPos();
            final World world3 = this.client.world;
            return world3.getBlockState(blockPos2).createContainerProvider(world3, blockPos2) != null;
        }
        return false;
    }
    
    protected void renderStatusEffectOverlay() {
        final Collection<StatusEffectInstance> collection1 = this.client.player.getStatusEffects();
        if (collection1.isEmpty()) {
            return;
        }
        GlStateManager.enableBlend();
        int integer2 = 0;
        int integer3 = 0;
        final StatusEffectSpriteManager statusEffectSpriteManager4 = this.client.getStatusEffectSpriteManager();
        final List<Runnable> list5 = Lists.newArrayListWithExpectedSize(collection1.size());
        this.client.getTextureManager().bindTexture(ContainerScreen.BACKGROUND_TEXTURE);
        for (final StatusEffectInstance statusEffectInstance7 : Ordering.<Comparable>natural().reverse().<StatusEffectInstance>sortedCopy(collection1)) {
            final StatusEffect statusEffect8 = statusEffectInstance7.getEffectType();
            if (statusEffectInstance7.shouldShowIcon()) {
                int integer4 = this.scaledWidth;
                int integer5 = 1;
                if (this.client.isDemo()) {
                    integer5 += 15;
                }
                if (statusEffect8.h()) {
                    ++integer2;
                    integer4 -= 25 * integer2;
                }
                else {
                    ++integer3;
                    integer4 -= 25 * integer3;
                    integer5 += 26;
                }
                GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                float float11 = 1.0f;
                if (statusEffectInstance7.isAmbient()) {
                    this.blit(integer4, integer5, 165, 166, 24, 24);
                }
                else {
                    this.blit(integer4, integer5, 141, 166, 24, 24);
                    if (statusEffectInstance7.getDuration() <= 200) {
                        final int integer6 = 10 - statusEffectInstance7.getDuration() / 20;
                        float11 = MathHelper.clamp(statusEffectInstance7.getDuration() / 10.0f / 5.0f * 0.5f, 0.0f, 0.5f) + MathHelper.cos(statusEffectInstance7.getDuration() * 3.1415927f / 5.0f) * MathHelper.clamp(integer6 / 10.0f * 0.25f, 0.0f, 0.25f);
                    }
                }
                final Sprite sprite12 = statusEffectSpriteManager4.getSprite(statusEffect8);
                final int integer7 = integer4;
                final int integer8 = integer5;
                final float float12 = float11;
                final float alpha;
                final int n;
                final int n2;
                final Sprite sprite13;
                list5.add(() -> {
                    GlStateManager.color4f(1.0f, 1.0f, 1.0f, alpha);
                    DrawableHelper.blit(n + 3, n2 + 3, this.blitOffset, 18, 18, sprite13);
                    return;
                });
            }
        }
        this.client.getTextureManager().bindTexture(SpriteAtlasTexture.STATUS_EFFECT_ATLAS_TEX);
        list5.forEach(Runnable::run);
    }
    
    protected void renderHotbar(final float float1) {
        final PlayerEntity playerEntity2 = this.getCameraPlayer();
        if (playerEntity2 == null) {
            return;
        }
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.client.getTextureManager().bindTexture(InGameHud.WIDGETS_TEX);
        final ItemStack itemStack3 = playerEntity2.getOffHandStack();
        final AbsoluteHand absoluteHand4 = playerEntity2.getMainHand().getOpposite();
        final int integer5 = this.scaledWidth / 2;
        final int integer6 = this.blitOffset;
        final int integer7 = 182;
        final int integer8 = 91;
        this.blitOffset = -90;
        this.blit(integer5 - 91, this.scaledHeight - 22, 0, 0, 182, 22);
        this.blit(integer5 - 91 - 1 + playerEntity2.inventory.selectedSlot * 20, this.scaledHeight - 22 - 1, 0, 22, 24, 22);
        if (!itemStack3.isEmpty()) {
            if (absoluteHand4 == AbsoluteHand.a) {
                this.blit(integer5 - 91 - 29, this.scaledHeight - 23, 24, 22, 29, 24);
            }
            else {
                this.blit(integer5 + 91, this.scaledHeight - 23, 53, 22, 29, 24);
            }
        }
        this.blitOffset = integer6;
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GuiLighting.enableForItems();
        for (int integer9 = 0; integer9 < 9; ++integer9) {
            final int integer10 = integer5 - 90 + integer9 * 20 + 2;
            final int integer11 = this.scaledHeight - 16 - 3;
            this.renderHotbarItem(integer10, integer11, float1, playerEntity2, playerEntity2.inventory.main.get(integer9));
        }
        if (!itemStack3.isEmpty()) {
            final int integer9 = this.scaledHeight - 16 - 3;
            if (absoluteHand4 == AbsoluteHand.a) {
                this.renderHotbarItem(integer5 - 91 - 26, integer9, float1, playerEntity2, itemStack3);
            }
            else {
                this.renderHotbarItem(integer5 + 91 + 10, integer9, float1, playerEntity2, itemStack3);
            }
        }
        if (this.client.options.attackIndicator == AttackIndicator.c) {
            final float float2 = this.client.player.s(0.0f);
            if (float2 < 1.0f) {
                final int integer10 = this.scaledHeight - 20;
                int integer11 = integer5 + 91 + 6;
                if (absoluteHand4 == AbsoluteHand.b) {
                    integer11 = integer5 - 91 - 22;
                }
                this.client.getTextureManager().bindTexture(DrawableHelper.GUI_ICONS_LOCATION);
                final int integer12 = (int)(float2 * 19.0f);
                GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                this.blit(integer11, integer10, 0, 94, 18, 18);
                this.blit(integer11, integer10 + 18 - integer12, 18, 112 - integer12, 18, integer12);
            }
        }
        GuiLighting.disable();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
    }
    
    public void renderMountJumpBar(final int integer) {
        this.client.getProfiler().push("jumpBar");
        this.client.getTextureManager().bindTexture(DrawableHelper.GUI_ICONS_LOCATION);
        final float float2 = this.client.player.G();
        final int integer2 = 182;
        final int integer3 = (int)(float2 * 183.0f);
        final int integer4 = this.scaledHeight - 32 + 3;
        this.blit(integer, integer4, 0, 84, 182, 5);
        if (integer3 > 0) {
            this.blit(integer, integer4, 0, 89, integer3, 5);
        }
        this.client.getProfiler().pop();
    }
    
    public void renderExperienceBar(final int integer) {
        this.client.getProfiler().push("expBar");
        this.client.getTextureManager().bindTexture(DrawableHelper.GUI_ICONS_LOCATION);
        final int integer2 = this.client.player.getNextLevelExperience();
        if (integer2 > 0) {
            final int integer3 = 182;
            final int integer4 = (int)(this.client.player.experienceLevelProgress * 183.0f);
            final int integer5 = this.scaledHeight - 32 + 3;
            this.blit(integer, integer5, 0, 64, 182, 5);
            if (integer4 > 0) {
                this.blit(integer, integer5, 0, 69, integer4, 5);
            }
        }
        this.client.getProfiler().pop();
        if (this.client.player.experience > 0) {
            this.client.getProfiler().push("expLevel");
            final String string3 = "" + this.client.player.experience;
            final int integer4 = (this.scaledWidth - this.getFontRenderer().getStringWidth(string3)) / 2;
            final int integer5 = this.scaledHeight - 31 - 4;
            this.getFontRenderer().draw(string3, (float)(integer4 + 1), (float)integer5, 0);
            this.getFontRenderer().draw(string3, (float)(integer4 - 1), (float)integer5, 0);
            this.getFontRenderer().draw(string3, (float)integer4, (float)(integer5 + 1), 0);
            this.getFontRenderer().draw(string3, (float)integer4, (float)(integer5 - 1), 0);
            this.getFontRenderer().draw(string3, (float)integer4, (float)integer5, 8453920);
            this.client.getProfiler().pop();
        }
    }
    
    public void renderHeldItemTooltip() {
        this.client.getProfiler().push("selectedItemName");
        if (this.heldItemTooltipFade > 0 && !this.currentStack.isEmpty()) {
            final TextComponent textComponent1 = new StringTextComponent("").append(this.currentStack.getDisplayName()).applyFormat(this.currentStack.getRarity().formatting);
            if (this.currentStack.hasDisplayName()) {
                textComponent1.applyFormat(TextFormat.u);
            }
            final String string2 = textComponent1.getFormattedText();
            final int integer3 = (this.scaledWidth - this.getFontRenderer().getStringWidth(string2)) / 2;
            int integer4 = this.scaledHeight - 59;
            if (!this.client.interactionManager.hasStatusBars()) {
                integer4 += 14;
            }
            int integer5 = (int)(this.heldItemTooltipFade * 256.0f / 10.0f);
            if (integer5 > 255) {
                integer5 = 255;
            }
            if (integer5 > 0) {
                GlStateManager.pushMatrix();
                GlStateManager.enableBlend();
                GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                final int left = integer3 - 2;
                final int top = integer4 - 2;
                final int right = integer3 + this.getFontRenderer().getStringWidth(string2) + 2;
                final int n = integer4;
                this.getFontRenderer().getClass();
                DrawableHelper.fill(left, top, right, n + 9 + 2, this.client.options.getTextBackgroundColor(0));
                this.getFontRenderer().drawWithShadow(string2, (float)integer3, (float)integer4, 16777215 + (integer5 << 24));
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
            }
        }
        this.client.getProfiler().pop();
    }
    
    public void renderDemoTimer() {
        this.client.getProfiler().push("demo");
        String string1;
        if (this.client.world.getTime() >= 120500L) {
            string1 = I18n.translate("demo.demoExpired");
        }
        else {
            string1 = I18n.translate("demo.remainingTime", ChatUtil.ticksToString((int)(120500L - this.client.world.getTime())));
        }
        final int integer2 = this.getFontRenderer().getStringWidth(string1);
        this.getFontRenderer().drawWithShadow(string1, (float)(this.scaledWidth - integer2 - 10), 5.0f, 16777215);
        this.client.getProfiler().pop();
    }
    
    private void renderScoreboardSidebar(final ScoreboardObjective scoreboardObjective) {
        final Scoreboard scoreboard2 = scoreboardObjective.getScoreboard();
        Collection<ScoreboardPlayerScore> collection3 = scoreboard2.getAllPlayerScores(scoreboardObjective);
        final List<ScoreboardPlayerScore> list4 = collection3.stream().filter(scoreboardPlayerScore -> scoreboardPlayerScore.getPlayerName() != null && !scoreboardPlayerScore.getPlayerName().startsWith("#")).collect(Collectors.toList());
        if (list4.size() > 15) {
            collection3 = Lists.newArrayList(Iterables.skip(list4, collection3.size() - 15));
        }
        else {
            collection3 = list4;
        }
        final String string5 = scoreboardObjective.getDisplayName().getFormattedText();
        int integer7;
        final int integer6 = integer7 = this.getFontRenderer().getStringWidth(string5);
        for (final ScoreboardPlayerScore scoreboardPlayerScore2 : collection3) {
            final Team team10 = scoreboard2.getPlayerTeam(scoreboardPlayerScore2.getPlayerName());
            final String string6 = Team.modifyText(team10, new StringTextComponent(scoreboardPlayerScore2.getPlayerName())).getFormattedText() + ": " + TextFormat.m + scoreboardPlayerScore2.getScore();
            integer7 = Math.max(integer7, this.getFontRenderer().getStringWidth(string6));
        }
        final int size = collection3.size();
        this.getFontRenderer().getClass();
        final int integer8 = size * 9;
        final int integer9 = this.scaledHeight / 2 + integer8 / 3;
        final int integer10 = 3;
        final int integer11 = this.scaledWidth - integer7 - 3;
        int integer12 = 0;
        final int integer13 = this.client.options.getTextBackgroundColor(0.3f);
        final int integer14 = this.client.options.getTextBackgroundColor(0.4f);
        for (final ScoreboardPlayerScore scoreboardPlayerScore3 : collection3) {
            ++integer12;
            final Team team11 = scoreboard2.getPlayerTeam(scoreboardPlayerScore3.getPlayerName());
            final String string7 = Team.modifyText(team11, new StringTextComponent(scoreboardPlayerScore3.getPlayerName())).getFormattedText();
            final String string8 = TextFormat.m + "" + scoreboardPlayerScore3.getScore();
            final int integer15 = integer11;
            final int n = integer9;
            final int n2 = integer12;
            this.getFontRenderer().getClass();
            final int integer16 = n - n2 * 9;
            final int integer17 = this.scaledWidth - 3 + 2;
            final int left = integer15 - 2;
            final int top = integer16;
            final int right = integer17;
            final int n3 = integer16;
            this.getFontRenderer().getClass();
            DrawableHelper.fill(left, top, right, n3 + 9, integer13);
            this.getFontRenderer().draw(string7, (float)integer15, (float)integer16, 553648127);
            this.getFontRenderer().draw(string8, (float)(integer17 - this.getFontRenderer().getStringWidth(string8)), (float)integer16, 553648127);
            if (integer12 == collection3.size()) {
                final int left2 = integer15 - 2;
                final int n4 = integer16;
                this.getFontRenderer().getClass();
                DrawableHelper.fill(left2, n4 - 9 - 1, integer17, integer16 - 1, integer14);
                DrawableHelper.fill(integer15 - 2, integer16 - 1, integer17, integer16, integer13);
                final TextRenderer fontRenderer = this.getFontRenderer();
                final String string9 = string5;
                final float x = (float)(integer15 + integer7 / 2 - integer6 / 2);
                final int n5 = integer16;
                this.getFontRenderer().getClass();
                fontRenderer.draw(string9, x, (float)(n5 - 9), 553648127);
            }
        }
    }
    
    private PlayerEntity getCameraPlayer() {
        if (!(this.client.getCameraEntity() instanceof PlayerEntity)) {
            return null;
        }
        return (PlayerEntity)this.client.getCameraEntity();
    }
    
    private LivingEntity getRiddenEntity() {
        final PlayerEntity playerEntity1 = this.getCameraPlayer();
        if (playerEntity1 != null) {
            final Entity entity2 = playerEntity1.getVehicle();
            if (entity2 == null) {
                return null;
            }
            if (entity2 instanceof LivingEntity) {
                return (LivingEntity)entity2;
            }
        }
        return null;
    }
    
    private int a(final LivingEntity livingEntity) {
        if (livingEntity == null || !livingEntity.isLiving()) {
            return 0;
        }
        final float float2 = livingEntity.getHealthMaximum();
        int integer3 = (int)(float2 + 0.5f) / 2;
        if (integer3 > 30) {
            integer3 = 30;
        }
        return integer3;
    }
    
    private int c(final int integer) {
        return (int)Math.ceil(integer / 10.0);
    }
    
    private void renderStatusBars() {
        final PlayerEntity playerEntity1 = this.getCameraPlayer();
        if (playerEntity1 == null) {
            return;
        }
        final int integer2 = MathHelper.ceil(playerEntity1.getHealth());
        final boolean boolean3 = this.C > this.ticks && (this.C - this.ticks) / 3L % 2L == 1L;
        final long long4 = SystemUtil.getMeasuringTimeMs();
        if (integer2 < this.z && playerEntity1.T > 0) {
            this.B = long4;
            this.C = this.ticks + 20;
        }
        else if (integer2 > this.z && playerEntity1.T > 0) {
            this.B = long4;
            this.C = this.ticks + 10;
        }
        if (long4 - this.B > 1000L) {
            this.z = integer2;
            this.A = integer2;
            this.B = long4;
        }
        this.z = integer2;
        final int integer3 = this.A;
        this.random.setSeed(this.ticks * 312871);
        final HungerManager hungerManager7 = playerEntity1.getHungerManager();
        final int integer4 = hungerManager7.getFoodLevel();
        final EntityAttributeInstance entityAttributeInstance9 = playerEntity1.getAttributeInstance(EntityAttributes.MAX_HEALTH);
        final int integer5 = this.scaledWidth / 2 - 91;
        final int integer6 = this.scaledWidth / 2 + 91;
        final int integer7 = this.scaledHeight - 39;
        final float float13 = (float)entityAttributeInstance9.getValue();
        final int integer8 = MathHelper.ceil(playerEntity1.getAbsorptionAmount());
        final int integer9 = MathHelper.ceil((float13 + integer8) / 2.0f / 10.0f);
        final int integer10 = Math.max(10 - (integer9 - 2), 3);
        final int integer11 = integer7 - (integer9 - 1) * integer10 - 10;
        int integer12 = integer7 - 10;
        int integer13 = integer8;
        final int integer14 = playerEntity1.getArmor();
        int integer15 = -1;
        if (playerEntity1.hasStatusEffect(StatusEffects.j)) {
            integer15 = this.ticks % MathHelper.ceil(float13 + 5.0f);
        }
        this.client.getProfiler().push("armor");
        for (int integer16 = 0; integer16 < 10; ++integer16) {
            if (integer14 > 0) {
                final int integer17 = integer5 + integer16 * 8;
                if (integer16 * 2 + 1 < integer14) {
                    this.blit(integer17, integer11, 34, 9, 9, 9);
                }
                if (integer16 * 2 + 1 == integer14) {
                    this.blit(integer17, integer11, 25, 9, 9, 9);
                }
                if (integer16 * 2 + 1 > integer14) {
                    this.blit(integer17, integer11, 16, 9, 9, 9);
                }
            }
        }
        this.client.getProfiler().swap("health");
        for (int integer16 = MathHelper.ceil((float13 + integer8) / 2.0f) - 1; integer16 >= 0; --integer16) {
            int integer17 = 16;
            if (playerEntity1.hasStatusEffect(StatusEffects.s)) {
                integer17 += 36;
            }
            else if (playerEntity1.hasStatusEffect(StatusEffects.t)) {
                integer17 += 72;
            }
            int integer18 = 0;
            if (boolean3) {
                integer18 = 1;
            }
            final int integer19 = MathHelper.ceil((integer16 + 1) / 10.0f) - 1;
            final int integer20 = integer5 + integer16 % 10 * 8;
            int integer21 = integer7 - integer19 * integer10;
            if (integer2 <= 4) {
                integer21 += this.random.nextInt(2);
            }
            if (integer13 <= 0 && integer16 == integer15) {
                integer21 -= 2;
            }
            int integer22 = 0;
            if (playerEntity1.world.getLevelProperties().isHardcore()) {
                integer22 = 5;
            }
            this.blit(integer20, integer21, 16 + integer18 * 9, 9 * integer22, 9, 9);
            if (boolean3) {
                if (integer16 * 2 + 1 < integer3) {
                    this.blit(integer20, integer21, integer17 + 54, 9 * integer22, 9, 9);
                }
                if (integer16 * 2 + 1 == integer3) {
                    this.blit(integer20, integer21, integer17 + 63, 9 * integer22, 9, 9);
                }
            }
            if (integer13 > 0) {
                if (integer13 == integer8 && integer8 % 2 == 1) {
                    this.blit(integer20, integer21, integer17 + 153, 9 * integer22, 9, 9);
                    --integer13;
                }
                else {
                    this.blit(integer20, integer21, integer17 + 144, 9 * integer22, 9, 9);
                    integer13 -= 2;
                }
            }
            else {
                if (integer16 * 2 + 1 < integer2) {
                    this.blit(integer20, integer21, integer17 + 36, 9 * integer22, 9, 9);
                }
                if (integer16 * 2 + 1 == integer2) {
                    this.blit(integer20, integer21, integer17 + 45, 9 * integer22, 9, 9);
                }
            }
        }
        final LivingEntity livingEntity22 = this.getRiddenEntity();
        int integer17 = this.a(livingEntity22);
        if (integer17 == 0) {
            this.client.getProfiler().swap("food");
            for (int integer18 = 0; integer18 < 10; ++integer18) {
                int integer19 = integer7;
                int integer20 = 16;
                int integer21 = 0;
                if (playerEntity1.hasStatusEffect(StatusEffects.q)) {
                    integer20 += 36;
                    integer21 = 13;
                }
                if (playerEntity1.getHungerManager().getSaturationLevel() <= 0.0f && this.ticks % (integer4 * 3 + 1) == 0) {
                    integer19 += this.random.nextInt(3) - 1;
                }
                final int integer22 = integer6 - integer18 * 8 - 9;
                this.blit(integer22, integer19, 16 + integer21 * 9, 27, 9, 9);
                if (integer18 * 2 + 1 < integer4) {
                    this.blit(integer22, integer19, integer20 + 36, 27, 9, 9);
                }
                if (integer18 * 2 + 1 == integer4) {
                    this.blit(integer22, integer19, integer20 + 45, 27, 9, 9);
                }
            }
            integer12 -= 10;
        }
        this.client.getProfiler().swap("air");
        int integer18 = playerEntity1.getBreath();
        int integer19 = playerEntity1.getMaxBreath();
        if (playerEntity1.isInFluid(FluidTags.a) || integer18 < integer19) {
            final int integer20 = this.c(integer17) - 1;
            integer12 -= integer20 * 10;
            for (int integer21 = MathHelper.ceil((integer18 - 2) * 10.0 / integer19), integer22 = MathHelper.ceil(integer18 * 10.0 / integer19) - integer21, integer23 = 0; integer23 < integer21 + integer22; ++integer23) {
                if (integer23 < integer21) {
                    this.blit(integer6 - integer23 * 8 - 9, integer12, 16, 18, 9, 9);
                }
                else {
                    this.blit(integer6 - integer23 * 8 - 9, integer12, 25, 18, 9, 9);
                }
            }
        }
        this.client.getProfiler().pop();
    }
    
    private void drawMountHealth() {
        final LivingEntity livingEntity1 = this.getRiddenEntity();
        if (livingEntity1 == null) {
            return;
        }
        int integer2 = this.a(livingEntity1);
        if (integer2 == 0) {
            return;
        }
        final int integer3 = (int)Math.ceil(livingEntity1.getHealth());
        this.client.getProfiler().swap("mountHealth");
        final int integer4 = this.scaledHeight - 39;
        final int integer5 = this.scaledWidth / 2 + 91;
        int integer6 = integer4;
        int integer7 = 0;
        final boolean boolean8 = false;
        while (integer2 > 0) {
            final int integer8 = Math.min(integer2, 10);
            integer2 -= integer8;
            for (int integer9 = 0; integer9 < integer8; ++integer9) {
                final int integer10 = 52;
                final int integer11 = 0;
                final int integer12 = integer5 - integer9 * 8 - 9;
                this.blit(integer12, integer6, 52 + integer11 * 9, 9, 9, 9);
                if (integer9 * 2 + 1 + integer7 < integer3) {
                    this.blit(integer12, integer6, 88, 9, 9, 9);
                }
                if (integer9 * 2 + 1 + integer7 == integer3) {
                    this.blit(integer12, integer6, 97, 9, 9, 9);
                }
            }
            integer6 -= 10;
            integer7 += 20;
        }
    }
    
    private void renderPumpkinOverlay() {
        GlStateManager.disableDepthTest();
        GlStateManager.depthMask(false);
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.disableAlphaTest();
        this.client.getTextureManager().bindTexture(InGameHud.PUMPKIN_BLUR);
        final Tessellator tessellator1 = Tessellator.getInstance();
        final BufferBuilder bufferBuilder2 = tessellator1.getBufferBuilder();
        bufferBuilder2.begin(7, VertexFormats.POSITION_UV);
        bufferBuilder2.vertex(0.0, this.scaledHeight, -90.0).texture(0.0, 1.0).next();
        bufferBuilder2.vertex(this.scaledWidth, this.scaledHeight, -90.0).texture(1.0, 1.0).next();
        bufferBuilder2.vertex(this.scaledWidth, 0.0, -90.0).texture(1.0, 0.0).next();
        bufferBuilder2.vertex(0.0, 0.0, -90.0).texture(0.0, 0.0).next();
        tessellator1.draw();
        GlStateManager.depthMask(true);
        GlStateManager.enableDepthTest();
        GlStateManager.enableAlphaTest();
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
    }
    
    private void a(final Entity entity) {
        if (entity == null) {
            return;
        }
        final float float2 = MathHelper.clamp(1.0f - entity.getBrightnessAtEyes(), 0.0f, 1.0f);
        this.a += (float)((float2 - this.a) * 0.01);
    }
    
    private void renderVignetteOverlay(final Entity entity) {
        final WorldBorder worldBorder2 = this.client.world.getWorldBorder();
        float float3 = (float)worldBorder2.contains(entity);
        final double double4 = Math.min(worldBorder2.getShrinkingSpeed() * worldBorder2.getWarningTime() * 1000.0, Math.abs(worldBorder2.getTargetSize() - worldBorder2.getSize()));
        final double double5 = Math.max(worldBorder2.getWarningBlocks(), double4);
        if (float3 < double5) {
            float3 = 1.0f - (float)(float3 / double5);
        }
        else {
            float3 = 0.0f;
        }
        GlStateManager.disableDepthTest();
        GlStateManager.depthMask(false);
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        if (float3 > 0.0f) {
            GlStateManager.color4f(0.0f, float3, float3, 1.0f);
        }
        else {
            GlStateManager.color4f(this.a, this.a, this.a, 1.0f);
        }
        this.client.getTextureManager().bindTexture(InGameHud.VIGNETTE_TEX);
        final Tessellator tessellator8 = Tessellator.getInstance();
        final BufferBuilder bufferBuilder9 = tessellator8.getBufferBuilder();
        bufferBuilder9.begin(7, VertexFormats.POSITION_UV);
        bufferBuilder9.vertex(0.0, this.scaledHeight, -90.0).texture(0.0, 1.0).next();
        bufferBuilder9.vertex(this.scaledWidth, this.scaledHeight, -90.0).texture(1.0, 1.0).next();
        bufferBuilder9.vertex(this.scaledWidth, 0.0, -90.0).texture(1.0, 0.0).next();
        bufferBuilder9.vertex(0.0, 0.0, -90.0).texture(0.0, 0.0).next();
        tessellator8.draw();
        GlStateManager.depthMask(true);
        GlStateManager.enableDepthTest();
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
    }
    
    private void renderPortalOverlay(float float1) {
        if (float1 < 1.0f) {
            float1 *= float1;
            float1 *= float1;
            float1 = float1 * 0.8f + 0.2f;
        }
        GlStateManager.disableAlphaTest();
        GlStateManager.disableDepthTest();
        GlStateManager.depthMask(false);
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, float1);
        this.client.getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
        final Sprite sprite2 = this.client.getBlockRenderManager().getModels().getSprite(Blocks.cM.getDefaultState());
        final float float2 = sprite2.getMinU();
        final float float3 = sprite2.getMinV();
        final float float4 = sprite2.getMaxU();
        final float float5 = sprite2.getMaxV();
        final Tessellator tessellator7 = Tessellator.getInstance();
        final BufferBuilder bufferBuilder8 = tessellator7.getBufferBuilder();
        bufferBuilder8.begin(7, VertexFormats.POSITION_UV);
        bufferBuilder8.vertex(0.0, this.scaledHeight, -90.0).texture(float2, float5).next();
        bufferBuilder8.vertex(this.scaledWidth, this.scaledHeight, -90.0).texture(float4, float5).next();
        bufferBuilder8.vertex(this.scaledWidth, 0.0, -90.0).texture(float4, float3).next();
        bufferBuilder8.vertex(0.0, 0.0, -90.0).texture(float2, float3).next();
        tessellator7.draw();
        GlStateManager.depthMask(true);
        GlStateManager.enableDepthTest();
        GlStateManager.enableAlphaTest();
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
    }
    
    private void renderHotbarItem(final int integer1, final int integer2, final float float3, final PlayerEntity playerEntity, final ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return;
        }
        final float float4 = itemStack.getUpdateCooldown() - float3;
        if (float4 > 0.0f) {
            GlStateManager.pushMatrix();
            final float float5 = 1.0f + float4 / 5.0f;
            GlStateManager.translatef((float)(integer1 + 8), (float)(integer2 + 12), 0.0f);
            GlStateManager.scalef(1.0f / float5, (float5 + 1.0f) / 2.0f, 1.0f);
            GlStateManager.translatef((float)(-(integer1 + 8)), (float)(-(integer2 + 12)), 0.0f);
        }
        this.itemRenderer.renderGuiItem(playerEntity, itemStack, integer1, integer2);
        if (float4 > 0.0f) {
            GlStateManager.popMatrix();
        }
        this.itemRenderer.renderGuiItemOverlay(this.client.textRenderer, itemStack, integer1, integer2);
    }
    
    public void tick() {
        if (this.overlayRemaining > 0) {
            --this.overlayRemaining;
        }
        if (this.titleTotalTicks > 0) {
            --this.titleTotalTicks;
            if (this.titleTotalTicks <= 0) {
                this.title = "";
                this.subtitle = "";
            }
        }
        ++this.ticks;
        final Entity entity1 = this.client.getCameraEntity();
        if (entity1 != null) {
            this.a(entity1);
        }
        if (this.client.player != null) {
            final ItemStack itemStack2 = this.client.player.inventory.getMainHandStack();
            if (itemStack2.isEmpty()) {
                this.heldItemTooltipFade = 0;
            }
            else if (this.currentStack.isEmpty() || itemStack2.getItem() != this.currentStack.getItem() || !itemStack2.getDisplayName().equals(this.currentStack.getDisplayName())) {
                this.heldItemTooltipFade = 40;
            }
            else if (this.heldItemTooltipFade > 0) {
                --this.heldItemTooltipFade;
            }
            this.currentStack = itemStack2;
        }
    }
    
    public void setRecordPlayingOverlay(final String string) {
        this.setOverlayMessage(I18n.translate("record.nowPlaying", string), true);
    }
    
    public void setOverlayMessage(final String string, final boolean boolean2) {
        this.overlayMessage = string;
        this.overlayRemaining = 60;
        this.overlayTinted = boolean2;
    }
    
    public void setTitles(final String string1, final String string2, final int integer3, final int integer4, final int integer5) {
        if (string1 == null && string2 == null && integer3 < 0 && integer4 < 0 && integer5 < 0) {
            this.title = "";
            this.subtitle = "";
            this.titleTotalTicks = 0;
            return;
        }
        if (string1 != null) {
            this.title = string1;
            this.titleTotalTicks = this.titleFadeInTicks + this.titleRemainTicks + this.titleFadeOutTicks;
            return;
        }
        if (string2 != null) {
            this.subtitle = string2;
            return;
        }
        if (integer3 >= 0) {
            this.titleFadeInTicks = integer3;
        }
        if (integer4 >= 0) {
            this.titleRemainTicks = integer4;
        }
        if (integer5 >= 0) {
            this.titleFadeOutTicks = integer5;
        }
        if (this.titleTotalTicks > 0) {
            this.titleTotalTicks = this.titleFadeInTicks + this.titleRemainTicks + this.titleFadeOutTicks;
        }
    }
    
    public void setOverlayMessage(final TextComponent textComponent, final boolean boolean2) {
        this.setOverlayMessage(textComponent.getString(), boolean2);
    }
    
    public void addChatMessage(final ChatMessageType chatMessageType, final TextComponent textComponent) {
        for (final ClientChatListener clientChatListener4 : this.listeners.get(chatMessageType)) {
            clientChatListener4.onChatMessage(chatMessageType, textComponent);
        }
    }
    
    public ChatHud getChatHud() {
        return this.chatHud;
    }
    
    public int getTicks() {
        return this.ticks;
    }
    
    public TextRenderer getFontRenderer() {
        return this.client.textRenderer;
    }
    
    public SpectatorHud getSpectatorWidget() {
        return this.spectatorHud;
    }
    
    public PlayerListHud getPlayerListWidget() {
        return this.playerListHud;
    }
    
    public void clear() {
        this.playerListHud.clear();
        this.bossBarHud.clear();
        this.client.getToastManager().clear();
    }
    
    public BossBarHud getBossBarHud() {
        return this.bossBarHud;
    }
    
    public void resetDebugHudChunk() {
        this.debugHud.resetChunk();
    }
    
    static {
        VIGNETTE_TEX = new Identifier("textures/misc/vignette.png");
        WIDGETS_TEX = new Identifier("textures/gui/widgets.png");
        PUMPKIN_BLUR = new Identifier("textures/misc/pumpkinblur.png");
    }
}

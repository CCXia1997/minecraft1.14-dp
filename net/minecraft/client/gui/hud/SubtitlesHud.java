package net.minecraft.client.gui.hud;

import net.minecraft.client.audio.WeightedSoundSet;
import net.minecraft.client.audio.SoundInstance;
import java.util.Iterator;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.Vec3d;
import com.mojang.blaze3d.platform.GlStateManager;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.audio.ListenerSoundInstance;
import net.minecraft.client.gui.DrawableHelper;

@Environment(EnvType.CLIENT)
public class SubtitlesHud extends DrawableHelper implements ListenerSoundInstance
{
    private final MinecraftClient client;
    private final List<SubtitleEntry> entries;
    private boolean enabled;
    
    public SubtitlesHud(final MinecraftClient minecraftClient) {
        this.entries = Lists.newArrayList();
        this.client = minecraftClient;
    }
    
    public void draw() {
        if (!this.enabled && this.client.options.showSubtitles) {
            this.client.getSoundManager().registerListener(this);
            this.enabled = true;
        }
        else if (this.enabled && !this.client.options.showSubtitles) {
            this.client.getSoundManager().unregisterListener(this);
            this.enabled = false;
        }
        if (!this.enabled || this.entries.isEmpty()) {
            return;
        }
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        final Vec3d vec3d1 = new Vec3d(this.client.player.x, this.client.player.y + this.client.player.getStandingEyeHeight(), this.client.player.z);
        final Vec3d vec3d2 = new Vec3d(0.0, 0.0, -1.0).rotateX(-this.client.player.pitch * 0.017453292f).rotateY(-this.client.player.yaw * 0.017453292f);
        final Vec3d vec3d3 = new Vec3d(0.0, 1.0, 0.0).rotateX(-this.client.player.pitch * 0.017453292f).rotateY(-this.client.player.yaw * 0.017453292f);
        final Vec3d vec3d4 = vec3d2.crossProduct(vec3d3);
        int integer5 = 0;
        int integer6 = 0;
        final Iterator<SubtitleEntry> iterator7 = this.entries.iterator();
        while (iterator7.hasNext()) {
            final SubtitleEntry subtitleEntry8 = iterator7.next();
            if (subtitleEntry8.getTime() + 3000L <= SystemUtil.getMeasuringTimeMs()) {
                iterator7.remove();
            }
            else {
                integer6 = Math.max(integer6, this.client.textRenderer.getStringWidth(subtitleEntry8.getText()));
            }
        }
        integer6 += this.client.textRenderer.getStringWidth("<") + this.client.textRenderer.getStringWidth(" ") + this.client.textRenderer.getStringWidth(">") + this.client.textRenderer.getStringWidth(" ");
        final Iterator<SubtitleEntry> iterator8 = this.entries.iterator();
        while (iterator8.hasNext()) {
            final SubtitleEntry subtitleEntry8 = iterator8.next();
            final int integer7 = 255;
            final String string10 = subtitleEntry8.getText();
            final Vec3d vec3d5 = subtitleEntry8.getPosition().subtract(vec3d1).normalize();
            final double double12 = -vec3d4.dotProduct(vec3d5);
            final double double13 = -vec3d2.dotProduct(vec3d5);
            final boolean boolean16 = double13 > 0.5;
            final int integer8 = integer6 / 2;
            this.client.textRenderer.getClass();
            final int integer9 = 9;
            final int integer10 = integer9 / 2;
            final float float20 = 1.0f;
            final int integer11 = this.client.textRenderer.getStringWidth(string10);
            final int integer12 = MathHelper.floor(MathHelper.clampedLerp(255.0, 75.0, (SystemUtil.getMeasuringTimeMs() - subtitleEntry8.getTime()) / 3000.0f));
            final int integer13 = integer12 << 16 | integer12 << 8 | integer12;
            GlStateManager.pushMatrix();
            GlStateManager.translatef(this.client.window.getScaledWidth() - integer8 * 1.0f - 2.0f, this.client.window.getScaledHeight() - 30 - integer5 * (integer9 + 1) * 1.0f, 0.0f);
            GlStateManager.scalef(1.0f, 1.0f, 1.0f);
            DrawableHelper.fill(-integer8 - 1, -integer10 - 1, integer8 + 1, integer10 + 1, this.client.options.getTextBackgroundColor(0.8f));
            GlStateManager.enableBlend();
            if (!boolean16) {
                if (double12 > 0.0) {
                    this.client.textRenderer.draw(">", (float)(integer8 - this.client.textRenderer.getStringWidth(">")), (float)(-integer10), integer13 - 16777216);
                }
                else if (double12 < 0.0) {
                    this.client.textRenderer.draw("<", (float)(-integer8), (float)(-integer10), integer13 - 16777216);
                }
            }
            this.client.textRenderer.draw(string10, (float)(-integer11 / 2), (float)(-integer10), integer13 - 16777216);
            GlStateManager.popMatrix();
            ++integer5;
        }
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }
    
    @Override
    public void onSoundPlayed(final SoundInstance sound, final WeightedSoundSet soundSet) {
        if (soundSet.getSubtitle() == null) {
            return;
        }
        final String string3 = soundSet.getSubtitle().getFormattedText();
        if (!this.entries.isEmpty()) {
            for (final SubtitleEntry subtitleEntry5 : this.entries) {
                if (subtitleEntry5.getText().equals(string3)) {
                    subtitleEntry5.reset(new Vec3d(sound.getX(), sound.getY(), sound.getZ()));
                    return;
                }
            }
        }
        this.entries.add(new SubtitleEntry(string3, new Vec3d(sound.getX(), sound.getY(), sound.getZ())));
    }
    
    @Environment(EnvType.CLIENT)
    public class SubtitleEntry
    {
        private final String text;
        private long time;
        private Vec3d pos;
        
        public SubtitleEntry(final String string, final Vec3d vec3d) {
            this.text = string;
            this.pos = vec3d;
            this.time = SystemUtil.getMeasuringTimeMs();
        }
        
        public String getText() {
            return this.text;
        }
        
        public long getTime() {
            return this.time;
        }
        
        public Vec3d getPosition() {
            return this.pos;
        }
        
        public void reset(final Vec3d vec3d) {
            this.pos = vec3d;
            this.time = SystemUtil.getMeasuringTimeMs();
        }
    }
}

package net.minecraft.client.toast;

import java.util.Iterator;
import java.util.List;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.entity.LivingEntity;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.audio.SoundInstance;
import net.minecraft.client.audio.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.advancement.AdvancementFrame;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.advancement.Advancement;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class AdvancementToast implements Toast
{
    private final Advancement c;
    private boolean d;
    
    public AdvancementToast(final Advancement advancement) {
        this.c = advancement;
    }
    
    @Override
    public Visibility draw(final ToastManager manager, final long currentTime) {
        manager.getGame().getTextureManager().bindTexture(AdvancementToast.TOASTS_TEX);
        GlStateManager.color3f(1.0f, 1.0f, 1.0f);
        final AdvancementDisplay advancementDisplay4 = this.c.getDisplay();
        manager.blit(0, 0, 0, 0, 160, 32);
        if (advancementDisplay4 != null) {
            final List<String> list5 = manager.getGame().textRenderer.wrapStringToWidthAsList(advancementDisplay4.getTitle().getFormattedText(), 125);
            final int integer6 = (advancementDisplay4.getFrame() == AdvancementFrame.CHALLENGE) ? 16746751 : 16776960;
            if (list5.size() == 1) {
                manager.getGame().textRenderer.draw(I18n.translate("advancements.toast." + advancementDisplay4.getFrame().getId()), 30.0f, 7.0f, integer6 | 0xFF000000);
                manager.getGame().textRenderer.draw(advancementDisplay4.getTitle().getFormattedText(), 30.0f, 18.0f, -1);
            }
            else {
                final int integer7 = 1500;
                final float float8 = 300.0f;
                if (currentTime < 1500L) {
                    final int integer8 = MathHelper.floor(MathHelper.clamp((1500L - currentTime) / 300.0f, 0.0f, 1.0f) * 255.0f) << 24 | 0x4000000;
                    manager.getGame().textRenderer.draw(I18n.translate("advancements.toast." + advancementDisplay4.getFrame().getId()), 30.0f, 11.0f, integer6 | integer8);
                }
                else {
                    final int integer8 = MathHelper.floor(MathHelper.clamp((currentTime - 1500L) / 300.0f, 0.0f, 1.0f) * 252.0f) << 24 | 0x4000000;
                    final int n = 16;
                    final int size = list5.size();
                    manager.getGame().textRenderer.getClass();
                    int integer9 = n - size * 9 / 2;
                    for (final String string12 : list5) {
                        manager.getGame().textRenderer.draw(string12, 30.0f, (float)integer9, 0xFFFFFF | integer8);
                        final int n2 = integer9;
                        manager.getGame().textRenderer.getClass();
                        integer9 = n2 + 9;
                    }
                }
            }
            if (!this.d && currentTime > 0L) {
                this.d = true;
                if (advancementDisplay4.getFrame() == AdvancementFrame.CHALLENGE) {
                    manager.getGame().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.mn, 1.0f, 1.0f));
                }
            }
            GuiLighting.enableForItems();
            manager.getGame().getItemRenderer().renderGuiItem(null, advancementDisplay4.getIcon(), 8, 8);
            return (currentTime >= 5000L) ? Visibility.b : Visibility.a;
        }
        return Visibility.b;
    }
}

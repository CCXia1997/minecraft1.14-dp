package net.minecraft.client.gui.widget;

import net.minecraft.client.audio.SoundInstance;
import net.minecraft.client.audio.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.gui.WrittenBookScreen;
import net.minecraft.client.MinecraftClient;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class BookPageButtonWidget extends ButtonWidget
{
    private final boolean isNextPageButton;
    private final boolean playPageTurnSound;
    
    public BookPageButtonWidget(final int x, final int y, final boolean boolean3, final PressAction pressAction, final boolean playPageTurnSound) {
        super(x, y, 23, 13, "", pressAction);
        this.isNextPageButton = boolean3;
        this.playPageTurnSound = playPageTurnSound;
    }
    
    @Override
    public void renderButton(final int mouseX, final int mouseY, final float delta) {
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        MinecraftClient.getInstance().getTextureManager().bindTexture(WrittenBookScreen.BOOK_TEXTURE);
        int integer4 = 0;
        int integer5 = 192;
        if (this.isHovered()) {
            integer4 += 23;
        }
        if (!this.isNextPageButton) {
            integer5 += 13;
        }
        this.blit(this.x, this.y, integer4, integer5, 23, 13);
    }
    
    @Override
    public void playDownSound(final SoundManager soundManager) {
        if (this.playPageTurnSound) {
            soundManager.play(PositionedSoundInstance.master(SoundEvents.ah, 1.0f));
        }
    }
}

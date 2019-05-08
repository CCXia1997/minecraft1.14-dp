package net.minecraft.client.input;

import net.minecraft.client.options.GameOptions;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class KeyboardInput extends Input
{
    private final GameOptions settings;
    
    public KeyboardInput(final GameOptions settings) {
        this.settings = settings;
    }
    
    @Override
    public void tick(final boolean boolean1, final boolean boolean2) {
        this.pressingForward = this.settings.keyForward.isPressed();
        this.pressingBack = this.settings.keyBack.isPressed();
        this.pressingLeft = this.settings.keyLeft.isPressed();
        this.pressingRight = this.settings.keyRight.isPressed();
        this.movementForward = ((this.pressingForward == this.pressingBack) ? 0.0f : ((float)(this.pressingForward ? 1 : -1)));
        this.movementSideways = ((this.pressingLeft == this.pressingRight) ? 0.0f : ((float)(this.pressingLeft ? 1 : -1)));
        this.jumping = this.settings.keyJump.isPressed();
        this.sneaking = this.settings.keySneak.isPressed();
        if (!boolean2 && (this.sneaking || boolean1)) {
            this.movementSideways *= (float)0.3;
            this.movementForward *= (float)0.3;
        }
    }
}

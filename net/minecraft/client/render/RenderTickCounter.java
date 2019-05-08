package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class RenderTickCounter
{
    public int ticksThisFrame;
    public float tickDelta;
    public float lastFrameDuration;
    private long prevTimeMillis;
    private final float timeScale;
    
    public RenderTickCounter(final float tps, final long timeMillis) {
        this.timeScale = 1000.0f / tps;
        this.prevTimeMillis = timeMillis;
    }
    
    public void beginRenderTick(final long timeMillis) {
        this.lastFrameDuration = (timeMillis - this.prevTimeMillis) / this.timeScale;
        this.prevTimeMillis = timeMillis;
        this.tickDelta += this.lastFrameDuration;
        this.ticksThisFrame = (int)this.tickDelta;
        this.tickDelta -= this.ticksThisFrame;
    }
}

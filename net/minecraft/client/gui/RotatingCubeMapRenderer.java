package net.minecraft.client.gui;

import net.minecraft.util.math.MathHelper;
import net.minecraft.client.MinecraftClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class RotatingCubeMapRenderer
{
    private final MinecraftClient client;
    private final CubeMapRenderer cubeMap;
    private float time;
    
    public RotatingCubeMapRenderer(final CubeMapRenderer cubeMap) {
        this.cubeMap = cubeMap;
        this.client = MinecraftClient.getInstance();
    }
    
    public void render(final float delta, final float alpha) {
        this.time += delta;
        this.cubeMap.draw(this.client, MathHelper.sin(this.time * 0.001f) * 5.0f + 25.0f, -this.time * 0.1f, alpha);
        this.client.window.a(MinecraftClient.IS_SYSTEM_MAC);
    }
}

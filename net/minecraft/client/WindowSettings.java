package net.minecraft.client;

import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class WindowSettings
{
    public final int width;
    public final int height;
    public final Optional<Integer> fullscreenWidth;
    public final Optional<Integer> fullscreenHeight;
    public final boolean fullscreen;
    
    public WindowSettings(final int width, final int height, final Optional<Integer> fullscreenWidth, final Optional<Integer> fullscreenHeight, final boolean fullscreen) {
        this.width = width;
        this.height = height;
        this.fullscreenWidth = fullscreenWidth;
        this.fullscreenHeight = fullscreenHeight;
        this.fullscreen = fullscreen;
    }
}

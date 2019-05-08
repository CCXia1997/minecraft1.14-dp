package net.minecraft.client.util;

import net.minecraft.client.WindowEventHandler;
import net.minecraft.client.WindowSettings;
import net.minecraft.client.options.GameOption;
import net.minecraft.client.MinecraftClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class WindowProvider implements AutoCloseable
{
    private final MinecraftClient client;
    private final MonitorTracker monitorTracker;
    
    public WindowProvider(final MinecraftClient minecraftClient) {
        this.client = minecraftClient;
        this.monitorTracker = new MonitorTracker(this::createMonitor);
    }
    
    public Monitor createMonitor(final long long1) {
        final Monitor monitor3 = new Monitor(this.monitorTracker, long1);
        GameOption.FULLSCREEN_RESOLUTION.setMax((float)monitor3.getVideoModeCount());
        return monitor3;
    }
    
    public Window createWindow(final WindowSettings windowSettings, final String string2, final String string3) {
        return new Window(this.client, this.monitorTracker, windowSettings, string2, string3);
    }
    
    @Override
    public void close() {
        this.monitorTracker.stop();
    }
}

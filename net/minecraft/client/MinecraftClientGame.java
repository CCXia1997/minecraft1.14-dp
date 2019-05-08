package net.minecraft.client;

import net.minecraft.util.MetricsData;
import com.mojang.bridge.game.PerformanceMetrics;
import net.minecraft.client.world.ClientWorld;
import com.mojang.bridge.game.GameSession;
import com.mojang.bridge.game.Language;
import net.minecraft.SharedConstants;
import com.mojang.bridge.game.GameVersion;
import com.mojang.bridge.Bridge;
import com.mojang.bridge.launcher.SessionEventListener;
import javax.annotation.Nullable;
import com.mojang.bridge.launcher.Launcher;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import com.mojang.bridge.game.RunningGame;

@Environment(EnvType.CLIENT)
public class MinecraftClientGame implements RunningGame
{
    private final MinecraftClient client;
    @Nullable
    private final Launcher launcher;
    private SessionEventListener listener;
    
    public MinecraftClientGame(final MinecraftClient client) {
        this.listener = SessionEventListener.NONE;
        this.client = client;
        this.launcher = Bridge.getLauncher();
        if (this.launcher != null) {
            this.launcher.registerGame((RunningGame)this);
        }
    }
    
    public GameVersion getVersion() {
        return SharedConstants.getGameVersion();
    }
    
    public Language getSelectedLanguage() {
        return (Language)this.client.getLanguageManager().getLanguage();
    }
    
    @Nullable
    public GameSession getCurrentSession() {
        final ClientWorld clientWorld1 = this.client.world;
        return (GameSession)((clientWorld1 == null) ? null : new ClientGameSession(clientWorld1, this.client.player, this.client.player.networkHandler));
    }
    
    public PerformanceMetrics getPerformanceMetrics() {
        final MetricsData metricsData1 = this.client.getMetricsData();
        long long2 = 2147483647L;
        long long3 = -2147483648L;
        long long4 = 0L;
        for (final long long5 : metricsData1.getSamples()) {
            long2 = Math.min(long2, long5);
            long3 = Math.max(long3, long5);
            long4 += long5;
        }
        return (PerformanceMetrics)new PerformanceMetricsImpl((int)long2, (int)long3, (int)(long4 / metricsData1.getSamples().length), metricsData1.getSamples().length);
    }
    
    public void setSessionEventListener(final SessionEventListener listener) {
        this.listener = listener;
    }
    
    public void onStartGameSession() {
        this.listener.onStartGameSession(this.getCurrentSession());
    }
    
    public void onLeaveGameSession() {
        this.listener.onLeaveGameSession(this.getCurrentSession());
    }
    
    @Environment(EnvType.CLIENT)
    static class PerformanceMetricsImpl implements PerformanceMetrics
    {
        private final int minTime;
        private final int maxTime;
        private final int averageTime;
        private final int sampleCount;
        
        public PerformanceMetricsImpl(final int minTime, final int maxTime, final int averageTime, final int sampleCount) {
            this.minTime = minTime;
            this.maxTime = maxTime;
            this.averageTime = averageTime;
            this.sampleCount = sampleCount;
        }
        
        public int getMinTime() {
            return this.minTime;
        }
        
        public int getMaxTime() {
            return this.maxTime;
        }
        
        public int getAverageTime() {
            return this.averageTime;
        }
        
        public int getSampleCount() {
            return this.sampleCount;
        }
    }
}

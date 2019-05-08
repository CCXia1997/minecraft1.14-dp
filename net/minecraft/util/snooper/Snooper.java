package net.minecraft.util.snooper;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.net.MalformedURLException;
import java.util.UUID;
import com.google.common.collect.Maps;
import java.util.Timer;
import java.net.URL;
import java.util.Map;

public class Snooper
{
    private final Map<String, Object> initialInfo;
    private final Map<String, Object> info;
    private final String token;
    private final URL snooperUrl;
    private final SnooperListener listener;
    private final Timer timer;
    private final Object syncObject;
    private final long startTime;
    private boolean active;
    
    public Snooper(final String urlPath, final SnooperListener snooperListener, final long long3) {
        this.initialInfo = Maps.newHashMap();
        this.info = Maps.newHashMap();
        this.token = UUID.randomUUID().toString();
        this.timer = new Timer("Snooper Timer", true);
        this.syncObject = new Object();
        try {
            this.snooperUrl = new URL("http://snoop.minecraft.net/" + urlPath + "?version=" + 2);
        }
        catch (MalformedURLException malformedURLException5) {
            throw new IllegalArgumentException();
        }
        this.listener = snooperListener;
        this.startTime = long3;
    }
    
    public void a() {
        if (!this.active) {}
    }
    
    public void update() {
        this.addInitialInfo("memory_total", Runtime.getRuntime().totalMemory());
        this.addInitialInfo("memory_max", Runtime.getRuntime().maxMemory());
        this.addInitialInfo("memory_free", Runtime.getRuntime().freeMemory());
        this.addInitialInfo("cpu_cores", Runtime.getRuntime().availableProcessors());
        this.listener.addSnooperInfo(this);
    }
    
    public void addInfo(final String key, final Object object) {
        synchronized (this.syncObject) {
            this.info.put(key, object);
        }
    }
    
    public void addInitialInfo(final String key, final Object object) {
        synchronized (this.syncObject) {
            this.initialInfo.put(key, object);
        }
    }
    
    public boolean isActive() {
        return this.active;
    }
    
    public void cancel() {
        this.timer.cancel();
    }
    
    @Environment(EnvType.CLIENT)
    public String getToken() {
        return this.token;
    }
    
    public long getStartTime() {
        return this.startTime;
    }
}

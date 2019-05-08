package net.minecraft.client.toast;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.MathHelper;
import java.util.Arrays;
import javax.annotation.Nullable;
import java.util.Iterator;
import net.minecraft.client.render.GuiLighting;
import com.google.common.collect.Queues;
import java.util.Deque;
import net.minecraft.client.MinecraftClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;

@Environment(EnvType.CLIENT)
public class ToastManager extends DrawableHelper
{
    private final MinecraftClient client;
    private final Entry<?>[] visibleEntries;
    private final Deque<Toast> toastQueue;
    
    public ToastManager(final MinecraftClient minecraftClient) {
        this.visibleEntries = new Entry[5];
        this.toastQueue = Queues.newArrayDeque();
        this.client = minecraftClient;
    }
    
    public void draw() {
        if (this.client.options.hudHidden) {
            return;
        }
        GuiLighting.disable();
        for (int integer1 = 0; integer1 < this.visibleEntries.length; ++integer1) {
            final Entry<?> entry2 = this.visibleEntries[integer1];
            if (entry2 != null && entry2.draw(this.client.window.getScaledWidth(), integer1)) {
                this.visibleEntries[integer1] = null;
            }
            if (this.visibleEntries[integer1] == null && !this.toastQueue.isEmpty()) {
                this.visibleEntries[integer1] = new Entry<>((Toast)this.toastQueue.removeFirst());
            }
        }
    }
    
    @Nullable
    public <T extends Toast> T getToast(final Class<? extends T> toastClass, final Object type) {
        for (final Entry<?> entry6 : this.visibleEntries) {
            if (entry6 != null && toastClass.isAssignableFrom(entry6.getInstance().getClass()) && ((Toast)entry6.getInstance()).getType().equals(type)) {
                return (T)entry6.getInstance();
            }
        }
        for (final Toast toast4 : this.toastQueue) {
            if (toastClass.isAssignableFrom(toast4.getClass()) && toast4.getType().equals(type)) {
                return (T)toast4;
            }
        }
        return null;
    }
    
    public void clear() {
        Arrays.fill(this.visibleEntries, null);
        this.toastQueue.clear();
    }
    
    public void add(final Toast toast) {
        this.toastQueue.add(toast);
    }
    
    public MinecraftClient getGame() {
        return this.client;
    }
    
    @Environment(EnvType.CLIENT)
    class Entry<T extends Toast>
    {
        private final T instance;
        private long c;
        private long d;
        private Toast.Visibility e;
        
        private Entry(final T toast) {
            this.c = -1L;
            this.d = -1L;
            this.e = Toast.Visibility.a;
            this.instance = toast;
        }
        
        public T getInstance() {
            return this.instance;
        }
        
        private float getDissapearProgress(final long time) {
            float float3 = MathHelper.clamp((time - this.c) / 600.0f, 0.0f, 1.0f);
            float3 *= float3;
            if (this.e == Toast.Visibility.b) {
                return 1.0f - float3;
            }
            return float3;
        }
        
        public boolean draw(final int integer1, final int integer2) {
            final long long3 = SystemUtil.getMeasuringTimeMs();
            if (this.c == -1L) {
                this.c = long3;
                this.e.play(ToastManager.this.client.getSoundManager());
            }
            if (this.e == Toast.Visibility.a && long3 - this.c <= 600L) {
                this.d = long3;
            }
            GlStateManager.pushMatrix();
            GlStateManager.translatef(integer1 - 160.0f * this.getDissapearProgress(long3), (float)(integer2 * 32), (float)(500 + integer2));
            final Toast.Visibility visibility5 = this.instance.draw(ToastManager.this, long3 - this.d);
            GlStateManager.popMatrix();
            if (visibility5 != this.e) {
                this.c = long3 - (int)((1.0f - this.getDissapearProgress(long3)) * 600.0f);
                (this.e = visibility5).play(ToastManager.this.client.getSoundManager());
            }
            return this.e == Toast.Visibility.b && long3 - this.c > 600L;
        }
    }
}

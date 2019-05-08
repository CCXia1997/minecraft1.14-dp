package net.minecraft.realms;

import net.minecraft.text.TextComponent;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.ChatMessageType;
import net.minecraft.client.util.NarratorManager;
import com.google.common.util.concurrent.RateLimiter;
import java.util.concurrent.atomic.AtomicReference;
import java.time.Duration;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
class RepeatedNarrator
{
    final Duration repeatDelay;
    private final float permitsPerSecond;
    final AtomicReference<a> params;
    
    public RepeatedNarrator(final Duration duration) {
        this.repeatDelay = duration;
        this.params = new AtomicReference<a>();
        final float float2 = duration.toMillis() / 1000.0f;
        this.permitsPerSecond = 1.0f / float2;
    }
    
    public void narrate(final String string) {
        final a a2 = this.params.updateAndGet(a -> {
            if (a == null || !string.equals(a.a)) {
                return new a(string, RateLimiter.create(this.permitsPerSecond));
            }
            else {
                return a;
            }
        });
        if (a2.b.tryAcquire(1)) {
            final NarratorManager narratorManager3 = NarratorManager.INSTANCE;
            narratorManager3.onChatMessage(ChatMessageType.b, new StringTextComponent(string));
        }
    }
    
    @Environment(EnvType.CLIENT)
    static class a
    {
        String a;
        RateLimiter b;
        
        a(final String string, final RateLimiter rateLimiter) {
            this.a = string;
            this.b = rateLimiter;
        }
    }
}

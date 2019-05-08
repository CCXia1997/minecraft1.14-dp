package net.minecraft.client.audio;

import java.util.Iterator;
import java.util.function.Predicate;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.function.Consumer;
import com.google.common.collect.Sets;
import java.util.concurrent.Executor;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class Channel
{
    private final Set<SourceManager> sources;
    private final SoundEngine soundEngine;
    private final Executor executor;
    
    public Channel(final SoundEngine soundEngine, final Executor executor) {
        this.sources = Sets.<SourceManager>newIdentityHashSet();
        this.soundEngine = soundEngine;
        this.executor = executor;
    }
    
    public SourceManager createSource(final SoundEngine.RunMode mode) {
        final SourceManager sourceManager2 = new SourceManager();
        final Source source3;
        final SourceManager sourceManager3;
        this.executor.execute(() -> {
            source3 = this.soundEngine.createSource(mode);
            if (source3 != null) {
                sourceManager3.source = source3;
                this.sources.add(sourceManager3);
            }
            return;
        });
        return sourceManager2;
    }
    
    public void execute(final Consumer<Stream<Source>> consumer) {
        this.executor.execute(() -> consumer.accept(this.sources.stream().<Source>map(sourceManager -> sourceManager.source).filter(Objects::nonNull)));
    }
    
    public void tick() {
        final Iterator<SourceManager> iterator1;
        SourceManager sourceManager2;
        this.executor.execute(() -> {
            iterator1 = this.sources.iterator();
            while (iterator1.hasNext()) {
                sourceManager2 = iterator1.next();
                sourceManager2.source.tick();
                if (sourceManager2.source.isStopped()) {
                    sourceManager2.close();
                    iterator1.remove();
                }
            }
        });
    }
    
    public void close() {
        this.sources.forEach(SourceManager::close);
        this.sources.clear();
    }
    
    @Environment(EnvType.CLIENT)
    public class SourceManager
    {
        private Source source;
        private boolean stopped;
        
        public boolean isStopped() {
            return this.stopped;
        }
        
        public void run(final Consumer<Source> action) {
            Channel.this.executor.execute(() -> {
                if (this.source != null) {
                    action.accept(this.source);
                }
            });
        }
        
        public void close() {
            this.stopped = true;
            Channel.this.soundEngine.release(this.source);
            this.source = null;
        }
    }
}

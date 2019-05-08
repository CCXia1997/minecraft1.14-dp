package net.minecraft.client.resource.metadata;

import java.util.Iterator;
import com.google.common.collect.Sets;
import java.util.Set;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class AnimationResourceMetadata
{
    public static final AnimationResourceMetadataReader READER;
    private final List<AnimationFrameResourceMetadata> frames;
    private final int width;
    private final int height;
    private final int defaultFrameTime;
    private final boolean interpolate;
    
    public AnimationResourceMetadata(final List<AnimationFrameResourceMetadata> frames, final int width, final int height, final int defaultFrameTime, final boolean boolean5) {
        this.frames = frames;
        this.width = width;
        this.height = height;
        this.defaultFrameTime = defaultFrameTime;
        this.interpolate = boolean5;
    }
    
    public int getHeight() {
        return this.height;
    }
    
    public int getWidth() {
        return this.width;
    }
    
    public int getFrameCount() {
        return this.frames.size();
    }
    
    public int getDefaultFrameTime() {
        return this.defaultFrameTime;
    }
    
    public boolean shouldInterpolate() {
        return this.interpolate;
    }
    
    private AnimationFrameResourceMetadata getFrame(final int integer) {
        return this.frames.get(integer);
    }
    
    public int getFrameTime(final int integer) {
        final AnimationFrameResourceMetadata animationFrameResourceMetadata2 = this.getFrame(integer);
        if (animationFrameResourceMetadata2.usesDefaultFrameTime()) {
            return this.defaultFrameTime;
        }
        return animationFrameResourceMetadata2.getTime();
    }
    
    public int getFrameIndex(final int integer) {
        return this.frames.get(integer).getIndex();
    }
    
    public Set<Integer> getFrameIndexSet() {
        final Set<Integer> set1 = Sets.newHashSet();
        for (final AnimationFrameResourceMetadata animationFrameResourceMetadata3 : this.frames) {
            set1.add(animationFrameResourceMetadata3.getIndex());
        }
        return set1;
    }
    
    static {
        READER = new AnimationResourceMetadataReader();
    }
}

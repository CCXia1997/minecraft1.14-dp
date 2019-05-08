package net.minecraft.client.resource.metadata;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class AnimationFrameResourceMetadata
{
    private final int index;
    private final int time;
    
    public AnimationFrameResourceMetadata(final int integer) {
        this(integer, -1);
    }
    
    public AnimationFrameResourceMetadata(final int index, final int integer2) {
        this.index = index;
        this.time = integer2;
    }
    
    public boolean usesDefaultFrameTime() {
        return this.time == -1;
    }
    
    public int getTime() {
        return this.time;
    }
    
    public int getIndex() {
        return this.index;
    }
}

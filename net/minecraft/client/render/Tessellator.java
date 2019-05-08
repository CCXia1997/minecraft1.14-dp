package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class Tessellator
{
    private final BufferBuilder buffer;
    private final BufferRenderer renderer;
    private static final Tessellator INSTANCE;
    
    public static Tessellator getInstance() {
        return Tessellator.INSTANCE;
    }
    
    public Tessellator(final int bufferCapacity) {
        this.renderer = new BufferRenderer();
        this.buffer = new BufferBuilder(bufferCapacity);
    }
    
    public void draw() {
        this.buffer.end();
        this.renderer.draw(this.buffer);
    }
    
    public BufferBuilder getBufferBuilder() {
        return this.buffer;
    }
    
    static {
        INSTANCE = new Tessellator(2097152);
    }
}

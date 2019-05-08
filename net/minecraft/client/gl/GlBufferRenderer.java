package net.minecraft.client.gl;

import net.minecraft.client.render.BufferBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferRenderer;

@Environment(EnvType.CLIENT)
public class GlBufferRenderer extends BufferRenderer
{
    private GlBuffer glBuffer;
    
    @Override
    public void draw(final BufferBuilder bufferBuilder) {
        bufferBuilder.clear();
        this.glBuffer.set(bufferBuilder.getByteBuffer());
    }
    
    public void setGlBuffer(final GlBuffer glBuffer) {
        this.glBuffer = glBuffer;
    }
}

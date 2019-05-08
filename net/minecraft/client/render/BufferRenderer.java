package net.minecraft.client.render;

import java.util.List;
import java.nio.ByteBuffer;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class BufferRenderer
{
    public void draw(final BufferBuilder bufferBuilder) {
        if (bufferBuilder.getVertexCount() > 0) {
            final VertexFormat vertexFormat2 = bufferBuilder.getVertexFormat();
            final int integer3 = vertexFormat2.getVertexSize();
            final ByteBuffer byteBuffer4 = bufferBuilder.getByteBuffer();
            final List<VertexFormatElement> list5 = vertexFormat2.getElements();
            for (int integer4 = 0; integer4 < list5.size(); ++integer4) {
                final VertexFormatElement vertexFormatElement7 = list5.get(integer4);
                final VertexFormatElement.Type type8 = vertexFormatElement7.getType();
                final int integer5 = vertexFormatElement7.getFormat().getGlId();
                final int integer6 = vertexFormatElement7.getIndex();
                byteBuffer4.position(vertexFormat2.getElementOffset(integer4));
                switch (type8) {
                    case POSITION: {
                        GlStateManager.vertexPointer(vertexFormatElement7.getCount(), integer5, integer3, byteBuffer4);
                        GlStateManager.enableClientState(32884);
                        break;
                    }
                    case UV: {
                        GLX.glClientActiveTexture(GLX.GL_TEXTURE0 + integer6);
                        GlStateManager.texCoordPointer(vertexFormatElement7.getCount(), integer5, integer3, byteBuffer4);
                        GlStateManager.enableClientState(32888);
                        GLX.glClientActiveTexture(GLX.GL_TEXTURE0);
                        break;
                    }
                    case COLOR: {
                        GlStateManager.colorPointer(vertexFormatElement7.getCount(), integer5, integer3, byteBuffer4);
                        GlStateManager.enableClientState(32886);
                        break;
                    }
                    case NORMAL: {
                        GlStateManager.normalPointer(integer5, integer3, byteBuffer4);
                        GlStateManager.enableClientState(32885);
                        break;
                    }
                }
            }
            GlStateManager.drawArrays(bufferBuilder.getDrawMode(), 0, bufferBuilder.getVertexCount());
            for (int integer4 = 0, integer7 = list5.size(); integer4 < integer7; ++integer4) {
                final VertexFormatElement vertexFormatElement8 = list5.get(integer4);
                final VertexFormatElement.Type type9 = vertexFormatElement8.getType();
                final int integer6 = vertexFormatElement8.getIndex();
                switch (type9) {
                    case POSITION: {
                        GlStateManager.disableClientState(32884);
                        break;
                    }
                    case UV: {
                        GLX.glClientActiveTexture(GLX.GL_TEXTURE0 + integer6);
                        GlStateManager.disableClientState(32888);
                        GLX.glClientActiveTexture(GLX.GL_TEXTURE0);
                        break;
                    }
                    case COLOR: {
                        GlStateManager.disableClientState(32886);
                        GlStateManager.clearCurrentColor();
                        break;
                    }
                    case NORMAL: {
                        GlStateManager.disableClientState(32885);
                        break;
                    }
                }
            }
        }
        bufferBuilder.clear();
    }
}

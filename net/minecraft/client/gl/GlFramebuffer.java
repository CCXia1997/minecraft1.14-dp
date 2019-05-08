package net.minecraft.client.gl;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.Tessellator;
import java.nio.IntBuffer;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.GLX;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class GlFramebuffer
{
    public int texWidth;
    public int texHeight;
    public int viewWidth;
    public int viewHeight;
    public final boolean useDepthAttachment;
    public int fbo;
    public int colorAttachment;
    public int depthAttachment;
    public final float[] clearColor;
    public int texFilter;
    
    public GlFramebuffer(final int width, final int height, final boolean useDepth, final boolean getError) {
        this.useDepthAttachment = useDepth;
        this.fbo = -1;
        this.colorAttachment = -1;
        this.depthAttachment = -1;
        (this.clearColor = new float[4])[0] = 1.0f;
        this.clearColor[1] = 1.0f;
        this.clearColor[2] = 1.0f;
        this.clearColor[3] = 0.0f;
        this.resize(width, height, getError);
    }
    
    public void resize(final int width, final int height, final boolean getError) {
        if (!GLX.isUsingFBOs()) {
            this.viewWidth = width;
            this.viewHeight = height;
            return;
        }
        GlStateManager.enableDepthTest();
        if (this.fbo >= 0) {
            this.delete();
        }
        this.initFbo(width, height, getError);
        GLX.glBindFramebuffer(GLX.GL_FRAMEBUFFER, 0);
    }
    
    public void delete() {
        if (!GLX.isUsingFBOs()) {
            return;
        }
        this.endRead();
        this.endWrite();
        if (this.depthAttachment > -1) {
            GLX.glDeleteRenderbuffers(this.depthAttachment);
            this.depthAttachment = -1;
        }
        if (this.colorAttachment > -1) {
            TextureUtil.releaseTextureId(this.colorAttachment);
            this.colorAttachment = -1;
        }
        if (this.fbo > -1) {
            GLX.glBindFramebuffer(GLX.GL_FRAMEBUFFER, 0);
            GLX.glDeleteFramebuffers(this.fbo);
            this.fbo = -1;
        }
    }
    
    public void initFbo(final int width, final int height, final boolean getError) {
        this.viewWidth = width;
        this.viewHeight = height;
        this.texWidth = width;
        this.texHeight = height;
        if (!GLX.isUsingFBOs()) {
            this.clear(getError);
            return;
        }
        this.fbo = GLX.glGenFramebuffers();
        this.colorAttachment = TextureUtil.generateTextureId();
        if (this.useDepthAttachment) {
            this.depthAttachment = GLX.glGenRenderbuffers();
        }
        this.setTexFilter(9728);
        GlStateManager.bindTexture(this.colorAttachment);
        GlStateManager.texImage2D(3553, 0, 32856, this.texWidth, this.texHeight, 0, 6408, 5121, null);
        GLX.glBindFramebuffer(GLX.GL_FRAMEBUFFER, this.fbo);
        GLX.glFramebufferTexture2D(GLX.GL_FRAMEBUFFER, GLX.GL_COLOR_ATTACHMENT0, 3553, this.colorAttachment, 0);
        if (this.useDepthAttachment) {
            GLX.glBindRenderbuffer(GLX.GL_RENDERBUFFER, this.depthAttachment);
            GLX.glRenderbufferStorage(GLX.GL_RENDERBUFFER, 33190, this.texWidth, this.texHeight);
            GLX.glFramebufferRenderbuffer(GLX.GL_FRAMEBUFFER, GLX.GL_DEPTH_ATTACHMENT, GLX.GL_RENDERBUFFER, this.depthAttachment);
        }
        this.checkFramebufferStatus();
        this.clear(getError);
        this.endRead();
    }
    
    public void setTexFilter(final int integer) {
        if (GLX.isUsingFBOs()) {
            this.texFilter = integer;
            GlStateManager.bindTexture(this.colorAttachment);
            GlStateManager.texParameter(3553, 10241, integer);
            GlStateManager.texParameter(3553, 10240, integer);
            GlStateManager.texParameter(3553, 10242, 10496);
            GlStateManager.texParameter(3553, 10243, 10496);
            GlStateManager.bindTexture(0);
        }
    }
    
    public void checkFramebufferStatus() {
        final int integer1 = GLX.glCheckFramebufferStatus(GLX.GL_FRAMEBUFFER);
        if (integer1 == GLX.GL_FRAMEBUFFER_COMPLETE) {
            return;
        }
        if (integer1 == GLX.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT) {
            throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT");
        }
        if (integer1 == GLX.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT) {
            throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT");
        }
        if (integer1 == GLX.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER) {
            throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER");
        }
        if (integer1 == GLX.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER) {
            throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER");
        }
        throw new RuntimeException("glCheckFramebufferStatus returned unknown status:" + integer1);
    }
    
    public void beginRead() {
        if (GLX.isUsingFBOs()) {
            GlStateManager.bindTexture(this.colorAttachment);
        }
    }
    
    public void endRead() {
        if (GLX.isUsingFBOs()) {
            GlStateManager.bindTexture(0);
        }
    }
    
    public void beginWrite(final boolean setViewport) {
        if (GLX.isUsingFBOs()) {
            GLX.glBindFramebuffer(GLX.GL_FRAMEBUFFER, this.fbo);
            if (setViewport) {
                GlStateManager.viewport(0, 0, this.viewWidth, this.viewHeight);
            }
        }
    }
    
    public void endWrite() {
        if (GLX.isUsingFBOs()) {
            GLX.glBindFramebuffer(GLX.GL_FRAMEBUFFER, 0);
        }
    }
    
    public void setClearColor(final float r, final float g, final float b, final float a) {
        this.clearColor[0] = r;
        this.clearColor[1] = g;
        this.clearColor[2] = b;
        this.clearColor[3] = a;
    }
    
    public void draw(final int width, final int height) {
        this.draw(width, height, true);
    }
    
    public void draw(final int width, final int height, final boolean boolean3) {
        if (!GLX.isUsingFBOs()) {
            return;
        }
        GlStateManager.colorMask(true, true, true, false);
        GlStateManager.disableDepthTest();
        GlStateManager.depthMask(false);
        GlStateManager.matrixMode(5889);
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0, width, height, 0.0, 1000.0, 3000.0);
        GlStateManager.matrixMode(5888);
        GlStateManager.loadIdentity();
        GlStateManager.translatef(0.0f, 0.0f, -2000.0f);
        GlStateManager.viewport(0, 0, width, height);
        GlStateManager.enableTexture();
        GlStateManager.disableLighting();
        GlStateManager.disableAlphaTest();
        if (boolean3) {
            GlStateManager.disableBlend();
            GlStateManager.enableColorMaterial();
        }
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.beginRead();
        final float float4 = (float)width;
        final float float5 = (float)height;
        final float float6 = this.viewWidth / (float)this.texWidth;
        final float float7 = this.viewHeight / (float)this.texHeight;
        final Tessellator tessellator8 = Tessellator.getInstance();
        final BufferBuilder bufferBuilder9 = tessellator8.getBufferBuilder();
        bufferBuilder9.begin(7, VertexFormats.POSITION_UV_COLOR);
        bufferBuilder9.vertex(0.0, float5, 0.0).texture(0.0, 0.0).color(255, 255, 255, 255).next();
        bufferBuilder9.vertex(float4, float5, 0.0).texture(float6, 0.0).color(255, 255, 255, 255).next();
        bufferBuilder9.vertex(float4, 0.0, 0.0).texture(float6, float7).color(255, 255, 255, 255).next();
        bufferBuilder9.vertex(0.0, 0.0, 0.0).texture(0.0, float7).color(255, 255, 255, 255).next();
        tessellator8.draw();
        this.endRead();
        GlStateManager.depthMask(true);
        GlStateManager.colorMask(true, true, true, true);
    }
    
    public void clear(final boolean getError) {
        this.beginWrite(true);
        GlStateManager.clearColor(this.clearColor[0], this.clearColor[1], this.clearColor[2], this.clearColor[3]);
        int integer2 = 16384;
        if (this.useDepthAttachment) {
            GlStateManager.clearDepth(1.0);
            integer2 |= 0x100;
        }
        GlStateManager.clear(integer2, getError);
        this.endWrite();
    }
}

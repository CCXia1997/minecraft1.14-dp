package com.mojang.blaze3d.platform;

import org.lwjgl.opengl.GL;
import net.minecraft.client.render.GuiLighting;
import java.util.stream.IntStream;
import net.minecraft.client.util.UntrackMemoryUtil;
import org.lwjgl.system.MemoryUtil;
import java.nio.ByteBuffer;
import net.minecraft.client.util.math.Matrix4f;
import javax.annotation.Nullable;
import java.nio.IntBuffer;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL11;
import java.nio.FloatBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class GlStateManager
{
    private static final int LIGHT_COUNT = 8;
    private static final int TEXTURE_COUNT = 8;
    private static final FloatBuffer MATRIX_BUFFER;
    private static final FloatBuffer COLOR_BUFFER;
    private static final AlphaTestState ALPHA_TEST;
    private static final CapabilityTracker LIGHTING;
    private static final CapabilityTracker[] LIGHT_ENABLE;
    private static final ColorMaterialState COLOR_MATERIAL;
    private static final BlendFuncState BLEND;
    private static final DepthTestState DEPTH;
    private static final FogState FOG;
    private static final CullFaceState CULL;
    private static final PolygonOffsetState POLY_OFFSET;
    private static final LogicOpState COLOR_LOGIC;
    private static final TexGenState TEX_GEN;
    private static final ClearState CLEAR;
    private static final StencilState STENCIL;
    private static final CapabilityTracker NORMALIZE;
    private static int activeTexture;
    private static final Texture2DState[] TEXTURES;
    private static int shadeModel;
    private static final CapabilityTracker RESCALE_NORMAL;
    private static final ColorMask COLOR_MASK;
    private static final Color4 COLOR;
    private static final float DEFAULTALPHACUTOFF = 0.1f;
    
    public static void pushLightingAttributes() {
        GL11.glPushAttrib(8256);
    }
    
    public static void pushTextureAttributes() {
        GL11.glPushAttrib(270336);
    }
    
    public static void popAttributes() {
        GL11.glPopAttrib();
    }
    
    public static void disableAlphaTest() {
        GlStateManager.ALPHA_TEST.capState.disable();
    }
    
    public static void enableAlphaTest() {
        GlStateManager.ALPHA_TEST.capState.enable();
    }
    
    public static void alphaFunc(final int func, final float ref) {
        if (func != GlStateManager.ALPHA_TEST.func || ref != GlStateManager.ALPHA_TEST.ref) {
            GL11.glAlphaFunc(GlStateManager.ALPHA_TEST.func = func, GlStateManager.ALPHA_TEST.ref = ref);
        }
    }
    
    public static void enableLighting() {
        GlStateManager.LIGHTING.enable();
    }
    
    public static void disableLighting() {
        GlStateManager.LIGHTING.disable();
    }
    
    public static void enableLight(final int integer) {
        GlStateManager.LIGHT_ENABLE[integer].enable();
    }
    
    public static void disableLight(final int integer) {
        GlStateManager.LIGHT_ENABLE[integer].disable();
    }
    
    public static void enableColorMaterial() {
        GlStateManager.COLOR_MATERIAL.capState.enable();
    }
    
    public static void disableColorMaterial() {
        GlStateManager.COLOR_MATERIAL.capState.disable();
    }
    
    public static void colorMaterial(final int face, final int mode) {
        if (face != GlStateManager.COLOR_MATERIAL.face || mode != GlStateManager.COLOR_MATERIAL.mode) {
            GL11.glColorMaterial(GlStateManager.COLOR_MATERIAL.face = face, GlStateManager.COLOR_MATERIAL.mode = mode);
        }
    }
    
    public static void light(final int light, final int pname, final FloatBuffer params) {
        GL11.glLightfv(light, pname, params);
    }
    
    public static void lightModel(final int pname, final FloatBuffer params) {
        GL11.glLightModelfv(pname, params);
    }
    
    public static void normal3f(final float nx, final float ny, final float nz) {
        GL11.glNormal3f(nx, ny, nz);
    }
    
    public static void disableDepthTest() {
        GlStateManager.DEPTH.capState.disable();
    }
    
    public static void enableDepthTest() {
        GlStateManager.DEPTH.capState.enable();
    }
    
    public static void depthFunc(final int func) {
        if (func != GlStateManager.DEPTH.func) {
            GL11.glDepthFunc(GlStateManager.DEPTH.func = func);
        }
    }
    
    public static void depthMask(final boolean mask) {
        if (mask != GlStateManager.DEPTH.mask) {
            GL11.glDepthMask(GlStateManager.DEPTH.mask = mask);
        }
    }
    
    public static void disableBlend() {
        GlStateManager.BLEND.capState.disable();
    }
    
    public static void enableBlend() {
        GlStateManager.BLEND.capState.enable();
    }
    
    public static void blendFunc(final SourceFactor sourceFactor, final DestFactor destFactor) {
        blendFunc(sourceFactor.value, destFactor.value);
    }
    
    public static void blendFunc(final int sfactor, final int dfactor) {
        if (sfactor != GlStateManager.BLEND.sfactor || dfactor != GlStateManager.BLEND.dfactor) {
            GL11.glBlendFunc(GlStateManager.BLEND.sfactor = sfactor, GlStateManager.BLEND.dfactor = dfactor);
        }
    }
    
    public static void blendFuncSeparate(final SourceFactor sourceFactor1, final DestFactor destFactor2, final SourceFactor sourceFactor3, final DestFactor destFactor4) {
        blendFuncSeparate(sourceFactor1.value, destFactor2.value, sourceFactor3.value, destFactor4.value);
    }
    
    public static void blendFuncSeparate(final int sFactorRGB, final int dFactorRGB, final int sFactorAlpha, final int dFactorAlpha) {
        if (sFactorRGB != GlStateManager.BLEND.sfactor || dFactorRGB != GlStateManager.BLEND.dfactor || sFactorAlpha != GlStateManager.BLEND.srcAlpha || dFactorAlpha != GlStateManager.BLEND.dstAlpha) {
            GLX.glBlendFuncSeparate(GlStateManager.BLEND.sfactor = sFactorRGB, GlStateManager.BLEND.dfactor = dFactorRGB, GlStateManager.BLEND.srcAlpha = sFactorAlpha, GlStateManager.BLEND.dstAlpha = dFactorAlpha);
        }
    }
    
    public static void blendEquation(final int mode) {
        GL14.glBlendEquation(mode);
    }
    
    public static void setupSolidRenderingTextureCombine(final int color) {
        GlStateManager.COLOR_BUFFER.put(0, (color >> 16 & 0xFF) / 255.0f);
        GlStateManager.COLOR_BUFFER.put(1, (color >> 8 & 0xFF) / 255.0f);
        GlStateManager.COLOR_BUFFER.put(2, (color >> 0 & 0xFF) / 255.0f);
        GlStateManager.COLOR_BUFFER.put(3, (color >> 24 & 0xFF) / 255.0f);
        texEnv(8960, 8705, GlStateManager.COLOR_BUFFER);
        texEnv(8960, 8704, 34160);
        texEnv(8960, 34161, 7681);
        texEnv(8960, 34176, 34166);
        texEnv(8960, 34192, 768);
        texEnv(8960, 34162, 7681);
        texEnv(8960, 34184, 5890);
        texEnv(8960, 34200, 770);
    }
    
    public static void tearDownSolidRenderingTextureCombine() {
        texEnv(8960, 8704, 8448);
        texEnv(8960, 34161, 8448);
        texEnv(8960, 34162, 8448);
        texEnv(8960, 34176, 5890);
        texEnv(8960, 34184, 5890);
        texEnv(8960, 34192, 768);
        texEnv(8960, 34200, 770);
    }
    
    public static void enableFog() {
        GlStateManager.FOG.capState.enable();
    }
    
    public static void disableFog() {
        GlStateManager.FOG.capState.disable();
    }
    
    public static void fogMode(final FogMode fogMode) {
        fogMode(fogMode.glValue);
    }
    
    private static void fogMode(final int integer) {
        if (integer != GlStateManager.FOG.mode) {
            GL11.glFogi(2917, GlStateManager.FOG.mode = integer);
        }
    }
    
    public static void fogDensity(final float float1) {
        if (float1 != GlStateManager.FOG.density) {
            GL11.glFogf(2914, GlStateManager.FOG.density = float1);
        }
    }
    
    public static void fogStart(final float float1) {
        if (float1 != GlStateManager.FOG.start) {
            GL11.glFogf(2915, GlStateManager.FOG.start = float1);
        }
    }
    
    public static void fogEnd(final float float1) {
        if (float1 != GlStateManager.FOG.end) {
            GL11.glFogf(2916, GlStateManager.FOG.end = float1);
        }
    }
    
    public static void fog(final int integer, final FloatBuffer floatBuffer) {
        GL11.glFogfv(integer, floatBuffer);
    }
    
    public static void fogi(final int integer1, final int integer2) {
        GL11.glFogi(integer1, integer2);
    }
    
    public static void enableCull() {
        GlStateManager.CULL.capState.enable();
    }
    
    public static void disableCull() {
        GlStateManager.CULL.capState.disable();
    }
    
    public static void cullFace(final FaceSides faceSides) {
        cullFace(faceSides.glValue);
    }
    
    private static void cullFace(final int integer) {
        if (integer != GlStateManager.CULL.mode) {
            GL11.glCullFace(GlStateManager.CULL.mode = integer);
        }
    }
    
    public static void polygonMode(final int integer1, final int integer2) {
        GL11.glPolygonMode(integer1, integer2);
    }
    
    public static void enablePolygonOffset() {
        GlStateManager.POLY_OFFSET.capFill.enable();
    }
    
    public static void disablePolygonOffset() {
        GlStateManager.POLY_OFFSET.capFill.disable();
    }
    
    public static void enableLineOffset() {
        GlStateManager.POLY_OFFSET.capLine.enable();
    }
    
    public static void disableLineOffset() {
        GlStateManager.POLY_OFFSET.capLine.disable();
    }
    
    public static void polygonOffset(final float factor, final float units) {
        if (factor != GlStateManager.POLY_OFFSET.factor || units != GlStateManager.POLY_OFFSET.units) {
            GL11.glPolygonOffset(GlStateManager.POLY_OFFSET.factor = factor, GlStateManager.POLY_OFFSET.units = units);
        }
    }
    
    public static void enableColorLogicOp() {
        GlStateManager.COLOR_LOGIC.capState.enable();
    }
    
    public static void disableColorLogicOp() {
        GlStateManager.COLOR_LOGIC.capState.disable();
    }
    
    public static void logicOp(final LogicOp logicOp) {
        logicOp(logicOp.glValue);
    }
    
    public static void logicOp(final int integer) {
        if (integer != GlStateManager.COLOR_LOGIC.opcode) {
            GL11.glLogicOp(GlStateManager.COLOR_LOGIC.opcode = integer);
        }
    }
    
    public static void enableTexGen(final TexCoord texCoord) {
        getTexGen(texCoord).capState.enable();
    }
    
    public static void disableTexGen(final TexCoord texCoord) {
        getTexGen(texCoord).capState.disable();
    }
    
    public static void texGenMode(final TexCoord texCoord, final int integer) {
        final TexGenCoordState texGenCoordState3 = getTexGen(texCoord);
        if (integer != texGenCoordState3.mode) {
            texGenCoordState3.mode = integer;
            GL11.glTexGeni(texGenCoordState3.coord, 9472, integer);
        }
    }
    
    public static void texGenParam(final TexCoord texCoord, final int integer, final FloatBuffer floatBuffer) {
        GL11.glTexGenfv(getTexGen(texCoord).coord, integer, floatBuffer);
    }
    
    private static TexGenCoordState getTexGen(final TexCoord texCoord) {
        switch (texCoord) {
            case a: {
                return GlStateManager.TEX_GEN.s;
            }
            case b: {
                return GlStateManager.TEX_GEN.t;
            }
            case c: {
                return GlStateManager.TEX_GEN.r;
            }
            case d: {
                return GlStateManager.TEX_GEN.q;
            }
            default: {
                return GlStateManager.TEX_GEN.s;
            }
        }
    }
    
    public static void activeTexture(final int integer) {
        if (GlStateManager.activeTexture != integer - GLX.GL_TEXTURE0) {
            GlStateManager.activeTexture = integer - GLX.GL_TEXTURE0;
            GLX.glActiveTexture(integer);
        }
    }
    
    public static void enableTexture() {
        GlStateManager.TEXTURES[GlStateManager.activeTexture].capState.enable();
    }
    
    public static void disableTexture() {
        GlStateManager.TEXTURES[GlStateManager.activeTexture].capState.disable();
    }
    
    public static void texEnv(final int integer1, final int integer2, final FloatBuffer floatBuffer) {
        GL11.glTexEnvfv(integer1, integer2, floatBuffer);
    }
    
    public static void texEnv(final int integer1, final int integer2, final int integer3) {
        GL11.glTexEnvi(integer1, integer2, integer3);
    }
    
    public static void texEnv(final int integer1, final int integer2, final float float3) {
        GL11.glTexEnvf(integer1, integer2, float3);
    }
    
    public static void texParameter(final int integer1, final int integer2, final float float3) {
        GL11.glTexParameterf(integer1, integer2, float3);
    }
    
    public static void texParameter(final int integer1, final int integer2, final int integer3) {
        GL11.glTexParameteri(integer1, integer2, integer3);
    }
    
    public static int getTexLevelParameter(final int integer1, final int integer2, final int integer3) {
        return GL11.glGetTexLevelParameteri(integer1, integer2, integer3);
    }
    
    public static int genTexture() {
        return GL11.glGenTextures();
    }
    
    public static void deleteTexture(final int integer) {
        GL11.glDeleteTextures(integer);
        for (final Texture2DState texture2DState5 : GlStateManager.TEXTURES) {
            if (texture2DState5.boundTexture == integer) {
                texture2DState5.boundTexture = -1;
            }
        }
    }
    
    public static void bindTexture(final int texture) {
        if (texture != GlStateManager.TEXTURES[GlStateManager.activeTexture].boundTexture) {
            GL11.glBindTexture(3553, GlStateManager.TEXTURES[GlStateManager.activeTexture].boundTexture = texture);
        }
    }
    
    public static void texImage2D(final int target, final int level, final int internalFormat, final int width, final int height, final int border, final int format, final int type, @Nullable final IntBuffer pixels) {
        GL11.glTexImage2D(target, level, internalFormat, width, height, border, format, type, pixels);
    }
    
    public static void texSubImage2D(final int target, final int level, final int xOffset, final int yOffset, final int width, final int height, final int format, final int type, final long pixels) {
        GL11.glTexSubImage2D(target, level, xOffset, yOffset, width, height, format, type, pixels);
    }
    
    public static void copyTexSubImage2D(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6, final int integer7, final int integer8) {
        GL11.glCopyTexSubImage2D(integer1, integer2, integer3, integer4, integer5, integer6, integer7, integer8);
    }
    
    public static void getTexImage(final int integer1, final int integer2, final int integer3, final int integer4, final long long5) {
        GL11.glGetTexImage(integer1, integer2, integer3, integer4, long5);
    }
    
    public static void enableNormalize() {
        GlStateManager.NORMALIZE.enable();
    }
    
    public static void disableNormalize() {
        GlStateManager.NORMALIZE.disable();
    }
    
    public static void shadeModel(final int integer) {
        if (integer != GlStateManager.shadeModel) {
            GL11.glShadeModel(GlStateManager.shadeModel = integer);
        }
    }
    
    public static void enableRescaleNormal() {
        GlStateManager.RESCALE_NORMAL.enable();
    }
    
    public static void disableRescaleNormal() {
        GlStateManager.RESCALE_NORMAL.disable();
    }
    
    public static void viewport(final int integer1, final int integer2, final int integer3, final int integer4) {
        GL11.glViewport(Viewport.INSTANCE.x = integer1, Viewport.INSTANCE.y = integer2, Viewport.INSTANCE.width = integer3, Viewport.INSTANCE.height = integer4);
    }
    
    public static void colorMask(final boolean boolean1, final boolean boolean2, final boolean boolean3, final boolean boolean4) {
        if (boolean1 != GlStateManager.COLOR_MASK.red || boolean2 != GlStateManager.COLOR_MASK.green || boolean3 != GlStateManager.COLOR_MASK.blue || boolean4 != GlStateManager.COLOR_MASK.alpha) {
            GL11.glColorMask(GlStateManager.COLOR_MASK.red = boolean1, GlStateManager.COLOR_MASK.green = boolean2, GlStateManager.COLOR_MASK.blue = boolean3, GlStateManager.COLOR_MASK.alpha = boolean4);
        }
    }
    
    public static void stencilFunc(final int integer1, final int integer2, final int integer3) {
        if (integer1 != GlStateManager.STENCIL.subState.func || integer1 != GlStateManager.STENCIL.subState.b || integer1 != GlStateManager.STENCIL.subState.c) {
            GL11.glStencilFunc(GlStateManager.STENCIL.subState.func = integer1, GlStateManager.STENCIL.subState.b = integer2, GlStateManager.STENCIL.subState.c = integer3);
        }
    }
    
    public static void stencilMask(final int integer) {
        if (integer != GlStateManager.STENCIL.b) {
            GL11.glStencilMask(GlStateManager.STENCIL.b = integer);
        }
    }
    
    public static void stencilOp(final int integer1, final int integer2, final int integer3) {
        if (integer1 != GlStateManager.STENCIL.c || integer2 != GlStateManager.STENCIL.d || integer3 != GlStateManager.STENCIL.e) {
            GL11.glStencilOp(GlStateManager.STENCIL.c = integer1, GlStateManager.STENCIL.d = integer2, GlStateManager.STENCIL.e = integer3);
        }
    }
    
    public static void clearDepth(final double double1) {
        if (double1 != GlStateManager.CLEAR.clearDepth) {
            GL11.glClearDepth(GlStateManager.CLEAR.clearDepth = double1);
        }
    }
    
    public static void clearColor(final float float1, final float float2, final float float3, final float float4) {
        if (float1 != GlStateManager.CLEAR.clearColor.red || float2 != GlStateManager.CLEAR.clearColor.green || float3 != GlStateManager.CLEAR.clearColor.blue || float4 != GlStateManager.CLEAR.clearColor.alpha) {
            GL11.glClearColor(GlStateManager.CLEAR.clearColor.red = float1, GlStateManager.CLEAR.clearColor.green = float2, GlStateManager.CLEAR.clearColor.blue = float3, GlStateManager.CLEAR.clearColor.alpha = float4);
        }
    }
    
    public static void clearStencil(final int integer) {
        if (integer != GlStateManager.CLEAR.c) {
            GL11.glClearStencil(GlStateManager.CLEAR.c = integer);
        }
    }
    
    public static void clear(final int integer, final boolean boolean2) {
        GL11.glClear(integer);
        if (boolean2) {
            getError();
        }
    }
    
    public static void matrixMode(final int integer) {
        GL11.glMatrixMode(integer);
    }
    
    public static void loadIdentity() {
        GL11.glLoadIdentity();
    }
    
    public static void pushMatrix() {
        GL11.glPushMatrix();
    }
    
    public static void popMatrix() {
        GL11.glPopMatrix();
    }
    
    public static void getMatrix(final int integer, final FloatBuffer floatBuffer) {
        GL11.glGetFloatv(integer, floatBuffer);
    }
    
    public static Matrix4f getMatrix4f(final int integer) {
        GL11.glGetFloatv(integer, GlStateManager.MATRIX_BUFFER);
        GlStateManager.MATRIX_BUFFER.rewind();
        final Matrix4f matrix4f2 = new Matrix4f();
        matrix4f2.setFromBuffer(GlStateManager.MATRIX_BUFFER);
        GlStateManager.MATRIX_BUFFER.rewind();
        return matrix4f2;
    }
    
    public static void ortho(final double double1, final double double3, final double double5, final double double7, final double double9, final double double11) {
        GL11.glOrtho(double1, double3, double5, double7, double9, double11);
    }
    
    public static void rotatef(final float angle, final float x, final float y, final float z) {
        GL11.glRotatef(angle, x, y, z);
    }
    
    public static void rotated(final double double1, final double double3, final double double5, final double double7) {
        GL11.glRotated(double1, double3, double5, double7);
    }
    
    public static void scalef(final float float1, final float float2, final float float3) {
        GL11.glScalef(float1, float2, float3);
    }
    
    public static void scaled(final double double1, final double double3, final double double5) {
        GL11.glScaled(double1, double3, double5);
    }
    
    public static void translatef(final float float1, final float float2, final float float3) {
        GL11.glTranslatef(float1, float2, float3);
    }
    
    public static void translated(final double double1, final double double3, final double double5) {
        GL11.glTranslated(double1, double3, double5);
    }
    
    public static void multMatrix(final FloatBuffer floatBuffer) {
        GL11.glMultMatrixf(floatBuffer);
    }
    
    public static void multMatrix(final Matrix4f matrix4f) {
        matrix4f.putIntoBuffer(GlStateManager.MATRIX_BUFFER);
        GlStateManager.MATRIX_BUFFER.rewind();
        GL11.glMultMatrixf(GlStateManager.MATRIX_BUFFER);
    }
    
    public static void color4f(final float red, final float green, final float blue, final float alpha) {
        if (red != GlStateManager.COLOR.red || green != GlStateManager.COLOR.green || blue != GlStateManager.COLOR.blue || alpha != GlStateManager.COLOR.alpha) {
            GL11.glColor4f(GlStateManager.COLOR.red = red, GlStateManager.COLOR.green = green, GlStateManager.COLOR.blue = blue, GlStateManager.COLOR.alpha = alpha);
        }
    }
    
    public static void color3f(final float red, final float green, final float blue) {
        color4f(red, green, blue, 1.0f);
    }
    
    public static void texCoord2f(final float float1, final float float2) {
        GL11.glTexCoord2f(float1, float2);
    }
    
    public static void vertex3f(final float float1, final float float2, final float float3) {
        GL11.glVertex3f(float1, float2, float3);
    }
    
    public static void clearCurrentColor() {
        GlStateManager.COLOR.red = -1.0f;
        GlStateManager.COLOR.green = -1.0f;
        GlStateManager.COLOR.blue = -1.0f;
        GlStateManager.COLOR.alpha = -1.0f;
    }
    
    public static void normalPointer(final int integer1, final int integer2, final int integer3) {
        GL11.glNormalPointer(integer1, integer2, (long)integer3);
    }
    
    public static void normalPointer(final int integer1, final int integer2, final ByteBuffer byteBuffer) {
        GL11.glNormalPointer(integer1, integer2, byteBuffer);
    }
    
    public static void texCoordPointer(final int integer1, final int integer2, final int integer3, final int integer4) {
        GL11.glTexCoordPointer(integer1, integer2, integer3, (long)integer4);
    }
    
    public static void texCoordPointer(final int integer1, final int integer2, final int integer3, final ByteBuffer byteBuffer) {
        GL11.glTexCoordPointer(integer1, integer2, integer3, byteBuffer);
    }
    
    public static void vertexPointer(final int integer1, final int integer2, final int integer3, final int integer4) {
        GL11.glVertexPointer(integer1, integer2, integer3, (long)integer4);
    }
    
    public static void vertexPointer(final int integer1, final int integer2, final int integer3, final ByteBuffer byteBuffer) {
        GL11.glVertexPointer(integer1, integer2, integer3, byteBuffer);
    }
    
    public static void colorPointer(final int integer1, final int integer2, final int integer3, final int integer4) {
        GL11.glColorPointer(integer1, integer2, integer3, (long)integer4);
    }
    
    public static void colorPointer(final int integer1, final int integer2, final int integer3, final ByteBuffer byteBuffer) {
        GL11.glColorPointer(integer1, integer2, integer3, byteBuffer);
    }
    
    public static void disableClientState(final int integer) {
        GL11.glDisableClientState(integer);
    }
    
    public static void enableClientState(final int integer) {
        GL11.glEnableClientState(integer);
    }
    
    public static void begin(final int integer) {
        GL11.glBegin(integer);
    }
    
    public static void end() {
        GL11.glEnd();
    }
    
    public static void drawArrays(final int mode, final int first, final int count) {
        GL11.glDrawArrays(mode, first, count);
    }
    
    public static void lineWidth(final float float1) {
        GL11.glLineWidth(float1);
    }
    
    public static void callList(final int integer) {
        GL11.glCallList(integer);
    }
    
    public static void deleteLists(final int integer1, final int integer2) {
        GL11.glDeleteLists(integer1, integer2);
    }
    
    public static void newList(final int integer1, final int integer2) {
        GL11.glNewList(integer1, integer2);
    }
    
    public static void endList() {
        GL11.glEndList();
    }
    
    public static int genLists(final int integer) {
        return GL11.glGenLists(integer);
    }
    
    public static void pixelStore(final int pname, final int param) {
        GL11.glPixelStorei(pname, param);
    }
    
    public static void pixelTransfer(final int integer, final float float2) {
        GL11.glPixelTransferf(integer, float2);
    }
    
    public static void readPixels(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6, final ByteBuffer byteBuffer) {
        GL11.glReadPixels(integer1, integer2, integer3, integer4, integer5, integer6, byteBuffer);
    }
    
    public static void readPixels(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6, final long long7) {
        GL11.glReadPixels(integer1, integer2, integer3, integer4, integer5, integer6, long7);
    }
    
    public static int getError() {
        return GL11.glGetError();
    }
    
    public static String getString(final int integer) {
        return GL11.glGetString(integer);
    }
    
    public static void getInteger(final int integer, final IntBuffer intBuffer) {
        GL11.glGetIntegerv(integer, intBuffer);
    }
    
    public static int getInteger(final int integer) {
        return GL11.glGetInteger(integer);
    }
    
    public static void setProfile(final RenderMode renderMode) {
        renderMode.begin();
    }
    
    public static void unsetProfile(final RenderMode renderMode) {
        renderMode.end();
    }
    
    static {
        MATRIX_BUFFER = GLX.<FloatBuffer>make(MemoryUtil.memAllocFloat(16), floatBuffer -> UntrackMemoryUtil.untrack(MemoryUtil.memAddress(floatBuffer)));
        COLOR_BUFFER = GLX.<FloatBuffer>make(MemoryUtil.memAllocFloat(4), floatBuffer -> UntrackMemoryUtil.untrack(MemoryUtil.memAddress(floatBuffer)));
        ALPHA_TEST = new AlphaTestState();
        LIGHTING = new CapabilityTracker(2896);
        LIGHT_ENABLE = IntStream.range(0, 8).mapToObj(integer -> new CapabilityTracker(16384 + integer)).<CapabilityTracker>toArray(CapabilityTracker[]::new);
        COLOR_MATERIAL = new ColorMaterialState();
        BLEND = new BlendFuncState();
        DEPTH = new DepthTestState();
        FOG = new FogState();
        CULL = new CullFaceState();
        POLY_OFFSET = new PolygonOffsetState();
        COLOR_LOGIC = new LogicOpState();
        TEX_GEN = new TexGenState();
        CLEAR = new ClearState();
        STENCIL = new StencilState();
        NORMALIZE = new CapabilityTracker(2977);
        TEXTURES = IntStream.range(0, 8).mapToObj(integer -> new Texture2DState()).<Texture2DState>toArray(Texture2DState[]::new);
        GlStateManager.shadeModel = 7425;
        RESCALE_NORMAL = new CapabilityTracker(32826);
        COLOR_MASK = new ColorMask();
        COLOR = new Color4();
    }
    
    @Environment(EnvType.CLIENT)
    public enum FogMode
    {
        a(9729), 
        b(2048), 
        c(2049);
        
        public final int glValue;
        
        private FogMode(final int integer1) {
            this.glValue = integer1;
        }
    }
    
    @Environment(EnvType.CLIENT)
    public enum FaceSides
    {
        a(1028), 
        b(1029), 
        c(1032);
        
        public final int glValue;
        
        private FaceSides(final int integer1) {
            this.glValue = integer1;
        }
    }
    
    @Environment(EnvType.CLIENT)
    public enum LogicOp
    {
        a(5377), 
        b(5380), 
        c(5378), 
        d(5376), 
        e(5379), 
        f(5388), 
        g(5385), 
        h(5386), 
        i(5390), 
        j(5381), 
        k(5384), 
        l(5383), 
        m(5389), 
        n(5387), 
        o(5391), 
        p(5382);
        
        public final int glValue;
        
        private LogicOp(final int integer1) {
            this.glValue = integer1;
        }
    }
    
    @Environment(EnvType.CLIENT)
    public enum Viewport
    {
        INSTANCE;
        
        protected int x;
        protected int y;
        protected int width;
        protected int height;
    }
    
    @Environment(EnvType.CLIENT)
    static class Texture2DState
    {
        public final CapabilityTracker capState;
        public int boundTexture;
        
        private Texture2DState() {
            this.capState = new CapabilityTracker(3553);
        }
    }
    
    @Environment(EnvType.CLIENT)
    static class AlphaTestState
    {
        public final CapabilityTracker capState;
        public int func;
        public float ref;
        
        private AlphaTestState() {
            this.capState = new CapabilityTracker(3008);
            this.func = 519;
            this.ref = -1.0f;
        }
    }
    
    @Environment(EnvType.CLIENT)
    static class ColorMaterialState
    {
        public final CapabilityTracker capState;
        public int face;
        public int mode;
        
        private ColorMaterialState() {
            this.capState = new CapabilityTracker(2903);
            this.face = 1032;
            this.mode = 5634;
        }
    }
    
    @Environment(EnvType.CLIENT)
    static class BlendFuncState
    {
        public final CapabilityTracker capState;
        public int sfactor;
        public int dfactor;
        public int srcAlpha;
        public int dstAlpha;
        
        private BlendFuncState() {
            this.capState = new CapabilityTracker(3042);
            this.sfactor = 1;
            this.dfactor = 0;
            this.srcAlpha = 1;
            this.dstAlpha = 0;
        }
    }
    
    @Environment(EnvType.CLIENT)
    static class DepthTestState
    {
        public final CapabilityTracker capState;
        public boolean mask;
        public int func;
        
        private DepthTestState() {
            this.capState = new CapabilityTracker(2929);
            this.mask = true;
            this.func = 513;
        }
    }
    
    @Environment(EnvType.CLIENT)
    static class FogState
    {
        public final CapabilityTracker capState;
        public int mode;
        public float density;
        public float start;
        public float end;
        
        private FogState() {
            this.capState = new CapabilityTracker(2912);
            this.mode = 2048;
            this.density = 1.0f;
            this.end = 1.0f;
        }
    }
    
    @Environment(EnvType.CLIENT)
    static class CullFaceState
    {
        public final CapabilityTracker capState;
        public int mode;
        
        private CullFaceState() {
            this.capState = new CapabilityTracker(2884);
            this.mode = 1029;
        }
    }
    
    @Environment(EnvType.CLIENT)
    static class PolygonOffsetState
    {
        public final CapabilityTracker capFill;
        public final CapabilityTracker capLine;
        public float factor;
        public float units;
        
        private PolygonOffsetState() {
            this.capFill = new CapabilityTracker(32823);
            this.capLine = new CapabilityTracker(10754);
        }
    }
    
    @Environment(EnvType.CLIENT)
    static class LogicOpState
    {
        public final CapabilityTracker capState;
        public int opcode;
        
        private LogicOpState() {
            this.capState = new CapabilityTracker(3058);
            this.opcode = 5379;
        }
    }
    
    @Environment(EnvType.CLIENT)
    static class ClearState
    {
        public double clearDepth;
        public final Color4 clearColor;
        public int c;
        
        private ClearState() {
            this.clearDepth = 1.0;
            this.clearColor = new Color4(0.0f, 0.0f, 0.0f, 0.0f);
        }
    }
    
    @Environment(EnvType.CLIENT)
    static class StencilSubState
    {
        public int func;
        public int b;
        public int c;
        
        private StencilSubState() {
            this.func = 519;
            this.c = -1;
        }
    }
    
    @Environment(EnvType.CLIENT)
    static class StencilState
    {
        public final StencilSubState subState;
        public int b;
        public int c;
        public int d;
        public int e;
        
        private StencilState() {
            this.subState = new StencilSubState();
            this.b = -1;
            this.c = 7680;
            this.d = 7680;
            this.e = 7680;
        }
    }
    
    @Environment(EnvType.CLIENT)
    static class TexGenState
    {
        public final TexGenCoordState s;
        public final TexGenCoordState t;
        public final TexGenCoordState r;
        public final TexGenCoordState q;
        
        private TexGenState() {
            this.s = new TexGenCoordState(8192, 3168);
            this.t = new TexGenCoordState(8193, 3169);
            this.r = new TexGenCoordState(8194, 3170);
            this.q = new TexGenCoordState(8195, 3171);
        }
    }
    
    @Environment(EnvType.CLIENT)
    static class TexGenCoordState
    {
        public final CapabilityTracker capState;
        public final int coord;
        public int mode;
        
        public TexGenCoordState(final int integer1, final int integer2) {
            this.mode = -1;
            this.coord = integer1;
            this.capState = new CapabilityTracker(integer2);
        }
    }
    
    @Environment(EnvType.CLIENT)
    public enum TexCoord
    {
        a, 
        b, 
        c, 
        d;
    }
    
    @Environment(EnvType.CLIENT)
    static class ColorMask
    {
        public boolean red;
        public boolean green;
        public boolean blue;
        public boolean alpha;
        
        private ColorMask() {
            this.red = true;
            this.green = true;
            this.blue = true;
            this.alpha = true;
        }
    }
    
    @Environment(EnvType.CLIENT)
    static class Color4
    {
        public float red;
        public float green;
        public float blue;
        public float alpha;
        
        public Color4() {
            this(1.0f, 1.0f, 1.0f, 1.0f);
        }
        
        public Color4(final float float1, final float float2, final float float3, final float float4) {
            this.red = 1.0f;
            this.green = 1.0f;
            this.blue = 1.0f;
            this.alpha = 1.0f;
            this.red = float1;
            this.green = float2;
            this.blue = float3;
            this.alpha = float4;
        }
    }
    
    @Environment(EnvType.CLIENT)
    static class CapabilityTracker
    {
        private final int cap;
        private boolean state;
        
        public CapabilityTracker(final int integer) {
            this.cap = integer;
        }
        
        public void disable() {
            this.setState(false);
        }
        
        public void enable() {
            this.setState(true);
        }
        
        public void setState(final boolean boolean1) {
            if (boolean1 != this.state) {
                this.state = boolean1;
                if (boolean1) {
                    GL11.glEnable(this.cap);
                }
                else {
                    GL11.glDisable(this.cap);
                }
            }
        }
    }
    
    @Environment(EnvType.CLIENT)
    public enum SourceFactor
    {
        CONSTANT_ALPHA(32771), 
        CONSTANT_COLOR(32769), 
        DST_ALPHA(772), 
        DST_COLOR(774), 
        ONE(1), 
        ONE_MINUS_CONSTANT_ALPHA(32772), 
        ONE_MINUS_CONSTANT_COLOR(32770), 
        ONE_MINUS_DST_ALPHA(773), 
        ONE_MINUS_DST_COLOR(775), 
        ONE_MINUS_SRC_ALPHA(771), 
        ONE_MINUS_SRC_COLOR(769), 
        SRC_ALPHA(770), 
        SRC_ALPHA_SATURATE(776), 
        SRC_COLOR(768), 
        ZERO(0);
        
        public final int value;
        
        private SourceFactor(final int integer1) {
            this.value = integer1;
        }
    }
    
    @Environment(EnvType.CLIENT)
    public enum DestFactor
    {
        CONSTANT_ALPHA(32771), 
        CONSTANT_COLOR(32769), 
        DST_ALPHA(772), 
        DST_COLOR(774), 
        ONE(1), 
        ONE_MINUS_CONSTANT_ALPHA(32772), 
        ONE_MINUS_CONSTANT_COLOR(32770), 
        ONE_MINUS_DST_ALPHA(773), 
        ONE_MINUS_DST_COLOR(775), 
        ONE_MINUS_SRC_ALPHA(771), 
        ONE_MINUS_SRC_COLOR(769), 
        SRC_ALPHA(770), 
        SRC_COLOR(768), 
        ZERO(0);
        
        public final int value;
        
        private DestFactor(final int integer1) {
            this.value = integer1;
        }
    }
    
    @Environment(EnvType.CLIENT)
    public enum RenderMode
    {
        DEFAULT {
            @Override
            public void begin() {
                GlStateManager.disableAlphaTest();
                GlStateManager.alphaFunc(519, 0.0f);
                GlStateManager.disableLighting();
                GlStateManager.lightModel(2899, GuiLighting.singletonBuffer(0.2f, 0.2f, 0.2f, 1.0f));
                for (int integer1 = 0; integer1 < 8; ++integer1) {
                    GlStateManager.disableLight(integer1);
                    GlStateManager.light(16384 + integer1, 4608, GuiLighting.singletonBuffer(0.0f, 0.0f, 0.0f, 1.0f));
                    GlStateManager.light(16384 + integer1, 4611, GuiLighting.singletonBuffer(0.0f, 0.0f, 1.0f, 0.0f));
                    if (integer1 == 0) {
                        GlStateManager.light(16384 + integer1, 4609, GuiLighting.singletonBuffer(1.0f, 1.0f, 1.0f, 1.0f));
                        GlStateManager.light(16384 + integer1, 4610, GuiLighting.singletonBuffer(1.0f, 1.0f, 1.0f, 1.0f));
                    }
                    else {
                        GlStateManager.light(16384 + integer1, 4609, GuiLighting.singletonBuffer(0.0f, 0.0f, 0.0f, 1.0f));
                        GlStateManager.light(16384 + integer1, 4610, GuiLighting.singletonBuffer(0.0f, 0.0f, 0.0f, 1.0f));
                    }
                }
                GlStateManager.disableColorMaterial();
                GlStateManager.colorMaterial(1032, 5634);
                GlStateManager.disableDepthTest();
                GlStateManager.depthFunc(513);
                GlStateManager.depthMask(true);
                GlStateManager.disableBlend();
                GlStateManager.blendFunc(SourceFactor.ONE, DestFactor.ZERO);
                GlStateManager.blendFuncSeparate(SourceFactor.ONE, DestFactor.ZERO, SourceFactor.ONE, DestFactor.ZERO);
                GlStateManager.blendEquation(32774);
                GlStateManager.disableFog();
                GlStateManager.fogi(2917, 2048);
                GlStateManager.fogDensity(1.0f);
                GlStateManager.fogStart(0.0f);
                GlStateManager.fogEnd(1.0f);
                GlStateManager.fog(2918, GuiLighting.singletonBuffer(0.0f, 0.0f, 0.0f, 0.0f));
                if (GL.getCapabilities().GL_NV_fog_distance) {
                    GlStateManager.fogi(2917, 34140);
                }
                GlStateManager.polygonOffset(0.0f, 0.0f);
                GlStateManager.disableColorLogicOp();
                GlStateManager.logicOp(5379);
                GlStateManager.disableTexGen(TexCoord.a);
                GlStateManager.texGenMode(TexCoord.a, 9216);
                GlStateManager.texGenParam(TexCoord.a, 9474, GuiLighting.singletonBuffer(1.0f, 0.0f, 0.0f, 0.0f));
                GlStateManager.texGenParam(TexCoord.a, 9217, GuiLighting.singletonBuffer(1.0f, 0.0f, 0.0f, 0.0f));
                GlStateManager.disableTexGen(TexCoord.b);
                GlStateManager.texGenMode(TexCoord.b, 9216);
                GlStateManager.texGenParam(TexCoord.b, 9474, GuiLighting.singletonBuffer(0.0f, 1.0f, 0.0f, 0.0f));
                GlStateManager.texGenParam(TexCoord.b, 9217, GuiLighting.singletonBuffer(0.0f, 1.0f, 0.0f, 0.0f));
                GlStateManager.disableTexGen(TexCoord.c);
                GlStateManager.texGenMode(TexCoord.c, 9216);
                GlStateManager.texGenParam(TexCoord.c, 9474, GuiLighting.singletonBuffer(0.0f, 0.0f, 0.0f, 0.0f));
                GlStateManager.texGenParam(TexCoord.c, 9217, GuiLighting.singletonBuffer(0.0f, 0.0f, 0.0f, 0.0f));
                GlStateManager.disableTexGen(TexCoord.d);
                GlStateManager.texGenMode(TexCoord.d, 9216);
                GlStateManager.texGenParam(TexCoord.d, 9474, GuiLighting.singletonBuffer(0.0f, 0.0f, 0.0f, 0.0f));
                GlStateManager.texGenParam(TexCoord.d, 9217, GuiLighting.singletonBuffer(0.0f, 0.0f, 0.0f, 0.0f));
                GlStateManager.activeTexture(0);
                GlStateManager.texParameter(3553, 10240, 9729);
                GlStateManager.texParameter(3553, 10241, 9986);
                GlStateManager.texParameter(3553, 10242, 10497);
                GlStateManager.texParameter(3553, 10243, 10497);
                GlStateManager.texParameter(3553, 33085, 1000);
                GlStateManager.texParameter(3553, 33083, 1000);
                GlStateManager.texParameter(3553, 33082, -1000);
                GlStateManager.texParameter(3553, 34049, 0.0f);
                GlStateManager.texEnv(8960, 8704, 8448);
                GlStateManager.texEnv(8960, 8705, GuiLighting.singletonBuffer(0.0f, 0.0f, 0.0f, 0.0f));
                GlStateManager.texEnv(8960, 34161, 8448);
                GlStateManager.texEnv(8960, 34162, 8448);
                GlStateManager.texEnv(8960, 34176, 5890);
                GlStateManager.texEnv(8960, 34177, 34168);
                GlStateManager.texEnv(8960, 34178, 34166);
                GlStateManager.texEnv(8960, 34184, 5890);
                GlStateManager.texEnv(8960, 34185, 34168);
                GlStateManager.texEnv(8960, 34186, 34166);
                GlStateManager.texEnv(8960, 34192, 768);
                GlStateManager.texEnv(8960, 34193, 768);
                GlStateManager.texEnv(8960, 34194, 770);
                GlStateManager.texEnv(8960, 34200, 770);
                GlStateManager.texEnv(8960, 34201, 770);
                GlStateManager.texEnv(8960, 34202, 770);
                GlStateManager.texEnv(8960, 34163, 1.0f);
                GlStateManager.texEnv(8960, 3356, 1.0f);
                GlStateManager.disableNormalize();
                GlStateManager.shadeModel(7425);
                GlStateManager.disableRescaleNormal();
                GlStateManager.colorMask(true, true, true, true);
                GlStateManager.clearDepth(1.0);
                GlStateManager.lineWidth(1.0f);
                GlStateManager.normal3f(0.0f, 0.0f, 1.0f);
                GlStateManager.polygonMode(1028, 6914);
                GlStateManager.polygonMode(1029, 6914);
            }
            
            @Override
            public void end() {
            }
        }, 
        PLAYER_SKIN {
            @Override
            public void begin() {
                GlStateManager.enableBlend();
                GlStateManager.blendFuncSeparate(770, 771, 1, 0);
            }
            
            @Override
            public void end() {
                GlStateManager.disableBlend();
            }
        }, 
        TRANSPARENT_MODEL {
            @Override
            public void begin() {
                GlStateManager.color4f(1.0f, 1.0f, 1.0f, 0.15f);
                GlStateManager.depthMask(false);
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
                GlStateManager.alphaFunc(516, 0.003921569f);
            }
            
            @Override
            public void end() {
                GlStateManager.disableBlend();
                GlStateManager.alphaFunc(516, 0.1f);
                GlStateManager.depthMask(true);
            }
        };
        
        public abstract void begin();
        
        public abstract void end();
    }
}

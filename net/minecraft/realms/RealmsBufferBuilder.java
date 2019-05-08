package net.minecraft.realms;

import net.minecraft.client.render.VertexFormat;
import java.nio.ByteBuffer;
import net.minecraft.client.render.BufferBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class RealmsBufferBuilder
{
    private BufferBuilder b;
    
    public RealmsBufferBuilder(final BufferBuilder bufferBuilder) {
        this.b = bufferBuilder;
    }
    
    public RealmsBufferBuilder from(final BufferBuilder bufferBuilder) {
        this.b = bufferBuilder;
        return this;
    }
    
    public void sortQuads(final float float1, final float float2, final float float3) {
        this.b.sortQuads(float1, float2, float3);
    }
    
    public void fixupQuadColor(final int integer) {
        this.b.setQuadColor(integer);
    }
    
    public ByteBuffer getBuffer() {
        return this.b.getByteBuffer();
    }
    
    public void postNormal(final float float1, final float float2, final float float3) {
        this.b.postNormal(float1, float2, float3);
    }
    
    public int getDrawMode() {
        return this.b.getDrawMode();
    }
    
    public void offset(final double double1, final double double3, final double double5) {
        this.b.setOffset(double1, double3, double5);
    }
    
    public void restoreState(final BufferBuilder.State state) {
        this.b.restoreState(state);
    }
    
    public void endVertex() {
        this.b.next();
    }
    
    public RealmsBufferBuilder normal(final float float1, final float float2, final float float3) {
        return this.from(this.b.normal(float1, float2, float3));
    }
    
    public void end() {
        this.b.end();
    }
    
    public void begin(final int integer, final VertexFormat vertexFormat) {
        this.b.begin(integer, vertexFormat);
    }
    
    public RealmsBufferBuilder color(final int integer1, final int integer2, final int integer3, final int integer4) {
        return this.from(this.b.color(integer1, integer2, integer3, integer4));
    }
    
    public void faceTex2(final int integer1, final int integer2, final int integer3, final int integer4) {
        this.b.brightness(integer1, integer2, integer3, integer4);
    }
    
    public void postProcessFacePosition(final double double1, final double double3, final double double5) {
        this.b.postPosition(double1, double3, double5);
    }
    
    public void fixupVertexColor(final float float1, final float float2, final float float3, final int integer) {
        this.b.setColor(float1, float2, float3, integer);
    }
    
    public RealmsBufferBuilder color(final float float1, final float float2, final float float3, final float float4) {
        return this.from(this.b.color(float1, float2, float3, float4));
    }
    
    public RealmsVertexFormat getVertexFormat() {
        return new RealmsVertexFormat(this.b.getVertexFormat());
    }
    
    public void faceTint(final float float1, final float float2, final float float3, final int integer) {
        this.b.multiplyColor(float1, float2, float3, integer);
    }
    
    public RealmsBufferBuilder tex2(final int integer1, final int integer2) {
        return this.from(this.b.texture(integer1, integer2));
    }
    
    public void putBulkData(final int[] arr) {
        this.b.putVertexData(arr);
    }
    
    public RealmsBufferBuilder tex(final double double1, final double double3) {
        return this.from(this.b.texture(double1, double3));
    }
    
    public int getVertexCount() {
        return this.b.getVertexCount();
    }
    
    public void clear() {
        this.b.clear();
    }
    
    public RealmsBufferBuilder vertex(final double double1, final double double3, final double double5) {
        return this.from(this.b.vertex(double1, double3, double5));
    }
    
    public void fixupQuadColor(final float float1, final float float2, final float float3) {
        this.b.setQuadColor(float1, float2, float3);
    }
    
    public void noColor() {
        this.b.disableColor();
    }
}

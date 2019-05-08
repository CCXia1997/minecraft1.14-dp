package net.minecraft.client.model;

import net.minecraft.client.render.BufferBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class Box
{
    private final Vertex[] vertices;
    private final Quad[] polygons;
    public final float xMin;
    public final float yMin;
    public final float zMin;
    public final float xMax;
    public final float yMax;
    public final float zMax;
    public String name;
    
    public Box(final Cuboid parent, final int textureOffsetU, final int textureOffsetV, final float xMin, final float yMin, final float zMin, final int xSize, final int ySize, final int zSize, final float scale) {
        this(parent, textureOffsetU, textureOffsetV, xMin, yMin, zMin, xSize, ySize, zSize, scale, parent.mirror);
    }
    
    public Box(final Cuboid parent, final int textureOffsetU, final int textureOffsetV, float xMin, float yMin, float zMin, final int xSize, final int ySize, final int zSize, final float scale, final boolean flip) {
        this.xMin = xMin;
        this.yMin = yMin;
        this.zMin = zMin;
        this.xMax = xMin + xSize;
        this.yMax = yMin + ySize;
        this.zMax = zMin + zSize;
        this.vertices = new Vertex[8];
        this.polygons = new Quad[6];
        float float12 = xMin + xSize;
        float float13 = yMin + ySize;
        float float14 = zMin + zSize;
        xMin -= scale;
        yMin -= scale;
        zMin -= scale;
        float12 += scale;
        float13 += scale;
        float14 += scale;
        if (flip) {
            final float float15 = float12;
            float12 = xMin;
            xMin = float15;
        }
        final Vertex vertex15 = new Vertex(xMin, yMin, zMin, 0.0f, 0.0f);
        final Vertex vertex16 = new Vertex(float12, yMin, zMin, 0.0f, 8.0f);
        final Vertex vertex17 = new Vertex(float12, float13, zMin, 8.0f, 8.0f);
        final Vertex vertex18 = new Vertex(xMin, float13, zMin, 8.0f, 0.0f);
        final Vertex vertex19 = new Vertex(xMin, yMin, float14, 0.0f, 0.0f);
        final Vertex vertex20 = new Vertex(float12, yMin, float14, 0.0f, 8.0f);
        final Vertex vertex21 = new Vertex(float12, float13, float14, 8.0f, 8.0f);
        final Vertex vertex22 = new Vertex(xMin, float13, float14, 8.0f, 0.0f);
        this.vertices[0] = vertex15;
        this.vertices[1] = vertex16;
        this.vertices[2] = vertex17;
        this.vertices[3] = vertex18;
        this.vertices[4] = vertex19;
        this.vertices[5] = vertex20;
        this.vertices[6] = vertex21;
        this.vertices[7] = vertex22;
        this.polygons[0] = new Quad(new Vertex[] { vertex20, vertex16, vertex17, vertex21 }, textureOffsetU + zSize + xSize, textureOffsetV + zSize, textureOffsetU + zSize + xSize + zSize, textureOffsetV + zSize + ySize, parent.textureWidth, parent.textureHeight);
        this.polygons[1] = new Quad(new Vertex[] { vertex15, vertex19, vertex22, vertex18 }, textureOffsetU, textureOffsetV + zSize, textureOffsetU + zSize, textureOffsetV + zSize + ySize, parent.textureWidth, parent.textureHeight);
        this.polygons[2] = new Quad(new Vertex[] { vertex20, vertex19, vertex15, vertex16 }, textureOffsetU + zSize, textureOffsetV, textureOffsetU + zSize + xSize, textureOffsetV + zSize, parent.textureWidth, parent.textureHeight);
        this.polygons[3] = new Quad(new Vertex[] { vertex17, vertex18, vertex22, vertex21 }, textureOffsetU + zSize + xSize, textureOffsetV + zSize, textureOffsetU + zSize + xSize + xSize, textureOffsetV, parent.textureWidth, parent.textureHeight);
        this.polygons[4] = new Quad(new Vertex[] { vertex16, vertex15, vertex18, vertex17 }, textureOffsetU + zSize, textureOffsetV + zSize, textureOffsetU + zSize + xSize, textureOffsetV + zSize + ySize, parent.textureWidth, parent.textureHeight);
        this.polygons[5] = new Quad(new Vertex[] { vertex19, vertex20, vertex21, vertex22 }, textureOffsetU + zSize + xSize + zSize, textureOffsetV + zSize, textureOffsetU + zSize + xSize + zSize + xSize, textureOffsetV + zSize + ySize, parent.textureWidth, parent.textureHeight);
        if (flip) {
            for (final Quad quad26 : this.polygons) {
                quad26.flip();
            }
        }
    }
    
    public void render(final BufferBuilder bufferBuilder, final float scale) {
        for (final Quad quad6 : this.polygons) {
            quad6.render(bufferBuilder, scale);
        }
    }
    
    public Box setName(final String name) {
        this.name = name;
        return this;
    }
}

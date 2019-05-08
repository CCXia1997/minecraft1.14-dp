package net.minecraft.client.model;

import net.minecraft.util.math.Vec3d;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.BufferBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class Quad
{
    public Vertex[] vertices;
    public final int vertexCount;
    private boolean flipNormals;
    
    public Quad(final Vertex[] arr) {
        this.vertices = arr;
        this.vertexCount = arr.length;
    }
    
    public Quad(final Vertex[] vertices, final int integer2, final int integer3, final int integer4, final int integer5, final float float6, final float float7) {
        this(vertices);
        final float float8 = 0.0f / float6;
        final float float9 = 0.0f / float7;
        vertices[0] = vertices[0].remap(integer4 / float6 - float8, integer3 / float7 + float9);
        vertices[1] = vertices[1].remap(integer2 / float6 + float8, integer3 / float7 + float9);
        vertices[2] = vertices[2].remap(integer2 / float6 + float8, integer5 / float7 - float9);
        vertices[3] = vertices[3].remap(integer4 / float6 - float8, integer5 / float7 - float9);
    }
    
    public void flip() {
        final Vertex[] arr1 = new Vertex[this.vertices.length];
        for (int integer2 = 0; integer2 < this.vertices.length; ++integer2) {
            arr1[integer2] = this.vertices[this.vertices.length - integer2 - 1];
        }
        this.vertices = arr1;
    }
    
    public void render(final BufferBuilder bufferBuilder, final float scale) {
        final Vec3d vec3d3 = this.vertices[1].pos.reverseSubtract(this.vertices[0].pos);
        final Vec3d vec3d4 = this.vertices[1].pos.reverseSubtract(this.vertices[2].pos);
        final Vec3d vec3d5 = vec3d4.crossProduct(vec3d3).normalize();
        float float6 = (float)vec3d5.x;
        float float7 = (float)vec3d5.y;
        float float8 = (float)vec3d5.z;
        if (this.flipNormals) {
            float6 = -float6;
            float7 = -float7;
            float8 = -float8;
        }
        bufferBuilder.begin(7, VertexFormats.POSITION_UV_NORMAL_2);
        for (int integer9 = 0; integer9 < 4; ++integer9) {
            final Vertex vertex10 = this.vertices[integer9];
            bufferBuilder.vertex(vertex10.pos.x * scale, vertex10.pos.y * scale, vertex10.pos.z * scale).texture(vertex10.u, vertex10.v).normal(float6, float7, float8).next();
        }
        Tessellator.getInstance().draw();
    }
}

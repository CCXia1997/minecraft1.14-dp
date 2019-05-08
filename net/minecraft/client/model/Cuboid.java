package net.minecraft.client.model;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.util.GlAllocationUtils;
import com.mojang.blaze3d.platform.GlStateManager;
import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class Cuboid
{
    public float textureWidth;
    public float textureHeight;
    private int textureOffsetU;
    private int textureOffsetV;
    public float rotationPointX;
    public float rotationPointY;
    public float rotationPointZ;
    public float pitch;
    public float yaw;
    public float roll;
    private boolean compiled;
    private int list;
    public boolean mirror;
    public boolean visible;
    public boolean k;
    public final List<Box> boxes;
    public List<Cuboid> children;
    public final String name;
    public float x;
    public float y;
    public float z;
    
    public Cuboid(final Model owner, final String name) {
        this.textureWidth = 64.0f;
        this.textureHeight = 32.0f;
        this.visible = true;
        this.boxes = Lists.newArrayList();
        owner.cuboidList.add(this);
        this.name = name;
        this.setTextureSize(owner.textureWidth, owner.textureHeight);
    }
    
    public Cuboid(final Model model) {
        this(model, null);
    }
    
    public Cuboid(final Model owner, final int textureOffsetU, final int textureOffsetV) {
        this(owner);
        this.setTextureOffset(textureOffsetU, textureOffsetV);
    }
    
    public void copyRotation(final Cuboid cuboid) {
        this.pitch = cuboid.pitch;
        this.yaw = cuboid.yaw;
        this.roll = cuboid.roll;
        this.rotationPointX = cuboid.rotationPointX;
        this.rotationPointY = cuboid.rotationPointY;
        this.rotationPointZ = cuboid.rotationPointZ;
    }
    
    public void addChild(final Cuboid cuboid) {
        if (this.children == null) {
            this.children = Lists.newArrayList();
        }
        this.children.add(cuboid);
    }
    
    public void removeChild(final Cuboid cuboid) {
        if (this.children != null) {
            this.children.remove(cuboid);
        }
    }
    
    public Cuboid setTextureOffset(final int u, final int v) {
        this.textureOffsetU = u;
        this.textureOffsetV = v;
        return this;
    }
    
    public Cuboid addBox(String suffix, final float xMin, final float yMin, final float zMin, final int xSize, final int ySize, final int zSize, final float float8, final int textureOffsetU, final int textureOffsetV) {
        suffix = this.name + "." + suffix;
        this.setTextureOffset(textureOffsetU, textureOffsetV);
        this.boxes.add(new Box(this, this.textureOffsetU, this.textureOffsetV, xMin, yMin, zMin, xSize, ySize, zSize, float8).setName(suffix));
        return this;
    }
    
    public Cuboid addBox(final float xMin, final float yMin, final float zMin, final int xSize, final int ySize, final int zSize) {
        this.boxes.add(new Box(this, this.textureOffsetU, this.textureOffsetV, xMin, yMin, zMin, xSize, ySize, zSize, 0.0f));
        return this;
    }
    
    public Cuboid addBox(final float xMin, final float yMin, final float zMin, final int xSize, final int ySize, final int zSize, final boolean boolean7) {
        this.boxes.add(new Box(this, this.textureOffsetU, this.textureOffsetV, xMin, yMin, zMin, xSize, ySize, zSize, 0.0f, boolean7));
        return this;
    }
    
    public void addBox(final float xMin, final float yMin, final float zMin, final int xSize, final int ySize, final int zSize, final float float7) {
        this.boxes.add(new Box(this, this.textureOffsetU, this.textureOffsetV, xMin, yMin, zMin, xSize, ySize, zSize, float7));
    }
    
    public void addBox(final float xMin, final float yMin, final float zMin, final int xSize, final int ySize, final int zSize, final float float7, final boolean boolean8) {
        this.boxes.add(new Box(this, this.textureOffsetU, this.textureOffsetV, xMin, yMin, zMin, xSize, ySize, zSize, float7, boolean8));
    }
    
    public void setRotationPoint(final float x, final float y, final float z) {
        this.rotationPointX = x;
        this.rotationPointY = y;
        this.rotationPointZ = z;
    }
    
    public void render(final float scale) {
        if (this.k) {
            return;
        }
        if (!this.visible) {
            return;
        }
        if (!this.compiled) {
            this.compile(scale);
        }
        GlStateManager.pushMatrix();
        GlStateManager.translatef(this.x, this.y, this.z);
        if (this.pitch != 0.0f || this.yaw != 0.0f || this.roll != 0.0f) {
            GlStateManager.pushMatrix();
            GlStateManager.translatef(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);
            if (this.roll != 0.0f) {
                GlStateManager.rotatef(this.roll * 57.295776f, 0.0f, 0.0f, 1.0f);
            }
            if (this.yaw != 0.0f) {
                GlStateManager.rotatef(this.yaw * 57.295776f, 0.0f, 1.0f, 0.0f);
            }
            if (this.pitch != 0.0f) {
                GlStateManager.rotatef(this.pitch * 57.295776f, 1.0f, 0.0f, 0.0f);
            }
            GlStateManager.callList(this.list);
            if (this.children != null) {
                for (int integer2 = 0; integer2 < this.children.size(); ++integer2) {
                    this.children.get(integer2).render(scale);
                }
            }
            GlStateManager.popMatrix();
        }
        else if (this.rotationPointX != 0.0f || this.rotationPointY != 0.0f || this.rotationPointZ != 0.0f) {
            GlStateManager.pushMatrix();
            GlStateManager.translatef(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);
            GlStateManager.callList(this.list);
            if (this.children != null) {
                for (int integer2 = 0; integer2 < this.children.size(); ++integer2) {
                    this.children.get(integer2).render(scale);
                }
            }
            GlStateManager.popMatrix();
        }
        else {
            GlStateManager.callList(this.list);
            if (this.children != null) {
                for (int integer2 = 0; integer2 < this.children.size(); ++integer2) {
                    this.children.get(integer2).render(scale);
                }
            }
        }
        GlStateManager.popMatrix();
    }
    
    public void b(final float scale) {
        if (this.k) {
            return;
        }
        if (!this.visible) {
            return;
        }
        if (!this.compiled) {
            this.compile(scale);
        }
        GlStateManager.pushMatrix();
        GlStateManager.translatef(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);
        if (this.yaw != 0.0f) {
            GlStateManager.rotatef(this.yaw * 57.295776f, 0.0f, 1.0f, 0.0f);
        }
        if (this.pitch != 0.0f) {
            GlStateManager.rotatef(this.pitch * 57.295776f, 1.0f, 0.0f, 0.0f);
        }
        if (this.roll != 0.0f) {
            GlStateManager.rotatef(this.roll * 57.295776f, 0.0f, 0.0f, 1.0f);
        }
        GlStateManager.callList(this.list);
        GlStateManager.popMatrix();
    }
    
    public void applyTransform(final float scale) {
        if (this.k) {
            return;
        }
        if (!this.visible) {
            return;
        }
        if (!this.compiled) {
            this.compile(scale);
        }
        if (this.pitch != 0.0f || this.yaw != 0.0f || this.roll != 0.0f) {
            GlStateManager.translatef(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);
            if (this.roll != 0.0f) {
                GlStateManager.rotatef(this.roll * 57.295776f, 0.0f, 0.0f, 1.0f);
            }
            if (this.yaw != 0.0f) {
                GlStateManager.rotatef(this.yaw * 57.295776f, 0.0f, 1.0f, 0.0f);
            }
            if (this.pitch != 0.0f) {
                GlStateManager.rotatef(this.pitch * 57.295776f, 1.0f, 0.0f, 0.0f);
            }
        }
        else if (this.rotationPointX != 0.0f || this.rotationPointY != 0.0f || this.rotationPointZ != 0.0f) {
            GlStateManager.translatef(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);
        }
    }
    
    private void compile(final float scale) {
        GlStateManager.newList(this.list = GlAllocationUtils.genLists(1), 4864);
        final BufferBuilder bufferBuilder2 = Tessellator.getInstance().getBufferBuilder();
        for (int integer3 = 0; integer3 < this.boxes.size(); ++integer3) {
            this.boxes.get(integer3).render(bufferBuilder2, scale);
        }
        GlStateManager.endList();
        this.compiled = true;
    }
    
    public Cuboid setTextureSize(final int width, final int height) {
        this.textureWidth = (float)width;
        this.textureHeight = (float)height;
        return this;
    }
}

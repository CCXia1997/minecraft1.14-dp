package net.minecraft.entity;

public class EntitySize
{
    public final float width;
    public final float height;
    public final boolean constant;
    
    public EntitySize(final float float1, final float float2, final boolean boolean3) {
        this.width = float1;
        this.height = float2;
        this.constant = boolean3;
    }
    
    public EntitySize scaled(final float ratio) {
        return this.scaled(ratio, ratio);
    }
    
    public EntitySize scaled(final float widthRatio, final float heightRatio) {
        if (this.constant || (widthRatio == 1.0f && heightRatio == 1.0f)) {
            return this;
        }
        return resizeable(this.width * widthRatio, this.height * heightRatio);
    }
    
    public static EntitySize resizeable(final float x, final float z) {
        return new EntitySize(x, z, false);
    }
    
    public static EntitySize constant(final float x, final float z) {
        return new EntitySize(x, z, true);
    }
    
    @Override
    public String toString() {
        return "EntityDimensions w=" + this.width + ", h=" + this.height + ", fixed=" + this.constant;
    }
}

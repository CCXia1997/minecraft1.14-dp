package net.minecraft.util.math;

import java.util.AbstractList;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;

public class EulerRotation
{
    protected final float x;
    protected final float y;
    protected final float z;
    
    public EulerRotation(final float x, final float y, final float z) {
        this.x = ((Float.isInfinite(x) || Float.isNaN(x)) ? 0.0f : (x % 360.0f));
        this.y = ((Float.isInfinite(y) || Float.isNaN(y)) ? 0.0f : (y % 360.0f));
        this.z = ((Float.isInfinite(z) || Float.isNaN(z)) ? 0.0f : (z % 360.0f));
    }
    
    public EulerRotation(final ListTag serialized) {
        this(serialized.getFloat(0), serialized.getFloat(1), serialized.getFloat(2));
    }
    
    public ListTag serialize() {
        final ListTag listTag1 = new ListTag();
        ((AbstractList<FloatTag>)listTag1).add(new FloatTag(this.x));
        ((AbstractList<FloatTag>)listTag1).add(new FloatTag(this.y));
        ((AbstractList<FloatTag>)listTag1).add(new FloatTag(this.z));
        return listTag1;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof EulerRotation)) {
            return false;
        }
        final EulerRotation eulerRotation2 = (EulerRotation)o;
        return this.x == eulerRotation2.x && this.y == eulerRotation2.y && this.z == eulerRotation2.z;
    }
    
    public float getX() {
        return this.x;
    }
    
    public float getY() {
        return this.y;
    }
    
    public float getZ() {
        return this.z;
    }
}

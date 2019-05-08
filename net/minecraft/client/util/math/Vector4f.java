package net.minecraft.client.util.math;

import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class Vector4f
{
    private final float[] components;
    
    public Vector4f() {
        this.components = new float[4];
    }
    
    public Vector4f(final float x, final float y, final float z, final float w) {
        this.components = new float[] { x, y, z, w };
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final Vector4f vector4f2 = (Vector4f)o;
        return Arrays.equals(this.components, vector4f2.components);
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(this.components);
    }
    
    public float x() {
        return this.components[0];
    }
    
    public float y() {
        return this.components[1];
    }
    
    public float z() {
        return this.components[2];
    }
    
    public float w() {
        return this.components[3];
    }
    
    public void multiply(final Vector3f vector3f) {
        final float[] components = this.components;
        final int n = 0;
        components[n] *= vector3f.x();
        final float[] components2 = this.components;
        final int n2 = 1;
        components2[n2] *= vector3f.y();
        final float[] components3 = this.components;
        final int n3 = 2;
        components3[n3] *= vector3f.z();
    }
    
    public void set(final float x, final float y, final float z, final float w) {
        this.components[0] = x;
        this.components[1] = y;
        this.components[2] = z;
        this.components[3] = w;
    }
    
    public void a(final Quaternion quaternion) {
        final Quaternion quaternion2 = new Quaternion(quaternion);
        quaternion2.a(new Quaternion(this.x(), this.y(), this.z(), 0.0f));
        final Quaternion quaternion3 = new Quaternion(quaternion);
        quaternion3.e();
        quaternion2.a(quaternion3);
        this.set(quaternion2.a(), quaternion2.b(), quaternion2.c(), this.w());
    }
}

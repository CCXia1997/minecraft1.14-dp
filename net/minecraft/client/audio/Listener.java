package net.minecraft.client.audio;

import org.lwjgl.openal.AL10;
import net.minecraft.util.math.Vec3d;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class Listener
{
    public static final Vec3d a;
    private float volume;
    
    public Listener() {
        this.volume = 1.0f;
    }
    
    public void setPosition(final Vec3d position) {
        AL10.alListener3f(4100, (float)position.x, (float)position.y, (float)position.z);
    }
    
    public void setOrientation(final Vec3d from, final Vec3d to) {
        AL10.alListenerfv(4111, new float[] { (float)from.x, (float)from.y, (float)from.z, (float)to.x, (float)to.y, (float)to.z });
    }
    
    public void setVolume(final float volume) {
        AL10.alListenerf(4106, volume);
        this.volume = volume;
    }
    
    public float getVolume() {
        return this.volume;
    }
    
    public void init() {
        this.setPosition(Vec3d.ZERO);
        this.setOrientation(new Vec3d(0.0, 0.0, -1.0), Listener.a);
    }
    
    static {
        a = new Vec3d(0.0, 1.0, 0.0);
    }
}

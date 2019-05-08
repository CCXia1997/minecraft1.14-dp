package net.minecraft.client.render.model.json;

import net.minecraft.util.math.Direction;
import net.minecraft.client.util.math.Vector3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ModelRotation
{
    public final Vector3f origin;
    public final Direction.Axis axis;
    public final float angle;
    public final boolean rescale;
    
    public ModelRotation(final Vector3f origin, final Direction.Axis axis, final float angle, final boolean rescale) {
        this.origin = origin;
        this.axis = axis;
        this.angle = angle;
        this.rescale = rescale;
    }
}

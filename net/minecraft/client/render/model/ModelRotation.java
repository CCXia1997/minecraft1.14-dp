package net.minecraft.client.render.model;

import java.util.stream.Collectors;
import java.util.Comparator;
import java.util.Arrays;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.util.math.Quaternion;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public enum ModelRotation implements ModelBakeSettings
{
    X0_Y0(0, 0), 
    X0_Y90(0, 90), 
    X0_Y180(0, 180), 
    X0_Y270(0, 270), 
    X90_Y0(90, 0), 
    X90_Y90(90, 90), 
    X90_Y180(90, 180), 
    X90_Y270(90, 270), 
    X180_Y0(180, 0), 
    X180_Y90(180, 90), 
    X180_Y180(180, 180), 
    X180_Y270(180, 270), 
    X270_Y0(270, 0), 
    X270_Y90(270, 90), 
    X270_Y180(270, 180), 
    X270_Y270(270, 270);
    
    private static final Map<Integer, ModelRotation> BY_INDEX;
    private final int index;
    private final Quaternion quaternion;
    private final int xRotations;
    private final int yRotations;
    
    private static int getIndex(final int x, final int y) {
        return x * 360 + y;
    }
    
    private ModelRotation(final int x, final int y) {
        this.index = getIndex(x, y);
        final Quaternion quaternion = new Quaternion(new Vector3f(0.0f, 1.0f, 0.0f), (float)(-y), true);
        quaternion.a(new Quaternion(new Vector3f(1.0f, 0.0f, 0.0f), (float)(-x), true));
        this.quaternion = quaternion;
        this.xRotations = MathHelper.abs(x / 90);
        this.yRotations = MathHelper.abs(y / 90);
    }
    
    @Override
    public ModelRotation getRotation() {
        return this;
    }
    
    public Quaternion getQuaternion() {
        return this.quaternion;
    }
    
    public Direction apply(final Direction direction) {
        Direction direction2 = direction;
        for (int integer3 = 0; integer3 < this.xRotations; ++integer3) {
            direction2 = direction2.rotateClockwise(Direction.Axis.X);
        }
        if (direction2.getAxis() != Direction.Axis.Y) {
            for (int integer3 = 0; integer3 < this.yRotations; ++integer3) {
                direction2 = direction2.rotateClockwise(Direction.Axis.Y);
            }
        }
        return direction2;
    }
    
    public int a(final Direction direction, final int integer) {
        int integer2 = integer;
        if (direction.getAxis() == Direction.Axis.X) {
            integer2 = (integer2 + this.xRotations) % 4;
        }
        Direction direction2 = direction;
        for (int integer3 = 0; integer3 < this.xRotations; ++integer3) {
            direction2 = direction2.rotateClockwise(Direction.Axis.X);
        }
        if (direction2.getAxis() == Direction.Axis.Y) {
            integer2 = (integer2 + this.yRotations) % 4;
        }
        return integer2;
    }
    
    public static ModelRotation get(final int x, final int y) {
        return ModelRotation.BY_INDEX.get(getIndex(MathHelper.floorMod(x, 360), MathHelper.floorMod(y, 360)));
    }
    
    static {
        BY_INDEX = Arrays.<ModelRotation>stream(values()).sorted(Comparator.comparingInt(modelRotation -> modelRotation.index)).collect(Collectors.toMap(modelRotation -> modelRotation.index, modelRotation -> modelRotation));
    }
}

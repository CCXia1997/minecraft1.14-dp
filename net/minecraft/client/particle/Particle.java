package net.minecraft.client.particle;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ViewableWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.LoopingStream;
import net.minecraft.util.shape.VoxelShape;
import java.util.stream.Stream;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.util.math.MathHelper;
import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.util.math.BoundingBox;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class Particle
{
    private static final BoundingBox EMPTY_BOUNDING_BOX;
    protected final World world;
    protected double prevPosX;
    protected double prevPosY;
    protected double prevPosZ;
    protected double x;
    protected double y;
    protected double z;
    protected double velocityX;
    protected double velocityY;
    protected double velocityZ;
    private BoundingBox boundingBox;
    protected boolean onGround;
    protected boolean collidesWithWorld;
    protected boolean dead;
    protected float spacingXZ;
    protected float spacingY;
    protected final Random random;
    protected int age;
    protected int maxAge;
    protected float gravityStrength;
    protected float colorRed;
    protected float colorGreen;
    protected float colorBlue;
    protected float colorAlpha;
    protected float angle;
    protected float prevAngle;
    public static double cameraX;
    public static double cameraY;
    public static double cameraZ;
    
    protected Particle(final World world, final double x, final double y, final double z) {
        this.boundingBox = Particle.EMPTY_BOUNDING_BOX;
        this.collidesWithWorld = true;
        this.spacingXZ = 0.6f;
        this.spacingY = 1.8f;
        this.random = new Random();
        this.colorRed = 1.0f;
        this.colorGreen = 1.0f;
        this.colorBlue = 1.0f;
        this.colorAlpha = 1.0f;
        this.world = world;
        this.setBoundingBoxSpacing(0.2f, 0.2f);
        this.setPos(x, y, z);
        this.prevPosX = x;
        this.prevPosY = y;
        this.prevPosZ = z;
        this.maxAge = (int)(4.0f / (this.random.nextFloat() * 0.9f + 0.1f));
    }
    
    public Particle(final World world, final double x, final double y, final double z, final double velocityX, final double velocityY, final double velocityZ) {
        this(world, x, y, z);
        this.velocityX = velocityX + (Math.random() * 2.0 - 1.0) * 0.4000000059604645;
        this.velocityY = velocityY + (Math.random() * 2.0 - 1.0) * 0.4000000059604645;
        this.velocityZ = velocityZ + (Math.random() * 2.0 - 1.0) * 0.4000000059604645;
        final float float14 = (float)(Math.random() + Math.random() + 1.0) * 0.15f;
        final float float15 = MathHelper.sqrt(this.velocityX * this.velocityX + this.velocityY * this.velocityY + this.velocityZ * this.velocityZ);
        this.velocityX = this.velocityX / float15 * float14 * 0.4000000059604645;
        this.velocityY = this.velocityY / float15 * float14 * 0.4000000059604645 + 0.10000000149011612;
        this.velocityZ = this.velocityZ / float15 * float14 * 0.4000000059604645;
    }
    
    public Particle move(final float speed) {
        this.velocityX *= speed;
        this.velocityY = (this.velocityY - 0.10000000149011612) * speed + 0.10000000149011612;
        this.velocityZ *= speed;
        return this;
    }
    
    public Particle e(final float float1) {
        this.setBoundingBoxSpacing(0.2f * float1, 0.2f * float1);
        return this;
    }
    
    public void setColor(final float red, final float green, final float blue) {
        this.colorRed = red;
        this.colorGreen = green;
        this.colorBlue = blue;
    }
    
    protected void setColorAlpha(final float alpha) {
        this.colorAlpha = alpha;
    }
    
    public void setMaxAge(final int maxAge) {
        this.maxAge = maxAge;
    }
    
    public int getMaxAge() {
        return this.maxAge;
    }
    
    public void update() {
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;
        if (this.age++ >= this.maxAge) {
            this.markDead();
            return;
        }
        this.velocityY -= 0.04 * this.gravityStrength;
        this.move(this.velocityX, this.velocityY, this.velocityZ);
        this.velocityX *= 0.9800000190734863;
        this.velocityY *= 0.9800000190734863;
        this.velocityZ *= 0.9800000190734863;
        if (this.onGround) {
            this.velocityX *= 0.699999988079071;
            this.velocityZ *= 0.699999988079071;
        }
    }
    
    public abstract void buildGeometry(final BufferBuilder arg1, final Camera arg2, final float arg3, final float arg4, final float arg5, final float arg6, final float arg7, final float arg8);
    
    public abstract ParticleTextureSheet getTextureSheet();
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ", Pos (" + this.x + "," + this.y + "," + this.z + "), RGBA (" + this.colorRed + "," + this.colorGreen + "," + this.colorBlue + "," + this.colorAlpha + "), Age " + this.age;
    }
    
    public void markDead() {
        this.dead = true;
    }
    
    protected void setBoundingBoxSpacing(final float spacingXZ, final float float2) {
        if (spacingXZ != this.spacingXZ || float2 != this.spacingY) {
            this.spacingXZ = spacingXZ;
            this.spacingY = float2;
            final BoundingBox boundingBox3 = this.getBoundingBox();
            final double double4 = (boundingBox3.minX + boundingBox3.maxX - spacingXZ) / 2.0;
            final double double5 = (boundingBox3.minZ + boundingBox3.maxZ - spacingXZ) / 2.0;
            this.setBoundingBox(new BoundingBox(double4, boundingBox3.minY, double5, double4 + this.spacingXZ, boundingBox3.minY + this.spacingY, double5 + this.spacingXZ));
        }
    }
    
    public void setPos(final double x, final double y, final double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        final float float7 = this.spacingXZ / 2.0f;
        final float float8 = this.spacingY;
        this.setBoundingBox(new BoundingBox(x - float7, y, z - float7, x + float7, y + float8, z + float7));
    }
    
    public void move(double dx, double dy, double dz) {
        final double double7 = dx;
        final double double8 = dy;
        final double double9 = dz;
        if ((this.collidesWithWorld && dx != 0.0) || dy != 0.0 || dz != 0.0) {
            final Vec3d vec3d13 = Entity.a(new Vec3d(dx, dy, dz), this.getBoundingBox(), this.world, VerticalEntityPosition.minValue(), new LoopingStream<VoxelShape>(Stream.<VoxelShape>empty()));
            dx = vec3d13.x;
            dy = vec3d13.y;
            dz = vec3d13.z;
        }
        if (dx != 0.0 || dy != 0.0 || dz != 0.0) {
            this.setBoundingBox(this.getBoundingBox().offset(dx, dy, dz));
            this.repositionFromBoundingBox();
        }
        this.onGround = (double8 != dy && double8 < 0.0);
        if (double7 != dx) {
            this.velocityX = 0.0;
        }
        if (double9 != dz) {
            this.velocityZ = 0.0;
        }
    }
    
    protected void repositionFromBoundingBox() {
        final BoundingBox boundingBox1 = this.getBoundingBox();
        this.x = (boundingBox1.minX + boundingBox1.maxX) / 2.0;
        this.y = boundingBox1.minY;
        this.z = (boundingBox1.minZ + boundingBox1.maxZ) / 2.0;
    }
    
    protected int getColorMultiplier(final float float1) {
        final BlockPos blockPos2 = new BlockPos(this.x, this.y, this.z);
        if (this.world.isBlockLoaded(blockPos2)) {
            return this.world.getLightmapIndex(blockPos2, 0);
        }
        return 0;
    }
    
    public boolean isAlive() {
        return !this.dead;
    }
    
    public BoundingBox getBoundingBox() {
        return this.boundingBox;
    }
    
    public void setBoundingBox(final BoundingBox boundingBox) {
        this.boundingBox = boundingBox;
    }
    
    static {
        EMPTY_BOUNDING_BOX = new BoundingBox(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
    }
}

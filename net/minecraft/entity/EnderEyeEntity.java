package net.minecraft.entity;

import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.client.network.packet.EntitySpawnS2CPacket;
import net.minecraft.network.Packet;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundEvents;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.BlockPos;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemProvider;
import net.minecraft.util.SystemUtil;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.data.TrackedData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.EnvironmentInterface;
import net.fabricmc.api.EnvironmentInterfaces;

@EnvironmentInterfaces({ @EnvironmentInterface(value = EnvType.CLIENT, itf = awj.class) })
public class EnderEyeEntity extends Entity implements FlyingItemEntity
{
    private static final TrackedData<ItemStack> b;
    private double c;
    private double d;
    private double e;
    private int useCount;
    private boolean g;
    
    public EnderEyeEntity(final EntityType<? extends EnderEyeEntity> type, final World world) {
        super(type, world);
    }
    
    public EnderEyeEntity(final World world, final double double2, final double double4, final double double6) {
        this(EntityType.EYE_OF_ENDER, world);
        this.useCount = 0;
        this.setPosition(double2, double4, double6);
    }
    
    public void b(final ItemStack itemStack) {
        if (itemStack.getItem() != Items.mt || itemStack.hasTag()) {
            this.getDataTracker().<ItemStack>set(EnderEyeEntity.b, (ItemStack)SystemUtil.<T>consume((T)itemStack.copy(), itemStack -> itemStack.setAmount(1)));
        }
    }
    
    private ItemStack i() {
        return this.getDataTracker().<ItemStack>get(EnderEyeEntity.b);
    }
    
    @Override
    public ItemStack getStack() {
        final ItemStack itemStack1 = this.i();
        return itemStack1.isEmpty() ? new ItemStack(Items.mt) : itemStack1;
    }
    
    @Override
    protected void initDataTracker() {
        this.getDataTracker().<ItemStack>startTracking(EnderEyeEntity.b, ItemStack.EMPTY);
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public boolean shouldRenderAtDistance(final double distance) {
        double double3 = this.getBoundingBox().averageDimension() * 4.0;
        if (Double.isNaN(double3)) {
            double3 = 4.0;
        }
        double3 *= 64.0;
        return distance < double3 * double3;
    }
    
    public void a(final BlockPos blockPos) {
        final double double2 = blockPos.getX();
        final int integer4 = blockPos.getY();
        final double double3 = blockPos.getZ();
        final double double4 = double2 - this.x;
        final double double5 = double3 - this.z;
        final float float11 = MathHelper.sqrt(double4 * double4 + double5 * double5);
        if (float11 > 12.0f) {
            this.c = this.x + double4 / float11 * 12.0;
            this.e = this.z + double5 / float11 * 12.0;
            this.d = this.y + 8.0;
        }
        else {
            this.c = double2;
            this.d = integer4;
            this.e = double3;
        }
        this.useCount = 0;
        this.g = (this.random.nextInt(5) > 0);
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void setVelocityClient(final double x, final double y, final double z) {
        this.setVelocity(x, y, z);
        if (this.prevPitch == 0.0f && this.prevYaw == 0.0f) {
            final float float7 = MathHelper.sqrt(x * x + z * z);
            this.yaw = (float)(MathHelper.atan2(x, z) * 57.2957763671875);
            this.pitch = (float)(MathHelper.atan2(y, float7) * 57.2957763671875);
            this.prevYaw = this.yaw;
            this.prevPitch = this.pitch;
        }
    }
    
    @Override
    public void tick() {
        this.prevRenderX = this.x;
        this.prevRenderY = this.y;
        this.prevRenderZ = this.z;
        super.tick();
        Vec3d vec3d1 = this.getVelocity();
        this.x += vec3d1.x;
        this.y += vec3d1.y;
        this.z += vec3d1.z;
        final float float2 = MathHelper.sqrt(Entity.squaredHorizontalLength(vec3d1));
        this.yaw = (float)(MathHelper.atan2(vec3d1.x, vec3d1.z) * 57.2957763671875);
        this.pitch = (float)(MathHelper.atan2(vec3d1.y, float2) * 57.2957763671875);
        while (this.pitch - this.prevPitch < -180.0f) {
            this.prevPitch -= 360.0f;
        }
        while (this.pitch - this.prevPitch >= 180.0f) {
            this.prevPitch += 360.0f;
        }
        while (this.yaw - this.prevYaw < -180.0f) {
            this.prevYaw -= 360.0f;
        }
        while (this.yaw - this.prevYaw >= 180.0f) {
            this.prevYaw += 360.0f;
        }
        this.pitch = MathHelper.lerp(0.2f, this.prevPitch, this.pitch);
        this.yaw = MathHelper.lerp(0.2f, this.prevYaw, this.yaw);
        if (!this.world.isClient) {
            final double double3 = this.c - this.x;
            final double double4 = this.e - this.z;
            final float float3 = (float)Math.sqrt(double3 * double3 + double4 * double4);
            final float float4 = (float)MathHelper.atan2(double4, double3);
            double double5 = MathHelper.lerp(0.0025, float2, float3);
            double double6 = vec3d1.y;
            if (float3 < 1.0f) {
                double5 *= 0.8;
                double6 *= 0.8;
            }
            final int integer13 = (this.y < this.d) ? 1 : -1;
            vec3d1 = new Vec3d(Math.cos(float4) * double5, double6 + (integer13 - double6) * 0.014999999664723873, Math.sin(float4) * double5);
            this.setVelocity(vec3d1);
        }
        final float float5 = 0.25f;
        if (this.isInsideWater()) {
            for (int integer14 = 0; integer14 < 4; ++integer14) {
                this.world.addParticle(ParticleTypes.e, this.x - vec3d1.x * 0.25, this.y - vec3d1.y * 0.25, this.z - vec3d1.z * 0.25, vec3d1.x, vec3d1.y, vec3d1.z);
            }
        }
        else {
            this.world.addParticle(ParticleTypes.O, this.x - vec3d1.x * 0.25 + this.random.nextDouble() * 0.6 - 0.3, this.y - vec3d1.y * 0.25 - 0.5, this.z - vec3d1.z * 0.25 + this.random.nextDouble() * 0.6 - 0.3, vec3d1.x, vec3d1.y, vec3d1.z);
        }
        if (!this.world.isClient) {
            this.setPosition(this.x, this.y, this.z);
            ++this.useCount;
            if (this.useCount > 80 && !this.world.isClient) {
                this.playSound(SoundEvents.cD, 1.0f, 1.0f);
                this.remove();
                if (this.g) {
                    this.world.spawnEntity(new ItemEntity(this.world, this.x, this.y, this.z, this.getStack()));
                }
                else {
                    this.world.playLevelEvent(2003, new BlockPos(this), 0);
                }
            }
        }
    }
    
    public void writeCustomDataToTag(final CompoundTag tag) {
        final ItemStack itemStack2 = this.i();
        if (!itemStack2.isEmpty()) {
            tag.put("Item", itemStack2.toTag(new CompoundTag()));
        }
    }
    
    public void readCustomDataFromTag(final CompoundTag tag) {
        final ItemStack itemStack2 = ItemStack.fromTag(tag.getCompound("Item"));
        this.b(itemStack2);
    }
    
    @Override
    public float getBrightnessAtEyes() {
        return 1.0f;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public int getLightmapCoordinates() {
        return 15728880;
    }
    
    @Override
    public boolean canPlayerAttack() {
        return false;
    }
    
    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }
    
    static {
        b = DataTracker.<ItemStack>registerData(EnderEyeEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
    }
}

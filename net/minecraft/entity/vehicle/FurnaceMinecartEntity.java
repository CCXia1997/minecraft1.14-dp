package net.minecraft.entity.vehicle;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.item.Items;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.Direction;
import net.minecraft.block.FurnaceBlock;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.Entity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.item.ItemProvider;
import net.minecraft.block.Blocks;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.minecraft.recipe.Ingredient;
import net.minecraft.entity.data.TrackedData;

public class FurnaceMinecartEntity extends AbstractMinecartEntity
{
    private static final TrackedData<Boolean> LIT;
    private int fuel;
    public double pushX;
    public double pushZ;
    private static final Ingredient f;
    
    public FurnaceMinecartEntity(final EntityType<? extends FurnaceMinecartEntity> type, final World world) {
        super(type, world);
    }
    
    public FurnaceMinecartEntity(final World world, final double double2, final double double4, final double double6) {
        super(EntityType.FURNACE_MINECART, world, double2, double4, double6);
    }
    
    @Override
    public Type getMinecartType() {
        return Type.c;
    }
    
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.<Boolean>startTracking(FurnaceMinecartEntity.LIT, false);
    }
    
    @Override
    public void tick() {
        super.tick();
        if (this.fuel > 0) {
            --this.fuel;
        }
        if (this.fuel <= 0) {
            this.pushX = 0.0;
            this.pushZ = 0.0;
        }
        this.setLit(this.fuel > 0);
        if (this.isLit() && this.random.nextInt(4) == 0) {
            this.world.addParticle(ParticleTypes.J, this.x, this.y + 0.8, this.z, 0.0, 0.0, 0.0);
        }
    }
    
    @Override
    protected double f() {
        return 0.2;
    }
    
    @Override
    public void dropItems(final DamageSource damageSource) {
        super.dropItems(damageSource);
        if (!damageSource.isExplosive() && this.world.getGameRules().getBoolean("doEntityDrops")) {
            this.dropItem(Blocks.bW);
        }
    }
    
    @Override
    protected void b(final BlockPos blockPos, final BlockState blockState) {
        super.b(blockPos, blockState);
        double double3 = this.pushX * this.pushX + this.pushZ * this.pushZ;
        final Vec3d vec3d5 = this.getVelocity();
        if (double3 > 1.0E-4 && Entity.squaredHorizontalLength(vec3d5) > 0.001) {
            double3 = MathHelper.sqrt(double3);
            this.pushX /= double3;
            this.pushZ /= double3;
            if (this.pushX * vec3d5.x + this.pushZ * vec3d5.z < 0.0) {
                this.pushX = 0.0;
                this.pushZ = 0.0;
            }
            else {
                final double double4 = double3 / this.f();
                this.pushX *= double4;
                this.pushZ *= double4;
            }
        }
    }
    
    @Override
    protected void k() {
        double double1 = this.pushX * this.pushX + this.pushZ * this.pushZ;
        if (double1 > 1.0E-7) {
            double1 = MathHelper.sqrt(double1);
            this.pushX /= double1;
            this.pushZ /= double1;
            this.setVelocity(this.getVelocity().multiply(0.8, 0.0, 0.8).add(this.pushX, 0.0, this.pushZ));
        }
        else {
            this.setVelocity(this.getVelocity().multiply(0.98, 0.0, 0.98));
        }
        super.k();
    }
    
    @Override
    public boolean interact(final PlayerEntity player, final Hand hand) {
        final ItemStack itemStack3 = player.getStackInHand(hand);
        if (FurnaceMinecartEntity.f.a(itemStack3) && this.fuel + 3600 <= 32000) {
            if (!player.abilities.creativeMode) {
                itemStack3.subtractAmount(1);
            }
            this.fuel += 3600;
        }
        this.pushX = this.x - player.x;
        this.pushZ = this.z - player.z;
        return true;
    }
    
    @Override
    protected void writeCustomDataToTag(final CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putDouble("PushX", this.pushX);
        tag.putDouble("PushZ", this.pushZ);
        tag.putShort("Fuel", (short)this.fuel);
    }
    
    @Override
    protected void readCustomDataFromTag(final CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        this.pushX = tag.getDouble("PushX");
        this.pushZ = tag.getDouble("PushZ");
        this.fuel = tag.getShort("Fuel");
    }
    
    protected boolean isLit() {
        return this.dataTracker.<Boolean>get(FurnaceMinecartEntity.LIT);
    }
    
    protected void setLit(final boolean boolean1) {
        this.dataTracker.<Boolean>set(FurnaceMinecartEntity.LIT, boolean1);
    }
    
    @Override
    public BlockState getDefaultContainedBlock() {
        return (((AbstractPropertyContainer<O, BlockState>)Blocks.bW.getDefaultState()).with((Property<Comparable>)FurnaceBlock.FACING, Direction.NORTH)).<Comparable, Boolean>with((Property<Comparable>)FurnaceBlock.LIT, this.isLit());
    }
    
    static {
        LIT = DataTracker.<Boolean>registerData(FurnaceMinecartEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        f = Ingredient.ofItems(Items.jh, Items.ji);
    }
}

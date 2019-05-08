package net.minecraft.entity;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.client.network.packet.EntitySpawnS2CPacket;
import net.minecraft.network.Packet;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.TagHelper;
import java.util.List;
import net.minecraft.block.AnvilBlock;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.tag.BlockTags;
import com.google.common.collect.Lists;
import net.minecraft.util.math.MathHelper;
import net.minecraft.nbt.Tag;
import java.util.Iterator;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.block.Block;
import net.minecraft.item.ItemProvider;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.FallingBlock;
import net.minecraft.fluid.Fluids;
import net.minecraft.state.property.Property;
import net.minecraft.state.property.Properties;
import net.minecraft.world.ViewableWorld;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.AutomaticItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.RayTraceContext;
import net.minecraft.tag.FluidTags;
import net.minecraft.block.ConcretePowderBlock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Vec3d;
import net.minecraft.block.Blocks;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.block.BlockState;

public class FallingBlockEntity extends Entity
{
    private BlockState block;
    public int timeFalling;
    public boolean dropItem;
    private boolean destroyedOnLanding;
    private boolean hurtEntities;
    private int fallHurtMax;
    private float fallHurtAmount;
    public CompoundTag blockEntityData;
    protected static final TrackedData<BlockPos> BLOCK_POS;
    
    public FallingBlockEntity(final EntityType<? extends FallingBlockEntity> type, final World world) {
        super(type, world);
        this.block = Blocks.C.getDefaultState();
        this.dropItem = true;
        this.fallHurtMax = 40;
        this.fallHurtAmount = 2.0f;
    }
    
    public FallingBlockEntity(final World world, final double x, final double y, final double double6, final BlockState blockState8) {
        this(EntityType.FALLING_BLOCK, world);
        this.block = blockState8;
        this.i = true;
        this.setPosition(x, y + (1.0f - this.getHeight()) / 2.0f, double6);
        this.setVelocity(Vec3d.ZERO);
        this.prevX = x;
        this.prevY = y;
        this.prevZ = double6;
        this.setFallingBlockPos(new BlockPos(this));
    }
    
    @Override
    public boolean canPlayerAttack() {
        return false;
    }
    
    public void setFallingBlockPos(final BlockPos blockPos) {
        this.dataTracker.<BlockPos>set(FallingBlockEntity.BLOCK_POS, blockPos);
    }
    
    @Environment(EnvType.CLIENT)
    public BlockPos getFallingBlockPos() {
        return this.dataTracker.<BlockPos>get(FallingBlockEntity.BLOCK_POS);
    }
    
    @Override
    protected boolean canClimb() {
        return false;
    }
    
    @Override
    protected void initDataTracker() {
        this.dataTracker.<BlockPos>startTracking(FallingBlockEntity.BLOCK_POS, BlockPos.ORIGIN);
    }
    
    @Override
    public boolean collides() {
        return !this.removed;
    }
    
    @Override
    public void tick() {
        if (this.block.isAir()) {
            this.remove();
            return;
        }
        this.prevX = this.x;
        this.prevY = this.y;
        this.prevZ = this.z;
        final Block block1 = this.block.getBlock();
        if (this.timeFalling++ == 0) {
            final BlockPos blockPos2 = new BlockPos(this);
            if (this.world.getBlockState(blockPos2).getBlock() == block1) {
                this.world.clearBlockState(blockPos2, false);
            }
            else if (!this.world.isClient) {
                this.remove();
                return;
            }
        }
        if (!this.isUnaffectedByGravity()) {
            this.setVelocity(this.getVelocity().add(0.0, -0.04, 0.0));
        }
        this.move(MovementType.a, this.getVelocity());
        if (!this.world.isClient) {
            BlockPos blockPos2 = new BlockPos(this);
            final boolean boolean3 = this.block.getBlock() instanceof ConcretePowderBlock;
            boolean boolean4 = boolean3 && this.world.getFluidState(blockPos2).matches(FluidTags.a);
            final double double5 = this.getVelocity().lengthSquared();
            if (boolean3 && double5 > 1.0) {
                final BlockHitResult blockHitResult7 = this.world.rayTrace(new RayTraceContext(new Vec3d(this.prevX, this.prevY, this.prevZ), new Vec3d(this.x, this.y, this.z), RayTraceContext.ShapeType.a, RayTraceContext.FluidHandling.b, this));
                if (blockHitResult7.getType() != HitResult.Type.NONE && this.world.getFluidState(blockHitResult7.getBlockPos()).matches(FluidTags.a)) {
                    blockPos2 = blockHitResult7.getBlockPos();
                    boolean4 = true;
                }
            }
            if (this.onGround || boolean4) {
                final BlockState blockState7 = this.world.getBlockState(blockPos2);
                this.setVelocity(this.getVelocity().multiply(0.7, -0.5, 0.7));
                if (blockState7.getBlock() != Blocks.bn) {
                    this.remove();
                    if (!this.destroyedOnLanding) {
                        if (boolean4 || (blockState7.canReplace(new AutomaticItemPlacementContext(this.world, blockPos2, Direction.DOWN, ItemStack.EMPTY, Direction.UP)) && this.block.canPlaceAt(this.world, blockPos2))) {
                            if (this.block.<Comparable>contains((Property<Comparable>)Properties.WATERLOGGED) && this.world.getFluidState(blockPos2).getFluid() == Fluids.WATER) {
                                this.block = ((AbstractPropertyContainer<O, BlockState>)this.block).<Comparable, Boolean>with((Property<Comparable>)Properties.WATERLOGGED, true);
                            }
                            if (this.world.setBlockState(blockPos2, this.block, 3)) {
                                if (block1 instanceof FallingBlock) {
                                    ((FallingBlock)block1).onLanding(this.world, blockPos2, this.block, blockState7);
                                }
                                if (this.blockEntityData != null && block1 instanceof BlockEntityProvider) {
                                    final BlockEntity blockEntity8 = this.world.getBlockEntity(blockPos2);
                                    if (blockEntity8 != null) {
                                        final CompoundTag compoundTag9 = blockEntity8.toTag(new CompoundTag());
                                        for (final String string11 : this.blockEntityData.getKeys()) {
                                            final Tag tag12 = this.blockEntityData.getTag(string11);
                                            if (!"x".equals(string11) && !"y".equals(string11)) {
                                                if ("z".equals(string11)) {
                                                    continue;
                                                }
                                                compoundTag9.put(string11, tag12.copy());
                                            }
                                        }
                                        blockEntity8.fromTag(compoundTag9);
                                        blockEntity8.markDirty();
                                    }
                                }
                            }
                            else if (this.dropItem && this.world.getGameRules().getBoolean("doEntityDrops")) {
                                this.dropItem(block1);
                            }
                        }
                        else if (this.dropItem && this.world.getGameRules().getBoolean("doEntityDrops")) {
                            this.dropItem(block1);
                        }
                    }
                    else if (block1 instanceof FallingBlock) {
                        ((FallingBlock)block1).onDestroyedOnLanding(this.world, blockPos2);
                    }
                }
            }
            else if (!this.world.isClient && ((this.timeFalling > 100 && (blockPos2.getY() < 1 || blockPos2.getY() > 256)) || this.timeFalling > 600)) {
                if (this.dropItem && this.world.getGameRules().getBoolean("doEntityDrops")) {
                    this.dropItem(block1);
                }
                this.remove();
            }
        }
        this.setVelocity(this.getVelocity().multiply(0.98));
    }
    
    @Override
    public void handleFallDamage(final float fallDistance, final float damageMultiplier) {
        if (this.hurtEntities) {
            final int integer3 = MathHelper.ceil(fallDistance - 1.0f);
            if (integer3 > 0) {
                final List<Entity> list4 = Lists.newArrayList(this.world.getEntities(this, this.getBoundingBox()));
                final boolean boolean5 = this.block.matches(BlockTags.A);
                final DamageSource damageSource6 = boolean5 ? DamageSource.ANVIL : DamageSource.FALLING_BLOCK;
                for (final Entity entity8 : list4) {
                    entity8.damage(damageSource6, (float)Math.min(MathHelper.floor(integer3 * this.fallHurtAmount), this.fallHurtMax));
                }
                if (boolean5 && this.random.nextFloat() < 0.05000000074505806 + integer3 * 0.05) {
                    final BlockState blockState7 = AnvilBlock.getLandingState(this.block);
                    if (blockState7 == null) {
                        this.destroyedOnLanding = true;
                    }
                    else {
                        this.block = blockState7;
                    }
                }
            }
        }
    }
    
    @Override
    protected void writeCustomDataToTag(final CompoundTag tag) {
        tag.put("BlockState", TagHelper.serializeBlockState(this.block));
        tag.putInt("Time", this.timeFalling);
        tag.putBoolean("DropItem", this.dropItem);
        tag.putBoolean("HurtEntities", this.hurtEntities);
        tag.putFloat("FallHurtAmount", this.fallHurtAmount);
        tag.putInt("FallHurtMax", this.fallHurtMax);
        if (this.blockEntityData != null) {
            tag.put("TileEntityData", this.blockEntityData);
        }
    }
    
    @Override
    protected void readCustomDataFromTag(final CompoundTag tag) {
        this.block = TagHelper.deserializeBlockState(tag.getCompound("BlockState"));
        this.timeFalling = tag.getInt("Time");
        if (tag.containsKey("HurtEntities", 99)) {
            this.hurtEntities = tag.getBoolean("HurtEntities");
            this.fallHurtAmount = tag.getFloat("FallHurtAmount");
            this.fallHurtMax = tag.getInt("FallHurtMax");
        }
        else if (this.block.matches(BlockTags.A)) {
            this.hurtEntities = true;
        }
        if (tag.containsKey("DropItem", 99)) {
            this.dropItem = tag.getBoolean("DropItem");
        }
        if (tag.containsKey("TileEntityData", 10)) {
            this.blockEntityData = tag.getCompound("TileEntityData");
        }
        if (this.block.isAir()) {
            this.block = Blocks.C.getDefaultState();
        }
    }
    
    @Environment(EnvType.CLIENT)
    public World getWorldClient() {
        return this.world;
    }
    
    public void setHurtEntities(final boolean boolean1) {
        this.hurtEntities = boolean1;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public boolean doesRenderOnFire() {
        return false;
    }
    
    @Override
    public void populateCrashReport(final CrashReportSection crashReportSection) {
        super.populateCrashReport(crashReportSection);
        crashReportSection.add("Immitating BlockState", this.block.toString());
    }
    
    public BlockState getBlockState() {
        return this.block;
    }
    
    @Override
    public boolean bS() {
        return true;
    }
    
    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this, Block.getRawIdFromState(this.getBlockState()));
    }
    
    static {
        BLOCK_POS = DataTracker.<BlockPos>registerData(FallingBlockEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);
    }
}

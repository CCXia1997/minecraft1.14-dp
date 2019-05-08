package net.minecraft.block.entity;

import java.util.stream.IntStream;
import net.minecraft.inventory.Inventory;
import net.minecraft.container.ShulkerBoxContainer;
import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerInventory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;
import net.minecraft.block.Block;
import java.util.Iterator;
import net.minecraft.inventory.Inventories;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.IWorld;
import java.util.List;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.MovementType;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.state.property.Property;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.block.BlockState;
import javax.annotation.Nullable;
import net.minecraft.util.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Tickable;
import net.minecraft.inventory.SidedInventory;

public class ShulkerBoxBlockEntity extends LootableContainerBlockEntity implements SidedInventory, Tickable
{
    private static final int[] AVAILABLE_SLOTS;
    private DefaultedList<ItemStack> inventory;
    private int viewerCount;
    private AnimationStage animationStage;
    private float animationProgress;
    private float prevAnimationProgress;
    private DyeColor cachedColor;
    private boolean cachedColorUpdateNeeded;
    
    public ShulkerBoxBlockEntity(@Nullable final DyeColor dyeColor) {
        super(BlockEntityType.SHUlKER_BOX);
        this.inventory = DefaultedList.<ItemStack>create(27, ItemStack.EMPTY);
        this.animationStage = AnimationStage.CLOSED;
        this.cachedColor = dyeColor;
    }
    
    public ShulkerBoxBlockEntity() {
        this((DyeColor)null);
        this.cachedColorUpdateNeeded = true;
    }
    
    @Override
    public void tick() {
        this.updateAnimation();
        if (this.animationStage == AnimationStage.b || this.animationStage == AnimationStage.d) {
            this.pushEntities();
        }
    }
    
    protected void updateAnimation() {
        this.prevAnimationProgress = this.animationProgress;
        switch (this.animationStage) {
            case CLOSED: {
                this.animationProgress = 0.0f;
                break;
            }
            case b: {
                this.animationProgress += 0.1f;
                if (this.animationProgress >= 1.0f) {
                    this.pushEntities();
                    this.animationStage = AnimationStage.OPENED;
                    this.animationProgress = 1.0f;
                    this.updateNeighborStates();
                    break;
                }
                break;
            }
            case d: {
                this.animationProgress -= 0.1f;
                if (this.animationProgress <= 0.0f) {
                    this.animationStage = AnimationStage.CLOSED;
                    this.animationProgress = 0.0f;
                    this.updateNeighborStates();
                    break;
                }
                break;
            }
            case OPENED: {
                this.animationProgress = 1.0f;
                break;
            }
        }
    }
    
    public AnimationStage getAnimationStage() {
        return this.animationStage;
    }
    
    public BoundingBox getBoundingBox(final BlockState state) {
        return this.getBoundingBox(state.<Direction>get(ShulkerBoxBlock.FACING));
    }
    
    public BoundingBox getBoundingBox(final Direction openDirection) {
        final float float2 = this.getAnimationProgress(1.0f);
        return VoxelShapes.fullCube().getBoundingBox().stretch(0.5f * float2 * openDirection.getOffsetX(), 0.5f * float2 * openDirection.getOffsetY(), 0.5f * float2 * openDirection.getOffsetZ());
    }
    
    private BoundingBox getCollisionBox(final Direction facing) {
        final Direction direction2 = facing.getOpposite();
        return this.getBoundingBox(facing).shrink(direction2.getOffsetX(), direction2.getOffsetY(), direction2.getOffsetZ());
    }
    
    private void pushEntities() {
        final BlockState blockState1 = this.world.getBlockState(this.getPos());
        if (!(blockState1.getBlock() instanceof ShulkerBoxBlock)) {
            return;
        }
        final Direction direction2 = blockState1.<Direction>get(ShulkerBoxBlock.FACING);
        final BoundingBox boundingBox3 = this.getCollisionBox(direction2).offset(this.pos);
        final List<Entity> list4 = this.world.getEntities((Entity)null, boundingBox3);
        if (list4.isEmpty()) {
            return;
        }
        for (int integer5 = 0; integer5 < list4.size(); ++integer5) {
            final Entity entity6 = list4.get(integer5);
            if (entity6.getPistonBehavior() != PistonBehavior.d) {
                double double7 = 0.0;
                double double8 = 0.0;
                double double9 = 0.0;
                final BoundingBox boundingBox4 = entity6.getBoundingBox();
                switch (direction2.getAxis()) {
                    case X: {
                        if (direction2.getDirection() == Direction.AxisDirection.POSITIVE) {
                            double7 = boundingBox3.maxX - boundingBox4.minX;
                        }
                        else {
                            double7 = boundingBox4.maxX - boundingBox3.minX;
                        }
                        double7 += 0.01;
                        break;
                    }
                    case Y: {
                        if (direction2.getDirection() == Direction.AxisDirection.POSITIVE) {
                            double8 = boundingBox3.maxY - boundingBox4.minY;
                        }
                        else {
                            double8 = boundingBox4.maxY - boundingBox3.minY;
                        }
                        double8 += 0.01;
                        break;
                    }
                    case Z: {
                        if (direction2.getDirection() == Direction.AxisDirection.POSITIVE) {
                            double9 = boundingBox3.maxZ - boundingBox4.minZ;
                        }
                        else {
                            double9 = boundingBox4.maxZ - boundingBox3.minZ;
                        }
                        double9 += 0.01;
                        break;
                    }
                }
                entity6.move(MovementType.d, new Vec3d(double7 * direction2.getOffsetX(), double8 * direction2.getOffsetY(), double9 * direction2.getOffsetZ()));
            }
        }
    }
    
    @Override
    public int getInvSize() {
        return this.inventory.size();
    }
    
    @Override
    public boolean onBlockAction(final int integer1, final int integer2) {
        if (integer1 == 1) {
            if ((this.viewerCount = integer2) == 0) {
                this.animationStage = AnimationStage.d;
                this.updateNeighborStates();
            }
            if (integer2 == 1) {
                this.animationStage = AnimationStage.b;
                this.updateNeighborStates();
            }
            return true;
        }
        return super.onBlockAction(integer1, integer2);
    }
    
    private void updateNeighborStates() {
        this.getCachedState().updateNeighborStates(this.getWorld(), this.getPos(), 3);
    }
    
    @Override
    public void onInvOpen(final PlayerEntity playerEntity) {
        if (!playerEntity.isSpectator()) {
            if (this.viewerCount < 0) {
                this.viewerCount = 0;
            }
            ++this.viewerCount;
            this.world.addBlockAction(this.pos, this.getCachedState().getBlock(), 1, this.viewerCount);
            if (this.viewerCount == 1) {
                this.world.playSound(null, this.pos, SoundEvents.jZ, SoundCategory.e, 0.5f, this.world.random.nextFloat() * 0.1f + 0.9f);
            }
        }
    }
    
    @Override
    public void onInvClose(final PlayerEntity playerEntity) {
        if (!playerEntity.isSpectator()) {
            --this.viewerCount;
            this.world.addBlockAction(this.pos, this.getCachedState().getBlock(), 1, this.viewerCount);
            if (this.viewerCount <= 0) {
                this.world.playSound(null, this.pos, SoundEvents.jY, SoundCategory.e, 0.5f, this.world.random.nextFloat() * 0.1f + 0.9f);
            }
        }
    }
    
    @Override
    protected TextComponent getContainerName() {
        return new TranslatableTextComponent("container.shulkerBox", new Object[0]);
    }
    
    @Override
    public void fromTag(final CompoundTag compoundTag) {
        super.fromTag(compoundTag);
        this.deserializeInventory(compoundTag);
    }
    
    @Override
    public CompoundTag toTag(final CompoundTag compoundTag) {
        super.toTag(compoundTag);
        return this.serializeInventory(compoundTag);
    }
    
    public void deserializeInventory(final CompoundTag tag) {
        this.inventory = DefaultedList.<ItemStack>create(this.getInvSize(), ItemStack.EMPTY);
        if (!this.deserializeLootTable(tag) && tag.containsKey("Items", 9)) {
            Inventories.fromTag(tag, this.inventory);
        }
    }
    
    public CompoundTag serializeInventory(final CompoundTag tag) {
        if (!this.serializeLootTable(tag)) {
            Inventories.toTag(tag, this.inventory, false);
        }
        return tag;
    }
    
    @Override
    protected DefaultedList<ItemStack> getInvStackList() {
        return this.inventory;
    }
    
    @Override
    protected void setInvStackList(final DefaultedList<ItemStack> list) {
        this.inventory = list;
    }
    
    @Override
    public boolean isInvEmpty() {
        for (final ItemStack itemStack2 : this.inventory) {
            if (!itemStack2.isEmpty()) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int[] getInvAvailableSlots(final Direction side) {
        return ShulkerBoxBlockEntity.AVAILABLE_SLOTS;
    }
    
    @Override
    public boolean canInsertInvStack(final int slot, final ItemStack stack, @Nullable final Direction direction) {
        return !(Block.getBlockFromItem(stack.getItem()) instanceof ShulkerBoxBlock);
    }
    
    @Override
    public boolean canExtractInvStack(final int slot, final ItemStack stack, final Direction direction) {
        return true;
    }
    
    public float getAnimationProgress(final float float1) {
        return MathHelper.lerp(float1, this.prevAnimationProgress, this.animationProgress);
    }
    
    @Environment(EnvType.CLIENT)
    public DyeColor getColor() {
        if (this.cachedColorUpdateNeeded) {
            this.cachedColor = ShulkerBoxBlock.getColor(this.getCachedState().getBlock());
            this.cachedColorUpdateNeeded = false;
        }
        return this.cachedColor;
    }
    
    @Override
    protected Container createContainer(final int integer, final PlayerInventory playerInventory) {
        return new ShulkerBoxContainer(integer, playerInventory, this);
    }
    
    static {
        AVAILABLE_SLOTS = IntStream.range(0, 27).toArray();
    }
    
    public enum AnimationStage
    {
        CLOSED, 
        b, 
        OPENED, 
        d;
    }
}

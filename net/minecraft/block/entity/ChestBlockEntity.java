package net.minecraft.block.entity;

import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;
import net.minecraft.block.Block;
import net.minecraft.util.math.Direction;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.property.Property;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.enums.ChestType;
import net.minecraft.sound.SoundEvent;
import java.util.List;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.DoubleInventory;
import net.minecraft.container.GenericContainer;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.sound.SoundEvents;
import net.minecraft.inventory.Inventories;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.text.TextComponent;
import java.util.Iterator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.EnvironmentInterface;
import net.fabricmc.api.EnvironmentInterfaces;
import net.minecraft.util.Tickable;
import net.minecraft.client.block.ChestAnimationProgress;

@EnvironmentInterfaces({ @EnvironmentInterface(value = EnvType.CLIENT, itf = buf.class) })
public class ChestBlockEntity extends LootableContainerBlockEntity implements ChestAnimationProgress, Tickable
{
    private DefaultedList<ItemStack> inventory;
    protected float animationAngle;
    protected float lastAnimationAngle;
    protected int viewerCount;
    private int ticksOpen;
    
    protected ChestBlockEntity(final BlockEntityType<?> blockEntityType) {
        super(blockEntityType);
        this.inventory = DefaultedList.<ItemStack>create(27, ItemStack.EMPTY);
    }
    
    public ChestBlockEntity() {
        this(BlockEntityType.CHEST);
    }
    
    @Override
    public int getInvSize() {
        return 27;
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
    protected TextComponent getContainerName() {
        return new TranslatableTextComponent("container.chest", new Object[0]);
    }
    
    @Override
    public void fromTag(final CompoundTag compoundTag) {
        super.fromTag(compoundTag);
        this.inventory = DefaultedList.<ItemStack>create(this.getInvSize(), ItemStack.EMPTY);
        if (!this.deserializeLootTable(compoundTag)) {
            Inventories.fromTag(compoundTag, this.inventory);
        }
    }
    
    @Override
    public CompoundTag toTag(final CompoundTag compoundTag) {
        super.toTag(compoundTag);
        if (!this.serializeLootTable(compoundTag)) {
            Inventories.toTag(compoundTag, this.inventory);
        }
        return compoundTag;
    }
    
    @Override
    public void tick() {
        final int integer1 = this.pos.getX();
        final int integer2 = this.pos.getY();
        final int integer3 = this.pos.getZ();
        ++this.ticksOpen;
        this.viewerCount = tickViewerCount(this.world, this, this.ticksOpen, integer1, integer2, integer3, this.viewerCount);
        this.lastAnimationAngle = this.animationAngle;
        final float float4 = 0.1f;
        if (this.viewerCount > 0 && this.animationAngle == 0.0f) {
            this.playSound(SoundEvents.aO);
        }
        if ((this.viewerCount == 0 && this.animationAngle > 0.0f) || (this.viewerCount > 0 && this.animationAngle < 1.0f)) {
            final float float5 = this.animationAngle;
            if (this.viewerCount > 0) {
                this.animationAngle += 0.1f;
            }
            else {
                this.animationAngle -= 0.1f;
            }
            if (this.animationAngle > 1.0f) {
                this.animationAngle = 1.0f;
            }
            final float float6 = 0.5f;
            if (this.animationAngle < 0.5f && float5 >= 0.5f) {
                this.playSound(SoundEvents.aM);
            }
            if (this.animationAngle < 0.0f) {
                this.animationAngle = 0.0f;
            }
        }
    }
    
    public static int tickViewerCount(final World world, final LockableContainerBlockEntity blockEntity, final int ticksOpen, final int x, final int y, final int z, int viewerCount) {
        if (!world.isClient && viewerCount != 0 && (ticksOpen + x + y + z) % 200 == 0) {
            viewerCount = countViewers(world, blockEntity, x, y, z);
        }
        return viewerCount;
    }
    
    public static int countViewers(final World world, final LockableContainerBlockEntity container, final int ticksOpen, final int x, final int y) {
        int integer6 = 0;
        final float float7 = 5.0f;
        final List<PlayerEntity> list8 = world.<PlayerEntity>getEntities(PlayerEntity.class, new BoundingBox(ticksOpen - 5.0f, x - 5.0f, y - 5.0f, ticksOpen + 1 + 5.0f, x + 1 + 5.0f, y + 1 + 5.0f));
        for (final PlayerEntity playerEntity10 : list8) {
            if (playerEntity10.container instanceof GenericContainer) {
                final Inventory inventory11 = ((GenericContainer)playerEntity10.container).getInventory();
                if (inventory11 != container && (!(inventory11 instanceof DoubleInventory) || !((DoubleInventory)inventory11).isPart(container))) {
                    continue;
                }
                ++integer6;
            }
        }
        return integer6;
    }
    
    private void playSound(final SoundEvent soundEvent) {
        final ChestType chestType2 = this.getCachedState().<ChestType>get(ChestBlock.CHEST_TYPE);
        if (chestType2 == ChestType.b) {
            return;
        }
        double double3 = this.pos.getX() + 0.5;
        final double double4 = this.pos.getY() + 0.5;
        double double5 = this.pos.getZ() + 0.5;
        if (chestType2 == ChestType.c) {
            final Direction direction9 = ChestBlock.getFacing(this.getCachedState());
            double3 += direction9.getOffsetX() * 0.5;
            double5 += direction9.getOffsetZ() * 0.5;
        }
        this.world.playSound(null, double3, double4, double5, soundEvent, SoundCategory.e, 0.5f, this.world.random.nextFloat() * 0.1f + 0.9f);
    }
    
    @Override
    public boolean onBlockAction(final int integer1, final int integer2) {
        if (integer1 == 1) {
            this.viewerCount = integer2;
            return true;
        }
        return super.onBlockAction(integer1, integer2);
    }
    
    @Override
    public void onInvOpen(final PlayerEntity playerEntity) {
        if (!playerEntity.isSpectator()) {
            if (this.viewerCount < 0) {
                this.viewerCount = 0;
            }
            ++this.viewerCount;
            this.onInvOpenOrClose();
        }
    }
    
    @Override
    public void onInvClose(final PlayerEntity playerEntity) {
        if (!playerEntity.isSpectator()) {
            --this.viewerCount;
            this.onInvOpenOrClose();
        }
    }
    
    protected void onInvOpenOrClose() {
        final Block block1 = this.getCachedState().getBlock();
        if (block1 instanceof ChestBlock) {
            this.world.addBlockAction(this.pos, block1, 1, this.viewerCount);
            this.world.updateNeighborsAlways(this.pos, block1);
        }
    }
    
    @Override
    protected DefaultedList<ItemStack> getInvStackList() {
        return this.inventory;
    }
    
    @Override
    protected void setInvStackList(final DefaultedList<ItemStack> list) {
        this.inventory = list;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public float getAnimationProgress(final float float1) {
        return MathHelper.lerp(float1, this.lastAnimationAngle, this.animationAngle);
    }
    
    public static int getPlayersLookingInChestCount(final BlockView world, final BlockPos pos) {
        final BlockState blockState3 = world.getBlockState(pos);
        if (blockState3.getBlock().hasBlockEntity()) {
            final BlockEntity blockEntity4 = world.getBlockEntity(pos);
            if (blockEntity4 instanceof ChestBlockEntity) {
                return ((ChestBlockEntity)blockEntity4).viewerCount;
            }
        }
        return 0;
    }
    
    public static void copyInventory(final ChestBlockEntity from, final ChestBlockEntity to) {
        final DefaultedList<ItemStack> defaultedList3 = from.getInvStackList();
        from.setInvStackList(to.getInvStackList());
        to.setInvStackList(defaultedList3);
    }
    
    @Override
    protected Container createContainer(final int integer, final PlayerInventory playerInventory) {
        return GenericContainer.createGeneric9x3(integer, playerInventory, this);
    }
}

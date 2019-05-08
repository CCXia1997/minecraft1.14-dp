package net.minecraft.block.entity;

import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.entity.player.PlayerEntity;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.TextComponent;
import net.minecraft.container.ContainerLock;
import net.minecraft.util.Nameable;
import net.minecraft.container.NameableContainerProvider;
import net.minecraft.inventory.Inventory;

public abstract class LockableContainerBlockEntity extends BlockEntity implements Inventory, NameableContainerProvider, Nameable
{
    private ContainerLock lock;
    private TextComponent customName;
    
    protected LockableContainerBlockEntity(final BlockEntityType<?> blockEntityType) {
        super(blockEntityType);
        this.lock = ContainerLock.NONE;
    }
    
    @Override
    public void fromTag(final CompoundTag compoundTag) {
        super.fromTag(compoundTag);
        this.lock = ContainerLock.deserialize(compoundTag);
        if (compoundTag.containsKey("CustomName", 8)) {
            this.customName = TextComponent.Serializer.fromJsonString(compoundTag.getString("CustomName"));
        }
    }
    
    @Override
    public CompoundTag toTag(final CompoundTag compoundTag) {
        super.toTag(compoundTag);
        this.lock.serialize(compoundTag);
        if (this.customName != null) {
            compoundTag.putString("CustomName", TextComponent.Serializer.toJsonString(this.customName));
        }
        return compoundTag;
    }
    
    public void setCustomName(final TextComponent textComponent) {
        this.customName = textComponent;
    }
    
    @Override
    public TextComponent getName() {
        if (this.customName != null) {
            return this.customName;
        }
        return this.getContainerName();
    }
    
    @Override
    public TextComponent getDisplayName() {
        return this.getName();
    }
    
    @Nullable
    @Override
    public TextComponent getCustomName() {
        return this.customName;
    }
    
    protected abstract TextComponent getContainerName();
    
    public boolean checkUnlocked(final PlayerEntity player) {
        return checkUnlocked(player, this.lock, this.getDisplayName());
    }
    
    public static boolean checkUnlocked(final PlayerEntity player, final ContainerLock lock, final TextComponent containerName) {
        if (player.isSpectator() || lock.isEmpty(player.getMainHandStack())) {
            return true;
        }
        player.addChatMessage(new TranslatableTextComponent("container.isLocked", new Object[] { containerName }), true);
        player.playSound(SoundEvents.aN, SoundCategory.e, 1.0f, 1.0f);
        return false;
    }
    
    @Nullable
    @Override
    public Container createMenu(final int syncId, final PlayerInventory playerInventory, final PlayerEntity playerEntity) {
        if (this.checkUnlocked(playerEntity)) {
            return this.createContainer(syncId, playerInventory);
        }
        return null;
    }
    
    protected abstract Container createContainer(final int arg1, final PlayerInventory arg2);
}

package net.minecraft.block.entity;

import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.Direction;
import net.minecraft.block.Block;
import net.minecraft.world.ViewableWorld;
import net.minecraft.block.CommandBlock;
import javax.annotation.Nullable;
import net.minecraft.client.network.packet.BlockEntityUpdateS2CPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.util.math.Vec2f;
import net.minecraft.server.command.ServerCommandSource;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Vec3d;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.CommandBlockExecutor;

public class CommandBlockBlockEntity extends BlockEntity
{
    private boolean powered;
    private boolean auto;
    private boolean conditionMet;
    private boolean needsUpdatePacket;
    private final CommandBlockExecutor commandExecutor;
    
    public CommandBlockBlockEntity() {
        super(BlockEntityType.COMMAND_BLOCK);
        this.commandExecutor = new CommandBlockExecutor() {
            @Override
            public void setCommand(final String string) {
                super.setCommand(string);
                CommandBlockBlockEntity.this.markDirty();
            }
            
            @Override
            public ServerWorld getWorld() {
                return (ServerWorld)CommandBlockBlockEntity.this.world;
            }
            
            @Override
            public void markDirty() {
                final BlockState blockState1 = CommandBlockBlockEntity.this.world.getBlockState(CommandBlockBlockEntity.this.pos);
                this.getWorld().updateListeners(CommandBlockBlockEntity.this.pos, blockState1, blockState1, 3);
            }
            
            @Environment(EnvType.CLIENT)
            @Override
            public Vec3d getPos() {
                return new Vec3d(CommandBlockBlockEntity.this.pos.getX() + 0.5, CommandBlockBlockEntity.this.pos.getY() + 0.5, CommandBlockBlockEntity.this.pos.getZ() + 0.5);
            }
            
            @Override
            public ServerCommandSource getSource() {
                return new ServerCommandSource(this, new Vec3d(CommandBlockBlockEntity.this.pos.getX() + 0.5, CommandBlockBlockEntity.this.pos.getY() + 0.5, CommandBlockBlockEntity.this.pos.getZ() + 0.5), Vec2f.ZERO, this.getWorld(), 2, this.getCustomName().getString(), this.getCustomName(), this.getWorld().getServer(), null);
            }
        };
    }
    
    @Override
    public CompoundTag toTag(final CompoundTag compoundTag) {
        super.toTag(compoundTag);
        this.commandExecutor.serialize(compoundTag);
        compoundTag.putBoolean("powered", this.isPowered());
        compoundTag.putBoolean("conditionMet", this.isConditionMet());
        compoundTag.putBoolean("auto", this.isAuto());
        return compoundTag;
    }
    
    @Override
    public void fromTag(final CompoundTag compoundTag) {
        super.fromTag(compoundTag);
        this.commandExecutor.deserialize(compoundTag);
        this.powered = compoundTag.getBoolean("powered");
        this.conditionMet = compoundTag.getBoolean("conditionMet");
        this.setAuto(compoundTag.getBoolean("auto"));
    }
    
    @Nullable
    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        if (this.needsUpdatePacket()) {
            this.setNeedsUpdatePacket(false);
            final CompoundTag compoundTag1 = this.toTag(new CompoundTag());
            return new BlockEntityUpdateS2CPacket(this.pos, 2, compoundTag1);
        }
        return null;
    }
    
    @Override
    public boolean shouldNotCopyTagFromItem() {
        return true;
    }
    
    public CommandBlockExecutor getCommandExecutor() {
        return this.commandExecutor;
    }
    
    public void setPowered(final boolean boolean1) {
        this.powered = boolean1;
    }
    
    public boolean isPowered() {
        return this.powered;
    }
    
    public boolean isAuto() {
        return this.auto;
    }
    
    public void setAuto(final boolean boolean1) {
        final boolean boolean2 = this.auto;
        this.auto = boolean1;
        if (!boolean2 && boolean1 && !this.powered && this.world != null && this.getType() != Type.a) {
            final Block block3 = this.getCachedState().getBlock();
            if (block3 instanceof CommandBlock) {
                this.updateConditionMet();
                this.world.getBlockTickScheduler().schedule(this.pos, block3, block3.getTickRate(this.world));
            }
        }
    }
    
    public boolean isConditionMet() {
        return this.conditionMet;
    }
    
    public boolean updateConditionMet() {
        this.conditionMet = true;
        if (this.isConditionalCommandBlock()) {
            final BlockPos blockPos1 = this.pos.offset(this.world.getBlockState(this.pos).<Direction>get((Property<Direction>)CommandBlock.FACING).getOpposite());
            if (this.world.getBlockState(blockPos1).getBlock() instanceof CommandBlock) {
                final BlockEntity blockEntity2 = this.world.getBlockEntity(blockPos1);
                this.conditionMet = (blockEntity2 instanceof CommandBlockBlockEntity && ((CommandBlockBlockEntity)blockEntity2).getCommandExecutor().getSuccessCount() > 0);
            }
            else {
                this.conditionMet = false;
            }
        }
        return this.conditionMet;
    }
    
    public boolean needsUpdatePacket() {
        return this.needsUpdatePacket;
    }
    
    public void setNeedsUpdatePacket(final boolean needsUpdatePacket) {
        this.needsUpdatePacket = needsUpdatePacket;
    }
    
    public Type getType() {
        final Block block1 = this.getCachedState().getBlock();
        if (block1 == Blocks.ej) {
            return Type.c;
        }
        if (block1 == Blocks.iy) {
            return Type.b;
        }
        if (block1 == Blocks.iz) {
            return Type.a;
        }
        return Type.c;
    }
    
    public boolean isConditionalCommandBlock() {
        final BlockState blockState1 = this.world.getBlockState(this.getPos());
        return blockState1.getBlock() instanceof CommandBlock && blockState1.<Boolean>get((Property<Boolean>)CommandBlock.CONDITIONAL);
    }
    
    @Override
    public void validate() {
        this.resetBlock();
        super.validate();
    }
    
    public enum Type
    {
        a, 
        b, 
        c;
    }
}

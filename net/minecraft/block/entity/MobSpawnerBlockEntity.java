package net.minecraft.block.entity;

import javax.annotation.Nullable;
import net.minecraft.client.network.packet.BlockEntityUpdateS2CPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.block.BlockState;
import net.minecraft.world.MobSpawnerEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.block.Blocks;
import net.minecraft.world.MobSpawnerLogic;
import net.minecraft.util.Tickable;

public class MobSpawnerBlockEntity extends BlockEntity implements Tickable
{
    private final MobSpawnerLogic logic;
    
    public MobSpawnerBlockEntity() {
        super(BlockEntityType.MOB_SPAWNER);
        this.logic = new MobSpawnerLogic() {
            @Override
            public void a(final int integer) {
                MobSpawnerBlockEntity.this.world.addBlockAction(MobSpawnerBlockEntity.this.pos, Blocks.bN, integer, 0);
            }
            
            @Override
            public World getWorld() {
                return MobSpawnerBlockEntity.this.world;
            }
            
            @Override
            public BlockPos getPos() {
                return MobSpawnerBlockEntity.this.pos;
            }
            
            @Override
            public void setSpawnEntry(final MobSpawnerEntry mobSpawnerEntry) {
                super.setSpawnEntry(mobSpawnerEntry);
                if (this.getWorld() != null) {
                    final BlockState blockState2 = this.getWorld().getBlockState(this.getPos());
                    this.getWorld().updateListeners(MobSpawnerBlockEntity.this.pos, blockState2, blockState2, 4);
                }
            }
        };
    }
    
    @Override
    public void fromTag(final CompoundTag compoundTag) {
        super.fromTag(compoundTag);
        this.logic.deserialize(compoundTag);
    }
    
    @Override
    public CompoundTag toTag(final CompoundTag compoundTag) {
        super.toTag(compoundTag);
        this.logic.serialize(compoundTag);
        return compoundTag;
    }
    
    @Override
    public void tick() {
        this.logic.update();
    }
    
    @Nullable
    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return new BlockEntityUpdateS2CPacket(this.pos, 1, this.toInitialChunkDataTag());
    }
    
    @Override
    public CompoundTag toInitialChunkDataTag() {
        final CompoundTag compoundTag1 = this.toTag(new CompoundTag());
        compoundTag1.remove("SpawnPotentials");
        return compoundTag1;
    }
    
    @Override
    public boolean onBlockAction(final int integer1, final int integer2) {
        return this.logic.b(integer1) || super.onBlockAction(integer1, integer2);
    }
    
    @Override
    public boolean shouldNotCopyTagFromItem() {
        return true;
    }
    
    public MobSpawnerLogic getLogic() {
        return this.logic;
    }
}

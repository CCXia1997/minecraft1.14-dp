package net.minecraft.entity.vehicle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.block.Blocks;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.minecraft.world.MobSpawnerLogic;

public class MobSpawnerMinecartEntity extends AbstractMinecartEntity
{
    private final MobSpawnerLogic logic;
    
    public MobSpawnerMinecartEntity(final EntityType<? extends MobSpawnerMinecartEntity> type, final World world) {
        super(type, world);
        this.logic = new MobSpawnerLogic() {
            @Override
            public void a(final int integer) {
                MobSpawnerMinecartEntity.this.world.sendEntityStatus(MobSpawnerMinecartEntity.this, (byte)integer);
            }
            
            @Override
            public World getWorld() {
                return MobSpawnerMinecartEntity.this.world;
            }
            
            @Override
            public BlockPos getPos() {
                return new BlockPos(MobSpawnerMinecartEntity.this);
            }
        };
    }
    
    public MobSpawnerMinecartEntity(final World world, final double double2, final double double4, final double double6) {
        super(EntityType.SPAWNER_MINECART, world, double2, double4, double6);
        this.logic = new MobSpawnerLogic() {
            @Override
            public void a(final int integer) {
                MobSpawnerMinecartEntity.this.world.sendEntityStatus(MobSpawnerMinecartEntity.this, (byte)integer);
            }
            
            @Override
            public World getWorld() {
                return MobSpawnerMinecartEntity.this.world;
            }
            
            @Override
            public BlockPos getPos() {
                return new BlockPos(MobSpawnerMinecartEntity.this);
            }
        };
    }
    
    @Override
    public Type getMinecartType() {
        return Type.e;
    }
    
    @Override
    public BlockState getDefaultContainedBlock() {
        return Blocks.bN.getDefaultState();
    }
    
    @Override
    protected void readCustomDataFromTag(final CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        this.logic.deserialize(tag);
    }
    
    @Override
    protected void writeCustomDataToTag(final CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        this.logic.serialize(tag);
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void handleStatus(final byte status) {
        this.logic.b(status);
    }
    
    @Override
    public void tick() {
        super.tick();
        this.logic.update();
    }
    
    @Override
    public boolean bS() {
        return true;
    }
}

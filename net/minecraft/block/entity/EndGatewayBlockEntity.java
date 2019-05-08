package net.minecraft.block.entity;

import org.apache.logging.log4j.LogManager;
import net.minecraft.util.math.Direction;
import net.minecraft.world.gen.feature.EndGatewayFeatureConfig;
import java.util.Iterator;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Block;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.FeatureConfig;
import java.util.Random;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.dimension.TheEndDimension;
import javax.annotation.Nullable;
import net.minecraft.client.network.packet.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.MathHelper;
import java.util.List;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.entity.Entity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.Tag;
import net.minecraft.util.TagHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.Logger;
import net.minecraft.util.Tickable;

public class EndGatewayBlockEntity extends EndPortalBlockEntity implements Tickable
{
    private static final Logger LOGGER;
    private long age;
    private int teleportCooldown;
    private BlockPos exitPortalPos;
    private boolean exactTeleport;
    
    public EndGatewayBlockEntity() {
        super(BlockEntityType.END_GATEWAY);
    }
    
    @Override
    public CompoundTag toTag(final CompoundTag compoundTag) {
        super.toTag(compoundTag);
        compoundTag.putLong("Age", this.age);
        if (this.exitPortalPos != null) {
            compoundTag.put("ExitPortal", TagHelper.serializeBlockPos(this.exitPortalPos));
        }
        if (this.exactTeleport) {
            compoundTag.putBoolean("ExactTeleport", this.exactTeleport);
        }
        return compoundTag;
    }
    
    @Override
    public void fromTag(final CompoundTag compoundTag) {
        super.fromTag(compoundTag);
        this.age = compoundTag.getLong("Age");
        if (compoundTag.containsKey("ExitPortal", 10)) {
            this.exitPortalPos = TagHelper.deserializeBlockPos(compoundTag.getCompound("ExitPortal"));
        }
        this.exactTeleport = compoundTag.getBoolean("ExactTeleport");
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public double getSquaredRenderDistance() {
        return 65536.0;
    }
    
    @Override
    public void tick() {
        final boolean boolean1 = this.isRecentlyGenerated();
        final boolean boolean2 = this.needsCooldownBeforeTeleporting();
        ++this.age;
        if (boolean2) {
            --this.teleportCooldown;
        }
        else if (!this.world.isClient) {
            final List<Entity> list3 = this.world.<Entity>getEntities(Entity.class, new BoundingBox(this.getPos()));
            if (!list3.isEmpty()) {
                this.tryTeleportingEntity(list3.get(0));
            }
            if (this.age % 2400L == 0L) {
                this.startTeleportCooldown();
            }
        }
        if (boolean1 != this.isRecentlyGenerated() || boolean2 != this.needsCooldownBeforeTeleporting()) {
            this.markDirty();
        }
    }
    
    public boolean isRecentlyGenerated() {
        return this.age < 200L;
    }
    
    public boolean needsCooldownBeforeTeleporting() {
        return this.teleportCooldown > 0;
    }
    
    @Environment(EnvType.CLIENT)
    public float getRecentlyGeneratedBeamHeight(final float tickDelta) {
        return MathHelper.clamp((this.age + tickDelta) / 200.0f, 0.0f, 1.0f);
    }
    
    @Environment(EnvType.CLIENT)
    public float getCooldownBeamHeight(final float tickDelta) {
        return 1.0f - MathHelper.clamp((this.teleportCooldown - tickDelta) / 40.0f, 0.0f, 1.0f);
    }
    
    @Nullable
    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return new BlockEntityUpdateS2CPacket(this.pos, 8, this.toInitialChunkDataTag());
    }
    
    @Override
    public CompoundTag toInitialChunkDataTag() {
        return this.toTag(new CompoundTag());
    }
    
    public void startTeleportCooldown() {
        if (!this.world.isClient) {
            this.teleportCooldown = 40;
            this.world.addBlockAction(this.getPos(), this.getCachedState().getBlock(), 1, 0);
            this.markDirty();
        }
    }
    
    @Override
    public boolean onBlockAction(final int integer1, final int integer2) {
        if (integer1 == 1) {
            this.teleportCooldown = 40;
            return true;
        }
        return super.onBlockAction(integer1, integer2);
    }
    
    public void tryTeleportingEntity(final Entity entity) {
        if (this.world.isClient || this.needsCooldownBeforeTeleporting()) {
            return;
        }
        this.teleportCooldown = 100;
        if (this.exitPortalPos == null && this.world.dimension instanceof TheEndDimension) {
            this.createPortal();
        }
        if (this.exitPortalPos != null) {
            final BlockPos blockPos2 = this.exactTeleport ? this.exitPortalPos : this.findBestPortalExitPos();
            entity.requestTeleport(blockPos2.getX() + 0.5, blockPos2.getY() + 0.5, blockPos2.getZ() + 0.5);
        }
        this.startTeleportCooldown();
    }
    
    private BlockPos findBestPortalExitPos() {
        final BlockPos blockPos1 = findExitPortalPos(this.world, this.exitPortalPos, 5, false);
        EndGatewayBlockEntity.LOGGER.debug("Best exit position for portal at {} is {}", this.exitPortalPos, blockPos1);
        return blockPos1.up();
    }
    
    private void createPortal() {
        final Vec3d vec3d1 = new Vec3d(this.getPos().getX(), 0.0, this.getPos().getZ()).normalize();
        Vec3d vec3d2 = vec3d1.multiply(1024.0);
        for (int integer3 = 16; getChunk(this.world, vec3d2).getHighestNonEmptySectionYOffset() > 0 && integer3-- > 0; vec3d2 = vec3d2.add(vec3d1.multiply(-16.0))) {
            EndGatewayBlockEntity.LOGGER.debug("Skipping backwards past nonempty chunk at {}", vec3d2);
        }
        for (int integer3 = 16; getChunk(this.world, vec3d2).getHighestNonEmptySectionYOffset() == 0 && integer3-- > 0; vec3d2 = vec3d2.add(vec3d1.multiply(16.0))) {
            EndGatewayBlockEntity.LOGGER.debug("Skipping forward past empty chunk at {}", vec3d2);
        }
        EndGatewayBlockEntity.LOGGER.debug("Found chunk at {}", vec3d2);
        final WorldChunk worldChunk4 = getChunk(this.world, vec3d2);
        this.exitPortalPos = findPortalPosition(worldChunk4);
        if (this.exitPortalPos == null) {
            this.exitPortalPos = new BlockPos(vec3d2.x + 0.5, 75.0, vec3d2.z + 0.5);
            EndGatewayBlockEntity.LOGGER.debug("Failed to find suitable block, settling on {}", this.exitPortalPos);
            Feature.az.generate(this.world, this.world.getChunkManager().getChunkGenerator(), new Random(this.exitPortalPos.asLong()), this.exitPortalPos, FeatureConfig.DEFAULT);
        }
        else {
            EndGatewayBlockEntity.LOGGER.debug("Found block at {}", this.exitPortalPos);
        }
        this.exitPortalPos = findExitPortalPos(this.world, this.exitPortalPos, 16, true);
        EndGatewayBlockEntity.LOGGER.debug("Creating portal at {}", this.exitPortalPos);
        this.createPortal(this.exitPortalPos = this.exitPortalPos.up(10));
        this.markDirty();
    }
    
    private static BlockPos findExitPortalPos(final BlockView world, final BlockPos pos, final int searchRadius, final boolean boolean4) {
        BlockPos blockPos5 = null;
        for (int integer6 = -searchRadius; integer6 <= searchRadius; ++integer6) {
            for (int integer7 = -searchRadius; integer7 <= searchRadius; ++integer7) {
                if (integer6 != 0 || integer7 != 0 || boolean4) {
                    for (int integer8 = 255; integer8 > ((blockPos5 == null) ? 0 : blockPos5.getY()); --integer8) {
                        final BlockPos blockPos6 = new BlockPos(pos.getX() + integer6, integer8, pos.getZ() + integer7);
                        final BlockState blockState10 = world.getBlockState(blockPos6);
                        if (Block.isShapeFullCube(blockState10.getCollisionShape(world, blockPos6)) && (boolean4 || blockState10.getBlock() != Blocks.z)) {
                            blockPos5 = blockPos6;
                            break;
                        }
                    }
                }
            }
        }
        return (blockPos5 == null) ? pos : blockPos5;
    }
    
    private static WorldChunk getChunk(final World world, final Vec3d pos) {
        return world.getChunk(MathHelper.floor(pos.x / 16.0), MathHelper.floor(pos.z / 16.0));
    }
    
    @Nullable
    private static BlockPos findPortalPosition(final WorldChunk worldChunk) {
        final ChunkPos chunkPos2 = worldChunk.getPos();
        final BlockPos blockPos3 = new BlockPos(chunkPos2.getStartX(), 30, chunkPos2.getStartZ());
        final int integer4 = worldChunk.getHighestNonEmptySectionYOffset() + 16 - 1;
        final BlockPos blockPos4 = new BlockPos(chunkPos2.getEndX(), integer4, chunkPos2.getEndZ());
        BlockPos blockPos5 = null;
        double double7 = 0.0;
        for (final BlockPos blockPos6 : BlockPos.iterate(blockPos3, blockPos4)) {
            final BlockState blockState11 = worldChunk.getBlockState(blockPos6);
            final BlockPos blockPos7 = blockPos6.up();
            final BlockPos blockPos8 = blockPos6.up(2);
            if (blockState11.getBlock() == Blocks.dW && !Block.isShapeFullCube(worldChunk.getBlockState(blockPos7).getCollisionShape(worldChunk, blockPos7)) && !Block.isShapeFullCube(worldChunk.getBlockState(blockPos8).getCollisionShape(worldChunk, blockPos8))) {
                final double double8 = blockPos6.getSquaredDistance(0.0, 0.0, 0.0, true);
                if (blockPos5 != null && double8 >= double7) {
                    continue;
                }
                blockPos5 = blockPos6;
                double7 = double8;
            }
        }
        return blockPos5;
    }
    
    private void createPortal(final BlockPos blockPos) {
        Feature.aB.generate(this.world, this.world.getChunkManager().getChunkGenerator(), new Random(), blockPos, EndGatewayFeatureConfig.createConfig(this.getPos(), false));
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public boolean shouldDrawSide(final Direction direction) {
        return Block.shouldDrawSide(this.getCachedState(), this.world, this.getPos(), direction);
    }
    
    @Environment(EnvType.CLIENT)
    public int getDrawnSidesCount() {
        int integer1 = 0;
        for (final Direction direction5 : Direction.values()) {
            integer1 += (this.shouldDrawSide(direction5) ? 1 : 0);
        }
        return integer1;
    }
    
    public void setExitPortalPos(final BlockPos blockPos, final boolean boolean2) {
        this.exactTeleport = boolean2;
        this.exitPortalPos = blockPos;
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}

package net.minecraft.block.entity;

import org.apache.logging.log4j.LogManager;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.client.network.packet.BlockEntityUpdateS2CPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import javax.annotation.Nullable;
import net.minecraft.world.World;
import org.apache.logging.log4j.Logger;

public abstract class BlockEntity
{
    private static final Logger LOGGER;
    private final BlockEntityType<?> type;
    @Nullable
    protected World world;
    protected BlockPos pos;
    protected boolean invalid;
    @Nullable
    private BlockState cachedState;
    
    public BlockEntity(final BlockEntityType<?> blockEntityType) {
        this.pos = BlockPos.ORIGIN;
        this.type = blockEntityType;
    }
    
    @Nullable
    public World getWorld() {
        return this.world;
    }
    
    public void setWorld(final World world) {
        this.world = world;
    }
    
    public boolean hasWorld() {
        return this.world != null;
    }
    
    public void fromTag(final CompoundTag compoundTag) {
        this.pos = new BlockPos(compoundTag.getInt("x"), compoundTag.getInt("y"), compoundTag.getInt("z"));
    }
    
    public CompoundTag toTag(final CompoundTag compoundTag) {
        return this.writeIdentifyingData(compoundTag);
    }
    
    private CompoundTag writeIdentifyingData(final CompoundTag compoundTag) {
        final Identifier identifier2 = BlockEntityType.getId(this.getType());
        if (identifier2 == null) {
            throw new RuntimeException(this.getClass() + " is missing a mapping! This is a bug!");
        }
        compoundTag.putString("id", identifier2.toString());
        compoundTag.putInt("x", this.pos.getX());
        compoundTag.putInt("y", this.pos.getY());
        compoundTag.putInt("z", this.pos.getZ());
        return compoundTag;
    }
    
    @Nullable
    public static BlockEntity createFromTag(final CompoundTag compoundTag) {
        final String string2 = compoundTag.getString("id");
        final Object o;
        final Object o2;
        return Registry.BLOCK_ENTITY.getOrEmpty(new Identifier(string2)).map(blockEntityType -> {
            try {
                return blockEntityType.instantiate();
            }
            catch (Throwable throwable3) {
                BlockEntity.LOGGER.error("Failed to create block entity {}", o, throwable3);
                return null;
            }
        }).<BlockEntity>map(blockEntity -> {
            try {
                blockEntity.fromTag(compoundTag);
                return blockEntity;
            }
            catch (Throwable throwable4) {
                BlockEntity.LOGGER.error("Failed to load data for block entity {}", o2, throwable4);
                return null;
            }
        }).orElseGet(() -> {
            BlockEntity.LOGGER.warn("Skipping BlockEntity with id {}", string2);
            return null;
        });
    }
    
    public void markDirty() {
        if (this.world != null) {
            this.cachedState = this.world.getBlockState(this.pos);
            this.world.markDirty(this.pos, this);
            if (!this.cachedState.isAir()) {
                this.world.updateHorizontalAdjacent(this.pos, this.cachedState.getBlock());
            }
        }
    }
    
    @Environment(EnvType.CLIENT)
    public double getSquaredDistance(final double x, final double double3, final double double5) {
        final double double6 = this.pos.getX() + 0.5 - x;
        final double double7 = this.pos.getY() + 0.5 - double3;
        final double double8 = this.pos.getZ() + 0.5 - double5;
        return double6 * double6 + double7 * double7 + double8 * double8;
    }
    
    @Environment(EnvType.CLIENT)
    public double getSquaredRenderDistance() {
        return 4096.0;
    }
    
    public BlockPos getPos() {
        return this.pos;
    }
    
    public BlockState getCachedState() {
        if (this.cachedState == null) {
            this.cachedState = this.world.getBlockState(this.pos);
        }
        return this.cachedState;
    }
    
    @Nullable
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return null;
    }
    
    public CompoundTag toInitialChunkDataTag() {
        return this.writeIdentifyingData(new CompoundTag());
    }
    
    public boolean isInvalid() {
        return this.invalid;
    }
    
    public void invalidate() {
        this.invalid = true;
    }
    
    public void validate() {
        this.invalid = false;
    }
    
    public boolean onBlockAction(final int integer1, final int integer2) {
        return false;
    }
    
    public void resetBlock() {
        this.cachedState = null;
    }
    
    public void populateCrashReport(final CrashReportSection crashReportSection) {
        crashReportSection.add("Name", () -> Registry.BLOCK_ENTITY.getId(this.getType()) + " // " + this.getClass().getCanonicalName());
        if (this.world == null) {
            return;
        }
        CrashReportSection.addBlockInfo(crashReportSection, this.pos, this.getCachedState());
        CrashReportSection.addBlockInfo(crashReportSection, this.pos, this.world.getBlockState(this.pos));
    }
    
    public void setPos(final BlockPos blockPos) {
        this.pos = blockPos.toImmutable();
    }
    
    public boolean shouldNotCopyTagFromItem() {
        return false;
    }
    
    public void applyRotation(final BlockRotation blockRotation) {
    }
    
    public void applyMirror(final BlockMirror blockMirror) {
    }
    
    public BlockEntityType<?> getType() {
        return this.type;
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}

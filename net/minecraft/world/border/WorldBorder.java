package net.minecraft.world.border;

import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.SystemUtil;
import net.minecraft.world.level.LevelProperties;
import java.util.Iterator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.util.math.BlockPos;
import com.google.common.collect.Lists;
import java.util.List;

public class WorldBorder
{
    private final List<WorldBorderListener> listeners;
    private double damagePerBlock;
    private double buffer;
    private int warningTime;
    private int warningBlocks;
    private double centerX;
    private double centerZ;
    private int maxWorldBorderRadius;
    private Area area;
    
    public WorldBorder() {
        this.listeners = Lists.newArrayList();
        this.damagePerBlock = 0.2;
        this.buffer = 5.0;
        this.warningTime = 15;
        this.warningBlocks = 5;
        this.maxWorldBorderRadius = 29999984;
        this.area = new StaticArea(6.0E7);
    }
    
    public boolean contains(final BlockPos blockPos) {
        return blockPos.getX() + 1 > this.getBoundWest() && blockPos.getX() < this.getBoundEast() && blockPos.getZ() + 1 > this.getBoundNorth() && blockPos.getZ() < this.getBoundSouth();
    }
    
    public boolean contains(final ChunkPos chunkPos) {
        return chunkPos.getEndX() > this.getBoundWest() && chunkPos.getStartX() < this.getBoundEast() && chunkPos.getEndZ() > this.getBoundNorth() && chunkPos.getStartZ() < this.getBoundSouth();
    }
    
    public boolean contains(final BoundingBox boundingBox) {
        return boundingBox.maxX > this.getBoundWest() && boundingBox.minX < this.getBoundEast() && boundingBox.maxZ > this.getBoundNorth() && boundingBox.minZ < this.getBoundSouth();
    }
    
    public double contains(final Entity entity) {
        return this.contains(entity.x, entity.z);
    }
    
    public VoxelShape asVoxelShape() {
        return this.area.m();
    }
    
    public double contains(final double double1, final double double3) {
        final double double4 = double3 - this.getBoundNorth();
        final double double5 = this.getBoundSouth() - double3;
        final double double6 = double1 - this.getBoundWest();
        final double double7 = this.getBoundEast() - double1;
        double double8 = Math.min(double6, double7);
        double8 = Math.min(double8, double4);
        return Math.min(double8, double5);
    }
    
    @Environment(EnvType.CLIENT)
    public WorldBorderStage getStage() {
        return this.area.getStage();
    }
    
    public double getBoundWest() {
        return this.area.getBoundWest();
    }
    
    public double getBoundNorth() {
        return this.area.getBoundNorth();
    }
    
    public double getBoundEast() {
        return this.area.getBoundEast();
    }
    
    public double getBoundSouth() {
        return this.area.getBoundSouth();
    }
    
    public double getCenterX() {
        return this.centerX;
    }
    
    public double getCenterZ() {
        return this.centerZ;
    }
    
    public void setCenter(final double x, final double z) {
        this.centerX = x;
        this.centerZ = z;
        this.area.onCenterChanged();
        for (final WorldBorderListener worldBorderListener6 : this.getListeners()) {
            worldBorderListener6.onCenterChanged(this, x, z);
        }
    }
    
    public double getSize() {
        return this.area.getSize();
    }
    
    public long getTargetRemainingTime() {
        return this.area.getTargetRemainingTime();
    }
    
    public double getTargetSize() {
        return this.area.getTargetSize();
    }
    
    public void setSize(final double size) {
        this.area = new StaticArea(size);
        for (final WorldBorderListener worldBorderListener4 : this.getListeners()) {
            worldBorderListener4.onSizeChange(this, size);
        }
    }
    
    public void interpolateSize(final double fromSize, final double toSize, final long time) {
        this.area = ((fromSize == toSize) ? new StaticArea(toSize) : new MovingArea(fromSize, toSize, time));
        for (final WorldBorderListener worldBorderListener8 : this.getListeners()) {
            worldBorderListener8.onInterpolateSize(this, fromSize, toSize, time);
        }
    }
    
    protected List<WorldBorderListener> getListeners() {
        return Lists.newArrayList(this.listeners);
    }
    
    public void addListener(final WorldBorderListener listener) {
        this.listeners.add(listener);
    }
    
    public void setMaxWorldBorderRadius(final int integer) {
        this.maxWorldBorderRadius = integer;
        this.area.onMaxWorldBorderRadiusChanged();
    }
    
    public int getMaxWorldBorderRadius() {
        return this.maxWorldBorderRadius;
    }
    
    public double getBuffer() {
        return this.buffer;
    }
    
    public void setBuffer(final double buffer) {
        this.buffer = buffer;
        for (final WorldBorderListener worldBorderListener4 : this.getListeners()) {
            worldBorderListener4.onSafeZoneChanged(this, buffer);
        }
    }
    
    public double getDamagePerBlock() {
        return this.damagePerBlock;
    }
    
    public void setDamagePerBlock(final double damagePerBlock) {
        this.damagePerBlock = damagePerBlock;
        for (final WorldBorderListener worldBorderListener4 : this.getListeners()) {
            worldBorderListener4.onDamagePerBlockChanged(this, damagePerBlock);
        }
    }
    
    @Environment(EnvType.CLIENT)
    public double getShrinkingSpeed() {
        return this.area.getShrinkingSpeed();
    }
    
    public int getWarningTime() {
        return this.warningTime;
    }
    
    public void setWarningTime(final int warningTime) {
        this.warningTime = warningTime;
        for (final WorldBorderListener worldBorderListener3 : this.getListeners()) {
            worldBorderListener3.onWarningTimeChanged(this, warningTime);
        }
    }
    
    public int getWarningBlocks() {
        return this.warningBlocks;
    }
    
    public void setWarningBlocks(final int warningBlocks) {
        this.warningBlocks = warningBlocks;
        for (final WorldBorderListener worldBorderListener3 : this.getListeners()) {
            worldBorderListener3.onWarningBlocksChanged(this, warningBlocks);
        }
    }
    
    public void tick() {
        this.area = this.area.getAreaInstance();
    }
    
    public void save(final LevelProperties levelProperties) {
        levelProperties.setBorderSize(this.getSize());
        levelProperties.setBorderCenterX(this.getCenterX());
        levelProperties.borderCenterZ(this.getCenterZ());
        levelProperties.setBorderSafeZone(this.getBuffer());
        levelProperties.setBorderDamagePerBlock(this.getDamagePerBlock());
        levelProperties.setBorderWarningBlocks(this.getWarningBlocks());
        levelProperties.setBorderWarningTime(this.getWarningTime());
        levelProperties.setBorderSizeLerpTarget(this.getTargetSize());
        levelProperties.setBorderSizeLerpTime(this.getTargetRemainingTime());
    }
    
    public void load(final LevelProperties levelProperties) {
        this.setCenter(levelProperties.getBorderCenterX(), levelProperties.getBorderCenterZ());
        this.setDamagePerBlock(levelProperties.getBorderDamagePerBlock());
        this.setBuffer(levelProperties.getBorderSafeZone());
        this.setWarningBlocks(levelProperties.getBorderWarningBlocks());
        this.setWarningTime(levelProperties.getBorderWarningTime());
        if (levelProperties.getBorderSizeLerpTime() > 0L) {
            this.interpolateSize(levelProperties.getBorderSize(), levelProperties.getBorderSizeLerpTarget(), levelProperties.getBorderSizeLerpTime());
        }
        else {
            this.setSize(levelProperties.getBorderSize());
        }
    }
    
    class MovingArea implements Area
    {
        private final double oldSize;
        private final double newSize;
        private final long timeEnd;
        private final long timeStart;
        private final double timeDuration;
        
        private MovingArea(final double double2, final double double4, final long long6) {
            this.oldSize = double2;
            this.newSize = double4;
            this.timeDuration = (double)long6;
            this.timeStart = SystemUtil.getMeasuringTimeMs();
            this.timeEnd = this.timeStart + long6;
        }
        
        @Override
        public double getBoundWest() {
            return Math.max(WorldBorder.this.getCenterX() - this.getSize() / 2.0, -WorldBorder.this.maxWorldBorderRadius);
        }
        
        @Override
        public double getBoundNorth() {
            return Math.max(WorldBorder.this.getCenterZ() - this.getSize() / 2.0, -WorldBorder.this.maxWorldBorderRadius);
        }
        
        @Override
        public double getBoundEast() {
            return Math.min(WorldBorder.this.getCenterX() + this.getSize() / 2.0, WorldBorder.this.maxWorldBorderRadius);
        }
        
        @Override
        public double getBoundSouth() {
            return Math.min(WorldBorder.this.getCenterZ() + this.getSize() / 2.0, WorldBorder.this.maxWorldBorderRadius);
        }
        
        @Override
        public double getSize() {
            final double double1 = (SystemUtil.getMeasuringTimeMs() - this.timeStart) / this.timeDuration;
            return (double1 < 1.0) ? MathHelper.lerp(double1, this.oldSize, this.newSize) : this.newSize;
        }
        
        @Environment(EnvType.CLIENT)
        @Override
        public double getShrinkingSpeed() {
            return Math.abs(this.oldSize - this.newSize) / (this.timeEnd - this.timeStart);
        }
        
        @Override
        public long getTargetRemainingTime() {
            return this.timeEnd - SystemUtil.getMeasuringTimeMs();
        }
        
        @Override
        public double getTargetSize() {
            return this.newSize;
        }
        
        @Environment(EnvType.CLIENT)
        @Override
        public WorldBorderStage getStage() {
            return (this.newSize < this.oldSize) ? WorldBorderStage.SHRINKING : WorldBorderStage.GROWING;
        }
        
        @Override
        public void onCenterChanged() {
        }
        
        @Override
        public void onMaxWorldBorderRadiusChanged() {
        }
        
        @Override
        public Area getAreaInstance() {
            if (this.getTargetRemainingTime() <= 0L) {
                return new StaticArea(this.newSize);
            }
            return this;
        }
        
        @Override
        public VoxelShape m() {
            return VoxelShapes.combineAndSimplify(VoxelShapes.UNBOUNDED, VoxelShapes.cuboid(Math.floor(this.getBoundWest()), Double.NEGATIVE_INFINITY, Math.floor(this.getBoundNorth()), Math.ceil(this.getBoundEast()), Double.POSITIVE_INFINITY, Math.ceil(this.getBoundSouth())), BooleanBiFunction.ONLY_FIRST);
        }
    }
    
    class StaticArea implements Area
    {
        private final double size;
        private double boundWest;
        private double boundNorth;
        private double boundEast;
        private double boundSouth;
        private VoxelShape g;
        
        public StaticArea(final double double2) {
            this.size = double2;
            this.recalculateBounds();
        }
        
        @Override
        public double getBoundWest() {
            return this.boundWest;
        }
        
        @Override
        public double getBoundEast() {
            return this.boundEast;
        }
        
        @Override
        public double getBoundNorth() {
            return this.boundNorth;
        }
        
        @Override
        public double getBoundSouth() {
            return this.boundSouth;
        }
        
        @Override
        public double getSize() {
            return this.size;
        }
        
        @Environment(EnvType.CLIENT)
        @Override
        public WorldBorderStage getStage() {
            return WorldBorderStage.STATIC;
        }
        
        @Environment(EnvType.CLIENT)
        @Override
        public double getShrinkingSpeed() {
            return 0.0;
        }
        
        @Override
        public long getTargetRemainingTime() {
            return 0L;
        }
        
        @Override
        public double getTargetSize() {
            return this.size;
        }
        
        private void recalculateBounds() {
            this.boundWest = Math.max(WorldBorder.this.getCenterX() - this.size / 2.0, -WorldBorder.this.maxWorldBorderRadius);
            this.boundNorth = Math.max(WorldBorder.this.getCenterZ() - this.size / 2.0, -WorldBorder.this.maxWorldBorderRadius);
            this.boundEast = Math.min(WorldBorder.this.getCenterX() + this.size / 2.0, WorldBorder.this.maxWorldBorderRadius);
            this.boundSouth = Math.min(WorldBorder.this.getCenterZ() + this.size / 2.0, WorldBorder.this.maxWorldBorderRadius);
            this.g = VoxelShapes.combineAndSimplify(VoxelShapes.UNBOUNDED, VoxelShapes.cuboid(Math.floor(this.getBoundWest()), Double.NEGATIVE_INFINITY, Math.floor(this.getBoundNorth()), Math.ceil(this.getBoundEast()), Double.POSITIVE_INFINITY, Math.ceil(this.getBoundSouth())), BooleanBiFunction.ONLY_FIRST);
        }
        
        @Override
        public void onMaxWorldBorderRadiusChanged() {
            this.recalculateBounds();
        }
        
        @Override
        public void onCenterChanged() {
            this.recalculateBounds();
        }
        
        @Override
        public Area getAreaInstance() {
            return this;
        }
        
        @Override
        public VoxelShape m() {
            return this.g;
        }
    }
    
    interface Area
    {
        double getBoundWest();
        
        double getBoundEast();
        
        double getBoundNorth();
        
        double getBoundSouth();
        
        double getSize();
        
        @Environment(EnvType.CLIENT)
        double getShrinkingSpeed();
        
        long getTargetRemainingTime();
        
        double getTargetSize();
        
        @Environment(EnvType.CLIENT)
        WorldBorderStage getStage();
        
        void onMaxWorldBorderRadiusChanged();
        
        void onCenterChanged();
        
        Area getAreaInstance();
        
        VoxelShape m();
    }
}

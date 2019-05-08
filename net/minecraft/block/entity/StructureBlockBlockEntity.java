package net.minecraft.block.entity;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.world.IWorld;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.BlockRotStructureProcessor;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.util.SystemUtil;
import java.util.Random;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3i;
import java.util.Iterator;
import com.google.common.collect.Lists;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Predicate;
import java.util.Objects;
import net.minecraft.util.math.MutableIntBoundingBox;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ChatUtil;
import net.minecraft.entity.player.PlayerEntity;
import javax.annotation.Nullable;
import net.minecraft.client.network.packet.BlockEntityUpdateS2CPacket;
import net.minecraft.state.property.Property;
import net.minecraft.block.StructureBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.MathHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Identifier;

public class StructureBlockBlockEntity extends BlockEntity
{
    private Identifier structureName;
    private String author;
    private String metadata;
    private BlockPos offset;
    private BlockPos size;
    private BlockMirror mirror;
    private BlockRotation rotation;
    private StructureBlockMode mode;
    private boolean ignoreEntities;
    private boolean powered;
    private boolean showAir;
    private boolean showBoundingBox;
    private float integrity;
    private long seed;
    
    public StructureBlockBlockEntity() {
        super(BlockEntityType.STRUCTURE_BLOCK);
        this.author = "";
        this.metadata = "";
        this.offset = new BlockPos(0, 1, 0);
        this.size = BlockPos.ORIGIN;
        this.mirror = BlockMirror.NONE;
        this.rotation = BlockRotation.ROT_0;
        this.mode = StructureBlockMode.d;
        this.ignoreEntities = true;
        this.showBoundingBox = true;
        this.integrity = 1.0f;
    }
    
    @Override
    public CompoundTag toTag(final CompoundTag compoundTag) {
        super.toTag(compoundTag);
        compoundTag.putString("name", this.getStructureName());
        compoundTag.putString("author", this.author);
        compoundTag.putString("metadata", this.metadata);
        compoundTag.putInt("posX", this.offset.getX());
        compoundTag.putInt("posY", this.offset.getY());
        compoundTag.putInt("posZ", this.offset.getZ());
        compoundTag.putInt("sizeX", this.size.getX());
        compoundTag.putInt("sizeY", this.size.getY());
        compoundTag.putInt("sizeZ", this.size.getZ());
        compoundTag.putString("rotation", this.rotation.toString());
        compoundTag.putString("mirror", this.mirror.toString());
        compoundTag.putString("mode", this.mode.toString());
        compoundTag.putBoolean("ignoreEntities", this.ignoreEntities);
        compoundTag.putBoolean("powered", this.powered);
        compoundTag.putBoolean("showair", this.showAir);
        compoundTag.putBoolean("showboundingbox", this.showBoundingBox);
        compoundTag.putFloat("integrity", this.integrity);
        compoundTag.putLong("seed", this.seed);
        return compoundTag;
    }
    
    @Override
    public void fromTag(final CompoundTag compoundTag) {
        super.fromTag(compoundTag);
        this.setStructureName(compoundTag.getString("name"));
        this.author = compoundTag.getString("author");
        this.metadata = compoundTag.getString("metadata");
        final int integer2 = MathHelper.clamp(compoundTag.getInt("posX"), -32, 32);
        final int integer3 = MathHelper.clamp(compoundTag.getInt("posY"), -32, 32);
        final int integer4 = MathHelper.clamp(compoundTag.getInt("posZ"), -32, 32);
        this.offset = new BlockPos(integer2, integer3, integer4);
        final int integer5 = MathHelper.clamp(compoundTag.getInt("sizeX"), 0, 32);
        final int integer6 = MathHelper.clamp(compoundTag.getInt("sizeY"), 0, 32);
        final int integer7 = MathHelper.clamp(compoundTag.getInt("sizeZ"), 0, 32);
        this.size = new BlockPos(integer5, integer6, integer7);
        try {
            this.rotation = BlockRotation.valueOf(compoundTag.getString("rotation"));
        }
        catch (IllegalArgumentException illegalArgumentException8) {
            this.rotation = BlockRotation.ROT_0;
        }
        try {
            this.mirror = BlockMirror.valueOf(compoundTag.getString("mirror"));
        }
        catch (IllegalArgumentException illegalArgumentException8) {
            this.mirror = BlockMirror.NONE;
        }
        try {
            this.mode = StructureBlockMode.valueOf(compoundTag.getString("mode"));
        }
        catch (IllegalArgumentException illegalArgumentException8) {
            this.mode = StructureBlockMode.d;
        }
        this.ignoreEntities = compoundTag.getBoolean("ignoreEntities");
        this.powered = compoundTag.getBoolean("powered");
        this.showAir = compoundTag.getBoolean("showair");
        this.showBoundingBox = compoundTag.getBoolean("showboundingbox");
        if (compoundTag.containsKey("integrity")) {
            this.integrity = compoundTag.getFloat("integrity");
        }
        else {
            this.integrity = 1.0f;
        }
        this.seed = compoundTag.getLong("seed");
        this.updateBlockMode();
    }
    
    private void updateBlockMode() {
        if (this.world == null) {
            return;
        }
        final BlockPos blockPos1 = this.getPos();
        final BlockState blockState2 = this.world.getBlockState(blockPos1);
        if (blockState2.getBlock() == Blocks.lX) {
            this.world.setBlockState(blockPos1, ((AbstractPropertyContainer<O, BlockState>)blockState2).<StructureBlockMode, StructureBlockMode>with(StructureBlock.MODE, this.mode), 2);
        }
    }
    
    @Nullable
    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return new BlockEntityUpdateS2CPacket(this.pos, 7, this.toInitialChunkDataTag());
    }
    
    @Override
    public CompoundTag toInitialChunkDataTag() {
        return this.toTag(new CompoundTag());
    }
    
    public boolean openScreen(final PlayerEntity playerEntity) {
        if (!playerEntity.isCreativeLevelTwoOp()) {
            return false;
        }
        if (playerEntity.getEntityWorld().isClient) {
            playerEntity.openStructureBlockScreen(this);
        }
        return true;
    }
    
    public String getStructureName() {
        return (this.structureName == null) ? "" : this.structureName.toString();
    }
    
    public boolean hasStructureName() {
        return this.structureName != null;
    }
    
    public void setStructureName(@Nullable final String string) {
        this.setStructureName(ChatUtil.isEmpty(string) ? null : Identifier.create(string));
    }
    
    public void setStructureName(@Nullable final Identifier identifier) {
        this.structureName = identifier;
    }
    
    public void setAuthor(final LivingEntity livingEntity) {
        this.author = livingEntity.getName().getString();
    }
    
    @Environment(EnvType.CLIENT)
    public BlockPos getOffset() {
        return this.offset;
    }
    
    public void setOffset(final BlockPos blockPos) {
        this.offset = blockPos;
    }
    
    @Environment(EnvType.CLIENT)
    public BlockPos getSize() {
        return this.size;
    }
    
    public void setSize(final BlockPos blockPos) {
        this.size = blockPos;
    }
    
    @Environment(EnvType.CLIENT)
    public BlockMirror getMirror() {
        return this.mirror;
    }
    
    public void setMirror(final BlockMirror blockMirror) {
        this.mirror = blockMirror;
    }
    
    @Environment(EnvType.CLIENT)
    public BlockRotation getRotation() {
        return this.rotation;
    }
    
    public void setRotation(final BlockRotation blockRotation) {
        this.rotation = blockRotation;
    }
    
    @Environment(EnvType.CLIENT)
    public String getMetadata() {
        return this.metadata;
    }
    
    public void setMetadata(final String string) {
        this.metadata = string;
    }
    
    public StructureBlockMode getMode() {
        return this.mode;
    }
    
    public void setMode(final StructureBlockMode structureBlockMode) {
        this.mode = structureBlockMode;
        final BlockState blockState2 = this.world.getBlockState(this.getPos());
        if (blockState2.getBlock() == Blocks.lX) {
            this.world.setBlockState(this.getPos(), ((AbstractPropertyContainer<O, BlockState>)blockState2).<StructureBlockMode, StructureBlockMode>with(StructureBlock.MODE, structureBlockMode), 2);
        }
    }
    
    @Environment(EnvType.CLIENT)
    public void cycleMode() {
        switch (this.getMode()) {
            case a: {
                this.setMode(StructureBlockMode.b);
                break;
            }
            case b: {
                this.setMode(StructureBlockMode.c);
                break;
            }
            case c: {
                this.setMode(StructureBlockMode.d);
                break;
            }
            case d: {
                this.setMode(StructureBlockMode.a);
                break;
            }
        }
    }
    
    @Environment(EnvType.CLIENT)
    public boolean shouldIgnoreEntities() {
        return this.ignoreEntities;
    }
    
    public void setIgnoreEntities(final boolean boolean1) {
        this.ignoreEntities = boolean1;
    }
    
    @Environment(EnvType.CLIENT)
    public float getIntegrity() {
        return this.integrity;
    }
    
    public void setIntegrity(final float float1) {
        this.integrity = float1;
    }
    
    @Environment(EnvType.CLIENT)
    public long getSeed() {
        return this.seed;
    }
    
    public void setSeed(final long long1) {
        this.seed = long1;
    }
    
    public boolean detectStructureSize() {
        if (this.mode != StructureBlockMode.a) {
            return false;
        }
        final BlockPos blockPos1 = this.getPos();
        final int integer2 = 80;
        final BlockPos blockPos2 = new BlockPos(blockPos1.getX() - 80, 0, blockPos1.getZ() - 80);
        final BlockPos blockPos3 = new BlockPos(blockPos1.getX() + 80, 255, blockPos1.getZ() + 80);
        final List<StructureBlockBlockEntity> list5 = this.findStructureBlockEntities(blockPos2, blockPos3);
        final List<StructureBlockBlockEntity> list6 = this.findCorners(list5);
        if (list6.size() < 1) {
            return false;
        }
        final MutableIntBoundingBox mutableIntBoundingBox7 = this.makeBoundingBox(blockPos1, list6);
        if (mutableIntBoundingBox7.maxX - mutableIntBoundingBox7.minX > 1 && mutableIntBoundingBox7.maxY - mutableIntBoundingBox7.minY > 1 && mutableIntBoundingBox7.maxZ - mutableIntBoundingBox7.minZ > 1) {
            this.offset = new BlockPos(mutableIntBoundingBox7.minX - blockPos1.getX() + 1, mutableIntBoundingBox7.minY - blockPos1.getY() + 1, mutableIntBoundingBox7.minZ - blockPos1.getZ() + 1);
            this.size = new BlockPos(mutableIntBoundingBox7.maxX - mutableIntBoundingBox7.minX - 1, mutableIntBoundingBox7.maxY - mutableIntBoundingBox7.minY - 1, mutableIntBoundingBox7.maxZ - mutableIntBoundingBox7.minZ - 1);
            this.markDirty();
            final BlockState blockState8 = this.world.getBlockState(blockPos1);
            this.world.updateListeners(blockPos1, blockState8, blockState8, 3);
            return true;
        }
        return false;
    }
    
    private List<StructureBlockBlockEntity> findCorners(final List<StructureBlockBlockEntity> structureBlockEntities) {
        final Predicate<StructureBlockBlockEntity> predicate2 = structureBlockBlockEntity -> structureBlockBlockEntity.mode == StructureBlockMode.c && Objects.equals(this.structureName, structureBlockBlockEntity.structureName);
        return structureBlockEntities.stream().filter(predicate2).collect(Collectors.toList());
    }
    
    private List<StructureBlockBlockEntity> findStructureBlockEntities(final BlockPos pos1, final BlockPos pos2) {
        final List<StructureBlockBlockEntity> list3 = Lists.newArrayList();
        for (final BlockPos blockPos5 : BlockPos.iterate(pos1, pos2)) {
            final BlockState blockState6 = this.world.getBlockState(blockPos5);
            if (blockState6.getBlock() != Blocks.lX) {
                continue;
            }
            final BlockEntity blockEntity7 = this.world.getBlockEntity(blockPos5);
            if (blockEntity7 == null || !(blockEntity7 instanceof StructureBlockBlockEntity)) {
                continue;
            }
            list3.add((StructureBlockBlockEntity)blockEntity7);
        }
        return list3;
    }
    
    private MutableIntBoundingBox makeBoundingBox(final BlockPos center, final List<StructureBlockBlockEntity> corners) {
        MutableIntBoundingBox mutableIntBoundingBox3;
        if (corners.size() > 1) {
            final BlockPos blockPos4 = corners.get(0).getPos();
            mutableIntBoundingBox3 = new MutableIntBoundingBox(blockPos4, blockPos4);
        }
        else {
            mutableIntBoundingBox3 = new MutableIntBoundingBox(center, center);
        }
        for (final StructureBlockBlockEntity structureBlockBlockEntity5 : corners) {
            final BlockPos blockPos5 = structureBlockBlockEntity5.getPos();
            if (blockPos5.getX() < mutableIntBoundingBox3.minX) {
                mutableIntBoundingBox3.minX = blockPos5.getX();
            }
            else if (blockPos5.getX() > mutableIntBoundingBox3.maxX) {
                mutableIntBoundingBox3.maxX = blockPos5.getX();
            }
            if (blockPos5.getY() < mutableIntBoundingBox3.minY) {
                mutableIntBoundingBox3.minY = blockPos5.getY();
            }
            else if (blockPos5.getY() > mutableIntBoundingBox3.maxY) {
                mutableIntBoundingBox3.maxY = blockPos5.getY();
            }
            if (blockPos5.getZ() < mutableIntBoundingBox3.minZ) {
                mutableIntBoundingBox3.minZ = blockPos5.getZ();
            }
            else {
                if (blockPos5.getZ() <= mutableIntBoundingBox3.maxZ) {
                    continue;
                }
                mutableIntBoundingBox3.maxZ = blockPos5.getZ();
            }
        }
        return mutableIntBoundingBox3;
    }
    
    public boolean saveStructure() {
        return this.saveStructure(true);
    }
    
    public boolean saveStructure(final boolean boolean1) {
        if (this.mode != StructureBlockMode.a || this.world.isClient || this.structureName == null) {
            return false;
        }
        final BlockPos blockPos2 = this.getPos().add(this.offset);
        final ServerWorld serverWorld3 = (ServerWorld)this.world;
        final StructureManager structureManager4 = serverWorld3.getStructureManager();
        Structure structure5;
        try {
            structure5 = structureManager4.getStructureOrBlank(this.structureName);
        }
        catch (InvalidIdentifierException invalidIdentifierException6) {
            return false;
        }
        structure5.a(this.world, blockPos2, this.size, !this.ignoreEntities, Blocks.iF);
        structure5.setAuthor(this.author);
        if (boolean1) {
            try {
                return structureManager4.saveStructure(this.structureName);
            }
            catch (InvalidIdentifierException invalidIdentifierException6) {
                return false;
            }
        }
        return true;
    }
    
    public boolean loadStructure() {
        return this.loadStructure(true);
    }
    
    private static Random createRandom(final long seed) {
        if (seed == 0L) {
            return new Random(SystemUtil.getMeasuringTimeMs());
        }
        return new Random(seed);
    }
    
    public boolean loadStructure(final boolean boolean1) {
        if (this.mode != StructureBlockMode.b || this.world.isClient || this.structureName == null) {
            return false;
        }
        final BlockPos blockPos2 = this.getPos();
        final BlockPos blockPos3 = blockPos2.add(this.offset);
        final ServerWorld serverWorld4 = (ServerWorld)this.world;
        final StructureManager structureManager5 = serverWorld4.getStructureManager();
        Structure structure6;
        try {
            structure6 = structureManager5.getStructure(this.structureName);
        }
        catch (InvalidIdentifierException invalidIdentifierException7) {
            return false;
        }
        if (structure6 == null) {
            return false;
        }
        if (!ChatUtil.isEmpty(structure6.getAuthor())) {
            this.author = structure6.getAuthor();
        }
        final BlockPos blockPos4 = structure6.getSize();
        final boolean boolean2 = this.size.equals(blockPos4);
        if (!boolean2) {
            this.size = blockPos4;
            this.markDirty();
            final BlockState blockState9 = this.world.getBlockState(blockPos2);
            this.world.updateListeners(blockPos2, blockState9, blockState9, 3);
        }
        if (!boolean1 || boolean2) {
            final StructurePlacementData structurePlacementData9 = new StructurePlacementData().setMirrored(this.mirror).setRotation(this.rotation).setIgnoreEntities(this.ignoreEntities).setChunkPosition(null);
            if (this.integrity < 1.0f) {
                structurePlacementData9.clearProcessors().addProcessor(new BlockRotStructureProcessor(MathHelper.clamp(this.integrity, 0.0f, 1.0f))).setRandom(createRandom(this.seed));
            }
            structure6.place(this.world, blockPos3, structurePlacementData9);
            return true;
        }
        return false;
    }
    
    public void unloadStructure() {
        if (this.structureName == null) {
            return;
        }
        final ServerWorld serverWorld1 = (ServerWorld)this.world;
        final StructureManager structureManager2 = serverWorld1.getStructureManager();
        structureManager2.unloadStructure(this.structureName);
    }
    
    public boolean isStructureAvailable() {
        if (this.mode != StructureBlockMode.b || this.world.isClient || this.structureName == null) {
            return false;
        }
        final ServerWorld serverWorld1 = (ServerWorld)this.world;
        final StructureManager structureManager2 = serverWorld1.getStructureManager();
        try {
            return structureManager2.getStructure(this.structureName) != null;
        }
        catch (InvalidIdentifierException invalidIdentifierException3) {
            return false;
        }
    }
    
    public boolean isPowered() {
        return this.powered;
    }
    
    public void setPowered(final boolean boolean1) {
        this.powered = boolean1;
    }
    
    @Environment(EnvType.CLIENT)
    public boolean shouldShowAir() {
        return this.showAir;
    }
    
    public void setShowAir(final boolean boolean1) {
        this.showAir = boolean1;
    }
    
    @Environment(EnvType.CLIENT)
    public boolean shouldShowBoundingBox() {
        return this.showBoundingBox;
    }
    
    public void setShowBoundingBox(final boolean showBoundingBox) {
        this.showBoundingBox = showBoundingBox;
    }
    
    public enum Action
    {
        a, 
        b, 
        c, 
        d;
    }
}

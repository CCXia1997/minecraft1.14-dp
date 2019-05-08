package net.minecraft.structure;

import org.apache.logging.log4j.LogManager;
import net.minecraft.util.BlockRotation;
import net.minecraft.block.BlockState;
import java.util.Iterator;
import java.util.List;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.arguments.BlockArgumentParser;
import com.mojang.brigadier.StringReader;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.block.Blocks;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.util.math.MutableIntBoundingBox;
import java.util.Random;
import net.minecraft.world.IWorld;
import net.minecraft.util.math.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.Logger;

public abstract class SimpleStructurePiece extends StructurePiece
{
    private static final Logger LOGGER;
    protected Structure structure;
    protected StructurePlacementData placementData;
    protected BlockPos pos;
    
    public SimpleStructurePiece(final StructurePieceType type, final int integer) {
        super(type, integer);
    }
    
    public SimpleStructurePiece(final StructurePieceType type, final CompoundTag tag) {
        super(type, tag);
        this.pos = new BlockPos(tag.getInt("TPX"), tag.getInt("TPY"), tag.getInt("TPZ"));
    }
    
    protected void setStructureData(final Structure structure, final BlockPos pos, final StructurePlacementData placementData) {
        this.structure = structure;
        this.setOrientation(Direction.NORTH);
        this.pos = pos;
        this.placementData = placementData;
        this.boundingBox = structure.calculateBoundingBox(placementData, pos);
    }
    
    @Override
    protected void toNbt(final CompoundTag tag) {
        tag.putInt("TPX", this.pos.getX());
        tag.putInt("TPY", this.pos.getY());
        tag.putInt("TPZ", this.pos.getZ());
    }
    
    @Override
    public boolean generate(final IWorld world, final Random random, final MutableIntBoundingBox boundingBox, final ChunkPos pos) {
        this.placementData.setBoundingBox(boundingBox);
        if (this.structure.a(world, this.pos, this.placementData, 2)) {
            final List<Structure.StructureBlockInfo> list5 = this.structure.a(this.pos, this.placementData, Blocks.lX);
            for (final Structure.StructureBlockInfo structureBlockInfo7 : list5) {
                if (structureBlockInfo7.tag == null) {
                    continue;
                }
                final StructureBlockMode structureBlockMode8 = StructureBlockMode.valueOf(structureBlockInfo7.tag.getString("mode"));
                if (structureBlockMode8 != StructureBlockMode.d) {
                    continue;
                }
                this.handleMetadata(structureBlockInfo7.tag.getString("metadata"), structureBlockInfo7.pos, world, random, boundingBox);
            }
            final List<Structure.StructureBlockInfo> list6 = this.structure.a(this.pos, this.placementData, Blocks.lY);
            for (final Structure.StructureBlockInfo structureBlockInfo8 : list6) {
                if (structureBlockInfo8.tag == null) {
                    continue;
                }
                final String string9 = structureBlockInfo8.tag.getString("final_state");
                final BlockArgumentParser blockArgumentParser10 = new BlockArgumentParser(new StringReader(string9), false);
                BlockState blockState11 = Blocks.AIR.getDefaultState();
                try {
                    blockArgumentParser10.parse(true);
                    final BlockState blockState12 = blockArgumentParser10.getBlockState();
                    if (blockState12 != null) {
                        blockState11 = blockState12;
                    }
                    else {
                        SimpleStructurePiece.LOGGER.error("Error while parsing blockstate {} in jigsaw block @ {}", string9, structureBlockInfo8.pos);
                    }
                }
                catch (CommandSyntaxException commandSyntaxException12) {
                    SimpleStructurePiece.LOGGER.error("Error while parsing blockstate {} in jigsaw block @ {}", string9, structureBlockInfo8.pos);
                }
                world.setBlockState(structureBlockInfo8.pos, blockState11, 3);
            }
        }
        return true;
    }
    
    protected abstract void handleMetadata(final String arg1, final BlockPos arg2, final IWorld arg3, final Random arg4, final MutableIntBoundingBox arg5);
    
    @Override
    public void translate(final int x, final int y, final int z) {
        super.translate(x, y, z);
        this.pos = this.pos.add(x, y, z);
    }
    
    @Override
    public BlockRotation getRotation() {
        return this.placementData.getRotation();
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}

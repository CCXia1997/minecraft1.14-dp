package net.minecraft.client.render.block;

import javax.annotation.Nullable;
import net.minecraft.block.MaterialColor;
import net.minecraft.world.BlockView;
import net.minecraft.world.ExtendedBlockView;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.block.BlockState;
import net.minecraft.block.StemBlock;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.Block;
import net.minecraft.state.property.Property;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.block.ReplaceableTallPlantBlock;
import net.minecraft.util.IdList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class BlockColorMap
{
    private final IdList<BlockColorMapper> mappers;
    
    public BlockColorMap() {
        this.mappers = new IdList<BlockColorMapper>(32);
    }
    
    public static BlockColorMap create() {
        final BlockColorMap blockColorMap1 = new BlockColorMap();
        blockColorMap1.register((blockState, extendedBlockView, blockPos, integer) -> {
            if (extendedBlockView == null || blockPos == null) {
                return -1;
            }
            else {
                return BiomeColors.grassColorAt(extendedBlockView, (blockState.<DoubleBlockHalf>get(ReplaceableTallPlantBlock.HALF) == DoubleBlockHalf.a) ? blockPos.down() : blockPos);
            }
        }, Blocks.gR, Blocks.gQ);
        blockColorMap1.register((blockState, extendedBlockView, blockPos, integer) -> {
            if (extendedBlockView == null || blockPos == null) {
                return GrassColorHandler.getColor(0.5, 1.0);
            }
            else {
                return BiomeColors.grassColorAt(extendedBlockView, blockPos);
            }
        }, Blocks.i, Blocks.aR, Blocks.aQ, Blocks.eu);
        blockColorMap1.register((blockState, extendedBlockView, blockPos, integer) -> FoliageColorHandler.getSpruceColor(), Blocks.ah);
        blockColorMap1.register((blockState, extendedBlockView, blockPos, integer) -> FoliageColorHandler.getBirchColor(), Blocks.ai);
        blockColorMap1.register((blockState, extendedBlockView, blockPos, integer) -> {
            if (extendedBlockView == null || blockPos == null) {
                return FoliageColorHandler.getDefaultColor();
            }
            else {
                return BiomeColors.foliageColorAt(extendedBlockView, blockPos);
            }
        }, Blocks.ag, Blocks.aj, Blocks.ak, Blocks.al, Blocks.dH);
        blockColorMap1.register((blockState, extendedBlockView, blockPos, integer) -> {
            if (extendedBlockView == null || blockPos == null) {
                return -1;
            }
            else {
                return BiomeColors.waterColorAt(extendedBlockView, blockPos);
            }
        }, Blocks.A, Blocks.kU, Blocks.dT);
        blockColorMap1.register((blockState, extendedBlockView, blockPos, integer) -> RedstoneWireBlock.getWireColor(blockState.<Integer>get((Property<Integer>)RedstoneWireBlock.POWER)), Blocks.bQ);
        blockColorMap1.register((blockState, extendedBlockView, blockPos, integer) -> {
            if (extendedBlockView == null || blockPos == null) {
                return -1;
            }
            else {
                return BiomeColors.grassColorAt(extendedBlockView, blockPos);
            }
        }, Blocks.cF);
        blockColorMap1.register((blockState, extendedBlockView, blockPos, integer) -> 14731036, Blocks.dE, Blocks.dD);
        final int integer5;
        final int integer6;
        final int integer7;
        final int integer8;
        blockColorMap1.register((blockState, extendedBlockView, blockPos, integer) -> {
            integer5 = blockState.<Integer>get((Property<Integer>)StemBlock.AGE);
            integer6 = integer5 * 32;
            integer7 = 255 - integer5 * 8;
            integer8 = integer5 * 4;
            return integer6 << 16 | integer7 << 8 | integer8;
        }, Blocks.dG, Blocks.dF);
        blockColorMap1.register((blockState, extendedBlockView, blockPos, integer) -> {
            if (extendedBlockView == null || blockPos == null) {
                return 7455580;
            }
            else {
                return 2129968;
            }
        }, Blocks.dM);
        return blockColorMap1;
    }
    
    public int a(final BlockState blockState, final World world, final BlockPos blockPos) {
        final BlockColorMapper blockColorMapper4 = this.mappers.get(Registry.BLOCK.getRawId(blockState.getBlock()));
        if (blockColorMapper4 != null) {
            return blockColorMapper4.getColor(blockState, null, null, 0);
        }
        final MaterialColor materialColor5 = blockState.getTopMaterialColor(world, blockPos);
        return (materialColor5 != null) ? materialColor5.color : -1;
    }
    
    public int getRenderColor(final BlockState state, @Nullable final ExtendedBlockView view, @Nullable final BlockPos pos, final int integer) {
        final BlockColorMapper blockColorMapper5 = this.mappers.get(Registry.BLOCK.getRawId(state.getBlock()));
        return (blockColorMapper5 == null) ? -1 : blockColorMapper5.getColor(state, view, pos, integer);
    }
    
    public void register(final BlockColorMapper mapper, final Block... arr) {
        for (final Block block6 : arr) {
            this.mappers.set(mapper, Registry.BLOCK.getRawId(block6));
        }
    }
}

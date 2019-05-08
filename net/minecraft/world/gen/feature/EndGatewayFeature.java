package net.minecraft.world.gen.feature;

import net.minecraft.block.entity.BlockEntity;
import java.util.Iterator;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.world.ModifiableWorld;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.IWorld;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class EndGatewayFeature extends Feature<EndGatewayFeatureConfig>
{
    public EndGatewayFeature(final Function<Dynamic<?>, ? extends EndGatewayFeatureConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    @Override
    public boolean generate(final IWorld world, final ChunkGenerator<? extends ChunkGeneratorConfig> generator, final Random random, final BlockPos pos, final EndGatewayFeatureConfig config) {
        for (final BlockPos blockPos5 : BlockPos.iterate(pos.add(-1, -2, -1), pos.add(1, 2, 1))) {
            final boolean boolean8 = blockPos5.getX() == pos.getX();
            final boolean boolean9 = blockPos5.getY() == pos.getY();
            final boolean boolean10 = blockPos5.getZ() == pos.getZ();
            final boolean boolean11 = Math.abs(blockPos5.getY() - pos.getY()) == 2;
            if (boolean8 && boolean9 && boolean10) {
                final BlockPos blockPos6 = blockPos5.toImmutable();
                this.setBlockState(world, blockPos6, Blocks.ix.getDefaultState());
                final BlockEntity blockEntity5;
                EndGatewayBlockEntity endGatewayBlockEntity6;
                config.getExitPos().ifPresent(blockPos4 -> {
                    blockEntity5 = world.getBlockEntity(blockPos6);
                    if (blockEntity5 instanceof EndGatewayBlockEntity) {
                        endGatewayBlockEntity6 = (EndGatewayBlockEntity)blockEntity5;
                        endGatewayBlockEntity6.setExitPortalPos(blockPos4, config.isExact());
                        blockEntity5.markDirty();
                    }
                    return;
                });
            }
            else if (boolean9) {
                this.setBlockState(world, blockPos5, Blocks.AIR.getDefaultState());
            }
            else if (boolean11 && boolean8 && boolean10) {
                this.setBlockState(world, blockPos5, Blocks.z.getDefaultState());
            }
            else if ((!boolean8 && !boolean10) || boolean11) {
                this.setBlockState(world, blockPos5, Blocks.AIR.getDefaultState());
            }
            else {
                this.setBlockState(world, blockPos5, Blocks.z.getDefaultState());
            }
        }
        return true;
    }
}

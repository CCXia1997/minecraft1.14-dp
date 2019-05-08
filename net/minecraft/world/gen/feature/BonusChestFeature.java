package net.minecraft.world.gen.feature;

import java.util.Iterator;
import net.minecraft.block.BlockState;
import net.minecraft.world.ViewableWorld;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.world.loot.LootTables;
import net.minecraft.block.Blocks;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.IWorld;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class BonusChestFeature extends Feature<DefaultFeatureConfig>
{
    public BonusChestFeature(final Function<Dynamic<?>, ? extends DefaultFeatureConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    @Override
    public boolean generate(final IWorld world, final ChunkGenerator<? extends ChunkGeneratorConfig> generator, final Random random, BlockPos pos, final DefaultFeatureConfig config) {
        for (BlockState blockState6 = world.getBlockState(pos); (blockState6.isAir() || blockState6.matches(BlockTags.C)) && pos.getY() > 1; pos = pos.down(), blockState6 = world.getBlockState(pos)) {}
        if (pos.getY() < 1) {
            return false;
        }
        pos = pos.up();
        for (int integer7 = 0; integer7 < 4; ++integer7) {
            final BlockPos blockPos8 = pos.add(random.nextInt(4) - random.nextInt(4), random.nextInt(3) - random.nextInt(3), random.nextInt(4) - random.nextInt(4));
            if (world.isAir(blockPos8)) {
                world.setBlockState(blockPos8, Blocks.bP.getDefaultState(), 2);
                LootableContainerBlockEntity.setLootTable(world, random, blockPos8, LootTables.CHEST_SPAWN_BONUS);
                final BlockState blockState7 = Blocks.bK.getDefaultState();
                for (final Direction direction11 : Direction.Type.HORIZONTAL) {
                    final BlockPos blockPos9 = blockPos8.offset(direction11);
                    if (blockState7.canPlaceAt(world, blockPos9)) {
                        world.setBlockState(blockPos9, blockState7, 2);
                    }
                }
                return true;
            }
        }
        return false;
    }
}

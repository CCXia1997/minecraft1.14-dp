package net.minecraft.world.gen.feature;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.property.Property;
import net.minecraft.block.Blocks;
import net.minecraft.block.BlockState;
import net.minecraft.world.BlockView;
import net.minecraft.block.VineBlock;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.IWorld;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.util.math.Direction;

public class VinesFeature extends Feature<DefaultFeatureConfig>
{
    private static final Direction[] DIRECTIONS;
    
    public VinesFeature(final Function<Dynamic<?>, ? extends DefaultFeatureConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    @Override
    public boolean generate(final IWorld world, final ChunkGenerator<? extends ChunkGeneratorConfig> generator, final Random random, final BlockPos pos, final DefaultFeatureConfig config) {
        final BlockPos.Mutable mutable6 = new BlockPos.Mutable(pos);
        for (int integer7 = pos.getY(); integer7 < 256; ++integer7) {
            mutable6.set(pos);
            mutable6.setOffset(random.nextInt(4) - random.nextInt(4), 0, random.nextInt(4) - random.nextInt(4));
            mutable6.setY(integer7);
            if (world.isAir(mutable6)) {
                for (final Direction direction11 : VinesFeature.DIRECTIONS) {
                    if (direction11 != Direction.DOWN) {
                        if (VineBlock.shouldConnectTo(world, mutable6, direction11)) {
                            world.setBlockState(mutable6, ((AbstractPropertyContainer<O, BlockState>)Blocks.dH.getDefaultState()).<Comparable, Boolean>with((Property<Comparable>)VineBlock.getFacingProperty(direction11), true), 2);
                            break;
                        }
                    }
                }
            }
        }
        return true;
    }
    
    static {
        DIRECTIONS = Direction.values();
    }
}

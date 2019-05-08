package net.minecraft.world.gen.feature;

import java.util.Iterator;
import net.minecraft.util.math.Vec3i;
import java.util.List;
import java.util.Collections;
import com.google.common.collect.Lists;
import net.minecraft.util.math.Direction;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import net.minecraft.world.IWorld;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class CoralTreeFeature extends CoralFeature
{
    public CoralTreeFeature(final Function<Dynamic<?>, ? extends DefaultFeatureConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    @Override
    protected boolean spawnCoral(final IWorld world, final Random random, final BlockPos pos, final BlockState state) {
        final BlockPos.Mutable mutable5 = new BlockPos.Mutable(pos);
        for (int integer6 = random.nextInt(3) + 1, integer7 = 0; integer7 < integer6; ++integer7) {
            if (!this.spawnCoralPiece(world, random, mutable5, state)) {
                return true;
            }
            mutable5.setOffset(Direction.UP);
        }
        final BlockPos blockPos7 = mutable5.toImmutable();
        final int integer8 = random.nextInt(3) + 2;
        final List<Direction> list9 = Lists.newArrayList(Direction.Type.HORIZONTAL);
        Collections.shuffle(list9, random);
        final List<Direction> list10 = list9.subList(0, integer8);
        for (final Direction direction12 : list10) {
            mutable5.set(blockPos7);
            mutable5.setOffset(direction12);
            final int integer9 = random.nextInt(5) + 2;
            int integer10 = 0;
            for (int integer11 = 0; integer11 < integer9 && this.spawnCoralPiece(world, random, mutable5, state); ++integer11) {
                ++integer10;
                mutable5.setOffset(Direction.UP);
                if (integer11 == 0 || (integer10 >= 2 && random.nextFloat() < 0.25f)) {
                    mutable5.setOffset(direction12);
                    integer10 = 0;
                }
            }
        }
        return true;
    }
}

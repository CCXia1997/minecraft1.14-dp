package net.minecraft.world.gen.feature;

import java.util.Iterator;
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

public class CoralClawFeature extends CoralFeature
{
    public CoralClawFeature(final Function<Dynamic<?>, ? extends DefaultFeatureConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    @Override
    protected boolean spawnCoral(final IWorld world, final Random random, final BlockPos pos, final BlockState state) {
        if (!this.spawnCoralPiece(world, random, pos, state)) {
            return false;
        }
        final Direction direction5 = Direction.Type.HORIZONTAL.random(random);
        final int integer6 = random.nextInt(2) + 2;
        final List<Direction> list7 = Lists.<Direction>newArrayList(direction5, direction5.rotateYClockwise(), direction5.rotateYCounterclockwise());
        Collections.shuffle(list7, random);
        final List<Direction> list8 = list7.subList(0, integer6);
        for (final Direction direction6 : list8) {
            final BlockPos.Mutable mutable11 = new BlockPos.Mutable(pos);
            final int integer7 = random.nextInt(2) + 1;
            mutable11.setOffset(direction6);
            Direction direction7;
            int integer8;
            if (direction6 == direction5) {
                direction7 = direction5;
                integer8 = random.nextInt(3) + 2;
            }
            else {
                mutable11.setOffset(Direction.UP);
                final Direction[] arr15 = { direction6, Direction.UP };
                direction7 = arr15[random.nextInt(arr15.length)];
                integer8 = random.nextInt(3) + 3;
            }
            for (int integer9 = 0; integer9 < integer7 && this.spawnCoralPiece(world, random, mutable11, state); ++integer9) {
                mutable11.setOffset(direction7);
            }
            mutable11.setOffset(direction7.getOpposite());
            mutable11.setOffset(Direction.UP);
            for (int integer9 = 0; integer9 < integer8; ++integer9) {
                mutable11.setOffset(direction5);
                if (!this.spawnCoralPiece(world, random, mutable11, state)) {
                    break;
                }
                if (random.nextFloat() < 0.25f) {
                    mutable11.setOffset(Direction.UP);
                }
            }
        }
        return true;
    }
}

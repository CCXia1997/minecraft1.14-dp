package net.minecraft.world.gen.feature;

import net.minecraft.util.math.Direction;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import net.minecraft.world.IWorld;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class CoralMushroomFeature extends CoralFeature
{
    public CoralMushroomFeature(final Function<Dynamic<?>, ? extends DefaultFeatureConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    @Override
    protected boolean spawnCoral(final IWorld world, final Random random, final BlockPos pos, final BlockState state) {
        final int integer5 = random.nextInt(3) + 3;
        final int integer6 = random.nextInt(3) + 3;
        final int integer7 = random.nextInt(3) + 3;
        final int integer8 = random.nextInt(3) + 1;
        final BlockPos.Mutable mutable9 = new BlockPos.Mutable(pos);
        for (int integer9 = 0; integer9 <= integer6; ++integer9) {
            for (int integer10 = 0; integer10 <= integer5; ++integer10) {
                for (int integer11 = 0; integer11 <= integer7; ++integer11) {
                    mutable9.set(integer9 + pos.getX(), integer10 + pos.getY(), integer11 + pos.getZ());
                    mutable9.setOffset(Direction.DOWN, integer8);
                    if (integer9 == 0 || integer9 == integer6) {
                        if (integer10 == 0) {
                            continue;
                        }
                        if (integer10 == integer5) {
                            continue;
                        }
                    }
                    if (integer11 == 0 || integer11 == integer7) {
                        if (integer10 == 0) {
                            continue;
                        }
                        if (integer10 == integer5) {
                            continue;
                        }
                    }
                    if (integer9 == 0 || integer9 == integer6) {
                        if (integer11 == 0) {
                            continue;
                        }
                        if (integer11 == integer7) {
                            continue;
                        }
                    }
                    if (integer9 == 0 || integer9 == integer6 || integer10 == 0 || integer10 == integer5 || integer11 == 0 || integer11 == integer7) {
                        if (random.nextFloat() >= 0.1f) {
                            if (!this.spawnCoralPiece(world, random, mutable9, state)) {}
                        }
                    }
                }
            }
        }
        return true;
    }
}

package net.minecraft.container;

import java.util.function.BiConsumer;
import java.util.Optional;
import java.util.function.BiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface BlockContext
{
    public static final BlockContext EMPTY = new BlockContext() {
        @Override
        public <T> Optional<T> run(final BiFunction<World, BlockPos, T> function) {
            return Optional.<T>empty();
        }
    };
    
    default BlockContext create(final World world, final BlockPos world) {
        return new BlockContext() {
            @Override
            public <T> Optional<T> run(final BiFunction<World, BlockPos, T> function) {
                return Optional.<T>of(function.apply(world, world));
            }
        };
    }
    
     <T> Optional<T> run(final BiFunction<World, BlockPos, T> arg1);
    
    default <T> T run(final BiFunction<World, BlockPos, T> function, final T defaultValue) {
        return this.<T>run(function).orElse(defaultValue);
    }
    
    default void run(final BiConsumer<World, BlockPos> function) {
        this.<Optional<Object>>run((world, blockPos) -> {
            function.accept(world, blockPos);
            return Optional.empty();
        });
    }
}

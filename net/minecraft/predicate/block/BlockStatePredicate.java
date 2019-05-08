package net.minecraft.predicate.block;

import java.util.Iterator;
import javax.annotation.Nullable;
import com.google.common.collect.Maps;
import net.minecraft.state.property.Property;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.state.StateFactory;
import net.minecraft.block.BlockState;
import java.util.function.Predicate;

public class BlockStatePredicate implements Predicate<BlockState>
{
    public static final Predicate<BlockState> ANY;
    private final StateFactory<Block, BlockState> factory;
    private final Map<Property<?>, Predicate<Object>> propertyTests;
    
    private BlockStatePredicate(final StateFactory<Block, BlockState> stateFactory) {
        this.propertyTests = Maps.newHashMap();
        this.factory = stateFactory;
    }
    
    public static BlockStatePredicate forBlock(final Block block) {
        return new BlockStatePredicate(block.getStateFactory());
    }
    
    public boolean a(@Nullable final BlockState blockState) {
        if (blockState == null || !blockState.getBlock().equals(this.factory.getBaseObject())) {
            return false;
        }
        if (this.propertyTests.isEmpty()) {
            return true;
        }
        for (final Map.Entry<Property<?>, Predicate<Object>> entry3 : this.propertyTests.entrySet()) {
            if (!this.testProperty(blockState, entry3.getKey(), entry3.getValue())) {
                return false;
            }
        }
        return true;
    }
    
    protected <T extends Comparable<T>> boolean testProperty(final BlockState blockState, final Property<T> property, final Predicate<Object> predicate) {
        final T comparable4 = blockState.<T>get(property);
        return predicate.test(comparable4);
    }
    
    public <V extends Comparable<V>> BlockStatePredicate with(final Property<V> property, final Predicate<Object> predicate) {
        if (!this.factory.getProperties().contains(property)) {
            throw new IllegalArgumentException(this.factory + " cannot support property " + property);
        }
        this.propertyTests.put(property, predicate);
        return this;
    }
    
    static {
        ANY = (blockState -> true);
    }
}

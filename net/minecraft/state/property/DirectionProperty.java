package net.minecraft.state.property;

import com.google.common.collect.Lists;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.Collection;
import net.minecraft.util.math.Direction;

public class DirectionProperty extends EnumProperty<Direction>
{
    protected DirectionProperty(final String name, final Collection<Direction> values) {
        super(name, Direction.class, values);
    }
    
    public static DirectionProperty create(final String name, final Predicate<Direction> predicate) {
        return create(name, Arrays.<Direction>stream(Direction.values()).filter(predicate).collect(Collectors.<Direction>toList()));
    }
    
    public static DirectionProperty create(final String name, final Direction... values) {
        return create(name, Lists.<Direction>newArrayList(values));
    }
    
    public static DirectionProperty create(final String name, final Collection<Direction> values) {
        return new DirectionProperty(name, values);
    }
}

package net.minecraft.state.property;

import java.util.Optional;
import java.util.Set;
import java.util.Collection;
import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableSet;

public class IntegerProperty extends AbstractProperty<Integer>
{
    private final ImmutableSet<Integer> validValues;
    
    protected IntegerProperty(final String name, final int min, final int integer3) {
        super(name, Integer.class);
        if (min < 0) {
            throw new IllegalArgumentException("Min value of " + name + " must be 0 or greater");
        }
        if (integer3 <= min) {
            throw new IllegalArgumentException("Max value of " + name + " must be greater than min (" + min + ")");
        }
        final Set<Integer> set4 = Sets.newHashSet();
        for (int integer4 = min; integer4 <= integer3; ++integer4) {
            set4.add(integer4);
        }
        this.validValues = ImmutableSet.<Integer>copyOf(set4);
    }
    
    @Override
    public Collection<Integer> getValues() {
        return this.validValues;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof IntegerProperty && super.equals(o)) {
            final IntegerProperty integerProperty2 = (IntegerProperty)o;
            return this.validValues.equals(integerProperty2.validValues);
        }
        return false;
    }
    
    @Override
    public int computeHashCode() {
        return 31 * super.computeHashCode() + this.validValues.hashCode();
    }
    
    public static IntegerProperty create(final String name, final int min, final int max) {
        return new IntegerProperty(name, min, max);
    }
    
    @Override
    public Optional<Integer> getValue(final String string) {
        try {
            final Integer integer2 = Integer.valueOf(string);
            return this.validValues.contains(integer2) ? Optional.<Integer>of(integer2) : Optional.<Integer>empty();
        }
        catch (NumberFormatException numberFormatException2) {
            return Optional.<Integer>empty();
        }
    }
    
    @Override
    public String getValueAsString(final Integer integer) {
        return integer.toString();
    }
}

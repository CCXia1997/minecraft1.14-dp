package net.minecraft.state.property;

import java.util.Optional;
import java.util.Collection;
import com.google.common.collect.ImmutableSet;

public class BooleanProperty extends AbstractProperty<Boolean>
{
    private final ImmutableSet<Boolean> values;
    
    protected BooleanProperty(final String string) {
        super(string, Boolean.class);
        this.values = ImmutableSet.<Boolean>of(true, false);
    }
    
    @Override
    public Collection<Boolean> getValues() {
        return this.values;
    }
    
    public static BooleanProperty create(final String name) {
        return new BooleanProperty(name);
    }
    
    @Override
    public Optional<Boolean> getValue(final String string) {
        if ("true".equals(string) || "false".equals(string)) {
            return Optional.<Boolean>of(Boolean.valueOf(string));
        }
        return Optional.<Boolean>empty();
    }
    
    @Override
    public String getValueAsString(final Boolean boolean1) {
        return boolean1.toString();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof BooleanProperty && super.equals(o)) {
            final BooleanProperty booleanProperty2 = (BooleanProperty)o;
            return this.values.equals(booleanProperty2.values);
        }
        return false;
    }
    
    @Override
    public int computeHashCode() {
        return 31 * super.computeHashCode() + this.values.hashCode();
    }
}

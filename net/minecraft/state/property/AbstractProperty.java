package net.minecraft.state.property;

import com.google.common.base.MoreObjects;

public abstract class AbstractProperty<T extends Comparable<T>> implements Property<T>
{
    private final Class<T> valueClass;
    private final String name;
    private Integer computedHashCode;
    
    protected AbstractProperty(final String name, final Class<T> class2) {
        this.valueClass = class2;
        this.name = name;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public Class<T> getValueClass() {
        return this.valueClass;
    }
    
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("name", this.name).add("clazz", this.valueClass).add("values", this.getValues()).toString();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof AbstractProperty) {
            final AbstractProperty<?> abstractProperty2 = o;
            return this.valueClass.equals(abstractProperty2.valueClass) && this.name.equals(abstractProperty2.name);
        }
        return false;
    }
    
    @Override
    public final int hashCode() {
        if (this.computedHashCode == null) {
            this.computedHashCode = this.computeHashCode();
        }
        return this.computedHashCode;
    }
    
    public int computeHashCode() {
        return 31 * this.valueClass.hashCode() + this.name.hashCode();
    }
}

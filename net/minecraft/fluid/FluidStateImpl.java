package net.minecraft.fluid;

import net.minecraft.state.property.Property;
import com.google.common.collect.ImmutableMap;
import net.minecraft.state.AbstractPropertyContainer;

public class FluidStateImpl extends AbstractPropertyContainer<Fluid, FluidState> implements FluidState
{
    public FluidStateImpl(final Fluid fluid, final ImmutableMap<Property<?>, Comparable<?>> immutableMap) {
        super(fluid, immutableMap);
    }
    
    @Override
    public Fluid getFluid() {
        return (Fluid)this.owner;
    }
}

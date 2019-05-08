package net.minecraft.fluid;

import com.google.common.collect.UnmodifiableIterator;
import java.util.Iterator;
import net.minecraft.util.registry.Registry;

public class Fluids
{
    public static final Fluid EMPTY;
    public static final BaseFluid FLOWING_WATER;
    public static final BaseFluid WATER;
    public static final BaseFluid FLOWING_LAVA;
    public static final BaseFluid LAVA;
    
    private static <T extends Fluid> T register(final String id, final T value) {
        return Registry.<T>register(Registry.FLUID, id, value);
    }
    
    static {
        EMPTY = Fluids.<EmptyFluid>register("empty", new EmptyFluid());
        FLOWING_WATER = Fluids.<WaterFluid.Flowing>register("flowing_water", new WaterFluid.Flowing());
        WATER = Fluids.<WaterFluid.Still>register("water", new WaterFluid.Still());
        FLOWING_LAVA = Fluids.<LavaFluid.Flowing>register("flowing_lava", new LavaFluid.Flowing());
        LAVA = Fluids.<LavaFluid.Still>register("lava", new LavaFluid.Still());
        for (final Fluid fluid2 : Registry.FLUID) {
            for (final FluidState fluidState4 : fluid2.getStateFactory().getStates()) {
                Fluid.STATE_IDS.add(fluidState4);
            }
        }
    }
}

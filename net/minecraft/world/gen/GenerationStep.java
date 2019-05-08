package net.minecraft.world.gen;

import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.Map;

public class GenerationStep
{
    public enum Feature
    {
        RAW_GENERATION("raw_generation"), 
        LOCAL_MODIFICATIONS("local_modifications"), 
        UNDERGROUND_STRUCTURES("underground_structures"), 
        SURFACE_STRUCTURES("surface_structures"), 
        UNDERGROUND_ORES("underground_ores"), 
        UNDERGROUND_DECORATION("underground_decoration"), 
        VEGETAL_DECORATION("vegetal_decoration"), 
        TOP_LAYER_MODIFICATION("top_layer_modification");
        
        private static final Map<String, Feature> BY_NAME;
        private final String name;
        
        private Feature(final String name) {
            this.name = name;
        }
        
        public String getName() {
            return this.name;
        }
        
        static {
            BY_NAME = Arrays.<Feature>stream(values()).collect(Collectors.toMap(Feature::getName, feature -> feature));
        }
    }
    
    public enum Carver
    {
        AIR("air"), 
        LIQUID("liquid");
        
        private static final Map<String, Carver> BY_NAME;
        private final String name;
        
        private Carver(final String name) {
            this.name = name;
        }
        
        public String getName() {
            return this.name;
        }
        
        static {
            BY_NAME = Arrays.<Carver>stream(values()).collect(Collectors.toMap(Carver::getName, carver -> carver));
        }
    }
}

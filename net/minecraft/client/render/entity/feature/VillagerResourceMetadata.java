package net.minecraft.client.render.entity.feature;

import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class VillagerResourceMetadata
{
    public static final VillagerResourceMetadataReader READER;
    private final HatType hatType;
    
    public VillagerResourceMetadata(final HatType hatType) {
        this.hatType = hatType;
    }
    
    public HatType getHatType() {
        return this.hatType;
    }
    
    static {
        READER = new VillagerResourceMetadataReader();
    }
    
    @Environment(EnvType.CLIENT)
    public enum HatType
    {
        a("none"), 
        b("partial"), 
        c("full");
        
        private static final Map<String, HatType> byName;
        private final String name;
        
        private HatType(final String string1) {
            this.name = string1;
        }
        
        public String getName() {
            return this.name;
        }
        
        public static HatType from(final String string) {
            return HatType.byName.getOrDefault(string, HatType.a);
        }
        
        static {
            byName = Arrays.<HatType>stream(values()).collect(Collectors.toMap(HatType::getName, hatType -> hatType));
        }
    }
}

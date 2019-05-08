package net.minecraft.client.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface TooltipContext
{
    boolean isAdvanced();
    
    @Environment(EnvType.CLIENT)
    public enum Default implements TooltipContext
    {
        NORMAL(false), 
        ADVANCED(true);
        
        private final boolean advanced;
        
        private Default(final boolean advanced) {
            this.advanced = advanced;
        }
        
        @Override
        public boolean isAdvanced() {
            return this.advanced;
        }
    }
}

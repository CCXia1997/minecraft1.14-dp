package net.minecraft.block;

public enum BlockRenderLayer
{
    SOLID("Solid"), 
    MIPPED_CUTOUT("Mipped Cutout"), 
    CUTOUT("Cutout"), 
    TRANSLUCENT("Translucent");
    
    private final String name;
    
    private BlockRenderLayer(final String string1) {
        this.name = string1;
    }
    
    @Override
    public String toString() {
        return this.name;
    }
}

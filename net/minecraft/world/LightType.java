package net.minecraft.world;

public enum LightType
{
    SKY(15), 
    BLOCK(0);
    
    public final int c;
    
    private LightType(final int integer1) {
        this.c = integer1;
    }
}

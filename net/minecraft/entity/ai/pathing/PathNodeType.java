package net.minecraft.entity.ai.pathing;

public enum PathNodeType
{
    a(-1.0f), 
    b(0.0f), 
    c(0.0f), 
    d(0.0f), 
    e(-1.0f), 
    f(-1.0f), 
    g(8.0f), 
    h(8.0f), 
    i(0.0f), 
    j(8.0f), 
    k(16.0f), 
    l(8.0f), 
    m(-1.0f), 
    n(8.0f), 
    o(-1.0f), 
    p(0.0f), 
    q(-1.0f), 
    r(-1.0f), 
    s(4.0f), 
    t(-1.0f);
    
    private final float weight;
    
    private PathNodeType(final float float1) {
        this.weight = float1;
    }
    
    public float getWeight() {
        return this.weight;
    }
}

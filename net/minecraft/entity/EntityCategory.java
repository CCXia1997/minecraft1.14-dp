package net.minecraft.entity;

import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.Map;

public enum EntityCategory
{
    a("monster", 70, false, false), 
    b("creature", 10, true, true), 
    c("ambient", 15, true, false), 
    d("water_creature", 15, true, false), 
    e("misc", 15, true, false);
    
    private static final Map<String, EntityCategory> BY_NAME;
    private final int spawnCap;
    private final boolean peaceful;
    private final boolean animal;
    private final String name;
    
    private EntityCategory(final String string1, final int integer2, final boolean boolean3, final boolean boolean4) {
        this.name = string1;
        this.spawnCap = integer2;
        this.peaceful = boolean3;
        this.animal = boolean4;
    }
    
    public String getName() {
        return this.name;
    }
    
    public int getSpawnCap() {
        return this.spawnCap;
    }
    
    public boolean isPeaceful() {
        return this.peaceful;
    }
    
    public boolean isAnimal() {
        return this.animal;
    }
    
    static {
        BY_NAME = Arrays.<EntityCategory>stream(values()).collect(Collectors.toMap(EntityCategory::getName, entityCategory -> entityCategory));
    }
}

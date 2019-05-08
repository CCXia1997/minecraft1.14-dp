package net.minecraft.datafixers;

import com.mojang.datafixers.DSL;

public enum DataFixTypes
{
    a(TypeReferences.LEVEL), 
    b(TypeReferences.PLAYER), 
    c(TypeReferences.CHUNK), 
    d(TypeReferences.HOTBAR), 
    e(TypeReferences.OPTIONS), 
    f(TypeReferences.STRUCTURE), 
    g(TypeReferences.STATS), 
    h(TypeReferences.SAVED_DATA), 
    i(TypeReferences.ADVANCEMENTS), 
    j(TypeReferences.POI_CHUNK);
    
    private final DSL.TypeReference typeReference;
    
    private DataFixTypes(final DSL.TypeReference typeReference) {
        this.typeReference = typeReference;
    }
    
    public DSL.TypeReference getTypeReference() {
        return this.typeReference;
    }
}

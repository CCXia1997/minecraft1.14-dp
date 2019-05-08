package net.minecraft.world.loot.context;

import net.minecraft.util.Identifier;

public class LootContextParameter<T>
{
    private final Identifier id;
    
    public LootContextParameter(final Identifier id) {
        this.id = id;
    }
    
    public Identifier getIdentifier() {
        return this.id;
    }
    
    @Override
    public String toString() {
        return "<parameter " + this.id + ">";
    }
}

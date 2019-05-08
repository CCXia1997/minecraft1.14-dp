package net.minecraft.advancement.criterion;

import net.minecraft.util.Identifier;

public class AbstractCriterionConditions implements CriterionConditions
{
    private final Identifier id;
    
    public AbstractCriterionConditions(final Identifier identifier) {
        this.id = identifier;
    }
    
    @Override
    public Identifier getId() {
        return this.id;
    }
    
    @Override
    public String toString() {
        return "AbstractCriterionInstance{criterion=" + this.id + '}';
    }
}

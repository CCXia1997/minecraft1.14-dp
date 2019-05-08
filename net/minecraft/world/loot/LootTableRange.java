package net.minecraft.world.loot;

import java.util.Random;
import net.minecraft.util.Identifier;

public interface LootTableRange
{
    public static final Identifier CONSTANT = new Identifier("constant");
    public static final Identifier UNIFORM = new Identifier("uniform");
    public static final Identifier BINOMIAL = new Identifier("binomial");
    
    int next(final Random arg1);
    
    Identifier getType();
}

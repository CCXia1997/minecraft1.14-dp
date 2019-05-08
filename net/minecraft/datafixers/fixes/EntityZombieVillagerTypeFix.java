package net.minecraft.datafixers.fixes;

import java.util.function.Function;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.Dynamic;
import net.minecraft.datafixers.TypeReferences;
import com.mojang.datafixers.schemas.Schema;
import java.util.Random;

public class EntityZombieVillagerTypeFix extends ChoiceFix
{
    private static final Random RANDOM;
    
    public EntityZombieVillagerTypeFix(final Schema schema, final boolean boolean2) {
        super(schema, boolean2, "EntityZombieVillagerTypeFix", TypeReferences.ENTITY, "Zombie");
    }
    
    public Dynamic<?> a(Dynamic<?> dynamic) {
        if (dynamic.get("IsVillager").asBoolean(false)) {
            if (!dynamic.get("ZombieType").get().isPresent()) {
                int integer2 = this.clampType(dynamic.get("VillagerProfession").asInt(-1));
                if (integer2 == -1) {
                    integer2 = this.clampType(EntityZombieVillagerTypeFix.RANDOM.nextInt(6));
                }
                dynamic = dynamic.set("ZombieType", dynamic.createInt(integer2));
            }
            dynamic = dynamic.remove("IsVillager");
        }
        return dynamic;
    }
    
    private int clampType(final int integer) {
        if (integer < 0 || integer >= 6) {
            return -1;
        }
        return integer;
    }
    
    @Override
    protected Typed<?> transform(final Typed<?> typed) {
        return typed.update(DSL.remainderFinder(), (Function)this::a);
    }
    
    static {
        RANDOM = new Random();
    }
}

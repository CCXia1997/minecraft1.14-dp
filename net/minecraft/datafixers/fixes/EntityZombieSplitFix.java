package net.minecraft.datafixers.fixes;

import java.util.Objects;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.schemas.Schema;

public class EntityZombieSplitFix extends EntitySimpleTransformFix
{
    public EntityZombieSplitFix(final Schema schema, final boolean boolean2) {
        super("EntityZombieSplitFix", schema, boolean2);
    }
    
    @Override
    protected Pair<String, Dynamic<?>> transform(final String choice, Dynamic<?> dynamic) {
        if (Objects.equals("Zombie", choice)) {
            String string3 = "Zombie";
            final int integer4 = dynamic.get("ZombieType").asInt(0);
            switch (integer4) {
                case 1:
                case 2:
                case 3:
                case 4:
                case 5: {
                    string3 = "ZombieVillager";
                    dynamic = dynamic.set("Profession", dynamic.createInt(integer4 - 1));
                    break;
                }
                case 6: {
                    string3 = "Husk";
                    break;
                }
            }
            dynamic = dynamic.remove("ZombieType");
            return (Pair<String, Dynamic<?>>)Pair.of(string3, dynamic);
        }
        return (Pair<String, Dynamic<?>>)Pair.of(choice, dynamic);
    }
}
